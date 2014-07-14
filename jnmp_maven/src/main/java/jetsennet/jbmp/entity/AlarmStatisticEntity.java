/**
 * 
 */
package jetsennet.jbmp.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 基于对象和对象组的告警统计信息，虽然是普通的entity对象，但它包含了告警统计的相关逻辑：包括统计内容、更新逻辑、传递逻辑等。这样做的好处是：
 * 1、针对不同的项目，可能会有不同的统计需求，因此可能要重新实现这个类(要保留几个接口)，但只要是基于对象和对象组的告警统计，则该类的改变不会影响到业务逻辑类：AlarmStatistic
 * 2、如果新的统计需求不是基于对象和对象组的告警统计，例如按照告警类型统计、按照告警级别统计，则也可在此基础上进行扩展，业务逻辑类：AlarmStatistic的实现也无需改变。
 * @author lianghongjie 
 */
@Table(name = "ALARM_STATISTIC")
public class AlarmStatisticEntity
{
    private static final String L0 = "L0";
    private static final String L1 = "L1";
    private static final String L2 = "L2";
    private static final String L3 = "L3";
    private static final String L4 = "L4";
    private static final String L5 = "L5";
    private static final String LAST_TIME = "LastUpdateTime";
    private static final String ID = "Id";
    /**
     * 对象或对象组的id
     */
    @Column(name = "OBJ_ID")
    private int id;
    /**
     * 表示对象状态
     */
    @Column(name = "LEVEL50")
    private int level50;
    @Column(name = "LEVEL40")
    private int level40;
    @Column(name = "LEVEL30")
    private int level30;
    @Column(name = "LEVEL20")
    private int level20;
    @Column(name = "LEVEL10")
    private int level10;
    @Column(name = "LEVEL0")
    private int level0;
    /**
     * 用于判断统计是否有更新（如新增、清除、处理、确认、恢复之后就会有更新）
     */
    private long lastUpdateTime;

    private boolean isGroup;
    private List<AlarmStatisticEntity> parentLst;
    private List<AlarmStatisticEntity> childLst;

    /**
     * @return 结果
     */
    public static String getInitSql()
    {
        String sql =
            "SELECT OBJ_ID,"
                + " SUM(CASE WHEN ALARM_LEVEL = 40 THEN 1 ELSE 0 END) AS LEVEL40,"
                + " SUM(CASE WHEN ALARM_LEVEL = 30 THEN 1 ELSE 0 END) AS LEVEL30,"
                + " SUM(CASE WHEN ALARM_LEVEL = 20 THEN 1 ELSE 0 END) AS LEVEL20,"
                + " SUM(CASE WHEN ALARM_LEVEL = 10 THEN 1 ELSE 0 END) AS LEVEL10,"
                + " SUM(CASE WHEN ALARM_LEVEL = 0 THEN 1 ELSE 0 END) AS LEVEL0"
                + " FROM BMP_ALARMEVENT GROUP BY OBJ_ID";
        return sql;
    }

    /**
     * 构造函数
     */
    public AlarmStatisticEntity()
    {
        lastUpdateTime = System.nanoTime();
        this.level50 = MObjectEntity.COLL_STATE_OK;
    }

    /**
     * 更新
     * @param entity 实体
     */
    public void update(AlarmStatisticEntity entity)
    {
        update(entity, System.nanoTime());
    }

    /**
     * 更新，同时向上传递
     * @param entity 实体
     * @param last 时间
     */
    public void update(AlarmStatisticEntity entity, long last)
    {
        if (entity == null)
        {
            return;
        }
        if (this.lastUpdateTime == last)
        {
            return;
        }
        this.level40 += entity.level40;
        this.level30 += entity.level30;
        this.level20 += entity.level20;
        this.level10 += entity.level10;
        this.level0 += entity.level0;
        this.lastUpdateTime = last;

        if (parentLst != null)
        {
            for (AlarmStatisticEntity parent : parentLst)
            {
                parent.update(entity, this.lastUpdateTime);
            }
        }
    }

    /**
     * 增加报警
     * @param alarm 告警
     */
    public void addAlarm(AlarmEventEntity alarm)
    {
        addAlarm(alarm, System.nanoTime());
    }

    /**
     * 增加报警
     * @param alarm 告警
     * @param last 时间
     */
    public void addAlarm(AlarmEventEntity alarm, long last)
    {
        if (alarm == null)
        {
            return;
        }
        if (this.lastUpdateTime == last)
        {
            return;
        }

        switch (alarm.getAlarmLevel())
        {
        case 40:
            level40++;
            break;
        case 30:
            level30++;
            break;
        case 20:
            level20++;
            break;
        case 10:
            level10++;
            break;
        case 0:
            level0++;
            break;
        default:
            break;
        }
        this.lastUpdateTime = last;

        // 更新父对象
        if (parentLst != null)
        {
            for (AlarmStatisticEntity parent : parentLst)
            {
                parent.addAlarm(alarm, this.lastUpdateTime);
            }
        }
    }

    /**
     * 移除报警
     * @param alarm 告警
     */
    public void removeAlarm(AlarmEventEntity alarm)
    {
        removeAlarm(alarm, System.nanoTime());
    }

    /**
     * 移除报警
     * @param alarm 告警
     * @param last 时间
     */
    public void removeAlarm(AlarmEventEntity alarm, long last)
    {
        if (alarm == null)
        {
            return;
        }
        if (this.lastUpdateTime == last)
        {
            return;
        }

        // 更新子对象
        switch (alarm.getAlarmLevel())
        {
        case 40:
            level40--;
            break;
        case 30:
            level30--;
            break;
        case 20:
            level20--;
            break;
        case 10:
            level10--;
            break;
        case 0:
            level0--;
            break;
        default:
            break;
        }
        this.lastUpdateTime = last;

        if (parentLst != null)
        {
            for (AlarmStatisticEntity parent : parentLst)
            {
                parent.removeAlarm(alarm, this.lastUpdateTime);
            }
        }
    }

    /**
     * @return 结果
     */
    public AlarmStatisticEntity reset()
    {
        if (childLst == null)
        {
            return this;
        }
        AlarmStatisticEntity result = new AlarmStatisticEntity();
        result.id = this.id;
        result.isGroup = this.isGroup;
        result.level50 = this.level50;
        result.level40 = this.level40;
        result.level30 = this.level30;
        result.level20 = this.level20;
        result.level10 = this.level10;
        result.level0 = this.level0;
        for (AlarmStatisticEntity child : childLst)
        {
            result.level50 -= child.level50;
            result.level40 -= child.level40;
            result.level30 -= child.level30;
            result.level20 -= child.level20;
            result.level10 -= child.level10;
            result.level0 -= child.level0;
        }
        return result;
    }

    /**
     * 更新报警
     * @param alarm 告警
     */
    public void updateAlarm(AlarmEventEntity alarm)
    {
        updateAlarm(alarm, System.nanoTime());
    }

    /**
     * 更新报警
     * @param alarm 告警
     * @param last 时间
     */
    public void updateAlarm(AlarmEventEntity alarm, long last)
    {
        if (alarm == null)
        {
            return;
        }
        // 模型为图结构，报警更新时可能会出现循环的情况。这里用时间做判断，解决循环的问题。
        if (this.lastUpdateTime == last)
        {
            return;
        }

        this.lastUpdateTime = last;

        if (parentLst != null)
        {
            for (AlarmStatisticEntity parent : parentLst)
            {
                parent.updateAlarm(alarm, this.lastUpdateTime);
            }
        }
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getLevel50()
    {
        return level50;
    }

    public void setLevel50(int level50)
    {
        this.level50 = level50;
        if (level50 == MObjectEntity.COLL_STATE_FAILED && !isGroup && this.childLst != null)
        {
            for (AlarmStatisticEntity child : childLst)
            {
                child.setLevel50(MObjectEntity.COLL_STATE_UNKNOWN1);
            }
        }
    }

    public int getLevel40()
    {
        return level40;
    }

    public void setLevel40(int level40)
    {
        this.level40 = level40;
    }

    public int getLevel30()
    {
        return level30;
    }

    public void setLevel30(int level30)
    {
        this.level30 = level30;
    }

    public int getLevel20()
    {
        return level20;
    }

    public void setLevel20(int level20)
    {
        this.level20 = level20;
    }

    public int getLevel10()
    {
        return level10;
    }

    public void setLevel10(int level10)
    {
        this.level10 = level10;
    }

    public int getLevel0()
    {
        return level0;
    }

    public void setLevel0(int level0)
    {
        this.level0 = level0;
    }

    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean getIsGroup()
    {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup)
    {
        this.isGroup = isGroup;
    }

    /**
     * 添加父对象
     * @param parent 参数
     */
    public void addParent(AlarmStatisticEntity parent)
    {
        if (parentLst == null)
        {
            parentLst = new LinkedList<AlarmStatisticEntity>();
        }
        parentLst.add(parent);
    }

    /**
     * 移除父对象
     * @param parent 参数
     */
    public void removeParent(AlarmStatisticEntity parent)
    {
        if (parentLst == null)
        {
            return;
        }
        parentLst.remove(parent);
    }

    public List<AlarmStatisticEntity> getParentLst()
    {
        return parentLst;
    }

    /**
     * @param child 参数
     */
    public void addChild(AlarmStatisticEntity child)
    {
        if (childLst == null)
        {
            childLst = new LinkedList<AlarmStatisticEntity>();
        }
        childLst.add(child);
    }

    /**
     * @param child 参数
     */
    public void removeChild(AlarmStatisticEntity child)
    {
        if (childLst == null)
        {
            return;
        }
        childLst.remove(child);
    }

    public List<AlarmStatisticEntity> getChildLst()
    {
        return childLst;
    }

    /**
     * 获取对象下的子对象
     * @param objSet 参数
     */
    public void fetchAllChildIds(Set<Integer> objSet)
    {
        if (objSet == null)
        {
            return;
        }
        if (childLst != null)
        {
            for (AlarmStatisticEntity child : childLst)
            {
                objSet.add(child.getId());
            }
        }
    }

    /**
     * 获取对象组下的所有对象及子对象
     * @param objSet 对象id列表
     * @param grpSet 对象组id列表
     */
    public void fetchAllChildIds(Set<Integer> objSet, Set<Integer> grpSet)
    {
        if (childLst != null)
        {
            if (getIsGroup())
            {
                if (grpSet.contains(getId()))
                {
                    return;
                }
                grpSet.add(getId());
            }
            for (AlarmStatisticEntity child : childLst)
            {
                if (!child.getIsGroup())
                {
                    objSet.add(child.getId());
                }
                child.fetchAllChildIds(objSet, grpSet);
            }
        }
    }

    /**
     * @param entity 实体对象
     * @return 结果
     */
    public boolean compareAndSet(AlarmStatisticEntity entity)
    {
        boolean flag = false;
        if (this.lastUpdateTime != entity.lastUpdateTime)
        {
            this.lastUpdateTime = entity.lastUpdateTime;
            flag = true;
        }
        if (this.level0 != entity.level0)
        {
            this.level0 = entity.level0;
            flag = true;
        }
        if (this.level10 != entity.level10)
        {
            this.level10 = entity.level10;
            flag = true;
        }
        if (this.level20 != entity.level20)
        {
            this.level20 = entity.level20;
            flag = true;
        }
        if (this.level30 != entity.level30)
        {
            this.level30 = entity.level30;
            flag = true;
        }
        if (this.level40 != entity.level40)
        {
            this.level40 = entity.level40;
            flag = true;
        }
        if (this.level50 != entity.level50)
        {
            this.level50 = entity.level50;
            flag = true;
        }
        return flag;
    }

    /**
     * @param elm 参数
     */
    public void fromXml(Element elm)
    {
        id = Integer.parseInt(elm.attributeValue(ID));
        String last = elm.attributeValue(LAST_TIME);
        lastUpdateTime = last == null ? 0 : Long.parseLong(last);
        String l5 = elm.attributeValue(L5);
        level50 = l5 == null ? 0 : Integer.parseInt(l5);
        String l4 = elm.attributeValue(L4);
        level40 = l4 == null ? 0 : Integer.parseInt(l4);
        String l3 = elm.attributeValue(L3);
        level30 = l3 == null ? 0 : Integer.parseInt(l3);
        String l2 = elm.attributeValue(L2);
        level20 = l2 == null ? 0 : Integer.parseInt(l2);
        String l1 = elm.attributeValue(L1);
        level10 = l1 == null ? 0 : Integer.parseInt(l1);
        String l0 = elm.attributeValue(L0);
        level0 = l0 == null ? 0 : Integer.parseInt(l0);
    }

    /**
     * @param elm 参数
     */
    public void toXml(Element elm)
    {
        elm.addAttribute(ID, Integer.toString(id));
        elm.addAttribute(LAST_TIME, Long.toString(lastUpdateTime));
        elm.addAttribute(L5, Integer.toString(level50));
        elm.addAttribute(L4, Integer.toString(level40));
        elm.addAttribute(L3, Integer.toString(level30));
        elm.addAttribute(L2, Integer.toString(level20));
        elm.addAttribute(L1, Integer.toString(level10));
        elm.addAttribute(L0, Integer.toString(level0));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof AlarmStatisticEntity))
        {
            return false;
        }
        AlarmStatisticEntity entity = (AlarmStatisticEntity) obj;
        if (entity.getId() == this.getId() && entity.getIsGroup() == this.getIsGroup())
        {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return ((Integer) this.getId()).hashCode();
    }
}
