/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.trap.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Guo
 */
public class SendUtil
{

    private static final Logger logger = Logger.getLogger(SendUtil.class);

    /**
     * 发送失败后再次上传的Trap
     * @param ip 目标地址
     * @param port 目标端口
     * @param eventId 上传的ID
     * @param path 文件路径
     * @param name 文件名称
     * @return 0，发送成功；1，发送失败
     */
    public static int sendReUpload(String ip, int port, String eventId, String path, String name)
    {
        int result = 1;
        SendTrap send = null;
        try
        {
            send = new SendTrap();
            send.initComm(ip, port);
            Map<String, String> vb = new HashMap<String, String>();
            vb.put("1.3.6.1.4.1.37073.10.1.50.1", eventId);
            vb.put("1.3.6.1.4.1.37073.10.1.50.2", path);
            vb.put("1.3.6.1.4.1.37073.10.1.50.3", name);
            send.sendPDU(vb);
            result = 0;
        }
        catch (IOException ex)
        {
            logger.error("发送Trap失败", ex);
            result = 1;
        }
        finally
        {
            send.close();
        }
        return result;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        SendUtil.sendReUpload("192.168.8.127", 162, "1", "file://", "xxxfile");
    }
}
