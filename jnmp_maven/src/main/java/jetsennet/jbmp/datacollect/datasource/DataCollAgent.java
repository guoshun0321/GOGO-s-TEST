package jetsennet.jbmp.datacollect.datasource;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

/**
 * 代理采集器端数据采集。
 * 
 * @author 郭祥
 */
public class DataCollAgent
{

    /**
     * 状态
     */
    private boolean isStart;
    /**
     * 数据处理线程池
     */
    private ExecutorService service;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DataCollAgent.class);
    // 单例
    private static final DataCollAgent instance = new DataCollAgent();

    private DataCollAgent()
    {
        this.isStart = false;
    }

    public static DataCollAgent getInstance()
    {
        return instance;
    }

    /**
     * 开始
     */
    public synchronized void start()
    {
        if (isStart)
        {
            return;
        }
        try
        {
            // 初始化线程池
            service = Executors.newFixedThreadPool(20);

            // 修改状态
            this.isStart = true;
        }
        catch (Exception ex)
        {
            this.isStart = false;
            logger.error("启动代理采集器数据采集模块失败。", ex);
        }
        logger.info("启动代理采集器数据采集模块成功。");
    }

    /**
     * 结束
     */
    public synchronized void stop()
    {
        if (isStart)
        {
            service.shutdown();
            this.isStart = false;
            logger.info("准备关闭数据代理采集模块。");
        }
    }

    /**
     * 添加需要采集的数据
     * 
     * @param trans
     */
    public synchronized void add(TransMsg trans)
    {
        if (isStart)
        {
            service.submit(new HandleRecCollThread(trans));
        }
        else
        {
            logger.info("代理采集关闭，丢弃数据：" + trans.getMsgId());
        }
    }

    /**
     * 处理收到的数据，并将数据放到输出缓存
     * 
     * @author 郭祥
     */
    private class HandleRecCollThread implements Runnable
    {

        private TransMsg trans;

        public HandleRecCollThread(TransMsg msg)
        {
            this.trans = msg;
        }

        @Override
        public void run()
        {
            try
            {
                DataAgent agent = new DataAgent();

                TransMsg result = null;
                if (trans.getCollType() == TransMsg.COLL_TYPE_COLL)
                {
                    // 采集
                    result = agent.getData(trans);
                }
                else if (trans.getCollType() == TransMsg.COLL_TYPE_INS)
                {
                    // 实例化
                    result = agent.getDataForIns(trans);
                }
                else if (trans.getCollType() == TransMsg.COLL_TYPE_INS_SUB)
                {
                    logger.debug("代理采集端：准备获取子对象数据。");
                    result = trans;
                    Object infoObj = trans.getRecInfo();
                    if (infoObj != null && infoObj instanceof String[])
                    {
                        MObjectEntity mo = trans.getMo();
                        Map<String, Map<String, VariableBinding>> snmpVal = agent.snmpGetSubInfo(mo, (String[]) infoObj);
                        logger.debug("代理采集端：子对象获取结果：" + snmpVal);
                        result.setRecInfo(snmpVal);
                    }
                }
                if (result != null)
                {
                    BFSSAgent.getInstance().send(result);
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }

}
