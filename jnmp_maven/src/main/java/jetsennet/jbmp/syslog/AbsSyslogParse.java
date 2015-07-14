package jetsennet.jbmp.syslog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ？
 */
public abstract class AbsSyslogParse implements ISyslogParse
{

    /**
     * 默认PRI
     */
    public static String PRI_DEF = "(<(\\d+)>)";
    /**
     * 默认时间格式
     */
    public static String TIME_DEF = "((\\p{Upper}\\p{Lower}{2}) ([\\d ][1-9]) (\\d\\d:\\d\\d:\\d\\d))";
    /**
     * 默认主机名
     */
    public static String HOST_NAME_DEF = "([^ \\t\\n\\x0B\\f\\r]+)";
    /**
     * 默认TAG
     */
    public static String TAG_DEF = "([^ \\t\\n\\x0B\\f\\r:]+)";
    /**
     * 冒号
     */
    public static String COLON = ":";
    /**
     * 空格
     */
    public static String SPACE = " ";

    /**
     * 解析
     * @param syslog 参数
     * @throws SyslogParseException 异常
     * @return 结果
     */
    public SyslogParseEntity parse(String syslog) throws SyslogParseException
    {
        try
        {
            if (syslog == null)
            {
                return null;
            }
            Matcher match = Pattern.compile(this.ensureParseExp()).matcher(syslog);
            if (match.find())
            {
                String msg = syslog;
                SyslogParseEntity sys = new SyslogParseEntity();
                msg = this.parsePRI(match, sys, msg);
                msg = this.parseTime(match, sys, msg);
                msg = this.parseHostName(match, sys, msg);
                msg = this.parseTAG(match, sys, msg);
                msg = this.parseMSG(match, sys, msg);
                return sys;
            }
            else
            {
                throw new SyslogParseException(String.format("SYSLOG消息：%s无法解析。", syslog));
            }
        }
        catch (Exception ex)
        {
            throw new SyslogParseException(ex);
        }
    }

    protected String ensureParseExp()
    {
        String retval = PRI_DEF + TIME_DEF + SPACE + HOST_NAME_DEF + SPACE + TAG_DEF + COLON;
        return retval;
    }

    protected String parsePRI(Matcher match, SyslogParseEntity sys, String msg)
    {
        String priStr = match.group(1);
        sys.pri = priStr;
        msg = msg.substring(priStr.length());
        return msg;
    }

    protected String parseTime(Matcher match, SyslogParseEntity sys, String msg)
    {
        String timeStr = match.group(3);
        sys.time = timeStr;
        msg = msg.substring(timeStr.length());
        return msg;
    }

    protected String parseHostName(Matcher match, SyslogParseEntity sys, String msg)
    {
        String hostNameStr = match.group(7);
        sys.hostName = hostNameStr;
        msg = msg.substring(hostNameStr.length());
        return msg;
    }

    protected String parseTAG(Matcher match, SyslogParseEntity sys, String msg)
    {
        String tagStr = match.group(8);
        sys.tag = tagStr;
        msg = msg.substring(tagStr.length());
        return msg;
    }

    protected void printGroup(Matcher match)
    {
        for (int i = 0; i <= match.groupCount(); i++)
        {
            System.out.println(" * " + i + "->" + match.group(i));
        }
    }

    protected String parseMSG(Matcher match, SyslogParseEntity sys, String msg)
    {
        return "";
    }
}
