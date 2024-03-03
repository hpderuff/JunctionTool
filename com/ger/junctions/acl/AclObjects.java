package com.ger.junctions.acl;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ger.junctions.ServerEnvironment;
import com.ger.junctions.files.Configuration;
import com.ger.junctions.files.PdadminTool;
import com.ger.junctions.junction.ObjectBean;
import com.ger.junctions.acl.AclBean;

public class AclObjects {
	
	Configuration configuration = new Configuration();
	String objectspace = configuration.getObjectSpace();
	Logger logger = Logger.getLogger(AclObjects.class);


	/**
	 * 
	 * @param list with all junctionbeans
	 * @return list with objects that have an ACL attached but are not junctions
	 */
	public AclBean [] checkForNonJunctions(ServerEnvironment serverenv, ArrayList junctionbeansarray)
	{
		String [] acllist;
		PdadminTool pdadmintool;
		AclBean aclbean;
		ArrayList listallobjectswithacl = new ArrayList();
		ArrayList nonjunctionsarray = new ArrayList();
		

		// get all ACL
		pdadmintool = new PdadminTool();
		acllist = pdadmintool.getAclList(serverenv);
		
		for (int i=0; i<acllist.length; i++)
		{			
			// find all objects of this ACL
			String [] acllisttemp = pdadmintool.getAclFind(serverenv, acllist[i]);

			// add all objects to the complete list
			for (int j=0; j<acllisttemp.length; j++)
			{
				aclbean = new AclBean();
				aclbean.setObjectString(acllisttemp[j]);
				// contains the corresponding ACL
				aclbean.setAclName(acllist[i]);
				listallobjectswithacl.add(aclbean);
				logger.debug("ACL list element: " + acllisttemp[j] + ", ACL: " + acllist[i]);
			}
		}
		
		// check for all objects if they are junctions
		logger.debug("array size of acl objects: " + listallobjectswithacl.size());
		for (int i=0; i<listallobjectswithacl.size(); i++)
		{
			// add objects that are not junctions and the corresponding ACL to a list
			AclBean aclbeantemp = (AclBean) listallobjectswithacl.get(i);
			aclbeantemp = isInJunctionList(aclbeantemp, junctionbeansarray);
			if (aclbeantemp.getIsAJunction()==true)
				nonjunctionsarray.add(listallobjectswithacl.get(i));
		}
		
		AclBean[] tempbeanlist = new AclBean[nonjunctionsarray.size()];
		return (AclBean[]) nonjunctionsarray.toArray(tempbeanlist);
	}

	/** 
	 * 
	 * @param aclobject
	 * @param junctionbeansarray
	 * @return true is aclobject is found in the array of junctionbeans
	 */
	private AclBean isInJunctionList(AclBean aclobject, ArrayList junctionbeansarray) 
	{
		String aclhostname = "";
		String aclinstancename;
		int firstslash;
		int lastslash;
		String aclobjectstring;
		ObjectBean junctionbean;
		
		aclobjectstring = aclobject.getObjectString();
		logger.debug("aclobjectstring: " + aclobjectstring);
		
		// objectspace itself is not a junction
		if (aclobjectstring.compareTo(objectspace)==0)
		{
			aclobject.setIsAJunction(false);
			return aclobject;
		}
		
		// first remove object name space + trailing "/"
		if (objectspace.endsWith("/")==false) objectspace=objectspace + "/";
		if (aclobjectstring.indexOf(objectspace)>-1)
			aclobjectstring = aclobjectstring.substring(aclobjectstring.indexOf(objectspace)
					+ objectspace.length());
		else
		{
			logger.debug("Could not find object namespace as defined in the " +
					"configuration file for ACL object " + aclobjectstring);
			aclobject.setIsAJunction(false);
			return aclobject;
		}
		
		// if there is no "/", object is not a junction
		if (aclobjectstring.indexOf("/")<0)
		{
			logger.debug("no slash in " + aclobjectstring);
			aclobject.setIsAJunction(false);
			return aclobject;
		}

		// if there are several "/", object is not a junction
		firstslash = 0;
		lastslash = 0;
		firstslash = aclobjectstring.indexOf("/");
		lastslash = aclobjectstring.lastIndexOf("/");
		if (lastslash>firstslash)
		{
			logger.debug("several slashes in " + aclobjectstring);
			aclobject.setIsAJunction(false);
			return aclobject;
		}
		
		aclinstancename = new String();
		aclinstancename = aclobjectstring.toString();
		
		// remove object name after slash
		aclinstancename = aclinstancename.substring(0, aclinstancename.indexOf("/"));
		// instance name is after the hostname, separated by a "-"
		if (aclinstancename.lastIndexOf("-")>0)
		{
			aclinstancename = aclinstancename.substring(aclinstancename.lastIndexOf("-")+1);
			aclobject.setInstancename(aclinstancename);
		}
		else
		{
			logger.warn("Could not find acl instance name for " + aclinstancename);
			{
				logger.debug("no slash in " + aclobjectstring);
				aclobject.setIsAJunction(false);
				return aclobject;
			}
		}
		
		// hostname is before instance name
		aclhostname = aclobjectstring.substring(0, aclobjectstring.lastIndexOf("-" + aclinstancename));
		if (aclhostname.equals("")==false)
			aclobject.setHostname(aclhostname);
		else 
		{
			aclobject.setIsAJunction(false);
			return aclobject;
		}

		// check for all junctions if there is one identical to the ACL object
		for (int i=0; i<junctionbeansarray.size(); i++)
		{
			junctionbean = (ObjectBean) junctionbeansarray.get(i);
			String websealinstance = junctionbean.getWebSealInstance();
			String websealhostname = junctionbean.getWebSealHost();
			String aclobjectname = aclobjectstring.substring(firstslash);

			// check if ACL hostname and webseal hostname are identical
			// check if ACL instance name and webseal instance name are identical
			// check if the last part of the ACL object name and the webseal junction name 
			// are identical
			if ((websealinstance.compareToIgnoreCase(aclinstancename)==0) 
					&& (websealhostname.compareToIgnoreCase(aclhostname)==0)
					&& junctionbean.getJunctionName().compareToIgnoreCase(aclobjectname)==0)
			{
					aclobject.setIsAJunction(true);
					aclobject.setObjectString(aclobjectname);
					logger.debug(aclobjectstring + " is a junction.");
					return aclobject;
			}
			
		}

		// no matching junction has been found
		logger.debug(aclobjectstring + " is not a junction.");
		aclobject.setIsAJunction(false);
		return aclobject;
	}

}
