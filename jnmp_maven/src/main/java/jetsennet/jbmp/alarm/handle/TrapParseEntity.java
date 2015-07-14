package jetsennet.jbmp.alarm.handle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.snmp4j.smi.Variable;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;
import jetsennet.jbmp.util.ConvertUtil;

/**
 * @author 郭祥
 */
public class TrapParseEntity
{

    /**
     * Trap名称
     */
    private String trapName;
    /**
     * 原始Trap，OID做主键。<OID,值>
     */
    private Map<String, String> original;
    /**
     * 宏替换Map，<$x, 值>
     */
    private Map<String, String> macroMap;
    /**
     * 转换后的Trap，别名做主键。<别名,值>
     */
    private Map<String, String> transMap;
    /**
     * Trap解析结果。<OID, TrapTableEntity>
     */
    private Map<String, TrapTableEntity> parseMap;
    /**
     * 报文里面的Trap时间
     */
    private long trapTime;
    /**
     * Trap接收时间
     */
    private long collTime;
    /**
     * 类型字段
     */
    private String trapOid;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TrapParseEntity.class);

    /**
     * 构造方法
     */
    public TrapParseEntity()
    {
        original = new LinkedHashMap<String, String>();
        macroMap = new LinkedHashMap<String, String>();
    }

    /**
     * 将收到的Trap转换成TrapParseEntity
     * @param data 参数
     * @param coding 参数
     * @param trans 参数
     * @return 结果
     */
    public static TrapParseEntity genEntity(CollData data, String coding, Map<String, String> trans)
    {
        TrapParseEntity retval = new TrapParseEntity();
        retval.collTime = data.time.getTime();
        if (trans != null)
        {
            retval.transMap = new HashMap<String, String>();
        }
        if (data != null && data.params != null)
        {
            Set<String> oids = data.params.keySet();
            int i = 1;
            for (String oid : oids)
            {
                Object obj = data.params.get(oid);
                String tValue = null;
                if (obj != null)
                {
                    if (obj instanceof Variable)
                    {
                        tValue = SnmpValueTranser.getInstance().trans((Variable) obj, coding, 0);
                    }
                    else
                    {
                        tValue = obj.toString();
                    }
                }
                retval.original.put(oid, tValue);
                retval.macroMap.put("$" + i, tValue);
                i++;
                // 类型
                if (oid.equals(CollDataTrapUtil.SNMP_TRAP_OID))
                {
                    retval.trapOid = tValue;
                }
                // 时间
                else if (oid.equals(CollDataTrapUtil.SYS_UP_TIME))
                {
                    retval.trapTime = ConvertUtil.stringToLong(tValue, -1);
                }
                // 转换
                if (trans != null)
                {
                    String key = trans.get(oid);
                    if (key != null)
                    {
                        retval.transMap.put(key, tValue);
                    }
                }
            }
        }
        return retval;
    }

    /**
     * 将收到的Trap转换成TrapParseEntity 
     * 匹配规则为已给定OID开头
     * @param data 参数
     * @param coding 参数
     * @param trans 参数
     * @return 结果
     */
    public static TrapParseEntity genEntityStartWith(CollData data, String coding, Map<String, String> trans)
    {
        TrapParseEntity retval = new TrapParseEntity();
        retval.collTime = data.time.getTime();
        if (trans != null)
        {
            retval.transMap = new HashMap<String, String>();
        }
        if (data != null && data.params != null)
        {
            Set<String> oids = data.params.keySet();
            for (String oid : oids)
            {
                Object obj = data.params.get(oid);
                String tValue = null;
                if (obj != null)
                {
                    if (obj instanceof Variable)
                    {
                        tValue = SnmpValueTranser.getInstance().trans((Variable) obj, coding, 0);
                    }
                    else
                    {
                        tValue = obj.toString();
                    }
                }
                retval.original.put(oid, tValue);
                // 类型
                if (oid.equals(CollDataTrapUtil.SNMP_TRAP_OID))
                {
                    retval.trapOid = tValue;
                }
                // 时间
                else if (oid.equals(CollDataTrapUtil.SYS_UP_TIME))
                {
                    retval.trapTime = ConvertUtil.stringToLong(tValue, -1);
                }
                // 转换
                if (trans != null)
                {
                    Set<String> keys = trans.keySet();
                    for (String key : keys)
                    {
                        if (oid.startsWith(key))
                        {
                            retval.transMap.put(trans.get(key), tValue);
                        }
                    }
                }
            }
        }
        return retval;
    }

    /**
     * 取值，先考虑原始Map，再考虑转后后的Map
     * @param str
     * @return
     */
    public String getBy(String str)
    {
        // OID精确匹配
        String retval = this.getFromOri(str);
        // OID模糊匹配
        if (retval == null)
        {
            Set<String> keys = original.keySet();
            for (String key : keys)
            {
                if (key.contains(str))
                {
                    logger.debug("模糊匹配：" + key + " : " + str);
                    retval = original.get(key);
                    break;
                }
            }
        }
        // 名称匹配
        if (retval == null)
        {
            retval = this.getFromTrans(str);
        }
        return retval;
    }

    /**
     * 使用数据库数据对Trap进行解析
     * @param objId 对象ID
     */
    public void parse(int objId)
    {
        if (parseMap == null)
        {
            parseMap = new HashMap<String, TrapTableEntity>();
            TrapTableDal ttdal = ClassWrapper.wrapTrans(TrapTableDal.class);
            TrapTableEntity tt = ttdal.getTrap(trapOid, MibUtil.ensureMib(objId));
            if (tt == null)
            {
                return;
            }
            this.trapName = tt.getDescName();
            if (tt.getSubs() == null || tt.getSubs().isEmpty())
            {
                return;
            }
            this.trapName = tt.getDescName();
            Set<String> oids = original.keySet();
            for (String oid : oids)
            {
                TrapTableEntity temp = this.ensureNode(oid, tt);
                parseMap.put(oid, temp);
                if (temp != null)
                {
                    if (transMap == null)
                    {
                        transMap = new HashMap<String, String>();
                    }
                    transMap.put(temp.getTrapName(), original.get(oid));
                }
            }
        }
    }

    private TrapTableEntity ensureNode(String oid, TrapTableEntity tt)
    {
        if (tt == null || tt.getSubs() == null || tt.getSubs().isEmpty())
        {
            return null;
        }
        List<TrapTableEntity> subs = tt.getSubs();
        for (TrapTableEntity sub : subs)
        {
            if (sub.getTrapOid().equals(oid))
            {
                return sub;
            }
        }
        return null;
    }

    /**
     * @return 生成解析后的字符串
     */
    public String genParseStr()
    {
        StringBuilder sb = new StringBuilder();
        Set<String> oids = original.keySet();
        for (String oid : oids)
        {
            TrapTableEntity tt = null;
            if (parseMap != null)
            {
                tt = parseMap.get(oid);
            }
            // 只拼接能找到TrapTableEntity的oid
            if (tt != null)
            {
                sb.append(tt.getDescName());
                sb.append(":");
                sb.append(original.get(oid));
                sb.append(",");
            }
        }
        if (sb.length() > 0)
        {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 宏替换时，获取"$x"对应的trap值
     * 
     * @param key 格式：$x
     * @return
     */
    public String getMacroValue(String key)
    {
        String retval = macroMap.get(key);
        if (retval == null)
        {
            retval = "";
        }
        return retval;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        Set<String> oids = original.keySet();
        for (String oid : oids)
        {
            String tValue = original.get(oid);
            sb.append(oid);
            sb.append(":");
            sb.append(tValue);
            sb.append(";");
        }
        return sb.toString();
    }

    /**
     * 使用转换后的KEY取值
     * @param key 键
     * @return 结果
     */
    public String getFromTrans(String key)
    {
        if (transMap != null)
        {
            return transMap.get(key);
        }
        return null;
    }

    /**
     * @param key 键
     * @return 结果
     */
    public String getFromOri(String key)
    {
        if (original != null)
        {
            return original.get(key);
        }
        return null;
    }

    public Map<String, String> getOriginal()
    {
        return original;
    }

    public void setOriginal(Map<String, String> original)
    {
        this.original = original;
    }

    public long getTrapTime()
    {
        return trapTime;
    }

    public void setTrapTime(long trapTime)
    {
        this.trapTime = trapTime;
    }

    public String getTrapOid()
    {
        return trapOid;
    }

    public void setTrapOid(String trapOid)
    {
        this.trapOid = trapOid;
    }

    public Map<String, String> getTransMap()
    {
        return transMap;
    }

    public void setTransMap(Map<String, String> transMap)
    {
        this.transMap = transMap;
    }

    public long getCollTime()
    {
        return collTime;
    }

    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    public String getTrapName()
    {
        return trapName;
    }

    public void setTrapName(String trapName)
    {
        this.trapName = trapName;
    }
}
