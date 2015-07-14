/************************************************************************
日 期：2011-12-27
作 者: 郭祥
版 本：v1.3
描 述: Trap处理
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.eventhandle.AlarmEventDispatch;
import jetsennet.jbmp.alarm.rule.AbsAlarmRule;
import jetsennet.jbmp.alarm.rule.AbsHistoryData;
import jetsennet.jbmp.alarm.rule.AlarmRuleManager;
import jetsennet.jbmp.alarm.rule.HistoryDataBuffer;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.SyslogDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.entity.SyslogEntity;
import jetsennet.jbmp.syslog.AbsSyslogParse;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;

/**
 * 默认Syslog处理
 * 
 * @author 郭祥
 */
public class SyslogHandle extends AbsCollDataHandle
{

    // 数据库操作
    protected ObjAttribDal oadal;
    protected SyslogDal syslogDal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SyslogHandle.class);

    /**
     * 构造方法
     */
    public SyslogHandle()
    {
        oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        syslogDal = ClassWrapper.wrapTrans(SyslogDal.class);
    }

    @Override
    public void handle(CollData icd, AlarmConfig config)
    {
        String coding = this.ensureCoding(config.getCoding());
        try
        {
            logger.debug("报警模块：收到syslog：" + icd.value);
            // 保存Syslog
            this.save(icd, coding);

            // 确定对象和对象属性
            MObjectEntity mo = this.ensureObject(icd);

            // 维护中的设备不产生报警
            if (mo == null || mo.getObjState() != MObjectEntity.OBJ_STATE_MANAGEABLE)
            {
                return;
            }

            // 报警处理
            this.handle(icd, mo, null, icd.value, config);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    protected void handle(CollData icd, MObjectEntity mo, AbsSyslogParse parse, String msg, AlarmConfig config) throws Exception
    {
        ObjAttribEntity oa = this.ensureObjAttr(icd, mo);
        if (oa != null)
        {
            AbsAlarmRule rule = AlarmRuleManager.getInstance().getRuleByObjAttrId(oa.getObjAttrId());
            if (rule != null)
            {
                AlarmEventEntity event = this.genAlarm(icd.value, rule, mo, oa, icd.time.getTime());
                AlarmEventDispatch.getInstance().handleEvent(config, event);
            }
        }
    }

    /**
     * 确定相匹配的对象属性
     * 
     * @param icd
     * @param mo
     * @return
     * @throws Exception
     */
    protected ObjAttribEntity ensureObjAttr(CollData icd, MObjectEntity mo) throws Exception
    {
        ObjAttribEntity retval = null;
        if (icd.value != null)
        {
            List<ObjAttribEntity> oas = oadal.getSyslogObjAttrib(mo.getObjId());
            for (ObjAttribEntity oa : oas)
            {
                if (oa != null && oa.getAttribValue() != null)
                {
                    String msg = icd.value;
                    String regex = oa.getAttribValue();
                    logger.debug(String.format("报警模块：syslog处理，匹配数据：<%s>和<%s>", msg, regex));
                    if (this.match(regex, msg))
                    {
                        retval = oa;
                        logger.debug("报警模块：确定对象属性：" + oa.getObjAttrId());
                        break;
                    }
                }
            }
        }
        return retval;
    }

    /**
     * 对正则表达式进行匹配操作
     * @param regex
     * @param msg
     * @return
     */
    protected boolean match(String regex, String msg)
    {
        boolean retval = false;
        try
        {
            Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            retval = p.matcher(msg).matches();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    @Override
    public void save(CollData data, String coding) throws Exception
    {
        if (data == null)
        {
            throw new NullPointerException();
        }
        if (data.params == null || data.params.get(CollData.PARAMS_DATA) == null)
        {
            return;
        }
        SyslogEntity obj = (SyslogEntity) data.params.get(CollData.PARAMS_DATA);
        syslogDal.insert(obj);
    }

    /**
     * 确认对象
     * @param idata
     * @return
     * @throws Exception
     */
    protected MObjectEntity ensureObject(CollData idata) throws Exception
    {
        MObjectEntity retval = null;
        String ip = idata.srcIP;
        List<MObjectEntity> mos = mobjBuffer.get(ip);

        // 关联对象，如有多个就关联第一个
        if (mos != null && !mos.isEmpty())
        {
            for (MObjectEntity mo : mos)
            {
                retval = mo;
                break;
            }
        }
        // 未找到对象时报错
        if (retval == null)
        {
            logger.debug(String.format("报警模块：找不到标识为<%s>对象。", ip));
        }
        return retval;
    }

    /**
     * 确认对象属性对应的报警
     * @param objAttrId
     * @return
     * @throws Exception
     */
    protected AbsHistoryData ensureAlarm(int objAttrId) throws Exception
    {
        AbsHistoryData ad = HistoryDataBuffer.getInstance().getAlarmData(objAttrId);

        // 关联报警
        if (ad != null && ad.getRule() != null)
        {
            AbsAlarmRule ar = ad.getRule();
            TwoTuple<AlarmEntity, AlarmLevelEntity> al = ar.firstLevel();
            AlarmEntity alarm = al.first;
            AlarmLevelEntity level = al.second;
            if (alarm == null)
            {
                throw new Exception(String.format("报警模块：对象属性<%s>未关联报警。", objAttrId));
            }
            if (level == null)
            {
                throw new Exception(String.format("报警模块：对象属性<%s>关联的报警无可用级别。", objAttrId));
            }
        }
        else
        {
            throw new Exception(String.format("报警模块：对象属性<%s>未关联报警。", objAttrId));
        }

        return ad;
    }

    private AlarmEventEntity genAlarm(String value, AbsAlarmRule rule, MObjectEntity mo, ObjAttribEntity oa, long collTime)
    {
        AlarmEventEntity retval = null;
        try
        {
            TwoTuple<AlarmEntity, AlarmLevelEntity> temp = rule.firstLevel(new Date());
            AlarmEntity alarm = temp.first;
            AlarmLevelEntity level = temp.second;
            retval = this.alarm(value, oa, mo, alarm, level, collTime);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 生成报警
     * @param value 参数
     * @param oa 参数
     * @param alarm 参数
     * @param level 参数
     * @param collTime 参数
     * @param mo 参数
     * @return 结果
     */
    public AlarmEventEntity alarm(String value, ObjAttribEntity oa, MObjectEntity mo, AlarmEntity alarm, AlarmLevelEntity level, long collTime)
    {
        AlarmEventEntity retval = new AlarmEventEntity();
        retval.setObjAttrId(oa.getObjAttrId());
        retval.setObjId(oa.getObjId());
        retval.setAttribId(oa.getAttribId());
        retval.setCollTime(collTime);
        retval.setCollValue("");
        retval.setResumeTime(0);
        retval.setEventDuration(0);
        retval.setLevelId(level.getLevelId());
        retval.setAlarmId(alarm.getAlarmId());
        retval.setAlarmLevel(level.getAlarmLevel());
        retval.setSubLevel(level.getSubLevel());
        retval.setAlarmType(alarm.getAlarmType());
        retval.setAlarmDesc("");
        retval.setLevelName(level.getLevelName());
        retval.setEventState(AlarmEventEntity.EVENT_STATE_NOTACK);
        retval.setEventDesc(value);
        retval.setEventType(AlarmEventEntity.EVENT_TYPE_TRAP);

        return retval;
    }
}
