package jetsennet.jbmp.dataaccess.buffer;

import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import jetsennet.jbmp.util.TwoTuple;

/**
 * 缓存消息队列
 * @author xuyuji
 */
public final class BufferMsgQueue
{
    /**
     * 无阻塞的无限队列（LinkedBlockingQueue）
     */
    private LinkedBlockingQueue<TwoTuple<Method, Object>> queue;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BufferMsgQueue.class);
    /**
     * 单例
     */
    private static BufferMsgQueue instance = new BufferMsgQueue();

    /**
     * 构造方法
     */
    private BufferMsgQueue()
    {
        queue = new LinkedBlockingQueue<TwoTuple<Method, Object>>();
    }

    /**
     * 获取单例
     * @return 单例
     */
    public static BufferMsgQueue getInstance()
    {
        return instance;
    }

    /**
     * 插入数据，如果队列已满则丢弃数据
     * @param data 数据
     */
    public void put(TwoTuple<Method, Object> data)
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
    public TwoTuple<Method, Object> get()
    {
        TwoTuple<Method, Object> result = null;
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

    /**
     * 清空
     */
    public void clear()
    {
        queue.clear();
    }
}
