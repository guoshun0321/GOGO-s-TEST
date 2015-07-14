package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.DutyLogAttachmentEntity;

/**
 * @author ？
 */
public class DutyLogAttachment
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addAttachment(String objXml) throws Exception
    {
        DefaultDal<DutyLogAttachmentEntity> dal = new DefaultDal<DutyLogAttachmentEntity>(DutyLogAttachmentEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAttachment(String objXml) throws Exception
    {
        DefaultDal<DutyLogAttachmentEntity> dal = new DefaultDal<DutyLogAttachmentEntity>(DutyLogAttachmentEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteAttachment(int keyId) throws Exception
    {
        DefaultDal<DutyLogAttachmentEntity> dal = new DefaultDal<DutyLogAttachmentEntity>(DutyLogAttachmentEntity.class);
        return dal.delete(keyId);
    }
}
