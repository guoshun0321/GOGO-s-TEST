package jetsennet.jnmp.datacollect.collector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collector.AbsCollector;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jnmp.entity.ApacheSysDataEntity;

/**
 * @author lianghongjie Aapche采集器
 */
public class ApacheCollector extends AbsCollector
{
    private static final Logger logger = Logger.getLogger(ApacheCollector.class);

    /**
     * 连接信息
     */
    private HttpURLConnection httpConn;
    private BufferedReader in;

    @Override
    public void connect() throws CollectorException
    {
        try
        {
            URL url = new URL("http://" + mo.getIpAddr() + ":" + mo.getIpPort() + "/server-status?auto");
            httpConn = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        }
        catch (Exception e)
        {
            String msg = mo.getIpAddr() + ":" + mo.getIpPort() + "上的apache监控对象连接失败";
            logger.error(msg);
            throw new CollectorException(msg, e);
        }
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        reset();
        ApacheSysDataEntity entity = new ApacheSysDataEntity();
        entity.setObjId(mo.getObjId());
        entity.setCollTime(time.getTime());
        try
        {
            String temp = "";
            while ((temp = in.readLine()) != null)
            {
                if (temp.startsWith("Total Accesses"))
                {
                    entity.setTotalAccesses((int) getApachePropertyData(temp));
                }
                else if (temp.startsWith("Total kBytes"))
                {
                    entity.setTotalKbytes((int) getApachePropertyData(temp));
                }
                else if (temp.startsWith("Uptime"))
                {
                    entity.setUpTime((int) getApachePropertyData(temp));
                }
                else if (temp.startsWith("ReqPerSec"))
                {
                    entity.setReqPersec(getApachePropertyData(temp));
                }
                else if (temp.startsWith("BytesPerSec"))
                {
                    entity.setBytesPersec(getApachePropertyData(temp));
                }
                else if (temp.startsWith("BytesPerReq"))
                {
                    entity.setBytesPerreq(getApachePropertyData(temp));
                }
                else if (temp.startsWith("BusyWorkers"))
                {
                    entity.setBusyWorkers((int) getApachePropertyData(temp));
                }
                else if (temp.startsWith("IdleWorkers"))
                {
                    entity.setIdleWorkers((int) getApachePropertyData(temp));
                }
            }
        }
        catch (Exception e)
        {
            logger.error("apache采集数据采集失败", e);
        }

        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        Map<Integer, ObjAttribEntity> idMap = toIdMap(objAttrLst);
        generateCollData(result, idMap, mo, entity, CollData.DATATYPE_PERF);
        generateFailedData(result, objAttrLst);
        return result;
    }

    /**
     * 解析数据
     * @param str
     * @return
     */
    private float getApachePropertyData(String str)
    {
        String[] splitStrs = str.split(":");
        if (splitStrs.length == 2)
        {
            return Float.parseFloat(splitStrs[1].trim());
        }
        return 0.0f;
    }

    @Override
    public void close()
    {
        try
        {
            if (in != null)
            {
                in.close();
            }
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        finally
        {
            in = null;
            httpConn = null;
        }
    }
}
