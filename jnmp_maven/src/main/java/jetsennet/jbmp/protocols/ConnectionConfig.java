/************************************************************************
日 期：2012-3-7
作 者: 郭祥
版 本: v1.3
描 述: 连接信息
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author 郭祥
 */
public class ConnectionConfig
{

    /**
     * IP地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;
    /**
     * 超时时间（单位：毫秒）
     */
    private int timeout;
    /**
     * 重试次数
     */
    private int retry;
    /**
     * 附加信息
     */
    private Map<String, Object> addInfos;

    /**
     * @param ip ip
     * @param port 端口
     * @param timeout 是就ok
     * @param retry 次数
     * @param addInfos 附件信息
     */
    public ConnectionConfig(String ip, int port, int timeout, int retry, Map<String, Object> addInfos)
    {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
        this.retry = retry;
        this.addInfos = addInfos;
    }

    /**
     * @param ip ip
     * @param port 端口
     */
    public ConnectionConfig(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
        this.timeout = ConnectInfo.TCP_TIMEOUT;
        this.retry = ConnectInfo.TCP_RETRY;
    }

    /**
     * 转换成Socket连接时需要的地址形式
     * @return
     */
    /**
     * @return 结果
     */
    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(ip, port);
    }

    /**
     * @return the ip
     */
    public String getIp()
    {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @return the timeout
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * @return the retry
     */
    public int getRetry()
    {
        return retry;
    }

    /**
     * @param retry the retry to set
     */
    public void setRetry(int retry)
    {
        this.retry = retry;
    }

    /**
     * @return the addInfos
     */
    public Map<String, Object> getAddInfos()
    {
        return addInfos;
    }

    /**
     * @param addInfos the addInfos to set
     */
    public void setAddInfos(Map<String, Object> addInfos)
    {
        this.addInfos = addInfos;
    }
}
