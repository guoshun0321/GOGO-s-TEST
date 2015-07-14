/************************************************************************
日 期：2012-03-22
作 者: 
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.MibBankDal;
import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.ValueTableDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.MibBanksEntity;

/**
 * @author?
 */
public class MibBanks
{

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addMibBanks(String objXml) throws Exception
    {
        DefaultDal<MibBanksEntity> dal = new DefaultDal<MibBanksEntity>(MibBanksEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateMibBanks(String objXml) throws Exception
    {
        DefaultDal<MibBanksEntity> dal = new DefaultDal<MibBanksEntity>(MibBanksEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteMibBanks(int keyId) throws Exception
    {
        if (keyId <= 0)
        {
            return;
        }
        DefaultDal<MibBanksEntity> dal = new DefaultDal<MibBanksEntity>(MibBanksEntity.class);
        dal.delete(keyId);
        SnmpNodesDal sndal = new SnmpNodesDal();
        sndal.deleteByType(keyId);
        TrapTableDal ttdal = new TrapTableDal();
        ttdal.deleteByType(keyId);
        ValueTableDal vtdal = new ValueTableDal();
        vtdal.deleteByMibId(keyId);
    }

    /**
     * 根据OID更新
     * @param oid 参数
     * @param desc 参数
     * @throws Exception 异常
     */
    @Business
    public void updateNodeByOID(String oid, String desc) throws Exception
    {
        MibBankDal mbdal = new MibBankDal();
        mbdal.UpdateNodeByOID(oid, desc);
    }

}
