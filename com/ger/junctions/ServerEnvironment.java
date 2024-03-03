package com.ger.junctions;

public class ServerEnvironment {
	
	private String serverLoginName = null;
	private String serverLoginPassword = null;
	private String serverName = null;
	private String objectName = null;
	private String pdadminUserName = null;
	private String pdadminPassword = null;

	/**
	 * Set username for login to host
	 * 
	 */
	public void setServerLoginName(String username) {

		serverLoginName=username;
	}	

	/**
	 * Get username for login to host
	 * 
	 */
	public String getServerLoginName() {

		return serverLoginName;
	}	

	/**
	 * Set password for login to host
	 * 
	 */
	public void setServerLoginPassword(String password) {

		serverLoginName=password;
	}

	
	/**
	 * Get password for login to host
	 * 
	 */
	public String getServerLoginPassword() {

		return serverLoginPassword;
	}

	
	/**
	 * Set hostname
	 * 
	 */
	public void setServerName(String servername) {

		serverName=servername;
	}

	/**
	 * Get hostname
	 * 
	 */
	public String getServerName() {

		return serverName;
	}


	/**
	 * Set entry point for WebSeal commands
	 * 
	 */
	public void setObjectName(String objectname) {

		objectName=objectname;
	}
	
	/**
	 * Get entry point for WebSeal commands
	 * 
	 */
	public String getObjectName() {

		return objectName;
	}

	/**
	 * Set loginname for pdadmin
	 * 
	 */
	public void setPdadminUserName(String username) {

		pdadminUserName=username;
	}

	/**
	 * Get loginname for pdadmin
	 * 
	 */
	public String getPdadminUserName() {

		return pdadminUserName;
	}

	/**
	 * Set password for pdadmin
	 * 
	 */
	public void setPdadminPassword(String password) {

		pdadminPassword=password;
	}
	
	/**
	 * Get password for pdadmin
	 * 
	 */
	public String getPdadminPassword() {

		return pdadminPassword;
	}

}
