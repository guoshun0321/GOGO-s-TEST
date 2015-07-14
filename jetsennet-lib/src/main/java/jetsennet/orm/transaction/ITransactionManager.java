package jetsennet.orm.transaction;

import java.sql.Connection;

public interface ITransactionManager
{

    /**
     * 打开连接
     */
    public Connection openConnection();

    /**
     * 关闭连接
     */
    public void closeConnection();

    /**
     * 获取连接
     */
    public Connection getConnection();

    /**
     * 开启事务
     */
    public boolean transBegin();

    /**
     * 提交事务
     */
    public void transCommit(boolean isSelf);

    /**
     * 事务回滚
     */
    public void transRollback(boolean isSelf);

}
