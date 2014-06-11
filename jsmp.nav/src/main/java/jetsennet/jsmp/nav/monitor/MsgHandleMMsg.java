package jetsennet.jsmp.nav.monitor;

public class MsgHandleMMsg extends MonitorMsg
{

    private long startTime;

    private long endTime;

    private String table;

    private String xml;

    private boolean isException;

    public MsgHandleMMsg()
    {
        this.isException = false;
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

    public String getTable()
    {
        return table;
    }

    public void setTable(String table)
    {
        this.table = table;
    }

    public String getXml()
    {
        return xml;
    }

    public void setXml(String xml)
    {
        this.xml = xml;
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
