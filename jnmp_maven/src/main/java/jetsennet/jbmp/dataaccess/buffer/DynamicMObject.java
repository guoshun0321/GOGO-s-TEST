/************************************************************************
日 期：2012-3-16
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess.buffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.AbsDynamicDBBuffer;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.util.ConfigUtil;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;

/**
 * 对象和对象属性缓存
 * @author 郭祥
 */
public final class DynamicMObject
{

    /**
     * 对象缓存，IP为键
     */
    private Map<String, List<MObjectEntity>> ip2list;
    /**
     * 对象缓存，OBJ_ID为键
     */
    private Map<Integer, MObjectEntity> id2obj;
    /**
     * 任务调度
     */
    private ScheduledExecutorService scheduledExe;
    /**
     * 数据读写锁
     */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * 读锁
     */
    private Lock readLock = lock.readLock();
    /**
     * 写锁
     */
    private Lock writeLock = lock.writeLock();
    /**
     * 状态
     */
    private AtomicBoolean isStart;

    private long delay = SPAN_TIME;
    /**
     * 调度间隔时间
     */
    private static final int SPAN_TIME = 10;
    // 数据库访问
    private MObjectDal modal;
    private ObjAttribDal oadal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AbsDynamicDBBuffer.class);

    private static final DynamicMObject instance = new DynamicMObject();

    /**
     * @param cls 参数
     */
    private DynamicMObject()
    {
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
        oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        ip2list = new HashMap<String, List<MObjectEntity>>();
        id2obj = new HashMap<Integer, MObjectEntity>();
        isStart = new AtomicBoolean(false);
        delay = ConfigUtil.getInteger("object.span", SPAN_TIME);
    }

    public static DynamicMObject getInstance()
    {
        return instance;
    }

    /**
     * 开始
     */
    public synchronized void start()
    {
        try
        {
            if (isStart.get())
            {
                return;
            }
            this.update();
            scheduledExe = Executors.newScheduledThreadPool(1);
            scheduledExe.scheduleWithFixedDelay(new UpdateDataThread(), delay, delay, TimeUnit.SECONDS);
            isStart.set(true);
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
        try
        {
            if (!isStart.get())
            {
                return;
            }
            if (scheduledExe != null)
            {
                scheduledExe.shutdownNow();
            }
            isStart.set(false);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            scheduledExe = null;
        }
    }

    public List<MObjectEntity> get(String ip)
    {
        List<MObjectEntity> retval = null;
        if (isStart.get())
        {
            readLock.lock();
            try
            {
                retval = ip2list.get(ip);
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                readLock.unlock();
            }
        }
        else
        {
            logger.info("对象缓存未开启，请检查代码调用情况。");
        }
        return retval;
    }

    public MObjectEntity get(int objId)
    {
        MObjectEntity retval = null;
        if (isStart.get())
        {
            readLock.lock();
            try
            {
                retval = id2obj.get(objId);
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                readLock.unlock();
            }
        }
        else
        {
            logger.info("对象缓存未开启，请检查代码调用情况。");
        }
        return retval;
    }

    private void update()
    {

        TwoTuple<Map<String, List<MObjectEntity>>, Map<Integer, MObjectEntity>> temp = this.refresh();
        writeLock.lock();
        try
        {
            ip2list.clear();
            id2obj.clear();
            this.ip2list = temp.first;
            this.id2obj = temp.second;
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    private TwoTuple<Map<String, List<MObjectEntity>>, Map<Integer, MObjectEntity>> refresh()
    {
        Map<String, List<MObjectEntity>> ip2list = new HashMap<String, List<MObjectEntity>>();
        Map<Integer, MObjectEntity> id2obj = new HashMap<Integer, MObjectEntity>();
        try
        {
            List<MObjectEntity> mos = modal.getAll();
            List<ObjAttribEntity> oas = oadal.getAll();

            for (MObjectEntity mo : mos)
            {
                mo.setAttrs(new ArrayList<ObjAttribEntity>());
                id2obj.put(mo.getObjId(), mo);

                String ip = mo.getIpAddr();
                if (ip != null)
                {
                    List<MObjectEntity> moList = ip2list.get(ip);
                    if (moList == null)
                    {
                        moList = new ArrayList<MObjectEntity>();
                        ip2list.put(ip, moList);
                    }
                    moList.add(mo);
                }
            }

            for (ObjAttribEntity oa : oas)
            {
                int objId = oa.getObjId();
                MObjectEntity mo = id2obj.get(objId);
                if (mo != null)
                {
                    mo.getAttrs().add(oa);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return new TwoTuple<Map<String, List<MObjectEntity>>, Map<Integer, MObjectEntity>>(ip2list, id2obj);
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

    public static void main(String[] args) throws Exception
    {
        DynamicMObject attr = DynamicMObject.getInstance();
        attr.start();
        TimeUnit.SECONDS.sleep(35);
        attr.stop();
        TimeUnit.SECONDS.sleep(30);
        attr.start();
    }
}
