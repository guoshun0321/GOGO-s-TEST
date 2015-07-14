package jetsennet.jbmp.alarm.rule;

import java.util.Date;

/**
 * 历史数据，在缓存中一个需要报警的对象属性对应一个历史数据对象
 * 
 * @author 郭祥
 */
public abstract class AbsHistoryData
{

    /**
     * 对象属性ID
     */
    protected int objAttrId;
    /**
     * 报警规则
     */
    protected AbsAlarmRule rule;

    public AbsHistoryData(int objAttrId, AbsAlarmRule rule)
    {
        this.objAttrId = objAttrId;
        this.rule = rule;
    }

    /**
     * 添加数据
     * @param value
     * @param time
     */
    public abstract boolean add(String value, Date time);

    /**
     * 清除数据
     */
    public abstract boolean clear();

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("对象属性ID：");
        sb.append(this.objAttrId);
        sb.append("；规则：");
        sb.append(this.rule);
        return sb.toString();
    }

    public int getObjAttrId()
    {
        return objAttrId;
    }

    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    public AbsAlarmRule getRule()
    {
        return rule;
    }

    public void setRule(AbsAlarmRule rule)
    {
        this.rule = rule;
    }

}
