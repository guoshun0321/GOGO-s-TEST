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
@Table(name = "BMP_WORKORDER")
public class WorkOrderEntity
{
    @Id
    @Column(name = "ORDER_ID")
    private int orderId;
    @Column(name = "ORDER_DESC")
    private String orderDesc;
    @Column(name = "EVENT_ID")
    private int eventId;
    @Column(name = "CHECK_USERID")
    private int checkUserId;
    @Column(name = "ORDER_STATE")
    private int orderState;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 默认构造方法
     */
    public WorkOrderEntity()
    {

    }

    /**
     * @return the orderId
     */
    public int getOrderId()
    {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    /**
     * @return the orderDesc
     */
    public String getOrderDesc()
    {
        return orderDesc;
    }

    /**
     * @param orderDesc the orderDesc to set
     */
    public void setOrderDesc(String orderDesc)
    {
        this.orderDesc = orderDesc;
    }

    /**
     * @return the eventId
     */
    public int getEventId()
    {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(int eventId)
    {
        this.eventId = eventId;
    }

    /**
     * @return the checkUserId
     */
    public int getCheckUserId()
    {
        return checkUserId;
    }

    /**
     * @param checkUserId the checkUserId to set
     */
    public void setCheckUserId(int checkUserId)
    {
        this.checkUserId = checkUserId;
    }

    /**
     * @return the orderState
     */
    public int getOrderState()
    {
        return orderState;
    }

    /**
     * @param orderState the orderState to set
     */
    public void setOrderState(int orderState)
    {
        this.orderState = orderState;
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
     * @return the updateTime
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
