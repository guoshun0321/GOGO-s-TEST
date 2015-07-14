/************************************************************************
日 期：2011-12-27
作 者: 郭祥
版 本：v1.3
描 述: Trap处理
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.Date;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.eventhandle.AlarmEventEntityX;
import jetsennet.jbmp.alarm.rule.AbsAlarmLevel;
import jetsennet.jbmp.alarm.rule.AbsAlarmRule;
import jetsennet.jbmp.alarm.rule.AlarmLevelTrap;
import jetsennet.jbmp.alarm.rule.AlarmRuleManager;
import jetsennet.jbmp.alarm.rule.AlarmRuleTrap;
import jetsennet.jbmp.alarm.rule.DataHandleDef;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.TrapEventDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.entity.TrapEventEntity;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;

/**
 * Trap处理
 * Trap信息储存在BMP_TRAPEVENT表中
 * Trap报警处理分两种：
 * 1、收到Trap就报警
 * 2、对Trap的值进行分析后报警
 * 
 * @author 郭祥
 */
public class TrapHandle extends AbsCollDataHandle
{

    /**
     * Trap解析
     */
    protected TrapParse parse;
    /**
     * 性能报警处理
     */
    protected DataHandleDef perfHandle;
    // 数据库操作
    protected MObjectDal modal;
    protected ObjAttribDal oadal;
    protected TrapEventDal tedal;
    /**
     * 报警事件附带信息，Trap报警事件对应的Trap事件id
     */
    public static final String TRAP_EVENT_ID = "trap.event.id";
    /**
     * 默认的对象属性ID
     */
    public static final int DEFAULT_OBJATTR_ID = -1;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TrapHandle.class);

    /**
     * 构造方法
     */
    public TrapHandle()
    {
        parse = new TrapParse();
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
        oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        tedal = ClassWrapper.wrapTrans(TrapEventDal.class);
        perfHandle = DataHandleDef.getInstance();
    }

    /**
     * trap处理
     * @param idata 参数
     * @param config 参数
     */
    @Override
    public void handle(CollData idata, AlarmConfig config)
    {
        String coding = this.ensureCoding(config.getCoding());

        // 信息解析，确认
        try
        {
            // 解析Trap
            TrapParseEntity trap = TrapParseEntity.genEntity(idata, coding, null);
            String trapStr = trap.toString();
            String ip = idata.srcIP;
            logger.debug("报警模块：收到Trap：<" + ip + ">" + trapStr);

            // 确定对象和对象属性
            TwoTuple<MObjectEntity, ObjAttribEntity> temp = TrapFilter.ensureObj(ip, trap);
            MObjectEntity mo = temp.first;
            if (mo == null)
            {
                throw new Exception(String.format("报警模块：找不到标识为<%s>对象。", ip));
            }

            // 确定对象属性并保存Trap
            ObjAttribEntity oa = temp.second;
            int objAttrId = oa == null ? DEFAULT_OBJATTR_ID : oa.getObjAttrId();
            int trapId = this.saveTrapEvent(mo.getObjId(), objAttrId, trap, trapStr);

            // 维护状态对象不产生报警
            if (mo.getObjState() != MObjectEntity.OBJ_STATE_MANAGEABLE)
            {
                return;
            }

            this.handle(trap, config, idata, mo, oa, trapId);
        }
        catch (Throwable t)
        {
            logger.error("", t);
        }
    }

    /**
     * 产生报警，并发送
     * @param trap
     * @param config
     * @param idata
     * @param mo
     * @param oa
     */
    protected void handle(TrapParseEntity trap, AlarmConfig config, CollData idata, MObjectEntity mo, ObjAttribEntity oa, int trapId)
    {
        try
        {
            if (idata != null && mo != null && oa != null)
            {
                AbsAlarmRule rule = AlarmRuleManager.getInstance().getRuleByObjAttrId(oa.getObjAttrId());

                if (rule != null)
                {
                    trap.parse(mo.getObjId());

                    // 是否产生报警
                    boolean isAlarm = true;
                    AlarmEntity alarm = null;
                    AlarmLevelEntity level = null;
                    String alarmDesc = null;
                    if (rule instanceof AlarmRuleTrap)
                    {
                        alarm = rule.getAlarm();
                        AbsAlarmLevel levelWrapper = ((AlarmRuleTrap) rule).isAlarm(trap);
                        if (levelWrapper == null)
                        {
                            isAlarm = false;
                        }
                        else
                        {
                            level = levelWrapper.getLevel();
                            if (levelWrapper instanceof AlarmLevelTrap)
                            {
                                // 宏替换
                                alarmDesc = ((AlarmLevelTrap) levelWrapper).macroDesc(trap);
                            }
                            else
                            {
                                // 这种情况一般不存在
                                alarmDesc = level.getLevelDesc();
                            }
                        }
                    }
                    else
                    {
                        TwoTuple<AlarmEntity, AlarmLevelEntity> temp = rule.firstLevel(new Date());
                        alarm = temp.first;
                        level = temp.second;
                        alarmDesc = alarm.getAlarmDesc();
                    }

                    if (isAlarm)
                    {
                        AlarmEventEntity event = genAlarm(trap, oa, mo, alarm, level, alarmDesc);

                        // 把Trap事件推送到事件总线
                        AlarmEventEntityX aeex = new AlarmEventEntityX();
                        aeex.setAlarm(event);
                        aeex.setEventType(AlarmEventEntityX.EVENT_TYPE_COMMEN);
                        aeex.addParams(TRAP_EVENT_ID, Integer.toString(trapId));
                        dispatch.handleEvent(config, aeex);
                    }
                }
            }
            else
            {
                logger.warn("报警模块：丢弃数据：" + idata.toString() + "。原因：过滤失败。");
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 生成报警事件
     * @param trap 参数
     * @param oa 参数
     * @param mo 参数
     * @param alarm 参数
     * @param level 参数
     * @return 结果
     */
    private AlarmEventEntity genAlarm(TrapParseEntity trap, ObjAttribEntity oa, MObjectEntity mo, AlarmEntity alarm, AlarmLevelEntity level,
            String alarmDesc)
    {
        trap.parse(mo.getObjId());

        AlarmEventEntity aee = new AlarmEventEntity();
        aee.setObjAttrId(oa.getObjAttrId());
        aee.setObjId(oa.getObjId());
        aee.setAttribId(oa.getAttribId());
        aee.setCollTime(trap.getCollTime());
        aee.setCollValue("");
        aee.setResumeTime(0);
        aee.setEventDuration(0);
        aee.setLevelId(level.getLevelId());
        aee.setAlarmId(alarm.getAlarmId());
        aee.setAlarmLevel(level.getAlarmLevel());
        aee.setSubLevel(level.getSubLevel());
        aee.setAlarmType(alarm.getAlarmType());
        aee.setAlarmDesc("");
        aee.setLevelName(level.getLevelName());
        aee.setEventState(AlarmEventEntity.EVENT_STATE_NOTACK);
        aee.setEventDesc(alarmDesc);
        aee.setEventType(AlarmEventEntity.EVENT_TYPE_TRAP);

        return aee;
    }

    /**
     * 保存数据，将trap信息保存到BMP_TRAPEVENT表
     * @param idata
     * @param objId
     * @param objAttribId
     * @param coding
     * @return
     */
    protected int saveTrapEvent(int objId, int objAttribId, TrapParseEntity trap, String trapStr) throws Exception
    {
        int retval = -1;
        TrapEventEntity te = new TrapEventEntity();
        te.setObjId(objId);
        te.setObjAttrId(objAttribId);
        te.setCollTime(trap.getCollTime());
        te.setTrapTime(trap.getTrapTime());
        te.setTrapOid(trap.getTrapOid());
        te.setTrapValue(trapStr);
        retval = tedal.insert(te);
        return retval;
    }

}
