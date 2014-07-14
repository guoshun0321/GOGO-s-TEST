package jetsennet.jbmp.servlets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jetsennet.jbmp.business.AlarmStatistic;
import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class BMPInsertAlarmTestListener implements ServletContextListener
{
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        String flag = sce.getServletContext().getInitParameter("insertAlarmTestFlag");
        String interval = sce.getServletContext().getInitParameter("insertAlarmTestInterval");

        if ("true".equalsIgnoreCase(flag))
        {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Map<String, String> alarmLevels = new HashMap<String, String>();
                        alarmLevels.put("10", "警告报警");
                        alarmLevels.put("20", "一般报警");
                        alarmLevels.put("30", "重要报警");
                        alarmLevels.put("40", "严重报警");
                        alarmLevels.put("50", "离线报警");

                        MObjectDal objectDal = new MObjectDal();
                        List<MObjectEntity> objectList = objectDal.getLst("SELECT OBJ_ID FROM BMP_OBJECT");
                        int objId = objectList.get(new Random().nextInt(objectList.size())).getObjId();

                        ObjAttribDal objAttribDal = new ObjAttribDal();
                        List<ObjAttribEntity> objAttrbList =
                            objAttribDal.getLst(new SqlCondition("OBJ_ID", String.valueOf(objId), SqlLogicType.And, SqlRelationType.Equal,
                                SqlParamType.Numeric));
                        ObjAttribEntity objAttrb = objAttrbList.get(new Random().nextInt(objAttrbList.size()));

                        int eventDuration = new Random().nextInt(100000);
                        int alarmLevel = (new Random().nextInt(4) + 1) * 10;

                        AlarmEventEntity alarm = new AlarmEventEntity();
                        alarm.setObjAttrId(objAttrb.getObjAttrId());
                        alarm.setCollTime(System.currentTimeMillis());
                        alarm.setResumeTime(System.currentTimeMillis() + eventDuration);
                        alarm.setCollValue(objAttrb.getAttribValue());
                        alarm.setEventDuration(eventDuration);
                        alarm.setObjId(objId);
                        alarm.setAttribId(objAttrb.getAttribId());
                        alarm.setAlarmId(15);
                        alarm.setAlarmLevel(alarmLevel);
                        alarm.setLevelName(alarmLevels.get(String.valueOf(alarmLevel)));
                        alarm.setEventState(0);
                        alarm.setEventDesc(objAttrb.getObjattrName());
                        new AlarmEventDal().insert(alarm);
                        AlarmStatistic.getInstance().addAlarm(alarm);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }, Integer.parseInt(interval), Integer.parseInt(interval));
        }
    }
}
