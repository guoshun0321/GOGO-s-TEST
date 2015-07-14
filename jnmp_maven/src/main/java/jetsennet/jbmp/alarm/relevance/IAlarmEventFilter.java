package jetsennet.jbmp.alarm.relevance;

import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 报警事件过滤接口
 * 
 * @author 郭祥
 */
public interface IAlarmEventFilter
{
    /**
     * 报警事件过滤
     * 
     * @param event
     * @return 成功，返回true；失败，返回false
     */
    public boolean filter(AlarmEventEntity event);
}
