/************************************************************************
日 期：2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: 自动发现接口
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import jetsennet.jbmp.autodiscovery.helper.AbsAutoDisResultHandle;
import jetsennet.jbmp.autodiscovery.helper.AutoDisResult;
import jetsennet.jbmp.autodiscovery.helper.DisAutoIns;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jbmp.util.IPSection;
import jetsennet.jbmp.util.IPUtil;

/**
 * 自动发现接口
 * @author 郭祥
 */
public abstract class AbsAutoDiscover
{

    /**
     * 自动发现。 注意：调用的上一层保证传入的task不为NULL。
     * @param task 任务ID
     * @param userId 用户ID
     * @param userName 用户名称
     * @param isLog 是否将日志记录到数据库
     * @return 结果
     * @throws DiscoverException 异常
     */
    public abstract AutoDisResult discover(AutoDisTaskEntity task, int userId, String userName, boolean isLog) throws DiscoverException;

    /**
     * 验证任务的合法性，同时初始化采集结果
     * @param taskId
     * @return
     * @throws Exception
     */
    protected AutoDisResult initResult(AutoDisTaskEntity task) throws DiscoverException
    {
        AutoDisResult retval = null;
        IPSection sec = IPUtil.checkIP(task.getBeginIp(), task.getEndIp());
        if (sec == null)
        {
            String msg = String.format("IP地址不合法。起始IP<?>，结束IP<?>。", task.getBeginIp(), task.getEndIp());
            throw new DiscoverException(msg);
        }
        retval = new AutoDisResult();
        retval.addIps(sec.getIpList());
        retval.setField1(task.getAddInfo());
        return retval;
    }

    /**
     * 配置结果处理类
     * @param handle
     * @param task
     * @param isLog
     * @param userId
     * @param userName
     */
    protected void setResultHandleParam(AbsAutoDisResultHandle handle, AutoDisTaskEntity task, boolean isLog, int userId, String userName)
    {
        if (task.getIsAutoDis() != AutoDisTaskEntity.IS_AUTOINS_MANU)
        {
            // 激活自动实例化
            handle.setIns(new DisAutoIns());
        }
        if (isLog)
        {
            // 激活日志
            handle.activeLog(userId, userName);
        }
    }
}
