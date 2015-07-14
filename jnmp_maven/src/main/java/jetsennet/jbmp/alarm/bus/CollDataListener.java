/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 从数据总线上获取数据。
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.bus;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.AlarmConfigColl;
import jetsennet.jbmp.alarm.handle.CollDataDispatch;
import jetsennet.jbmp.manage.LifecycleBase;

import org.apache.log4j.Logger;

/**
 * 获取采集数据模块
 * @author 郭祥
 */
public class CollDataListener extends LifecycleBase
{

    /**
     * 数据总线
     */
    private CollDataBus bus;
    /**
     * 线程池
     */
    private CollDataDispatch dhtpm;
    /**
     * 报警配置文件
     */
    private AlarmConfigColl configs;
    /**
     * 模块状态标志位
     */
    private boolean isStop = false;
    /**
     * 线程结束标志
     */
    private Future<Integer> endFlag;
    private ExecutorService single;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(CollDataListener.class);

    /**
     * 构造函数
     */
    public CollDataListener()
    {
        // 数据总线
        bus = CollDataBus.getInstance();
        // 获取报警配置
        configs = AlarmConfigColl.getInstance();
        // 数据分发
        dhtpm = CollDataDispatch.getInstance();
    }

    @Override
    protected void startInternal() throws Exception
    {
        dhtpm.start();
        single = Executors.newSingleThreadExecutor();
        endFlag = single.submit(new GetCollDataCallable());
        logger.info("报警模块：开始从数据总线上获取数据。");
    }

    @Override
    protected void stopInternal() throws Exception
    {
        bus.put(this.newEmptyCollData());
        endFlag.get(1, TimeUnit.SECONDS); // 1s超时
        single.shutdown();
        dhtpm.stop();
        logger.info("报警模块：停止从数据总线上获取数据。");
    }

    /**
     * 生成空数据，用于线程唤醒
     * @return
     */
    private CollData newEmptyCollData()
    {
        CollData result = new CollData();
        result.dataType = CollData.DATATYPE_EMPTY;
        return result;
    }

    class GetCollDataCallable implements Callable<Integer>
    {

        @Override
        public Integer call() throws Exception
        {
            while (!isStop)
            {
                try
                {
                    CollData data = bus.get();
                    if (data == null || data.dataType == CollData.DATATYPE_EMPTY)
                    {
                        continue;
                    }
                    AlarmConfig config = configs.get(data);
                    if (config == null)
                    {
                        logger.warn("报警模块：丢弃数据：" + data.toString() + "；找不到类型：" + data.dataType + " 对应的配置信息。");
                    }
                    else
                    {
                        dhtpm.submit(config, data);
                    }
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
            logger.info("报警模块：数据获取线程返回。");
            return 1;
        }
    }
}
