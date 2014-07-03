package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.SnmpTrapEntity;

/**
 * @author ？
 */
public class SnmpTrap
{
    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void addSnmpTrap(String objXml) throws Exception
    {
        DefaultDal<SnmpTrapEntity> dal = new DefaultDal<SnmpTrapEntity>(SnmpTrapEntity.class);
        dal.insertXml(objXml);
    }

    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateSnmpTrap(String objXml) throws Exception
    {
        DefaultDal<SnmpTrapEntity> dal = new DefaultDal<SnmpTrapEntity>(SnmpTrapEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteSnmpTrap(int keyId) throws Exception
    {
        DefaultDal<SnmpTrapEntity> dal = new DefaultDal<SnmpTrapEntity>(SnmpTrapEntity.class);
        dal.delete(keyId);
    }
}
