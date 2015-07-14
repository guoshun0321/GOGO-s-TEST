/************************************************************************
日 期：2012-02-22
作 者: 郭祥
版 本：v1.3
描 述: 实例化配置信息
历 史：
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import jetsennet.jbmp.util.XmlUtil;

/**
 * 对象采集信息
 * @author 郭祥
 */
public class AutoDisConfig implements Cloneable
{

    /**
     * 任务类型
     */
    private int taskType;
    /**
     * 实例化类
     */
    private String disClass;
    /**
     * 显示方式
     */
    private int disPlay;
    /**
     * 描述
     */
    private String desc;

    public static final String AUTO_DIS_CONF_TASKTYPE = "type";
    public static final String AUTO_DIS_CONF_CLASS = "disClass";
    public static final String AUTO_DIS_CONF_DISPLAY = "display";
    public static final String AUTO_DIS_CONF_DESC = "discription";

    /**
     * 构造方法
     */
    public AutoDisConfig()
    {
    }

    /**
     * @return 结果
     */
    public String toXml()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "Record");
        XmlUtil.appendTextNode(sb, AUTO_DIS_CONF_TASKTYPE, Integer.toString(taskType));
        XmlUtil.appendTextNode(sb, AUTO_DIS_CONF_CLASS, disClass);
        XmlUtil.appendTextNode(sb, AUTO_DIS_CONF_DISPLAY, Integer.toString(disPlay));
        XmlUtil.appendTextNode(sb, AUTO_DIS_CONF_DESC, desc);
        XmlUtil.appendXmlEnd(sb, "Record");
        return sb.toString();
    }

    /**
     * @return the taskType
     */
    public int getTaskType()
    {
        return taskType;
    }

    /**
     * @param taskType the taskType to set
     */
    public void setTaskType(int taskType)
    {
        this.taskType = taskType;
    }

    /**
     * @return the disClass
     */
    public String getDisClass()
    {
        return disClass;
    }

    /**
     * @param disClass the disClass to set
     */
    public void setDisClass(String disClass)
    {
        this.disClass = disClass;
    }

    /**
     * @return the desc
     */
    public String getDesc()
    {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public int getDisPlay()
    {
        return disPlay;
    }

    public void setDisPlay(int disPlay)
    {
        this.disPlay = disPlay;
    }
}
