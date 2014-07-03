/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 实例化结果处理，不往数据库中插入任何数据
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;

/**
 * 实例化结果处理，不往数据库中插入任何数据
 * @author 郭祥
 */
public class InsRstHandleUnsave extends AbsInsRstHandle
{

    private static final Logger logger = Logger.getLogger(InsRstHandleUnsave.class);

    /**
     * 构造函数
     */
    public InsRstHandleUnsave()
    {
    }

    @Override
    public void handle() throws InstanceException
    {
        for (ObjAttribEntity oa : oas)
        {
            logger.info("不储存对象属性，属性ID<" + oa.getObjAttrId() + ">，对象ID<" + oa.getObjId() + ">，类型：<" + oa.getAttribType() + ">");
        }
    }
}
