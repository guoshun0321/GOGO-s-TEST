package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 定时生成报表任务<br/> 注意：生成报表任务的时间间隔不能小于等于0，最小为1。
 * @author xdh
 */
@Table(name = "BMP_REPORTTIME")
public class ReportTimeEntity
{
    /**
     * 任务ID
     */
    @Id
    @Column(name = "TASK_ID")
    private int taskId;
    /**
     * 任务名称
     */
    @Column(name = "TASK_NAME")
    private String taskName;
    /**
     * 报表ID
     */
    @Column(name = "REPORT_ID")
    private int reportId;
    /**
     * 任务类型。1，单次；2，周期
     */
    @Column(name = "TASK_TYPE")
    private int taskType;
    /**
     * 任务状态。0，新建；1，准备完毕；2，执行中；3，执行结束；10，任务停止。
     */
    @Column(name = "TASK_STATE")
    private int taskState;
    /**
     * 任务开始时间
     */
    @Column(name = "START_TIME")
    private Date startTime;
    /**
     * 任务结束时间
     */
    @Column(name = "END_TIME")
    private Date endTime;
    /**
     * 任务时间间隔
     */
    @Column(name = "COLL_TIMESPAN")
    private int collTimespan;
    /**
     * 日期掩码
     */
    @Column(name = "WEEK_MASK")
    private String weekMask;
    /**
     * 时间掩码
     */
    @Column(name = "HOUR_MASK")
    private String hourMask;
    /**
     * 创建用户
     */
    @Column(name = "CREATE_USER")
    private String createUser;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;
    /**
     * 最新报表生成时间
     */
    @Column(name = "LAST_TIME")
    private Date lastTime;
    /**
     * 报表类型
     */
    @Column(name = "REPORT_TYPE")
    private String reportType;
    /**
     * 报表参数
     */
    @Column(name = "PARAM_FORMAT")
    private String paramFormat;
    /**
     * 报表文件生成格式。0表示excel, 1表示word, 2表示pdf
     */
    @Column(name = "FILE_FORMAT")
    private String fileFormat;
    /**
     * 是否发邮件。0表示不发，1表示发
     */
    @Column(name = "IS_MAIL")
    private int isMail;
    /**
     * 保留字段
     */
    @Column(name = "FIELD_1")
    private String field1;
    /**
     * 单次
     */
    public static final int TASK_TYPE_SINGLE = 2;
    /**
     * 周期
     */
    public static final int TASK_TYPE_PERIOD = 1;
    /**
     * 新建
     */
    public static final int TASK_STATE_NEW = 0;
    /**
     * 准备完毕
     */
    public static final int TASK_STATE_OK = 1;
    /**
     * 执行中
     */
    public static final int TASK_STATE_RUNNING = 2;
    /**
     * 执行结束
     */
    public static final int TASK_STATE_END = 10;
    /**
     * 任务失败
     */
    public static final int TASK_STATE_STOP = 11;

    /**
     * 构造函数
     */
    public ReportTimeEntity()
    {
    }

    /**
     * @return 结果
     */
    public String toLongString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("打印定时报表任务：");
        builder.append("\n任务编号：");
        builder.append(getTaskId());
        builder.append(" 报表ID：");
        builder.append(getReportId());
        builder.append(" 报表类型：");
        builder.append(getReportType());
        builder.append(" 开始时间：");
        builder.append(getStartTime());
        builder.append(" 结束时间：");
        builder.append(getEndTime());
        builder.append(" 时间间隔：");
        builder.append(getCollTimespan());
        builder.append(" 星期：");
        builder.append(getWeekMask());
        builder.append(" 小时：");
        builder.append(getHourMask());
        builder.append(" 任务类型:");
        builder.append(getTaskType());
        builder.append(" 任务状态:");
        builder.append(getTaskState());
        return builder.toString();
    }

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
     * @return the taskName
     */
    public String getTaskName()
    {
        return taskName;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    /**
     * @return the reportId
     */
    public int getReportId()
    {
        return reportId;
    }

    /**
     * @param reportId the reportId to set
     */
    public void setReportId(int reportId)
    {
        this.reportId = reportId;
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
     * @return the taskState
     */
    public int getTaskState()
    {
        return taskState;
    }

    /**
     * @param taskState the taskState to set
     */
    public void setTaskState(int taskState)
    {
        this.taskState = taskState;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime()
    {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime()
    {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    /**
     * @return the collTimespan
     */
    public int getCollTimespan()
    {
        return collTimespan;
    }

    /**
     * @param collTimespan the collTimespan to set
     */
    public void setCollTimespan(int collTimespan)
    {
        this.collTimespan = collTimespan;
    }

    /**
     * @return the weekMask
     */
    public String getWeekMask()
    {
        return weekMask;
    }

    /**
     * @param weekMask the weekMask to set
     */
    public void setWeekMask(String weekMask)
    {
        this.weekMask = weekMask;
    }

    /**
     * @return the hourMask
     */
    public String getHourMask()
    {
        return hourMask;
    }

    /**
     * @param hourMask the hourMask to set
     */
    public void setHourMask(String hourMask)
    {
        this.hourMask = hourMask;
    }

    /**
     * @return the createUser
     */
    public String getCreateUser()
    {
        return createUser;
    }

    /**
     * @param createUser the createUser to set
     */
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    /**
     * @return the lastTime
     */
    public Date getLastTime()
    {
        return lastTime;
    }

    /**
     * @param lastTime the lastTime to set
     */
    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
    }

    /**
     * @return the reportType
     */
    public String getReportType()
    {
        return reportType;
    }

    /**
     * @param reportType the reportType to set
     */
    public void setReportType(String reportType)
    {
        this.reportType = reportType;
    }

    /**
     * @return the objId
     */
    public String getParamFormat()
    {
        return paramFormat;
    }

    /**
     * @param paramFormat the paramFormat to set
     */
    public void setParamFormat(String paramFormat)
    {
        this.paramFormat = paramFormat;
    }

    /**
     * @return the fileFormat
     */
    public String getFileFormat()
    {
        return fileFormat;
    }

    /**
     * @param fileFormat the fileFormat to set
     */
    public void setFileFormat(String fileFormat)
    {
        this.fileFormat = fileFormat;
    }

    /**
     * @return the isMail
     */
    public int getIsMail()
    {
        return isMail;
    }

    /**
     * @param isMail the isMail to set
     */
    public void setIsMail(int isMail)
    {
        this.isMail = isMail;
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
}
