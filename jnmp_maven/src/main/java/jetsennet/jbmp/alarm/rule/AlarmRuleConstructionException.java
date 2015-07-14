package jetsennet.jbmp.alarm.rule;

/**
 * 报警构造时异常
 * @author 郭祥
 */
public class AlarmRuleConstructionException extends Exception
{

    /**
     * 构造函数
     */
    public AlarmRuleConstructionException()
    {
        super();
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public AlarmRuleConstructionException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public AlarmRuleConstructionException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public AlarmRuleConstructionException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
