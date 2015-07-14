/************************************************************************
日 期：2013-09-29
作 者: 刘帅
版 本：v1.3
描 述: 
历 史： 
 ************************************************************************/
package jetsennet.jbmp.business;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AlarmConfigTemplateEntity;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlHelper;
import jetsennet.util.SerializerUtil;

import org.apache.log4j.Logger;

/**
 * 报警配置模板 business 
 * @author liushuai
 */
public class AlarmConfigTemplate
{
    private static final Logger logger = Logger.getLogger(AlarmConfigTemplate.class);

    /**
     * 新增记录。
     * 因为TEMPLATE_INFO字段为CLOB类型，DefaultDal是采用拼接生成sql语句的，
     * 此时sql语句的长度可能会超过4000（oracle中sql语句的长度最多支持4000）。
     * @throws Exception 
     */
    @Business
    public String addAlarmConfigTemplate(String objXml) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int newId;

        try
        {
            HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
            String getNewId = "SELECT MAX(TEMPLATE_ID) FROM BMP_ALARMCONFIGTEMPLATE";
            String insertSql =
                "INSERT INTO BMP_ALARMCONFIGTEMPLATE (TEMPLATE_ID,TEMPLATE_NAME,TEMPLATE_TYPE,TEMPLATE_INFO,CREATE_TIME) Values (?,?,?,?,?)";
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            ConnectionInfo info = exec.getConnectionInfo();
            conn = SqlHelper.getConnection(info);
            //获取插入新记录的id
            pstmt = conn.prepareStatement(getNewId);
            ResultSet rs = pstmt.executeQuery();
            if ((null != rs) && rs.next())
                newId = rs.getInt("MAX(TEMPLATE_ID)") + 1;
            else
                newId = 1;
            //插入记录
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, newId);
            pstmt.setString(2, map.get("TEMPLATE_NAME"));
            pstmt.setInt(3, Integer.parseInt(map.get("TEMPLATE_TYPE")));
            pstmt.setCharacterStream(4, new StringReader(map.get("TEMPLATE_INFO")), map.get("TEMPLATE_INFO").length());
            pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
            pstmt.executeUpdate();
        }
        catch (Exception e)
        {
            logger.error("新增报警配置模板(BMP_ALARMCONFIGTEMPLATE)失败！");
            throw e;
        }
        finally
        {
            if (null != pstmt)
                pstmt.close();
            if (null != conn)
                conn.close();
        }
        return "" + newId;
    }

    /**
     * 删除 
     */
    @Business
    public void deleteAlarmConfigTemplate(int templateId) throws Exception
    {
        DefaultDal<AlarmConfigTemplateEntity> dal = new DefaultDal<AlarmConfigTemplateEntity>(AlarmConfigTemplateEntity.class);
        dal.delete(templateId);
    }
}
