package jetsennet.orm.session;

import jetsennet.orm.transaction.ITransactionManager;

/**
 * 数据操作。
 * 采用继承将方法分布在各个父类中，减轻Session类的大小，便于代码查看。
 * 
 * @author 郭祥
 */
public class Session extends SessionCmp
{

    protected Session(ITransactionManager transaction, SqlSessionFactory factory)
    {
        super(transaction, factory);
    }

}
