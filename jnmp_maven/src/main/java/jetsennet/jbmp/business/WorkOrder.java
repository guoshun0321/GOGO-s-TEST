package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.WorkOrderEntity;
import jetsennet.jbmp.entity.WorkOrderProcessEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ?
 */
public class WorkOrder
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addWorkOrder(String objXml) throws Exception
    {
        DefaultDal<WorkOrderEntity> dal = new DefaultDal<WorkOrderEntity>(WorkOrderEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateWorkOrder(String objXml) throws Exception
    {
        DefaultDal<WorkOrderEntity> dal = new DefaultDal<WorkOrderEntity>(WorkOrderEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteWorkOrder(int keyId) throws Exception
    {
        // 先删除工单对应的所有处理流程
        DefaultDal<WorkOrderProcessEntity> dalWorkOrderProcess = new DefaultDal<WorkOrderProcessEntity>(WorkOrderProcessEntity.class);
        dalWorkOrderProcess.delete(new SqlCondition("ORDER_ID", keyId + "", SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        DefaultDal<WorkOrderEntity> dal = new DefaultDal<WorkOrderEntity>(WorkOrderEntity.class);
        return dal.delete(keyId);
    }
}
