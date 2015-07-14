/************************************************************************
日 期：2012-2-27
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess.buffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttribClassEntity;

/**
 * 属性分类缓存，在需要多次读取BMP_ATTRIBCLASS表时使用
 * @author 郭祥
 */
public class AttribClassBuffer
{

    /**
     * BUFFER
     */
    private Map<Integer, AttribClassEntity> acMap;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AttribClassBuffer.class);

    /**
     * 构造方法
     */
    public AttribClassBuffer()
    {
        this.init();
    }

    /**
     * @param classId 参数
     * @return 结果
     */
    public AttribClassEntity get(int classId)
    {
        return acMap.get(classId);
    }

    private void init()
    {
        acMap = new HashMap<Integer, AttribClassEntity>();
        AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
        try
        {
            List<AttribClassEntity> acs = acdal.getAll();
            for (AttribClassEntity ac : acs)
            {
                acMap.put(ac.getClassId(), ac);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }
}
