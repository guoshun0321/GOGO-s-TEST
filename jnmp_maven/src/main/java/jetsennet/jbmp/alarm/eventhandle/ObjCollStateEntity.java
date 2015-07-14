package jetsennet.jbmp.alarm.eventhandle;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 对象采集状态实体
 * 
 * @author 郭祥
 */
public class ObjCollStateEntity
{

    /**
     * 对象ID
     */
    public int objId;
    /**
     * 对象采集状态
     */
    public int objState;
    /**
     * 子对象采集状态
     */
    public Map<Integer, Integer> subMap;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ObjCollStateEntity.class);

    public ObjCollStateEntity()
    {
        this.objId = -1;
    }

    public void setObj(int objId, int objState)
    {
        this.objId = objId;
        this.objState = objState;
    }

    public void addSub(int objId, int objState)
    {
        if (subMap == null)
        {
            subMap = new HashMap<Integer, Integer>();
        }
        subMap.put(objId, objState);
    }

    /**
     * 转换成采集器和服务器之间传输的字符串
     * 
     * @return
     */
    public String toTransMsg()
    {
        if (objId < 0)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(objId).append(":").append(objState).append(";");
        if (subMap != null)
        {
            for (Map.Entry<Integer, Integer> sub : subMap.entrySet())
            {
                sb.append(sub.getKey()).append(":").append(sub.getValue()).append(";");
            }
        }
        if (sb.length() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 将字符串转换成实体
     * @param msg
     * @return
     */
    public static ObjCollStateEntity fromTransMsg(String msg)
    {
        if (msg == null || msg.trim().isEmpty())
        {
            return null;
        }
        ObjCollStateEntity retval = null;
        try
        {
            String[] statMsgs = msg.split(";");
            int sLength = statMsgs.length;
            if (sLength > 0)
            {
                retval = new ObjCollStateEntity();
                String[] temp = statMsgs[0].split(":");
                retval.setObj(Integer.valueOf(temp[0]), Integer.valueOf(temp[1]));
                for (int i = 1; i < sLength; i++)
                {
                    temp = statMsgs[i].split(":");
                    retval.addSub(Integer.valueOf(temp[0]), Integer.valueOf(temp[1]));
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = null;
        }
        return retval;
    }
}
