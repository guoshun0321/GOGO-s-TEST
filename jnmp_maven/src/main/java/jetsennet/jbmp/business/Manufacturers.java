/**
 * 日 期： 2011-12-6
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  Manufacturers.java
 * 历 史： 2011-12-6 创建
 */
package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ManufacturersEntity;

/**
 * 厂商管理
 */
public class Manufacturers
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addManufacturers(String objXml) throws Exception
    {
        DefaultDal<ManufacturersEntity> dal = new DefaultDal<ManufacturersEntity>(ManufacturersEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateManufacturers(String objXml) throws Exception
    {
        DefaultDal<ManufacturersEntity> dal = new DefaultDal<ManufacturersEntity>(ManufacturersEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteManufacturers(int keyId) throws Exception
    {
        DefaultDal<ManufacturersEntity> dal = new DefaultDal<ManufacturersEntity>(ManufacturersEntity.class);
        dal.delete(keyId);
    }
}
