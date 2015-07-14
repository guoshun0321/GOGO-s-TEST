/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author Guo
 */
@Table(name = "BMP_ALARMACTION")
public class AlarmActionEntity
{

    @Column(name = "LEVEL_ID")
    private int levelId;
    @Column(name = "ACTION_ID")
    private int actionId;

    /**
     * 构造函数
     */
    public AlarmActionEntity()
    {
    }

    /**
     * @return the levelId
     */
    public int getLevelId()
    {
        return levelId;
    }

    /**
     * @param levelId the levelId to set
     */
    public void setLevelId(int levelId)
    {
        this.levelId = levelId;
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
}
