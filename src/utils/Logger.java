package utils;

import static utils.CryptServices.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class Logger {
	
	private static final File LOG_DIR = new File("C:\\temp\\ProtectorLogs\\");  
	private static final String SYS_LOG_NAME = "LoggingSys";  	// Logs of the logging system
	private static final long MAX_TIME_DIFF = 300;				// If writer wasn't used for this period, close it.
	private static final int MAX_WRITE_NUM = 20;				// Check old writers every given number of writes 
	private static int MAX_LOG_FILES = 5;					// Max number of log files to rotate
	private static int MAX_LOG_SIZE_KB = 2048;			// Max size of log file
	private static boolean loggerCreated = false;				// Only one instance of Logger object may exist at a given time.
	
	private File logDir;
	private HashMap<String,BufferedWriter> writersList;
	private HashMap<String,Long> lastWriteTime;
	private int numOfWrites;
	
	private Logger() {
		this(LOG_DIR);
	}
	
	private Logger(File logDir) {
		writersList = new HashMap<>();
		lastWriteTime = new HashMap<>();
		numOfWrites = 0;
		this.logDir = logDir;
		
		this.logDir = logDir.getAbsoluteFile();
		while(! fullDirPermissions(logDir)) {
			logDir = new File(JOptionPane.showInputDialog("The given log directory '" + logDir +
					"' is not accessible, please supply a new log directory."));
			if(! logDir.isAbsolute()) logDir = logDir.getAbsoluteFile();
		}
	}

	public static Logger createLogger(File path, Logger oldLogger) {
		if(! loggerCreated) {
			loggerCreated = true;
			return new Logger(path);
		} else if (oldLogger == null) {
			return null;
		} else {
			oldLogger.closeAllWriters();
			return new Logger(path);
		}
		
	}
	
	private int getNumOfWrites() { return (++numOfWrites % MAX_WRITE_NUM); }
	
	private void addNewWriter(String logName) {
		BufferedWriter writer = null;
		File logFile = new File(LOG_DIR + FS_DELIMITER + logName + ".log");
		try {
		    writer = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException ex) {
			if(! logName.equals(SYS_LOG_NAME))
			    log(SYS_LOG_NAME, "Couldn't create writer to log file: \"" + logFile, ex);
		}
		if(writer != null) {
			writersList.put(logName,writer);
			lastWriteTime.put(logName,getTimeStamp());
		}
	}
	
	private void closeOldWriters() {
		long timeStamp = getTimeStamp();
		for(String logName : lastWriteTime.keySet()) {
			Long lastLogWriteTime = lastWriteTime.get(logName);
			if(timeStamp - lastLogWriteTime > MAX_TIME_DIFF) {
				BufferedWriter writer = writersList.get(logName);
				if(writer != null) try { writer.close(); } catch(IOException ex) {}
				writersList.remove(logName);
				lastWriteTime.remove(logName);
			}
				
			
		}
	}
	
	private void logRotate(String logName) {
		String logPrefix = logDir + FS_DELIMITER + logName + ".";
		File log = new File(logPrefix + "log");
		if(! log.exists()) return;
		if(log.getTotalSpace() / 1024 > MAX_LOG_SIZE_KB) {
			File logRotFile = new File(logPrefix + MAX_LOG_FILES + ".log");
			if(logRotFile.exists()) logRotFile.delete();
			for (int i = MAX_LOG_FILES - 1; i > 0; --i) { // TODO : change to while loop for enhanced performance
				logRotFile = new File(logPrefix + i + ".log");
				if(logRotFile.exists()) logRotFile.renameTo(new File(logPrefix + (i+1) + ".log")); 
			}
			log.renameTo(new File(logPrefix + 1 + ".log"));
			try {
				log.createNewFile();
			} catch (IOException ex) {
				log(SYS_LOG_NAME,"Failed to create log file '" + log + "'.", ex);
			}
		}
		
	}
	
	public void log(String logName, String errMsg) {
		BufferedWriter writer;
		if(! writersList.containsKey(logName)) addNewWriter(logName);
		writer = writersList.get(logName);
		try {
			writer.write(getFormattedTime() + ": " + errMsg);
			writer.newLine();
			writer.flush();
			lastWriteTime.put(logName, getTimeStamp());
		} catch (IOException ex) { 
			if(writer != null) try { writer.close(); } catch (IOException ex2) {}
			writersList.remove(logName);
			lastWriteTime.remove(logName);
			log(SYS_LOG_NAME, "Failed to audit exeption to logfile:\"" + LOG_DIR + FS_DELIMITER + logName + ".log\".", ex);
		}
		if(getNumOfWrites() == MAX_WRITE_NUM - 1) {
			rotateLogs();
			closeOldWriters();
		}
	}
	
	public void log(String logName, String errMsg, Exception e) {
		BufferedWriter writer;
		if(! writersList.containsKey(logName)) addNewWriter(logName);
		writer = writersList.get(logName);
		try {
			writer.write(getFormattedTime() + ": " + errMsg);
			writer.newLine();
			writer.write("Exception type: " + e.getClass().getName());
			writer.newLine();
			writer.write("Exception Message: " + e.getMessage());
			writer.newLine();
			writer.write("Exception Stack Trace:");
			writer.newLine();
			writer.write(e.getStackTrace().toString());
			writer.flush();
			lastWriteTime.put(logName, getTimeStamp());
		} catch (IOException ex) { 
			if(writer != null) try { writer.close(); } catch (IOException ex2) {}
			writersList.remove(logName);
			lastWriteTime.remove(logName);
			if(! logName.equals(SYS_LOG_NAME))
				log(SYS_LOG_NAME, "Failed to audit exeption to logfile:\"" + LOG_DIR + FS_DELIMITER + logName + ".log\".", ex);
		}
		if(getNumOfWrites() == MAX_WRITE_NUM - 1) {
			rotateLogs();
			closeOldWriters();	
		}
	}
	
	private void rotateLogs() {
		for (String logName : writersList.keySet())
			logRotate(logName);
	}
	
	private void closeAllWriters() {
		for (String writerKey : writersList.keySet()) {
			BufferedWriter writer = writersList.get(writerKey);
			if(writer != null) try { writer.close(); } catch(IOException ex) {}
		}
	}
	
	protected void finalize() throws Throwable {
		closeAllWriters();
		super.finalize();
	}
	
	protected void setMAX_LOG_FILES(int mAX_LOG_FILES) { MAX_LOG_FILES = mAX_LOG_FILES; }
	protected void setMAX_LOG_SIZE_KB(int mAX_LOG_SIZE_KB) { MAX_LOG_SIZE_KB = mAX_LOG_SIZE_KB; }
	
}
