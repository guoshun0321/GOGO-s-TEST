package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;

/**
 * @author ？
 */
public class SnmpObjType
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addSnmpObjType(String objXml) throws Exception
    {
        DefaultDal<SnmpObjTypeEntity> dal = new DefaultDal<SnmpObjTypeEntity>(SnmpObjTypeEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateSnmpObjType(String objXml) throws Exception
    {
        DefaultDal<SnmpObjTypeEntity> dal = new DefaultDal<SnmpObjTypeEntity>(SnmpObjTypeEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteSnmpObjType(int keyId) throws Exception
    {
        DefaultDal<SnmpObjTypeEntity> dal = new DefaultDal<SnmpObjTypeEntity>(SnmpObjTypeEntity.class);
        dal.delete(keyId);
    }
}
