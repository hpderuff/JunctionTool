package com.ger.junctions;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ger.junctions.acl.AclBean;
import com.ger.junctions.acl.AclObjects;
import com.ger.junctions.acl.AclPop;
import com.ger.junctions.files.Configuration;
import com.ger.junctions.files.PdadminTool;
import com.ger.junctions.files.WriteJunctionXML;
import com.ger.junctions.junction.ObjectBean;
import com.ger.junctions.junction.JunctionProperties;

public class PrepareXML {
	
	Logger logger = Logger.getLogger(PrepareXML.class);
	
	public PrepareXML(ServerEnvironment serverenv, String [] serverlist)
	{
		Configuration config = new Configuration();
		PdadminTool pdadmintool = new PdadminTool();
		AclBean [] aclonlyobjects;
		// contains all junctionsbeans of all webseal servers
		ArrayList alljunctionbeans=new ArrayList();

		// go through all webseal servers
		for (int i=0; i<serverlist.length; i++)
		{
			logger.debug("WebSEAL servers: " + serverlist[i] + " " + i);
			// a list of junction per server
			String [] junctionnames = pdadmintool.getServerTaskList(serverenv, serverlist[i]);
			if (junctionnames == null)
			{
					logger.error("Could not get junction list from pdadmin for " + serverlist[i]);
					continue;
			}
			// go through all junctions of this webseal server 
			// and recreate the corresponding command lines
			for (int j=0; j<junctionnames.length; j++)
			{
				logger.debug("Junction list: " + serverlist[i] + junctionnames[j]);
				// get all properties of a single junction
				String [] junctionpropsarray = pdadmintool.getServerTaskShow(serverenv, 
						serverlist[i], junctionnames[j]);
				if (junctionpropsarray == null)
				{
						logger.error("Could not get junction properties from pdadmin for " + serverlist[i] + junctionnames[j]);
						continue;
				}
				
				// add command lines, acl and pop for this junction to the global list
				alljunctionbeans.add(getObjectBean(serverenv, serverlist[i],
						junctionpropsarray, junctionnames[j]));
			}
			
			// go through all virtual hosts of this webseal server 
			// and recreate the corresponding command lines

			String [] virtualhosts = pdadmintool.getServerTaskVirtualhostList(serverenv, serverlist[i]);
			if (virtualhosts == null)
					logger.error("Could not get virtual hosts from pdadmin for " + serverlist[i]);

			for (int j=0; j<virtualhosts.length; j++)
			{
				logger.debug("Virtual hosts list: " + serverlist[i] + virtualhosts[j]);
				// all properties of one virtual host 
				String [] virtualhostsarray = pdadmintool.getServerTaskVirtualhostShow(serverenv, 
						serverlist[i], virtualhosts[j]);
				if (virtualhostsarray == null || virtualhostsarray[0].length()==0)
						logger.info("Could not get virtual host properties from pdadmin for " + serverlist[i] + virtualhosts[j]);
				else
				// add command lines, acl and pop for this virtual host to the global list
					alljunctionbeans.add(getObjectBean(serverenv, serverlist[i],
						virtualhostsarray, virtualhosts[j]));

			}	

		}
		
		
		// search for objects that are not junctions
		AclObjects acldiff = new AclObjects();
		aclonlyobjects = acldiff.checkForNonJunctions(serverenv, alljunctionbeans);
		
		for (int i=0; i<aclonlyobjects.length; i++)
		{
			logger.debug("ACL only element: " + aclonlyobjects[i].getObjectString()
					+ ", ACL name: " + aclonlyobjects[i].getAclName());
		}

		
		// creates an XML file containing the properties and commandlines
		WriteJunctionXML createxml = new WriteJunctionXML();
		String xmlfilename = config.getXmlOutputFile();
		String aclonlyname = config.getAclOnlyFile();
		if (createxml.writeXml(alljunctionbeans, aclonlyobjects, xmlfilename, aclonlyname)==false)
				{
					logger.error("Could not write XML file " + xmlfilename);
				}

	}
	
	private ObjectBean getObjectBean(ServerEnvironment serverenv, String servername, 
			String [] junctionpropsarray, String junctionname)
	{
		// get properties of this junction
		JunctionProperties junctionprops = new JunctionProperties(servername, junctionpropsarray);
		// save all properties in a bean
		ObjectBean junctionbean = new ObjectBean();
		junctionbean.setJunctionName(junctionname);
		junctionbean.setWebsealName(servername);
		junctionbean.setCommandLine(junctionprops.getCommandLine());
		junctionbean.setAttributeArray(junctionprops.getJunctionAttributes());
		// set attached ACL and POP
		AclPop aclpop = new AclPop(serverenv, servername, junctionname);
		junctionbean.setAttachedAcl(aclpop.getAttachedAcl());
		junctionbean.setAttachedPop(aclpop.getAttachedPop());
		
		return junctionbean;
	}


}
