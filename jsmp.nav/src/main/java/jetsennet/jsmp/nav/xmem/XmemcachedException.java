package jetsennet.jsmp.nav.xmem;

public class XmemcachedException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    public XmemcachedException()
    {
        super();
    }

    public XmemcachedException(String msg)
    {
        super(msg);
    }

    public XmemcachedException(Throwable t)
    {
        super(t);
    }

    public XmemcachedException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
