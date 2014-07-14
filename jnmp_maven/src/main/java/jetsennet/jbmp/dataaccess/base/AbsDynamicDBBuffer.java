/************************************************************************
日 期：2012-3-13
作 者: 郭祥
版 本: v1.3
描 述: 映射数据库表，定期刷新数据。要求被映射的表为单主键，且主键类型为int
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * @author 郭祥
 * @param <T> 泛型
 */
public abstract class AbsDynamicDBBuffer<T>
{

    /**
     * 缓存数据集合，代码中未做curDatas为null的判断
     */
    protected final Map<Integer, T> curDatas;
    /**
     * T类型的CLASS
     */
    protected Class<T> cls;
    /**
     * 调度器
     */
    private Timer timer;
    /**
     * 调度器名称
     */
    protected String timerName;
    /**
     * 调度间隔时间
     */
    private static final long SPAN_TIME = 10 * 1000;
    /**
     * 观察者集合
     */
    private final List<IDataChangeObserver<T>> observers;
    /**
     * 添加操作
     */
    public static final int OP_NUM_ADD = 0;
    /**
     * 删除操作
     */
    public static final int OP_NUM_DEL = 1;
    /**
     * 更新操作
     */
    public static final int OP_NUM_UPDATE = 2;
    /**
     * 未变化操作
     */
    public static final int OP_NUM_UNCHANGE = 3;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AbsDynamicDBBuffer.class);

    /**
     * @param cls 参数
     */
    public AbsDynamicDBBuffer(Class<T> cls)
    {
        timerName = "dynamib_db_timer";
        this.cls = cls;
        curDatas = new LinkedHashMap<Integer, T>();
        observers = new ArrayList<IDataChangeObserver<T>>();
    }

    /**
     * 开始
     */
    public synchronized void start()
    {
        this.removeAll();
        try
        {
            Map<Integer, T> newDatas = this.getNewDatas();
            this.compare(newDatas);
            timer = new Timer(timerName);
            timer.schedule(new UpdateDataThread(), SPAN_TIME, SPAN_TIME);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 结束
     */
    public synchronized void stop()
    {
        this.removeAll();
        try
        {
            if (timer != null)
            {
                timer.cancel();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            timer = null;
        }
    }

    /**
     * 添加观察者
     * @param observer 参数
     */
    public void attach(IDataChangeObserver<T> observer)
    {
        synchronized (observers)
        {
            this.observers.add(observer);
        }
    }

    /**
     * 删除观察者
     * @param observer 参数
     */
    public void detach(IDataChangeObserver<T> observer)
    {
        synchronized (observers)
        {
            this.observers.remove(observer);
        }
    }

    // 缓存的添删改查
    // 添，改，删，已经数据迭代需要同步
    // 查不需要同步
    // 数据迭代在子类里面有扩展
    /**
     * 根据主键值获取数据
     * @param key 键
     * @return 结果
     */
    public T get(int key)
    {
        return curDatas.get(key);
    }

    /**
     * @param key 键
     * @param obj 对象
     * @return 结果
     */
    public T put(int key, T obj)
    {
        synchronized (curDatas)
        {
            curDatas.put(key, obj);
        }
        return obj;
    }

    /**
     * @param key 键
     * @return 结果
     */
    public T remove(int key)
    {
        T retval = null;
        synchronized (curDatas)
        {
            retval = curDatas.remove(key);
        }
        return retval;
    }

    /**
     * 删除所有
     */
    public void removeAll()
    {
        synchronized (curDatas)
        {
            curDatas.clear();
        }
    }

    /**
     * 缓存中的对象和数据库的对象比较 两个对象相同是返回true 两个对象不同时返回false
     * @param oldObj
     * @param newObj
     * @return
     */
    protected boolean compare(T oldObj, T newObj)
    {
        try
        {
            TableInfo tableInfo = TableInfoMgr.getTableInfo(cls);
            for (int i = 0; i < tableInfo.columns.length; i++)
            {
                Object oldValue = tableInfo.getterMethods[i].invoke(oldObj);
                Object newValue = tableInfo.getterMethods[i].invoke(newObj);

                if (!((oldValue == null && newValue == null) || (oldValue != null && newValue != null && oldValue.equals(newValue))))
                {
                    return false;
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            return false;
        }
        return true;
    }

    /**
     * 获取新的数据
     * @return
     */
    protected abstract Map<Integer, T> getNewDatas();

    /**
     * 数据更新，并通知观察者
     */
    private void update()
    {
        Map<Integer, T> newDatas = this.getNewDatas();
        this.compare(newDatas);
    }

    /**
     * 新旧数据比较
     * @param newDatas
     */
    private void compare(Map<Integer, T> newDatas)
    {
        if (newDatas == null || newDatas.isEmpty())
        {
            curDatas.clear();
        }
        List<Integer> oldKeys = this.getCurIds();
        Set<Integer> keys = newDatas.keySet();
        for (Integer key : keys)
        {
            T newData = newDatas.get(key);
            T oldData = this.get(key);
            if (oldData == null)
            {
                // 新增
                this.put(key, newData);
                this.notifyNew(newData);
            }
            else
            {
                if (this.compare(oldData, newData))
                {
                    // 未变
                    this.notifyUnchange(newData);
                }
                else
                {
                    // 修改
                    this.put(key, newData);
                    this.notifyUpdate(newData);
                }
                oldKeys.remove(key);
            }
        }
        if (oldKeys != null && !oldKeys.isEmpty())
        {
            for (Integer oldKey : oldKeys)
            {
                T oldData = this.remove(oldKey);
                this.notifyDel(oldData);
            }
        }
    }

    private void notifyNew(T obj)
    {
        this.notify(obj, OP_NUM_ADD);
    }

    private void notifyDel(T obj)
    {
        this.notify(obj, OP_NUM_DEL);
    }

    private void notifyUpdate(T obj)
    {
        this.notify(obj, OP_NUM_UPDATE);
    }

    private void notifyUnchange(T obj)
    {
        this.notify(obj, OP_NUM_UNCHANGE);
    }

    private void notify(T obj, int opNum)
    {
        // logger.debug(String.format("对象<%s>，操作<%s>", obj, opNum));
        synchronized (observers)
        {
            if (observers == null || observers.isEmpty())
            {
                return;
            }
            for (IDataChangeObserver<T> observer : observers)
            {
                observer.change(obj, opNum);
            }
        }
    }

    private List<Integer> getCurIds()
    {
        List<Integer> retval = new ArrayList<Integer>();
        synchronized (curDatas)
        {
            if (curDatas != null)
            {
                Set<Integer> keys = curDatas.keySet();
                for (Integer key : keys)
                {
                    retval.add(key);
                }
            }
        }
        return retval;
    }

    class UpdateDataThread extends TimerTask
    {

        @Override
        public void run()
        {
            try
            {
                update();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }
}
