package jetsennet.orm.transaction;

import java.sql.Connection;

import javax.sql.DataSource;

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

    public TransactionManager(DataSource dataSource)
    {
        this.dataSource = dataSource;

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

    public final Connection getConnection()
    {
        return this.openConnection();
    }

    public final boolean transBegin()
    {
        boolean retval = false;
        try
        {
            if (!this.isTrans)
            {
                this.conn.setAutoCommit(false);
                retval = true;
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
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            notAutoCommit();
        }
    }

    public final void transRollback(boolean isSelf)
    {
        try
        {
            if (isSelf)
            {
                this.conn.rollback();
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            notAutoCommit();
        }
    }

    private final void notAutoCommit()
    {
        try
        {
            this.conn.setAutoCommit(true);
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
    }

}
