package jetsennet.jbmp.alarm.relevance;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.util.ConfigUtil;

public class AlarmEventFilterManager
{

    /**
     * 报警过滤
     */
    private IAlarmEventFilter filter;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AlarmEventFilterManager.class);

    private static final AlarmEventFilterManager instance = new AlarmEventFilterManager();

    private AlarmEventFilterManager()
    {
        this.filter = this.ensureFilterClass();
    }

    public static AlarmEventFilterManager getInstance()
    {
        return instance;
    }

    /**
     * 过滤报警。
     * 
     * @param event
     * @return 过滤规则不存在或通过过滤时，返回true；未通过过滤时，返回false。
     */
    public boolean filter(AlarmEventEntity event)
    {
        boolean retval = true;
        if (filter != null)
        {
            retval = filter.filter(event);
        }
        return retval;
    }

    private IAlarmEventFilter ensureFilterClass()
    {
        IAlarmEventFilter retval = null;
        String filterStr = ConfigUtil.getString("alarm.filter", null);
        if (filterStr != null)
        {
            try
            {
                retval = (IAlarmEventFilter) Class.forName(filterStr).newInstance();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        else
        {
            logger.debug("未配置alarm.filter参数，不启用报警相关性分析。");
        }
        return retval;
    }
}
