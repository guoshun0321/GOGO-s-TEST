package jetsennet.jbmp.datacollect.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.protocols.usb.bfss.UsbConnClient;
import jetsennet.jbmp.protocols.usb.bfss.helper.UsbMessage;
import jetsennet.jbmp.protocols.usb.bfss.state.MachineState;

public class BFSSCollector
{

    /**
     * 消息锁
     */
    private Map<Integer, Object> lockMap;
    /**
     * 结果
     */
    private Map<Integer, TransMsg> retMsgMap;
    /**
     * 状态
     */
    private volatile boolean isStart;
    /**
     * 最低超时时间
     */
    private static final long TIMEOUT = 1500l;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BFSSCollector.class);

    private static final BFSSCollector instance = new BFSSCollector();

    private BFSSCollector()
    {
        this.isStart = false;
        lockMap = new ConcurrentHashMap<Integer, Object>();
        retMsgMap = new HashMap<Integer, TransMsg>();
        UsbConnClient.getInstance().setRecHandle(new ReceiveMsgHandleServer());
    }

    public static BFSSCollector getInstance()
    {
        return instance;
    }

    public synchronized void start()
    {
        try
        {
            if (!isStart)
            {
                UsbConnClient.getInstance().start();
                this.isStart = true;
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public synchronized void stop()
    {
        try
        {
            if (isStart)
            {
                UsbConnClient.getInstance().stop();
                this.isStart = false;
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public synchronized String getState()
    {
        String retval = "采集开始";
        if (isStart)
        {
            int temp = UsbConnClient.getInstance().getState();
            switch (temp)
            {
            case MachineState.STATE_INIT:
                retval = "初始化";
                break;
            case MachineState.INIT_ERROR:
                retval = "初始化失败";
                break;
            case MachineState.INIT_SUC:
                retval = "初始化成功";
                break;
            case MachineState.LINK_ONE:
                retval = "本端已经连接，对端未连接";
                break;
            case MachineState.LINK_TWO:
                retval = "对端已经连接，但软件未开";
                break;
            case MachineState.LINK_OK:
                retval = "连接正常";
                break;
            case MachineState.STATE_POLLING:
                retval = "轮询";
                break;
            case MachineState.STATE_READ:
                retval = "读数据";
                break;
            case MachineState.STATE_PREWRITE:
                retval = "预写数据";
                break;
            case MachineState.STATE_WRITE:
                retval = "写数据";
                break;
            default:
                retval = "未知";
                break;
            }
        }
        else
        {
            retval = "采集关闭";
        }
        return "USB通讯状态：" + retval;
    }

    /**
     * 同步获取数据
     * 
     * @param msg
     * @param timeout
     * @return
     * @throws Exception
     */
    public TransMsg synGet(TransMsg msg, long timeout) throws Exception
    {
        TransMsg retval = null;

        if (msg == null)
        {
            return retval;
        }

        timeout = timeout < TIMEOUT ? TIMEOUT : timeout;

        int msgId = msg.getMsgId();
        Object lockObj = new Object();

        synchronized (lockObj)
        {
            // 添加锁
            lockMap.put(msgId, lockObj);
            // 发送消息
            UsbMessage sMsg = UsbConnClient.getInstance().send(msg);
            if (sMsg.getType() == UsbMessage.TYPE_ERROR)
            {
                // 消息发送失败
                logger.debug("BFSS:消息发送失败：" + msgId);
                lockMap.remove(msgId);
                retval = msg;
                retval.setCollState(TransMsg.COLL_STATE_FAILD);
            }
            else
            {
                logger.debug("BFSS：消息准备进入等待状态：" + msgId);
                // 等待
                lockObj.wait(timeout);
                logger.debug("BFSS：检查数据采集结果：" + msgId);
                // 移除数据
                lockMap.remove(msgId);
                retval = retMsgMap.remove(msgId);
            }
        }
        return retval;
    }

    /**
     * 收到消息时，通知被阻塞的synGet操作
     * 
     * @param msg
     */
    public void notify(TransMsg msg)
    {
        if (msg == null)
        {
            return;
        }
        int msgId = msg.getMsgId();
        Object lockObj = lockMap.get(msgId);

        if (lockObj != null)
        {
            synchronized (lockObj)
            {
                // 二次确认，确保存在相应的锁
                lockObj = lockMap.get(msgId);
                if (lockObj != null)
                {
                    retMsgMap.put(msgId, msg);
                    logger.debug("BFSS：通知消息返回：" + msgId);
                    lockObj.notifyAll();
                }
            }
        }
    }

}
