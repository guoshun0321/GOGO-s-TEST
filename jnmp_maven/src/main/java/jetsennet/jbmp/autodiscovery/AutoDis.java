/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 自动发现
历 史：
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.helper.AutoDisResult;
import jetsennet.jbmp.dataaccess.AutoDisTaskDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jbmp.log.OperatorLog;

/**
 * 自动发现模块对外接口。 自动发现模块扩展： 
 * 1、实现具体扫描方式，继承jetsennet.jbmp.autodiscovery.helper.AbsDiscover 
 * 2、实现扫描流程，继承jetsennet.jbmp.autodiscovery.AbsAutoDiscover
 * 3、配置autodis.xml文件
 * 
 * @author 郭祥
 */
public class AutoDis
{

    private AutoDisTaskDal adtdal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AutoDis.class);

    /**
     * 构造函数
     */
    public AutoDis()
    {
        adtdal = ClassWrapper.wrapTrans(AutoDisTaskDal.class);
    }

    /**
     * 自动发现。
     * 
     * @param taskId 任务ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param isLog 是否将日志记录到数据库
     * @return 结果
     */
    public AutoDisResult disWithLog(int taskId, int userId, String userName, boolean isLog)
    {
        AutoDisResult coll = null;
        try
        {
            adtdal.updateState(taskId, AutoDisTaskEntity.STATUS_EXE_ING);
            if (isLog)
            {
                OperatorLog.log(userId, userName, String.format("自动发现任务<%s>开始", taskId));
            }

            // 确定并验证任务
            AutoDisTaskEntity task = this.ensureTask(taskId);

            // 确定扫描流程
            AbsAutoDiscover aad = this.ensureDis(task);

            // 开始扫描
            coll = aad.discover(task, userId, userName, isLog);
            adtdal.updateState(taskId, AutoDisTaskEntity.STATUS_EXE_SUCCESS);
            if (isLog)
            {
                OperatorLog.log(userId, userName, String.format("自动发现任务<%s>结束", taskId));
            }
        }
        catch (Throwable ex)
        {
            logger.error("", ex);
            adtdal.updateState(taskId, AutoDisTaskEntity.STATUS_EXE_FAILD);
            if (isLog)
            {
                OperatorLog.log(userId, userName, String.format("采集任务<%s>失败", taskId));
            }
        }
        return coll;
    }

    /**
     * 获取采集任务
     * @param taskId
     * @return
     */
    private AutoDisTaskEntity ensureTask(int taskId) throws Exception
    {
        AutoDisTaskEntity task = adtdal.get(taskId);
        if (task == null)
        {
            throw new DiscoverException(String.format("找不到ID为<?>的自动发现任务", taskId));
        }
        return task;
    }

    /**
     * 确定采集器
     * @param task
     * @return
     * @throws DiscoverException
     */
    private AbsAutoDiscover ensureDis(AutoDisTaskEntity task) throws DiscoverException
    {
        int taskType = task.getTaskType();
        AutoDisConfig config = AutoDisConfigColl.getInstance().get(taskType);
        if (config == null)
        {
            throw new DiscoverException(String.format("找不到类型(%s)对应的配置。", taskType));
        }
        String className = config.getDisClass();
        if (className == null || className.isEmpty())
        {
            throw new DiscoverException(String.format("类型(%s)对应的类(%s)不合法。", taskType, className));
        }
        Object obj = null;
        try
        {
            obj = Class.forName(className).newInstance();
        }
        catch (Exception ex)
        {
            throw new DiscoverException(String.format("无法实例化类(%s)", className), ex);
        }
        if (!(obj instanceof AbsAutoDiscover))
        {
            throw new DiscoverException(String.format("类(%s)不是jetsennet.jbmp.autodiscovery.AbsAutoDiscover的子类", className));
        }
        return (AbsAutoDiscover) obj;
    }

    /**
     * 主函数
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        AutoDis ad = new AutoDis();
        AutoDisResult ret = ad.disWithLog(12, -1, null, false);
        System.out.println("运行时间为：" + ret.getTime() + "ms");
    }
}
