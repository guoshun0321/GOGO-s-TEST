package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.CheckTemplateEntity;

public class CheckTemplate
{
    @Business
    public String addCheckTemplate(String objXml) throws Exception
    {
        DefaultDal<CheckTemplateEntity> dal = new DefaultDal<CheckTemplateEntity>(CheckTemplateEntity.class);
        return "" + dal.insertXml(objXml);
    }

    @Business
    public void deleteCheckTemplate(int keyId) throws Exception
    {
        DefaultDal<CheckTemplateEntity> dal = new DefaultDal<CheckTemplateEntity>(CheckTemplateEntity.class);
        dal.delete(keyId);
    }
}
