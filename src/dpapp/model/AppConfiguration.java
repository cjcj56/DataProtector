package dpapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AppConfiguration {
	
	@Id @GeneratedValue
	private int confId;
	private String rootDir;
	private String hashAlgoritm;
	private int blockSize;
	private String charset;
	private int keySizeBits;
	
	public AppConfiguration() {}
	
	public AppConfiguration(String rootDir, String hashAlgoritm, int blockSize, String charset, int keySizeBits) {
		this.rootDir = rootDir;
		this.hashAlgoritm = hashAlgoritm;
		this.blockSize = blockSize;
		this.charset = charset;
		this.keySizeBits = keySizeBits;
	}
	public AppConfiguration(int confId, String rootDir, String hashAlgoritm, int blockSize, String charset,
			int keySizeBits) {
		this.confId = confId;
		this.rootDir = rootDir;
		this.hashAlgoritm = hashAlgoritm;
		this.blockSize = blockSize;
		this.charset = charset;
		this.keySizeBits = keySizeBits;
	}
	
	
	public int getConfId() {
		return confId;
	}
	public void setConfId(int confId) {
		this.confId = confId;
	}
	public String getRootDir() {
		return rootDir;
	}
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
	public String getHashAlgoritm() {
		return hashAlgoritm;
	}
	public void setHashAlgoritm(String hashAlgoritm) {
		this.hashAlgoritm = hashAlgoritm;
	}
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public int getKeySizeBits() {
		return keySizeBits;
	}
	public void setKeySizeBits(int keySizeBits) {
		this.keySizeBits = keySizeBits;
	} 
	@Override
	public String toString() {
		return "AppConfiguration [configurationId=" + confId + ", rootDir=" + rootDir + ", hashAlgoritm="
				+ hashAlgoritm + ", blockSize=" + blockSize + ", charset=" + charset + "]";
	}
	
	
}
