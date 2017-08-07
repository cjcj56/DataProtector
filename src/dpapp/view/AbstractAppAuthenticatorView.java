package dpapp.view;

import java.awt.Component;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import dpapp.controller.AppAuthenticator;
import dpapp.model.DataProtectorImpl;

public abstract class AbstractAppAuthenticatorView implements AppAuthenticatorView {
	
	@Autowired
	private AppAuthenticator dpAppAuth;
	private HashMap<String, Component> componentMap = new HashMap<>();
	
	public AbstractAppAuthenticatorView() {}
	
	public AppAuthenticator getDpAppAuth() {
		return dpAppAuth;
	}
	public void setDpAppAuth(AppAuthenticator dpAppAuth) {
		this.dpAppAuth = dpAppAuth;
	}
	public HashMap<String, Component> getComponentMap() {
		return componentMap;
	}
	public void setComponentMap(HashMap<String, Component> componentMap) {
		this.componentMap = componentMap;
	}
	
	public Component getComponent(String componentName) {
		return componentMap.get(componentName);
	}


}
