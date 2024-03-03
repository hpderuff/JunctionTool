package com.ger.junctions.junction;

import org.apache.log4j.Logger;

public class JunctionPropertiesCommandLine 
{
	private String commandline[];
	Logger logger = Logger.getLogger(JunctionPropertiesCommandLine.class);
	
	JunctionPropertiesCommandLine(String websealserver, String junctionpoint, String virtualhostlabel, String type, String statefuljunction, 
			String scriptingsupport, String rootdirectory, String virtualhostname, 
			String[] uuidarray, String[] hostnamearray, String[] portarray,  
			String[] serverdnarray,	String[] caseinsensitiveurlsarray, String[] allowwindowsstyleurlsarray)
	{
		// how many commandlines are needed (how many backend servers)?
		int numberbackends = hostnamearray.length;
		logger.debug("Number of backend servers: " + numberbackends);
		// one commandline for each backend server if there are backend servers
		if (numberbackends==0) commandline = new String[1];
		else commandline = new String[numberbackends];
				
		// if it is a virtual host, we need to add the keyword virtualhost
		// and replace junctionname
		String virtualhoststring = "";
		if (virtualhostlabel!=null && virtualhostlabel.length()>0)
		{
			virtualhoststring = " virtualhost ";
			junctionpoint = virtualhostlabel;
		}

		// create first commandline
		commandline[0] = new String("server task " + websealserver + virtualhoststring + " create -f" + getType(type) + 
			getStateful(statefuljunction) + getScriptingSupport(scriptingsupport) + 
			getRootDirectory(rootdirectory) + getUuid(uuidarray, 0) +
			getHostName(hostnamearray, 0) + getVirtualHostname(virtualhostname) + getPort(portarray, 0) + getServerDn(serverdnarray, 0) + 
			getCaseinsensitiveUrl(caseinsensitiveurlsarray, 0) + 
			getAllowWindowsStyleUrls(allowwindowsstyleurlsarray, 0) + " " + junctionpoint);
		logger.debug("First commandline: " + commandline[0]);
		
		// add further commandlines
		for (int i=1; i<numberbackends; i++)
		{
		 	commandline[i] = new String("server task " + websealserver + virtualhoststring + " add " +
		 			getUuid(uuidarray, i) +
		 			getHostName(hostnamearray, i) + getPort(portarray, i) + 
					getServerDn(serverdnarray, i) + 
					getCaseinsensitiveUrl(caseinsensitiveurlsarray, i) + 
					getAllowWindowsStyleUrls(allowwindowsstyleurlsarray, i) + " " + 
					junctionpoint);
		 	logger.debug("commandline " + (i+1) + ": " + commandline[i]);
		}

	}
	
	/**
	 * 
	 * @return strings of pdadmin commands that are needed to create the junction
	 */
	public String[] getCommandLine()
	{
		return commandline;
	}
	
	/**
	 * 
	 * avoid NullPointerExceptions by checking first if the field of the array exists
	 * @param Array that will be checked
	 * @param count: number of field to be checked  
	 * @return true if length of the array is not large enough, or if the field is "" or null 
	 */
	private boolean CheckForEmptyArray(String[] stringarray, int count)
	{
		if (stringarray.length<=count)
		{
				return true;
		} else
		{
			if (stringarray[count]==null || stringarray[count].equals(""))
				return true;
		}
		return false;
	}
	
	
	private String getType(String type)
	{
		String temp=""; 

		if (type.equals("TCP"))
			temp=" -t tcp";
		if (type.equals("Local"))
			temp=" -t local";
		if (type.equals("SSL"))
			temp=" -t ssl";
		return temp;
	}
	
	private String getStateful(String statefuljunction)
	{
		if (statefuljunction.equals("yes")) 
			return " -s";
		else 
			return "";
	}

	private String getScriptingSupport(String scriptingsupport)
	{
		if (scriptingsupport.equals("yes")) 
			return " -j";
		else 
			return "";
	}
	
	private String getRootDirectory(String rootdirectory)
	{
		if (rootdirectory.equals(""))
			return "";
		else
			return " -d " + rootdirectory;
	}
	
	private String getUuid(String[] uuid, int count)
	{
		if (CheckForEmptyArray(uuid, count)==true)
			return "";
		if (uuid[count].equals("")||uuid[count]==null) 
			return ""; 
		else 
			return " -u " + uuid[count];
	}
	
	private String getHostName(String[] hostname, int count)
	{
		if (CheckForEmptyArray(hostname, count)==true)
			return "";
		if (hostname[count].equals("")||hostname[count]==null) 
			return ""; 
		else 
			return " -h " + hostname[count];
	}
	
	private String getPort(String[] port, int count)
	{
		if (CheckForEmptyArray(port, count)==true)
			return "";
		if (port[count].equals("")||port[count]==null) 
			return "";
		else 
			return " -p " + port[count];
	}
	
	private String getVirtualHostname(String virtualhostname)
	{
		if (virtualhostname.equals(""))
			return "";
		else
			return " -h " + virtualhostname; 
	}
	
	private String getServerDn(String[] serverdn, int count)
	{
		if (CheckForEmptyArray(serverdn, count)==true)
			return "";
		if (serverdn[count].equals("")||serverdn[count]==null)
			return "";
		else
			return " -D \"" + serverdn[count] + "\"";
	}
	
	private String getCaseinsensitiveUrl(String[] caseinsensitive, int count)
	{
		if (CheckForEmptyArray(caseinsensitive, count)==true)
			return "";
		if (caseinsensitive[count].equals("yes"))
			return " -i";
		else
			return "";
	}
	
	private String getAllowWindowsStyleUrls(String[] allowwindowsstyleurls, int count)
	{
		if (CheckForEmptyArray(allowwindowsstyleurls, count)==true)
			return "";
		if (allowwindowsstyleurls[count].equals("yes"))
			return " -w";
		else
			return "";
	}

}
