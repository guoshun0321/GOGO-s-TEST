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
 * @author Guo
 */
@Table(name = "BMP_ACTION")
public class ActionEntity
{

    @Id
    @Column(name = "ACTION_ID")
    private int actionId;
    @Column(name = "ACTION_NAME")
    private String actionName;
    @Column(name = "ACTION_TYPE")
    private int actionType;
    @Column(name = "ACTION_PARAM")
    private String actionParam;
    @Column(name = "ACTION_DESC")
    private String actionDesc;
    @Column(name = "ASSIGN_TYPE")
    private int assignType;
    @Column(name = "ASSIGN_OBJID")
    private String assignObjId;
    @Column(name = "ASSIGN_PARAM")
    private String assignParam;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "WEEK_MASK")
    private String weekMask;
    @Column(name = "HOUR_MASK")
    private String hourMask;
    @Column(name = "FIELD_1")
    private String field1;

    /**
     * 构造函数
     */
    public ActionEntity()
    {
    }

    /**
     * @return the actionId
     */
    public int getActionId()
    {
        return actionId;
    }

    /**
     * @param actionId the actionId to set
     */
    public void setActionId(int actionId)
    {
        this.actionId = actionId;
    }

    /**
     * @return the actionName
     */
    public String getActionName()
    {
        return actionName;
    }

    /**
     * @param actionName the actionName to set
     */
    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    /**
     * @return the actionType
     */
    public int getActionType()
    {
        return actionType;
    }

    /**
     * @param actionType the actionType to set
     */
    public void setActionType(int actionType)
    {
        this.actionType = actionType;
    }

    /**
     * @return the actionParam
     */
    public String getActionParam()
    {
        return actionParam;
    }

    /**
     * @param actionParam the actionParam to set
     */
    public void setActionParam(String actionParam)
    {
        this.actionParam = actionParam;
    }

    /**
     * @return the actionDesc
     */
    public String getActionDesc()
    {
        return actionDesc;
    }

    /**
     * @param actionDesc the actionDesc to set
     */
    public void setActionDesc(String actionDesc)
    {
        this.actionDesc = actionDesc;
    }

    /**
     * @return the assignType
     */
    public int getAssignType()
    {
        return assignType;
    }

    /**
     * @param assignType the assignType to set
     */
    public void setAssignType(int assignType)
    {
        this.assignType = assignType;
    }

    /**
     * @return the assignObjId
     */
    public String getAssignObjId()
    {
        return assignObjId;
    }

    /**
     * @param assignObjId the assignObjId to set
     */
    public void setAssignObjId(String assignObjId)
    {
        this.assignObjId = assignObjId;
    }

    /**
     * @return the assignParam
     */
    public String getAssignParam()
    {
        return assignParam;
    }

    /**
     * @param assignParam the assignParam to set
     */
    public void setAssignParam(String assignParam)
    {
        this.assignParam = assignParam;
    }

    /**
     * @return the createUser
     */
    public String getCreateUser()
    {
        return createUser;
    }

    /**
     * @param createUser the createUser to set
     */
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    /**
     * @return the weekMask
     */
    public String getWeekMask()
    {
        return weekMask;
    }

    /**
     * @param weekMask the weekMask to set
     */
    public void setWeekMask(String weekMask)
    {
        this.weekMask = weekMask;
    }

    /**
     * @return the hourMask
     */
    public String getHourMask()
    {
        return hourMask;
    }

    /**
     * @param hourMask the hourMask to set
     */
    public void setHourMask(String hourMask)
    {
        this.hourMask = hourMask;
    }

    public String getField1()
    {
        return field1;
    }

    public void setField1(String field1)
    {
        this.field1 = field1;
    }
}
