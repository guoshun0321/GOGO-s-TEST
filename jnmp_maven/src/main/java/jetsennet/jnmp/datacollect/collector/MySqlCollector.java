/************************************************************************
日 期：2012-12-27
作 者: 郭祥
版 本：v1.3
描 述: 报警配置
历 史：
 ************************************************************************/
package jetsennet.jnmp.datacollect.collector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jnmp.entity.SqlServerAttribEntity;

/**
 * MySQL采集器
 * @author 郭祥
 */
public class MySqlCollector extends AbsDBCollector
{

    private static final Logger logger = Logger.getLogger(MySqlCollector.class);
    private static final String CONN_TIME_STR = "conn_time";
    private static Map<String, SqlServerAttribEntity> map = new ConcurrentHashMap<String, SqlServerAttribEntity>();

    /*
     * (non-Javadoc)
     * @see jetsennet.jnmp.datacollect.coll.AbsDBCollector#getDriverClassName()
     */
    protected String getDriverClassName()
    {
        return "com.mysql.jdbc.Driver";
    }

    /*
     * (non-Javadoc)
     * @see jetsennet.jnmp.datacollect.coll.AbsDBCollector#getConnectionURL()
     */
    protected String getConnectionURL()
    {
        return "jdbc:mysql://" + mo.getIpAddr() + ":" + mo.getIpPort() + "/" + this.ensureDbName(mo.getField1());
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        reset();
        Map<ObjAttribEntity, Object> retval = new HashMap<ObjAttribEntity, Object>();

        if (objAttrLst != null && !objAttrLst.isEmpty())
        {
            Map<String, String> statusMap = this.getStatusData();
            rrdValues = new HashMap<String, Double>(statusMap.size());

            for (ObjAttribEntity oa : objAttrLst)
            {
                String key = oa.getAttribParam();
                if (oa.getAttribId() == BMPConstants.ON_OFF_ATTRIB_ID)
                {
                    continue;
                }
                else if (CONN_TIME_STR.equals(key))
                {
                    rrdValues.put(Integer.toString(oa.getObjAttrId()), Double.valueOf(connTime));
                    CollData data = createCollData(mo, oa, connTime, CollData.DATATYPE_PERF);
                    retval.put(oa, data);
                }
                else
                {
                    String strValue = statusMap.get(key);
                    Double value = null;
                    try
                    {
                        value = Double.parseDouble(strValue);
                    }
                    catch (Exception ex)
                    {
                        logger.error(String.format("数据采集：对象属性<%s>，名称<%s>，参数<%s>，取值<%s>不正确", oa.getObjAttrId(), oa.getObjattrName(), oa
                            .getAttribValue(), strValue));
                        value = Double.NaN;
                    }
                    rrdValues.put(Integer.toString(oa.getObjAttrId()), value);
                    value = value.isNaN() ? null : value;
                    CollData data = createCollData(mo, oa, value, CollData.DATATYPE_PERF);
                    data.put(CollData.PARAMS_DATA, rrdValues);
                    retval.put(oa, data);
                }
            }
        }
        return retval;
    }

    /**
     * 表格数据
     */
    private Map<String, String> getStatusData()
    {
        Map<String, String> retval = new HashMap<String, String>();
        ResultSet rs = null;
        try
        {
            rs = stm.executeQuery("SHOW GLOBAL STATUS;");
            while (rs.next())
            {
                String key = rs.getString(1);
                String value = rs.getString(2);
                retval.put(key, value);
            }
            rs.close();
        }
        catch (SQLException ex)
        {
            logger.error("", ex);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        return retval;
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 参数
     */
    public static void main(String[] args) throws Exception
    {
        MObjectEntity mo = new MObjectEntity();
        mo.setIpAddr("192.168.8.41");
        mo.setIpPort(3306);
        mo.setUserName("root");
        mo.setUserPwd("jetsen");
        mo.setField1("");
        ObjAttribDal oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        List<ObjAttribEntity> oas = oadal.getByID(1);
        MySqlCollector ssc = new MySqlCollector();
        ssc.setMonitorObject(mo);
        ssc.connect();
        ssc.collect(oas, null);
        ssc.collect(null, null);
        ssc.close();
    }
}
