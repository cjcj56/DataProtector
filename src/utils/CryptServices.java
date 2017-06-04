package utils;

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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


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
	private static Logger logger = null;
	private static SessionFactory factory = new Configuration().configure().buildSessionFactory();;

	private static MessageDigest hashFunction;
	static {
		try { 
			hashFunction = MessageDigest.getInstance(HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	public static byte[] digest(byte[] bytes) {
		return hashFunction.digest(bytes);
	}

	
	// Changed from protected to public
	public static void createNewLogger(File path) {
		logger = Logger.createLogger(path, logger);
	}
	
	
	// Changed from protected to public
	public static String getFormattedTime() {
		return dateFormat.format(Calendar.getInstance().getTime());
	} 

	// Changed from protected to public
	public static Long getTimeStamp() {
		return Calendar.getInstance().getTime().getTime();
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


	public static int byteArrayComparator(byte[] arr1, byte[] arr2) {
		if (arr1.equals(arr2) ) {
			return 0;
		}
		
		if(arr1.length < arr2.length) {
			return -1;
		} else if (arr1.length > arr2.length) {
			return 1;
		}
		
		for (int i = 0; i < arr1.length; ++i) {
			int cmpValue = Byte.compare(arr1[i], arr2[i]);
			if(cmpValue != 0) {
				return cmpValue;
			}
		}
		
		return 0;
	}
	
	
	// Setters
	public static void setHASH_ALGORITHM(String hASH_ALGORITHM) { HASH_ALGORITHM = hASH_ALGORITHM; }
	public static void setBLOCK_SIZE(int bLOCK_SIZE) { BLOCK_SIZE = bLOCK_SIZE; }
	public static void setCHARSET(Charset cHARSET) { CHARSET = cHARSET; }
	public static void setKEY_SIZE_BITS(int kEY_SIZE_BITS) { KEY_SIZE_BITS = kEY_SIZE_BITS; }
	public static void setENCRYPTION_BLOCK_SIZE(int eNCRYPTION_BLOCK_SIZE) { ENCRYPTION_BLOCK_SIZE = eNCRYPTION_BLOCK_SIZE; }
	public static void setLoggerMAX_LOG_FILES(int mAX_LOG_FILES) { logger.setMAX_LOG_FILES(mAX_LOG_FILES); }
	public static void setLoggerMAX_LOG_SIZE_KB(int mAX_LOG_SIZE_KB) { logger.setMAX_LOG_SIZE_KB(mAX_LOG_SIZE_KB); }
	public static void print(String str) { System.out.println(str); }

	// Getters
	
	// TODO: Problematic????
	public static Logger getLogger() {
		return logger;
	}

	private static SessionFactory getFactory() {
		return factory;
	}
	
	public static Session openSession() {
		return getFactory().openSession();
	}
	
}
