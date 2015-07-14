package jetsennet.jbmp.datacollect.datasource;

import org.apache.log4j.Logger;

import jetsennet.jbmp.util.ConfigUtil;

/**
 * 数据代理
 * @author GoGo
 */
public class DataAgentManager
{

    /**
     * 默认采集类型
     */
    public static String COLL_TYPE_STR = "jetsennet.jbmp.datacollect.datasource.DataAgent";
    /**
     * 配置的采集类型
     */
    private static final String COLL_TYPE;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DataAgentManager.class);

    static
    {
        COLL_TYPE = ConfigUtil.getString("collect.type", COLL_TYPE_STR);
    }

    /**
     * @return 结果
     */
    public static IDataAgent getAgent()
    {
        IDataAgent retval = null;
        try
        {
            retval = (IDataAgent) Class.forName(COLL_TYPE).newInstance();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

}
