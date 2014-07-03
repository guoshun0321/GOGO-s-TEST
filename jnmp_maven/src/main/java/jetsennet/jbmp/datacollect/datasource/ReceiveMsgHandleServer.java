package jetsennet.jbmp.datacollect.datasource;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.bus.CollDataBus;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.protocols.usb.bfss.IReceiveMsgHandle;
import jetsennet.jbmp.protocols.usb.bfss.helper.UsbMessage;
import jetsennet.jbmp.protocols.usb.bfss.helper.UsbMessageResponse;

/**
 * 处理接收到的数据，位于采集器端。
 * 
 * @author 郭祥
 */
public class ReceiveMsgHandleServer implements IReceiveMsgHandle
{

    private static final Logger logger = Logger.getLogger(ReceiveMsgHandleServer.class);

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
                BFSSCollector.getInstance().notify(transObj);
                break;
            case TransMsg.COLL_TYPE_REC:
                Object recObj = transObj.getRecInfo();
                if (recObj != null && recObj instanceof CollData)
                {
                    CollData trapData = (CollData) recObj;
                    CollDataBus.getInstance().put(trapData);
                }
                break;
            case TransMsg.COLL_TYPE_INS_SUB:
                BFSSCollector.getInstance().notify(transObj);
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
