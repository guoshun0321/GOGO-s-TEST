package jetsennet.jbmp.util;

public class UncheckedJbmpException extends RuntimeException
{

    public UncheckedJbmpException(String msg)
    {
        super(msg);
    }
    
    public UncheckedJbmpException(String msg, Exception ex)
    {
        super(msg, ex);
    }

}
