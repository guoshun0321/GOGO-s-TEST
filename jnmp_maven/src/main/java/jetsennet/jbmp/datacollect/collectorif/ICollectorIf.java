package jetsennet.jbmp.datacollect.collectorif;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.exception.CollectorException;

/**
 * @author lianghongjie 只采一次采集器接口
 */
public interface ICollectorIf
{
    /**
     * 设置监控对象
     * @param mo 对象
     */
    public void setMonitorObject(MObjectEntity mo);

    /**
     * 连接监控对象
     * @throws CollectorException 异常
     */
    public void connect() throws CollectorException;

    /**
     * 采集数据
     * @param tranMsg 参数
     * @return 结果
     */
    public TransMsg collect(TransMsg tranMsg);

    /**
     * 关闭连接
     */
    public void close();
}
