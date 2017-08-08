package com.binyaminkisch.data_protector.controller;

import com.binyaminkisch.data_protector.model.Credentials;
import com.binyaminkisch.data_protector.model.DataProtector;

public interface AppAuthenticator {
	
	public DataProtector login(String username, String password);
	public void changeCreds(String userName, String oldPassword, String newPassword);
	public Credentials validateUser(String username, String password);
	public void createUser(String username, String password);
	public void deleteUser(String username, String password);
	
}
