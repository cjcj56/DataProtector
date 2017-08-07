package rsa;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import static rsa.CryptServices.*;

public class DataProtector implements ActionListener {
	
	public static final int WIDTH = 750;
	public static final int HEIGHT = 500;
	private static final String ROOT_DIR = "DataProtector";
	private static final String CONF_DIR = "Conf";
	private static final String CONF_FILE = "properties.ini";
	private static final String SEC_STORE_DIR = "SecStore";
	private static final String USER_MAP_FILE = "userCredsMappings.ini";
	private static final String LOG_DIR = "Logs";
	
	private HashMap<String, Component> componentMap;
	private HashMap<String, Long> usersCreds;
	private HashMap<String, String> configs;
	private Credentials serverCreds, loggedUserCreds;
	private String loggedUser;
	private File rootDir, confDir, secStoreDir, logsDir, confFile, userCredMapFile;
	private static String[] CONF_DEFAULT_ATTRS = {
		"ServerCreds" + CONF_DELIMITER +  SEC_STORE_DIR + FS_DELIMITER + 0,
		"RootDir" + CONF_DELIMITER +  ROOT_DIR, 
		"HASH_ALGORITHM" + CONF_DELIMITER + "SHA-256",
		"BLOCK_SIZE" + CONF_DELIMITER + 4096,
		"CHARSET" + CONF_DELIMITER + "UTF-8",
		"KEY_SIZE_BITS" + CONF_DELIMITER + 512,
		"MAX_LOG_FILES" + CONF_DELIMITER + 5,
		"MAX_LOG_SIZE_KB" + CONF_DELIMITER + 2048
		};
	
	public DataProtector() { this(null); }
	
	public DataProtector(File file) {
		this(file,JOptionPane.showConfirmDialog(null, "Is this your first use?", "Login", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION);
	}
	
	public DataProtector(File file, boolean install) {
		usersCreds = new  HashMap<>();
		componentMap = new HashMap<>();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("C:\\temp\\")); //TODO : Remove this line!
		componentMap.put("FileChooser", fc);
		loggedUser = null;
		loggedUserCreds = null;
		
		configs = new HashMap<>();
		for(String conf : CONF_DEFAULT_ATTRS) {
			String[] attr_val = conf.split(CONF_DELIMITER);
			configs.put(attr_val[0], attr_val[1]);
		}
		
		if((install) && (! checkInstallationDir(file))) {
			file = selectRootDir(install);
			rootDir = new File(file.getAbsolutePath() + FS_DELIMITER + ROOT_DIR);
		}
		else if ((! install) && (! checkWorkingDir(file))) {
			rootDir = selectRootDir(install);
		} else {
			rootDir = file;
		}
		
		confDir = new File(rootDir.getAbsolutePath() + FS_DELIMITER + CONF_DIR);
		confFile = new File(confDir.getAbsolutePath() + FS_DELIMITER + CONF_FILE);
		secStoreDir = new File(rootDir.getAbsolutePath() + FS_DELIMITER + SEC_STORE_DIR);
		Credentials.setSecStoreDir(secStoreDir);
		userCredMapFile = new File(secStoreDir.getAbsolutePath() + FS_DELIMITER + USER_MAP_FILE);
		logsDir = new File(rootDir.getAbsolutePath() + FS_DELIMITER + LOG_DIR);
		
		if(install) { 
			install();
		} else {
			importServerCreds();
			Credentials.setServerCredsCreatedTrue();
			importUsersCredsMappings();
			importConfigs();
		}
		
		CryptServices.createNewLogger(logsDir);	
		
				
		// Window Frame
		componentMap = new HashMap<String, Component>();
		JFrame mainWindow = new JFrame("Data Protector");
		componentMap.put("MainWindow",mainWindow);
		
		// Main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		componentMap.put("MainPanel",mainPanel);
		mainWindow.add(mainPanel);
				
		// Create Title
		Font titleFont = new Font("Times New Roman",Font.BOLD,30);
		JLabel title = new JLabel("Data Protector");
		title.setFont(titleFont);
		title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		mainPanel.add(title,BorderLayout.NORTH);		
		
		// Create control panel
		JPanel controlPanel = new JPanel(new GridLayout(4,3));

		// Create control buttons
		Font btnFont = new Font("Times New Roman",Font.BOLD,24);
		String [] controlBtnsText = {
				"Login", "Logout", "Change Password",
				"New User", "Delete User",  "Change Credentials",
				"Choose File", "Encrypt", "Decrypt",  
				"Configs", "Export Configs", "Import Configs" 
				};
		JButton [] btns = new JButton[controlBtnsText.length];
		for (int i = 0; i < controlBtnsText.length; ++i) {
			btns[i] = new JButton(controlBtnsText[i]);
			btns[i].setFont(btnFont);
			btns[i].addActionListener(this);
			controlPanel.add(btns[i]);
			componentMap.put(controlBtnsText[i], btns[i]);
		}
		mainPanel.add(controlPanel,BorderLayout.CENTER);
		enableControlPanel(false);
		
		// Create user input field
		JLabel selectedFile = new JLabel();
		selectedFile.setFont(btnFont);
		componentMap.put("SelectedFile", selectedFile);
		
		mainWindow.setSize(WIDTH, HEIGHT);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.pack();
		mainWindow.setSize(mainWindow.getWidth(), HEIGHT);
		mainWindow.setVisible(true);
		mainWindow.revalidate();
		mainWindow.repaint();
	}
	
	
	private File selectRootDir(boolean newInstallation) {
		JOptionPane.showMessageDialog(null, "Please choose your working directory.");
		
		File file = null;
		String message;
		
		JFileChooser fc = (JFileChooser) componentMap.get("FileChooser");
		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int userChoice = fc.showOpenDialog(null);
		do {
			message = null;
			if(userChoice != JFileChooser.APPROVE_OPTION) {
				message = "No selection detected.";
			} else {
				file = fc.getSelectedFile();
				if ((newInstallation) && (! checkInstallationDir(file))) 
					message = "Please choose a directory without a " + ROOT_DIR + " file.";
				else if((! newInstallation) && (! checkWorkingDir(file)))
					message = "Invalid working directory.";
			}

			if(message != null) {
				userChoice = JOptionPane.showConfirmDialog(null, message
						+ System.lineSeparator()
						+ "If you want to login please select a file. Else, click 'No' to quit.",
						"Login", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(userChoice == JOptionPane.NO_OPTION) {
					System.exit(0);
				} else {
					userChoice = fc.showOpenDialog(null);
				}
			} else {
				break;
			}
		} while (true);
		return file;
	}
	
	
	private static boolean checkWorkingDir(File rootDir) {
		if(rootDir == null) return false;
		File confDir, logDir,  confFile, secStoreDir, serverCreds;
		confDir = new File(rootDir.getAbsolutePath() + FS_DELIMITER + CONF_DIR);
		confFile = new File(confDir.getAbsolutePath() + FS_DELIMITER + CONF_FILE);
		logDir = new File(rootDir.getAbsolutePath() + FS_DELIMITER + LOG_DIR);
		secStoreDir = new File(rootDir.getAbsolutePath() + FS_DELIMITER + SEC_STORE_DIR);
		serverCreds = new File(secStoreDir.getAbsolutePath() + FS_DELIMITER + 0);
//		print("" + fullDirPermissions(rootDir));
//		print("" + fullDirPermissions(confDir));
//		print("" + fullDirPermissions(logDir));
//		print("" + fullDirPermissions(secStoreDir));
//		print("" + fullFilePermissions(confFile));
//		print("" + fullFilePermissions(serverCreds));
		return (
				(fullDirPermissions(rootDir))
				&& (fullDirPermissions(confDir))
				&& (fullDirPermissions(logDir))
				&& (fullDirPermissions(secStoreDir))
				&&(fullFilePermissions(confFile))
				&& (fullFilePermissions(serverCreds))
//				&& (validateConfFile(confFile))
//				&& (Credentials.validateCredFiles(serverCreds))
				);
	}
	
	
	private static boolean checkInstallationDir(File path) {
		if(! fullDirPermissions(path)) return false;
		
		for (String subDir : path.list())
			if(subDir.equals(ROOT_DIR))
				return false;
		
		return true;
	}
	
	private File install() {
		rootDir.mkdir();
		confDir.mkdir();
		secStoreDir.mkdir();
		logsDir.mkdir();
		String serverUsername = JOptionPane.showInputDialog("Please insert server administrator username:");
		String serverPassword1 = JOptionPane.showInputDialog("Please insert server administrator password:");
		String serverPassword2 = JOptionPane.showInputDialog("Please re-insert server administrator password for confirmation:");
		while(! serverPassword1.equals(serverPassword2)) {
			serverUsername = JOptionPane.showInputDialog("Passwords didn't match!" + System.lineSeparator() + "Please insert server administrator username:");
			serverPassword1 = JOptionPane.showInputDialog("Please insert server administrator password:");
			serverPassword2 = JOptionPane.showInputDialog("Please re-insert server administrator password for confirmation:");
		}
		serverCreds = Credentials.createServerCreds(serverUsername, serverPassword1, secStoreDir);
		for(String config : CONF_DEFAULT_ATTRS) {
			String[] attr_val = config.split(CONF_DELIMITER);
			configs.put(attr_val[0], attr_val[1]);
		}
		configs.put("ServerCreds", secStoreDir.getAbsolutePath() + FS_DELIMITER + 0);
		configs.put("RootDir", rootDir.getAbsolutePath());
		exportConfigs();
		try { userCredMapFile.createNewFile(); } catch(IOException e) {}
		return rootDir;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		JButton btn = (JButton) ev.getSource();
		switch(btn.getText()) {
		case "Login": login(); break;
		case "Logout": logout(); break;
		case "Change Credetials": changeCreds(); break;
		case "New User": createNewUser(); break;
		case "Delete User": deleteUser(); break;
		case "Change Password": changePassword(); break;
		case "Choose File": selectFile(); break;
		case "Encrypt": encryption(true); break;
		case "Decrypt": encryption(false); break;
		case "Configs": configs(); break;
		case "Export Configs": exportConfigs(); break;
		case "Import Configs": importConfigs(); break;
		case "Update Configuration": updateConfAttr(btn.getActionCommand()); break;
		}
		
	}

	public boolean validateLoggedUser(String username, String password) {
		return (
				(loggedUser != null) && 
				(loggedUserCreds != null) &&
				(username.equals(loggedUser)) &&
				(Arrays.equals(loggedUserCreds.getHashedPassword(), digest((password + loggedUserCreds.getSalt()).getBytes())))
				);
	}
	
	
	public Credentials validateUser(String username, String password) {
		if(! usersCreds.containsKey(username)) return null;
		long credId = usersCreds.get(username);
		Credentials userCred = new Credentials(credId, serverCreds);
		byte[] hashedPassword = digest((password + userCred.getSalt()).getBytes());
		byte[] credHasedPassword = userCred.getHashedPassword();
		if (username.equals(userCred.getUsername()) && (Arrays.equals(hashedPassword, credHasedPassword))) {
			return userCred;
		} else {
			return null;
		}
	}
	
	public void login() {
		if(loggedUser != null) {
			JOptionPane.showMessageDialog(null, "User: '" + loggedUser + "' is crrently logged in. Please logout before logging in again.");
			return;
		}
		String username = JOptionPane.showInputDialog("Username: ");
		String password = JOptionPane.showInputDialog("Password: ");
		Credentials userCred = validateUser(username,password);
		if(userCred != null) {
			loggedUser = userCred.getUsername();
			loggedUserCreds = userCred;
			enableControlPanel(true);
		} else {
			JOptionPane.showMessageDialog(null, "Login failed for user: '" + username + "'.");
		}
	}
	
	
	public void logout() {
		loggedUser = null;
		loggedUserCreds = null;
		enableControlPanel(false);
	}
	
	
	public void changeCreds() {
		int userChoice = JOptionPane.showConfirmDialog(null, "Warning! After changing credentials, all files encrypted with old credentials will remain inaccessible!" + System.lineSeparator() + "Do you wish to continue?", "User Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if(userChoice == JOptionPane.YES_OPTION) {
			String username = JOptionPane.showInputDialog("Username: ");
			String password = JOptionPane.showInputDialog("Password: ");
			if(validateLoggedUser(username, password)) {
				deleteUser(username);
				createNewUser(username,password);
			} else {
				JOptionPane.showMessageDialog(null, "Failed changing credentials for user: '" + username + "' - wrong username or password.");
			}
		}

	}

	
	public void createNewUser() {
		String username = JOptionPane.showInputDialog("Username: ");
		while(usersCreds.containsKey(username)) {
			username = JOptionPane.showInputDialog("Username '" + username + "' is already taken. Insert another username: ");
		}
		String password = JOptionPane.showInputDialog("Password: ");
		createNewUser(username, password);
	}
	
	
	private void createNewUser(String username, String password) {
		Credentials userCreds = new Credentials(username, password);
		usersCreds.put(username, userCreds.getCredId());
		userCreds.saveToFile(serverCreds);
	}
	
	
	public void deleteUser() {
		int userChoice = JOptionPane.showConfirmDialog(null, "Warning! After deleting user, all encrypted files will remain inaccessible!" + System.lineSeparator() + "Do you wish to continue?", "User Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if(userChoice == JOptionPane.YES_OPTION) {
			String username = JOptionPane.showInputDialog("Username: ");
			String password = JOptionPane.showInputDialog("Password: ");
			if(validateUser(username, password) != null)
				deleteUser(username);
			else
				JOptionPane.showMessageDialog(null, "Failed deleting user: '" + username + "' - wrong username or password.");
		}
	}
	
	
	private void deleteUser(String username) {
		Credentials userCreds = new Credentials(usersCreds.get(username),serverCreds);
		userCreds.getCredFile().delete();
		usersCreds.remove(username);
	}
	
	
	public void changePassword() {
		String oldPassword = JOptionPane.showInputDialog("Old password: "); 
		if(! validateLoggedUser(loggedUser, oldPassword)) {
			JOptionPane.showMessageDialog(null, "Incorrect password!");
			return;
		}
		String newPassword = JOptionPane.showInputDialog("New password: ");
		String newPasswordConfirm = JOptionPane.showInputDialog("Confirm new password: ");
		if(! newPassword.equals(newPasswordConfirm)) {
			JOptionPane.showMessageDialog(null, "Passwords do not match!");
			return;
		}
		loggedUserCreds.setPassword(newPassword, serverCreds);
	}
	
	
	public void selectFile() {
		JFileChooser fc = (JFileChooser) componentMap.get("FileChooser");
		setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		JFrame mainWindow = (JFrame) componentMap.get("MainWindow");
		int userChoice = fc.showSaveDialog(mainWindow);
		if(userChoice != JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(null, "No file or directory were selected.");
			return;
		}
		((JLabel) componentMap.get("SelectedFile")).setText(fc.getSelectedFile().getAbsolutePath());
		mainWindow.repaint();
		mainWindow.revalidate();
	}
	
	
	public File getSelectedFile() {
		return new File(((JLabel) componentMap.get("SelectedFile")).getText());
	}
	
	
	public void encryption(boolean encrypt) {
		File selecteFile = getSelectedFile();
		if(! selecteFile.exists()) return;
		else if(selecteFile.isDirectory()) loggedUserCreds.filesystemEncryption(selecteFile, encrypt);
		else if(selecteFile.isFile()) loggedUserCreds.fileEncryption(selecteFile, encrypt);
	}
	
	
	public void configs() {
		JFrame configFrame = new JFrame("Configuration Settings");
		JPanel configPanel = new JPanel(new GridLayout(configs.size()+1,3));
		configFrame.add(configPanel);
		for (String config : configs.keySet()) {
			configPanel.add(new JLabel(config));
			configPanel.add(new JLabel(configs.get(config)));
			JButton btn = new JButton("Update Configuration");
			if((config.equals("RootDir")) || (config.equals("ServerCreds"))) {
				btn.setActionCommand(config + CONF_DELIMITER + config);
			} else {
				btn.setActionCommand(config + CONF_DELIMITER + JOptionPane.showInputDialog("New configuration value: "));
			}
			btn.addActionListener(this);
		}
		configFrame.setSize(WIDTH, HEIGHT);
		configFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		configFrame.pack();
		configFrame.setVisible(true);
		configFrame.revalidate();
		configFrame.repaint();

	}
	
	
	private void updateConfAttr(String confUpdate) {
		String[] attr_val = confUpdate.split(CONF_DELIMITER);
		updateConfAttr(attr_val[0],attr_val[1]);
	}
	
	
	private void updateConfAttr(String confAttr, String confVal) {
		boolean commit = false;
		switch (confAttr) {
			case "ServerCreds":
				JFileChooser fc = (JFileChooser) componentMap.get("FileChooser");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int userChoice = fc.showSaveDialog(null);
				if(userChoice != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "No servers credentials were chosen.");
					return;
				}
				commit = importServerCreds(fc.getSelectedFile());
				break;
			case "RootDir":
				File file = selectRootDir(false);
				commit = file != null;
				if(commit) {
					confVal = file.getAbsolutePath();
				}
				break;
			case "HASH_ALGORITHM":
				CryptServices.setHASH_ALGORITHM(confVal);
				break;
			case "BLOCK_SIZE":
				int blockSize = Integer.parseInt(confVal);
				commit = ((blockSize >= 8) && (blockSize % 2 == 0));
				if(commit) CryptServices.setBLOCK_SIZE(blockSize);
				break;
			case "CHARSET":
				try {
					CryptServices.setCHARSET(Charset.forName(confVal));
					commit = true;
				} catch (Exception ex) {}
				break;
			case "KEY_SIZE_BITS":
				int keySizeBits = Integer.parseInt(confVal);
				commit = ((keySizeBits >= 256) && (keySizeBits % 2 == 0));
				if(commit) CryptServices.setKEY_SIZE_BITS(keySizeBits);
				break;
			case "MAX_LOG_FILES":
				int maxLogFiles = Integer.parseInt(confVal);
				commit = (maxLogFiles >= 2);
				if(commit) CryptServices.setLoggerMAX_LOG_FILES(maxLogFiles);
				break;
			case "MAX_LOG_SIZE_KB": 
				int maxLogSizeKB = Integer.parseInt(confVal);
				commit = (maxLogSizeKB >= 4);
				if(commit) CryptServices.setLoggerMAX_LOG_SIZE_KB(maxLogSizeKB);
				break;
		}
		if(commit) configs.put(confAttr, confVal);
	}
	
	
	public void exportConfigs() {
		writeHashMapToFile(configs,confFile);
	}
	
	
	public void importConfigs() {
		HashMap<String, String> newConfigs = readHashMapFromFile(confFile);
		if(validateConfs(newConfigs)) {
			configs = newConfigs;
		} else {
			logger.log("DataProtector", "Configuration file: '" + confFile + "' is unvalid. Configs were ignored.");
		}
	}
	
	private boolean validateConfs(HashMap<String, String> configs) {
		return (configs.keySet().equals(this.configs.keySet()));
	}
	
	
	public boolean importServerCreds() {
//		print("" + new File(secStoreDir + FS_DELIMITER + 0));
		serverCreds = new Credentials(0, null);
		return true;
	}
	
	public boolean importServerCreds(File path) {
		if(fullFilePermissions(path)) {
			try {
			File serverCredsFile = new File(SEC_STORE_DIR + FS_DELIMITER + "0");
			File oldServerCredsFile = new File(SEC_STORE_DIR + FS_DELIMITER + "0.old");
			serverCredsFile.renameTo(oldServerCredsFile);
			Files.copy(path.toPath(),serverCredsFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
			serverCreds = new Credentials(0, null);
			return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
			
	}
	
	
	public void exportUsersCredsMappings() {
		if (userCredMapFile.exists()) {
			try { 
				userCredMapFile.createNewFile();
				userCredMapFile.delete();	
			}
			catch (IOException ex) {
				logger.log("DataProtector", "Couldn't create '" + userCredMapFile + "'.", ex);
			}	
		}
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(userCredMapFile));
			for(String user : usersCreds.keySet()) {
				writer.write(user + CONF_DELIMITER + usersCreds.get(user));
				writer.newLine();
			}
		} catch (IOException ex) {
			logger.log("DataProtector", "Couldn't write to '" + userCredMapFile + "'.", ex);
		} finally { if(writer != null) try { writer.close(); } catch (IOException ex) {} }
		serverCreds.fileEncryption(userCredMapFile, true);
	}
	
	public void importUsersCredsMappings() {
		BufferedReader reader = null;
		serverCreds.fileEncryption(userCredMapFile, false);
		usersCreds.clear();
		
		try {
			reader = new BufferedReader(new FileReader(userCredMapFile));
			String line = reader.readLine();
			while(line != null) {
				if(line.equals("")) {
					line = reader.readLine();
				} else {
					String[] user_id = line.split(CONF_DELIMITER);
					usersCreds.put(user_id[0], Long.parseLong(user_id[1]));
					line = reader.readLine();
				}
			}
		} catch (FileNotFoundException ex) {
			logger.log("DataProtector", "File: '" + userCredMapFile + "' wan't found.", ex);
		} catch (IOException ex) {
			logger.log("DataProtector", "Couldn't read file: '" + userCredMapFile + "'.", ex);
		} finally {
			if(reader != null) try { reader.close(); } catch (IOException e) {}
			serverCreds.fileEncryption(userCredMapFile, true);
		}
	}
	
	
	public void enableControlPanel(boolean enable) {
		String [] controlBtnsText = {
				"Logout", "Change Password", "Decrypt", 
				"Change Credentials", "Choose File", "Encrypt",
				};
		for(String controlBtnText : controlBtnsText) {
			((JButton) componentMap.get(controlBtnText)).setEnabled(enable);
		}
		((JButton) componentMap.get("Login")).setEnabled(! enable);
	}
	
	private void setFileSelectionMode(int selectionMode) {
		JFileChooser fc = (JFileChooser) componentMap.get("FileChooser");
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					fc.setFileSelectionMode(selectionMode);
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setCurrentDirectory(File currentDirectory) {
		JFileChooser fc = (JFileChooser) componentMap.get("FileChooser");
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					fc.setCurrentDirectory(currentDirectory);
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
