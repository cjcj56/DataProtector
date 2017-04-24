package rsa;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static rsa.CryptServices.*;

public class Credentials {	
	
	private static long credIdGen = 0;
	private static long assignCredId() { return ++credIdGen; }
	private static boolean serverCredsCreated = false;
	protected static void setServerCredsCreatedTrue() { serverCredsCreated = true; }
	private static File secStoreDir = null;
	protected static void setSecStoreDir(File path) {
		secStoreDir = path;
		
		int maxCredId = 0;
		if(fullDirPermissions(secStoreDir)) {
			for (String file : secStoreDir.list()) {
				int filename = maxCredId;
				try {
					filename = Integer.parseInt(file);
				} catch (NumberFormatException ex) {
					continue;
				}
				if(filename > maxCredId) maxCredId = filename;
			}
		}
		credIdGen = maxCredId;
	}
	
	private long credId;
	private String username;
	private byte[] hashedPassword;
	private int salt;
	private Keys keys;

	
	public Credentials(String username, String password) {
		this.credId = assignCredId();
		this.username = username;
		this.salt = (int) (100000 * Math.random());
		this.hashedPassword = digest((password + salt).getBytes());
		keys = Keys.generateKeys();
	}
	
	protected Credentials(long credId, Credentials serverCreds) {
		this(credId, new File(secStoreDir.getAbsolutePath() + FS_DELIMITER + credId), serverCreds);
	}
	
	private Credentials(long credId, File file, Credentials serverCreds) {
		this.credId = credId;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			List<String> lines = Files.readAllLines(file.toPath());
			if(serverCreds != null) {
				for(int i = 0; i < lines.size(); ++i) {
					lines.set(i, serverCreds.decryptString(lines.get(i)));
				}
			}
			this.username = lines.get(0);
			this.hashedPassword = hexToByteArray(lines.get(1));
			this.salt = Integer.parseInt(lines.get(2));
			this.keys = new Keys(lines.get(3),lines.get(4),lines.get(5));
//			this.keys = Keys.getKeyFromFile(reader, serverCreds);
		} catch (FileNotFoundException ex) {
			logger.log("Credentials", "Couldn't find file: \"" + file + "\"", ex);
		} catch (IOException ex) {
			logger.log("Credentials", "Couldn't read key from file: \"" + file + "\"", ex);
		} finally {
			if(reader != null) try { reader.close(); } catch (IOException ex) {}
		}
	}
	
	
	protected static Credentials createServerCreds(String username, String password, File path) {
		if(serverCredsCreated) return null;
		serverCredsCreated = true;
		Credentials cred = new Credentials(username, password);
		cred.credId = 0;
		cred.saveToFile(new File(path.getAbsolutePath() + FS_DELIMITER + 0), null);
		return cred;
	}
	
	protected void saveToFile(Credentials serverCreds) {
		saveToFile(getCredFile(),serverCreds);
	}
	
	private void saveToFile(File credFile, Credentials serverCreds) {
		BufferedWriter writer = null;
		try {
			if(credFile.exists() && credFile.isFile()) {
				credFile.delete();
				credFile.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(credFile));
			String[] lines = {username, byteArrayToHex(hashedPassword, hashedPassword.length), ""+salt};
			for (String line : lines) {
				if(serverCreds != null) line = serverCreds.encryptString(line);
				writer.write(line);
				writer.newLine();
			}
			keys.saveKeyToFile(writer, serverCreds);
		} catch (IOException ex) {
			logger.log("Credentials", "Couldn't write credentials to file:" + credFile, ex);
		} finally { if(writer != null) try { writer.close(); } catch (IOException ex) {} }
	}

	
//	private String hashString(String str) {
//		byte[] bytes = digest(str.getBytes());
//		StringBuilder sb = new StringBuilder();
//        for(int i=0; i< bytes.length ;i++) {
//            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
//        }
//        return sb.toString();
//	}
	
	
	public String encryptBytes (byte[] bytes, int length) { return encrypt(byteArrayToHex(bytes,length)); }
	public String encryptString(String string) { return encrypt(stringToHex(string));         }
	public String encrypt      (String hex)    {
		int hexLength = hex.length();
		String chunk, encryptedChunk; 
		StringBuilder encrypted = new StringBuilder(2*hexLength);
		int beginIdx = 0, endIdx = ENCRYPTION_BLOCK_SIZE, to;
		while(beginIdx < hexLength) {  // TODO : appends a redundent delimiter ("g") to the last encrypted chunk.  
			to = (endIdx <= hexLength) ? endIdx : hexLength;
			chunk = hex.substring(beginIdx, to);
			int i = 1;
			char c = chunk.charAt(0); // If chunk starts with a 00 byte, this byte will get lost when creating a bigint. so we will preserve it by adding a special prefix.
			while((i < chunk.length()) && (Character.toString(c).equals("0"))) {
				encrypted.append(ZERO_PLACEHOLDER);
				c = chunk.charAt(i);
				++i;
			}
			encryptedChunk = keys.useKey(chunk,true);
			assert (! encryptedChunk.contains(DELIMITER));
			encrypted.append(encryptedChunk);
			encrypted.append(DELIMITER);
			beginIdx = endIdx;
			endIdx += ENCRYPTION_BLOCK_SIZE;
		}
		return encrypted.toString();
	}
	
	
	public byte[] decryptBytes (String cipher) { return hexToByteArray(decrypt(cipher)); }
	public String decryptString(String cipher) { return hexToString(decrypt(cipher));    }
	public String decrypt      (String cipher) {
		StringBuilder decrypted = new StringBuilder();
		String[] chunks = cipher.split(DELIMITER);
		String decryptedChunk;
		for (int i = 0; i < chunks.length; ++i) {
			if(chunks[i].length() == 0) continue;
			int zeroSequenceLength = 0;
			while((zeroSequenceLength < chunks[i].length()) && (Character.toString(chunks[i].charAt(zeroSequenceLength)).equals(ZERO_PLACEHOLDER))) ++zeroSequenceLength;
			chunks[i] = chunks[i].substring(zeroSequenceLength); 
			decryptedChunk = keys.useKey(chunks[i],false);
			if(decryptedChunk.length() == 0) continue;
//			if(decryptedChunk.length() % 2 != 0) decrypted.append("0");
			for(int j = 0; j < zeroSequenceLength; ++ j) decrypted.append("0");
			decrypted.append(decryptedChunk);
		}
		return decrypted.toString();
	}	

	
	public void encryptFile(File inFile, File outFile) {
		BufferedInputStream bis = null;
		BufferedWriter writer = null;
		byte[] buffer = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(inFile));
			writer = new BufferedWriter(new FileWriter(outFile));
			buffer = new byte[BLOCK_SIZE];
			int read;
			while ((read = bis.read(buffer)) > 0) {
				writer.write(encryptBytes(buffer,read));
				writer.newLine();
			}
		} catch (FileNotFoundException ex) {
			logger.log("Credentials", "Failed to write to temp out file: \"" + outFile + "\" - file not found!", ex);
		} catch (IOException ex) {
			logger.log("Credentials", "Failed to read from in file: \"" + inFile + "\"!", ex);
		} finally {
			try { if(bis != null)    bis.close();    } catch (IOException e) {}
			try { if(writer != null) writer.close(); } catch (IOException e) {}
		}

	}

	
	public void decryptFile(File inFile, File outFile) {
		BufferedReader reader = null;
		BufferedOutputStream ous = null;

		try {
			reader = new BufferedReader(new FileReader(inFile));
			ous = new BufferedOutputStream(new FileOutputStream(outFile));
			String line = reader.readLine();
			while(line != null) {
				String[] chunks = line.split(DELIMITER);
				for (String chunk : chunks) {
					ous.write(decryptBytes(chunk));
				}
				line = reader.readLine();
			}
		} catch (FileNotFoundException ex) {
			logger.log("Credentials", "Failed to write to temp out file: \"" + outFile + "\" - file not found!", ex);
		} catch (IOException ex) {
			logger.log("Credentials", "Failed to read from in file: \"" + inFile + "\"!", ex);
		} finally {
			try { if(reader != null) reader.close(); } catch (IOException e) {}
			try { if(ous != null)    ous.close();    } catch (IOException e) {}
			reader = null;
			ous = null;
			System.gc();
		}

	}
	

	public void fileEncryption(File inFile, boolean encrypt) {
		// Create temporary file
		String fullPath = inFile.getAbsolutePath().toString();
		long idx = 0;
		while((Files.exists(Paths.get(fullPath + "_CRYPT" + idx + "." + getFileType(fullPath)))) && (idx < Long.MAX_VALUE)) { ++idx;	}
		File outFile = new File(fullPath + "_CRYPT" + idx + "."  + getFileType(fullPath));
		try { assert outFile.createNewFile(); }
		catch (IOException ex) {
			logger.log("Credentials", "Encryption/Decryption Error! Failed to create temporary file during encryption/decryption", ex);
			return;
		}

		if(encrypt) { encryptFile(inFile, outFile); }
		else        { decryptFile(inFile, outFile); }

		inFile.delete();
		outFile.renameTo(inFile);
	}

	
	public void filesystemEncryption(File path, boolean encrypt) {
		if (path.isDirectory()){
			for(File f : path.listFiles()) filesystemEncryption(f, encrypt);
		} else if(path.isFile()) {
			fileEncryption(path, encrypt);
		} 
	}
	
	
	protected int getSalt()              { return salt;           }
	protected String getUsername()       { return username;       }
	protected byte[] getHashedPassword() { return hashedPassword; }
	protected long getCredId()           { return credId;         }
	protected File getCredFile()         { return new File(secStoreDir + FS_DELIMITER + credId); } 
	protected void setPassword(String newPassword, Credentials serverCreds) {
		hashedPassword = digest((newPassword + salt).getBytes());
		saveToFile(secStoreDir, serverCreds);
	}
	
//	=========================================================================================
//	=========================================================================================
//	=========================================================================================
	
	/*
	public static final void main (String args[]) throws Throwable {
		String allChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ,.";
		
		setSecStoreDir(new  File("C:\\temp\\SecStore"));
		Credentials c = new Credentials("binyamink", "A123456a");
		c.saveToFile(new File("C:\\temp\\SecStore\\1"),null);
		Credentials c1 = new Credentials(1, null);
		
		String plaintext = "O god almighty, please help me finish this accuresed "
				+ "assignment successfully, and in a timely manner. Amen.";
//		String plaintext = "abc";
		String hexPlaintext = stringToHex(plaintext);
		String encrypted = c1.encryptString(plaintext);
		String decrypted = c1.decryptString(encrypted);

		System.out.println("================  Strart TEST ================");
		System.out.println(plaintext);
		System.out.println(encrypted);
		System.out.println(decrypted);
		
//		c1.fileEncryption(new File("C:\\temp\\aaa1.txt"), true);
//		c1.fileEncryption(new File("C:\\temp\\aaa1.txt"), false);
//
//		c1.fileEncryption(new File("C:\\temp\\a\\b\\RSAAlgorithm.class"), true);
//		c1.fileEncryption(new File("C:\\temp\\a\\b\\RSAAlgorithm.class"), false);


//		c1.encryptFile(new File("C:\\temp\\a\\b\\c\\maman16_302310902.docx"), new File("C:\\temp\\a\\b\\c\\maman16_302310902.docx_CRYPT3.docx"));
//		c1.decryptFile(new File("C:\\temp\\a\\b\\c\\maman16_302310902.docx_CRYPT3.docx"), new File("C:\\temp\\a\\b\\c\\maman16_302310902.docx_CRYPT4.docx"));
		
//		c1.filesystemEncryption(new File("C:\\temp\\a"), true);
//		c1.filesystemEncryption(new File("C:\\temp\\a"), false);
		
		System.out.println("================   END TEST   ================");
		
	}
*/
}
