package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

@Table(name = "BMP_CHECKTEMPLATE")
public class CheckTemplateEntity
{
    @Id
    @Column(name = "TEMPLATE_ID")
    private int templateId;
    @Column(name = "MAP_IDS")
    private String mapIds;
    @Column(name = "TEMPLATE_NAME")
    private String templateName;

    public CheckTemplateEntity()
    {

    }

    public int getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(int templateId)
    {
        this.templateId = templateId;
    }

    public String getMapIds()
    {
        return mapIds;
    }

    public void setMapIds(String mapIds)
    {
        this.mapIds = mapIds;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

}
