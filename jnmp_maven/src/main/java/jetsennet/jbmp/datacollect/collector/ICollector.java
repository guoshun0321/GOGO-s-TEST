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

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;

/**
 * 采集器接口
 * @author 梁宏杰
 */
public interface ICollector
{
    /**
     * 设置监控对象
     * @param mo 监控对象
     */
    public void setMonitorObject(MObjectEntity mo);

    /**
     * 设置父采集器
     * @param parentColl 父采集器
     */
    public void setParentCollector(ICollector parentColl);

    /**
     * @return 父采集器
     */
    public ICollector getParentCollector();

    /**
     * 连接监控对象
     * @throws CollectorException 异常
     */
    public void connect() throws CollectorException;

    /**
     * 采集数据。
     * @param objAttrLst 参数
     * @param msg 参数
     * @return 结果 对象属性值存在时，返回CollData;对象值为多个时，返回List;对象属性值不存在时，返回null;对象属性值由于特殊原因（如：公式等）无法获取时，返回Double.NaN。
     */
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg);

    /**
     * 获取实例化数据
     * @param msg
     * @return
     */
    public TransMsg collectForIns(TransMsg msg);

    /**
     * 关闭连接
     */
    public void close();
}
