/**
 * 
 */
package com.binyaminkisch.data_protector.model;

import java.io.File;
import java.util.Collection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.binyaminkisch.data_protector.view.DataProtectorViewImpl;

/**
 * @author binyamink
 *
 */
public abstract class AbstractDataProtector implements DataProtector {

	private ServerConfiguration servConfigs;
	private DataProtectorViewImpl viewer;
	private Credentials loggedCreds;
	private ApplicationContext context;

	public AbstractDataProtector(Credentials loggedCreds) {	
		context = new ClassPathXmlApplicationContext("spring.xml");
		servConfigs = (ServerConfiguration) context.getBean("serverConfiguraiton");
		this.loggedCreds = loggedCreds;
	}
	
	
	public ApplicationContext getContext() {
		return context;
	}
	
	
	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
	
	public ServerConfiguration getServConfigs() {
		return servConfigs;
	}


	public void setServConfigs(ServerConfiguration servConfigs) {
		this.servConfigs = servConfigs;
	}


	public DataProtectorViewImpl getViewer() {
		return viewer;
	}


	public void setViewer(DataProtectorViewImpl viewer) {
		this.viewer = viewer;
	}


	public Credentials getLoggedCreds() {
		return loggedCreds;
	}


	public void setLoggedCreds(Credentials loggedCreds) {
		this.loggedCreds = loggedCreds;
	}


	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#logout()
	 */
	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#encryptFile(java.lang.String)
	 */
	@Override
	public void encryptFile(String fullPathToFile) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#encryptFile(java.io.File)
	 */
	@Override
	public void encryptFile(File file) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#decryptFile(java.lang.String)
	 */
	@Override
	public void decryptFile(String fullPathToFile) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#decryptFile(java.io.File)
	 */
	@Override
	public void decryptFile(File file) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#encrypt(java.lang.String)
	 */
	@Override
	public String encrypt(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#decrypt(java.lang.String)
	 */
	@Override
	public String decrypt(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#storeFile(java.lang.String)
	 */
	@Override
	public void storeFile(String fullPathToFile) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#storeFile(java.io.File)
	 */
	@Override
	public void storeFile(File file) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#fetchAllTitles()
	 */
	@Override
	public Collection<String> fetchAllTitles() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#fetchTitlesContaining(java.lang.String)
	 */
	@Override
	public Collection<String> fetchTitlesContaining(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.binyaminkisch.data_protector.model.DataProtector#fetchFile(java.lang.String)
	 */
	@Override
	public File fetchFile(String fullPathToFile) {
		// TODO Auto-generated method stub
		return null;
	}

}
