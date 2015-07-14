package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.DutyEntity;
import jetsennet.sqlclient.SqlCondition;

/**
 * @author ？
 */
public class Duty
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addDuty(String objXml) throws Exception
    {
        DefaultDal<DutyEntity> dal = new DefaultDal<DutyEntity>(DutyEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateDuty(String objXml) throws Exception
    {
        DefaultDal<DutyEntity> dal = new DefaultDal<DutyEntity>(DutyEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteDuty(int keyId) throws Exception
    {
        DefaultDal<DutyEntity> dal = new DefaultDal<DutyEntity>(DutyEntity.class);
        return dal.delete(keyId);
    }

    /**
     * 根据条件删除
     * @param condition 条件
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteDutyByCondition(SqlCondition... condition) throws Exception
    {
        DefaultDal<DutyEntity> dal = new DefaultDal<DutyEntity>(DutyEntity.class);
        return dal.delete(condition);
    }
}
