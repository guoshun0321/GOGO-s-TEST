/************************************************************************
日 期: 2012-1-12
作 者: 郭祥
版 本: v1.3
描 述: 将数据库中的报警信息映射到内存，并定期刷新
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jetsennet.jbmp.dataaccess.AlarmDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.entity.AttribAlarmEntity;
import jetsennet.jbmp.util.ConfigUtil;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;

/**
 * 将数据库中的报警信息映射到内存，并定期刷新 
 * 包含对象属性ID和报警ID的关系，以及报警ID和对应报警规则的关系
 * 线程安全
 * 
 * @author 郭祥
 */
public final class AlarmRuleManager
{

    /**
     * 报警ID对应报警
     */
    private Map<Integer, AbsAlarmRule> curRules;
    /**
     * 对象属性ID对应报警ID
     */
    private Map<Integer, Integer> curRels;
    /**
     * 规则生成器
     */
    private RuleBuilder builder;
    /**
     * 任务调度
     */
    private ScheduledExecutorService scheduledExe;
    /**
     * 模块是否开始
     */
    private boolean isStart;
    /**
     * 数据库操作
     */
    private AlarmDal adal;
    /**
     * 默认的间隔时间
     */
    private long delay = UPDATE_RULE_SPAN;
    /**
     * 数据锁，数据访问同步
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    /**
     * 定时更新时间，10秒
     */
    private static int UPDATE_RULE_SPAN = 10;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AlarmRuleManager.class);

    private static AlarmRuleManager instance = new AlarmRuleManager();

    private AlarmRuleManager()
    {
        this.curRules = new HashMap<Integer, AbsAlarmRule>();
        this.curRels = new HashMap<Integer, Integer>();
        this.adal = ClassWrapper.wrapTrans(AlarmDal.class);
        this.builder = RuleBuilder.getInstance();
        this.isStart = false;
        delay = ConfigUtil.getInteger("alarmrule.span", UPDATE_RULE_SPAN);
    }

    public static AlarmRuleManager getInstance()
    {
        return instance;
    }

    /**
     * 模块开始
     * @throws Exception 异常
     */
    public synchronized void start() throws Exception
    {
        try
        {
            if (isStart)
            {
                return;
            }
            this.isStart = true;
            logger.info("报警模块：初始化报警规则缓存。");
            this.fresh(true);
            scheduledExe = Executors.newScheduledThreadPool(1);
            scheduledExe.scheduleWithFixedDelay(new UpdateRuleThread(), delay, delay, TimeUnit.SECONDS);
            logger.info("报警模块：更新报警规则线程启动，" + delay + "秒更新一次报警规则。");
            logger.info("报警模块：报警规则模块启动。");
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw ex;
        }
    }

    /**
     * 模块结束
     * @throws Exception 异常
     */
    public synchronized void stop() throws Exception
    {
        try
        {
            if (!isStart)
            {
                return;
            }
            isStart = false;
            if (scheduledExe != null)
            {
                scheduledExe.shutdownNow();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw ex;
        }
        finally
        {
            scheduledExe = null;
        }
    }

    /**
     * 根据对象属性ID获取规则
     * @param objAttrId 对象属性ID
     * @return 结果
     */
    public AbsAlarmRule getRuleByObjAttrId(int objAttrId)
    {
        AbsAlarmRule retval = null;
        Lock l = this.readLock;
        l.lock();
        try
        {
            Integer alarmId = curRels.get(objAttrId);
            if (alarmId != null)
            {
                retval = curRules.get(alarmId);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
        }
        return retval;
    }

    /**
     * 获取一个报警规则的所有信息，包括规则和级别
     * 
     * @param alarmId 参数
     * @return 结果
     */
    public TwoTuple<AlarmEntity, AlarmLevelEntity> getFirstLevel(int alarmId)
    {
        TwoTuple<AlarmEntity, AlarmLevelEntity> retval = null;
        Lock l = this.readLock;
        l.lock();
        try
        {
            AbsAlarmRule temp = curRules.get(alarmId);
            if (temp != null)
            {
                retval = temp.firstLevel();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
        }
        return retval;
    }

    /**
     * 刷新
     * @throws Exception
     */
    private void fresh(boolean isInit) throws Exception
    {
        try
        {
            if (!isStart)
            {
                logger.error("报警模块：规则更新未开启。");
                return;
            }
            TwoTuple<ArrayList<AlarmEntity>, ArrayList<AttribAlarmEntity>> aa = adal.getAllAlarms();
            ArrayList<AlarmEntity> alarms = aa.first;
            Map<Integer, AbsAlarmRule> newRules = this.freshAlarm(alarms, isInit);
            ArrayList<AttribAlarmEntity> aas = aa.second;
            Map<Integer, Integer> newRels = this.freshAttrAlarm(aas);

            Lock l = this.writeLock;
            l.lock();
            try
            {
                this.curRules = newRules;
                this.curRels = newRels;
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                l.unlock();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw ex;
        }
    }

    /**
     * 刷新报警规则
     * @param iAlarms
     */
    private Map<Integer, AbsAlarmRule> freshAlarm(ArrayList<AlarmEntity> iAlarms, boolean isInit)
    {
        Map<Integer, AbsAlarmRule> newRules = new HashMap<Integer, AbsAlarmRule>();

        // 无报警规则时，返回空
        if (iAlarms == null)
        {
            return newRules;
        }

        // 缓存内报警的ID集合
        ArrayList<Integer> oris = new ArrayList<Integer>();
        oris.addAll(curRules.keySet());

        // 更新报警
        for (AlarmEntity iAlarm : iAlarms)
        {
            int iAlarmId = iAlarm.getAlarmId();
            AbsAlarmRule rule = curRules.get(iAlarmId);
            if (rule == null)
            {
                // 不存在的报警规则
                AbsAlarmRule temp = builder.genRule(iAlarm);
                newRules.put(iAlarm.getAlarmId(), temp);
                if (!isInit)
                {
                    logger.debug("报警模块：新增报警规则，规则ID：" + iAlarm.getAlarmId() + "\n" + temp);
                }
            }
            else
            {
                // 存在的报警规则
                if (rule.needUpdate(iAlarm))
                {
                    AbsAlarmRule temp = builder.genRule(iAlarm);
                    newRules.put(iAlarm.getAlarmId(), temp);
                    logger.debug("报警模块：更新报警规则，规则ID：" + iAlarm.getAlarmId() + "\n" + temp);
                }
                else
                {
                    newRules.put(rule.getAlarm().getAlarmId(), rule);

                    // 更新报警规则相关信息
                    // 这个地方存在同步的问题，但是影响不大，就不处理同步了
                    rule.getAlarm().setAlarmName(iAlarm.getAlarmName());
                    rule.getAlarm().setAlarmDesc(iAlarm.getAlarmDesc());
                    rule.getAlarm().setAlarmType(iAlarm.getAlarmType());
                }
            }
            oris.remove(Integer.valueOf(iAlarmId));
        }
        for (Integer ori : oris)
        {
            logger.debug("报警模块：移除报警规则，规则ID：" + ori);
        }
        return newRules;
    }

    /**
     * 刷新对象属性和报警规则的关系
     * @param aas
     */
    private Map<Integer, Integer> freshAttrAlarm(ArrayList<AttribAlarmEntity> aas)
    {
        Map<Integer, Integer> retval = new HashMap<Integer, Integer>();
        for (AttribAlarmEntity aa : aas)
        {
            retval.put(aa.getObjattrId(), aa.getAlarmId());
        }
        return retval;
    }

    /**
     * 规则定期刷新线程
     */
    private class UpdateRuleThread implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                logger.debug("报警模块：刷新报警规则开始。");
                fresh(false);
                logger.debug("报警模块：刷新报警规则结束。");
            }
            catch (Exception ex)
            {
                logger.error("报警模块：刷新报警规则出错。", ex);
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        AlarmRuleManager manager = AlarmRuleManager.getInstance();
        manager.start();
        TimeUnit.SECONDS.sleep(90);
        manager.stop();
        TimeUnit.SECONDS.sleep(10);
        manager.start();
        TimeUnit.SECONDS.sleep(10);
        manager.stop();
    }
}
