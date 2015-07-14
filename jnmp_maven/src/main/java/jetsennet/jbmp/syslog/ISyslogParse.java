package jetsennet.jbmp.syslog;

/**
 * @author ？
 */
public interface ISyslogParse
{

    /**
     * @param syslog 参数
     * @return 结果
     * @throws SyslogParseException 异常
     */
    public SyslogParseEntity parse(String syslog) throws SyslogParseException;
}
