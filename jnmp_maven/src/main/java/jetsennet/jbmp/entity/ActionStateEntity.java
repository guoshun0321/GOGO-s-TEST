/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author Guo
 */
@Table(name = "BMP_ACTIONSTATE")
public class ActionStateEntity
{

    @Column(name = "ALARMEVT_ID")
    private int AlarmEvtId;
    @Column(name = "ACTION_ID")
    private int actionId;
    @Column(name = "ACTION_STATE")
    private int actionState;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    public static final int ACTION_STATE_NEW = 0;
    public static final int ACTION_STATE_RUNNABLE = 1;
    public static final int ACTION_STATE_RUNNING = 2;
    public static final int ACTION_STATE_SUC = 3;
    public static final int ACTION_STATE_FAILD = 4;

    /**
     * 构造函数
     */
    public ActionStateEntity()
    {
    }

    /**
     * @param AlarmEvtId 告警
     * @param actionId 参数
     * @param updateTime 更新时间
     */
    public ActionStateEntity(int AlarmEvtId, int actionId, Date updateTime)
    {
        this.AlarmEvtId = AlarmEvtId;
        this.actionId = actionId;
        this.actionState = ACTION_STATE_NEW;
        this.updateTime = updateTime;
    }

    /**
     * @return the AlarmEvtId
     */
    public int getAlarmEvtId()
    {
        return AlarmEvtId;
    }

    /**
     * @param AlarmEvtId the AlarmEvtId to set
     */
    public void setAlarmEvtId(int AlarmEvtId)
    {
        this.AlarmEvtId = AlarmEvtId;
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
     * @return the actionState
     */
    public int getActionState()
    {
        return actionState;
    }

    /**
     * @param actionState the actionState to set
     */
    public void setActionState(int actionState)
    {
        this.actionState = actionState;
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
