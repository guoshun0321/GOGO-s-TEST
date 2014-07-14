/************************************************************************
 日 期：2012-8-20
 作 者: 郭祥 
 版 本: v1.3
 描 述: 
 历 史:
 ************************************************************************/
package jetsennet.jbmp.datacollect.collector;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;

/**
 * 空采集器，不执行任何操作
 * @author 郭祥
 */
public class EmptyCollector extends AbsCollector
{

    private static final Logger logger = Logger.getLogger(EmptyCollector.class);

    /**
     * 构造函数
     */
    public EmptyCollector()
    {
    }

    @Override
    public void connect() throws CollectorException
    {

    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        return null;
    }

    @Override
    public void close()
    {
    }
}
