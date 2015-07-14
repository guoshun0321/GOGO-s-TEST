package jetsennet.jbmp.business;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author
 */
public class AlarmEvent
{

    private static final Logger logger = Logger.getLogger(AlarmEvent.class);

    /**
     * 新增
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public String addAlarmEvent(String objXml) throws Exception
    {
        AlarmEventDal dal = new AlarmEventDal();
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAlarmEvent(String objXml) throws Exception
    {
        AlarmEventDal dal = new AlarmEventDal();
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarmEvent(int keyId) throws Exception
    {
        AlarmEventDal dal = new AlarmEventDal();
        dal.delete(keyId);
    }

    /**
     * 更新，审核操作
     * @param keyId id
     * @param checkState 审核状态
     * @throws Exception 异常
     */
    @Business
    public void verifyAlarmEvent(int keyId, int checkState) throws Exception
    {
        AlarmEventEntity entity = DefaultDal.get(AlarmEventEntity.class, keyId);
        entity.setEventState(checkState);
        AlarmEventDal dal = new AlarmEventDal();
        dal.update(entity, new SqlCondition("ALARMEVT_ID", String.valueOf(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
    }

    /**
     * 获取首页报警数据
     * 
     * @param num
     * @param isAdmin
     * @param objIds
     * @return
     */
    @Business
    public String indexAlarmEvent(int num, boolean isAdmin, String objIds)
    {
        Document doc = null;
        try
        {
            String sql = null;
            if (isAdmin)
            {
                sql =
                    "SELECT * FROM (SELECT ALARMEVT_ID,a.COLL_TIME,EVENT_DESC,OBJATTR_NAME,OBJ_NAME FROM BMP_ALARMEVENT a INNER JOIN BMP_OBJATTRIB b ON a.OBJATTR_ID = b.OBJATTR_ID INNER JOIN BMP_OBJECT c ON a.OBJ_ID = c.OBJ_ID ORDER BY ALARMEVT_ID DESC) WHERE ROWNUM <= "
                        + num;
            }
            else
            {
                sql =
                    "SELECT * FROM (SELECT ALARMEVT_ID,a.COLL_TIME,EVENT_DESC,OBJATTR_NAME,OBJ_NAME FROM BMP_ALARMEVENT a INNER JOIN BMP_OBJATTRIB b ON a.OBJATTR_ID = b.OBJATTR_ID INNER JOIN BMP_OBJECT c ON a.OBJ_ID = c.OBJ_ID WHERE a.OBJ_ID IN (%s) ORDER BY ALARMEVT_ID DESC) WHERE ROWNUM <= %s";
                if (objIds != null && !objIds.isEmpty())
                {
                    sql = String.format(sql, objIds, num);
                }
                else
                {
                    sql = null;
                }
            }
            doc = new Document();
            final Element rootEle = new Element("RocordSet");
            doc.setRootElement(rootEle);

            if (sql != null)
            {
                // sql合法时返回的数据
                final int[] maxAlarm = { -1 };
                DefaultDal.read(sql, new IReadHandle()
                {
                    @Override
                    public void handle(ResultSet rs) throws Exception
                    {
                        while (rs.next())
                        {
                            Element recordEle = new Element("Record");
                            int evtId = rs.getInt("ALARMEVT_ID");
                            String desc = rs.getString("EVENT_DESC");
                            String name = rs.getString("OBJATTR_NAME");
                            String objName = rs.getString("OBJ_NAME");
                            long collTime = rs.getLong("COLL_TIME");
                            recordEle.addContent(new Element("ALARMEVT_ID").setText(Integer.toString(evtId)));
                            recordEle.addContent(new Element("EVENT_DESC").setText(desc));
                            recordEle.addContent(new Element("OBJATTR_NAME").setText(name));
                            recordEle.addContent(new Element("OBJ_NAME").setText(objName));
                            recordEle.addContent(new Element("COLL_TIME").setText(formatDate(collTime)));
                            rootEle.addContent(recordEle);
                            if (evtId > maxAlarm[0])
                            {
                                maxAlarm[0] = evtId;
                            }
                        }
                    }
                });

                Element recordEle = new Element("Record1");
                recordEle.addContent(new Element("MAX_ALARM").setText(Integer.toString(maxAlarm[0])));
                rootEle.addContent(recordEle);
            }
            else
            {
                // sql不合法时返回的数据
                Element recordEle = new Element("Record1");
                recordEle.addContent(new Element("MAX_ALARM").setText("-1"));
                rootEle.addContent(recordEle);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        XMLOutputter out = new XMLOutputter();
        return out.outputString(doc);
    }

    private String formatDate(long time)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }
}
