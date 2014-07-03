package jetsennet.jbmp.datacollect.datasource;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.protocols.usb.bfss.UsbConnClient;
import jetsennet.jbmp.protocols.usb.bfss.helper.UsbMessage;

import org.apache.log4j.Logger;

public class BFSSAgent
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BFSSAgent.class);
    // 单例
    private static final BFSSAgent instance = new BFSSAgent();

    private BFSSAgent()
    {
        UsbConnClient.getInstance().setRecHandle(new ReceiveMsgHandleAgent());
    }

    public static BFSSAgent getInstance()
    {
        return instance;
    }

    public void start()
    {
        try
        {
            UsbConnClient.getInstance().start();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public void stop()
    {
        try
        {
            UsbConnClient.getInstance().stop();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 发送消息
     * 
     * @param msg
     * @param timeout
     * @return
     */
    public void send(TransMsg msg) throws Exception
    {
        if (msg == null)
        {
            return;
        }

        int msgId = msg.getMsgId();
        UsbMessage sMsg = UsbConnClient.getInstance().send(msg);
        if (sMsg.getType() == UsbMessage.TYPE_ERROR)
        {
            // 消息发送失败
            logger.debug("BFSS:消息发送失败：" + msgId);
        }
    }
}
