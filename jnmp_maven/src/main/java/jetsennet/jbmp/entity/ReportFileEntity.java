package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "BMP_REPORTFILE")
public class ReportFileEntity
{

    @Id
    @Column(name = "FILE_ID")
    private int fileId;
    @Column(name = "TASK_ID")
    private int taskId;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "FILE_STATE")
    private int fileState;
    @Column(name = "ACTION_STATE")
    private int actionState;
    @Column(name = "FILE_DESC")
    private String fileDesc;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "FIELD_1")
    private String field1;

    /**
     * 构造函数
     */
    public ReportFileEntity()
    {

    }

    public int getFileId()
    {
        return fileId;
    }

    public void setFileId(int fileId)
    {
        this.fileId = fileId;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public int getFileState()
    {
        return fileState;
    }

    public void setFileState(int fileState)
    {
        this.fileState = fileState;
    }

    public int getActionState()
    {
        return actionState;
    }

    public void setActionState(int actionState)
    {
        this.actionState = actionState;
    }

    public String getFileDesc()
    {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc)
    {
        this.fileDesc = fileDesc;
    }

    public String getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getField1()
    {
        return field1;
    }

    public void setField1(String field1)
    {
        this.field1 = field1;
    }
}
