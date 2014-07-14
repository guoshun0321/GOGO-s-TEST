/************************************************************************
日 期: 2012-2-28
作 者: 郭祥
版 本: v1.3
描 述: 报警规则抽象类
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.util.TaskUtil;
import jetsennet.jbmp.util.TwoTuple;

/**
 * 报警规则抽象类
 * @author 郭祥
 */
public abstract class AbsAlarmRule
{

    /**
     * 规则ID，唯一
     */
    protected int ruleId;
    /**
     * 报警规则
     */
    protected AlarmEntity alarm;
    /**
     * 报警级别
     */
    protected ArrayList<AbsAlarmLevel> levels;
    /**
     * 报警级别，级别ID做索引
     */
    protected Map<Integer, AbsAlarmLevel> id2level;
    /**
     * 报警规则编号
     */
    public static final AtomicInteger RULE_ID = new AtomicInteger(0);

    public AbsAlarmRule(AlarmEntity alarm) throws AlarmRuleConstructionException
    {
        this.levels = new ArrayList<AbsAlarmLevel>();
        this.id2level = new HashMap<Integer, AbsAlarmLevel>();
        this.alarm = alarm;
        this.ruleId = RULE_ID.getAndIncrement();
        this.build();
    }

    /**
     * 构造报警规则
     * @param iAlarm 参数
     * @throws AlarmRuleConstructionException 异常
     */
    protected void build() throws AlarmRuleConstructionException
    {
        // 检查报警是否合法，不合法抛出异常
        List<AlarmLevelEntity> als = alarm.getLevels();
        if (als != null && !als.isEmpty())
        {
            for (AlarmLevelEntity al : als)
            {
                AlarmLevelSpan temp = new AlarmLevelSpan(al);
                this.levels.add(temp);
                this.id2level.put(al.getLevelId(), temp);
            }
        }
    }

    /**
     * 是否需要更新
     * @param iAlarm 参数
     * @return 结果
     */
    public boolean needUpdate(AlarmEntity iAlarm)
    {
        // 判断规则是否需要更新
        // 由前台保证字段不为NULL
        if (iAlarm.getCheckSpan() != alarm.getCheckSpan() || iAlarm.getCheckNum() != alarm.getCheckNum() || iAlarm.getOverNum() != alarm.getOverNum())
        {
            return true;
        }

        // 判断级别是否需要更新
        List<AlarmLevelEntity> als = iAlarm.getLevels();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.addAll(id2level.keySet());
        for (AlarmLevelEntity al : als)
        {
            int levelId = al.getLevelId();
            AbsAlarmLevel aae = this.id2level.get(levelId);
            
            // 新增或删除
            if (aae == null || aae.needUpdate(al))
            {
                return true;
            }
            // 注意：这里需要使用Integer.valueOf(int)将int转换成Integer
            keys.remove(Integer.valueOf(levelId));
        }
        if (keys.size() > 0)
        {
            // 需要删除
            return true;
        }
        return false;
    }

    /**
     * 获取报警规则对象和第一个报警级别对象
     * @return 结果
     */
    public TwoTuple<AlarmEntity, AlarmLevelEntity> firstLevel()
    {
        return new TwoTuple<AlarmEntity, AlarmLevelEntity>(alarm, levels.get(0).getLevel());
    }

    /**
     * 获取报警规则对象和第一个满足时间要求的报警级别对象
     * @return 结果
     */
    public TwoTuple<AlarmEntity, AlarmLevelEntity> firstLevel(Date now)
    {
        for (AbsAlarmLevel level : levels)
        {
            if (TaskUtil.validWeek(level.getLevel().getWeekMask(), level.getLevel().getHourMask(), now))
            {
                return new TwoTuple<AlarmEntity, AlarmLevelEntity>(alarm, level.getLevel());
            }
        }
        return null;
    }

    /**
     * 获取级别数量
     * @return 结果
     */
    public int getLevelSize()
    {
        return levels.size();
    }

    /**
     * 数据缓存中需要缓存的数据量
     * 按次数判断时，返回检查次数
     * 按时间判断时，返回Integer.MIN_VALUE
     */
    public abstract int sizeOfData();

    /**
     * 生成报警事件
     * 
     * @param data
     * @return
     */
    public abstract List<AlarmEventEntity> genAlarmEvent(AbsHistoryData data, Object obj, int objId, int attrId, int objAttrId, String desc);

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(alarm.getAlarmName());
        sb.append("；检查次数：").append(alarm.getCheckNum());
        sb.append("；检查时间：").append(alarm.getCheckSpan());
        sb.append("；越限次数：").append(alarm.getOverNum());
        sb.append("。\n{");
        if (levels != null && !levels.isEmpty())
        {
            for (AbsAlarmLevel level : levels)
            {
                sb.append("\n").append("\t").append(level.toString());
            }
        }
        sb.append("\n").append("}");
        return sb.toString();
    }

    /**
     * @return the alarm
     */
    public AlarmEntity getAlarm()
    {
        return alarm;
    }

    /**
     * @return the ruleId
     */
    public int getRuleId()
    {
        return ruleId;
    }

    /**
     * @param ruleId the ruleId to set
     */
    public void setRuleId(int ruleId)
    {
        this.ruleId = ruleId;
    }

    /**
     * @return the levels
     */
    public ArrayList<AbsAlarmLevel> getLevels()
    {
        return levels;
    }

    /**
     * @return the id2level
     */
    public Map<Integer, AbsAlarmLevel> getId2level()
    {
        return id2level;
    }
}
