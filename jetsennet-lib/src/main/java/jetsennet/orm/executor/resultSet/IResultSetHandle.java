package jetsennet.orm.executor.resultSet;

import java.sql.ResultSet;

public interface IResultSetHandle<T>
{

    /**
     * 处理查询结果
     * 
     * @param rs
     * @return
     */
    public T handle(ResultSet rs);

}
