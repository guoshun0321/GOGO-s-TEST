package jetsennet.orm.configuration;

/**
 * orm模块配置异常，当出现配置异常时抛出。非受查异常。
 * 
 * @author 郭祥
 */
public class ConfigurationException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    public ConfigurationException()
    {
        super();
    }

    public ConfigurationException(String msg)
    {
        super(msg);
    }

    public ConfigurationException(Throwable t)
    {
        super(t);
    }

    public ConfigurationException(String msg, Throwable t)
    {
        super(msg, t);
    }

}
