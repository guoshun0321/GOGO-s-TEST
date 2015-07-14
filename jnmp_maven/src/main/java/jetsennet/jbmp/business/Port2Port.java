/**********************************************************************
 * 日 期: 2012-07-06
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Assetsrecord.java
 * 历 史: 2012-07-06 Create
 *********************************************************************/
package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.Port2PortDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * 端口连接
 * @author xuyuji
 *
 */
public class Port2Port
{
    /**
     * 新建
     * @param objXml
     */
    @Business
    public String addP2p(String objXml) throws Exception
    {
    	Port2PortDal p2p = new Port2PortDal();
        return "" + p2p.insertXml(objXml);
    }

    /**
     * 删除
     * @param key
     * @throws Exception
     */
    @Business
    public void deleteP2p(int keyId) throws Exception
    {
    	Port2PortDal p2p = new Port2PortDal();
    	p2p.delete(keyId);
    }
    
    /**
     * 删除
     * @param key
     * @throws Exception
     */
    @Business
    public void deleteP2ps(String keyIds) throws Exception
    {
    	Port2PortDal p2p = new Port2PortDal();
    	SqlCondition cond = new SqlCondition("ID", keyIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
    	p2p.delete(cond);
    }
    
    /**
     * 判断portAID和portBID这连个端口是否连接
     * @param portAID
     * @param portBID
     * @param userAuth
     * @return
     */
    public int p2pIsExists(String portAID, String portBID)
    {
    	Port2PortDal p2p = new Port2PortDal();
    	return p2p.p2pIsExists(portAID, portBID);
    }
}
