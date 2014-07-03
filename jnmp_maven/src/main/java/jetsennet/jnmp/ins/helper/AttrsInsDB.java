package jetsennet.jnmp.ins.helper;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.ins.helper.AbsAttrsIns;
import jetsennet.jbmp.ins.helper.AttrsInsResult;

/**
 * 数据库属性实例化
 * 
 * @author 郭祥
 */
public class AttrsInsDB extends AbsAttrsIns
{

    private static final Logger logger = Logger.getLogger(AttrsInsDB.class);

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
                oa.setAttribValue(attr.getAttribCode());
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
