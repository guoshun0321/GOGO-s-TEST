package jetsennet.orm.transaction.auto;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jetsennet.orm.annotation.Business;
import jetsennet.orm.annotation.Transactional;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

/**
 * Class包装器
 * 
 * @author 郭祥 
 */
public class ClassWrapper
{

    /**
     * Business过滤器
     */
    private static BusinessFilter bussFilter = new BusinessFilter();
    /**
     * transaction过滤器
     */
    private static TransFilter transFilter = new TransFilter();
    /**
     * 保存sessionFactory对应的拦截器
     */
    private static ConcurrentMap<SqlSessionFactory, TransInterceptor> factory2Interceptor =
        new ConcurrentHashMap<SqlSessionFactory, TransInterceptor>();

    public static <T> T wrap(Class<T> c, Object... params)
    {
        return wrap(c, SqlSessionFactoryBuilder.builder(), params);
    }

    /**
     * 将Class包装成具有日志、安全、事务处理功能的类并创建相应实例
     * @param <T> 泛型
     * @param c 类型
     * @param params 参数
     * @return 结果
     */
    public static <T> T wrap(Class<T> c, SqlSessionFactory factory, Object... params)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(c);
        enhancer.setUseCache(true);
        enhancer.setCallbacks(new Callback[] { ensureFactoryInterceptor(factory), NoOp.INSTANCE });
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
     * 将Class包装成具有事务处理功能的类并创建相应实例
     * @param <T> 泛型
     * @param c 类型
     * @param params 参数
     * @return 结果
     */
    public static <T> T wrapTrans(Class<T> c, SqlSessionFactory factory, Object... params)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(c);
        enhancer.setUseCache(true);
        enhancer.setCallbacks(new Callback[] { ensureFactoryInterceptor(factory), NoOp.INSTANCE });
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
     * 获取factory对应的interceptor，使用ConcurrentHashMap来保证只有一个factory只对应一个Interceptor实例
     * 
     * @param factory
     * @return
     */
    private static TransInterceptor ensureFactoryInterceptor(SqlSessionFactory factory)
    {
        TransInterceptor retval = factory2Interceptor.get(factory);
        if (retval == null)
        {
            retval = new TransInterceptor(factory);
            TransInterceptor temp = factory2Interceptor.putIfAbsent(factory, retval);
            if (temp != null)
            {
                retval = temp;
            }
        }
        return retval;
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
        private SqlSessionFactory factory;

        public TransInterceptor(SqlSessionFactory factory)
        {
            this.factory = factory;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
        {
            Object retval = null;

            Session session = factory.openSession();
            boolean isSelf = false;
            try
            {
                isSelf = session.transBegin();
                retval = proxy.invokeSuper(obj, args);
                session.transCommit(isSelf);
            }
            catch (Exception ex)
            {
                session.transRollback(isSelf);
                throw ex;
            }
            return retval;
        }
    }
}
