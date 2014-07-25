package jetsennet.orm.util;

import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil
{

    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

    public static void closeStatement(Statement stat)
    {
        try
        {
            if (stat != null)
            {
                stat.close();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public static void closeResultSet(ResultSet rs)
    {
        try
        {
            if (rs != null)
            {
                rs.close();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

}
