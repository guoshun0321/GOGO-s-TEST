package jetsennet.jbmp.autodiscovery;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.buffer.AttribClassBuffer;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AutoDisObjEntity;
import jetsennet.jbmp.log.OperatorLog;

/**
 * @author liwei 代码优化
 */
public class AutoDisLogUtil
{

    /**
     * 是否激活
     */
    private boolean isValid;
    /**
     * 任务ID
     */
    private int taskId;
    /**
     * ID
     */
    private int userId;
    /**
     * 名称
     */
    private String userName;
    /**
     * 属性分类缓存
     */
    protected AttribClassBuffer acBuffer;
    private static final String NEW_MSG = "任务(%s)，新发现对象(%s)，类型(%s)";
    private static final String UPDATE_MSG = "任务(%s)，更新对象(%s)，旧类型(%s)，新类型(%s)，旧状态(%s)，新状态(%s)";
    private static final String DELETE_MSG = "任务(%s)，删除对象(%s)，类型(%s)";
    private static final String MIS_MSG = "任务(%s)，未发现对象(%s)";
    private static final Logger logger = Logger.getLogger(AutoDisLogUtil.class);

    /**
     * 构造函数
     */
    public AutoDisLogUtil()
    {
        acBuffer = new AttribClassBuffer();
        isValid = false;
    }

    /**
     * 激活
     * @param taskId 任务ID
     * @param userId 用户ID
     * @param userName 用户名称
     */
    public void active(int taskId, int userId, String userName)
    {
        this.userId = userId;
        this.userName = userName;
        this.taskId = taskId;
        this.isValid = true;
    }

    /**
     * 记录日志
     * @param msg 参数
     */
    public void logger(String msg)
    {
        if (isValid)
        {
            OperatorLog.log(userId, userName, msg);
        }
    }

    /**
     * @param ip ip
     * @param classId 参数
     */
    public void logNew(String ip, int classId)
    {
        String msg = String.format(NEW_MSG, taskId, ip, this.ensureClass(classId));
        this.logger(msg);
    }

    /**
     * @param ip ip
     * @param oldClass 参数
     * @param newClass 参数
     * @param oldStatus 参数
     * @param newStatus 参数
     */
    public void logUpdate(String ip, int oldClass, int newClass, int oldStatus, int newStatus)
    {
        String msg =
            String.format(UPDATE_MSG, taskId, ip, this.ensureClass(oldClass), this.ensureClass(newClass), ensureStatus(oldStatus),
                ensureStatus(newStatus));
        this.logger(msg);
    }

    /**
     * @param ip ip
     * @param classId 参数
     */
    public void logDelete(String ip, int classId)
    {
        String msg = String.format(DELETE_MSG, taskId, ip, this.ensureClass(classId));
        this.logger(msg);
    }

    /**
     * @param ip ip
     */
    public void logMiss(String ip)
    {
        String msg = String.format(MIS_MSG, taskId, ip);
        this.logger(msg);
    }

    private String ensureClass(int classId)
    {
        String retval = null;
        try
        {
            AttribClassEntity ac = acBuffer.get(classId);
            if (ac == null)
            {
                retval = Integer.toString(classId);
            }
            else
            {
                retval = ac.getClassName();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = Integer.toString(classId);
        }
        return retval;
    }

    private String ensureStatus(int status)
    {
        String retval = null;
        switch (status)
        {
        case AutoDisObjEntity.STATUS_NEW:
            retval = "新增";
            break;
        case AutoDisObjEntity.STATUS_DELETE:
            retval = "删除";
            break;
        case AutoDisObjEntity.STATUS_UPDATE:
            retval = "修改";
            break;
        case AutoDisObjEntity.STATUS_USABLE:
            retval = "可用";
            break;
        case AutoDisObjEntity.STATUS_UNUSABLE:
            retval = "不可用";
            break;
        default:
            retval = Integer.toString(status);
        }
        return retval;
    }
}
