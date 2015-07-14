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
@Table(name = "BMP_DUTY")
public class DutyEntity
{
    @Id
    @Column(name = "DUTY_ID")
    private int dutyId;
    @Column(name = "USER_ID")
    private int userId;
    @Column(name = "DUTY_TYPE")
    private int dutyType;
    @Column(name = "MONTH_NUM")
    private int monthNum;
    @Column(name = "DAY_NUM")
    private int dayNum;
    @Column(name = "WEEK_NUM")
    private int weekNum;
    @Column(name = "START_TIME")
    private Date startTime;
    @Column(name = "END_TIME")
    private Date endTime;
    @Column(name = "DUTY_DESC")
    private String dutyDesc;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "CREATE_USERID")
    private int createUserId;

    /**
     * 构造函数
     */
    public DutyEntity()
    {
    }

    /**
     * @return the dutyId
     */
    public int getDutyId()
    {
        return dutyId;
    }

    /**
     * @param dutyId the dutyId to set
     */
    public void setDutyId(int dutyId)
    {
        this.dutyId = dutyId;
    }

    /**
     * @return the userId
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * @return the dutyType
     */
    public int getDutyType()
    {
        return dutyType;
    }

    /**
     * @param dutyType the dutyType to set
     */
    public void setDutyType(int dutyType)
    {
        this.dutyType = dutyType;
    }

    /**
     * @return the monthNum
     */
    public int getMonthNum()
    {
        return monthNum;
    }

    /**
     * @param monthNum the monthNum to set
     */
    public void setMonthNum(int monthNum)
    {
        this.monthNum = monthNum;
    }

    /**
     * @return the dayNum
     */
    public int getDayNum()
    {
        return dayNum;
    }

    /**
     * @param dayNum the dayNum to set
     */
    public void setDayNum(int dayNum)
    {
        this.dayNum = dayNum;
    }

    /**
     * @return the weekNum
     */
    public int getWeekNum()
    {
        return weekNum;
    }

    /**
     * @param weekNum the weekNum to set
     */
    public void setWeekNum(int weekNum)
    {
        this.weekNum = weekNum;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime()
    {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    /**
     * @return the dutyDesc
     */
    public String getDutyDesc()
    {
        return dutyDesc;
    }

    /**
     * @param dutyDesc the dutyDesc to set
     */
    public void setDutyDesc(String dutyDesc)
    {
        this.dutyDesc = dutyDesc;
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
     * @return the createUserId
     */
    public int getCreateUserId()
    {
        return createUserId;
    }

    /**
     * @param createUserId the createUserId to set
     */
    public void setCreateUserId(int createUserId)
    {
        this.createUserId = createUserId;
    }
}
