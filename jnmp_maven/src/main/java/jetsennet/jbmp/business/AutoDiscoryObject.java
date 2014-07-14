package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AutoDisObjEntity;

/**
 * @author ?
 */
public class AutoDiscoryObject
{

    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAutoDiscoryObject(String objXml) throws Exception
    {
        DefaultDal<AutoDisObjEntity> dal = new DefaultDal<AutoDisObjEntity>(AutoDisObjEntity.class);
        dal.updateXml(objXml);
    }
}
