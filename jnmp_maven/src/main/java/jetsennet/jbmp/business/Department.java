package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.DepartmentEntity;

/**
 * @author？
 */
public class Department
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addDepartment(String objXml) throws Exception
    {
        DefaultDal<DepartmentEntity> dal = new DefaultDal<DepartmentEntity>(DepartmentEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 修改
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateDepartment(String objXml) throws Exception
    {
        DefaultDal<DepartmentEntity> dal = new DefaultDal<DepartmentEntity>(DepartmentEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteDepartment(int keyId) throws Exception
    {
        DefaultDal<DepartmentEntity> dal = new DefaultDal<DepartmentEntity>(DepartmentEntity.class);
        dal.delete(keyId);
    }
}
