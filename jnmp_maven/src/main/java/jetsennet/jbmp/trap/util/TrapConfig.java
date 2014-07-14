package jetsennet.jbmp.trap.util;

import java.util.List;

public class TrapConfig
{

    /**
     * OID
     */
    public final String trapOid;
    /**
     * 匹配
     */
    public final List<TrapMatcher> matchers;

    public TrapConfig(String trapOid, List<TrapMatcher> matchers)
    {
        this.trapOid = trapOid;
        this.matchers = matchers;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(trapOid).append("\n");
        if (matchers != null)
        {
            for (TrapMatcher matcher : matchers)
            {
                sb.append(matcher);
            }
        }
        return sb.toString();
    }

}
