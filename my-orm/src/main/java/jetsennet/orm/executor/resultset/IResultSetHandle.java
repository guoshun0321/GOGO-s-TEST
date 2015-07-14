package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;

public interface IResultSetHandle<T>
{

    /**
     * 处理查询结果
     * 
     * @param rs
     * @return
     */
    public T handle(ResultSet rs) throws Exception;

    /**
     * 设置开始值偏移位置
     * 
     * @param offset
     */
    public void setOffset(int offset);

    /**
     * 设置最大取值量
     * @param max
     */
    public IResultSetHandle<T> setMax(int max);

}
