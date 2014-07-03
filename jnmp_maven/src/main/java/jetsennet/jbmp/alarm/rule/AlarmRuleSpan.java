package jetsennet.jbmp.alarm.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.util.TaskUtil;

public class AlarmRuleSpan extends AbsAlarmRule
{

    public AlarmRuleSpan(AlarmEntity alarm) throws AlarmRuleConstructionException
    {
        super(alarm);
    }

    @Override
    public List<AlarmEventEntity> genAlarmEvent(AbsHistoryData data, Object obj, int objId, int attrId, int objAttrId, String desc)
    {
        List<AlarmEventEntity> retval = new ArrayList<AlarmEventEntity>();
        if (data instanceof HistoryDataSpan)
        {
            AlarmEventEntity event = genAlarm((HistoryDataSpan) data, objId, attrId, objAttrId);
            if (event != null)
            {
                retval.add(event);
            }
        }
        return retval;
    }

    @Override
    public int sizeOfData()
    {
        return Integer.MIN_VALUE;
    }

    /**
     * 生成报警
     * @param ad 参数
     * @param objId 对象ID
     * @param objAttrId 对象属性ID
     * @param attrId 属性ID
     * @return 结果
     */
    public static AlarmEventEntity genAlarm(HistoryDataSpan data, int objId, int attrId, int objAttrId)
    {
        AlarmEventEntity retval = null;

        // 报警规则
        AbsAlarmRule rule = data.getRule();

        // 最后添加的数据，也就是当次需要判断的数据
        TimeQueue<HistoryDataEntry> datas = data.getQueue();
        HistoryDataEntry lastData = datas.getLast();

        // 确定报警级别
        AbsAlarmLevel tempLevel = ensureLevel(rule, datas);

        if (tempLevel != null)
        {
            retval = AlarmGenUtil.genAlarmEvent(objId, attrId, objAttrId, rule, tempLevel, lastData);
        }
        return retval;
    }

    /**
     * 确定报警级别。先根据时间掩码进行过滤，再根据级别设定的阀值进行过滤。 返回满足以上条件的Level中级别最高的一条规则。
     * @param rule
     * @param datas
     */
    protected static AbsAlarmLevel ensureLevel(AbsAlarmRule rule, TimeQueue<HistoryDataEntry> datas)
    {
        AbsAlarmLevel retval = null;
        int overNum = rule.getAlarm().getOverNum();
        int levelSize = rule.getLevelSize();
        int[] nums = new int[levelSize];
        Date now = new Date();
        for (int i = 0; i < levelSize; i++)
        {
            AbsAlarmLevel aal = rule.getLevels().get(i);

            // 过滤掉不满足时间条件的级别
            if (!TaskUtil.validWeek(aal.getLevel().getWeekMask(), aal.getLevel().getHourMask(), now))
            {
                continue;
            }
            HistoryDataEntry temp = null;

            // 遍历获取满足报警条件的数据次数M
            datas.resetPos();
            while ((temp = datas.next()) != null)
            {
                if (aal.isAlarm(temp.value) != null)
                {
                    nums[i]++;
                }
            }
            // M大于规定的次数
            if (nums[i] >= overNum)
            {
                // tempLevel报警不存在报警或当前报警的级别大于tempLevel的级别
                if (retval == null || retval.getLevel().compare(aal.getLevel()) != 1)
                {
                    retval = aal;
                }
            }
        }
        return retval;
    }

}
