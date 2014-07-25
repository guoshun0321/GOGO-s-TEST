package jetsennet.orm.executor;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import jetsennet.orm.transaction.ITransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsExecutor implements IExecutor
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AbsExecutor.class);

    protected final void closeConnection(ITransactionManager trans)
    {
        try
        {
            if (trans != null)
            {
                trans.closeConnection();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    protected final void closeClob(Clob clob)
    {
        try
        {
            if (clob != null)
            {
                clob.free();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    protected final void closeClobs(List<Clob> clobs)
    {
        for (Clob clob : clobs)
        {
            closeClob(clob);
        }
    }

    protected final void closeBlob(Blob blob)
    {
        try
        {
            if (blob != null)
            {
                blob.free();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    protected final void closeBlobs(List<Blob> blobs)
    {
        for (Blob blob : blobs)
        {
            closeBlob(blob);
        }
    }

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
