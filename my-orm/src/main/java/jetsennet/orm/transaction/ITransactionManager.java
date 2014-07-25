package jetsennet.orm.transaction;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;

import jetsennet.orm.session.SqlSessionFactory;

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

    /**
     * 是否已经持有连接
     * 
     * @return
     */
    public boolean isConnectionOpen();

    /**
     * 是否已经打开事务
     * 
     * @return
     */
    public boolean isTrans();

    /**
     * 创建Clob
     * 
     * @return
     */
    public Clob createClob();

    /**
     * 创建Blob
     * 
     * @return
     */
    public Blob createBlob();

    /**
     * 获取session工厂
     * @return
     */
    public SqlSessionFactory getFactory();

}
