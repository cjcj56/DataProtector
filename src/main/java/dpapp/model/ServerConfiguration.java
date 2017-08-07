package dpapp.model;


public class ServerConfiguration extends AppConfiguration {
	
	private int screenWidth, screenHeight;
	private Credentials serverCreds;
	private int maxLogFiles, maxLogSizeKb;
	
	public ServerConfiguration() {
		super();
	}
	
	public ServerConfiguration(int screenWidth, int screenHeight, Credentials serverCreds, int maxLogFiles,
			int maxLogSizeKb) {
		super();
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.serverCreds = serverCreds;
		this.maxLogFiles = maxLogFiles;
		this.maxLogSizeKb = maxLogSizeKb;
	}
	
	public ServerConfiguration( String rootDir, String hashAlgoritm, int blockSize, String charset, int keySizeBits,
			int screenWidth, int screenHeight, Credentials serverCreds, int maxLogFiles,
			int maxLogSizeKb) {
		super(rootDir, hashAlgoritm, blockSize, charset, keySizeBits);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.serverCreds = serverCreds;
		this.maxLogFiles = maxLogFiles;
		this.maxLogSizeKb = maxLogSizeKb;
	}
	
	public ServerConfiguration(int confId, String rootDir, String hashAlgoritm, int blockSize,
			String charset, int keySizeBits, int screenWidth, int screenHeight, Credentials serverCreds, 
			int maxLogFiles,int maxLogSizeKb) {
		super(confId, rootDir, hashAlgoritm, blockSize, charset, keySizeBits);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.serverCreds = serverCreds;
		this.maxLogFiles = maxLogFiles;
		this.maxLogSizeKb = maxLogSizeKb;
	}
	
	
	public int getScreenWidth() {
		return screenWidth;
	}
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}
	public int getScreenHeight() {
		return screenHeight;
	}
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
	public Credentials getServerCreds() {
		return serverCreds;
	}
	public void setServerCreds(Credentials serverCreds) {
		this.serverCreds = serverCreds;
	}
	public int getMaxLogFiles() {
		return maxLogFiles;
	}
	public void setMaxLogFiles(int maxLogFiles) {
		this.maxLogFiles = maxLogFiles;
	}
	public int getMaxLogSizeKb() {
		return maxLogSizeKb;
	}
	public void setMaxLogSizeKb(int maxLogSizeKb) {
		this.maxLogSizeKb = maxLogSizeKb;
	}

	
	
}
