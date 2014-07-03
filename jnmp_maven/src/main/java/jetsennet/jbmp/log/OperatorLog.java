package jetsennet.jbmp.log;

import org.apache.log4j.Logger;

import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;

/**
 * 日志操作
 * @author 郭祥
 */
public class OperatorLog
{

    private static ConnectionInfo info =
        new ConnectionInfo(DbConfig.getProperty("bmp_driver"), DbConfig.getProperty("bmp_dburl"), DbConfig.getProperty("bmp_dbuser"), DbConfig
            .getProperty("bmp_dbpwd"));

    private static jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.Nmp-Dmp");

    private static final Logger logger4j = Logger.getLogger(OperatorLog.class);

    /**
     * @param userId 用户id
     * @param userName 用户名称
     * @param msg 信息
     */
    public static void log(int userId, String userName, String msg)
    {
        logger4j.debug(msg);
        logger.logOperator(info, userId, userName, "JBMP", msg);
    }

}
