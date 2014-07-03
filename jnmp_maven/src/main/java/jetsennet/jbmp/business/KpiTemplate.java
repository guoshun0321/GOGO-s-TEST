package jetsennet.jbmp.business;

import java.util.List;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.KpiTemplateEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

public class KpiTemplate
{
    @Business
    public String addKpiTemplate(String objXml) throws Exception
    {
        DefaultDal<KpiTemplateEntity> dal = new DefaultDal<KpiTemplateEntity>(KpiTemplateEntity.class);
        return "" + dal.insertXml(objXml);
    }

    @Business
    public String deleteKpiTemplate(int keyId) throws Exception
    {
        DefaultDal<KpiTemplateEntity> dal = new DefaultDal<KpiTemplateEntity>(KpiTemplateEntity.class);
        return "" + dal.delete(keyId);
    }

    @Business
    public String queryTemplateByName(String templateName) throws Exception
    {
        DefaultDal<KpiTemplateEntity> dal = new DefaultDal<KpiTemplateEntity>(KpiTemplateEntity.class);
        List<KpiTemplateEntity> list =
            dal.getLst(new SqlCondition("TEMPLATE_NAME", templateName, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String));
        int number = 0;
        if (list != null && list.size() > 0)
        {
            number = list.size();
        }
        return String.valueOf(number);
    }
}
