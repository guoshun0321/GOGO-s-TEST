/************************************************************************
日 期：2011-11-29
作 者: 郭祥
版 本：v1.3
描 述: 浪潮机房实例化（新）
历 史：
 ************************************************************************/
package jetsennet.jnmp.ins.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnsMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.OutputEntity;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.datacollect.datasource.DataAgentManager;
import jetsennet.jbmp.datacollect.datasource.IDataAgent;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.formula.inspur.FormulaHandleInspur;
import jetsennet.jbmp.ins.helper.AbsAttrsIns;
import jetsennet.jbmp.ins.helper.AttrsInsResult;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.jbmp.util.ErrorMessageConstant;

/**
 * 浪潮机房属性实例化
 * @author 郭祥
 */
public class AttrsInsInspurSec extends AbsAttrsIns
{
    /**
     * 远程调用
     */
    private BMPServletContextListener listener;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AttrsInsInspurSec.class);

    public AttrsInsInspurSec()
    {
        listener = BMPServletContextListener.getInstance();
    }

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        TransMsg trans = new TransMsg();
        trans.setMo(mo);
        for (AttributeEntity attr : result.getInputAttrs())
        {
            try
            {
                this.addToTransMsg(trans, mo.getObjId(), attr);
            }
            catch (Exception ex)
            {
                logger.error("", ex);
                result.addErr(attr, ex.getMessage());
            }
        }
        trans = this.getTransRst(trans, collId, mo.getObjId(), isLocal);

        if (trans != null && trans.getColumns() != null)
        {
            ColumnsMsg cmsg = trans.getColumns();
            ArrayList<ColumnMsg> cms = cmsg.getColumns();
            if (cms != null)
            {
                for (ColumnMsg cm : cms)
                {
                    int attrId = cm.getInput().getAttribId();
                    if (cm.getOutputs() == null || cm.getOutputs().size() == 0)
                    {
                        result.addErr(attrId, "");
                    }
                    else
                    {
                        int size = cm.getOutputs().size();
                        ObjAttribEntity[] oas = new ObjAttribEntity[size];
                        for (int i = 0; i < size; i++)
                        {
                            ObjAttribEntity oa = cm.getInput().copy();
                            OutputEntity output = cm.getOutputs().get(i);
                            if (output != null)
                            {
                                oa.setAttribParam(output.exp);
                                if (output.name != null)
                                {
                                    oa.setObjattrName(output.name);
                                }
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
            }
        }
        return result;
    }

    /**
     * 将属性转换成传输数据
     * @param attr
     * @throws InstanceException
     */
    private void addToTransMsg(TransMsg trans, int objId, AttributeEntity attr) throws InstanceException
    {
        if (attr == null)
        {
            throw new InstanceException();
        }
        ObjAttribEntity oa = attr.genObjAttrib();
        oa.setObjId(objId);
        trans.addTable(oa);
    }

    /**
     * 获取扫描后的数据
     * @param msg
     * @param collId
     * @param objId
     * @param isLocal
     * @return
     */
    private TransMsg getTransRst(TransMsg msg, int collId, int objId, boolean isLocal)
    {
        TransMsg retval = null;
        if (isLocal)
        {
            IDataAgent agent = DataAgentManager.getAgent();
            retval = agent.getDataForIns(msg);
        }
        else
        {
            try
            {
                if (collId < 0)
                {
                    retval =
                        (TransMsg) listener.callRemote(Integer.toString(objId), "remoteCollData", new Object[] { msg },
                            new Class[] { TransMsg.class }, true);
                }
                else
                {
                    retval = (TransMsg) listener.callRemote(collId, "remoteCollData", new Object[] { msg }, new Class[] { TransMsg.class }, true);
                }
                if (retval == null)
                {
                    retval = msg;
                }
            }
            catch (Exception ex)
            {
                logger.error(String.format(ErrorMessageConstant.RMI_ERROR, "remoteCollData"), ex);
                retval = msg;
            }
            return retval;
        }
        return retval;
    }
}
