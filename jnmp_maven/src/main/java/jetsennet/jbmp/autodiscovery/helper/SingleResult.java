/************************************************************************
日 期: 2012-2-22
作 者: 郭祥
版 本: v1.3
描 述: 单一对象的扫描结果
历 史: 
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.util.XmlUtil;

/**
 * 单一对象的扫描结果
 * @author 郭祥
 */
public class SingleResult
{

    /**
     * 标识字段
     */
    private String singleKey;
    /**
     * 协议结果
     */
    private List<ProResult> pros;
    /**
     * 协议结果，协议做索引
     */
    private Map<String, ProResult> ip2pros;

    /**
     * 构造方法
     * @param ip ip
     */
    public SingleResult(String ip)
    {
        this.singleKey = ip;
        pros = new ArrayList<ProResult>();
        ip2pros = new LinkedHashMap<String, ProResult>();
    }

    /**
     * 添加协议发现结果
     * @param pr 参数
     */
    public void addProResult(ProResult pr)
    {
        pros.add(pr);
        ip2pros.put(pr.getProName(), pr);
    }

    /**
     * 通过协议去获取协议结果
     * @param pro 参数
     * @return 结果
     */
    public ProResult getByPro(String pro)
    {
        return ip2pros.get(pro);
    }

    /**
     * 转换成XML形式
     * @return 结果
     */
    public String toXml()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "dev");
        XmlUtil.appendXmlBegin(sb, "ip");
        sb.append(singleKey);
        XmlUtil.appendXmlEnd(sb, "ip");
        for (ProResult pro : pros)
        {
            sb.append(pro.toXml());
        }
        XmlUtil.appendXmlEnd(sb, "dev");
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return this.toXml();
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the singleKey
     */
    public String getSingleKey()
    {
        return singleKey;
    }

    /**
     * @param ip ip
     */
    public void setSingleKey(String ip)
    {
        this.singleKey = ip;
    }

    /**
     * @return the pros
     */
    public List<ProResult> getPros()
    {
        return pros;
    }

    /**
     * @param pros the pros to set
     */
    public void setPros(List<ProResult> pros)
    {
        this.pros = pros;
    }
    // </editor-fold>
}
