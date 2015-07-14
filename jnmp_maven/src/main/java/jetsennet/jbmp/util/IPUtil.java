/************************************************************************
日 期：2011-11-30
作 者: 郭祥
版 本：v1.3
描 述: IP相关的工具类
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP相关的工具类
 * @author 郭祥
 */
public class IPUtil
{

    /**
     * MAC地址表达式，格式为xx:xx:xx:xx:xx:xx
     */
    public static String MAC_REGEX = "^(([0-9a-f]{2,2}\\:){5}([0-9a-f]{2,2}))$";

    /**
     * windows上使用的获取IP地址的方法，只能获取一个IP地址
     * @return 结果
     * @throws UnknownHostException 异常
     */
    public static String getIP() throws UnknownHostException
    {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println(address.getHostAddress());
        return address.getHostAddress();
    }

    /**
     * 获取本机全部IP，windows，linux通用
     * @return 结果
     * @throws SocketException 异常
     */
    public static ArrayList<InetAddress> getAllIP() throws SocketException
    {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
        while (interfaces.hasMoreElements())
        {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements())
            {
                InetAddress addr = addrs.nextElement();
                ips.add(addr);
            }
        }
        return ips;
    }

    /**
     * 获取本机的非环回端口IP地址，windows，linux通用
     * @return 结果
     * @throws SocketException 异常
     */
    public static ArrayList<String> getNotLoopIPAddress() throws SocketException
    {
        ArrayList<String> ips = new ArrayList<String>();
        ArrayList<InetAddress> addrs = getAllIP();
        for (InetAddress addr : addrs)
        {
            String ip = addr.getHostAddress();
            if (IPv4AddressUtil.isLegalAddress(ip) && !"127.0.0.1".equals(ip))
            {
                ips.add(ip);
            }
        }
        return ips;
    }

    /**
     * 检查起始和结束IP，生成IPSection
     * @param beginIP 开始ip
     * @param endIP 结束 ip
     * @return 结果
     */
    public static IPSection checkIP(String beginIP, String endIP)
    {
        long begin = IPv4AddressUtil.stringToLong(beginIP);
        long end = IPv4AddressUtil.stringToLong(endIP);
        IPSection section = null;
        if (begin != -1 && end != -1)
        {
            byte[] checker = IPv4AddressUtil.longToByte(begin);
            if (((int) checker[3] & 255) == 0)
            {
                begin++;
            }
            checker = IPv4AddressUtil.longToByte(end);
            if (((int) checker[3] & 255) == 255)
            {
                end--;
            }
            if (begin <= end)
            {
                section = new IPSection(begin, end);
            }
            else
            {
                section = null;
            }
        }
        else
        {
            section = null;
        }
        return section;
    }

    /**
     * 判断是否为合法的mac地址
     * @param mac
     * @return
     */
    public static boolean isLegalMac(String mac)
    {
        if (mac == null || mac.trim().equals(""))
        {
            return false;
        }
        Pattern pattern = Pattern.compile(MAC_REGEX);
        Matcher m = pattern.matcher(mac);
        return m.find();
    }

    public static void main(String[] args)
    {
        System.out.println(IPUtil.isLegalMac("aa:bb:cc:01:23:99"));
        System.out.println(IPUtil.isLegalMac("aa:bb:cc:1:23:99"));
        System.out.println(IPUtil.isLegalMac("aa:bb:cc:01:23"));
        System.out.println(IPUtil.isLegalMac("00:00:00:00:00:00"));
    }
}
