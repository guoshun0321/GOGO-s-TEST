package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ValueTableEntity;

/**
 * @author ?
 */
public class ValueTable
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addValueTable(String objXml) throws Exception
    {
        DefaultDal<ValueTableEntity> dal = new DefaultDal<ValueTableEntity>(ValueTableEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 修改
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateValueTable(String objXml) throws Exception
    {
        DefaultDal<ValueTableEntity> dal = new DefaultDal<ValueTableEntity>(ValueTableEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteValueTable(int keyId) throws Exception
    {
        DefaultDal<ValueTableEntity> dal = new DefaultDal<ValueTableEntity>(ValueTableEntity.class);
        dal.delete(keyId);
    }
}
