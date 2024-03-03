package com.ger.junctions.acl;

public class AclBean {
	
		private String objectstring = "";
		private String aclstring = "";
		private boolean isajunction = false;
		private String instancestring = "";
		private String hoststring = "";
		
		public void setObjectString(String aclobject)
		{
			objectstring=aclobject;
		}
		
		public String getObjectString()
		{
			return objectstring;
		}
		
		public void setAclName(String aclname)
		{
			aclstring=aclname;
		}
		
		public String getAclName()
		{
			return aclstring;
		}

		public void setIsAJunction(boolean isjunction)
		{
			isajunction=isjunction;
		}
		
		public boolean getIsAJunction()
		{
			return isajunction;
		}

		public void setInstancename(String instancename)
		{
			instancestring=instancename;
		}
		
		public String getInstancename()
		{
			return instancestring;
		}
		
		public void setHostname(String hostname)
		{
			hoststring=hostname;
		}
		
		public String getHostname()
		{
			return hoststring;
		}

}
