package jetsennet.jbmp.datacollect.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.datacollect.collectorif.transmsg.OutputEntity;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ScalarMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * @author？
 */
public class DataAgentUtil
{

    /**
     * 将对象属性包装成TransMsg
     * @param mo 对象
     * @param oas 参数
     * @return 结果
     */
    public static TransMsg wrapObjAttrib(MObjectEntity mo, List<ObjAttribEntity> oas)
    {
        if (mo == null || oas == null)
        {
            return null;
        }
        TransMsg retval = new TransMsg();
        retval.setMo(mo);
        for (ObjAttribEntity oa : oas)
        {
            retval.addScalar(oa);
        }
        return retval;
    }

    /**
     * @param msg 参数
     * @return 结果
     */
    public static Map<ObjAttribEntity, Object> unwrapTransMsg(TransMsg msg)
    {
        if (msg == null)
        {
            return null;
        }
        Map<ObjAttribEntity, Object> retval = new HashMap<ObjAttribEntity, Object>();
        ScalarMsg scalar = msg.getScalar();
        if (scalar != null)
        {
            List<ObjAttribEntity> inputs = scalar.getInputs();
            List<OutputEntity> outputs = scalar.getOutputs();
            int size = inputs.size();
            for (int i = 0; i < size; i++)
            {
                ObjAttribEntity oa = inputs.get(i);
                Object obj = outputs.get(i).value;
                retval.put(oa, obj);
            }
        }
        return retval;
    }

}
