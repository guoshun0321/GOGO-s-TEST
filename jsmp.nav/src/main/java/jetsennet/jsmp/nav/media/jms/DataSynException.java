package jetsennet.jsmp.nav.media.jms;

/**
 * 数据同步异常
 * 
 * @author 郭祥
 */
public class DataSynException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    public DataSynException()
    {
        super();
    }

    public DataSynException(String msg)
    {
        super(msg);
    }

    public DataSynException(Throwable t)
    {
        super(t);
    }

    public DataSynException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
