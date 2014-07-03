/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 默认报警规则
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.util.TaskUtil;

import org.apache.log4j.Logger;

/**
 * 默认的报警规则
 * @author 郭祥
 */
public class AlarmRuleTime extends AbsAlarmRule
{

    /**
     * 日志
     */
    private Logger logger = Logger.getLogger(AlarmRuleTime.class);

    public AlarmRuleTime(AlarmEntity alarm) throws Exception
    {
        super(alarm);
    }

    @Override
    public List<AlarmEventEntity> genAlarmEvent(AbsHistoryData data, Object obj, int objId, int attrId, int objAttrId, String desc)
    {
        List<AlarmEventEntity> retval = new ArrayList<AlarmEventEntity>();
        if (data instanceof HistoryDataTime)
        {
            AlarmEventEntity event = genAlarm((HistoryDataTime) data, objId, attrId, objAttrId);
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
        return alarm.getCheckNum();
    }

    /**
     * 生成报警
     * 
     * @param ad 参数
     * @param objId 对象ID
     * @param objAttrId 对象属性ID
     * @param attrId 属性ID
     * @return 结果
     */
    public static AlarmEventEntity genAlarm(HistoryDataTime data, int objId, int attrId, int objAttrId)
    {
        AlarmEventEntity retval = null;

        // 报警规则
        AbsAlarmRule rule = data.getRule();

        // 最后添加的数据，也就是当次需要判断的数据
        CircularArray<HistoryDataEntry> datas = data.getDatas();
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
     * 
     * @param rule
     * @param datas
     */
    protected static AbsAlarmLevel ensureLevel(AbsAlarmRule rule, CircularArray<HistoryDataEntry> datas)
    {
        AbsAlarmLevel retval = null;
        int overNum = rule.getAlarm().getOverNum();
        int[] nums = new int[rule.getLevelSize()];
        Date now = new Date();
        for (int i = 0; i < nums.length; i++)
        {
            AbsAlarmLevel aal = rule.getLevels().get(i);
            // 过滤时间
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
