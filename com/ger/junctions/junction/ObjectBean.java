package com.ger.junctions.junction;

import org.apache.log4j.Logger;

import com.ger.junctions.PrepareXML;

/**
 * 
 * stores all junctions of a webseal server with all properties
 * (attributes and required command lines for that junction)
 *
 */
public class ObjectBean {
	
	Logger logger = Logger.getLogger(ObjectBean.class);
	
	private String [] commandlines = null;
	private String [] attributes = null;
	private String websealserver = "";
	private String junctionname = "";
	private String attachedacl = "";
	private String attachedpop = "";
	private final String separator = "-webseald-";
	
	public void setCommandLine(String[] cl)
	{
		commandlines=cl;
	}
	
	public String[] getCommandLine()
	{
		return commandlines;
	}
	
	public void setWebsealName(String websealservername)
	{
		websealserver=websealservername;
	}
	
	public String getWebsealName()
	{
		return websealserver;
	}

	public void setJunctionName(String junctionstring)
	{
		junctionname=junctionstring;
	}
	
	public String getJunctionName()
	{
		return junctionname;
	}
	
	public void setAttachedAcl(String attacl)
	{
		attachedacl=attacl;
	}
	
	public String getAttachedAcl()
	{
		return attachedacl;
	}

	public void setAttachedPop(String attpop)
	{
		attachedpop=attpop;
	}
	
	public String getAttachedPop()
	{
		return attachedpop;
	}

	public void setAttributeArray(String [] al)
	{
		attributes=al;
	}
	
	public String[] getAttributeArray()
	{
		return attributes;
	}
	
	public String getWebSealHost()
	{
		// hostname is after the separator
		if (websealserver.indexOf(separator)+separator.length()>0)
			return websealserver.substring(websealserver.indexOf(separator)+separator.length(), websealserver.length());
		else
			return "";
	}
	
	public String getWebSealInstance()
	{
		// instance name is before separator
		if (websealserver.indexOf(separator)+separator.length()>0)
			return websealserver.substring(0, websealserver.indexOf(separator));
		else
			return "";
	}
	
}
