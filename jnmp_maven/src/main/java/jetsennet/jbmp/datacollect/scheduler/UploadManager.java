/**********************************************************************
 * 日 期： 2012-3-28
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  UploadManager.java
 * 历 史： 2012-3-28 创建
 *********************************************************************/
package jetsennet.jbmp.datacollect.scheduler;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.PerfDataDayDal;
import jetsennet.jbmp.dataaccess.PerfDataHourDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.rrd.RrdHelper;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.PerfDataDayEntity;
import jetsennet.jbmp.entity.PerfDataHourEntity;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.jbmp.util.FourTuple;
import jetsennet.jbmp.util.TimeUtil;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 性能数据上载管理器
 */
public class UploadManager
{

    /**
     * 汇总数据上传定时器
     */
    private Timer upLoadTimer;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(UploadManager.class);

    /**
     * 初始化
     */
    public void init()
    {
        upLoadTimer = new Timer("UploadTimer");
        UploadTask uploadTask = new UploadTask();
        Calendar ca = getUploadTime();
        long beginTime = ca.getTimeInMillis() - System.currentTimeMillis();
        upLoadTimer.scheduleAtFixedRate(uploadTask, beginTime, 24 * 60 * 60 * 1000L);
    }

    /**
     * 释放资源
     */
    public void dispose()
    {
        if (upLoadTimer != null)
        {
            upLoadTimer.cancel();
            upLoadTimer = null;
        }
    }

    private Calendar getUploadTime()
    {
        int[] times = getUploadTimes();
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        ca.set(Calendar.HOUR_OF_DAY, times[0]);
        ca.set(Calendar.MINUTE, times[1]);
        ca.set(Calendar.SECOND, times[2]);
        ca.set(Calendar.MILLISECOND, 0);
        if (ca.getTimeInMillis() < System.currentTimeMillis())
        {
            ca.add(Calendar.DATE, 1);
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("上载时间：" + df.format(ca.getTimeInMillis()));
        return ca;
    }

    private int[] getUploadTimes()
    {
        String uploadTime = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_DATA_UPLOAD_TIME_CFG, null);
        if (uploadTime == null)
        {
            return new int[] { 0, 0, 0 };
        }
        String[] times = uploadTime.split(":");
        if (times.length != 3)
        {
            return new int[] { 0, 0, 0 };
        }

        int[] iTimes = new int[3];
        for (int i = 0; i < times.length; i++)
        {
            iTimes[i] = Integer.parseInt(times[i]);
            if ((i == 0 && iTimes[i] > 23) || ((i == 1 || i == 2) && iTimes[i] > 59) || iTimes[i] < 0)
            {
                iTimes[i] = 0;
            }
        }
        return iTimes;
    }

    /**
     * 数据上传
     * 
     * @author 郭祥
     */
    private static class UploadTask extends TimerTask
    {
        private PerfDataHourDal hourDal = ClassWrapper.wrapTrans(PerfDataHourDal.class);
        private PerfDataDayDal dayDal = ClassWrapper.wrapTrans(PerfDataDayDal.class);

        public void run()
        {
            try
            {
                logger.debug("准备统计上传RRD文件。");
                // 计算统计的开始时间和结束时间
                // 时间从前N天0点0分0秒到前一天23点59分59秒
                int timeSpan = 1; // 统计的时间段
                Calendar ca = Calendar.getInstance();
                ca.add(Calendar.DATE, timeSpan * -1);
                ca.set(Calendar.HOUR_OF_DAY, 0);
                ca.set(Calendar.MINUTE, 0);
                ca.set(Calendar.SECOND, 0);
                ca.set(Calendar.MILLISECOND, 0);
                long startTime = ca.getTimeInMillis();
                long endTime = startTime + 24 * 60 * 60 * 1000 * timeSpan - 1;
                logger.debug(String.format("文件统计的时间范围为：%s到%s", TimeUtil.dateToString(new Date(startTime)), TimeUtil.dateToString(new Date(endTime))));
                upload(startTime / 1000, endTime / 1000);
                logger.debug("统计上传RRD文件结束。");
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
            }
        }

        private void upload(long startTime, long endTime) throws Exception
        {
            // 对每个对象及子对象，上传其小时和天汇总数据到中心数据库
            Integer[] rrdFiles = getRrdFiles();
            logger.debug("需要被统计上传的RRD文件包括：" + ConvertUtil.arrayToString(rrdFiles, ",", false));
            for (Integer objId : rrdFiles)
            {
                try
                {
                    logger.debug("统计上传RRD文件开始：" + objId);
                    uploadHourData(objId, startTime, endTime);
                    uploadDayData(objId, startTime, endTime);
                    logger.debug("统计上传RRD文件结束：" + objId);
                }
                catch (Exception ex)
                {
                    logger.debug("统计上传RRD文件失败：" + objId, ex);
                }
            }
        }

        private Integer[] getRrdFiles()
        {
            String root = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, CollConstants.RRD_ROOT_CFG, CollConstants.DEFAULT_RRD_ROOT);
            File rootDir = new File(root);
            String[] files = rootDir.list();
            List<Integer> resultLst = new ArrayList<Integer>(files.length);
            for (String fileName : files)
            {
                if (fileName.endsWith(".rrd"))
                {
                    resultLst.add(Integer.parseInt(fileName.substring(0, fileName.lastIndexOf(".rrd"))));
                }
            }
            return resultLst.toArray(new Integer[resultLst.size()]);
        }

        /**
         * 按小时统计数据
         * @param objId
         * @param startTime
         * @param endTime
         * @throws Exception
         */
        private void uploadHourData(Integer objId, long startTime, long endTime) throws Exception
        {
            // 从rrd文件查询出小时汇总数据
            List<PerfDataHourEntity> hourLst = new ArrayList<PerfDataHourEntity>();
            Map<Integer, List<FourTuple<Long, Double, Double, Double>>> resultMap =
                RrdHelper.getInstance().genStatistics(objId, startTime, endTime, 3600);
            if (resultMap == null)
            {
                return;
            }
            for (Map.Entry<Integer, List<FourTuple<Long, Double, Double, Double>>> resultEntry : resultMap.entrySet())
            {
                for (FourTuple<Long, Double, Double, Double> result : resultEntry.getValue())
                {
                    PerfDataHourEntity hourEntity = new PerfDataHourEntity();
                    hourEntity.setObjId(objId);
                    hourEntity.setObjAttrId(resultEntry.getKey());
                    hourEntity.setCollTime(result.first);
                    hourEntity.setValue(result.second);
                    hourEntity.setMaxValue(result.third);
                    hourEntity.setMinValue(result.fourth);
                    hourLst.add(hourEntity);
                }
            }

            // 批量插入小时汇总数据
            if (hourLst.size() > 0)
            {
                hourDal.insert(hourLst);
            }
        }

        /**
         * 按天统计数据
         * @param objId
         * @throws Exception
         */
        private void uploadDayData(Integer objId, long startTime, long endTime) throws Exception
        {
            // 从rrd文件查询出天汇总数据
            List<PerfDataDayEntity> dayLst = new LinkedList<PerfDataDayEntity>();
            Map<Integer, List<FourTuple<Long, Double, Double, Double>>> resultMap =
                RrdHelper.getInstance().genStatistics(objId, startTime, endTime, 3600 * 24);
            if (resultMap == null)
            {
                return;
            }
            for (Map.Entry<Integer, List<FourTuple<Long, Double, Double, Double>>> resultEntry : resultMap.entrySet())
            {
                for (FourTuple<Long, Double, Double, Double> result : resultEntry.getValue())
                {
                    PerfDataDayEntity dayEntity = new PerfDataDayEntity();
                    dayEntity.setObjId(objId);
                    dayEntity.setObjAttrId(resultEntry.getKey());
                    dayEntity.setCollTime(result.first);
                    dayEntity.setValue(result.second);
                    dayEntity.setMaxValue(result.third);
                    dayEntity.setMinValue(result.fourth);
                    dayLst.add(dayEntity);
                }
            }

            // 批量插入天汇总数据
            if (dayLst.size() > 0)
            {
                dayDal.insert(dayLst);
            }
        }
    }

    /**
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        UploadTask uploadTask = new UploadTask();
        uploadTask.run();

        PerfDataHourDal hourDal = ClassWrapper.wrapTrans(PerfDataHourDal.class);
        PerfDataDayDal dayDal = ClassWrapper.wrapTrans(PerfDataDayDal.class);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("小时数据列表:");
        List<PerfDataHourEntity> hourLst = hourDal.getLst("SELECT * FROM BMP_PERFDATAHOUR ORDER BY OBJATTR_ID");
        for (PerfDataHourEntity entity : hourLst)
        {
            System.out.println(entity);
        }
        System.out.println("日数据列表:");
        List<PerfDataDayEntity> dayLst = dayDal.getLst("SELECT * FROM BMP_PERFDATADAY ORDER BY OBJATTR_ID");
        for (PerfDataDayEntity entity : dayLst)
        {
            System.out.println(entity);
        }
    }

}
