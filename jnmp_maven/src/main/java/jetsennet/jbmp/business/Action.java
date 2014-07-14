package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ActionEntity;

/**
 * @author liwei代码优化
 */
public class Action
{

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addAction(String objXml) throws Exception
    {
        DefaultDal<ActionEntity> dal = new DefaultDal<ActionEntity>(ActionEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAction(String objXml) throws Exception
    {
        DefaultDal<ActionEntity> dal = new DefaultDal<ActionEntity>(ActionEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAction(int keyId) throws Exception
    {
        DefaultDal<ActionEntity> dal = new DefaultDal<ActionEntity>(ActionEntity.class);
        dal.delete(keyId);
    }
}
