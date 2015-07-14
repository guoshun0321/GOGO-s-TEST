package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

@Table(name = "BMP_KPITEMPLATE")
public class KpiTemplateEntity
{
    @Id
    @Column(name = "TEMPLATE_ID")
    private int kpiTemplateId;
    @Column(name = "TEMPLATE_NAME")
    private String kpiTemplateName;
    @Column(name = "TEMPLATE_XML")
    private String kpiTemplateXml;

    public KpiTemplateEntity()
    {

    }

    public int getKpiTemplateId()
    {
        return kpiTemplateId;
    }

    public void setKpiTemplateId(int kpiTemplateId)
    {
        this.kpiTemplateId = kpiTemplateId;
    }

    public String getKpiTemplateName()
    {
        return kpiTemplateName;
    }

    public void setKpiTemplateName(String kpiTemplateName)
    {
        this.kpiTemplateName = kpiTemplateName;
    }

    public String getKpiTemplateXml()
    {
        return kpiTemplateXml;
    }

    public void setKpiTemplateXml(String kpiTemplateXml)
    {
        this.kpiTemplateXml = kpiTemplateXml;
    }

}
