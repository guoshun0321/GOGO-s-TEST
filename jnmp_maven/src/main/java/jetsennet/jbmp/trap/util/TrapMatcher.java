package jetsennet.jbmp.trap.util;

import java.util.Map;
import java.util.Map.Entry;

import jetsennet.jbmp.alarm.handle.TrapParseEntity;
import jetsennet.jbmp.entity.MObjectEntity;

public class TrapMatcher
{

    /**
     * 类型
     */
    public final String classType;
    /**
     * 匹配OID
     */
    public final String matchOid;
    /**
     * 是否采用Like的匹配方式
     */
    public boolean matchLike;

    public TrapMatcher(String classType, String matchOid, boolean matchLike)
    {
        this.classType = classType;
        this.matchOid = matchOid;
        this.matchLike = matchLike;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append(this.classType).append(":").append(matchOid).append(":");
        sb.append(matchLike ? "like" : "equal").append("\n");
        return sb.toString();
    }

    /**
     * 判断对象和Trap是否满足该匹配
     * @param mo
     * @param trap
     * @return
     */
    public boolean match(MObjectEntity mo, TrapParseEntity trap)
    {
        boolean retval = false;

        // 比较类型
        if (classType.equals(mo.getClassType()))
        {
            Map<String, String> trapMap = trap.getOriginal();
            String indexValue = mo.getField1();
            if (!matchLike)
            {
                String matchValue = trapMap.get(matchOid);
                if (matchValue != null && matchValue.equals(indexValue))
                {
                    retval = true;
                }
            }
            else
            {
                for (Entry<String, String> entry : trapMap.entrySet())
                {
                    String matchedOid = entry.getKey();
                    String matchValue = entry.getValue();
                    if (matchedOid.startsWith(this.matchOid) && matchValue.equals(indexValue))
                    {
                        retval = true;
                        break;
                    }
                }
            }
        }
        return retval;
    }

}
