/************************************************************************
日 期: 2012-10-11
作 者: 郭祥
版 本: v1.3
描 述: 管理子对象实例化类
历 史:
 ************************************************************************/
package jetsennet.jbmp.ins;

import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttribClassEntity;

import org.apache.log4j.Logger;

/**
 * 管理子对象实例化类
 * @author 郭祥
 */
public class SubObjInsManager
{

    private static final SubObjInsManager instance = new SubObjInsManager();

    private SubObjInsManager()
    {
    }

    public static SubObjInsManager getInstance()
    {
        return instance;
    }

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SubObjInsManager.class);

    public AbsSubObjIns ensureInsClass(int classId)
    {
        AbsSubObjIns retval = null;
        try
        {
            AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
            AttribClassEntity ac = acdal.get(classId);
            retval = ensureInsClass(ac == null ? null : ac.getClassType());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    public AbsSubObjIns ensureInsClass(String classType)
    {
        AbsSubObjIns retval = null;
        try
        {
            InsConfig config = InsConfigColl.getInstance().getSub(classType);
            retval = InsConfigColl.ensureSubObjInsClass(config.getInsClass());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

}
