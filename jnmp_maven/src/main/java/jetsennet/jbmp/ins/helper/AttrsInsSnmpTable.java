/************************************************************************
日 期：2011-11-29
作 者: 郭祥
版 本：v1.3
描 述: SNMP表格型数据实例化
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.formula.FormulaTrans;
import jetsennet.jbmp.mib.MibUtil;

/**
 * SNMP表格型数据实例化
 * @author 郭祥
 */
public class AttrsInsSnmpTable extends AbsAttrsIns
{

    /**
     * 公式转换
     */
    private FormulaTrans trans;
    private static final Logger logger = Logger.getLogger(AttrsInsSnmpTable.class);

    /**
     * 构造函数
     */
    public AttrsInsSnmpTable()
    {
        trans = new FormulaTrans();
    }

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        ArrayList<AttributeEntity> attrs = result.getInputAttrs();
        if (attrs != null && attrs.size() > 0)
        {
            int mibId = MibUtil.ensureMib(mo.getObjId());
            for (AttributeEntity attr : attrs)
            {
                try
                {
                    ObjAttribEntity oa = this.genObjAttrib(attr, mibId, mo);
                    result.addResult(attr, oa);
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                    result.addErr(attr, ex.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 将属性数据转换成传输数据
     * @param attr
     * @throws InstanceException
     */
    private ObjAttribEntity genObjAttrib(AttributeEntity attr, int mibId, MObjectEntity mo) throws InstanceException
    {
        ObjAttribEntity oa = attr.genObjAttrib();
        oa.setObjId(mo.getObjId());
        trans.transform(attr.getAttribParam(), mibId, useBuffer);
        String param = trans.getOutput();
        String index = trans.getIndex();
        oa.setAttribParam(param);
        if (index != null)
        {
            oa.setField1(index);
        }
        return oa;
    }
}
