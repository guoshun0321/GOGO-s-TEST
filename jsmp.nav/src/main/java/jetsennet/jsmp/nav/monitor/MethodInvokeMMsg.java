package jetsennet.jsmp.nav.monitor;

/**
 * 方法调用统计信息
 * 
 * @author 郭祥
 */
public class MethodInvokeMMsg extends MonitorMsg
{

    private String methodName;

    private long startTime;

    private long endTime;

    private boolean isException;

    public MethodInvokeMMsg()
    {
        this.isException = false;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public boolean isException()
    {
        return isException;
    }

    public void setException(boolean isException)
    {
        this.isException = isException;
    }

}
