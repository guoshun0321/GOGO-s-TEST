package jetsennet.orm.util;

/**
 * orm模块异常，非受查异常。
 * 
 * @author 郭祥
 */
public class UncheckedOrmException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    public UncheckedOrmException()
    {
        super();
    }

    public UncheckedOrmException(String msg)
    {
        super(msg);
    }

    public UncheckedOrmException(Throwable t)
    {
        super(t);
    }

    public UncheckedOrmException(String msg, Throwable t)
    {
        super(msg, t);
    }

}
