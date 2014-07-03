/************************************************************************
日 期：2012-3-7
作 者: 郭祥
版 本: v1.3
描 述: 用于FTP自动发现，源于OpenNMS开源项目org.opennms.netmgt.capsd.plugins.FtpPlugin类
历 史:
 ************************************************************************/
package jetsennet.jnmp.protocols.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;

import jetsennet.jbmp.protocols.ConnectionConfig;

/**
 * 用于FTP自动发现
 * @author 郭祥
 */
public class FtpPtl extends AbsTcpPtl
{

    /**
     * FTP协议默认端口
     */
    public static final int DEFAULT_PORT = 21;
    /**
     * 协议名称
     */
    private static final String PROTOCOL_NAME = "FTP";
    public static Logger logger = Logger.getLogger(FtpPtl.class);

    /**
     * 参数
     */
    public FtpPtl()
    {
        super();
        this.protocolName = PROTOCOL_NAME;
    }

    @Override
    protected boolean checkProtocol(Socket socket, ConnectionConfig config) throws Exception
    {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        FtpResponse connectResponse = FtpResponse.readResponse(rdr);
        if (!connectResponse.isCodeValid())
        {
            return false;
        }

        FtpResponse.sendCommand(socket, "QUIT");

        FtpResponse quitResponse = FtpResponse.readResponse(rdr);
        if (!quitResponse.isCodeValid())
        {
            return false;
        }

        logger.debug(String.format("%s协议：连接成功，IP<%s>，端口<%s>", protocolName, config.getIp(), config.getPort()));
        return true;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        FtpPtl hp = new FtpPtl();
        boolean isAServer = hp.checkConnection(new ConnectionConfig("192.168.8.62", 21));
        System.out.println(isAServer);
    }
}
