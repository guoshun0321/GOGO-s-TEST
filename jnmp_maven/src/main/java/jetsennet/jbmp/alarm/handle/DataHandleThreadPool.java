/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 数据处理线程池
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.bus.CollData;

/**
 * 数据处理线程池
 * @author 郭祥
 */
public class DataHandleThreadPool
{

    /**
     * JAVA线程池
     */
    private ExecutorService pool;
    /**
     * 名称
     */
    private String name;
    /**
     * 报警配置
     */
    private AlarmConfig config;
    /**
     * 状态
     */
    private AtomicBoolean isStop;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DataHandleThreadPool.class);

    /**
     * 构造函数
     * @param config 参数
     */
    public DataHandleThreadPool(AlarmConfig config)
    {
        if (config == null)
        {
            throw new NullPointerException();
        }
        this.config = config;

        // 固定大小的线程池
        pool = Executors.newFixedThreadPool(config.getPoolSize() <= 0 ? 1 : config.getPoolSize());
        name = config.getName();
        isStop = new AtomicBoolean(false);
    }

    /**
     * 提交
     * @param data 数据
     */
    public void submit(CollData data)
    {
        try
        {
            if (!isStop.get())
            {
                pool.submit(new AlarmAnalysisRunnable(data));
            }
            else
            {
                logger.debug("报警模块：线程池未开启。");
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 停止
     */
    public void stop()
    {
        try
        {
            if (isStop.compareAndSet(false, true))
            {
                if (pool != null)
                {
                    pool.shutdown();
                }
            }
            logger.info("报警模块：停止线程池 " + name + "。");
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            pool = null;
            isStop.set(false);
        }
    }

    /**
     * 池大小
     * @return 结果
     */
    public long getSize()
    {
        if (pool == null)
        {
            return 0l;
        }
        else
        {
            return ((ThreadPoolExecutor) pool).getTaskCount();
        }
    }

    /**
     * 组织字符串
     * @return 结果
     */
    public String snapshot()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[pool<");
        sb.append(name);
        sb.append(">:");
        sb.append(this.getSize());
        sb.append("]");

        return sb.toString();
    }

    /**
     * 数据处理
     */
    private class AlarmAnalysisRunnable implements Runnable
    {

        private CollData data;

        public AlarmAnalysisRunnable(CollData data)
        {
            this.data = data;
        }

        @Override
        public void run()
        {
            // 这里进行NULL值判断，后面不再判断
            if (config == null || data == null)
            {
                logger.error("报警模块：输入数据或配置文件为空，丢弃数据。");
                return;
            }
            ICollDataHandle handle = HandleColl.getInstance().get(config.getAnalysisClass());
            if (handle != null)
            {
                handle.handle(data, config);
            }
            else
            {
                logger.debug("报警模块：无法获取数据处理实例：" + config.getAnalysisClass());
            }
        }
    }
}
