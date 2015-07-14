/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 默认的实例化结果处理
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.ObjAttribValueDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.exception.InstanceException;

/**
 * 默认的实例化结果处理
 * @author 郭祥
 */
public class InsRstHandleDef extends AbsInsRstHandle
{

    private ObjAttribDal oadal;
    private ObjAttribValueDal oavdal;

    /**
     * 构造函数
     */
    public InsRstHandleDef()
    {
        oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        oavdal = ClassWrapper.wrapTrans(ObjAttribValueDal.class);
    }

    @Override
    public void handle() throws InstanceException
    {
        try
        {
            oadal.insertWithAlarm(oas);
            oavdal.insertOrUpdate(oas);
        }
        catch (Exception ex)
        {
            throw new InstanceException(ex);
        }
    }
}
