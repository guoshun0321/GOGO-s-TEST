package jetsennet.orm.executor.keygen;

import jetsennet.orm.session.SessionBase;

public interface IKeyGeneration<T>
{

    /**
     * 获取一个单独的主键
     * 
     * @param session
     * @return
     */
    public T genKey(SessionBase session, String tableName);

    /**
     * 批量获取主键
     * 
     * @param session
     * @param num 主键数量
     * @return
     */
    public T[] genKeys(SessionBase session, String tableName, int num);
    
    /**
     * 批量获取主键
     * 
     * @param session
     * @param tableName 表名
     * @param fieldName 字段名
     * @param num 主键数量
     * @return
     */
    public T[] genKeys(SessionBase session, String tableName, String fieldName, int num);
}
