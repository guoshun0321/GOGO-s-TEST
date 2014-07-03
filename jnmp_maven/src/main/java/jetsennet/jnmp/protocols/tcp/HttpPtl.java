/************************************************************************
日 期：2012-3-7
作 者: 郭祥
版 本: v1.3
描 述: 用于Http协议自动发现，源于org.opennms.netmgt.capsd.plugins.HttpPlugin
历 史:
 ************************************************************************/
package jetsennet.jnmp.protocols.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import jetsennet.jbmp.protocols.ConnectionConfig;

/**
 * @author 郭祥
 */
public class HttpPtl extends AbsTcpPtl
{

    /**
     * 默认端口
     */
    public static final int DEFAULT_PORT = 80;
    /**
     * 发送的消息
     */
    private static final String QUERY_STRING = "GET / HTTP/1.0\r\n\r\n";
    /**
     * 正确返回时的消息头
     */
    private static final String RESPONSE_HEAD_STRING = "HTTP/";
    private static final Logger logger = Logger.getLogger(HttpPtl.class);

    /**
     * 构造方法，初始化
     */
    public HttpPtl()
    {
        super();
        this.protocolName = "HTTP";
    }

    @Override
    protected boolean checkProtocol(Socket socket, ConnectionConfig config) throws Exception
    {
        boolean isAServer = false;

        try
        {
            BufferedReader lineRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            socket.getOutputStream().write(QUERY_STRING.getBytes());
            char[] cbuf = new char[1024];
            int chars = 0;
            StringBuilder response = new StringBuilder();
            try
            {
                while ((chars = lineRdr.read(cbuf, 0, 1024)) != -1)
                {
                    String line = new String(cbuf, 0, chars);
                    // logger.debug(String.format("从socket读取字符串[%s],长度%sbytes。", line.toString(), line.length()));
                    response.append(line);
                }
            }
            catch (java.net.SocketTimeoutException timeoutEx)
            {
                if (timeoutEx.bytesTransferred > 0)
                {
                    String line = new String(cbuf, 0, timeoutEx.bytesTransferred);
                    // logger.debug(String.format("从socket读取字符串[%s],长度%sbytes @ tomeout", line.toString(), line.length()));
                    response.append(line);
                }
            }
            if (response.toString() != null)
            {
                isAServer = true;
                logger.debug(String.format("%s协议：连接成功，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()));
            }
        }
        catch (SocketException e)
        {
            logger.debug(String.format("%s协议：连接失败，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()), e);
            isAServer = false;
        }
        return isAServer;
    }

    /**
     * 主方法测试
     * @param args 参数
     */
    public static void main(String[] args)
    {
        HttpPtl hp = new HttpPtl();
        boolean isAServer = hp.checkConnection(new ConnectionConfig("192.168.8.56", 8080));
        System.out.println(isAServer);
    }
}
