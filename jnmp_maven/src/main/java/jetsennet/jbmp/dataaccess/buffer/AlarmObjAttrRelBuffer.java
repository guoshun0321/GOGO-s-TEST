package jetsennet.jbmp.dataaccess.buffer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jetsennet.jbmp.dataaccess.AlarmObjAttrRelDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.AlarmObjAttrRelEntity;
import jetsennet.jbmp.util.XmlCfgUtil;

import org.apache.log4j.Logger;

/**
 * 报警关系缓存。
 * 由AlarmRelEntry和它们之间的关系组成。其结构为不存在环的有向图（不存在环的特性由前台页面保证）。
 * 
 * @author 郭祥
 */
public class AlarmObjAttrRelBuffer
{

    /**
     * 采集器ID
     */
    private int collId;
    /**
     * 数据缓存
     */
    private Map<Integer, AlarmRelEntry> id2Rel;
    /**
     * 定时刷新
     */
    private Timer timer;
    /**
     * 调度间隔
     */
    private static final long TIMER_SPAN = 10 * 1000;
    // 读写锁
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writerLock = lock.writeLock();
    /**
     * 数据库操作
     */
    private AlarmObjAttrRelDal aoarDal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AlarmObjAttrRelBuffer.class);

    private static final AlarmObjAttrRelBuffer instance = new AlarmObjAttrRelBuffer();

    private AlarmObjAttrRelBuffer()
    {
        aoarDal = ClassWrapper.wrapTrans(AlarmObjAttrRelDal.class);
        collId = XmlCfgUtil.getIntValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_ID_CFG, -1);

        this.fresh();

        // 声明为后台线程，第一次调用报警关联分析时启动。采集器关闭时，关闭。
        timer = new Timer("ALARM_REL_TIMER", true);
        timer.schedule(new RefreshAlarmRelTask(), TIMER_SPAN, TIMER_SPAN);
    }

    public static AlarmObjAttrRelBuffer getInstance()
    {
        return instance;
    }

    /**
     * 根据对象属性ID获取关联性报警信息
     * @param objAttrId
     * @return
     */
    public String getByObjAttrId(int objAttrId)
    {
        String retval = null;
        readLock.lock();
        try
        {
            AlarmRelEntry entry = id2Rel.get(objAttrId);
            if (entry != null)
            {
                retval = entry.getParentStr().trim();
                retval = "".equals(retval) ? null : retval;
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            readLock.unlock();
        }
        return retval;
    }

    /**
     * 刷新
     */
    private void fresh()
    {
        try
        {
            List<AlarmObjAttrRelEntity> rels = aoarDal.getValidRels(collId);

            if (rels == null)
            {
                return;
            }

            Map<Integer, AlarmRelEntry> id2Rel = new HashMap<Integer, AlarmRelEntry>();
            for (AlarmObjAttrRelEntity rel : rels)
            {
                int objAttrId = rel.getObjAttrId();
                int objAttrPid = rel.getObjAttrPid();
                AlarmRelEntry entry = this.ensureNode(id2Rel, objAttrId);
                AlarmRelEntry pEntry = this.ensureNode(id2Rel, objAttrPid);
                entry.addPNode(pEntry);
            }

            Set<Map.Entry<Integer, AlarmRelEntry>> entrys = id2Rel.entrySet();
            for (Map.Entry<Integer, AlarmRelEntry> entry : entrys)
            {
                AlarmRelEntry value = entry.getValue();
                value.getAncestors();
            }

            writerLock.lock();
            try
            {
                this.id2Rel = id2Rel;
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                writerLock.unlock();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 确定对象属性ID对应的实体是否存在，不存在是新建该实体
     * @param id2Rel
     * @param objAttrId
     * @return
     */
    private AlarmRelEntry ensureNode(Map<Integer, AlarmRelEntry> id2Rel, int objAttrId)
    {
        AlarmRelEntry retval = id2Rel.get(objAttrId);
        if (retval == null)
        {
            retval = new AlarmRelEntry(objAttrId);
            id2Rel.put(objAttrId, retval);
        }
        return retval;
    }

    /**
     * 定时刷新
     * @author GoGo
     *
     */
    private class RefreshAlarmRelTask extends TimerTask
    {

        @Override
        public void run()
        {
            try
            {
                fresh();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }

    }

    /**
     * 报警关系实体，有向图中的一个节点
     * @author GoGo
     *
     */
    private class AlarmRelEntry
    {
        /**
         * 对象属性ID
         */
        public int objAttrId;
        /**
         * 所有关联父节点
         */
        public Set<AlarmRelEntry> pNodes;
        /**
         * 所有的祖先对象属性ID
         */
        public Set<Integer> ancestors;

        public AlarmRelEntry(int objAttrId)
        {
            this.objAttrId = objAttrId;
        }

        /**
         * 添加关联关系
         * 
         * @param entry
         */
        public void addPNode(AlarmRelEntry entry)
        {
            if (pNodes == null)
            {
                pNodes = new HashSet<AlarmRelEntry>();
            }
            pNodes.add(entry);
        }

        /**
         * 计算所有祖先对象属性
         * 
         * @return
         */
        public Set<Integer> getAncestors()
        {
            if (ancestors == null)
            {
                ancestors = new HashSet<Integer>();
                if (pNodes != null)
                {
                    for (AlarmRelEntry pNode : pNodes)
                    {
                        int objAttrId = pNode.objAttrId;
                        ancestors.add(objAttrId);
                        ancestors.addAll(pNode.getAncestors());
                    }
                }
            }
            return ancestors;
        }

        /**
         * 返回所有祖先对象属性ID的字符串，格式xxx,xxx,xxx，方便数据库查询
         * 
         * @return
         */
        public String getParentStr()
        {
            StringBuilder sb = new StringBuilder();
            if (ancestors != null)
            {
                for (Integer ancestor : ancestors)
                {
                    sb.append(ancestor).append(",");
                }
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString().trim();
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("对象属性ID：").append(this.objAttrId);
            sb.append("；关联对象属性：");
            if (ancestors != null)
            {
                sb.append(this.getParentStr());
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) throws Exception
    {
        AlarmObjAttrRelBuffer buffer = AlarmObjAttrRelBuffer.getInstance();
        TimeUnit.MINUTES.sleep(10);
    }

}
