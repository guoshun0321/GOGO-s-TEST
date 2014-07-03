package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ReportTimeEntity;

/**
 * @author ？
 */
public class ReportTime
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addReportTime(String objXml) throws Exception
    {
        DefaultDal<ReportTimeEntity> dal = new DefaultDal<ReportTimeEntity>(ReportTimeEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateReportTime(String objXml) throws Exception
    {
        DefaultDal<ReportTimeEntity> dal = new DefaultDal<ReportTimeEntity>(ReportTimeEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteReportTime(int keyId) throws Exception
    {
        DefaultDal<ReportTimeEntity> dal = new DefaultDal<ReportTimeEntity>(ReportTimeEntity.class);
        dal.delete(keyId);
    }
}
