package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ?
 */
@Table(name = "BMP_ATTRIBALARM")
public class AttribAlarmEntity
{

    @Column(name = "OBJATTR_ID")
    private int objattrId;
    @Column(name = "ALARM_ID")
    private int alarmId;

    /**
     * 构造函数
     */
    public AttribAlarmEntity()
    {

    }

    /**
     * @param objattrId 对象属性
     * @param alarmId 告警
     */
    public AttribAlarmEntity(int objattrId, int alarmId)
    {
        this.objattrId = objattrId;
        this.alarmId = alarmId;
    }

    /**
     * @return the objattrId
     */
    public int getObjattrId()
    {
        return objattrId;
    }

    /**
     * @param objattrId the objattrId to set
     */
    public void setObjattrId(int objattrId)
    {
        this.objattrId = objattrId;
    }

    /**
     * @return the alarmId
     */
    public int getAlarmId()
    {
        return alarmId;
    }

    /**
     * @param alarmId the alarmId to set
     */
    public void setAlarmId(int alarmId)
    {
        this.alarmId = alarmId;
    }
}
