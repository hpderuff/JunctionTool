package com.ger.junctions.acl;

import com.ger.junctions.ServerEnvironment;
import com.ger.junctions.files.Configuration;
import com.ger.junctions.files.PdadminTool;


public class AclPop {

	private final String attachedacl="Attached ACL: ";
	private final String attachedpop="Attached POP: ";

	PdadminTool pdadmin;
	Configuration config;
	String objectspace;
	ServerEnvironment serverenvironment; 
	String websealname;
	String junction;

	
	public AclPop(ServerEnvironment serverenvironmenttemp, String websealnametemp, String junctiontemp)
	{
		config = new Configuration();
		pdadmin = new PdadminTool();
		
		objectspace=config.getObjectSpace();
		serverenvironment=serverenvironmenttemp;
		websealname=websealnametemp;
		junction=junctiontemp;
	}
	        
	 /**
	 *
	 * @return effective acl for that junction
	 *   
	 */
	public String getAttachedAcl()
	{
		String [] acloutput; 

		// get the ACL output for the junction
		acloutput=pdadmin.getObjectShow(serverenvironment, websealname, objectspace, junction);
		for (int i=0; i<acloutput.length; i++)
		{
			// search for the attached ACL
			if (acloutput[i].indexOf(attachedacl)>-1)
				// return the rest of the line (text after attached ACL)
				return acloutput[i].substring(acloutput[i].indexOf(attachedacl)+attachedacl.length());
		}
		
		return "";	
	}
	
	/**
	 * 
	 * @return  effective pop for that junction
	 */
	public String getAttachedPop()
	{
		String [] popoutput;
		
		// get the POP output for the junction
		popoutput=pdadmin.getObjectShow(serverenvironment, websealname, objectspace, junction);
		for (int i=0; i<popoutput.length; i++)
		{
			// search for the attached POP
			if (popoutput[i].indexOf(attachedpop)>-1)
				// return the rest of the line (text after attached ACL)
				return popoutput[i].substring(popoutput[i].indexOf(attachedpop)+attachedpop.length());
		}
		
		return "";
	}
	
}
