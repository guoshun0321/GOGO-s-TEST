package jetsennet.orm.configuration;

public class ConnectionInfo
{

    public final String driver;

    public final String url;

    public final String user;

    public final String pwd;

    public ConnectionInfo(String driver, String url, String user, String pwd)
    {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }

}
