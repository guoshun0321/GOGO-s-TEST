/************************************************************************
日 期: 2012-2-24
作 者: 郭祥
版 本: v1.3
描 述: 单个对象单个协议扫描结果
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import jetsennet.jbmp.util.XmlUtil;

/**
 * 通过某种协议进行自动发现后的结果
 * @author 郭祥
 */
public class ProResult
{

    /**
     * 协议
     */
    private String proName;
    /**
     * 结果
     */
    private Map<String, String> proResult;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ProResult.class);

    /**
     * @param proName 参数
     */
    public ProResult(String proName)
    {
        this.proName = proName;
        proResult = new LinkedHashMap<String, String>();
    }

    /**
     * 添加结果
     * @param key 键
     * @param value 值
     */
    public void addResult(String key, Object value)
    {
        if (key == null)
        {
            logger.warn("自动发现：添加KEY为NULL的采集结果");
            return;
        }
        if (proResult.containsKey(key))
        {
            logger.info("KEY:<" + key + ">，结果<" + proResult.get(key) + ">被替换成<" + value + ">");
        }
        if (value != null)
        {
            proResult.put(key, value.toString());
        }
        else
        {
            proResult.put(key, "");
        }
    }

    /**
     * 获取对应的值
     * @param key 键
     * @return 结果
     */
    public String getByKey(String key)
    {
        return proResult.get(key);
    }

    /**
     * 转换成XML
     * @return 结果
     */
    public String toXml()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, proName);
        Set<String> keys = proResult.keySet();
        for (String key : keys)
        {
            XmlUtil.appendXmlBegin(sb, key);
            sb.append(proResult.get(key));
            XmlUtil.appendXmlEnd(sb, key);
        }
        XmlUtil.appendXmlEnd(sb, proName);
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return this.toXml();
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the proName
     */
    public String getProName()
    {
        return proName;
    }

    /**
     * @param proName the proName to set
     */
    public void setProName(String proName)
    {
        this.proName = proName;
    }

    /**
     * @return the proResult
     */
    public Map<String, String> getProResult()
    {
        return proResult;
    }

    /**
     * @param proResult the proResult to set
     */
    public void setProResult(Map<String, String> proResult)
    {
        this.proResult = proResult;
    }
    // </editor-fold>
}
