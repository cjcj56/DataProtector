package rsa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class CryptServices {

	/*
	 * Crypto-System Constants
	 */
	public static String HASH_ALGORITHM = "SHA-256";
	public static int BLOCK_SIZE = 4096;
	public static Charset CHARSET = Charset.forName("UTF-8");
	public static int KEY_SIZE_BITS = 512;
	public static int ENCRYPTION_PADDING_SIZE = 11;
	public static int ENCRYPTION_BLOCK_SIZE = (KEY_SIZE_BITS / 8);
	public static String CONF_DELIMITER = "=";
	public static String FS_DELIMITER = "\\";
	public static String DELIMITER = "g";
	public static String ZERO_PLACEHOLDER = "h";
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	protected static Logger logger = null;
	
	
	protected static void createNewLogger(File path) {
		logger = Logger.createLogger(path, logger);
	}
	
	protected static boolean fullFilePermissions(File file) {
		return ((file != null) && (file.exists()) && (file.isFile()) && (file.canRead()) && (file.canWrite()));
	}
	
	protected static boolean fullDirPermissions(File file) {
		return ((file != null) && (file.exists()) && (file.isDirectory()) && (file.canRead()) && (file.canWrite()) && (file.canExecute()));
	}
	
	protected static String getFormattedTime() {
		return dateFormat.format(Calendar.getInstance().getTime());
	} 

	protected static Long getTimeStamp() {
		return Calendar.getInstance().getTime().getTime();
	}
	
	private static MessageDigest hashFunction;
	static {
		try { hashFunction = MessageDigest.getInstance(HASH_ALGORITHM); }
		catch (NoSuchAlgorithmException e) {System.out.println("ERROR!!!");} //TODO
	}
	
	public static byte[] digest(byte[] bytes) {
		return hashFunction.digest(bytes);
	}


	public static String stringToHex(String str) {
		char[] chars = str.toCharArray();
		StringBuilder hex = new StringBuilder();
		for(int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int)chars[i]));
		}
		return hex.toString();
	}

	public static String hexToString(String hex) {
		StringBuilder sb = new StringBuilder();

		//49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for(int i = 0; i < hex.length() - 1; i += 2 ){
			//grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			//convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			//convert the decimal to character
			sb.append((char)decimal);
		}
		return sb.toString();
	}


	public static String byte2hex(byte b)   { return String.format("%02x", b); }
	public static byte   hex2byte(String h) { return (byte) Integer.parseInt(h, 16); }

	public static String byteArrayToHex(byte[] bytes, int length) {
		if((bytes.length == 0) || (length <= 0)) return "";
		if(length > bytes.length) length = bytes.length;
		StringBuilder sb = new StringBuilder(2 * length);
		for (int i = 0; i < length; ++i) {
			sb.append(byte2hex(bytes[i]));
		}
		return sb.toString();
	}

	public static byte[] hexToByteArray(String hex) {
		if (hex.equals("")) return new byte[0];
		char[] hexChars = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = hex2byte("" + hexChars[2*i] + hexChars[2*i+1]);
		}
		return bytes;
	}

	public static String getFileType(String path) {
		return path.substring(path.lastIndexOf(".") + 1); 
	}

	
	public static <K,V> void writeHashMapToFile(HashMap<K, V> map, File path) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			for(K key : map.keySet()) {
				writer.write(key + CONF_DELIMITER + map.get(key));
				writer.newLine();
			}
		} catch (IOException e) {
			logger.log("CryptServices", "Failed to write map to file: '" + path + "'.", e);
		} finally { if(writer != null) try {writer.close();} catch(IOException e) {} }
	}
	
	
	public static HashMap<String, String> readHashMapFromFile(File path) {
		BufferedReader reader = null;
		HashMap<String, String> map = null;
		try {
			reader = new BufferedReader(new FileReader(path));
			map = new HashMap<>();
			
			String line = reader.readLine();
			while((line != null) && (! line.equals(""))) {
				String[] attr_val = line.split(CONF_DELIMITER);
				map.put(attr_val[0], attr_val[1]);
				line = reader.readLine();
			}
			
		} catch (FileNotFoundException e) {
			logger.log("CryptServices", "File: '" + path + "' was not found.", e);
		} catch (IOException e) {
			logger.log("CryptServices", "Failed to read from file: '" + path + "'.", e);
		} finally { if(reader != null) try {reader.close();} catch(IOException e) {} }
		
		return map;
	}
	
	
	// Setters
	protected static void setHASH_ALGORITHM(String hASH_ALGORITHM) { HASH_ALGORITHM = hASH_ALGORITHM; }
	protected static void setBLOCK_SIZE(int bLOCK_SIZE) { BLOCK_SIZE = bLOCK_SIZE; }
	protected static void setCHARSET(Charset cHARSET) { CHARSET = cHARSET; }
	protected static void setKEY_SIZE_BITS(int kEY_SIZE_BITS) { KEY_SIZE_BITS = kEY_SIZE_BITS; }
	protected static void setENCRYPTION_BLOCK_SIZE(int eNCRYPTION_BLOCK_SIZE) { ENCRYPTION_BLOCK_SIZE = eNCRYPTION_BLOCK_SIZE; }
	protected static void setLoggerMAX_LOG_FILES(int mAX_LOG_FILES) { logger.setMAX_LOG_FILES(mAX_LOG_FILES); }
	protected static void setLoggerMAX_LOG_SIZE_KB(int mAX_LOG_SIZE_KB) { logger.setMAX_LOG_SIZE_KB(mAX_LOG_SIZE_KB); }

	public static void print(String str) { System.out.println(str); }

//	public static void main(String[] args) throws Throwable {}
}
