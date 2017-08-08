package com.binyaminkisch.data_protector.model;

public interface AppConfigurator {
	public void loadConfiguration();
	public void saveConfiguration();
	public void setConfigurationAttribute(String attribute, String value);
	public String getConfigurationAttribute(String attribute, String value);
	public void setConfigurationFileLocation(String fullPath);
	public String getConfigurationFileLocation();
	public void exportConfigurationToFile(String pathToConfiguration);
	public void importConfigurationFromFile(String pathToConfiguration);
}
