package com.ger.junctions.junction;

import java.util.ArrayList;

import org.apache.log4j.Logger;


/**
 * 
 * Reads properties from one junction
 * checks which attributes are set
 * and recreates the original create command
 * 
 */
public class JunctionProperties {
	
	private static Logger logger = Logger.getLogger(JunctionProperties.class);
	
	// strings to search for
	final String junctionpointstring = "Junction point: ";
	final String virtualhostlabelstring = "Virtual Host Junction label: ";
	final String scriptingsupportstring = "Scripting support: ";
	final String typestring = "Type: ";
	final String statefuljunctionstring = "Stateful junction: ";
	final String uuidstring = " ID: ";
	final String hostnamestring = "Hostname: ";
	final String portstring = "Port: ";
	final String virtualhostnamestring = "Virtual hostname: ";
	final String serverdnstring = "Server DN: ";
	final String caseinsensitiveurlsstring = "Case insensitive URLs: ";
	final String allowwindowsstyleurlsstring = "Allow Windows-style URLs: ";
	final String rootdirectorystring = "Root Directory: ";
	final String authenticationheaderstring = "Authentication HTTP header: ";
//	final String querycontentsurl = "Query_contents URL: ";
//	final String querycontents = "Query-contents: ";
	
	// contain single values
	private String websealservername = "";
	private String junctionpoint = "";
	private String virtualhostlabel = "";
	private String type = "";
	private String statefuljunction = "";
	private String scriptingsupport = "";
	private String rootdirectory = "";
	private String authenticationheader = "";
	private String virtualhostname = "";
		
	// there might be several backend servers/attributes for a junction, so we need arraylists for these values
	ArrayList uuidarray = new ArrayList();
	ArrayList hostnamearray = new ArrayList();
	ArrayList portarray = new ArrayList();
	ArrayList serverdnarray = new ArrayList();
	ArrayList caseinsensitiveurlsarray = new ArrayList();
	ArrayList allowwindowsstyleurlsarray = new ArrayList();
	ArrayList authenticationheaderarray = new ArrayList();
	
	final String donotinsert="do not insert";
	

	// contains a list of all properties from this junction
	ArrayList propertyarray=new ArrayList();
	
	/**
	 * 
	 * @param: WebSEAL server; all lines from output of getJunctionProperties ("server task ... show /junction")
	 * gets values and stores them in strings and arrays 
	 * 
	 */
	public JunctionProperties(String websealserver, String[] junction)
	{
		// save for future command line creation
		websealservername = websealserver;
		
		// go through all lines from "server task ... show /junction"
		// and search for the values of junction
		for (int i=0; i<junction.length;i++)
		{
			String line = junction[i].toString();
			
			if (checkFor(line, junctionpointstring))
			{
				junctionpoint = getValue(line, junctionpointstring);
				propertyarray.add(junctionpointstring + junctionpoint);
				logger.debug(junctionpointstring + junctionpoint);
			}
			
			if (checkFor(line, virtualhostlabelstring))
			{
				virtualhostlabel = getValue(line, virtualhostlabelstring);
				propertyarray.add(virtualhostlabelstring + virtualhostlabel);
				logger.debug(virtualhostlabelstring + virtualhostlabel);
			}
						
			// get the junction "type" (TCP, SSL, local etc.) 
			if (checkFor(line, typestring))
			{
				type = getValue(line, typestring);
				propertyarray.add(typestring + type);
				logger.debug(typestring + type);
			}
			
			if (checkFor(line, statefuljunctionstring))
			{
				statefuljunction = getValue(line, statefuljunctionstring);
				propertyarray.add(statefuljunctionstring + statefuljunction);
				logger.debug(statefuljunctionstring + statefuljunction);
			}

			if (checkFor(line, scriptingsupportstring))
			{
				scriptingsupport = getValue(line, scriptingsupportstring);
				propertyarray.add(scriptingsupportstring + scriptingsupport);
				logger.debug(scriptingsupportstring + scriptingsupport);
			}
			
			if (checkFor(line, rootdirectorystring))
				rootdirectory = addValue(line, rootdirectorystring);
			
			if (checkFor(line, uuidstring))
				uuidarray.add(addValue(line, uuidstring));
			
			if (checkFor(line, hostnamestring))
				hostnamearray.add(addValue(line, hostnamestring));
			
			if (checkFor(line, portstring))
				portarray.add(addValue(line, portstring));
			
			if (checkFor(line, virtualhostnamestring))
				virtualhostname = addValue(line, virtualhostnamestring);
			
			if (checkFor(line, serverdnstring))
				serverdnarray.add(addValue(line, serverdnstring));
			
			if (checkFor(line, caseinsensitiveurlsstring))	
				caseinsensitiveurlsarray.add(addValue(line, caseinsensitiveurlsstring));
			
			if (checkFor(line, allowwindowsstyleurlsstring))
				allowwindowsstyleurlsarray.add(addValue(line, allowwindowsstyleurlsstring));
			
			if (checkFor(line, authenticationheaderstring))
			{
				authenticationheader = getMultipleValues(line, authenticationheaderstring);
				propertyarray.add(authenticationheaderstring + authenticationheader);
				logger.debug(authenticationheaderstring + authenticationheader);
			}
			
		}
	}
	
	/**
	* 
    * @return all attributes (retrieved from server ... task show) 
    * 			of this junction in human readable format
	*/
	public String[] getJunctionAttributes()
	{
		logger.debug("propertyarray.size = " + propertyarray.size());
		String[] propertylist = new String[propertyarray.size()];
		propertylist = (String[]) propertyarray.toArray(propertylist);
		return propertylist;
	}
	
	public String getJunctionPoint()
	{
		return junctionpoint;
	}
	
	public String getVirtualHost()
	{
		return virtualhostlabel;
	}
		
	/**
	 *  adds the value to the propertyarray and gives back the value
	 * @param line  complete line
	 * @param fixstring  key to search for
	 * @return String  containing the value found after the key 
	 */
	private String addValue(String line, String fixstring)
	{
		propertyarray.add(fixstring + getValue(line, fixstring));
		logger.debug(fixstring + getValue(line, fixstring));
		return getValue(line, fixstring);
	}
	
	/**
	 * Checks if the output line contains a parameter
	 * @param line single 
	 * @param parameter keyword to look for
	 * @return true if parameter is found in line, otherwise false
	 */
	private boolean checkFor(String line, String parameter)
	{
		int value = line.indexOf(parameter);
		if (value > -1) return true; 
		else return false;
	}
	
	
	/**
	 * get the value of the corresponding search string
	 * @param line, searchstring search this line for "searchstring"  
	 * @return String with value or "" if "searchstring" was not found in "line"
	 */
	private String getValue(String line, String searchstring)
	{
		String value = null;
		
		int index = line.indexOf(searchstring);
		if (index > -1)
		{
			try
			{
				value = line.substring(index + searchstring.length());
			} catch (IndexOutOfBoundsException e)
			{
				logger.error(e.getMessage());
				return "";
			}
			logger.debug(searchstring + value);
			return value;
		}
		else return "";
	}

	/**
	 * @param line
	 * @param searchstring
	 * @return line without searchstring, values separated by comma
	 * string "insert - " needs to be removed
	 */
	private String getMultipleValues(String line, String searchstring)
	{
		final String insertstring = "insert - ";
		int index=0;
		
		// separate searchstring and values
		index = line.indexOf(searchstring);
		if (index > -1)
		{
			try
			{
				line = line.substring(index + searchstring.length());
			} catch (IndexOutOfBoundsException e)
			{
				logger.error(e.getMessage());
				return null;
			}
			// check if there are any attributes
			// if not, return empty string
			logger.debug("Line before checking attributes: " + line);
			if (line.trim().compareTo(donotinsert)==0) return "";
			
			// remove the keyword "insert - "
			int insertposition = line.indexOf(insertstring);
			if (insertposition > -1)
			{
				try
				{
					line = line.substring(insertposition + insertstring.length());
					logger.debug("Line after removing \" insert - \": " + line);
				} catch (IndexOutOfBoundsException e)
				{
					logger.error(e.getMessage());
					return "";
				}
			}

			// return values separated by comma (if not empty)
			line=line.trim();
			if (line.length()>0)
			{
				int blankposition=line.indexOf(" ");
				while (blankposition>0)
				{
					line=line.substring(0, blankposition) + "," + line.substring(blankposition+1);
					blankposition=line.indexOf(" ");
				}
			}
			else
				return null;
			
			logger.debug(searchstring + line);
			return line;
		}
		else return "";
	}

	/**
	 * 
	 * @return the commandline that has been used for creation of the junction
	 */
	public String[] getCommandLine()
	{
		//convert ArrayLists to string lists
		String[] uuidarraylist = convertToList(uuidarray);
		String[] hostnamelist = convertToList(hostnamearray);
		String[] portlist = convertToList(portarray);
		String[] serverdnlist = convertToList(serverdnarray);
		String[] caseinsensitiveurlslist = convertToList(caseinsensitiveurlsarray);
		logger.debug("allowwindowsstyleurlsarray.size = " + allowwindowsstyleurlsarray.size());
		String[] allowwindowsstyleurlslist = convertToList(allowwindowsstyleurlsarray);		
		
		// get commandlines as strings of pdadmin commands that would be needed to recreate the junction
		JunctionPropertiesCommandLine junctionpropertiescommandline = new JunctionPropertiesCommandLine(
				websealservername, junctionpoint, virtualhostlabel, type, statefuljunction, 
				scriptingsupport, rootdirectory, virtualhostname,
				uuidarraylist, hostnamelist, portlist, serverdnlist, 
				caseinsensitiveurlslist, allowwindowsstyleurlslist);
		return junctionpropertiescommandline.getCommandLine();
	}
	
	private String[] convertToList(ArrayList arraylist)
	{
		String[] tempstringlist = new String[arraylist.size()];
		return (String[]) arraylist.toArray(tempstringlist);
	}

}
