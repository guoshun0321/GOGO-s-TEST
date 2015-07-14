package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.AlarmEventLogDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmEventLogEntity;

/**
 * @author liwei代码格式优化
 */
public class AlarmEventLog
{

    /**
     * 新增
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public String addAlarmEventLog(String objXml) throws Exception
    {
        //设置BMP_ALARMEVENTLOG在NET_SEQUENCE表中的SERIAL_NUMBER和BMP_ALARMEVENT的一致
        AlarmEventLogDal logDal = new AlarmEventLogDal();
        AlarmEventLogEntity alarmEventEntity =
            logDal.get("SELECT SERIAL_NUMBER AS ALARMEVT_ID FROM NET_SEQUENCE WHERE TABLE_NAME = 'BMP_ALARMEVENT'");
        DefaultDal dal = new DefaultDal();
        AlarmEventLogEntity alarmEventLogEntity = 
            logDal.get("SELECT SERIAL_NUMBER AS ALARMEVT_ID FROM NET_SEQUENCE WHERE TABLE_NAME = 'BMP_ALARMEVENTLOG'");
        int alarmEventId = alarmEventEntity.getAlarmEvtId()>=alarmEventLogEntity.getAlarmEvtId()?alarmEventEntity.getAlarmEvtId():alarmEventLogEntity.getAlarmEvtId();
        
        dal.update(String.format("UPDATE NET_SEQUENCE SET SERIAL_NUMBER='%s' WHERE TABLE_NAME = 'BMP_ALARMEVENTLOG'",
            alarmEventId));
        String result = "" + logDal.insertXml(objXml);
        dal.update(String.format("UPDATE NET_SEQUENCE SET SERIAL_NUMBER='%s' WHERE TABLE_NAME = 'BMP_ALARMEVENT'",
            alarmEventId + 1));
        return result; 
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAlarmEventLog(String objXml) throws Exception
    {
        AlarmEventLogDal dal = new AlarmEventLogDal();
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarmEventLog(int keyId) throws Exception
    {
        AlarmEventLogDal dal = new AlarmEventLogDal();
        dal.delete(keyId);
    }
}
