/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * @author xdh
 */
public final class ActionUtil
{
    private static final Logger logger = Logger.getLogger(ActionUtil.class);

    private ActionUtil()
    {
    }

    /**
     * 循环处理报警
     * @param alarmEvents 参数
     * @throws Exception 异常
     */
    public static void processAlarm(List<AlarmEventEntity> alarmEvents) throws Exception
    {
        try
        {
            logger.info("开始处理报警动作");
            if (alarmEvents != null && alarmEvents.size() > 0)
            {
                for (AlarmEventEntity alarm : alarmEvents)
                {
                    AlarmActionBase.getInstance().handle(alarm);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }

        logger.info("处理完毕");
    }
}
