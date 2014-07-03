/************************************************************************
 * 日 期：2011-11-25 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 告警查询及告警处理
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.util.StringUtil;

/**
 * @author 余灵
 */
public class AlarmMonitor
{
    private ConnectionInfo nmpConnectionInfo;
    private ISqlExecutor sqlExecutor;

    /**
     * 构造函数
     */
    public AlarmMonitor()
    {
        nmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("nmp_driver"), DbConfig.getProperty("nmp_dburl"), DbConfig.getProperty("nmp_dbuser"), DbConfig
                .getProperty("nmp_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
    }

    /**
     * 获取某对象/对象组的告警列表
     * @param filterId :对象/对象组ID
     * @param filterType :获取类型。"OBJECT"为对象，"OBJGROUP"为对象组
     * @param sTime :起始时间
     * @param eTime :结束时间
     * @param level :告警级别
     * @param type : active，活动告警；history，历史告警。
     * @return 返回该对象/对象组的告警列表的XML
     * @throws Exception 异常
     */
    public String getFilterAlarmList(int filterId, String filterType, long sTime, long eTime, int level, String type) throws Exception
    {
        String result = "";
        String objIds = ""; // 要获取告警的对象

        Set<Integer> objSet = new HashSet<Integer>();
        if ("OBJECT".equals(filterType))
        {
            AlarmStatistic.getInstance().fetchObjAllChilds(filterId, objSet);
            objSet.add(filterId); // 加入对象本身
        }
        else if ("OBJGROUP".equals(filterType))
        {
            AlarmStatistic.getInstance().fetchGrpAllChilds(filterId, objSet);
        }

        // 将对象ID用逗号连接
        if (objSet != null && objSet.size() > 0)
        {
            objIds = StringUtils.join(objSet.toArray(), ",");
        }

        // 查询该对象及其所有子对象的最近100条未处理告警
        if (!StringUtil.isNullOrEmpty(objIds))
        {
            if (!StringUtil.isNullOrEmpty(type))
            {
                String table = "BMP_ALARMEVENT";
                if ("history".equals(type))
                {
                    table = "BMP_ALARMEVENTLOG";
                }

                String cmd =
                    "SELECT A.*,B.OBJ_NAME,B.CLASS_GROUP," + "(SELECT ATTRIB_NAME FROM BMP_ATTRIBUTE WHERE ATTRIB_ID=A.ATTRIB_ID) AS ATTRIB_NAME,"
                        + "(SELECT OBJATTR_NAME FROM BMP_OBJATTRIB WHERE OBJATTR_ID=A.OBJATTR_ID) AS OBJATTR_NAME" + " FROM " + table
                        + " A LEFT JOIN BMP_OBJECT B ON A.OBJ_ID=B.OBJ_ID" + " WHERE A.OBJ_ID IN (" + objIds + ") AND ALARM_LEVEL>=" + level
                        + " AND COLL_TIME >= " + sTime + " AND COLL_TIME<=" + eTime + " ORDER BY A.ALARMEVT_ID DESC";

                result = sqlExecutor.fill(cmd).asXML();
            }
        }

        return result;
    }

    /**
     * 获取某对象/对象组的告警条数
     * @param filterId :对象/对象组ID
     * @param filterType :获取类型。"OBJECT"为对象，"OBJGROUP"为对象组
     * @param sTime :起始时间
     * @param eTime :结束时间
     * @param level :告警级别
     * @param type : active，活动告警；history，历史告警。
     * @return 返回该对象/对象组根据属性分组的告警条数
     * @throws Exception 异常
     */
    public String getFilterAlarmCount(int filterId, String filterType, long sTime, long eTime, int level, String type) throws Exception
    {
        String result = "";
        String objIds = ""; // 要获取告警的对象

        Set<Integer> objSet = new HashSet<Integer>();
        if ("OBJECT".equals(filterType))
        {
            AlarmStatistic.getInstance().fetchObjAllChilds(filterId, objSet);
            objSet.add(filterId); // 加入对象本身
        }
        else if ("OBJGROUP".equals(filterType))
        {
            AlarmStatistic.getInstance().fetchGrpAllChilds(filterId, objSet);
        }

        // 将对象ID用逗号连接
        if (objSet != null && objSet.size() > 0)
        {
            objIds = StringUtils.join(objSet.toArray(), ",");
        }

        // 查询该对象及子对象根据属性分组的未处理告警条数
        if (!StringUtil.isNullOrEmpty(objIds))
        {
            if (!StringUtil.isNullOrEmpty(type))
            {
                String table = "BMP_ALARMEVENT";
                if ("history".equals(type))
                {
                    table = "BMP_ALARMEVENTLOG";
                }
                String cmd =
                    "SELECT COUNT(e.ALARMEVT_ID) AS ALARM_COUNT,e.ATTRIB_ID,a.ATTRIB_NAME " + "FROM " + table
                        + " e LEFT JOIN BMP_ATTRIBUTE a ON e.ATTRIB_ID=a.ATTRIB_ID " + "WHERE e.OBJ_ID IN (" + objIds + ") AND ALARM_LEVEL>=" + level
                        + " AND COLL_TIME >= " + sTime + " AND COLL_TIME<=" + eTime + " GROUP BY e.ATTRIB_ID,a.ATTRIB_NAME";

                result = sqlExecutor.fill(cmd).asXML();
            }
        }

        return result;
    }

}
