package jetsennet.jbmp.trap.receiver;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.datacollect.datasource.BFSSAgent;

import org.apache.log4j.Logger;

public class TrapPduHandleAgent extends TrapPduHandle
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TrapPduHandleAgent.class);

    public TrapPduHandleAgent()
    {
    }

    @Override
    protected void handleCollData(CollData trapData)
    {
        try
        {
            TransMsg trans = new TransMsg();
            trans.setCollType(TransMsg.COLL_TYPE_REC);
            trans.setRecInfo(trapData);
            BFSSAgent.getInstance().send(trans);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }
}
