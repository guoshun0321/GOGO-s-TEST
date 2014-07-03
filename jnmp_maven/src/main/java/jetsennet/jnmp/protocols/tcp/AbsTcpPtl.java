/************************************************************************
日 期：2012-3-7
作 者: 郭祥
版 本: v1.3
描 述: 通用TCP连接类，源于OpenNMS项目org.opennms.netmgt.capsd.AbstractTcpPlugin
历 史:
 ************************************************************************/
package jetsennet.jnmp.protocols.tcp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;

import org.apache.log4j.Logger;

import jetsennet.jbmp.protocols.ConnectionConfig;

/**
 * @author 郭祥
 */
public abstract class AbsTcpPtl
{

    protected String protocolName;
    private static final Logger logger = Logger.getLogger(AbsTcpPtl.class);

    /**
     * 构造函数
     */
    public AbsTcpPtl()
    {
        this.protocolName = "TCP";
    }

    /**
     * @param config 参数
     * @return 结果
     */
    public boolean checkConnection(ConnectionConfig config)
    {
        // int timeout = (config.getTimeout() == 0 ? 10 : config.getTimeout());
        int timeout = config.getTimeout() == 0 ? 10 : config.getTimeout();

        boolean isAServer = false;
        for (int attempts = 0; attempts < config.getRetry() && !isAServer; attempts++)
        {
            Socket socket = null;
            try
            {
                // 创建Socket
                socket = new Socket();
                socket.connect(config.getSocketAddress(), timeout);
                socket.setSoTimeout(timeout);
                logger.debug(String.format("%s协议：尝试连接，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()));
                isAServer = checkProtocol(socket, config);
            }
            catch (ConnectException cE)
            {
                logger.debug(String.format("%s协议：连接失败，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()));
                isAServer = false;
            }
            catch (NoRouteToHostException e)
            {
                // 无法到达远程主机，原因是防火墙干扰或者中间路由器停机。
                // 不需要重发
                e.fillInStackTrace();
                logger.debug(String.format("%s协议：无法到达远程主机，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()));
                isAServer = false;
                break;
            }
            catch (InterruptedIOException e)
            {
                logger.debug(String.format("%s协议：连接超时，IP<%s>，端口<%s>，第<%s>次尝试，超时时间<%s>", protocolName, config.getIp(), config.getPort(), attempts,
                    config.getTimeout()));
                isAServer = false;
            }
            catch (IOException e)
            {
                logger.debug(String.format("%s协议：IO异常，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()));
                isAServer = false;
            }
            catch (Throwable t)
            {
                logger.debug(String.format("%s协议：其他错误，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()), t);
                isAServer = false;
            }
            finally
            {
                if (socket != null)
                {
                    closeSocket(socket, config);
                }
            }
        }
        return isAServer;
    }

    protected abstract boolean checkProtocol(Socket socket, ConnectionConfig config) throws Exception;

    protected void closeSocket(Socket socket, ConnectionConfig config)
    {
        try
        {
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException e)
        {
            logger.error("", e);
        }
    }
}
