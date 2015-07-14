package jetsennet.orm.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.orm.executor.resultSet.IResultSetHandle;
import jetsennet.orm.util.UncheckedOrmException;

public class SimpleExecutor extends AbsExecutor
{

    private static final Logger logger = LoggerFactory.getLogger(SimpleExecutor.class);

    public int update(Connection conn, String sql)
    {
        int retval = DEFAULT_RETURN;
        Statement stat = null;
        try
        {
            stat = conn.createStatement();
            retval = stat.executeUpdate(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeStatement(stat);
        }
        return retval;
    }

    public int[] update(Connection conn, String[] sqls)
    {
        int length = sqls.length;
        int[] retval = new int[length];
        Statement stat = null;
        try
        {
            stat = conn.createStatement();
            for (int i = 0; i < length; i++)
            {
                retval[i] = stat.executeUpdate(sqls[i]);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeStatement(stat);
        }
        return retval;
    }

    public int[] update(Connection conn, String sql, Object[][] params)
    {
        int[] retval = null;
        PreparedStatement stat = null;
        try
        {
            stat = conn.prepareStatement(sql);
            int recordNum = params.length;
            for (int i = 0; i < recordNum; i++)
            {
                Object[] param = params[i];
                int paramNum = param.length;
                for (int j = 0; j < paramNum; j++)
                {
                    stat.setObject(j, param[j]);
                }
                stat.addBatch();
            }
            retval = stat.executeBatch();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeStatement(stat);
        }
        return retval;
    }

    public <T> T query(Connection conn, String sql, IResultSetHandle<T> handle)
    {
        T retval = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);
            retval = handle.handle(rs);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeStatement(stat);
            closeResultSet(rs);
        }
        return retval;
    }
}
