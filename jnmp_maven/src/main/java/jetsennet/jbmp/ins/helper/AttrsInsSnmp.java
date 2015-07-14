/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: SNMP属性实例化
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import java.util.ArrayList;
import java.util.Map;

import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnsMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.OutputEntity;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ScalarMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.datacollect.datasource.DataAgentManager;
import jetsennet.jbmp.datacollect.datasource.IDataAgent;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.formula.FormulaTrans;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.protocols.snmp.SnmpResult;
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.jbmp.util.ErrorMessageConstant;

import org.apache.log4j.Logger;

/**
 * SNMP属性实例化
 * 
 * @author 郭祥
 */
public class AttrsInsSnmp extends AbsAttrsIns
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AttrsInsSnmp.class);

    /**
     * 构造函数
     */
    public AttrsInsSnmp()
    {
    }

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        TransMsg trans = new TransMsg();
        trans.setMo(mo);
        int mibId = MibUtil.ensureMib(mo.getObjId());
        
        for (AttributeEntity attr : result.getInputAttrs())
        {
            try
            {
                FormulaTrans ft = new FormulaTrans();
                this.addToTransMsg(trans, mo, attr, mibId, ft);
            }
            catch (Exception ex)
            {
                logger.error("", ex);
                result.addErr(attr, ex.getMessage());
            }
        }
        
        SnmpResult snmpR = new SnmpResult();
        snmpR.init(trans);
        snmpR.setEnumValue(mibId);
        trans.setRecInfo(snmpR);
        
        if (isLocal)
        {
            trans = this.scanLocal(mo, trans);
        }
        else
        {
            trans = this.scanRemote(collId, mo.getObjId(), trans);
        }
        this.assemble(trans);
        return result;
    }

    /**
     * 将属性数据转换成传输数据
     * @param attr
     * @throws InstanceException
     */
    private void addToTransMsg(TransMsg trans, MObjectEntity mo, AttributeEntity attr, int mibId, FormulaTrans ft) throws InstanceException
    {
        if (attr == null)
        {
            throw new InstanceException();
        }
        ObjAttribEntity oa = attr.genObjAttrib();
        oa.setObjId(mo.getObjId());
        String param = attr.getAttribParam();
        if (param != null)
        {
            ft.transform(param, mibId, useBuffer);
            param = ft.getOutput();
            oa.setAttribParam(param);
            String index = ft.getIndex();
            if (index == null)
            {
                trans.addScalar(oa);
            }
            else
            {
                oa.setField1(index);
                trans.addTable(oa);
            }
        }
    }

    /**
     * 远程扫描数据
     */
    private TransMsg scanLocal(MObjectEntity mo, TransMsg trans)
    {
        try
        {
            IDataAgent agent = DataAgentManager.getAgent();
            trans = agent.getDataForIns(trans);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return trans;
    }

    /**
     * 远程扫描数据
     */
    private TransMsg scanRemote(int collId, int objId, TransMsg trans)
    {
        TransMsg retval = null;
        try
        {
            if (collId < 0)
            {
                retval =
                    (TransMsg) BMPServletContextListener.getInstance().callRemote(Integer.toString(objId), "remoteCollData", new Object[] { trans },
                        new Class[] { TransMsg.class }, true);
            }
            else
            {
                retval =
                    (TransMsg) BMPServletContextListener.getInstance().callRemote(collId, "remoteCollData", new Object[] { trans },
                        new Class[] { TransMsg.class }, true);
            }
            if (retval == null)
            {
                retval = trans;
            }
        }
        catch (Exception ex)
        {
            logger.error(String.format(ErrorMessageConstant.RMI_ERROR, "remoteCollData"), ex);
            retval = trans;
        }
        // 直接调用，测试用
        // AlarmCluster ac = new AlarmCluster();
        // retval = ac.remoteCollData(trans);
        return retval;
    }

    /**
     * 组装
     */
    private void assemble(TransMsg trans)
    {
        if (trans == null)
        {
            return;
        }
        ScalarMsg sm = trans.getScalar();
        this.assembleScalar(sm);
        ColumnsMsg cm = trans.getColumns();
        this.assembleColumns(cm);
    }

    /**
     * 组装标量类型数据
     * @param msg
     */
    private void assembleScalar(ScalarMsg msg)
    {
        if (msg == null)
        {
            return;
        }
        ArrayList<ObjAttribEntity> inputs = msg.getInputs();
        ArrayList<OutputEntity> outputs = msg.getOutputs();
        if (inputs == null || inputs.isEmpty())
        {
            return;
        }
        for (int i = 0; i < inputs.size(); i++)
        {
            ObjAttribEntity input = inputs.get(i);
            if (outputs != null && outputs.size() > i)
            {
                OutputEntity output = outputs.get(i);
                // 正确实例化的
                if (output != null)
                {
                    input.setAttribParam(output.exp);
                    input.setObjattrName(output.name);
                }
                else
                // 错误实例化的
                {
                    result.addErr(input.getObjAttrId(), "");
                }
            }
            result.addResult(input.getAttribId(), input);
        }
    }

    /**
     * 组装表格类型数据
     * @param msg
     */
    private void assembleColumns(ColumnsMsg msg)
    {
        if (msg == null)
        {
            return;
        }
        ArrayList<ColumnMsg> cms = msg.getColumns();
        if (cms == null || cms.isEmpty())
        {
            return;
        }
        for (ColumnMsg cm : cms)
        {
            this.assembleColumn(cm);
        }
    }

    /**
     * 组装表格类型数据
     * @param msg
     */
    private void assembleColumn(ColumnMsg msg)
    {
        int attrId = msg.getInput().getAttribId();
        if (msg.getOutputs() == null || msg.getOutputs().size() == 0)
        {
            result.addErr(attrId, "");
            return;
        }
        int size = msg.getOutputs().size();
        ObjAttribEntity[] oas = new ObjAttribEntity[size];
        for (int i = 0; i < size; i++)
        {
            ObjAttribEntity oa = msg.getInput().copy();
            OutputEntity output = msg.getOutputs().get(i);
            if (output != null)
            {
                oa.setAttribParam(output.exp);
                oa.setObjattrName(output.name);
                oas[i] = oa;
            }
            else
            {
                oas[i] = null;
            }
        }
        result.addResult(attrId, oas);
    }
}
