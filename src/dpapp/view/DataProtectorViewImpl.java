package dpapp.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;

import dpapp.model.DataProtectorImpl;

public class DataProtectorViewImpl implements DataProtectorView {

	@Autowired
	private DataProtectorImpl dpapp;
	private HashMap<String, Component> componentMap = new HashMap<>();;
	
	private void initScreen() {
		// Window Frame
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

	// perhaps i could replace with anonymous classes
	@Override
	public void actionPerformed(ActionEvent ev) {
		JButton btn = (JButton) ev.getSource();
		switch(btn.getText()) {
		case "Login": dpapp.login(); break;
		case "Logout": dpapp.logout(); break;
		case "Change Credetials": dpapp.changeCreds(); break;
		case "New User": dpapp.createNewUser(); break;
		case "Delete User": dpapp.deleteUser(); break;
		case "Change Password": dpapp.changePassword(); break;
		case "Choose File": dpapp.selectFile(); break;
		case "Encrypt": dpapp.encryption(true); break;
		case "Decrypt": dpapp.encryption(false); break;
		case "Configs": dpapp.configs(); break;
		case "Export Configs": dpapp.exportConfigs(); break;
		case "Import Configs": dpapp.importConfigs(); break;
		case "Update Configuration": dpapp.updateConfAttr(btn.getActionCommand()); break;
		}

	}

}
