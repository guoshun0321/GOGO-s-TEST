package jetsennet.orm.executor;

import java.sql.Connection;

import jetsennet.orm.executor.resultSet.IResultSetHandle;

public interface IExecutor
{
    
    public static final int DEFAULT_RETURN = -1;

    public int update(Connection conn, String sql);

    public int[] update(Connection conn, String[] sql);

    public int[] update(Connection conn, String sql, Object[][] params);

    public <T> T query(Connection conn, String sql, IResultSetHandle<T> handle);

}
