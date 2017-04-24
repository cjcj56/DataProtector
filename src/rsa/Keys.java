package rsa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import static rsa.CryptServices.*;

public class Keys {
	
	
	private BigInteger privateKey, publicKey, mod;
	private Keys(BigInteger mod, BigInteger publicKey, BigInteger privateKey) {
		this.mod = mod;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}
	
	public Keys(String n, String e, String d) {
		this(new BigInteger(n), new BigInteger(e), new BigInteger(d));
	}

	public static Keys generateKeys() {
		BigInteger p, q, totient, n, e, d;
		p = BigInteger.probablePrime((int) Math.ceil(CryptServices.KEY_SIZE_BITS / 2), new SecureRandom());
		SecureRandom sr = new SecureRandom();
		q = BigInteger.probablePrime((int) Math.floor(CryptServices.KEY_SIZE_BITS / 2), sr);
		while(p.compareTo(q) == 0) {
			q = BigInteger.probablePrime((int) Math.floor(CryptServices.KEY_SIZE_BITS / 2), sr);
		}
		n = p.multiply(q);
		totient = n.add(BigInteger.ONE).subtract(p.add(q));
		sr = new SecureRandom();
		e = BigInteger.probablePrime((int) (n.bitCount() * 0.4), sr);
		while(e.compareTo(totient) >= 0)
			e = BigInteger.probablePrime((int) (n.bitCount() * 0.4), sr);
		assert ((e.compareTo(totient) < 0) && (totient.gcd(e).compareTo(BigInteger.ONE) == 0));
		d = e.modInverse(totient);
		return new Keys(n, e, d);
	}
	
	public  BigInteger getPrivateKey() { return privateKey; }
	public  BigInteger getPublicKey()  { return publicKey;  }
	public  BigInteger getMod()        { return mod;        }
	
	public String useKey(String message, boolean encrypt) {
		if(message.length() == 0) return "";
		return useKey(new BigInteger(message,16), encrypt).toString(16);
	}
	
	public byte [] useKey(byte [] message, boolean encrypt) {
		return useKey(new BigInteger(message), encrypt).toByteArray();
	}
	
	public BigInteger useKey(BigInteger message, boolean encrypt) {
		if(encrypt) { return message.modPow(publicKey, mod);  }
		else        { return message.modPow(privateKey, mod); }
	}
	
	public void saveKeyToFile(BufferedWriter writer, Credentials serverCreds) throws IOException {
		String n = mod.toString(), e = publicKey.toString(), d = privateKey.toString();
		if(serverCreds != null) {
			n = serverCreds.encryptString(n);
			e = serverCreds.encryptString(e);
			d = serverCreds.encryptString(d);
		}
		writer.write(n);
		writer.newLine();
		writer.write(e);
		writer.newLine();
		writer.write(d);
	}
	
	public void saveKeyToFile(File file, Credentials serverCreds) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			saveKeyToFile(writer, serverCreds);
		} catch (IOException ex) {
			logger.log("Keys", "Couldn't write key to file: \"" + file + "\"", ex);
		} finally {
			if(writer != null) try { writer.close(); } catch(IOException ex) {}
		}
		
	}
	
	public static Keys getKeyFromFile(BufferedReader reader, Credentials serverCreds) throws IOException {
		String n, e, d;
		n = e = d = null;
		n = reader.readLine();
		e = reader.readLine();
		d = reader.readLine();
		
		if(serverCreds != null) {
			n = serverCreds.decryptString(n);
			e = serverCreds.decryptString(e);
			d = serverCreds.decryptString(d);
		}
		
		return new Keys(n, e, d);
	}
	
	public static Keys getKeyFromFile(File file, Credentials serverCreds) {
		BufferedReader reader = null;
		Keys k = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			for(int i = 0; i < 3; ++i) reader.readLine();
			k = getKeyFromFile(reader, serverCreds);
		} catch (FileNotFoundException ex) {
			logger.log("Keys", "Couldn't find file: \"" + file + "\"", ex);
		} catch (IOException ex) {
			logger.log("Keys", "Couldn't read key from file: \"" + file + "\"", ex);
		} finally {
			if(reader != null) try { reader.close(); } catch (IOException ex) {}
		}
		
		return k;
	}
	
}