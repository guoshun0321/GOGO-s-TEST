/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.util.List;

/**
 * 代码优化改动
 * @author liwei
 */
public class AlarmEventInfo
{
    /**
     * 报警ID
     */
    public int AlarmEvtId;
    /**
     * 报警开始时间
     */
    public long EventTime;
    /**
     * 报警恢复时间
     */
    public long ResumeTime;
    /**
     * 报警持续时间
     */
    public long EventDuration;
    /**
     * 报警属性名称 ATTRIB_NAME
     */
    public String EvtName;
    /**
     * 报警对象名称 OBJ_NAME
     */
    public String ObjName;
    /**
     * 报警对象ID
     */
    public String ObjId;
    /**
     * 报警动作ID
     */
    public List<Integer> ActionIds;
}
