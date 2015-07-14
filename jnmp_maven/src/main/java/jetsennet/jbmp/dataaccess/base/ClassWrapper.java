package jetsennet.jbmp.dataaccess.base;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import jetsennet.jbmp.dataaccess.AlarmDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author lianghongjie Class包装器
 */
public class ClassWrapper
{
    private static BusinessFilter bussFilter = new BusinessFilter();
    private static TransFilter transFilter = new TransFilter();
    private static TransInterceptor transIcp = new TransInterceptor();

    /**
     * 将Class包装成具有日志、安全、事务处理功能的类并创建相应实例
     * @param <T> 泛型
     * @param c 参数
     * @param params 参数
     * @return 结果
     */
    public static <T> T wrap(Class<T> c, Object... params)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(c);
        enhancer.setUseCache(true);
        enhancer.setCallbacks(new Callback[] { transIcp, NoOp.INSTANCE });
        enhancer.setCallbackFilter(bussFilter);
        T result = null;
        if (params == null || params.length == 0)
        {
            result = (T) enhancer.create();
        }
        else
        {
            Class[] cs = new Class[params.length];
            for (int i = 0; i < cs.length; i++)
            {
                cs[i] = params[i].getClass();
            }
            result = (T) enhancer.create(cs, params);
        }
        return result;
    }

    /**
     * 将Class包装成具有事务处理功能的类并创建相应实例
     * @param <T> 泛型
     * @param c 参数
     * @param params 参数
     * @return 结果
     */
    public static <T> T wrapTrans(Class<T> c, Object... params)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(c);
        enhancer.setUseCache(true);
        enhancer.setCallbacks(new Callback[] { transIcp, NoOp.INSTANCE });
        enhancer.setCallbackFilter(transFilter);
        T result = null;
        if (params == null || params.length == 0)
        {
            result = (T) enhancer.create();
        }
        else
        {
            Class[] cs = new Class[params.length];
            for (int i = 0; i < cs.length; i++)
            {
                cs[i] = params[i].getClass();
            }
            result = (T) enhancer.create(cs, params);
        }
        return result;
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        int count = 0;
        for (;;)
        {
            System.out.println(count++);
            AlarmDal dal = ClassWrapper.wrap(AlarmDal.class);
        }
    }

    private static class BusinessFilter implements CallbackFilter
    {
        @Override
        public int accept(Method method)
        {
            if (!method.isAnnotationPresent(Business.class))
            {
                return 1;
            }
            return 0;
        }
    }

    private static class TransFilter implements CallbackFilter
    {
        @Override
        public int accept(Method method)
        {
            if (!method.isAnnotationPresent(Transactional.class))
            {
                return 1;
            }
            return 0;
        }
    }

    private static class TransInterceptor implements MethodInterceptor
    {
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            boolean isTrans = exec.getIsTransing();
            try
            {
                if (!isTrans)
                {
                    exec.transBegin();
                }
                Object result = proxy.invokeSuper(obj, args);
                if (!isTrans)
                {
                    exec.transCommit();
                }
                return result;
            }
            catch (Exception ex)
            {
                if (!isTrans)
                {
                    exec.transRollback();
                }
                throw ex;
            }
            finally
            {
                if (!isTrans)
                {
                    SqlExecutorFacotry.unbindSqlExecutor();
                }
            }
        }
    }

}
