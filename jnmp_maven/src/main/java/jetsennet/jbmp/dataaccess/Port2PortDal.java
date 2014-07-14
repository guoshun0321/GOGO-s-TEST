/**********************************************************************
 * 日 期: 2012-07-06
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: AssetsrecordDal.java
 * 历 史: 2012-07-06 Create
 *********************************************************************/
package jetsennet.jbmp.dataaccess;

import java.sql.SQLException;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.Port2PortEntiry;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

import org.apache.log4j.Logger;
import org.dom4j.Document;

/**
 * 端口连接
 * @author xuyuji
 */
public class Port2PortDal extends DefaultDal<Port2PortEntiry>
{
    private static final Logger logger = Logger.getLogger(Port2PortDal.class);

    public Port2PortDal()
    {
        super(Port2PortEntiry.class);
    }

    /**
     * 更新连接关系
     * @param groupId
     * @param rels
     */
    @Transactional
    public void updateLink(int groupId, List<Port2PortEntiry> rels)
    {
        try
        {
            SqlCondition[] delConds =
                new SqlCondition[] {
                    new SqlCondition("GROUP_ID", Integer.toString(groupId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("REL_TYPE", Integer.toString(Port2PortEntiry.REL_TYPE_AUTO), SqlLogicType.And, SqlRelationType.Equal,
                        SqlParamType.Numeric) };
            this.delete(delConds);
            for (Port2PortEntiry rel : rels)
            {
                this.insert(rel);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }
    
    public String getP2pData(String devAID, String devBID){
    	ISqlExecutor execNmp = SqlExecutorFacotry.getSqlExecutor();
		//端口条件不要忘记了。
		String strSql = "select a.ID, b.OBJ_NAME as PORTA_NAME, b.IP_ADDR as PORTA_IP, c.OBJ_NAME PORTA_PARENT, " + 
						"	d.OBJ_NAME as PORTB_NAME, d.IP_ADDR as PORTB_IP, e.OBJ_NAME as PORTB_PARENT from BMP_PORT2PORT a " +  
						" inner join BMP_OBJECT b on a.PORTA_ID = b.OBJ_ID " + 
						" inner join BMP_OBJECT c on b.PARENT_ID = c.OBJ_ID " + 
						" inner join BMP_OBJECT d on a.PORTB_ID = d.OBJ_ID " + 
						" inner join BMP_OBJECT e on d.PARENT_ID = e.OBJ_ID " + 
						" where c.OBJ_ID = " + devAID + " and e.OBJ_ID = " + devBID +
						" union " + 
						" select a.ID, d.OBJ_NAME as PORTA_NAME, d.IP_ADDR as PORTA_IP, e.OBJ_NAME as PORTA_PARENT , " + 
						"	b.OBJ_NAME as PORTB_NAME, b.IP_ADDR as PORTB_IP, c.OBJ_NAME PORTB_PARENT from BMP_PORT2PORT a " +  
						" inner join BMP_OBJECT b on a.PORTA_ID = b.OBJ_ID " + 
						" inner join BMP_OBJECT c on b.PARENT_ID = c.OBJ_ID " + 
						" inner join BMP_OBJECT d on a.PORTB_ID = d.OBJ_ID " + 
						" inner join BMP_OBJECT e on d.PARENT_ID = e.OBJ_ID " + 
						" where c.OBJ_ID = " + devBID + " and e.OBJ_ID = " + devAID;
		Document ds = null;
		try {
			logger.info(strSql);
			ds = execNmp.fill(strSql);
		} catch (SQLException e) {
			logger.error("", e);
		}
		return ds.asXML().replaceAll("DataSource", "RecordSet").replaceAll("DataTable", "Record");
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
    	ISqlExecutor execNmp = SqlExecutorFacotry.getSqlExecutor();
        // 端口条件不要忘记了。
        String strSql = "select * from BMP_PORT2PORT where (PORTA_ID = %s and PORTB_ID = %s) or (PORTB_ID = %s and PORTA_ID = %s)";
        Document ds = null;
        try
        {
        	logger.info(strSql);
            ds = execNmp.fill(String.format(strSql, portAID, portBID, portAID, portBID));
        }
        catch (Exception ex)
        {
        	logger.error(ex);
        }
        return (ds.asXML().indexOf("DataTable") == -1 ? 0 : 1);
    }
}
