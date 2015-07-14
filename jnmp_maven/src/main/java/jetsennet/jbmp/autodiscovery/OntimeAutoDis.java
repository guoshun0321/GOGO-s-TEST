/************************************************************************
日 期：2012-3-14
作 者: 郭祥
版 本: v1.3
描 述: 定时自动发现
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.IDataChangeObserver;
import jetsennet.jbmp.dataaccess.buffer.DynamicAutoDisTask;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.jbmp.util.TimeUtil;

/**
 * 定时自动发现
 * @author 郭祥
 */
public final class OntimeAutoDis implements IDataChangeObserver<AutoDisTaskEntity>
{

    /**
     * 调度器
     */
    private Timer timer;
    /**
     * 数据库刷新工具
     */
    private DynamicAutoDisTask dynamic;
    /**
     * 任务列表
     */
    private OntimeAutoDisTaskTable tasks;
    /**
     * 打印格式
     */
    private static final String PRINT_STYLE = "%-10s%-40s%-100s";
    private static final Logger logger = Logger.getLogger(OntimeAutoDis.class);
    private static OntimeAutoDis instance = new OntimeAutoDis();

    private OntimeAutoDis()
    {
        dynamic = DynamicAutoDisTask.getInstance();
    }

    public static OntimeAutoDis getInstance()
    {
        return instance;
    }

    /**
     * 开始
     */
    public synchronized void start()
    {
        try
        {
            tasks = new OntimeAutoDisTaskTable();
            timer = new Timer("ontime_autodistask");
            dynamic.attach(this);
            dynamic.start();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 停止
     */
    public synchronized void stop()
    {
        try
        {
            dynamic.detach(this);
            dynamic.stop();
            timer.cancel();
            tasks = null;
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            timer = null;
        }
    }

    @Override
    public void change(AutoDisTaskEntity obj, int opNum)
    {
        if (obj == null)
        {
            return;
        }
        // 手动发现任务，返回
        if (obj.getExeType() == AutoDisTaskEntity.EXE_TYPE_MANU)
        {
            return;
        }
        switch (opNum)
        {
        case DynamicAutoDisTask.OP_NUM_ADD:
        {
            tasks.add(obj, true);
            break;
        }
        case DynamicAutoDisTask.OP_NUM_DEL:
        {
            tasks.del(obj);
            break;
        }
        case DynamicAutoDisTask.OP_NUM_UPDATE:
        {
            tasks.update(obj);
            break;
        }
        default:
            break;
        }
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        OntimeAutoDis dis = OntimeAutoDis.getInstance();
        dis.start();
    }

    /**
     * 定时执行的任务
     */
    private class OntimeAutoDisTask extends TimerTask
    {

        /**
         * 记录ID
         */
        private int id;

        public OntimeAutoDisTask(int id)
        {
            this.id = id;
        }

        @Override
        public void run()
        {
            try
            {
                OntimeAutoDisRecord record = tasks.get(id);
                Date exeTime = record.exeTime;
                Date now = new Date();

                // 执行时间-当前时间的绝对值在1分钟以外，不执行该任务
                if (Math.abs(exeTime.getTime() - now.getTime()) > 60 * 1000)
                {
                    return;
                }
                if (record != null && record.task != null)
                {
                    // 执行当前任务
                    AutoDisTaskEntity adt = record.task;
                    AutoDisMethod.getInstance().remoteAutoDis(adt.getTaskId(), adt.getCollId(), BMPConstants.LOG_USER_ID, BMPConstants.LOG_USER_NAME);
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }

    /**
     * 自动发现记录表
     */
    private class OntimeAutoDisTaskTable
    {

        /**
         * 计数器
         */
        private int counter;
        /**
         * 任务记录
         */
        private List<OntimeAutoDisRecord> records;

        public OntimeAutoDisTaskTable()
        {
            counter = 0;
            records = new LinkedList<OntimeAutoDisRecord>();
        }

        /**
         * 新建并添加记录，并提交到调度器
         * @param obj
         */
        public synchronized void add(AutoDisTaskEntity obj, boolean isToday)
        {
            try
            {
                int id = counter++;
                OntimeAutoDisRecord record = new OntimeAutoDisRecord(id, obj, isToday);
                records.add(record);
                timer.schedule(new OntimeAutoDisTask(id), record.exeTime);
                logger.debug("自动发现任务，添加新记录：" + record + "。当前表为：\n" + this.toString());
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }

        /**
         * 删除记录
         * @param obj
         */
        public synchronized void del(AutoDisTaskEntity obj)
        {
            if (obj == null)
            {
                return;
            }
            int taskId = obj.getTaskId();
            for (int i = 0; i < records.size();)
            {
                OntimeAutoDisRecord record = records.get(i);
                if (record.task.getTaskId() == taskId)
                {
                    records.remove(record);
                    logger.debug("自动发现任务，删除记录：" + record + "。当前表为：\n" + this.toString());
                }
                else
                {
                    i++;
                }
            }
        }

        /**
         * 更新记录
         * @param obj
         */
        public synchronized void update(AutoDisTaskEntity obj)
        {
            this.del(obj);
            this.add(obj, true);
        }

        /**
         * 获取id对应的记录，删除该条记录，并插入下一条记录 无记录时返回null
         * @param id
         * @return
         */
        public synchronized OntimeAutoDisRecord get(int id)
        {
            OntimeAutoDisRecord retval = null;
            for (OntimeAutoDisRecord record : records)
            {
                if (record.id == id)
                {
                    retval = record;
                    logger.debug("自动发现任务，提取记录：" + record);
                    break;

                }
            }
            if (retval != null)
            {
                // 移除旧记录，添加新记录
                this.del(retval.task);
                this.add(retval.task, false);
            }
            return retval;
        }

        /**
         * 表清空
         */
        public synchronized void removeAll()
        {
            records.clear();
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PRINT_STYLE, "ID", "TIME", "TASK"));
            for (OntimeAutoDisRecord record : records)
            {
                sb.append("\r\n");
                sb.append(record.toString());
            }
            sb.append("\r\n总记录数：");
            sb.append(records.size());
            return sb.toString();
        }
    }

    /**
     * 一条自动发现记录
     */
    private class OntimeAutoDisRecord
    {

        /**
         * 任务编号
         */
        public int id;
        /**
         * 执行时间
         */
        public Date exeTime;
        /**
         * 执行任务
         */
        public AutoDisTaskEntity task;

        public OntimeAutoDisRecord(int id, AutoDisTaskEntity task, boolean isToday)
        {
            this.id = id;
            this.task = task;
            this.exeTime = this.nextDate(this.task, isToday);
        }

        /**
         * 任务下一次执行的时间
         * @param task 任务
         * @param isToday 参数
         * @return 结果
         */
        private Date nextDate(AutoDisTaskEntity task, boolean isToday)
        {
            Date now = new Date();
            int span = ConvertUtil.daySpan(task.getWeekMask(), TimeUtil.getWeek(now), isToday);
            int[] hms = ConvertUtil.parseTime(task.getTimePoint());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, span);
            calendar.set(Calendar.HOUR_OF_DAY, hms[0]);
            calendar.set(Calendar.MINUTE, hms[1]);
            calendar.set(Calendar.SECOND, hms[2]);
            return calendar.getTime();
        }

        @Override
        public String toString()
        {
            return String.format(PRINT_STYLE, id, exeTime.toLocaleString(), task);
        }
    }

}
