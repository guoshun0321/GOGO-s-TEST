/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 默认数据处理
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jetsennet.jbmp.alarm.eventhandle.AlarmEventDispatch;
import jetsennet.jbmp.entity.AlarmEventEntity;

import org.apache.log4j.Logger;

/**
 * 默认报警产生。 这里使用单例和同步保证报警产生和报警发送的同步
 * 
 * @author 郭祥
 */
public final class DataHandleDef
{
    /**
     * 报警产生
     */
    public static final int OP_NUM_GEN = 0;
    /**
     * 报警恢复
     */
    public static final int OP_NUM_RECOVER = 1;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DataHandleDef.class);

    private static DataHandleDef instance = new DataHandleDef();

    private DataHandleDef()
    {
    }

    public static DataHandleDef getInstance()
    {
        return instance;
    }

    /**
     * 处理数据，产生报警，发送报警 这里的同步用来保证先产生的报警先发送
     * @param value 采集值
     * @param time 采集时间
     * @param handleCls 事件处理类 
     * @param data 历史数据
     * @param oEvents 参数
     * @param objId 对象ID
     * @param objAttrId 对象属性ID
     * @param attrId 属性ID
     */
    public void handleData(String value, Date time, String handleCls, AbsHistoryData data, ArrayList<AlarmEventEntity> oEvents, int objId,
            int objAttrId, int attrId)
    {
        if (handleCls == null || data == null || data.getRule() == null)
        {
            return;
        }

        // 同步，保证一次处理一个对象属性的一条数据
        synchronized (data)
        {
            try
            {
                // 将数据添加到数据缓存
                data.add(value, time);

                // 产生报警
                List<AlarmEventEntity> events = data.rule.genAlarmEvent(data, null, objId, attrId, objAttrId, null);

                // 如果有产生新的报警，处理新报警，并清空历史记录；如果不产生新报警，尝试恢复原先的报警
                if (events != null && !events.isEmpty())
                {
                    if (events.get(0) != null)
                    {
                        AlarmEventDispatch.getInstance().handleEvent(handleCls, events.get(0));
                    }
                }
                else
                {
                    AlarmEventDispatch.getInstance().resumeLast(objAttrId, new Date().getTime());
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }
}
