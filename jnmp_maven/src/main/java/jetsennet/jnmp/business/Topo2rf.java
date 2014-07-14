/**********************************************************************
 * 日 期: 2013-07-01
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Topo2rf.java
 * 历 史: 2013-07-01 Create
 *********************************************************************/
package jetsennet.jnmp.business;

import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jnmp.dataaccess.Topo2rfDal;
import jetsennet.jnmp.entity.Topo2rfEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 *  Bussiness
 */
public class Topo2rf
{
    /**
     * 新建
     * @param objXml
     */
    @Business
    public String addTopo2rf(String mapIds, String rfId, String rfType) throws Exception
    {
    	deleteTopo2rf(rfId);
    	
        Topo2rfDal dalTopo2rf = new Topo2rfDal();
        String newIds = "";
        for(String mapId : mapIds.split(","))
        {
        	Topo2rfEntity e = new Topo2rfEntity();
        	e.setMapId(Integer.parseInt(mapId));
        	e.setRfId(Integer.parseInt(rfId));
        	e.setRfType(Integer.parseInt(rfType));
        	
        	newIds += dalTopo2rf.insert(e) + ",";
        }
        if(newIds.length() > 0){
        	newIds = newIds.substring(0, newIds.length() - 1);
        }
        return newIds;
    }
    
    /**
     * 删除
     * @param key
     * @throws Exception
     */
    @Business
    public void deleteTopo2rf(String rfId) throws Exception
    {
        Topo2rfDal dalTopo2rf = new Topo2rfDal();
        dalTopo2rf.delete(new SqlCondition( "RF_ID" , rfId, SqlLogicType.And , SqlRelationType.Equal, SqlParamType.Numeric , true));
    }

}
