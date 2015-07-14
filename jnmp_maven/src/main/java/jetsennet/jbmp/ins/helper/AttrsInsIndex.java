/************************************************************************
日 期：2012-1-13
作 者: 郭祥
版 本: v1.3
描 述: 在知道索引的情况下实例化SNMP公式
历 史:
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
import jetsennet.jbmp.util.InsUtil;

/**
 * @author 郭祥
 */
public class AttrsInsIndex extends AbsAttrsIns
{

    /**
     * 公式转换
     */
    private FormulaTrans trans;
    /**
     * 索引
     */
    private String index;
    private static final Logger logger = Logger.getLogger(AttrsInsIndex.class);

    /**
     * @param index 参数
     */
    public AttrsInsIndex(String index)
    {
        trans = new FormulaTrans();
        this.index = index;
    }

    /**
     * 设置索引
     * @param index 参数
     */
    public void setIndex(String index)
    {
        this.index = index;
    }

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        ArrayList<AttributeEntity> attrs = result.getInputAttrs();
        if (attrs != null && attrs.size() > 0)
        {
            int mibId = MibUtil.ensureMibByClassId(mo.getClassId());
            for (AttributeEntity attr : attrs)
            {
                try
                {
                    ObjAttribEntity oa = this.genOa(mo, attr, index, mibId);
                    result.addResult(attr, oa);
                }
                catch (Exception ex)
                {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
        return result;
    }

    /**
     * @param mo 参数
     * @param attr 参数
     * @param index 参数
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    protected ObjAttribEntity genOa(MObjectEntity mo, AttributeEntity attr, String index, int mibId) throws Exception
    {
        ObjAttribEntity retval = attr.genObjAttrib();
        retval.setObjId(mo.getObjId());
        String param = attr.getAttribParam();
        trans.transform(param, mibId, useBuffer);
        retval.setField1(trans.getIndex());
        param = InsUtil.addIndexToColumn(trans.getOutput(), index);
        retval.setAttribParam(param);
        return retval;
    }
}
