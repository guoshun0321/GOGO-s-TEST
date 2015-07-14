package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.DutyLogEntity;

/**
 * @author？
 */
public class DutyLog
{

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addDutyLog(String objXml) throws Exception
    {
        DefaultDal<DutyLogEntity> dal = new DefaultDal<DutyLogEntity>(DutyLogEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateDutyLog(String objXml) throws Exception
    {
        DefaultDal<DutyLogEntity> dal = new DefaultDal<DutyLogEntity>(DutyLogEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteDutyLog(int keyId) throws Exception
    {
        DefaultDal<DutyLogEntity> dal = new DefaultDal<DutyLogEntity>(DutyLogEntity.class);
        dal.delete(keyId);
    }
}
