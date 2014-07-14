/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 保存对象属性的性能数据，报警数据，作为产生报警的依据。
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 对象属性历史数据，产生报警的依据。
 * @author 郭祥
 */
public final class HistoryDataTime extends AbsHistoryData
{

    /**
     * 数据缓存
     */
    private CircularArray<HistoryDataEntry> datas;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(HistoryDataTime.class);

    /**
     * @param objAttrId 对象
     * @param type 类型
     * @param rule 规则
     */
    public HistoryDataTime(int objAttrId, AbsAlarmRule rule)
    {
        super(objAttrId, rule);
        this.init();
    }

    private void init()
    {
        int size = this.rule.sizeOfData();
        datas = new CircularArray<HistoryDataEntry>(HistoryDataEntry.class, size);
    }

    @Override
    public boolean add(String value, Date time)
    {
        datas.add(new HistoryDataEntry(value, time));
        return true;
    }

    @Override
    public boolean clear()
    {
        datas.clear();
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("对象属性ID：");
        sb.append(this.objAttrId);
        sb.append("；规则：");
        sb.append(this.rule);
        return sb.toString();
    }

    public CircularArray<HistoryDataEntry> getDatas()
    {
        return datas;
    }

    public void setDatas(CircularArray<HistoryDataEntry> datas)
    {
        this.datas = datas;
    }

}
