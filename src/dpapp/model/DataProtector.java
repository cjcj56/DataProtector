package dpapp.model;

import java.io.File;
import java.util.Collection;

public interface DataProtector {
	
	public void logout();
	
	// Encrypting and decrypting files on the file system
	public void encryptFile(String fullPathToFile);
	public void encryptFile(File file);
	public void decryptFile(String fullPathToFile);
	public void decryptFile(File file);

	// Encrypting and decrypting given strings
	public String encrypt(String text);
	public String decrypt(String text);
	
	// Storing and retrieving user data to database.
	// All user data should be stored encrypted.
	public void storeUserData(AbstractUserData data);		// Store to database general data or strings.
	public void storeFile(String fullPathToFile);	// Store to database binary file.
	public void storeFile(File file);				// Store to database binary file.
	public Collection<String> fetchAllTitles();		// Fetch 
	public Collection<String> fetchTitlesContaining(String text);
	public AbstractUserData fetchUserData(String title);
	public Collection<AbstractUserData> fetchUserDataDataContaining(String text);
	public Collection<AbstractUserData> fetchUserDataTitleContaining(String text);
	public File fetchFile(String fullPathToFile);
	
}
