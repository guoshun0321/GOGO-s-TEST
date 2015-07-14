/************************************************************************
日 期: 2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: 自动发现结果集
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.util.XmlUtil;

/**
 * 自动发现结果集
 * @author 郭祥
 */
public class AutoDisResult
{

    /**
     * 自动发现结果
     */
    private List<SingleResult> irs;
    /**
     * 自动发现结果，IP做索引
     */
    private Map<String, SingleResult> ip2ir;
    /**
     * 需要用于自动发现的IP
     */
    private List<String> ips;
    /**
     * 扩展
     */
    private String field1;
    /**
     * 采集时间
     */
    private long time;

    /**
     * 构造方法
     */
    public AutoDisResult()
    {
        ips = new ArrayList<String>();
        irs = new ArrayList<SingleResult>();
        ip2ir = new HashMap<String, SingleResult>();
    }

    /**
     * 添加IP
     * @param ip IP
     */
    public void addIp(String ip)
    {
        SingleResult ir = new SingleResult(ip);
        this.addIpResult(ir);
    }

    /**
     * 添加
     * @param ir 参数
     */
    public void addIpResult(SingleResult ir)
    {
        if (ir == null || ir.getSingleKey() == null)
        {
            return;
        }
        ips.add(ir.getSingleKey());
        irs.add(ir);
        ip2ir.put(ir.getSingleKey(), ir);
    }

    /**
     * 批量添加
     * @param ips 批量ip
     */
    public void addIps(ArrayList<String> ips)
    {
        if (ips == null || ips.isEmpty())
        {
            return;
        }
        for (String ip : ips)
        {
            this.addIp(ip);
        }
    }

    /**
     * 通过IP获取IpResult
     * @param ip ip
     * @return 结果
     */
    public SingleResult getByIp(String ip)
    {
        return ip2ir.get(ip);
    }

    /**
     * 转换成XML
     * @return 结果
     */
    public String toXml()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "auto");
        for (SingleResult ir : irs)
        {
            sb.append(ir.toXml());
        }
        XmlUtil.appendXmlEnd(sb, "auto");
        return sb.toString();
    }

    /**
     * 转换成XML
     * @return 结果
     */
    public String toUsableXml()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "auto");
        for (SingleResult ir : irs)
        {
            if (ir.getPros() != null && !ir.getPros().isEmpty())
            {
                sb.append(ir.toXml());
            }
        }
        XmlUtil.appendXmlEnd(sb, "auto");
        return sb.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the irs
     */
    public List<SingleResult> getIrs()
    {
        return irs;
    }

    /**
     * @param irs the irs to set
     */
    public void setIrs(List<SingleResult> irs)
    {
        this.irs = irs;
    }

    /**
     * @return the ip2ir
     */
    public Map<String, SingleResult> getIp2ir()
    {
        return ip2ir;
    }

    /**
     * @param ip2ir the ip2ir to set
     */
    public void setIp2ir(Map<String, SingleResult> ip2ir)
    {
        this.ip2ir = ip2ir;
    }

    /**
     * @return the ips
     */
    public List<String> getIps()
    {
        return ips;
    }

    /**
     * @param ips the ips to set
     */
    public void setIps(List<String> ips)
    {
        this.ips = ips;
    }

    /**
     * @return the field1
     */
    public String getField1()
    {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    /**
     * @return the time
     */
    public long getTime()
    {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time)
    {
        this.time = time;
    }
    // </editor-fold>
}
