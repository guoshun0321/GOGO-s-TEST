/************************************************************************
日 期：2011-11-29
作 者: 郭祥
版 本：v1.3
描 述: 自定义属性实例化
历 史：
 ************************************************************************/
package jetsennet.jnmp.ins.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.formula.inspur.FormulaHandleInspur;
import jetsennet.jbmp.ins.helper.AbsAttrsIns;
import jetsennet.jbmp.ins.helper.AttrsInsResult;

/**
 * 浪潮机房属性实例化
 * @author 李巍
 */
public class AttrsInsInspur extends AbsAttrsIns
{
    FormulaHandleInspur f = new FormulaHandleInspur();
    private static final Logger logger = Logger.getLogger(AttrsInsInspur.class);

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        ArrayList<AttributeEntity> attrs = result.getInputAttrs();
        ArrayList<ObjAttribEntity> list = new ArrayList<ObjAttribEntity>();
        for (AttributeEntity attr : attrs)
        {
            try
            {
                String formual = attr.getAttribParam().trim();
                // 获取公式
                List<String> formuals = f.genInsFormula(formual);
                if (!this.isAuto || !(formuals.size() > 1))
                {
                    // 把获取的公式放入到对象属性的实体中
                    for (String s : formuals)
                    {
                        ObjAttribEntity oa = attr.genObjAttrib();
                        oa.setObjId(mo.getObjId());
                        oa.setAttribParam(s);
                        oa.setInsResult(null);
                        list.add(oa);
                    }
                    ObjAttribEntity[] objs = list.toArray(new ObjAttribEntity[] {});
                    list.clear();
                    result.addResult(attr, objs);
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
                result.addErr(attr, ex.getMessage());
            }
        }
        return result;
    }
}
