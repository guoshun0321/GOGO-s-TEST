/************************************************************************
日 期: 2012-3-15
作 者: 郭祥
版 本: v1.3
描 述: 报警时间总线
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.bus;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 采集数据总线，采用无阻塞的无限队列（LinkedBlockingQueue）
 * @author 郭祥
 */
public final class AlarmEventDataBus
{

    private LinkedBlockingQueue<AlarmEventEntity> queue;
    private static final Logger logger = Logger.getLogger(AlarmEventDataBus.class);
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static final AlarmEventDataBus instance = new AlarmEventDataBus();

    private AlarmEventDataBus()
    {
        queue = new LinkedBlockingQueue<AlarmEventEntity>();
    }

    public static AlarmEventDataBus getInstance()
    {
        return instance;
    }

    // </editor-fold>

    /**
     * 插入数据，如果队列已满则丢弃数据
     * @param data 数据
     */
    public void put(AlarmEventEntity data)
    {
        if (data != null && !queue.offer(data))
        {
            logger.error("报警模块：丢弃数据：" + data.toString());
        }
    }

    /**
     * @param aees 值
     */
    public void put(List<AlarmEventEntity> aees)
    {
        if (aees == null || aees.isEmpty())
        {
            return;
        }
        for (int i = 0; i < aees.size(); i++)
        {
            this.put(aees.get(i));
        }
    }

    /**
     * 获取数据，在无法获取数据时阻塞
     * @return 结果
     */
    public AlarmEventEntity get()
    {
        AlarmEventEntity result = null;
        try
        {
            result = queue.take();
        }
        catch (InterruptedException ex)
        {
            logger.error(ex);
        }
        return result;
    }
}
