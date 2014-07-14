package jetsennet.jbmp.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 储存String类型IP地址的数组。 position是一个int类型的变量，初始化为0，最大值为(数组的长度 -1)。 这是一个线程安全的类，主要用于多线程扫描。
 * @author GUO
 */
public class IPSection
{

    /**
     * 储存ip地址的数组
     */
    private List<String> ipList = new ArrayList<String>();
    /**
     * 数组中当前元素的索引值
     */
    private int position;
    private static final Logger logger = Logger.getLogger(IPSection.class);

    /**
     * 初始化数组，设置position为0
     */
    public IPSection()
    {
        ipList = new ArrayList<String>();
        position = 0;
    }

    /**
     * 初始化数组，设置position为0，并在其中添加从begin到end的IP（包括begin，不包括end）
     * @param begin 初始ip地址的long值
     * @param end 结束ip地址的long值
     */
    public IPSection(long begin, long end)
    {
        if (begin > end)
        {
            logger.warn("begin ip > end ip");
            position = 0;
        }
        else
        {
            ipList = new ArrayList<String>((int) (end - begin + 1));
            initIPSection(begin, end);
            position = 0;
        }
    }

    /**
     * 初始化数组，设置position为0，并在其中添加从begin到end的ip
     * @param start 初始ip地址
     * @param last 结束ip地址
     */
    public IPSection(String start, String last)
    {
        long begin = IPv4AddressUtil.stringToLong(start);
        long end = IPv4AddressUtil.stringToLong(last);
        if (begin > end)
        {
            logger.warn("begin ip > end ip");
            position = 0;
        }
        else
        {
            ipList = new ArrayList<String>((int) (end - begin + 1));
            initIPSection(begin, end);
            position = 0;
        }
    }

    /**
     * 清除数组中的全部元素，然后添加从begin到end的ip
     * @param begin 初始ip地址的long值
     * @param end 结束ip地址的long值
     */
    private synchronized void initIPSection(long begin, long end)
    {
        ipList.clear();
        for (long i = begin; i <= end; i++)
        {
            ipList.add(IPv4AddressUtil.longToString(i));
        }
    }

    /**
     * 清除数组中的全部元素，然后添加从begin到end的ip
     * @param begin 初始ip地址
     * @param end 结束ip地址
     */
    public synchronized void initIPSection(String begin, String end)
    {
        long start = IPv4AddressUtil.stringToLong(begin);
        long last = IPv4AddressUtil.stringToLong(end);
        this.initIPSection(start, last);
    }

    /**
     * 获取列表中position位置的ip
     * @return 列表中position位置的ip，如果position大于或等于数组长度，则返回null
     */
    public synchronized String getOneIP()
    {
        String result = null;
        if (position < ipList.size())
        {
            result = ipList.get(position);
            position++;
        }
        return result;
    }

    /**
     * 在数组中添加一个ip
     * @param ip 添加的ip
     */
    public synchronized void addOneIp(String ip)
    {
        ipList.add(ip);
    }

    /**
     * 清理数组，position设为0
     */
    public synchronized void clear()
    {
        ipList.clear();
        position = 0;
    }

    /**
     * 查看数组中是否还有ip未被获取，
     * @return 如果数组中还有ip，返回true。否则，返回false
     */
    public synchronized boolean hasNext()
    {
        if (position >= ipList.size())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 获取数组的长度
     * @return 数组的长度
     */
    public synchronized int size()
    {
        return ipList.size();
    }

    /**
     * 获取IP列表
     * @return 结果
     */
    public synchronized ArrayList<String> getIpList()
    {
        return (ArrayList<String>) ipList;
    }
}
