/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 报警事件分发，处理对应报警事件模块的调用。
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.AlarmConfigColl;
import jetsennet.jbmp.alarm.AlarmException;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.util.ErrorMessageConstant;

/**
 * 报警事件处理
 * @author 郭祥
 */
public final class AlarmEventDispatch
{

    /**
     * 开始
     */
    private volatile boolean isStart;
    /**
     * 报警处理函数
     */
    private Map<String, AbsAlarmEventHandle> handles;
    /**
     * 锁
     */
    private Lock lock;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AlarmEventDispatch.class);
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static final AlarmEventDispatch instance = new AlarmEventDispatch();

    private AlarmEventDispatch()
    {
        isStart = false;
        lock = new ReentrantLock();

    }

    public static AlarmEventDispatch getInstance()
    {
        return instance;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="事件处理类相关">
    /**
     * 初始化事件处理类
     */
    private void initHandles()
    {
        ArrayList<AlarmConfig> configs = AlarmConfigColl.getInstance().getAll();
        if (configs == null)
        {
            return;
        }
        if (handles == null)
        {
            handles = new HashMap<String, AbsAlarmEventHandle>();
        }
        handles.clear();
        for (int i = 0; i < configs.size(); i++)
        {
            AlarmConfig config = configs.get(i);
            String handleClass = config.getEventHandleClass();
            if (handleClass != null && !"".equals(handleClass.trim()))
            {
                AbsAlarmEventHandle result = handles.get(handleClass);
                if (result == null)
                {
                    try
                    {
                        result = (AbsAlarmEventHandle) Class.forName(handleClass).newInstance();
                        logger.info(String.format("报警模块：初始化报警事件处理类(%s)成功。", handleClass));
                    }
                    catch (Exception ex)
                    {
                        logger.error("", ex);
                        throw new AlarmException(String.format("报警模块：初始化报警事件处理类(%s)失败。", handleClass), ex);
                    }
                    handles.put(handleClass, result);
                }
            }
        }
    }

    /**
     * 获取报警事件处理类
     * @param config
     * @return
     */
    private AbsAlarmEventHandle getEventHandle(AlarmConfig config)
    {
        String handleClass = config.getEventHandleClass();
        return this.getEventHandle(handleClass);
    }

    /**
     * 获取报警事件处理类
     * @param config
     * @return
     */
    private AbsAlarmEventHandle getEventHandle(String handleClass)
    {
        if (handleClass != null)
        {
            AbsAlarmEventHandle result = handles.get(handleClass);
            return result;
        }
        else
        {
            return null;
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="模块开始、阻塞、恢复以及结束">
    /**
     * 开始
     */
    public void start()
    {
        Lock l = lock;
        l.lock();
        try
        {
            isStart = true;
            this.initHandles();
            Set<String> keys = handles.keySet();
            for (String key : keys)
            {
                AbsAlarmEventHandle temp = handles.get(key);
                if (temp != null)
                {
                    temp.start();
                    logger.info(String.format("报警模块：报警事件处理类(%s)启动。", key));
                }
            }
        }
        catch (Exception ex)
        {
            throw new AlarmException(ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 阻塞
     */
    public void block()
    {
        Lock l = lock;
        l.lock();
        try
        {
            Set<String> keys = handles.keySet();
            for (String key : keys)
            {
                AbsAlarmEventHandle temp = handles.get(key);
                if (temp != null)
                {
                    temp.stop();
                }
            }
            logger.info("报警模块：报警事件处理模块阻塞成功。");
        }
        catch (Exception ex)
        {
            throw new AlarmException(ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 恢复
     */
    public void unblock()
    {
        Lock l = lock;
        l.lock();
        try
        {
            Set<String> keys = handles.keySet();
            for (String key : keys)
            {
                AbsAlarmEventHandle temp = handles.get(key);
                if (temp != null)
                {
                    temp.start();
                }
            }
            logger.info("报警模块：报警事件处理模块恢复成功。");
        }
        catch (Exception ex)
        {
            throw new AlarmException(ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 结束
     */
    public void stop()
    {
        Lock l = lock;
        l.lock();
        try
        {
            isStart = false;
            Set<String> keys = handles.keySet();
            for (String key : keys)
            {
                AbsAlarmEventHandle temp = handles.get(key);
                if (temp != null)
                {
                    temp.stop();
                    logger.info(String.format("报警模块：报警事件处理类(%s)停止。", key));
                }
            }
            handles.clear();
            logger.info("报警模块：报警事件处理模块停止成功。");
        }
        catch (Exception ex)
        {
            throw new AlarmException(ex);
        }
        finally
        {
            l.unlock();
        }
    }

    // </editor-fold>

    /**
     * 处理报警事件
     * @param event 事件
     * @param eventType 事件类型
     * @param handleType 处理类型，可以为String和AlarmConfig
     */
    public void handleEvent(AlarmEventEntity event, int eventType, Object handleType)
    {
        if (isStart && event != null)
        {
            AbsAlarmEventHandle handle = this.ensureHandle(handleType);
            if (handle != null)
            {
                AlarmEventEntityX temp = new AlarmEventEntityX();
                temp.setAlarm(event);
                temp.setEventType(eventType);
                handle.put(temp);
            }
        }
    }

    /**
     * 处理事件
     * @param config 参数
     * @param eventx 参数
     */
    public void handleEvent(AlarmConfig config, AlarmEventEntityX eventx)
    {
        if (config == null || eventx == null)
        {
            return;
        }
        if (isStart)
        {
            AbsAlarmEventHandle handle = this.getEventHandle(config);
            if (handle != null)
            {
                handle.put(eventx);
            }
        }
        else
        {
            throw new AlarmException(ErrorMessageConstant.EVENT_HANDLE_NOSTART);
        }
    }

    /**
     * 处理事件
     * @param cls 用于处理的报警的类的全限定名(xxxx.xxxx.xxxx)
     * @param event 参数
     */
    public void handleEvent(String cls, AlarmEventEntity event)
    {
        if (isStart)
        {
            AbsAlarmEventHandle handle = this.getEventHandle(cls);
            if (handle != null)
            {
                handle.put(event);
            }
        }
        else
        {
            throw new AlarmException(ErrorMessageConstant.EVENT_HANDLE_NOSTART);
        }
    }

    /**
     * 处理事件
     * @param config 参数
     * @param event 参数
     */
    public void handleEvent(AlarmConfig config, AlarmEventEntity event)
    {
        if (config == null || event == null)
        {
            return;
        }
        if (isStart)
        {
            event.setConfig(config);
            AbsAlarmEventHandle handle = this.getEventHandle(config);
            if (handle != null)
            {
                handle.put(event);
            }
        }
        else
        {
            throw new AlarmException(ErrorMessageConstant.EVENT_HANDLE_NOSTART);
        }
    }

    /**
     * 恢复前一次报警
     * @param objAttribId 参数
     * @param time 时间
     */
    public void resumeLast(int objAttribId, long time)
    {
        Set<String> keys = handles.keySet();
        for (String key : keys)
        {
            AbsAlarmEventHandle handle = handles.get(key);
            handle.buffer.putRecoverEvent(objAttribId, time);
        }
    }

    /**
     * 移除前一次报警，规则改变
     * @param objAttribId 参数
     * @param time 时间
     */
    public void removeLast(int objAttribId, long time)
    {
        Set<String> keys = handles.keySet();
        for (String key : keys)
        {
            AbsAlarmEventHandle handle = handles.get(key);
            handle.buffer.putRecoverRule(objAttribId);
        }
    }

    /**
     * 手动清理事件，服务器端手动清除
     * @param objAttribId 参数
     * @param eventId 参数
     */
    public void manuClean(int objAttribId, int eventId)
    {
        Set<String> keys = handles.keySet();
        for (String key : keys)
        {
            AbsAlarmEventHandle handle = handles.get(key);
            handle.buffer.putRecoverManu(objAttribId, eventId);
        }
    }

    /**
     * 确定处理线程
     * @param obj
     * @return
     */
    private AbsAlarmEventHandle ensureHandle(Object obj)
    {
        if (obj != null)
        {
            if (obj instanceof String)
            {
                return handles.get((String) obj);
            }
            else if (obj instanceof AlarmConfig)
            {
                return handles.get(((AlarmConfig) obj).getEventHandleClass());
            }
        }
        return null;
    }
}
