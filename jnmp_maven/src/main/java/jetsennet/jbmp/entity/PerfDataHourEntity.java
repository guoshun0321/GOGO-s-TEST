/************************************************************************
日 期：2012-03-27
作 者: 梁洪杰
版 本：v1.3
描 述: 按小时性能数据汇总
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.util.Calendar;
import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 按小时性能数据汇总
 * @author 梁洪杰
 */
@Table(name = "BMP_PERFDATAHOUR")
public class PerfDataHourEntity
{
    /**
     * 对象属性ID
     */
    @Column(name = "OBJATTR_ID")
    private int objAttrId;
    /**
     * 对象ID
     */
    @Column(name = "OBJ_ID")
    private int objId;
    /**
     * 采集时间
     */
    @Column(name = "COLL_TIME")
    private long collTime;
    /**
     * 属性值
     */
    @Column(name = "VALUE")
    private double value;
    /**
     * 最大值
     */
    @Column(name = "MAX_VALUE")
    private double maxValue;
    /**
     * 最小值
     */
    @Column(name = "MIN_VALUE")
    private double minValue;

    /**
     * 构造函数
     */
    public PerfDataHourEntity()
    {
    }

    /**
     * @return the objAttrId
     */
    public int getObjAttrId()
    {
        return objAttrId;
    }

    /**
     * @param objAttrId the objAttrId to set
     */
    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    /**
     * @return the objId
     */
    public int getObjId()
    {
        return objId;
    }

    /**
     * @param objId the objId to set
     */
    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public long getCollTime()
    {
        return collTime;
    }

    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public double getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(double maxValue)
    {
        this.maxValue = maxValue;
    }

    public double getMinValue()
    {
        return minValue;
    }

    public void setMinValue(double minValue)
    {
        this.minValue = minValue;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(objId).append("\t").append(objAttrId).append("\t").append(new Date(collTime)).append("\t");
        sb.append(this.minValue).append("\t").append(this.value).append("\t").append(this.maxValue);
        return sb.toString();
    }

    /**
     * @param time 时间
     * @return 更新
     */
    public static String getQuerySql(Long time)
    {
        return String.format("SELECT * FROM BMP_PERFDATAHOUR WHERE COLL_TIME<%s", time);
    }

    /**
     * @param time 时间
     * @return 删除
     */
    public static String getDelSql(Long time)
    {
        return String.format("DELETE FROM BMP_PERFDATAHOUR WHERE COLL_TIME<%s", time);
    }

    public static void main(String[] args)
    {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, -1);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        long startTime = ca.getTimeInMillis();
        long endTime = startTime + 24 * 60 * 60 * 1000 - 1;
        System.out.println(new Date(startTime));
        System.out.println(new Date(endTime));
    }

}
