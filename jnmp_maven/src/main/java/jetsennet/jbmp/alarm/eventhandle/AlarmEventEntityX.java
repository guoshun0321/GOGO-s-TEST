/************************************************************************
日 期: 2012-1-9
作 者: 郭祥
版 本: v1.3
描 述: 报警事件扩展
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.HashMap;
import java.util.Map;

import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 报警事件扩展
 * @author 郭祥
 */
public class AlarmEventEntityX
{

    /**
     * 对象ID
     */
    private int objId;
    /**
     * 对象属性ID
     */
    private int objAttrId;
    /**
     * 参考值
     */
    private String value;
    /**
     * 恢复时间
     */
    private long recoverTime;
    /**
     * 报警事件
     */
    private AlarmEventEntity alarm;
    /**
     * 事件类型
     */
    private int eventType;
    /**
     * 事件ID
     */
    private int eventId;
    /**
     * 扩展参数
     */
    private Map<String, String> params;
    /**
     * 一般事件
     */
    public static final int EVENT_TYPE_COMMEN = 0;
    /**
     * 手动清除事件
     */
    public static final int EVENT_TYPE_MANU = 1;
    /**
     * 规则改变事件
     */
    public static final int EVENT_TYPE_CHANGE = 2;
    /**
     * 线程停止时，用于唤醒线程的事件
     */
    public static final int EVENT_TYPE_STOP = 9999;
    /**
     * 扩展参数KEY，操作数。具体的操作数在Handle类定义
     */
    public static final String EVENT_OPNUM = "OPNUM";

    /**
     * 构造方法
     */
    public AlarmEventEntityX()
    {
    }

    /**
     * 构造函数
     * @param alarm 报警事件
     * @param eventType 状态
     * @param objId 对象ID
     * @param objAttrId 对象属性ID
     * @param value 值
     */
    public AlarmEventEntityX(AlarmEventEntity alarm, int eventType, int objId, int objAttrId, String value)
    {
        this.alarm = alarm;
        this.eventType = eventType;
        this.objId = objId;
        this.objAttrId = objAttrId;
        this.value = value;
    }

    /**
     * 添加扩展值
     * @param key 键
     * @param value 值
     */
    public void addParams(String key, String value)
    {
        if (params == null)
        {
            params = new HashMap<String, String>();
        }
        params.put(key, value);
    }

    /**
     * 删除扩展值
     * @param key 键
     * @return 结果
     */
    public String getParams(String key)
    {
        if (params == null)
        {
            return null;
        }
        return params.get(key);
    }

    /**
     * @return 获取操作数，默认为-1
     */
    public int getOpNum()
    {
        int retval = -1;
        if (params != null)
        {
            Object obj = params.get(EVENT_OPNUM);
            if (obj != null && obj instanceof Integer)
            {
                retval = (Integer) obj;
            }
        }
        return retval;
    }

    public int getObjId()
    {
        return objId;
    }

    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public int getObjAttrId()
    {
        return objAttrId;
    }

    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public long getRecoverTime()
    {
        return recoverTime;
    }

    public void setRecoverTime(long recoverTime)
    {
        this.recoverTime = recoverTime;
    }

    public AlarmEventEntity getAlarm()
    {
        return alarm;
    }

    public void setAlarm(AlarmEventEntity alarm)
    {
        this.alarm = alarm;
    }

    public int getEventType()
    {
        return eventType;
    }

    public void setEventType(int eventType)
    {
        this.eventType = eventType;
    }

    public Map<String, String> getParams()
    {
        return params;
    }

    public void setParams(Map<String, String> params)
    {
        this.params = params;
    }

    public int getEventId()
    {
        return eventId;
    }

    public void setEventId(int eventId)
    {
        this.eventId = eventId;
    }
}
