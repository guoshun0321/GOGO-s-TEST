/************************************************************************
日 期：2011-12-27
作 者: 郭祥
版 本：v1.3
描 述: 报警配置
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm;

import java.util.Map;

import jetsennet.jbmp.util.TwoTuple;

/**
 * 报警配置
 * @author 郭祥
 */
public class AlarmConfig
{

    /**
     * 名称
     */
    private String name;
    /**
     * 数据类型
     */
    private int[] types;
    /**
     * 线程池名称
     */
    private String poolName;
    /**
     * 线程池大小
     */
    private int poolSize;
    /**
     * 报警分析类，用于产生报警事件
     */
    private String analysisClass;
    /**
     * 报警事件处理类
     */
    private String eventHandleClass;
    /**
     * 编码，允许为空
     */
    private String coding;
    /**
     * 匹配规则
     */
    private TwoTuple<String, String> match;

    /**
     * 构造方法
     */
    public AlarmConfig()
    {
    }

    @Override
    public String toString()
    {
        String str = "%s配置[线程池名称：%s；线程池大小：%s；报警产生：%s；报警处理：%s；]";
        str = String.format(str, this.name, this.poolName, this.poolSize, this.analysisClass, this.eventHandleClass);
        return str;
    }

    /**
     * 匹配采集数据
     * @param type 类型
     * @param params 参数
     * @return 结果
     */
    public boolean match(int type, Map<String, Object> params)
    {
        boolean result = false;
        if (types == null)
        {
            return false;
        }
        for (int i = 0; i < types.length; i++)
        {
            if (types[i] == type)
            {
                result = true;
                break;
            }
        }
        if (result && match != null && match.first != null && match.second != null)
        {
            Object obj = params.get(match.first);
            if (obj == null || !(obj.toString()).startsWith(match.second))
            {
                result = false;
            }
        }
        return result;
    }

    /**
     * 获取线程池名称
     * @return the poolName
     */
    public String getPoolName()
    {
        return poolName;
    }

    /**
     * @param poolName the poolName to set
     */
    public void setPoolName(String poolName)
    {
        this.poolName = poolName;
    }

    /**
     * @return the analysisClass
     */
    public String getAnalysisClass()
    {
        return analysisClass;
    }

    /**
     * @param analysisClass the analysisClass to set
     */
    public void setAnalysisClass(String analysisClass)
    {
        this.analysisClass = analysisClass;
    }

    /**
     * @return the poolSize
     */
    public int getPoolSize()
    {
        return poolSize;
    }

    /**
     * @param poolSize the poolSize to set
     */
    public void setPoolSize(int poolSize)
    {
        this.poolSize = poolSize;
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
     * @return the types
     */
    public int[] getTypes()
    {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(int[] types)
    {
        this.types = types;
    }

    /**
     * @return the coding
     */
    public String getCoding()
    {
        return coding;
    }

    /**
     * @param coding the coding to set
     */
    public void setCoding(String coding)
    {
        this.coding = coding;
    }

    /**
     * @return the eventHandleClass
     */
    public String getEventHandleClass()
    {
        return eventHandleClass;
    }

    /**
     * @param eventHandleClass the eventHandleClass to set
     */
    public void setEventHandleClass(String eventHandleClass)
    {
        this.eventHandleClass = eventHandleClass;
    }

    /**
     * @return the match
     */
    public TwoTuple<String, String> getMatch()
    {
        return match;
    }

    /**
     * @param match the match to set
     */
    public void setMatch(TwoTuple<String, String> match)
    {
        this.match = match;
    }
}
