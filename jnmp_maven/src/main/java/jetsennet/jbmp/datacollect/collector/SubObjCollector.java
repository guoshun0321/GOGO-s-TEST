package jetsennet.jbmp.datacollect.collector;

import java.util.List;
import java.util.Map;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;

/**
 * @author lianghongjie 通用子对象采集器
 */
public class SubObjCollector extends AbsCollector
{
    /*
     * (non-Javadoc)
     * @see jetsennet.jbmp.datacollect.collector.AbsCollector#connect()
     */
    @Override
    public void connect() throws CollectorException
    {
    }

    /*
     * (non-Javadoc)
     * @see jetsennet.jbmp.datacollect.collector.AbsCollector#collect(java.util.List)
     */
    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        return parentColl.collect(objAttrLst, msg);
    }

    @Override
    public void close()
    {
    }
}
