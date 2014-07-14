package jetsennet.jnmp.business;

import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jnmp.dataaccess.RFTopo2RoleDal;
import jetsennet.jnmp.entity.RFTopo2RoleEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

public class RFTopo2Role {

    /**
     * 新建
     * @param objXml
     */
    @Business
    public String addRFTopo2Role(String roleId, String mapIds) throws Exception
    {
    	delRFTopo2Role(roleId);
    	
    	String newIds = "";
    	if(mapIds != null && !"".equals(mapIds)){
    		RFTopo2RoleDal dalRFTopo2Role = new RFTopo2RoleDal();
    		for(String mapId : mapIds.split(","))
    		{
    			RFTopo2RoleEntity e = new RFTopo2RoleEntity();
    			e.setMapId(Integer.parseInt(mapId));
    			e.setRoleId(Integer.parseInt(roleId));
    			
    			newIds += dalRFTopo2Role.insert(e) + ",";
    		}
    		if(newIds.length() > 0){
    			newIds = newIds.substring(0, newIds.length() - 1);
    		}
    	}
        return newIds;
    }
    
    /**
     * 删除
     * @param key
     * @throws Exception
     */
    @Business
    public void delRFTopo2Role(String roleId) throws Exception
    {
    	RFTopo2RoleDal dalRFTopo2Role = new RFTopo2RoleDal();
    	dalRFTopo2Role.delete(new SqlCondition( "ROLE_ID" , roleId, SqlLogicType.And , SqlRelationType.Equal, SqlParamType.Numeric , true));
    }
}
