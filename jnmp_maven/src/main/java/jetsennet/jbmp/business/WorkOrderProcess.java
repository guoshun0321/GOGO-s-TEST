package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.WorkOrderProcessEntity;

/**
 * @author ?
 */
public class WorkOrderProcess
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addWorkOrderProcess(String objXml) throws Exception
    {
        DefaultDal<WorkOrderProcessEntity> dal = new DefaultDal<WorkOrderProcessEntity>(WorkOrderProcessEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateWorkOrderProcess(String objXml) throws Exception
    {
        DefaultDal<WorkOrderProcessEntity> dal = new DefaultDal<WorkOrderProcessEntity>(WorkOrderProcessEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteWorkOrderProcess(int keyId) throws Exception
    {
        DefaultDal<WorkOrderProcessEntity> dal = new DefaultDal<WorkOrderProcessEntity>(WorkOrderProcessEntity.class);
        return dal.delete(keyId);
    }
}
