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

    @Override
    public int hashCode()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(driver).append("$").append(url).append("$").append(user).append("$").append(pwd);
        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof ConnectionInfo)
        {
            ConnectionInfo temp = (ConnectionInfo) obj;
            if (temp.driver.equals(this.driver) && temp.url.equals(this.url) && temp.user.equals(this.user) && temp.pwd.equals(this.pwd))
            {
                return true;
            }
        }
        return false;
    }

}
