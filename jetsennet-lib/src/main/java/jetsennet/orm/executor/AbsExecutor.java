package jetsennet.orm.executor;

import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsExecutor implements IExecutor
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AbsExecutor.class);

    protected final void closeStatement(Statement stat)
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

    protected final void closeResultSet(ResultSet rs)
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
