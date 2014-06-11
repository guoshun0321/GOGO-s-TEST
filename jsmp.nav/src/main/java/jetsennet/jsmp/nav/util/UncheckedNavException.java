package jetsennet.jsmp.nav.util;

public class UncheckedNavException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    public UncheckedNavException()
    {
        super();
    }

    public UncheckedNavException(String msg)
    {
        super(msg);
    }

    public UncheckedNavException(Throwable t)
    {
        super(t);
    }

    public UncheckedNavException(String msg, Throwable t)
    {
        super(msg, t);
    }

}
