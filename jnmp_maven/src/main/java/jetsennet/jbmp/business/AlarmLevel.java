package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.AlarmLevelDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;

/**
 * @author liwei代码格式优化
 */
public class AlarmLevel
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addAlarmLevel(String objXml) throws Exception
    {
        AlarmLevelDal dal = new AlarmLevelDal();
        return "" + dal.addAlarmLevel(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAlarmLevel(String objXml) throws Exception
    {
        AlarmLevelDal dal = new AlarmLevelDal();
        dal.updateAlarmLevel(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarmLevel(int keyId) throws Exception
    {
        AlarmLevelDal dal = new AlarmLevelDal();
        dal.deleteAlarmLevel(keyId);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarmLevel(String sql) throws Exception
    {
        AlarmLevelDal dal = new AlarmLevelDal();
        dal.delete(sql);
    }
}
