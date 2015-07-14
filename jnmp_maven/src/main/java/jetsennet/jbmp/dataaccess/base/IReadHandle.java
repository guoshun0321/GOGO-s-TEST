package jetsennet.jbmp.dataaccess.base;

import java.sql.ResultSet;

/**
 * 包装exec.executeReader(sql);的后续操作
 * 
 * @author 郭祥
 */
public interface IReadHandle
{

    /**
     * 处理结果集
     * @param rs
     */
    public abstract void handle(ResultSet rs) throws Exception;

}
