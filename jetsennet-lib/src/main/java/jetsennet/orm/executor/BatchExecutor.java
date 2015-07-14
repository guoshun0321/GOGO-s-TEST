package jetsennet.orm.executor;

import java.sql.Connection;

import jetsennet.orm.executor.resultSet.IResultSetHandle;

public class BatchExecutor implements IExecutor
{

    public int update(Connection conn, String sql)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int[] update(Connection conn, String[] sql)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int[] update(Connection conn, String sql, Object[][] params)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> T query(Connection conn, String sql, IResultSetHandle<T> handle)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
