/************************************************************************
 日 期：2012-8-20
 作 者: 郭祥
 版 本: v1.3
 描 述: 
 历 史:
 ************************************************************************/
package jetsennet.jbmp.datacollect.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.ins.InsConstants;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.EditNode;
import jetsennet.jbmp.mib.node.SnmpProcessTable;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;

/**
 * 进程子对象采集
 * @author 郭祥
 */
public class SNMPCollectorProc extends SNMPCollector
{

    private static final Logger logger = Logger.getLogger(SNMPCollectorProc.class);

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        if (msg == null)
        {
            throw new NullPointerException();
        }

        setTime(new Date());
        String proName = mo.getField2();
        proName = proName == null ? "" : proName;
        String endNum = mo.getField1();

        objAttrLst.add(this.genNameOa(endNum));
        Map<String, VariableBinding> rs = getDatas(objAttrLst);

        // 名称不匹配时，返回
        boolean flag = this.compareProcess(rs, proName);
        if (!flag)
        {
            String endOid = this.ensureProcess(proName, mo);
            Map<ObjAttribEntity, Object> temp = this.genEmptyResult(objAttrLst);
            if (endOid == null)
            {
                msg.setCollState(TransMsg.COLL_STATE_FAILD);
                msg.setMsg(String.format("找不到名称为(%s)的进程。", proName));
            }
            else
            {
                msg.setCollState(TransMsg.COLL_STATE_UPDATE);
                msg.setMsg(endOid);
            }
            return temp;
        }

        // 下列代码拷贝自父类
        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        // 用于持久化的数据
        Map<String, Double> values = new HashMap<String, Double>(result.size());
        boolean dataFlag = false;
        for (ObjAttribEntity oa : objAttrLst)
        {
            String exp = parseFormula(oa, rs);
            if (exp == null)
            {
                continue;
            }
            Object temp = this.genCollData(oa, exp);
            if (temp != null && temp instanceof CollData)
            {
                CollData data = (CollData) temp;
                if (data.value != null && oa.getAttribType() == AttribClassEntity.CLASS_LEVEL_PERF)
                {
                    try
                    {
                        // 判断值是否>=0，舍弃<0的值
                        double tempValue = Double.parseDouble(data.value);
                        if (tempValue >= 0)
                        {
                            values.put(Integer.toString(oa.getObjAttrId()), tempValue);
                        }
                    }
                    catch (Exception e)
                    {
                        logger.warn("数据转换异常，值:" + data.value + "；属性名称：" + oa.getObjattrName(), e);
                    }
                    // 将数据添加到第一个CollData的CollData.PARAMS_DATA中
                    if (!dataFlag)
                    {
                        data.put(CollData.PARAMS_DATA, values);
                        dataFlag = true;
                    }
                }
            }
            result.put(oa, temp);
        }
        generateFailedData(result, objAttrLst);
        return result;
    }

    /**
     * 确定采集到的进程名称和给定的进程名称是否相符合
     * @param rs
     * @param proName
     * @return
     */
    private boolean compareProcess(Map<String, VariableBinding> rs, String proName)
    {
        boolean retval = true;
        String nameOid = InsConstants.OID_HEAD + ":(" + SnmpProcessTable.SNMP_PROCESS_NAME_OID + ".";
        for (String key : rs.keySet())
        {
            if (key.startsWith(nameOid))
            {
                VariableBinding bind = rs.get(key);
                if (bind == null)
                {
                    retval = false;
                    break;
                }
                else
                {
                    String str = SnmpValueTranser.getInstance().trans(bind, null, 200);
                    if (!proName.equals(str))
                    {
                        retval = true;
                        break;
                    }
                }
            }
        }
        return retval;
    }

    /**
     * 获取给定名称对应的OID尾
     * @param procName
     * @return
     */
    private String ensureProcess(String proName, MObjectEntity mo)
    {
        String retval = null;
        try
        {
            SnmpTable table = SnmpProcessTable.getTable();
            SnmpTableUtil.initSnmpTable(table, null, mo);
            ArrayList<EditNode> nodes = table.getColumn(SnmpProcessTable.SNMP_PROCESS_NAME_COLUMN);
            for (EditNode node : nodes)
            {
                String name = node.getEditValue();
                if (proName.equals(name))
                {
                    retval = node.getOid().substring(SnmpProcessTable.SNMP_PROCESS_NAME_OID.length() + 1);
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    private ObjAttribEntity genNameOa(String endOid)
    {
        ObjAttribEntity oa = new ObjAttribEntity();
        oa.setAttribParam("str:(OID:(" + SnmpProcessTable.SNMP_PROCESS_NAME_OID + "." + endOid + "))");
        return oa;
    }

    /**
     * 主函数
     * @param args 参数
     */
    public static void main(String[] args)
    {
        SNMPCollectorProc proc = new SNMPCollectorProc();
        MObjectEntity mo = new MObjectEntity();
        mo.setIpAddr("192.168.8.145");
        mo.setIpPort(161);
        mo.setUserName("public");
        proc.ensureProcess("System", mo);
    }

}
