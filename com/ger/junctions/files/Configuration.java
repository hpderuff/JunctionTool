package com.ger.junctions.files;

import java.util.Properties;
import java.io.IOException;
import org.apache.log4j.Logger;


public class Configuration {
	
	private String xmloutputfile="pdout.xml";
	private String acl_only_file="aclonlyobjects.xml";
	private String objectspace="/WebSEAL";
	private String pdadmintool="/usr/bin/pdadmin";
	private String configurationfile="configuration.properties";

	Logger logger = Logger.getLogger(Configuration.class);

	public Configuration()
	{
		Properties configFile = new Properties();
		try {
			configFile.load(this.getClass().getClassLoader().getResourceAsStream(configurationfile));
		} catch (NullPointerException e)
		{
			logger.info("configuration.properties not found!");
		} catch (IOException e) {
			logger.warn("I/O exception while loading " + configurationfile);
			logger.warn(e.getMessage());
		}
		
		xmloutputfile=configFile.getProperty("xml_output_file", xmloutputfile);
		objectspace=configFile.getProperty("objectspace", objectspace);
		pdadmintool=configFile.getProperty("pdamintool", pdadmintool);
		acl_only_file=configFile.getProperty("acl_only_file", acl_only_file);
	}
	
	public String getXmlOutputFile()
	{
		return xmloutputfile;
	}

	public String getObjectSpace()
	{
		return objectspace;
	}
	
	public String getPdadminTool()
	{
		return pdadmintool;
	}
	
	public String getAclOnlyFile()
	{
		return acl_only_file;
	}


}
