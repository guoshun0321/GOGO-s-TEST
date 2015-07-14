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
@Table(name = "BMP_WORKORDERPROCESS")
public class WorkOrderProcessEntity
{
    @Id
    @Column(name = "PROCESS_ID")
    private int processId;
    @Column(name = "PROCESS_DESC")
    private String processDesc;
    @Column(name = "ORDER_ID")
    private int orderId;
    @Column(name = "FROM_USERID")
    private int fromUserId;
    @Column(name = "TO_USERID")
    private int toUserId;
    @Column(name = "PROCESS_TYPE")
    private int processType;
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 默认构造方法
     */
    public WorkOrderProcessEntity()
    {
    }

    /**
     * @return the processId
     */
    public int getProcessId()
    {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public void setProcessId(int processId)
    {
        this.processId = processId;
    }

    /**
     * @return the processDesc
     */
    public String getProcessDesc()
    {
        return processDesc;
    }

    /**
     * @param processDesc the processDesc to set
     */
    public void setProcessDesc(String processDesc)
    {
        this.processDesc = processDesc;
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
     * @return the fromUserId
     */
    public int getFromUserId()
    {
        return fromUserId;
    }

    /**
     * @param fromUserId the fromUserId to set
     */
    public void setFromUserId(int fromUserId)
    {
        this.fromUserId = fromUserId;
    }

    /**
     * @return the toUserId
     */
    public int getToUserId()
    {
        return toUserId;
    }

    /**
     * @param toUserId the toUserId to set
     */
    public void setToUserId(int toUserId)
    {
        this.toUserId = toUserId;
    }

    /**
     * @return the processType
     */
    public int getProcessType()
    {
        return processType;
    }

    /**
     * @param processType the processType to set
     */
    public void setProcessType(int processType)
    {
        this.processType = processType;
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
}
