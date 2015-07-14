package jetsennet.jbmp.trap.receiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jetsennet.jbmp.trap.util.TrapConfigs;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * 监听发送到本机的SNMP Trap信息
 * @author Administrator
 */
public final class TrapReceiver
{

    /**
     * 线程池
     */
    private ExecutorService pool;
    /**
     * 编码、解码
     */
    private MessageDispatcher dispatcher;
    /**
     * 收发信息
     */
    private TransportMapping trans;
    /**
     * SNMP操作执行
     */
    private Snmp snmp;
    /**
     * Trap处理接口
     */
    private CommandResponder trapProcess;
    /**
     * 状态标记
     */
    private boolean isStart;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TrapReceiver.class);

    // 单例
    private static TrapReceiver instance = new TrapReceiver();

    private TrapReceiver()
    {
        this.trapProcess = new TrapPduHandle();
    }

    public static TrapReceiver getInstance()
    {
        return instance;
    }

    /**
     * 启动
     */
    public synchronized void start()
    {
        if (isStart())
        {
            return;
        }
        try
        {
            initMessageDispatcher();
            initTransportMapping();
            initSnmp();
            snmp.listen();
            isStart = true;
        }
        catch (Exception ex)
        {
            this.isStart = false;
            logger.error("Trap接收启动失败。", ex);
        }
        logger.info("开始接收Trap信息");
    }

    /**
     * 初始化消息分发
     */
    private void initMessageDispatcher()
    {
        int threadPoolSize = TrapConfigs.getInstance().poolSize;
        pool = Executors.newFixedThreadPool(threadPoolSize);
        dispatcher = new ThreadPoolMessageDispatcher(pool, new MessageDispatcherImpl());
    }

    /**
     * 初始化Trap接收端口
     */
    private void initTransportMapping()
    {
        try
        {
            String ip = TrapConfigs.getInstance().ip;
            String trapProtocol = TrapConfigs.getInstance().protocol;
            int trapPort = TrapConfigs.getInstance().port;
            String addr = trapProtocol + ":" + ip + "/" + trapPort;
            logger.info("监听地址：" + addr);
            Address listenAddress = GenericAddress.parse(addr);
            if (listenAddress instanceof UdpAddress)
            {
                trans = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
            }
            else
            {
                trans = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 初始化snmp协议
     */
    private void initSnmp()
    {
        snmp = new Snmp(dispatcher, trans);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
        snmp.addCommandResponder(trapProcess);
    }

    /**
     * 停止
     */
    public synchronized void stop()
    {
        if (!isStart())
        {
            return;
        }
        // 停止线程池
        try
        {
            if (pool != null)
            {
                pool.shutdown();
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            pool = null;
        }
        // 停止Trap捕获线程
        try
        {
            if (snmp != null)
            {
                snmp.close();
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            snmp = null;
        }
        isStart = false;
        logger.info("停止接收Trap信息");
    }

    public boolean isStart()
    {
        return isStart;
    }

    /**
     * @param isStart 参数
     */
    public void setStart(boolean isStart)
    {
        this.isStart = isStart;
    }

    /**
     * 设置Trap处理接口，在start前调用
     * @param trapProcess
     */
    public void setTrapProcess(CommandResponder trapProcess)
    {
        this.trapProcess = trapProcess;
    }

    /**
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        TrapReceiver.getInstance().start();
        String input = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (input.startsWith("E") || input.startsWith("e"))
        {
            TrapReceiver.getInstance().stop();
        }
    }
}
