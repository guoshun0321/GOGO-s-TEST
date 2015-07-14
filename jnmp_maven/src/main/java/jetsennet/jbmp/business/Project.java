package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ProjectEntity;

/**
 * @author？
 */
public class Project
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addProject(String objXml) throws Exception
    {
        DefaultDal<ProjectEntity> dal = new DefaultDal<ProjectEntity>(ProjectEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateProject(String objXml) throws Exception
    {
        DefaultDal<ProjectEntity> dal = new DefaultDal<ProjectEntity>(ProjectEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteProject(int keyId) throws Exception
    {
        DefaultDal<ProjectEntity> dal = new DefaultDal<ProjectEntity>(ProjectEntity.class);
        dal.delete(keyId);
    }
}
