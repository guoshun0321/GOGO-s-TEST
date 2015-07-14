package jetsennet.jbmp.datacollect.datasource;

import java.util.HashMap;
import java.util.Map;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

public class DataAgentRemoteUsb implements IDataAgent
{

    /**
     * USB连接超时
     */
    private static final int USB_TIMEOUT = 20000;

    private static final Logger logger = Logger.getLogger(DataAgentRemoteUsb.class);

    @Override
    public TransMsg getData(TransMsg msg)
    {
        logger.debug("开始通过USB线获取数据");
        TransMsg retval = null;
        try
        {
            if (msg != null)
            {
                msg.setCollType(TransMsg.COLL_TYPE_COLL);
                TransMsg retMsg = BFSSCollector.getInstance().synGet(msg, USB_TIMEOUT);
                if (retMsg == null)
                {
                    retval = new TransMsg();
                    retval.setCollState(TransMsg.COLL_STATE_FAILD);
                    retval.setMsg("通过USB数据线获取数据失败。");
                    logger.debug("通过USB数据线获取数据失败。");
                }
                else if (retMsg.getCollState() == TransMsg.COLL_STATE_FAILD)
                {
                    retval = retMsg;
                    logger.debug("通过USB数据线发送数据失败。");
                }
                else
                {
                    retval = retMsg;
                    logger.debug("通过USB数据线获取数据成功，消息状态：" + retval.getCollState());
                }
            }
            else
            {
                retval = new TransMsg();
                retval.setCollState(TransMsg.COLL_STATE_FAILD);
                retval.setMsg("传入信息为NULL。");
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = new TransMsg();
            retval.setCollState(TransMsg.COLL_STATE_FAILD);
            retval.setMsg(ex.getMessage());
        }
        logger.debug("通过USB线获取数据结束");
        return retval;
    }

    @Override
    public TransMsg getDataForIns(TransMsg msg)
    {
        if (msg == null)
        {
            throw new NullPointerException();
        }
        TransMsg retval = null;
        try
        {
            msg.setCollType(TransMsg.COLL_TYPE_INS);
            retval = BFSSCollector.getInstance().synGet(msg, USB_TIMEOUT);
            if (retval.getCollState() == TransMsg.COLL_STATE_FAILD)
            {
                retval = null;
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = new TransMsg();
            retval.setCollState(TransMsg.COLL_STATE_FAILD);
            retval.setMsg(ex.getMessage());
        }
        return retval;
    }

    @Override
    public Map<String, Map<String, VariableBinding>> snmpGetSubInfo(MObjectEntity mo, String[] oids)
    {
        Map<String, Map<String, VariableBinding>> retval = null;
        logger.debug("开始通过USB线获取子对象数据");
        try
        {
            TransMsg msg = new TransMsg();
            msg.setMo(mo);
            msg.setRecInfo(oids);
            msg.setCollType(TransMsg.COLL_TYPE_INS_SUB);
            TransMsg retMsg = BFSSCollector.getInstance().synGet(msg, USB_TIMEOUT);

            if (retMsg == null)
            {
                logger.debug("通过USB数据线获取子对象数据失败。");
            }
            else if (retMsg.getCollState() == TransMsg.COLL_STATE_FAILD)
            {
                logger.debug("通过USB数据线发送子对象数据失败。");
            }
            else
            {
                Object transObj = retMsg.getRecInfo();
                if (transObj != null && transObj instanceof Map)
                {
                    retval = (Map<String, Map<String, VariableBinding>>) transObj;
                }
                logger.debug("通过USB数据线获取子对象数据成功，消息状态：" + retMsg.getCollState());
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        logger.debug("通过USB线获取子对象数据结束");
        if (retval == null)
        {
            retval = new HashMap<String, Map<String, VariableBinding>>();
        }
        return retval;
    }
}
