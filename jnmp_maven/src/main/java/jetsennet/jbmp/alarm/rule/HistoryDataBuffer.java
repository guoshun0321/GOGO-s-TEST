/************************************************************************
日 期: 2012-1-12
作 者: 郭祥
版 本: v1.3
描 述: 管理对象属性历史数据
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jetsennet.jbmp.alarm.eventhandle.AlarmEventDispatch;

import org.apache.log4j.Logger;

/**
 * 管理对象属性历史数据
 * @author 郭祥
 */
public final class HistoryDataBuffer
{

    /**
     * 对象属性对应数据缓存
     */
    private Map<Integer, AbsHistoryData> id2buf;
    /**
     * 规则管理
     */
    private AlarmRuleManager arm;
    /**
     * 报警事件处理，在规则变化是需要发送一条规则变化报警
     */
    private AlarmEventDispatch aeh;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(HistoryDataBuffer.class);
    // 单例
    private static HistoryDataBuffer instance = new HistoryDataBuffer();

    private HistoryDataBuffer()
    {
        id2buf = new HashMap<Integer, AbsHistoryData>();
        arm = AlarmRuleManager.getInstance();
        aeh = AlarmEventDispatch.getInstance();
    }

    public static HistoryDataBuffer getInstance()
    {
        return instance;
    }

    /**
     * @param objAttrId 对象属性ID
     * @param type 类型
     * @return 结果
     */
    public synchronized AbsHistoryData getAlarmData(int objAttrId)
    {
        AbsHistoryData retval = null;
        try
        {
            retval = id2buf.get(objAttrId); // 当前缓存中的数据
            AbsAlarmRule rule = arm.getRuleByObjAttrId(objAttrId); // 规则缓存中的RULE
            if (retval == null)
            {
                if (rule != null)
                {
                    // 数据缓存中没有，规则缓存中有，新增
                    retval = this.genNewRecord(objAttrId, rule);
                    id2buf.put(objAttrId, retval);
                    logger.debug("报警模块：新增AlarmData，对象属性ID：" + objAttrId);
                }
            }
            else
            {
                // 规则缓存中存在
                if (rule != null)
                {
                    AbsAlarmRule temp = retval.getRule();

                    // 当前缓存中的报警规则已经被更新
                    if (temp.getRuleId() != rule.getRuleId())
                    {
                        // 更新
                        retval = this.genNewRecord(objAttrId, rule);
                        id2buf.put(objAttrId, retval);

                        // 规则改变后，移除该对象属性上一次报警
                        aeh.removeLast(objAttrId, new Date().getTime());
                        logger.debug("报警模块：更新AlarmData，对象属性ID：" + objAttrId);
                    }
                }
                else
                {
                    // 规则缓存中不存在，清理当前缓存
                    id2buf.remove(Integer.valueOf(objAttrId));
                    retval = null;
                    logger.debug("报警模块：移除AlarmData，对象属性ID：" + objAttrId);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }

        return retval;
    }

    /**
     * 生成新的历史数据对象
     * 
     * @param objAttrid
     * @param rule
     * @return
     */
    private AbsHistoryData genNewRecord(int objAttrId, AbsAlarmRule rule)
    {
        AbsHistoryData retval = null;
        if (rule instanceof AlarmRuleTime)
        {
            retval = new HistoryDataTime(objAttrId, rule);
        }
        else if (rule instanceof AlarmRuleSpan)
        {
            retval = new HistoryDataSpan(objAttrId, rule);
        }
        return retval;
    }
}
