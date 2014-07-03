package jetsennet.jbmp.ins.helper;

import java.util.ArrayList;
import java.util.Map;

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
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.jbmp.util.ErrorMessageConstant;

import org.apache.log4j.Logger;

public class AttrsInsDayang extends AbsAttrsIns {

	private static final Logger logger = Logger.getLogger(AttrsInsDayang.class);

	@Override
	public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException {
		ArrayList<AttributeEntity> attrs = result.getInputAttrs();
		TransMsg trans = null;
		for (AttributeEntity attr : attrs) {
			logger.debug("解析属性：" + attr.getAttribName());
			try {
				ObjAttribEntity oa = attr.genObjAttrib();
				String attribValue = oa.getAttribValue();
				if (attribValue.contains("/")) {
					if (trans == null) {
						trans = new TransMsg();
						trans.setMo(mo);
					}
					oa.setObjId(mo.getObjId());
					trans.addTable(oa);
				} else {
					oa.setObjId(mo.getObjId());
					oa.setInsResult(null);
					result.addResult(attr, oa);
				}
			} catch (Exception ex) {
				logger.error("", ex);
				result.addErr(attr, ex.getMessage());
			}
		}

		
		if (trans != null) {
			if (isLocal) {
				trans = this.scanLocal(mo, trans);
			} else {
				logger.debug("准备获取数据。collId : " + collId + "/ objId : " + mo.getObjId());
				trans = this.scanRemote(collId, mo.getObjId(), trans);
			}
		}
		
		logger.debug("准备组装数据：" + trans);
		if(trans != null) {
			ColumnsMsg colMsg = trans.getColumns();
			ArrayList<ColumnMsg> cms = colMsg.getColumns();
	        if (cms != null && !cms.isEmpty())
	        {
	        	for (ColumnMsg cm : cms)
		        {
		            this.assembleColumn(cm);
		        }
	        }
		}
		return result;
	}

	/**
	 * 远程扫描数据
	 */
	private TransMsg scanLocal(MObjectEntity mo, TransMsg trans) {
		try {
			IDataAgent agent = DataAgentManager.getAgent();
			trans = agent.getDataForIns(trans);
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return trans;
	}

	/**
	 * 远程扫描数据
	 */
	private TransMsg scanRemote(int collId, int objId, TransMsg trans) {
		TransMsg retval = null;
		try {
			if (collId < 0) {
				retval = (TransMsg) BMPServletContextListener.getInstance().callRemote(Integer.toString(objId), "remoteCollData",
						new Object[] { trans }, new Class[] { TransMsg.class }, true);
			} else {
				retval = (TransMsg) BMPServletContextListener.getInstance().callRemote(collId, "remoteCollData", new Object[] { trans },
						new Class[] { TransMsg.class }, true);
			}
			if (retval == null) {
				retval = trans;
			}
		} catch (Exception ex) {
			logger.error(String.format(ErrorMessageConstant.RMI_ERROR, "remoteCollData"), ex);
			retval = trans;
		}
		// 直接调用，测试用
		// AlarmCluster ac = new AlarmCluster();
		// retval = ac.remoteCollData(trans);
		return retval;
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
            OutputEntity out = msg.getOutputs().get(i);
            oa.setObjattrName(out.name);
            oa.setAttribValue(out.exp);
            oas[i] = oa;
        }
        result.addResult(attrId, oas);
    }
}
