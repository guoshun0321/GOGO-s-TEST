/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: ipv4形式的IP地址的工具类
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本类定义了ipv4地址的3种形式：byte[4]数组形式，String形式以及Long值(0到4294967295)形式。 并定义了ip地址在这三者之间的转换。
 * @author 郭祥
 */
public class IPv4AddressUtil
{
    /*
     * ipv4地址的正则表达式
     */
    public static String IP_REGEX =
        "^((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))$";

    public static String IP_SEC = "((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))";

    public static String IP_WITH_PORT_REGEX = "(" + IP_SEC + "\\." + IP_SEC + "\\." + IP_SEC + "\\." + IP_SEC + "(:\\d{1,5})?)";

    /**
     * 初始化
     */
    public IPv4AddressUtil()
    {
    }

    /**
     * long类型转换为byte数组类型
     * @param lIpAddress long值类型的ip地址
     * @return byte数组类型的ip地址
     */
    public static byte[] longToByte(long lIpAddress)
    {
        byte[] byteIpAddress = new byte[4];
        if (lIpAddress >= 0 && lIpAddress <= 4294967295l)
        {
            byteIpAddress[0] = (byte) (lIpAddress >> 24);
            byteIpAddress[1] = (byte) (lIpAddress >> 16);
            byteIpAddress[2] = (byte) (lIpAddress >> 8);
            byteIpAddress[3] = (byte) lIpAddress;
        }
        else
        {
            byteIpAddress = null;
        }
        return byteIpAddress;
    }

    /**
     * long类型转换为String类型
     * @param lIpAddress long值类型的ip地址
     * @return String类型的ip地址
     */
    public static String longToString(long lIpAddress)
    {
        StringBuilder sb = new StringBuilder();
        if (lIpAddress >= 0 && lIpAddress <= 4294967295l)
        {
            byte[] results = longToByte(lIpAddress);
            for (int i = 0; i < 4; i++)
            {
                sb.append(results[i] & 255);
                if (i != 3)
                {
                    sb.append(".");
                }
            }
        }
        return sb.toString();
    }

    /**
     * byte数组类型转换为Long值类型
     * @param bIpAddress byte数组类型的ip地址
     * @return Long值类型的ip地址
     */
    public static long byteToLong(byte[] bIpAddress)
    {
        if (bIpAddress != null)
        {
            return (((long) bIpAddress[0] & 255) << 24) | (((long) bIpAddress[1] & 255) << 16) | (((long) bIpAddress[2] & 255) << 8)
                | (((long) bIpAddress[3] & 255));
        }
        else
        {
            return -1;
        }
    }

    /**
     * byte数组类型转换为Long值类型
     * @param bIpAddress byte数组类型的ip地址
     * @return Long值类型的ip地址
     */
    public static String byteToString(byte[] bIpAddress)
    {
        return (bIpAddress[0] & 255) + "." + (bIpAddress[1] & 255) + "." + (bIpAddress[2] & 255) + "." + (bIpAddress[3] & 255);
    }

    /**
     * string类型转换为byte数组类型
     * @param stringIpAddress string类型的ip地址
     * @return byte数组类型的ip地址,如果输入值不符合ip地址的形式，返回null
     */
    public static byte[] stringToByte(String sIpAddress)
    {
        byte[] bIpAddress = null;
        Pattern pattern = Pattern.compile(IP_REGEX);
        Matcher m = pattern.matcher(sIpAddress);
        if (m.find())
        {
            bIpAddress = new byte[4];
            bIpAddress[0] = (byte) Long.parseLong(m.group(1));
            bIpAddress[1] = (byte) Long.parseLong(m.group(5));
            bIpAddress[2] = (byte) Long.parseLong(m.group(9));
            bIpAddress[3] = (byte) Long.parseLong(m.group(13));
        }
        else
        {
            return null;
        }
        return bIpAddress;
    }

    /**
     * 是否为合法IP地址
     * @param ipAddress
     * @return
     */
    public static boolean isLegalAddress(String ipAddress)
    {
        if (ipAddress == null || ipAddress.trim().equals(""))
        {
            return false;
        }
        Pattern pattern = Pattern.compile(IP_REGEX);
        Matcher m = pattern.matcher(ipAddress);
        return m.find();
    }

    /**
     * 判断port是否为合法端口
     * @param port
     * @return
     */
    public static boolean isLegalPort(String port)
    {
        Pattern pattern = Pattern.compile("^[1-9][0-9]+$");
        Matcher m = pattern.matcher(port);
        if (m.find())
        {
            int iPort = Integer.valueOf(port);
            if (iPort > 0 && iPort <= 65535)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * String类型转换为byte数组类型
     * @param stringIpAddress bstring类型的ip地址
     * @return long值类型的ip地址,,如果输入值不符合ip地址的形式，返回-1
     */
    public static long stringToLong(String stringIpAddress)
    {
        if (isLegalAddress(stringIpAddress))
        {
            byte[] byteIpAddress = stringToByte(stringIpAddress);
            if (byteIpAddress != null)
            {
                return byteToLong(byteIpAddress);
            }
        }
        return -1;
    }

    /**
     * ip地址和掩码的反码做或操作，得到该ip在指定掩码下的最大ip值
     * @param ip ip地址
     * @param subnetMask 子网掩码
     * @return ip在指定掩码下的最大ip值
     */
    public static byte[] ipAddSubnetMask(byte[] ip, byte[] subnetMask)
    {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            result[i] = (byte) (ip[i] | (~subnetMask[i]));
        }
        return result;
    }

    public static long ipAddSubnetMask(String ip, String subnetMask)
    {
        byte[] byteIpAddress = stringToByte(ip);
        byte[] byteSubnetMask = stringToByte(subnetMask);
        byte[] results = null;
        if (byteIpAddress != null && byteSubnetMask != null)
        {
            results = ipAddSubnetMask(byteIpAddress, byteSubnetMask);
        }
        return byteToLong(results);
    }

    /**
     * 打印MAC地址
     * @param macs
     * @return
     */
    public static String macByte2String(byte[] macs)
    {
        StringBuilder sb = new StringBuilder();
        if (macs != null)
        {
            for (byte b : macs)
            {
                String temp = Integer.toHexString(b & 0xff);
                temp = temp.length() == 1 ? "0" + temp : temp;
                sb.append(temp);
                sb.append(":");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 计算子网IP
     * @param subnetByte
     * @param maskByte
     * @return
     */
    public static byte[] calSubnetIp(byte[] subnetByte, byte[] maskByte)
    {
        byte[] retval = null;
        if (subnetByte != null && maskByte != null && subnetByte.length == 4 && maskByte.length == 4)
        {
            retval = new byte[4];
            retval[0] = (byte) (subnetByte[0] & maskByte[0]);
            retval[1] = (byte) (subnetByte[1] & maskByte[1]);
            retval[2] = (byte) (subnetByte[2] & maskByte[2]);
            retval[3] = (byte) (subnetByte[3] & maskByte[3]);
        }
        return retval;
    }

    public static boolean isInSubnet(byte[] ip, byte[] mask, byte[] subnet)
    {
        for (int i = 0; i < 4; i++)
        {
            if ((ip[i] & mask[i]) != subnet[i])
            {
                return false;
            }
        }
        return true;
    }

    public static boolean isInSubnet(String ipStr, byte[] mask, byte[] subnet)
    {
        byte[] ip = stringToByte(ipStr);
        for (int i = 0; i < 4; i++)
        {
            if ((ip[i] & mask[i]) != subnet[i])
            {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args)
    {
        String ip = "192.168.8.1";
        String mask = "255.255.255.0";
        byte[] temp = IPv4AddressUtil.calSubnetIp(IPv4AddressUtil.stringToByte(ip), IPv4AddressUtil.stringToByte(mask));
        System.out.println(IPv4AddressUtil.byteToString(temp));
    }
}
