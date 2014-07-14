package jetsennet.jbmp.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.AlarmActionDal;
import jetsennet.jbmp.dataaccess.AlarmDal;
import jetsennet.jbmp.dataaccess.AlarmLevelDal;
import jetsennet.jbmp.dataaccess.AttribAlarmDal;
import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.AlarmActionEntity;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.entity.AttribAlarmEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.util.TwoTuple;
import jetsennet.sqlclient.ISqlExecutor;

import org.apache.log4j.Logger;

public class Update20131114
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(Update20131114.class);

    public void update()
    {
        try
        {
            logger.info("运行本程序前，请保证所有服务器、采集器、报警通知服务器都已经停止运行！！！");
            int a = -1;
            System.out.print("是否开始执行(y/n)：");
            if ((a = System.in.read()) == 'y')
            {
                logger.info("开始执行更新程序!");
                this.updateDB();
                this.updateDBDate();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 修改数据库结构
     */
    public void updateDB()
    {

    }

    /**
     * 修改数据库数据
     */
    public void updateDBDate()
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            exec.transBegin();

            AlarmDal adal = new AlarmDal();
            AlarmLevelDal aldal = new AlarmLevelDal();
            AttributeDal attrDal = new AttributeDal();
            AlarmActionDal actionDal = new AlarmActionDal();
            ObjAttribDal oadal = new ObjAttribDal();
            AttribAlarmDal a2adal = new AttribAlarmDal();

            // 现有系统中的所有报警信息
            logger.debug("获取现有系统中的所有信息。");

            TwoTuple<Map<Integer, AlarmEntity>, Map<Integer, List<Integer>>> temp = adal.getAllAlarmInfo();
            Map<Integer, AlarmEntity> allAlarm = temp.first;
            Map<Integer, List<Integer>> allAction = temp.second;

            // 更新属性相关信息

            // 更新对象属性相关信息
            logger.debug("开始更新对象属性相关的信息。");
            List<AttribAlarmEntity> allA2a = a2adal.getAll();
            Map<Integer, Integer> a2aMap = new HashMap<Integer, Integer>();
            for (AttribAlarmEntity a2a : allA2a)
            {
                a2aMap.put(a2a.getObjattrId(), a2a.getAlarmId());
            }

            Map<Integer, ObjAttribEntity> oaMap = new HashMap<Integer, ObjAttribEntity>();
            List<ObjAttribEntity> allOa = new ArrayList<ObjAttribEntity>();
            // 报警规则出现重复
            List<ObjAttribEntity> repOas =
                oadal
                    .getLst("select * from bmp_objattrib where objattr_id in (select objattr_id from bmp_attribalarm where alarm_id in (select alarm_id from (select alarm_id, count(0) as cn from bmp_attribalarm group by alarm_id  order by alarm_id ) where cn > 1))");
            logger.debug("报警规则重复：" + repOas.size());
            for (ObjAttribEntity oa : repOas)
            {
                if (!oaMap.containsKey(oa.getObjAttrId()))
                {
                    allOa.add(oa);
                    oaMap.put(oa.getObjAttrId(), oa);
                }
            }
            // 对应报警规则为0或负数
            List<ObjAttribEntity> negOas =
                oadal.getLst("select * from bmp_objattrib where objattr_id in (select objattr_id from bmp_attribalarm where alarm_id <= 0)");
            logger.debug("报警规则为0或负数：" + negOas.size());
            for (ObjAttribEntity oa : negOas)
            {
                if (!oaMap.containsKey(oa.getObjAttrId()))
                {
                    allOa.add(oa);
                    oaMap.put(oa.getObjAttrId(), oa);
                }
            }
            // 无对应报警规则的
            List<ObjAttribEntity> nullOas =
                oadal.getLst("select * from bmp_objattrib where objattr_id not in (select objattr_id from bmp_attribalarm)");
            logger.debug("无对应报警规则：" + nullOas.size());
            for (ObjAttribEntity oa : nullOas)
            {
                if (!oaMap.containsKey(oa.getObjAttrId()))
                {
                    allOa.add(oa);
                    oaMap.put(oa.getObjAttrId(), oa);
                }
            }
            logger.debug("总数：" + allOa.size());

            int oaCount = 0;
            for (ObjAttribEntity oa : allOa)
            {
                oaCount++;
                logger.debug("开始更新对象属性：" + oa.getObjattrName());
                Integer alarmIdI = a2aMap.get(oa.getObjAttrId());
                if (alarmIdI != null && allAlarm.containsKey(alarmIdI.intValue()))
                {
                    AlarmEntity alarm = allAlarm.get(alarmIdI.intValue());
                    int tempAlarmId = this.copyAlarm(alarm, allAction);

                    // 属性和新规则关联起来
                    a2adal.delete("delete from bmp_attribalarm where objattr_id = " + oa.getObjAttrId());
                    AttribAlarmEntity aa = new AttribAlarmEntity(oa.getObjAttrId(), tempAlarmId);
                    a2adal.insert(aa);
                }
                else
                {
                    // 不存在旧报警规则
                    AlarmEntity unvalidAlarm = AlarmEntity.newUnValidAlarm("未命名规则");
                    // 插入一条不生效的规则
                    int tempAlarmId = adal.insert(unvalidAlarm);

                    a2adal.delete("delete from bmp_attribalarm where objattr_id = " + oa.getObjAttrId());
                    AttribAlarmEntity aa = new AttribAlarmEntity(oa.getObjAttrId(), tempAlarmId);
                    a2adal.insert(aa);
                }
            }
            logger.debug("更新对象属性结束，总共更新信息：" + oaCount);

            // 清理数据库
            logger.debug("清理多余的指标和报警规则的对应关系");
            DefaultDal.delete("delete from bmp_attribalarm where objattr_id not in (select objattr_id from bmp_objattrib)");

            logger.debug("清理无效的报警级别");
            DefaultDal
                .delete("delete from bmp_alarmlevel where alarm_id not in (select distinct alarm_id from bmp_attribute union select distinct alarm_id from bmp_attribalarm) and alarm_id != 10");

            logger.debug("清理无效的报警规则");
            DefaultDal
                .delete("delete from bmp_alarm where alarm_id not in (select distinct alarm_id from bmp_attribute union select distinct alarm_id from bmp_attribalarm) and alarm_id != 10");

            logger.debug("清理无效的级别和报警动作的对应关系");
            DefaultDal.delete("delete from bmp_alarmaction where level_id not in (select level_id from bmp_alarmlevel)");

            exec.transCommit();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            exec.transRollback();
        }
        finally
        {
            SqlExecutorFacotry.unbindSqlExecutor();
        }
    }

    /**
     * 复制一条报警规则，包括：规则、级别、动作
     * 
     * @param alarm
     * @param allAction
     * @return
     * @throws Exception
     */
    private int copyAlarm(AlarmEntity alarm, Map<Integer, List<Integer>> allAction) throws Exception
    {
        AlarmDal adal = new AlarmDal();
        AlarmLevelDal aldal = new AlarmLevelDal();
        AlarmActionDal actionDal = new AlarmActionDal();

        // 规则
        int tempAlarmId = adal.insert(alarm);
        // 级别
        List<AlarmLevelEntity> levels = alarm.getLevels();
        if (levels != null && !levels.isEmpty())
        {
            for (AlarmLevelEntity level : levels)
            {
                // 插入新的级别
                int levelId = level.getLevelId();
                List<Integer> actionIds = allAction.get(level.getLevelId());
                level.setAlarmId(tempAlarmId);
                int tempLevelId = aldal.insert(level);

                actionDal.delete("delete from bmp_alarmaction where level_id=" + levelId);

                if (actionIds != null)
                {
                    // 动作
                    for (Integer actionId : actionIds)
                    {
                        AlarmActionEntity alarmAction = new AlarmActionEntity();
                        alarmAction.setLevelId(tempLevelId);
                        alarmAction.setActionId(actionId);
                        actionDal.insert(alarmAction);
                    }
                }
            }
        }
        return tempAlarmId;
    }

    public static void main(String[] args)
    {
        Update20131114 update = new Update20131114();
        update.update();
    }

}
