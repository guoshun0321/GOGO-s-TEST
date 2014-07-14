package jetsennet.jnmp.datacollect.collector;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.datacollect.collector.AbsCollector;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jbmp.protocols.ConnectionConfig;
import jetsennet.jnmp.protocols.tcp.HttpPtl;

import org.apache.log4j.Logger;

/**
 * @author lianghongjie Http采集器
 */
public class HttpCollector extends AbsCollector
{
    private static final Logger logger = Logger.getLogger(HttpCollector.class);

    @Override
    public void connect() throws CollectorException
    {
        HttpPtl http = new HttpPtl();
        if (!http.checkConnection(new ConnectionConfig(mo.getIpAddr(), mo.getIpPort())))
        {
            String msg = mo.getIpAddr() + ":" + mo.getIpPort() + "上的HTTP监控对象连接失败";
            logger.error(msg);
            throw new CollectorException(msg);
        }
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        return result;
    }

    @Override
    public void close()
    {
    }
}
