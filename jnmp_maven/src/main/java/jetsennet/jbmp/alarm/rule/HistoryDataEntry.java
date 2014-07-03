/************************************************************************
日 期: 2012-1-12
作 者: 郭祥
版 本: v1.3
描 述: 性能和报警数据缓存
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import java.util.Date;

/**
 * 性能和报警数据缓存
 * @author 郭祥
 */
public class HistoryDataEntry
{

    /**
     * 性能数据
     */
    public String value;
    /**
     * 采集时间
     */
    public Date time;

    /**
     * 构造方法
     */
    public HistoryDataEntry()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * 构造方法
     * @param value 值
     * @param time 时间
     */
    public HistoryDataEntry(String value, Date time)
    {
        this.value = value;
        this.time = time;
    }

    /**
     * 构造方法
     * @param value 值
     */
    public HistoryDataEntry(String value)
    {
        this.value = value;
        this.time = new Date();
    }

}
