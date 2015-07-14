/************************************************************************
日 期：2011-12-29
作 者: 郭祥
版 本：v1.3
描 述: 实例化配置信息
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins;

import org.apache.log4j.Logger;

/**
 * 对象采集信息
 * @author Guo
 */
public class InsConfig implements Cloneable
{

    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String classType;
    /**
     * 协议扩展。对于snmp对象，是表BMP_SNMPNODES的DEV_TYPE
     */
    private String proExtend;
    /**
     * 实例化类
     */
    private String insClass;
    /**
     * 匹配类型。0，完全匹配；1，模糊匹配。
     */
    private int matchType;
    /**
     * 是否实例化子对象
     */
    private boolean insSub;
    /**
     * 是否为子对象
     */
    private boolean isSub;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(InsConfig.class);

    /**
     * 构造函数
     */
    public InsConfig()
    {
        matchType = 0;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("名称：").append(this.name).append("\n");
        sb.append("标识：").append(this.classType).append("\n");
        sb.append("是否子对象：").append(this.isSub ? "是" : "否").append("\n");
        sb.append("扩展：").append(this.proExtend).append("\n");
        sb.append("实例化类：").append(this.insClass).append("\n");
        sb.append("匹配方式：").append(this.matchType == 0 ? "完全" : "模糊").append("\n");
        sb.append("实例化子类：").append(this.insSub ? "是" : "否").append("\n");
        return sb.toString();
    }

    @Override
    public Object clone()
    {
        Object config = null;
        try
        {
            config = (InsConfig) super.clone();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return config;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the classType
     */
    public String getClassType()
    {
        return classType;
    }

    /**
     * @param classType the classType to set
     */
    public void setClassType(String classType)
    {
        this.classType = classType;
    }

    /**
     * @return the proExtend
     */
    public String getProExtend()
    {
        return proExtend;
    }

    /**
     * @param proExtend the proExtend to set
     */
    public void setProExtend(String proExtend)
    {
        this.proExtend = proExtend;
    }

    /**
     * @return the insClass
     */
    public String getInsClass()
    {
        return insClass;
    }

    /**
     * @param insClass the insClass to set
     */
    public void setInsClass(String insClass)
    {
        this.insClass = insClass;
    }

    /**
     * @return the matchType
     */
    public int getMatchType()
    {
        return matchType;
    }

    /**
     * @param matchType the matchType to set
     */
    public void setMatchType(int matchType)
    {
        this.matchType = matchType;
    }

    public boolean isInsSub()
    {
        return insSub;
    }

    public void setInsSub(boolean insSub)
    {
        this.insSub = insSub;
    }

    public boolean isSub()
    {
        return isSub;
    }

    public void setSub(boolean isSub)
    {
        this.isSub = isSub;
    }
}
