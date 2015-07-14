/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: 调用windows自身的ping，来扫描指定主机
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * 调用windows自身的ping，来扫描指定主机
 * @author 郭祥
 */
public class WinPing
{

    public final Logger logger = Logger.getLogger(WinPing.class);

    /**
     * 构造函数
     */
    public WinPing()
    {
    }

    /**
     * @param ip ip
     * @return true表示指定主机为活动主机，false表示指定主机不是活动主机
     */
    public boolean isUsedIPAddress(String ip)
    {
        Process process = null;
        BufferedReader bufReader = null;
        String bufReadLineString = null;
        try
        {
            process = Runtime.getRuntime().exec("ping " + ip + " -w 100 -n 1");
            bufReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (int i = 0; i < 6 && bufReader != null; i++)
            {
                bufReader.readLine();
            }
            bufReadLineString = bufReader.readLine();

            logger.debug("ping \"" + ip + "\" result : " + bufReadLineString);
            if (bufReadLineString == null)
            {
                process.destroy();
                return false;
            }
            if (bufReadLineString.indexOf("timed out") > 0 || bufReadLineString.length() < 17 || bufReadLineString.indexOf("invalid") > 0)
            {
                process.destroy();
                return false;
            }
        }
        catch (IOException e)
        {
            logger.error("", e);
        }
        process.destroy();
        return true;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        WinPing scanner = new WinPing();
        System.out.println(scanner.isUsedIPAddress("192.168.8.145"));
    }
}
