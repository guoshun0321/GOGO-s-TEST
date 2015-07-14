/************************************************************************
日 期: 2013-09-29
作 者: 刘帅
版 本: v1.3
描 述: 报警配置模板：批量配置属性或对象属性的报警规则和采集间隔的时候，用于存放选择的属性或对象属性。
历 史:
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 报警配置模板 
 * @author liushuai
 */
@Table(name = "BMP_ALARMCONFIGTEMPLATE")
public class AlarmConfigTemplateEntity
{
    @Id
    @Column(name = "TEMPLATE_ID")
    private int templateId;

    @Column(name = "TEMPLATE_NAME")
    private String templateName;

    @Column(name = "TEMPLATE_TYPE")
    private int templateType;

    /**
     * 0 表示属性
     */
    private static final int TEMPLATE_TYPE_ATTR = 0;

    /**
     * 1 表示对象属性（指标）
     */
    private static final int TEMPLATE_TYPE_OBJATTR = 1;

    @Column(name = "TEMPLATE_INFO")
    private String templateInfo;

    @Column(name = "CREATE_TIME")
    private Date createDate;

    public int getTemplateId()
    {
        return templateId;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public int getTemplateType()
    {
        return templateType;
    }

    public void setTemplateType(int templateType)
    {
        this.templateType = templateType;
    }

    public String getTemplateInfo()
    {
        return templateInfo;
    }

    public void setTemplateInfo(String templateInfo)
    {
        this.templateInfo = templateInfo;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }
}
