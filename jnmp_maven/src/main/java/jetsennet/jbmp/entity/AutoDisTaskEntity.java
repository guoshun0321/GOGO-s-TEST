/************************************************************************
日 期：2012-3-7
作 者: 郭祥
版 本: v1.3
描 述: 自动发现任务表
历 史:
 ************************************************************************/
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 自动发现任务
 * @author 郭祥
 */
@Table(name = "BMP_AUTODISTASK")
public class AutoDisTaskEntity
{

    /**
     * 任务ID
     */
    @Id
    @Column(name = "TASK_ID")
    private int taskId;
    /**
     * 采集器ID
     */
    @Column(name = "COLL_ID")
    private int collId;
    /**
     * 任务类型 0，SNMP扫描 1，中国有线项目金数信WebService
     */
    @Column(name = "TASK_TYPE")
    private int taskType;
    /**
     * 开始IP
     */
    @Column(name = "BEGIN_IP")
    private String beginIp;
    /**
     * 结束IP
     */
    @Column(name = "END_IP")
    private String endIp;
    /**
     * 附加信息
     */
    @Column(name = "ADD_INFO")
    private String addInfo;
    /**
     * 任务状态
     */
    @Column(name = "STATUS")
    private int status;
    /**
     * 共同体
     */
    @Column(name = "COMMUNITY")
    private String community;
    /**
     * 执行类型
     */
    @Column(name = "EXE_TYPE")
    private int exeType;
    /**
     * 星期掩码
     */
    @Column(name = "WEEK_MASK")
    private String weekMask;
    /**
     * 是否自动实例化
     */
    @Column(name = "IS_AUTOINS")
    private int isAutoDis;
    /**
     * 时间点
     */
    @Column(name = "TIME_POINT")
    private String timePoint;
    @Column(name = "FIELD_1")
    private String field1;
    @Column(name = "FIELD_2")
    private String field2;
    /**
     * 自动发现就绪
     */
    public static final int STATUS_READY = 0;
    /**
     * 自动发现开始
     */
    public static final int STATUS_START = 1;
    /**
     * 自动发现下发成功
     */
    public static final int STATUS_ALLOCATION_SUCCESS = 2;
    /**
     * 自动发现下发失败
     */
    public static final int STATUS_ALLOCATION_FAILD = 3;
    /**
     * 自动发现执行中
     */
    public static final int STATUS_EXE_ING = 4;
    /**
     * 自动发现执行成功
     */
    public static final int STATUS_EXE_SUCCESS = 5;
    /**
     * 自动发现执行失败
     */
    public static final int STATUS_EXE_FAILD = 6;
//    /**
//     * 自动发现结束
//     */
//    public static final int STATUS_END = 0;
//    /**
//     * 准备下发
//     */
//    public static final int STATUS_ALLOCATION = 1;
//    /**
//     * 自动发现中
//     */
//    public static final int STATUS_ING = 2;
//    /**
//     * 自动发现失败
//     */
//    public static final int STATUS_FAILD = 3;
    /**
     * 手动执行
     */
    public static final int EXE_TYPE_MANU = 0;
    /**
     * 自动执行
     */
    public static final int EXE_TYPE_AUTO = 1;
    /**
     * 手动实例化
     */
    public static final int IS_AUTOINS_MANU = 0;
    /**
     * 自动实例化
     */
    public static final int IS_AUTOINS_AUTO = 1;

    /**
     * @return the taskId
     */
    public int getTaskId()
    {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    /**
     * @return the collId
     */
    public int getCollId()
    {
        return collId;
    }

    /**
     * @param collId the collId to set
     */
    public void setCollId(int collId)
    {
        this.collId = collId;
    }

    /**
     * @return the beginIp
     */
    public String getBeginIp()
    {
        return beginIp;
    }

    /**
     * @param beginIp the beginIp to set
     */
    public void setBeginIp(String beginIp)
    {
        this.beginIp = beginIp;
    }

    /**
     * @return the endIp
     */
    public String getEndIp()
    {
        return endIp;
    }

    /**
     * @param endIp the endIp to set
     */
    public void setEndIp(String endIp)
    {
        this.endIp = endIp;
    }

    /**
     * @return the status
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * @return the field1
     */
    public String getField1()
    {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    /**
     * @return the field2
     */
    public String getField2()
    {
        return field2;
    }

    /**
     * @param field2 the field2 to set
     */
    public void setField2(String field2)
    {
        this.field2 = field2;
    }

    public String getCommunity()
    {
        return community;
    }

    public void setCommunity(String community)
    {
        this.community = community;
    }

    /**
     * @return the taskType
     */
    public int getTaskType()
    {
        return taskType;
    }

    /**
     * @param taskType the taskType to set
     */
    public void setTaskType(int taskType)
    {
        this.taskType = taskType;
    }

    /**
     * @return the addInfo
     */
    public String getAddInfo()
    {
        return addInfo;
    }

    /**
     * @param addInfo the addInfo to set
     */
    public void setAddInfo(String addInfo)
    {
        this.addInfo = addInfo;
    }

    public int getExeType()
    {
        return exeType;
    }

    public void setExeType(int exeType)
    {
        this.exeType = exeType;
    }

    public String getWeekMask()
    {
        return weekMask;
    }

    public void setWeekMask(String weekMask)
    {
        this.weekMask = weekMask;
    }

    public String getTimePoint()
    {
        return timePoint;
    }

    public void setTimePoint(String timePoint)
    {
        this.timePoint = timePoint;
    }

    public int getIsAutoDis()
    {
        return isAutoDis;
    }

    public void setIsAutoDis(int isAutoDis)
    {
        this.isAutoDis = isAutoDis;
    }

    @Override
    public String toString()
    {
        String str = "任务ID：<%s>，采集器ID：<%s>，任务类型：<%s>，星期掩码：<%S>，时间：<%s>，开始IP：<%s>，结束IP：<%s>，附加信息：<%s>";
        str = String.format(str, this.taskId, this.collId, this.taskType, this.weekMask, this.timePoint, this.beginIp, this.endIp, this.addInfo);
        return str;
    }
}
