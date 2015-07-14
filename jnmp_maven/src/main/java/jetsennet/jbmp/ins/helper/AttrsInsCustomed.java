/************************************************************************
日 期：2011-11-29
作 者: 郭祥
版 本：v1.3
描 述: 自定义属性实例化
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

/**
 * 自定义属性实例化
 * @author 郭祥
 */
public class AttrsInsCustomed extends AbsAttrsIns
{

    private static final Logger logger = Logger.getLogger(AttrsInsCustomed.class);

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        ArrayList<AttributeEntity> attrs = result.getInputAttrs();
        for (AttributeEntity attr : attrs)
        {
            try
            {
                ObjAttribEntity oa = attr.genObjAttrib();
                oa.setObjId(mo.getObjId());
                oa.setInsResult(null);
                result.addResult(attr, oa);
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
