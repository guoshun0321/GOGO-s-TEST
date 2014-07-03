/**********************************************************************
 * 日 期: 2012-08-23
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Alarmlog.java
 * 历 史: 2012-08-23 Create
 *********************************************************************/
package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.AlarmlogDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AlarmlogEntity;

/**
 * 报警处理日志 Bussiness
 */
public class Alarmlog
{

    /**
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addAlarmlog(String objXml) throws Exception
    {
        AlarmlogDal dalAlarmlog = new AlarmlogDal();
        return "" + dalAlarmlog.insertXml(objXml);
    }

    /**
     * 编辑
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAlarmlog(String objXml) throws Exception
    {
        AlarmlogDal dalAlarmlog = new AlarmlogDal();
        dalAlarmlog.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarmlog(int keyId) throws Exception
    {
        AlarmlogDal dalAlarmlog = new AlarmlogDal();
        dalAlarmlog.delete(keyId);
    }

    /**
     * @param sql 语句
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarmlogMany(String sql) throws Exception
    {
        AlarmlogDal dalAlarmlog = new AlarmlogDal();
        dalAlarmlog.delete(sql);
    }

    /**
     * @param log 日志
     * @throws Exception 异常
     */
    @Business
    public void addAlarmlogMany(AlarmlogEntity log) throws Exception
    {
        AlarmlogDal dalAlarmlog = new AlarmlogDal();
        dalAlarmlog.insert(log);
    }

}
