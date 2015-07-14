package jetsennet.jbmp.datacollect.datasource;

import java.util.Map;

import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;

import org.snmp4j.smi.VariableBinding;

/**
 * 数据源类
 * @author 郭祥
 */
public interface IDataAgent
{

    /**
     * 获取数据，用于数据采集
     * @param msg 参数
     * @return 结果
     */
    public TransMsg getData(TransMsg msg);

    /**
     * 获取数据，用于实例化。目前仅SNMP协议需要。
     * @param msg 参数
     * @return 结果
     */
    public TransMsg getDataForIns(TransMsg msg);

    /**
     * 获取信息，主要用于扩展
     * @param obj
     * @return
     */
    public Map<String, Map<String, VariableBinding>> snmpGetSubInfo(MObjectEntity mo, String[] oids);

}
