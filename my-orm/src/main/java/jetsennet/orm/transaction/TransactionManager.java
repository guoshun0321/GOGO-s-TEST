package jetsennet.orm.transaction;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.util.UncheckedOrmException;

public class TransactionManager implements ITransactionManager
{

    /**
     * 数据源
     */
    private final DataSource dataSource;
    /**
     * 连接
     */
    private Connection conn;
    /**
     * 是否在事务中
     */
    private boolean isTrans;
    /**
     * 生成连接的工厂
     */
    private SqlSessionFactory factory;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    public TransactionManager(DataSource dataSource, SqlSessionFactory factory)
    {
        this.dataSource = dataSource;
        this.factory = factory;
    }

    public final Connection openConnection()
    {
        try
        {
            if (this.conn == null || this.conn.isClosed())
            {
                this.conn = dataSource.getConnection();
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        return this.conn;
    }

    public final void closeConnection()
    {
        try
        {
            if (!this.conn.isClosed())
            {
                this.conn.close();
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            this.conn = null;
        }
    }

    public final boolean transBegin()
    {
        boolean retval = false;
        try
        {
            if (!this.isTrans)
            {
                this.openConnection();
                this.conn.setAutoCommit(false);
                this.isTrans = true;
                retval = true;
                logger.debug("transBegin");
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    public final void transCommit(boolean isSelf)
    {
        try
        {
            if (isSelf)
            {
                this.conn.commit();
                logger.debug("transCommit");
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            if (isSelf)
            {
                autoCommit();
            }
        }
    }

    public final void transRollback(boolean isSelf)
    {
        try
        {
            if (isSelf)
            {
                this.conn.rollback();
                logger.debug("transRollback");
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            if (isSelf)
            {
                autoCommit();
            }
        }
    }

    /**
     * 设置为自动提交
     */
    private final void autoCommit()
    {
        try
        {
            this.isTrans = false;
            this.conn.setAutoCommit(true);
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            this.closeConnection();
        }
    }

    public final boolean isConnectionOpen()
    {
        return this.conn != null;
    }

    public boolean isTrans()
    {
        return isTrans;
    }

    public SqlSessionFactory getFactory()
    {
        return factory;
    }

    @Override
    public Clob createClob()
    {
        try
        {
            return this.conn.createClob();
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
    }

    @Override
    public Blob createBlob()
    {
        try
        {
            return this.conn.createBlob();
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
    }

}
