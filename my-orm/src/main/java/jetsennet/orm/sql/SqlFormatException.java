package jetsennet.orm.sql;

/**
 * sql拼装异常
 * 
 * @author 郭祥
 */
public class SqlFormatException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    public SqlFormatException()
    {
        super();
    }

    public SqlFormatException(String msg)
    {
        super(msg);
    }

    public SqlFormatException(Throwable t)
    {
        super(t);
    }

    public SqlFormatException(String msg, Throwable t)
    {
        super(msg, t);
    }

}
