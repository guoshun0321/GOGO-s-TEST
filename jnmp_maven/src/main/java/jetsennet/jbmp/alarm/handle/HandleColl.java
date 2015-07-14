/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 数据处理类的实例的集合
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import org.apache.log4j.Logger;

/**
 * 数据处理类的集合。数据处理线程包含一个数据处理类。线程安全。
 * @author 郭祥
 */
public final class HandleColl
{

    /**
     * 每个线程一个处理类实例
     */
    private ThreadLocal<ICollDataHandle> localHandles;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(HandleColl.class);
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static HandleColl instance = new HandleColl();

    private HandleColl()
    {
        localHandles = new ThreadLocal<ICollDataHandle>();
    }

    public static HandleColl getInstance()
    {
        return instance;
    }

    // </editor-fold>

    /**
     * 获取className对应的类，这个函数是线程安全的。
     * @param className 参数
     * @return 结果
     */
    public ICollDataHandle get(String className)
    {
        ICollDataHandle handle = localHandles.get();
        if (handle == null)
        {
            handle = this.insClass(className);
            localHandles.set(handle);
        }

        return handle;
    }

    /**
     * 实例化类
     * @param className
     * @return
     */
    private ICollDataHandle insClass(String className)
    {
        AbsCollDataHandle retval = null;
        try
        {
            Class cl = Class.forName(className);
            Object obj = cl.newInstance();
            if (obj != null && obj instanceof AbsCollDataHandle)
            {
                retval = (AbsCollDataHandle) obj;
            }
        }
        catch (Throwable ex)
        {
            logger.error("", ex);
            retval = null;
        }

        return retval;
    }
}
