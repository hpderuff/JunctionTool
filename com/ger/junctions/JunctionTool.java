package com.ger.junctions;

import org.apache.log4j.Logger;

import com.ger.junctions.ServerEnvironment;
import com.ger.junctions.files.PdadminTool;


public class JunctionTool {
	
	private static Logger logger = Logger.getRootLogger();

	public static void main(String args[])
	{
		PdadminTool pdadmintool = new PdadminTool();

		// check if username and password are provided
		if (args.length<2)
		{
			logger.error("Syntax: java -jar junctiontool.jar [pdadmin user] [pdadmin password]");
			System.exit(1);			
		}
		String pdadminuser = args[0];
		String pdadminpassword = args[1];
		
		if (pdadminuser==null && pdadminuser=="" && pdadminpassword==null && pdadminpassword=="")
		{
			logger.error("Syntax: java -jar junctiontool.jar [pdadmin user] [pdadmin password]");
			System.exit(1);
		}
	
		// save credentials
		ServerEnvironment serverenv = new ServerEnvironment();
		serverenv.setPdadminUserName(pdadminuser);
		serverenv.setPdadminPassword(pdadminpassword);
		// TODO: add server name, server login for SSH login
		
		String [] serverlist = pdadmintool.getServerList(serverenv);
		//String [] serverlist = {"imap-webseald-iuwb953.app-intra-pre.web.audi.vwg"};
		if (serverlist == null) 
		{
				logger.error("Could not get server list from pdadmintool.");
		 		logger.error("Please check username/password and availability of pdadmintool tool");
		 		System.exit(1);
		}

		PrepareXML preparexml = new PrepareXML(serverenv, serverlist);
		
	}
	

}
