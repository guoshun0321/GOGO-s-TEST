package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.DiskArrayEntity;

/**
 * @author ？
 */
public class DiskArray
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addDiskArray(String objXml) throws Exception
    {
        DefaultDal<DiskArrayEntity> dal = new DefaultDal<DiskArrayEntity>(DiskArrayEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 修改
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateDiskArray(String objXml) throws Exception
    {
        DefaultDal<DiskArrayEntity> dal = new DefaultDal<DiskArrayEntity>(DiskArrayEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteDiskArray(int keyId) throws Exception
    {
        DefaultDal<DiskArrayEntity> dal = new DefaultDal<DiskArrayEntity>(DiskArrayEntity.class);
        dal.delete(keyId);
    }
}
