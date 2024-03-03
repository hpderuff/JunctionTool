package com.ger.junctions.files;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ger.junctions.ServerEnvironment;

/**
 * executes pdadmin commands
 * 
 * @param ServerEnvironment (user, password), String (command)
 * @return pdadmin output as string[]
 */
public class PdadminTool {
	
	Logger logger = Logger.getLogger(PdadminTool.class);

	private String [] pdadminExecute(ServerEnvironment env, String command)
	{
		ArrayList arraylist=new ArrayList();

		try 
		{
			//execute pdadmin with "command"
            logger.debug("Start executing pdadmin");
			Runtime rt=Runtime.getRuntime(); 
			Process proc=rt.exec("/usr/bin/pdadmin -a "	+ env.getPdadminUserName() + " -p " + env.getPdadminPassword() + " " +command);
			logger.debug("/usr/bin/pdadmin -a " + env.getPdadminUserName() + " -p <password> " +command);
			proc.waitFor();
			int exitvalue=proc.exitValue();
			logger.info("pdadmin process exit status: " + exitvalue);
            logger.debug("End executing pdadmin");
            if (exitvalue > 0) 
            	{
            		logger.error("pdadmin did not finish properly: " + exitvalue);
            		logger.error(proc.getErrorStream().toString());
            		return null;
            	}

			// write output from pdadmin to an ArrayList
			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null)
                {
            		logger.debug("" + line);
            		arraylist.add(line);
                }
            logger.info("Number of results: " + arraylist.size());

		} 
		catch(Exception e) 
		{ logger.error(e.getMessage()); } 
		
		//convert ArrayList to an array of strings
		String [] stringlist = new String[arraylist.size()];
		stringlist = (String[]) arraylist.toArray(stringlist);
		
		return stringlist;
	}

	
	public String [] getServerList(ServerEnvironment env)
	{ 
		String [] tempserverlist=pdadminExecute(env, "server list");
		// remove leading blanks from all entries in the serverlist
		for (int i=0; i<tempserverlist.length; i++)
		{
			while (tempserverlist[i].indexOf(" ") == 0)
				tempserverlist[i]=tempserverlist[i].substring(1);
		}
		return tempserverlist;
	}

	
	
	public String [] getAclList(ServerEnvironment env)
	{
		return pdadminExecute(env, "acl list");
	}
	
	public String [] getAclFind(ServerEnvironment env, String acl)
	{
		return pdadminExecute(env, "acl find " + acl);
	}
	

	public String [] getServerTaskList(ServerEnvironment env, String server)
	{
			return pdadminExecute(env, "server task " + server + " list");
	}
	
	public String [] getServerTaskVirtualhostList(ServerEnvironment env, String server)
	{
			return pdadminExecute(env, "server task " + server + " virtualhost list");
	}
	
	
	public String [] getServerTaskVirtualhostShow(ServerEnvironment env, String server, String junction)
	{
		return pdadminExecute(env, "server task " + server + " virtualhost show " + junction);
	}
	

	public String [] getServerTaskShow(ServerEnvironment env, String server, String junction)
	{
		return pdadminExecute(env, "server task " + server + " show " + junction);
	}

	/**
	 * 
	 * @param env  username, password
	 * @param server  servername
	 * @param objectspace
	 * @param junction name of the junction
	 * @return  output of "object show" for that junction
	 */
	public String [] getObjectShow(ServerEnvironment env, String server, String objectspace, String junction)
	{
		String aclinstancename;
		String aclhostname;
		final String separator = "-webseald-";
		
		// convert servername from "server list" output to acl convention

		aclinstancename = new String();
		aclinstancename = server.toString();
		
		// instance name is separated by a "-"
		if (aclinstancename.indexOf("-")>0)
			aclinstancename = aclinstancename.substring(0, aclinstancename.indexOf(separator));
		else
		{
			logger.warn("Could not find instance name for " + aclinstancename);
			return null;
		}
		
		// hostname is after instance name
		aclhostname = server.substring(server.indexOf(separator)+separator.length());
		
		// add objectspace to servername
		if (objectspace.endsWith("/")==false) objectspace=objectspace + "/";
		
		logger.debug("getObjectShow parameters: " + objectspace + aclhostname + 
				"-" + aclinstancename + junction);
		return pdadminExecute(env, "object show " + objectspace + aclhostname + 
				"-" + aclinstancename + junction);
	}
	
}
