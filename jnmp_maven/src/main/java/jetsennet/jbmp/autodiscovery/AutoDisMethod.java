/************************************************************************
日 期：2011-12-27
作 者: 郭祥
版 本：v1.3
描 述: 远程调用的发生和接收
历 史：
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AutoDisTaskDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jbmp.log.OperatorLog;
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.jbmp.util.ErrorMessageConstant;

/**
 * 远程调用的发生和接收
 * @author 郭祥
 */
public final class AutoDisMethod
{

    private BMPServletContextListener listener;
    /**
     * 自动发现
     */
    private AutoDis dis;
    /**
     * SNMP锁
     */
    private Lock snmpLock;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AutoDisMethod.class);
    // 数据库访问
    private AutoDisTaskDal adtdal;
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static AutoDisMethod instance = new AutoDisMethod();

    private AutoDisMethod()
    {
        dis = new AutoDis();
        snmpLock = new ReentrantLock();
        listener = BMPServletContextListener.getInstance();
        adtdal = ClassWrapper.wrapTrans(AutoDisTaskDal.class);
    }

    public static AutoDisMethod getInstance()
    {
        return instance;
    }

    /**
     * 服务器端调用，用于下发采集任务到采集器。
     * @param taskId 任务ID
     * @param collId 采集ID
     * @param userId 用户id
     * @param userName 用户名称
     * @return null, 成功。other, 自动发现失败的原因。
     */
    public String remoteAutoDis(int taskId, int collId, int userId, String userName)
    {
        OperatorLog.log(userId, userName, String.format("自动发现：任务<%s>，采集器<%s>，准备下发到采集器。", taskId, collId));
        String retval = null;
        try
        {
            if (listener.isOnline(collId))
            {
                listener.callRemote(collId, "remoteAutoDis", new Object[] { taskId, userId, userName }, new Class[] { int.class, int.class,
                    String.class }, false);
                adtdal.updateState(taskId, AutoDisTaskEntity.STATUS_ALLOCATION_SUCCESS);
                OperatorLog.log(userId, userName, String.format("自动发现：任务<%s>，采集器<%s>，下发到采集器成功。", taskId, collId));
            }
            else
            {
                OperatorLog.log(userId, userName, String.format("自动发现：任务<%s>，采集器<%s>，下发到采集器失败，无法连接到采集器。", taskId, collId));
                adtdal.updateState(taskId, AutoDisTaskEntity.STATUS_ALLOCATION_FAILD);
                retval = ErrorMessageConstant.AUTODIS_ERROR_NOCONN;
            }
        }
        catch (Throwable ex)
        {
            OperatorLog.log(userId, userName, String.format("自动发现：任务<%s>，采集器<%s>，下发到采集器失败。", taskId, collId));
            adtdal.updateState(taskId, AutoDisTaskEntity.STATUS_ALLOCATION_FAILD);
            retval = ex.getMessage();
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * @param taskId 任务id
     * @param userId 用户id
     * @param userName 用户名称
     */
    public void disWithLock(int taskId, int userId, String userName)
    {
        Lock l = snmpLock;
        l.lock();
        try
        {
            logger.debug(String.format("自动发现任务<%s>进入锁。", taskId));
            dis.disWithLog(taskId, userId, userName, true);
        }
        catch (Throwable ex)
        {
            logger.error("", ex);
        }
        finally
        {
            logger.debug(String.format("自动发现任务<%s>离开锁。", taskId));
            l.unlock();
        }
    }
}
