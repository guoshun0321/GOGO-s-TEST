/************************************************************************
日 期: 2012-3-6
作 者: 郭祥
版 本: v1.3
描 述: 采集数据总线
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.bus;

import java.util.concurrent.LinkedBlockingQueue;

import jetsennet.jbmp.manage.MBeanRegister;

import org.apache.log4j.Logger;

/**
 * 采集数据总线，采用无阻塞的无限队列（LinkedBlockingQueue）
 * @author 郭祥
 */
public final class CollDataBus implements CollDataBusMBean
{

    private LinkedBlockingQueue<CollData> queue;
    private static final Logger logger = Logger.getLogger(CollDataBus.class);
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static final CollDataBus instance = new CollDataBus();

    private CollDataBus()
    {
        queue = new LinkedBlockingQueue<CollData>();

        try
        {
            MBeanRegister.register("JBMP.CollDataBus:type=monitor", this);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public static CollDataBus getInstance()
    {
        return instance;
    }

    // </editor-fold>

    /**
     * 插入数据，如果队列已满则丢弃数据
     * @param data 数据
     */
    public void put(CollData data)
    {
        if (data != null && !queue.offer(data))
        {
            logger.error("报警模块：丢弃数据：" + data.toString());
        }
    }

    /**
     * 获取数据，在无法获取数据时阻塞
     * @return 结果
     */
    public CollData get()
    {
        CollData result = null;
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

    public int getSize()
    {
        return queue.size();
    }
}
