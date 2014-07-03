/************************************************************************
日 期：2012-3-7
作 者: 郭祥
版 本: v1.3
描 述: FTP发现
历 史:
 ************************************************************************/
package jetsennet.jnmp.autodiscovery.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.AutoDisUtil;
import jetsennet.jbmp.autodiscovery.DiscoverException;
import jetsennet.jbmp.autodiscovery.helper.AbsDiscover;
import jetsennet.jbmp.autodiscovery.helper.AutoDisResult;
import jetsennet.jbmp.autodiscovery.helper.ProResult;
import jetsennet.jbmp.autodiscovery.helper.SingleResult;
import jetsennet.jbmp.protocols.ConnectionConfig;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.jnmp.protocols.tcp.FtpPtl;

/**
 * FTP发现
 * @author 郭祥
 */
public class FtpDiscover extends AbsDiscover
{

    /**
     * 需要扫描的IP个数
     */
    private int size;
    /**
     * 默认线程池大小
     */
    protected static final int DEFAULT_POOL_SIZE = 10;
    /**
     * 默认线程池大小
     */
    protected static final int DEFAULT_TIME_OUT = 60 * 1000;
    /**
     * 结果集
     */
    protected final Map<String, Boolean> result;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(FtpDiscover.class);

    /**
     * 构造函数
     */
    public FtpDiscover()
    {
        result = new HashMap<String, Boolean>();
    }

    @Override
    protected AutoDisResult find(AutoDisResult coll, List<SingleResult> irs) throws DiscoverException
    {
        if (irs == null || irs.isEmpty())
        {
            return coll;
        }
        this.size = irs.size();
        String addInfo = coll.getField1();
        int port = ConvertUtil.stringToInt(addInfo, FtpPtl.DEFAULT_PORT);
        this.send(irs, port);
        Set<String> keys = result.keySet();
        for (String key : keys)
        {
            boolean isOpen = result.get(key);
            if (isOpen)
            {
                this.dateHandling(coll.getByIp(key), key, port);
            }
        }
        return coll;
    }

    /**
     * 向IP集合发送HTTP包，HTTP包全部返回或超时后，函数返回
     * @param irs
     */
    private void send(List<SingleResult> irs, int port)
    {
        ExecutorService pool = null;
        try
        {
            List<String> ips = AutoDisUtil.getIpColl(irs);
            int poolSize = irs.size() >= DEFAULT_POOL_SIZE ? DEFAULT_POOL_SIZE : irs.size();
            pool = Executors.newFixedThreadPool(poolSize);
            for (String ip : ips)
            {
                pool.submit(this.getTask(ip, port));
            }

            synchronized (result)
            {
                if (result.size() != this.size)
                {
                    result.wait(DEFAULT_TIME_OUT);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            pool.shutdownNow();
        }
    }

    /**
     * 处理获得的数据
     * @param pdu 获得的PDU
     * @param ip 主机IP
     * @return
     */
    private void dateHandling(SingleResult ir, String ip, int port)
    {
        if (ir == null)
        {
            return;
        }
        ProResult pr = new ProResult(AutoDisConstant.PRO_NAME_FTP);
        // 添加基本信息
        pr.addResult(AutoDisConstant.IS_FTP, 1);
        pr.addResult(AutoDisConstant.PORT, port);

        ir.addProResult(pr);
    }

    protected Runnable getTask(String ip, int port)
    {
        return new FtpRunnable(ip, port);
    }

    /**
     * 在结果集中添加数据
     * @param ip
     * @param isOpen
     */
    private void addResult(String ip, boolean isOpen)
    {
        synchronized (result)
        {
            result.put(ip, isOpen);
            if (result.size() == this.size)
            {
                result.notify();
            }
        }
    }

    private class FtpRunnable implements Runnable
    {

        /**
         * IP地址
         */
        private String ip;
        /**
         * IP端口号
         */
        private int port;

        public FtpRunnable(String ip, int port)
        {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run()
        {
            FtpPtl ptl = new FtpPtl();
            boolean isOpen = ptl.checkConnection(new ConnectionConfig(ip, port));
            addResult(ip, isOpen);
        }
    }
}
