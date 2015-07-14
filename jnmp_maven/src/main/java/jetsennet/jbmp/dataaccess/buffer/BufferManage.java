package jetsennet.jbmp.dataaccess.buffer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.annotation.Operation;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.util.TwoTuple;

/**
 * 缓存管理模块
 * @author xuyuji
 */
public final class BufferManage
{
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BufferManage.class);
    /**
     * 单例
     */
    private static BufferManage instance = new BufferManage();
    /**
     * 消息队列
     */
    private BufferMsgQueue bufferMsgQueue;
    /**
     * 消息队列
     */
    private Lock lock;
    /**
     * 模块状态标志位
     */
    private boolean isStop;
    /**
     * 线程执行器
     */
    private ExecutorService single;
    /**
     * 线程结束标志
     */
    private Future<Integer> endFlag;
    /**
     * 缓存模块映射
     */
    private HashMap<String, BufferObservable> buffers;
    /**
     * BMP_OBJECT表的观察者
     */
    public BufferObservable<MObjectEntity> BMP_OBJECT;
    /**
     * 方法返回值是ID
     */
    private static final int YES = 1;
    /**
     * 方法返回值不是ID
     */
    private static final int NO = 0;
    /**
     * 插入方法
     */
    private static final int INSERT = 1;
    /**
     * 删除方法
     */
    private static final int DELETE = 2;
    /**
     * 修改方法
     */
    private static final int UPDATE = 3;
    /**
     * 错误
     */
    private static final int ERROR = 0;

    /**
     * 构造函数
     */
    private BufferManage()
    {
        // 获取队列实例
        bufferMsgQueue = BufferMsgQueue.getInstance();
        buffers = new HashMap<String, BufferObservable>();
        isStop = false;
        // 获取锁实例
        lock = new ReentrantLock();
    }

    /**
     * 获取单例
     * @return 结果
     */
    public static BufferManage getInstance()
    {
        return instance;
    }

    /**
     * 添加缓存模块
     * @param bufferObservable 参数
     */
    public void addBufferObservable(BufferObservable bufferObservable)
    {
        if (!buffers.containsKey(bufferObservable.getTableName()))
        {
            logger.info("缓存模块：添加" + bufferObservable.getTableName() + "表缓存模块。");
            buffers.put(bufferObservable.getTableName(), bufferObservable);
        }
        else
        {
            logger.info("缓存模块：" + bufferObservable.getTableName() + "表缓存模块已存在。");
        }
    }

    /**
     * 删除缓存模块
     * @param tableName 表名
     */
    public void delBufferObservable(String tableName)
    {
        if (buffers.containsKey(tableName))
        {
            logger.info("缓存模块：删除" + tableName + "表缓存模块。");
            buffers.remove(tableName);
        }
        else
        {
            logger.info("缓存模块：不存在" + tableName + "表缓存模块。");
        }
    }

    /**
     * 添加缓存模块观察者
     * @param tableName 表明
     * @param observer 观察者
     */
    public void addBufferObserver(String tableName, Observer observer)
    {
        if (buffers.containsKey(tableName))
        {
            logger.info("缓存模块：添加" + tableName + "表缓存模块观察者" + observer + "。");
            buffers.get(tableName).addObserver(observer);
        }
        else
        {
            logger.info("缓存模块：不存在" + tableName + "表缓存模块。");
        }
    }

    /**
     * 删除缓存模块观察者
     * @param tableName 表名
     * @param observer 观察者
     */
    public void delBufferObserver(String tableName, Observer observer)
    {
        if (buffers.containsKey(tableName))
        {
            logger.info("缓存模块：删除" + tableName + "表缓存模块观察者" + observer + "。");
            buffers.get(tableName).deleteObserver(observer);
        }
        else
        {
            logger.info("缓存模块：不存在" + tableName + "表缓存模块。");
        }
    }

    /**
     * 获取tableName表的缓存
     * @param tableName 表名
     * @return 结果
     */
    public Map getBuffer(String tableName)
    {
        if (buffers.containsKey(tableName))
        {
            return buffers.get(tableName).getBuffer();
        }
        else
        {
            return null;
        }
    }

    /**
     * 开始监控消息队列
     */
    public void start()
    {
        Lock l = lock;
        l.lock();
        try
        {
            isStop = false;
            single = Executors.newSingleThreadExecutor();
            endFlag = single.submit(new Notify());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
            logger.info("缓存模块：启动。");
        }
    }

    /**
     * 停止监控消息队列
     */
    public void stop()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (isStop)
            {
                return;
            }
            isStop = true;
            bufferMsgQueue.put(new TwoTuple<Method, Object>(null, null));
            endFlag.get(1, TimeUnit.SECONDS);
            single.shutdown();
            bufferMsgQueue.clear();
            buffers.clear();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
            logger.info("缓存模块：关闭。");
        }
    }

    /**
     * 消息通知
     * @param method 方法
     * @param result 结果
     */
    public void pushMsg(Method method, Object result)
    {
        /*
         * 只管理标记了Operation注解的方法 注：对要缓存的表的所有改变数据的方法都要加上Operation注解。******************************************************************* 例：
         * @Business
         * @Operation(name = "insert", tablename = "BMP_OBJECT") public MObjectEntity addObj(String objXml) throws Exception{...}
         * ******************************************************************* name接受insert、update、delete，分别对应增、改、删 tablename写入数据所属表
         */
        bufferMsgQueue.put(new TwoTuple<Method, Object>(method, result));
    }

    /**
     * 测试方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        BufferManage test = new BufferManage();
        test.start();
        test.stop();
    }

    /**
     * 内部类:通告类 监控消息队列，并处理数据，然后通知观察者。
     * @author xuyuji
     */
    class Notify implements Callable<Integer>
    {

        @Override
        public Integer call()
        {
            while (!isStop)
            {
                TwoTuple<Method, Object> twoTuple = bufferMsgQueue.get();
                // 空消息，用于关闭时。
                if (twoTuple.first == null)
                {
                    continue;
                }
                Method method = twoTuple.first;
                Object result = twoTuple.second;

                Operation operation = method.getAnnotation(Operation.class);
                if (operation == null)
                {
                    continue;
                }
                Map<String, Integer> map = new HashMap<String, Integer>();
                // 方法返回的是否为id,1是0否
                if (result instanceof Integer)
                {
                    map.put("isid", YES);
                    map.put("id", (Integer) result);
                }
                else
                {
                    map.put("isid", NO);
                }

                if ("insert".equals(operation.name()))
                {
                    map.put("type", INSERT);
                }
                else if ("update".equals(operation.name()))
                {
                    map.put("type", UPDATE);
                }
                else if ("delete".equals(operation.name()))
                {
                    map.put("type", DELETE);
                }
                else
                {
                    map.put("type", ERROR);
                }

                String tableName = operation.tablename();
                if (buffers.containsKey(tableName))
                {
                    buffers.get(tableName).refreshBuffer(map);
                }
            }
            logger.info("缓存模块：缓存同步结束。");
            return 1;
        }
    }

}
