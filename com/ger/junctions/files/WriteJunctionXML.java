package com.ger.junctions.files;

import java.util.ArrayList;
import java.util.Calendar;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ger.junctions.acl.AclBean;
import com.ger.junctions.junction.ObjectBean;


public class WriteJunctionXML
{
	Logger logger = Logger.getLogger(WriteJunctionXML.class);
	
	/**
	 * 
	 * @param junctionarray that contains all infos about a junction
	 * @param aclonlybeans that contain all objects that have an ACL attached but are not a junction
	 * @param xmlfilename that will be created for all junctions
	 * @param aclonlyfile that 
	 * @return if file creation was successful
	 */
	public boolean writeXml(ArrayList junctionbeanarray, AclBean [] aclonlybeans, String xmlfilename, String aclonlyname)
	{
			boolean success = true;
			String [] commandlines;
			String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
			Document doc;
			Element root;
			File file;
			FileOutputStream fos = null;
			String currentwebsealname = "";
			Element websealelement = new Element("init");
			
			// set the header of the XML tree
			Calendar cal = Calendar.getInstance();
		    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
			root = new Element("Junctionlist");
			root.setAttribute("Date", sdf.format(cal.getTime()));

			if (junctionbeanarray==null||xmlfilename==null||xmlfilename.compareTo("")==0) return false;
		     
			// go through all junctions
			for (int i=0; i<=junctionbeanarray.size()-1; i++)
			{
				// get a single junction
				ObjectBean junction=(ObjectBean)junctionbeanarray.get(i);
				String websealhost=junction.getWebsealName();

				// check if this junction is now from a different webseal
				// and set websealname as element
				if (!(websealhost.equals(currentwebsealname)))
				{
					currentwebsealname=websealhost;
					websealelement = new Element("WebSEAL");
					websealelement.setText(currentwebsealname);
					root.addContent(websealelement);
					logger.debug("currentwebsealname: " + currentwebsealname);
				}

				// check if junction or virtualhost
				String junctionname = junction.getJunctionName();
				if (junctionname==null || junctionname.equals(""))
				{			
					// TODO: check for virtual hosts
					//junctionname = get Virtual Host Junction label
					junctionname = "virtual host";
				}	
				
				// add attributes from the junction to the XML tree
				Element junctionelement = new Element("Junction");
				websealelement.addContent(junctionelement);
				
				String[] attributlist=junction.getAttributeArray();
				for (int j=0; j<=attributlist.length-1; j++)
				{
					String attribut=attributlist[j];
					
					// separate key and value (separated with ": ")
					int index = attribut.indexOf(": ");
					String tempkey = attribut.substring(0, index);
					logger.debug("tempkey=" + tempkey);
			
					// value is after the ": "
					String tempvalue = attribut.substring(index+2);
					logger.debug("tempvalue=" + tempvalue);
					
					// remove blanks within the key if any
					logger.debug("tempkey.indexOf(\" \")=" + tempkey.indexOf(" "));
					while (tempkey.indexOf(" ") >= 0)
					{
						index=tempkey.indexOf(" ");
						tempkey=tempkey.subSequence(0, index)+tempkey.substring(index+1, tempkey.length());
					}

					// set key and value to XML elements 
					// set only non-empty values
					if (tempvalue.trim().equals("")==false)
					{
						Element attributelement = new Element(tempkey);
						attributelement.setText(tempvalue);
						junctionelement.addContent(attributelement);					
					}
				}
								
				// add command lines to XML tree
				commandlines = junction.getCommandLine();
				logger.debug("Number of command lines=" + commandlines.length);
				for (int k=0; k<commandlines.length; k++)
				{
					Element commandlineelement = new Element("commandline");
					commandlineelement.addContent(commandlines[k]);
					junctionelement.addContent(commandlineelement);
				}
				
				// add attached ACL if not empty
				if (junction.getAttachedAcl().trim().equals("")==false)
				{
					Element attachedaclelement = new Element("AttachedACL");
					attachedaclelement.addContent(junction.getAttachedAcl());
					junctionelement.addContent(attachedaclelement);
				}
				
				// add attached POP if not empty
				if (junction.getAttachedPop().trim().equals("")==false)
				{
					Element attachedpopelement = new Element("AttachedPOP");
					attachedpopelement.addContent(junction.getAttachedPop());
					junctionelement.addContent(attachedpopelement);
				}
				
				// add objects with an attached ACL to the corresponding junction
				// means that object is starting with the junction/virtual host name
				for (int j=0; j<=aclonlybeans.length-1; j++)
				{
					logger.debug("aclonlybean.getHostname: " + aclonlybeans[j].getHostname());
					logger.debug("junction.getWebSealHost: " + junction.getWebSealHost());					
					logger.debug("aclonlybean.getInstancename: " + aclonlybeans[j].getInstancename());
					logger.debug("junction.getWebSealInstance: " + junction.getWebSealInstance());
					logger.debug("aclonlybean.getAclName: " + aclonlybeans[j].getAclName());
					logger.debug("junction.getJunctionName: " + junction.getJunctionName());
					if (aclonlybeans[j].getHostname().compareTo(junction.getWebSealHost())==0 && 
							aclonlybeans[j].getInstancename().compareTo(junction.getWebSealInstance())==0
							&& aclonlybeans[j].getAclName().compareTo(junction.getJunctionName())==0)
					{
						Element attributelement = new Element("Object");
						attributelement.addContent(aclonlybeans[j].getObjectString());
						Element aclelement = new Element("ACL");
						aclelement.addContent(aclonlybeans[j].getAclName());
						attributelement.addContent(aclelement);
						junctionelement.addContent(attributelement);
						logger.debug("ACL only: " + aclonlybeans[j].getHostname()+
								aclonlybeans[j].getInstancename()+aclonlybeans[j].getAclName());
					}				
				}
			}
			
			doc = new Document(root);
			XMLOutputter fmt = new XMLOutputter();
			// only for nicer formatting
	        fmt.setFormat( Format.getPrettyFormat() );  
 			file = new File(xmlfilename);
 			try {
		    	  fos = new FileOutputStream(file);
		    	  fmt.output(doc, fos);
		    } catch (Exception ex) 
		    {
		    	logger.error("Could not write XML file " + xmlfilename);
		    	logger.error(ex.getMessage());
		    	success=false;
		    }
		    
		    try 
	    	{ 
	    		fos.close(); 
	    	} catch (Exception exception)
	    	{	
	    		logger.error("Could not close XML file " + xmlfilename);
	    		logger.error(exception.getMessage());
	    		success=false;
	    	} 
	    	
	    	
// // ACL 
	    	// ACL only elements
	    	// add elements to XML file that are not junctions
	    	
			// set the header of the XML tree
			root = new Element("ACLelements");
			root.setAttribute("Date", sdf.format(cal.getTime()));

			Element junctionelement = new Element("ACLelements");
			root.addContent(junctionelement);
			
			// add values to the XML tree
			for (int j=0; j<=aclonlybeans.length-1; j++)
			{
				Element attributelement = new Element("Object");
				attributelement.addContent(aclonlybeans[j].getObjectString());
				Element aclelement = new Element("ACL");
				aclelement.addContent(aclonlybeans[j].getAclName());
				attributelement.addContent(aclelement);
				junctionelement.addContent(attributelement);
			}
	
			doc = new Document(root);
 			file = new File(aclonlyname);
 			try {
		    	  fos = new FileOutputStream(file);
		    	  fmt.output(doc, fos);
		    } catch (Exception ex) 
		    {
		    	logger.error("Could not write XML file " + aclonlyname);
		    	logger.error(ex.getMessage());
		    	success=false;
		    }

		    try 
	    	{ 
	    		fos.close(); 
	    	} catch (Exception exception)
	    	{	
	    		logger.error("Could not close XML file " + aclonlyname);
	    		logger.error(exception.getMessage());
	    		success=false;
	    	}
		
		return success;
	}
	
}
