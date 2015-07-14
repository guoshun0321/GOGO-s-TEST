package jetsennet.jbmp.datacollect.datasource;

import org.apache.log4j.Logger;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.protocols.usb.bfss.IReceiveMsgHandle;
import jetsennet.jbmp.protocols.usb.bfss.helper.UsbMessage;
import jetsennet.jbmp.protocols.usb.bfss.helper.UsbMessageResponse;

/**
 * 处理接收到的数据，位于代理采集器端。
 * 
 * @author 郭祥
 */
public class ReceiveMsgHandleAgent implements IReceiveMsgHandle
{

    private static final Logger logger = Logger.getLogger(ReceiveMsgHandleAgent.class);

    @Override
    public void handle(UsbMessage msg)
    {
        if (msg == null)
        {
            return;
        }
        Object obj = msg.getTransObj();
        if (obj instanceof TransMsg)
        {
            TransMsg transObj = (TransMsg) obj;
            int collType = transObj.getCollType();
            switch (collType)
            {
            case TransMsg.COLL_TYPE_COLL:
            case TransMsg.COLL_TYPE_INS:
                DataCollAgent.getInstance().add(transObj);
                break;
            case TransMsg.COLL_TYPE_REC:
                logger.error("rec类型消息：" + collType);
                break;
            case TransMsg.COLL_TYPE_INS_SUB:
                DataCollAgent.getInstance().add(transObj);
                break;
            default:
                logger.error("未知TransMsg类型：" + collType);
                break;
            }
        }
        else
        {
            logger.error("未知数据类型，丢弃USB数据：" + obj);
        }
    }

    @Override
    public void handleError(UsbMessageResponse arg0)
    {
        // TODO Auto-generated method stub
    }

}
