package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;

/**
 * @author ？
 */
public class TrapTable
{

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addTrapTable(String objXml) throws Exception
    {
        TrapTableDal dal = new TrapTableDal();
        return dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateTrapTable(String objXml) throws Exception
    {
        TrapTableDal dal = new TrapTableDal();
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteTrapTable(int keyId) throws Exception
    {
        TrapTableDal dal = new TrapTableDal();
        dal.deleteById(keyId);
    }

    /**
     * 根据OID更新
     * @param OID 参数
     * @param NAME_CN 参数
     * @param DESC_CN 参数
     * @throws Exception 异常
     */
    @Business
    public void updateMibTrapNodeByOID(String OID, String NAME_CN, String DESC_CN) throws Exception
    {
        TrapTableDal dal = new TrapTableDal();
        dal.updateMibTrapNodeByOID(OID, NAME_CN, DESC_CN);
    }

}
