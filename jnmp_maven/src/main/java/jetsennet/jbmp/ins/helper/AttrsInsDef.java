/************************************************************************
日 期：2011-11-29
作 者: 郭祥
版 本：v1.3
描 述: 默认的实例化方式
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.exception.InstanceException;

/**
 * 默认的实例化方式，返回的结果为NULL。
 * @author 郭祥
 */
public class AttrsInsDef extends AbsAttrsIns
{

    private static final Logger logger = Logger.getLogger(AttrsInsDef.class);

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        ArrayList<AttributeEntity> attrs = result.getInputAttrs();
        for (AttributeEntity attr : attrs)
        {
            logger.info("实例化模块：属性<" + attr.getAttribId() + ">，类型<" + attr.getClassType() + ">不需要实例化。");
        }
        return null;
    }
}
