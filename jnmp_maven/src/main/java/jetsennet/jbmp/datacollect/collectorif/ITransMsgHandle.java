package jetsennet.jbmp.datacollect.collectorif;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;

/**
 * 处理采集结果
 * @author 郭祥
 */
public interface ITransMsgHandle
{

    /**
     * 处理采集结果
     * @param msg 参数
     */
    public void handleTransMsg(TransMsg msg);

}
