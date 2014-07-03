/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 数据处理抽象类
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.ArrayList;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.AlarmUtil;
import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.eventhandle.AlarmEventDispatch;
import jetsennet.jbmp.alarm.rule.AbsAlarmRule;
import jetsennet.jbmp.alarm.rule.AbsHistoryData;
import jetsennet.jbmp.alarm.rule.HistoryDataBuffer;
import jetsennet.jbmp.dataaccess.buffer.DynamicMObject;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;

/**
 * 数据处理抽象类。 
 * 处理采集到的数据。 
 * 流程为：过滤，储存，分析并产生告警事件，如果产生了告警事件，将告警传递给告警事件处理模块。 
 * 注意：数据处理类绑定到线程池的每个线程，在第一次初始化后不会初始化。
 * @author 郭祥
 */
public abstract class AbsCollDataHandle implements ICollDataHandle
{

    /**
     * 事件处理
     */
    protected AlarmEventDispatch dispatch;
    /**
     * 对象缓存
     */
    protected DynamicMObject mobjBuffer;
    /**
     * 历史数据
     */
    protected HistoryDataBuffer dataBuffer;
    /**
     * 日志
     */
    private Logger logger = Logger.getLogger(AbsCollDataHandle.class);

    /**
     * 构造函数
     */
    public AbsCollDataHandle()
    {
        dispatch = AlarmEventDispatch.getInstance();
        mobjBuffer = DynamicMObject.getInstance();
        dataBuffer = HistoryDataBuffer.getInstance();
    }

    /**
     * 处理数据
     * @param icd 参数
     * @param config 参数
     */
    @Override
    public void handle(CollData icd, AlarmConfig config)
    {
        // 确定编码
        String coding = AlarmUtil.ensureCoding(config.getCoding());
        try
        {
            int objId = this.ensureObjId(icd);
            if (this.filter(icd, objId))
            {
                this.save(icd, coding);
                this.alarm(config, icd, objId, coding);
            }
            else
            {
                logger.warn("报警模块：丢弃数据：" + icd.toString() + "。原因：过滤失败。");
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 数据过滤
     * @param idata 数据
     * @param objId 对象ID
     * @return 结果
     * @throws Exception 异常
     */
    public boolean filter(CollData idata, int objId) throws Exception
    {
        return true;
    }

    /**
     * 数据储存
     * @param idata 数据
     * @param coding 参数
     * @throws Exception 异常
     */
    public void save(CollData idata, String coding) throws Exception
    {
        throw new UnsupportedOperationException("save()");
    }

    /**
     * 生成报警事件，并将事件发送到事件处理模块。
     * @param config 参数
     * @param idata 参数
     * @param objId 对象ID
     * @param coding 参数
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<AlarmEventEntity> alarm(AlarmConfig config, CollData idata, int objId, String coding) throws Exception
    {
        throw new UnsupportedOperationException("alarm(AlarmConfig config)");
    }

    /**
     * 处理告警事件
     * @param config
     * @param event
     */
    protected void handleEvent(AlarmConfig config, AlarmEventEntity event)
    {
        if (event == null)
        {
            return;
        }
        dispatch.handleEvent(config, event);
    }

    /**
     * 确认编码
     * @param coding
     */
    protected String ensureCoding(String coding)
    {
        return (coding == null || coding.trim().isEmpty()) ? BMPConstants.DEFAULT_SNMP_CODING : coding;
    }

    /**
     * 确定可用的对象ID
     * @param idata
     * @return
     * @throws Exception
     */
    protected int ensureObjId(CollData idata) throws Exception
    {
        int objId = idata.objID;
        if (objId <= 0)
        {
            throw new Exception(String.format("报警模块：不合法的对象ID<%s>", objId));
        }
        return objId;
    }

    protected AbsHistoryData ensureLevel(int objAttrId, AlarmEventEntity event) throws Exception
    {
        AbsHistoryData ad = dataBuffer.getAlarmData(objAttrId);
        if (ad != null && ad.getRule() != null)
        {
            AbsAlarmRule ar = ad.getRule();
            TwoTuple<AlarmEntity, AlarmLevelEntity> al = ar.firstLevel();
            AlarmEntity alarm = al.first;
            AlarmLevelEntity level = al.second;
            if (alarm != null && level != null)
            {
                event.setLevel(alarm, level);
            }
            else
            {
                throw new Exception(String.format("报警模块：对象属性<%s>未匹配报警。", objAttrId));
            }
        }
        else
        {
            throw new Exception(String.format("报警模块：对象属性<%s>未匹配报警。", objAttrId));
        }
        return ad;
    }
}
