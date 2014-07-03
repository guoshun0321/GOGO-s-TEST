/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 采集数据分发
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.manage.LifecycleBase;

/**
 * 采集数据分发
 * @author 郭祥
 */
public final class CollDataDispatch extends LifecycleBase
{

    /**
     * 线程池组
     */
    private ConcurrentHashMap<String, DataHandleThreadPool> pools;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(CollDataDispatch.class);
    private static final CollDataDispatch instance = new CollDataDispatch();

    private CollDataDispatch()
    {
    }

    public static CollDataDispatch getInstance()
    {
        return instance;
    }

    @Override
    protected void startInternal() throws Exception
    {
        pools = new ConcurrentHashMap<String, DataHandleThreadPool>();
        logger.info("报警模块：开始数据处理模块。");
    }

    @Override
    protected void stopInternal() throws Exception
    {

        this.pools = null;
        if (pools != null)
        {
            Set<String> keys = pools.keySet();
            for (String key : keys)
            {
                pools.get(key).stop();
            }
        }
        logger.info("报警模块：停止所有报警线程池。");
        logger.info("报警模块：结束数据处理模块。");
    }

    /**
     * 向线程池提交报警数据
     * @param config 参数
     * @param data 参数
     */
    public void submit(AlarmConfig config, CollData data)
    {
        try
        {
            DataHandleThreadPool pool = this.getPool(config);
            if (pool != null)
            {
                pool.submit(data);
            }
            else
            {
                logger.error(String.format("报警模块：无法获取相应的线程池<%s>，丢弃数据：%s", config.getPoolName(), data.toString()));
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 获取线程池，线程池不存在时新建线程池。
     * @param config
     * @return
     */
    private DataHandleThreadPool getPool(AlarmConfig config)
    {
        DataHandleThreadPool retval = null;
        String poolName = config.getPoolName();
        retval = pools.get(config.getPoolName());
        if (retval == null)
        {
            retval = new DataHandleThreadPool(config);
            DataHandleThreadPool temp = pools.putIfAbsent(poolName, retval);
            if (temp != null)
            {
                retval = temp;
            }
            else
            {
                logger.info("报警模块：新建报警线程池：" + poolName);
            }

        }
        return retval;
    }

    /**
     * 线程池快照
     * @return 结果
     */
    public String snapshot()
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Date now = new Date();
            sb.append(now.toString());
            sb.append("线程池快照：");
            if (pools != null && !pools.isEmpty())
            {
                Set<String> keys = pools.keySet();
                for (String key : keys)
                {
                    DataHandleThreadPool pool = pools.get(key);
                    sb.append("\n");
                    sb.append(pool.snapshot());
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return sb.toString();
    }
}
