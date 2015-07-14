package jetsennet.jbmp.backup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.SysconfigDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.util.FileUtil;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * 数据转储
 * @author Guo
 */
public final class BackupManager
{
    private static final Logger logger = Logger.getLogger(BackupManager.class);
    private static long ONE_WEEK = 7 * 24 * 60 * 60 * 1000L;
    private static long ONE_DAY = 24 * 60 * 60 * 1000L;
    private static BackupManager instance = new BackupManager();
    private Timer timer;
    private SysconfigDal cfgDal;
    private DefaultDal defDal;
    private boolean isStart;

    public static BackupManager getInstance()
    {
        return instance;
    }

    private BackupManager()
    {
        cfgDal = ClassWrapper.wrapTrans(SysconfigDal.class);
        defDal = ClassWrapper.wrapTrans(DefaultDal.class);
    }

    /**
     * 开始
     */
    public void start()
    {
        if (isStart())
        {
            return;
        }
        timer = new Timer("BackUpTimer");
        BackupTask backupTask = new BackupTask();
        long startTime = BackupUtil.getBeginTime().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        timer.scheduleAtFixedRate(backupTask, startTime, ONE_DAY);
        setStart(true);
        logger.info("转储线程启动");
    }

    /**
     * 结束
     */
    public void stop()
    {
        if (!isStart())
        {
            return;
        }
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
        setStart(false);
        logger.info("转储线程停止");
    }

    /**
     * ？
     */
    public void backupNow()
    {
        BackupTask backup = new BackupTask();
        backup.run();
    }

    /**
     * @return 是否开始
     */
    public boolean isStart()
    {
        return isStart;
    }

    /**
     * @param isStart设置开始
     */
    public void setStart(boolean isStart)
    {
        this.isStart = isStart;
    }

    class BackupTask extends TimerTask
    {
        public void run()
        {
            Calendar ca = Calendar.getInstance();
            ca.setTimeInMillis(System.currentTimeMillis());
            String fileName = ca.get(Calendar.YEAR) + "-" + (ca.get(Calendar.MONTH) + 1) + "-" + ca.get(Calendar.DATE);

            List<Element> modules = BackupUtil.getBackupCfgs();
            for (Element elm : modules)
            {
                String moduleName = BackupUtil.getValue(elm, "name", "");
                String backupRoot = BackupUtil.getValue(elm, "backupRoot", "E:/backup");
                String backupFile = backupRoot + "/" + moduleName + "." + fileName;
                int outday = 30;
                String outdaykey = BackupUtil.getValue(elm, "outdayKey", null);
                if (outdaykey != null)
                {
                    outday = cfgDal.getConfigData(outdaykey, 30);
                }
                String className = BackupUtil.getValue(elm, "className", null);
                backup(backupFile, outday, className);
            }
        }

        private void backup(String backupFile, int outday, String className)
        {
            FileWriter writer = null;
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            try
            {
                logger.info("开始转储,转储文件路径:" + backupFile + "...");
                Calendar outdayCal = BackupUtil.getBackupTime(outday);
                Class cla = Class.forName(className);
                String querySql = (String) BackupUtil.invoke(cla, "getQuerySql", outdayCal.getTimeInMillis());
                String delSql = (String) BackupUtil.invoke(cla, "getDelSql", outdayCal.getTimeInMillis());
                File file = new File(backupFile);
                FileUtil.createIfNotExist(file, false);
                writer = new FileWriter(file, true);
                exec.transBegin();

                List dataLst = defDal.getLst(cla, querySql);
                for (Object data : dataLst)
                {
                    writer.write(data.toString());
                    writer.write("\r\n");
                }
                defDal.delete(delSql);

                exec.transCommit();
                logger.info("转储成功");
            }
            catch (Exception e)
            {
                logger.error("转储异常", e);
                exec.transRollback();
            }
            finally
            {
                if (writer != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (IOException e1)
                    {
                        logger.error(e1.getMessage());
                    }
                }
            }
        }
    }
    
    public static void main(String[] args)
    {
        BackupManager bm = new BackupManager();
        bm.start();
    }

}
