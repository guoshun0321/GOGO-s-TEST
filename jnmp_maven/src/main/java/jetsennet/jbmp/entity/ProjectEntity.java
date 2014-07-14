/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author Li
 */
@Table(name = "BMP_Project")
public class ProjectEntity
{
    @Id
    @Column(name = "PROJECT_ID")
    private int projectId;
    @Column(name = "PROJECT_NUM")
    private String projectNum;
    @Column(name = "PROJECT_NAME")
    private String projectName;
    @Column(name = "PROJECT_TYPE")
    private int projectType;
    @Column(name = "REPORT_TIME")
    private Date reportTime;
    @Column(name = "PROJECT_CONTENT")
    private String projectContent;
    @Column(name = "BUDGET_MONEY")
    private double budgetMoney;
    @Column(name = "PROJECT_ATTACHMENT")
    private String projectAttachment;
    @Column(name = "PROJECT_ATTACHMENT_PATH")
    private String projectAttachmentPath;

    /**
     * 构造函数
     */
    public ProjectEntity()
    {

    }

    /**
     * @return the projectId
     */
    public int getProjectId()
    {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(int projectId)
    {
        this.projectId = projectId;
    }

    /**
     * @return the projectNum
     */
    public String getProjectNum()
    {
        return projectNum;
    }

    /**
     * @param projectNum the projectNum to set
     */
    public void seProjectNum(String projectNum)
    {
        this.projectNum = projectNum;
    }

    /**
     * @return the projectName
     */
    public String getProjectName()
    {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    /**
     * @return the projectType
     */
    public int getProjectType()
    {
        return projectType;
    }

    /**
     * @param projectType the projectType to set
     */
    public void setProjectType(int projectType)
    {
        this.projectType = projectType;
    }

    /**
     * @return the reportTime
     */
    public Date getReportTime()
    {
        return reportTime;
    }

    /**
     * @param reportTime the reportTime to set
     */
    public void setReportTime(Date reportTime)
    {
        this.reportTime = reportTime;
    }

    /**
     * @return the projectContent
     */
    public String getProjectContent()
    {
        return projectContent;
    }

    /**
     * @param projectContent the projectContent to set
     */
    public void setProjectContent(String projectContent)
    {
        this.projectContent = projectContent;
    }

    /**
     * @return the budgetMoney
     */
    public double getBudgetMoney()
    {
        return budgetMoney;
    }

    /**
     * @param budgetMoney the budgetMoney to set
     */
    public void setBudgetMoney(double budgetMoney)
    {
        this.budgetMoney = budgetMoney;
    }

    /**
     * @return the projectAttachment
     */
    public String getProjectAttachment()
    {
        return projectAttachment;
    }

    /**
     * @param projectAttachment the projectAttachment to set
     */
    public void setProjectAttachment(String projectAttachment)
    {
        this.projectAttachment = projectAttachment;
    }

    /**
     * @return the projectAttachmentPath
     */
    public String getProjectAttachmentPath()
    {
        return projectAttachmentPath;
    }

    /**
     * @param projectAttachmentPath the projectAttachmentPath to set
     */
    public void setProjectAttachmentPath(String projectAttachmentPath)
    {
        this.projectAttachmentPath = projectAttachmentPath;
    }
}
