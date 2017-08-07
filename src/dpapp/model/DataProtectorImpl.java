package dpapp.model;

import static utils.CryptServices.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dpapp.view.DataProtectorViewImpl;

import java.util.Collection;
import java.util.List;
import utils.CryptServices;

public class DataProtectorImpl extends AbstractDataProtector {
	
	public DataProtectorImpl(Credentials loggedCreds) {	
		super(loggedCreds);
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encryptFile(String fullPathToFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encryptFile(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decryptFile(String fullPathToFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decryptFile(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String encrypt(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decrypt(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeFile(String fullPathToFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeFile(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<String> fetchAllTitles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> fetchTitlesContaining(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File fetchFile(String fullPathToFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeUserData(AbstractUserData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractUserData fetchUserData(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<AbstractUserData> fetchUserDataDataContaining(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<AbstractUserData> fetchUserDataTitleContaining(String text) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/*
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
			getLogger().log("DataProtector", "Configuration file: '" + confFile + "' is unvalid. Configs were ignored.");
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
				getLogger().log("DataProtector", "Couldn't create '" + userCredMapFile + "'.", ex);
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
			getLogger().log("DataProtector", "Couldn't write to '" + userCredMapFile + "'.", ex);
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
			getLogger().log("DataProtector", "File: '" + userCredMapFile + "' wan't found.", ex);
		} catch (IOException ex) {
			getLogger().log("DataProtector", "Couldn't read file: '" + userCredMapFile + "'.", ex);
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
	*/
}
