package jetsennet.jbmp.dataaccess.buffer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * BMP_SNMPNODES表的缓存，主要在子对象实例化的时候使用。使用ThreadLocal，多线程安全。
 * @author 郭祥
 */
public final class SnmpNodeBuffer
{

    private Map<Integer, SnmpMib> id2Mib;
    private SnmpNodesDal sndal;

    private static ThreadLocal<SnmpNodeBuffer> buffer = new ThreadLocal<SnmpNodeBuffer>();
    private static final Logger logger = Logger.getLogger(SnmpNodeBuffer.class);

    private SnmpNodeBuffer()
    {
        id2Mib = new HashMap<Integer, SnmpMib>();
        sndal = ClassWrapper.wrapTrans(SnmpNodesDal.class);
    }

    /**
     * 构造方法
     * @return 结果
     */
    public static SnmpNodeBuffer get()
    {
        SnmpNodeBuffer retval = buffer.get();
        if (retval == null)
        {
            retval = new SnmpNodeBuffer();
        }
        buffer.set(retval);
        return retval;
    }

    /**
     * 关闭
     */
    public static void close()
    {
        buffer.set(null);
    }

    /**
     * 清除
     */
    public void clear()
    {
        this.id2Mib.clear();
    }

    /**
     * @param mibId 参数
     * @param name 参数
     * @return 结果
     */
    public SnmpNodesEntity getByName(int mibId, String name)
    {
        SnmpNodesEntity retval = null;
        try
        {
            // 缓存中查找
            SnmpMib mib = id2Mib.get(mibId);
            if (mib != null)
            {
                retval = mib.name2Node.get(name);
                if (retval != null)
                {
                    return retval;
                }
            }
            else
            {
                mib = new SnmpMib();
                id2Mib.put(mibId, mib);
            }

            // 数据库查找
            retval = sndal.getByName(mibId, name);

            // 加入到缓存
            if (retval != null)
            {
                mib.id2Node.put(retval.getNodeId(), retval);
                mib.name2Node.put(retval.getNodeName(), retval);
            }

        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = null;
        }
        return retval;
    }

    /**
     * @param mibId 参数
     * @param nodeId 参数
     * @return 结果
     */
    public SnmpNodesEntity getById(int mibId, int nodeId)
    {
        SnmpNodesEntity retval = null;
        try
        {
            // 缓存中查找
            SnmpMib mib = id2Mib.get(mibId);
            if (mib != null)
            {
                retval = mib.id2Node.get(nodeId);
                if (retval != null)
                {
                    return retval;
                }
            }
            else
            {
                mib = new SnmpMib();
                id2Mib.put(mibId, mib);
            }

            // 数据库查找
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String),
                    new SqlCondition("NODE_ID", Integer.toString(nodeId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
            retval = sndal.get(conds);

            // 加入到缓存
            if (retval != null)
            {
                mib.id2Node.put(retval.getNodeId(), retval);
                mib.name2Node.put(retval.getNodeName(), retval);
            }

        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = null;
        }
        return retval;
    }

    /**
     * 内部类
     */
    public static class SnmpMib
    {
        public Map<Integer, SnmpNodesEntity> id2Node;
        public Map<String, SnmpNodesEntity> name2Node;

        /**
         * 构造方法
         */
        public SnmpMib()
        {
            id2Node = new HashMap<Integer, SnmpNodesEntity>();
            name2Node = new HashMap<String, SnmpNodesEntity>();
        }
    }

}
