package jetsennet.jsmp.nav.cache.xmem;

public class CacheException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    public CacheException()
    {
        super();
    }

    public CacheException(String msg)
    {
        super(msg);
    }

    public CacheException(Throwable t)
    {
        super(t);
    }

    public CacheException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
