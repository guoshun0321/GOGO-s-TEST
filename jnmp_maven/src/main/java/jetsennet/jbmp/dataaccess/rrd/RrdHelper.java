/**
 * 
 */
package jetsennet.jbmp.dataaccess.rrd;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jetsennet.jbmp.dataaccess.SysconfigDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.QueryResult;
import jetsennet.jbmp.util.FileUtil;
import jetsennet.jbmp.util.FourTuple;
import jetsennet.jbmp.util.XmlCfgUtil;

import org.apache.log4j.Logger;
import org.jrobin.core.ConsolFuns;
import org.jrobin.core.DsTypes;
import org.jrobin.core.FetchData;
import org.jrobin.core.FetchRequest;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;

/**
 * @author lianghongjie RRD帮助类
 */
public final class RrdHelper
{
    private static final Logger logger = Logger.getLogger(RrdHelper.class);
    private static RrdHelper instance = new RrdHelper();

    private ConcurrentHashMap<String, RrdDb> rrdMap = new ConcurrentHashMap<String, RrdDb>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private String root;
    private int realKeepDays;

    private DecimalFormat numFormatter = new DecimalFormat("#.##");

    private RrdHelper()
    {
        root = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, CollConstants.RRD_ROOT_CFG, CollConstants.DEFAULT_RRD_ROOT);
        try
        {
            FileUtil.createIfNotExist(new File(root), true);
        }
        catch (IOException e)
        {
            logger.error("创建rrd文件根目录异常", e);
        }
        SysconfigDal dal = ClassWrapper.wrapTrans(SysconfigDal.class);
        realKeepDays = dal.getConfigData(CollConstants.REAL_PERF_DATA_KEEP_DAYS_CFG, CollConstants.DEFAULT_REAL_PERF_DATA_KEEP_DAYS);
        if (realKeepDays < 1)
        {
            realKeepDays = CollConstants.DEFAULT_REAL_PERF_DATA_KEEP_DAYS;
        }
    }

    /**
     * 单例
     * @return 单例
     */
    public static RrdHelper getInstance()
    {
        return instance;
    }

    /**
     * 检查监控对象对应的rrd文件
     * @param objId 对象ID
     * @param step 参数
     * @param objAttrIds 对象属性id
     * @throws Exception 异常
     */
    public void checkRrdFile(String objId, int step, String[] objAttrIds) throws Exception
    {
        // 若不存在则新建
        if (!isRrdFileExist(objId))
        {
            createRrdFile(objId, step, objAttrIds);
            logger.debug("新建rrd文件:" + objId + ".rrd");
            return;
        }

        // 若采集间隔或者监控属性更改了，则重新生成rrd文件
        // 监控对象删除不会自动删除对应的rrd文件，这个需要通过JNMP管理器的定期清理rrd文件功能来完成
        boolean flag = false;
        RrdDb rrdDb = getRrdFile(objId);
        if (rrdDb.getHeader().getStep() != step)
        {
            flag = true;
        }
        else
        {
            List<String> newIds = Arrays.asList(objAttrIds);
            List<String> oldIds = Arrays.asList(rrdDb.getDsNames());
            if (!newIds.containsAll(oldIds) || !oldIds.containsAll(newIds))
            {
                flag = true;
            }
        }
        if (flag)
        {
            writeLock.lock();
            try
            {
                changeRrdFile(rrdDb, objId, step, objAttrIds);
                logger.info("重建rrd文件:" + objId + ".rrd");
            }
            finally
            {
                writeLock.unlock();
            }
        }
    }

    /**
     * 保存监控对象的指标数据
     * @param objId 对象id
     * @param time 时间
     * @param values 值
     * @throws Exception 异常
     */
    public void save(String objId, Date time, Map<String, Double> values) throws Exception
    {
        readLock.lock();
        try
        {
            RrdDb rrdDb = getRrdFile(objId);
            synchronized (rrdDb)
            {
                Sample sample = rrdDb.createSample(Util.normalize(time.getTime() / 1000, rrdDb.getHeader().getStep()));
                StringBuilder builder = new StringBuilder("INSERT:");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                builder.append(df.format(time.getTime()) + "  ");
                builder.append("ObjId:" + objId + "  ");
                for (Map.Entry<String, Double> entry : values.entrySet())
                {
                    sample.setValue(entry.getKey(), entry.getValue());
                    builder.append(entry.getKey()).append(":").append(entry.getValue()).append("  ");
                }
                sample.update();
                logger.debug(builder.toString());
            }
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * 统计从sTime到eTime的性能数据
     * 
     * @param objId 对象id
     * @param sTime 开始时间
     * @param eTime 结束时间
     * @param period 统计的周期
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, List<FourTuple<Long, Double, Double, Double>>> genStatistics(int objId, long sTime, long eTime, long period) throws Exception
    {
        if (!isRrdFileExist(Integer.toString(objId)))
        {
            return null;
        }
        Map<Integer, List<FourTuple<Long, Double, Double, Double>>> result = new HashMap<Integer, List<FourTuple<Long, Double, Double, Double>>>();
        readLock.lock();
        try
        {
            RrdDb rrdDb = getRrdFile(Integer.toString(objId));
            FetchRequest request = rrdDb.createFetchRequest(ConsolFuns.CF_LAST, sTime, eTime);
            FetchData fetchData = request.fetchData();
            long[] timestamps = fetchData.getTimestamps();
            String[] dsNames = fetchData.getDsNames();

            for (String dsName : dsNames)
            {
                double[] values = fetchData.getValues(dsName);
                List<FourTuple<Long, Double, Double, Double>> datas = new ArrayList<FourTuple<Long, Double, Double, Double>>(values.length);

                // 初始化统计时间段
                long startTime = sTime;
                long nextTime = sTime + period;

                // 初始化统计数据
                int count = 0;
                double max = Double.NEGATIVE_INFINITY;
                double min = Double.POSITIVE_INFINITY;
                double ave = 0;
                double value = 0;

                int valuesSize = values.length - 1;
                for (int i = 0; i <= valuesSize; i++)
                {
                    // RRD有时候取出的数据会超过给定的时间，过滤掉这部分数据
                    if (timestamps[i] > eTime)
                    {
                        break;
                    }

                    // 统计数据
                    double tempValue = values[i];
                    if (!Double.isNaN(tempValue))
                    {
                        count++;
                        max = tempValue > max ? tempValue : max;
                        min = tempValue < min ? tempValue : min;
                        value += tempValue;
                    }

                    // 当数据为最后一个数据或后一个数据超出时间段时
                    if (i == valuesSize || timestamps[i + 1] >= nextTime)
                    {
                        if (count > 0)
                        {
                            // 计算平均数据
                            ave = value / count;

                            // 将统计数据添加到集合
                            FourTuple<Long, Double, Double, Double> data =
                                new FourTuple<Long, Double, Double, Double>(startTime * 1000, ave, max, min);
                            datas.add(data);

                            // 重新初始化统计数据
                            count = 0;
                            max = Double.NEGATIVE_INFINITY;
                            min = Double.POSITIVE_INFINITY;
                            ave = 0;
                            value = 0;
                        }

                        startTime = nextTime;
                        nextTime = startTime + period;
                    }
                }

                result.put(Integer.parseInt(dsName), datas);
            }
        }
        finally
        {
            readLock.unlock();
        }
        return result;
    }

    /**
     * 从rrd文件中查询监控对象的指标数据
     * @param objId 对象id
     * @param size 大小
     * @param objAttrIds 对象属性
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, List<QueryResult>> query(int objId, int size, int[] objAttrIds) throws Exception
    {
        if (!isRrdFileExist(Integer.toString(objId)))
        {
            return null;
        }
        readLock.lock();
        try
        {
            // 如果size为1则取最后一次更新的时间，否则取当前时间
            RrdDb rrdDb = getRrdFile(Integer.toString(objId));
            long endTime = size == 1 ? rrdDb.getLastUpdateTime() : System.currentTimeMillis() / 1000;
            long startTime = endTime - rrdDb.getHeader().getStep() * size;
            startTime = startTime > 0 ? startTime : 0;
            FetchRequest request = rrdDb.createFetchRequest(ConsolFuns.CF_LAST, startTime, endTime);
            String[] ids = new String[objAttrIds.length];
            for (int i = 0; i < ids.length; i++)
            {
                ids[i] = Integer.toString(objAttrIds[i]);
            }
            ids = filterIds(rrdDb, ids);
            request.setFilter(ids);
            FetchData fetchData = request.fetchData();
            double[][] values = fetchData.getValues();
            long[] timestamps = fetchData.getTimestamps();

            Map<Integer, List<QueryResult>> result = new HashMap<Integer, List<QueryResult>>();
            for (int i = 0; i < values.length; i++)
            {
                List<QueryResult> datas = new ArrayList<QueryResult>(timestamps.length);
                int counter = size;
                for (int j = timestamps.length - 1; j >= 0; j--)
                {
                    // size不为1则头尾两个数据不管是不是空都要返回
                    if (size != 1 && (j == 0 || j == timestamps.length - 1))
                    {
                        datas.add(createQueryResult(timestamps[j], values[i][j]));
                        if (--counter == 0)
                        {
                            break;
                        }
                        continue;
                    }
                    if (!Double.isNaN(values[i][j]))
                    {
                        datas.add(createQueryResult(timestamps[j], values[i][j]));
                        if (--counter == 0)
                        {
                            break;
                        }
                    }
                }
                if (datas.size() == 0)
                {
                    datas.add(createQueryResult(endTime, Double.NaN));
                }
                result.put(Integer.parseInt(ids[i]), datas);
            }
            return result;
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * @param objId 对象id
     * @param fetchSizes 参数
     * @param fetchTimes 参数
     * @param objAttrIds 对象属性id
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, List<QueryResult>> query(int objId, int[] fetchSizes, int[] fetchTimes, int[] objAttrIds) throws Exception
    {
        if (!isRrdFileExist(Integer.toString(objId)))
        {
            return null;
        }
        readLock.lock();
        try
        {
            Map<Integer, List<QueryResult>> result = new HashMap<Integer, List<QueryResult>>();
            RrdDb rrdDb = getRrdFile(Integer.toString(objId));
            for (int i = 0; i < objAttrIds.length; i++)
            {
                try
                {
                    // 如果size为1则取最后一次更新的时间，否则取当前时间
                    long endTime = fetchSizes[i] == 1 ? rrdDb.getLastUpdateTime() : System.currentTimeMillis() / 1000;
                    long startTime = endTime - fetchTimes[i];
                    startTime = startTime > 0 ? startTime : 0;
                    FetchRequest request = rrdDb.createFetchRequest(ConsolFuns.CF_LAST, startTime, endTime);
                    request.setFilter(new String[] { Integer.toString(objAttrIds[i]) });
                    FetchData fetchData = request.fetchData();
                    double[] values = fetchData.getValues()[0];
                    long[] timestamps = fetchData.getTimestamps();

                    List<QueryResult> datas = new ArrayList<QueryResult>(timestamps.length);
                    int counter = fetchSizes[i];
                    for (int j = timestamps.length - 1; j >= 0; j--)
                    {
                        // size不为1则头尾两个数据不管是不是空都要返回
                        if (fetchSizes[i] != 1 && (j == 0 || j == timestamps.length - 1))
                        {
                            datas.add(createQueryResult(timestamps[j], values[j]));
                            if (--counter == 0)
                            {
                                break;
                            }
                            continue;
                        }
                        if (!Double.isNaN(values[j]))
                        {
                            datas.add(createQueryResult(timestamps[j], values[j]));
                            if (--counter == 0)
                            {
                                break;
                            }
                        }
                    }
                    if (datas.size() == 0)
                    {
                        datas.add(createQueryResult(endTime, Double.NaN));
                    }
                    result.put(objAttrIds[i], datas);
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
            return result;
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * 从rrd文件中查询监控对象的指标数据
     * @param objId 对象id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param objAttrIds 对象属性
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, List<QueryResult>> query(int objId, long startTime, long endTime, int[] objAttrIds) throws Exception
    {
        if (!isRrdFileExist(Integer.toString(objId)))
        {
            return null;
        }
        readLock.lock();
        try
        {
            RrdDb rrdDb = getRrdFile(Integer.toString(objId));
            startTime = getValidStartTime(rrdDb, startTime);
            startTime = startTime <= endTime ? startTime : endTime;
            FetchRequest request = rrdDb.createFetchRequest(ConsolFuns.CF_LAST, startTime, endTime);
            String[] ids = new String[objAttrIds.length];
            for (int i = 0; i < ids.length; i++)
            {
                ids[i] = Integer.toString(objAttrIds[i]);
            }
            ids = filterIds(rrdDb, ids);
            request.setFilter(ids);
            FetchData fetchData = request.fetchData();
            double[][] values = fetchData.getValues();
            long[] timestamps = fetchData.getTimestamps();

            Map<Integer, List<QueryResult>> result = new HashMap<Integer, List<QueryResult>>();
            for (int i = 0; i < values.length; i++)
            {
                List<QueryResult> datas = new ArrayList<QueryResult>(timestamps.length);
                for (int j = timestamps.length - 1; j >= 0; j--)
                {
                    if (!Double.isNaN(values[i][j]) && timestamps[j] >= startTime && timestamps[j] <= endTime)
                    {
                        datas.add(createQueryResult(timestamps[j], values[i][j]));
                    }
                }
                result.put(Integer.parseInt(ids[i]), datas);
            }
            return result;
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * @param objId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public RrdDb getRrdDb(String objId) throws Exception
    {
        return new RrdDb(root + objId + ".rrd");
    }

    /**
     * 销毁
     */
    public void destroy()
    {
        writeLock.lock();
        try
        {
            for (RrdDb rrd : rrdMap.values())
            {
                try
                {
                    rrd.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage());
                }
            }
            rrdMap.clear();
        }
        finally
        {
            writeLock.unlock();
        }
    }

    private boolean isRrdFileExist(String objId)
    {
        File file = new File(root + objId + ".rrd");
        return file.exists();
    }

    /**
     * 创建rrd文件 文件名为对象id.rrd，因此是唯一的 对象属性id为数据源名称，即相当于数据库表格的列名，也是唯一的
     * @param objId
     * @param step
     * @param objAttrIds
     * @throws Exception
     */
    private void createRrdFile(String objId, int step, String[] objAttrIds) throws Exception
    {
        RrdDef rrdDef = new RrdDef(root + objId + ".rrd", 0, step);
        for (String objAttrId : objAttrIds)
        {
            rrdDef.addDatasource(objAttrId, DsTypes.DT_GAUGE, step * 2, Double.NaN, Double.NaN);
        }
        rrdDef.addArchive(ConsolFuns.CF_LAST, 0.0, 1, (int) (realKeepDays * 24 * 3600 / step));
        RrdDb rrdDb = new RrdDb(rrdDef);
        rrdDb.close();
    }

    private void changeRrdFile(RrdDb rrdDb, String objId, int step, String[] objAttrIds) throws Exception
    {
        createRrdFile(objId + "_tmp", step, objAttrIds);
        String destPath = rrdDb.getPath();
        String tmpPath = root + objId + "_tmp" + ".rrd";
        RrdDb tmpRrd = new RrdDb(tmpPath);
        rrdDb.copyStateTo(tmpRrd);
        rrdDb.close();
        rrdMap.remove(objId);
        tmpRrd.close();
        new File(destPath).delete();
        new File(tmpPath).renameTo(new File(destPath));
    }

    private RrdDb getRrdFile(String objId) throws Exception
    {
        RrdDb rrdDb = rrdMap.get(objId);
        if (rrdDb == null)
        {
            rrdDb = new RrdDb(root + objId + ".rrd");
            RrdDb rrdDbTmp = rrdMap.putIfAbsent(objId, rrdDb);
            if (rrdDbTmp != null)
            {
                rrdDb.close();
                rrdDb = rrdDbTmp;
            }
        }
        return rrdDb;
    }

    private String[] filterIds(RrdDb rrdDb, String[] ids) throws Exception
    {
        List<String> oldIds = Arrays.asList(rrdDb.getDsNames());
        List<String> resIds = new ArrayList<String>();
        for (String id : ids)
        {
            if (oldIds.contains(id))
            {
                resIds.add(id);
            }
        }
        return resIds.toArray(new String[resIds.size()]);
    }

    private long getValidStartTime(RrdDb rrdDb, long startTime) throws Exception
    {
        long result = rrdDb.getLastUpdateTime();
        result = result == 0 ? System.currentTimeMillis() / 1000 : result;
        result -= realKeepDays * 24 * 60 * 60L;
        result = result > startTime ? result : startTime;
        return result;
    }

    private QueryResult createQueryResult(long time, double value)
    {
        QueryResult data = new QueryResult();
        data.setCollTime(time * 1000);
        data.setValue(doubleToString(value));
        return data;
    }

    private String doubleToString(double value)
    {
        if (Double.isNaN(value))
        {
            return "";
        }
        return numFormatter.format(value);
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        //        String objStr = "1536";
        //        String attrStr = "11882,11883,11892,11893,11894,11895,11896,11897";
        String objStr = "1537";
        String attrStr = "11934,11935";
        String valueStr =
            "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100";
        String[] attrs = attrStr.split(",");

        Date start = new Date("2013/02/24 00:00:00");
        Calendar c = Calendar.getInstance();
        c.setTime(start);

        // 创建文件
        RrdHelper.getInstance().checkRrdFile(objStr, 5, attrs);
        // 添加数据
        c.add(Calendar.SECOND, 5);
        for (int i = 1; i <= 60000; i++)
        {
            Date time = c.getTime();
            Map<String, Double> values = new HashMap<String, Double>();
            for (int j = 1; j <= attrs.length; j++)
            {
                String attr = attrs[j - 1];
                values.put(attr, (10d * i + j));
            }
            RrdHelper.getInstance().save(objStr, time, values);
            c.add(Calendar.SECOND, 5);
        }

        // 打印信息
        //        String printStr = "objAttrId:%s; time:%s; ave:%s; max:%s; min:%s";
        //        Map<Integer, List<FourTuple<Long, Double, Double, Double>>> valueMap = RrdHelper.getInstance().genStatistics(1, start.getTime(), 60);
        //        Set<Map.Entry<Integer, List<FourTuple<Long, Double, Double, Double>>>> set = valueMap.entrySet();
        //        for (Map.Entry<Integer, List<FourTuple<Long, Double, Double, Double>>> entry : set)
        //        {
        //            int objAttrId = entry.getKey();
        //            List<FourTuple<Long, Double, Double, Double>> values = entry.getValue();
        //            if (values != null && !values.isEmpty())
        //            {
        //                for (FourTuple<Long, Double, Double, Double> temp : values)
        //                {
        //                    if (temp != null)
        //                    {
        //                        String str =
        //                            String.format(printStr, objAttrId, temp.first, temp.second, Double.valueOf((double) temp.third).intValue(), temp.fourth);
        //                        System.out.println(str);
        //                    }
        //                }
        //            }
        //        }

        //        int[] iAttrs = new int[attrs.length];
        //        for (int i = 0; i < attrs.length; i++)
        //        {
        //            iAttrs[i] = Integer.parseInt(attrs[i]);
        //        }
        //        long start = System.currentTimeMillis();
        //        for (int i = 0; i < 100; i++)
        //        {
        //            long endTime = start / 1000;
        //            long startTime = endTime - 60 * 60 * 24 * 5L;
        //            RrdHelper.getInstance().query(1, startTime, endTime, iAttrs);
        //            System.out.println("no." + i + " fetch,cause:" + (System.currentTimeMillis() - start) + "ms");
        //        }
        //        long end = System.currentTimeMillis();
        //        System.out.println("use time:" + (end - start) / 1000 + "s");
    }
}
