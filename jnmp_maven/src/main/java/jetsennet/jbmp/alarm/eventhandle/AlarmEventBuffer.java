/************************************************************************
日 期：2012-06-05
作 者: 郭祥
版 本：v1.3
描 述: 报警事件缓存
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 报警事件缓存。
 * 
 * @author 郭祥
 */
public final class AlarmEventBuffer
{

    /**
     * 对象属性ID与该对象属性的最后一次报警。<对象属性ID， 事件>
     */
    private Map<Integer, AlarmEventEntity> id2event;
    // 数据库操作
    protected AlarmEventDal aedao;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AlarmEventBuffer.class);
    // 单例
    private static final AlarmEventBuffer instance = new AlarmEventBuffer();

    private AlarmEventBuffer()
    {
        id2event = new HashMap<Integer, AlarmEventEntity>();
        aedao = ClassWrapper.wrapTrans(AlarmEventDal.class);
    }

    public static AlarmEventBuffer getInstance()
    {
        return instance;
    }

    /**
     * 获取报警。第一次调用时，会从数据库取该对象属性最后一次未恢复的报警。
     * 如果报警存在，将该报警存储在缓存中
     * 如果报警不存在，将EMPTY_ALARM存储在缓存中
     * @param objAttrId 参数
     * @return 结果
     */
    public synchronized AlarmEventEntity get(int objAttrId)
    {
        AlarmEventEntity retval = null;

        if (id2event.containsKey(objAttrId))
        {
            retval = id2event.get(objAttrId);
        }
        else
        {
            // 从数据库取该对象属性的最后一次未恢复报警
            retval = aedao.getLastAlarm(objAttrId);
            logger.debug(String.format("报警模块：从数据库取对象属性<%s>最后一次未恢复的报警:<%s>。", objAttrId, retval));
            id2event.put(objAttrId, retval);
        }
        return retval;
    }

    /**
     * 插入新报警
     * @param objAttrId 参数
     * @param alarm 报警
     */
    public synchronized void insert(int objAttrId, AlarmEventEntity alarm)
    {
        id2event.put(alarm.getObjAttrId(), alarm);
    }

    /**
     * 更新旧报警
     * @param alarm 报警
     * @param objAttrId 参数
     */
    public synchronized void update(int objAttrId, AlarmEventEntity alarm)
    {
        id2event.put(objAttrId, alarm);
    }

    /**
     * 删除报警
     * @param objAttrId 参数
     * @return 结果
     */
    public synchronized AlarmEventEntity delete(int objAttrId)
    {
        AlarmEventEntity retval = id2event.get(objAttrId);

        // 判读retval是否为空，是为了判断objAttrId对应的报警是否有从数据库中取出
        if (retval != null)
        {
            id2event.put(objAttrId, null);
        }
        return retval;
    }

    /**
     * 删除ALARMEVT_ID对应的报警
     * @param objAttrId 对象属性ID
     * @param eventId 事件ID
     * @return
     */
    public synchronized AlarmEventEntity delete(int objAttrId, int eventId)
    {
        AlarmEventEntity retval = id2event.get(objAttrId);

        // 判读retval是否为空，是为了判断objAttrId对应的报警是否有从数据库中取出
        if (retval != null && retval.getAlarmEvtId() == eventId)
        {
            id2event.put(objAttrId, null);
        }
        return retval;
    }

    /**
     * 清空缓存
     */
    public synchronized void clear()
    {
        this.id2event.clear();
    }

}
