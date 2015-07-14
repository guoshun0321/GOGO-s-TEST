package jetsennet.orm.session;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.datasource.DataSourceFactory;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transaction.TransactionManager;
import jetsennet.orm.transform.ITransform2Page;
import jetsennet.orm.transform.ITransform2Sql;
import jetsennet.orm.transform.Transform2Page;
import jetsennet.orm.transform.Transform2Sql;

public class SqlSessionFactory
{

    /**
     * 配置文件
     */
    private final Configuration config;
    /**
     * 数据源
     */
    private final DataSource dataSource;
    /**
     * Session缓存
     */
    private final ThreadLocal<Session> localSession;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(SqlSessionFactory.class);

    /**
     * 构造函数
     * 
     * @param config
     */
    protected SqlSessionFactory(Configuration config)
    {
        this.config = config;
        this.dataSource = DataSourceFactory.getDataSource(config);
        localSession = new ThreadLocal<Session>();
    }

    public final Session openSession()
    {
        Session retval = localSession.get();
        if (retval == null)
        {
            retval = this.initSession();
        }
        return retval;
    }

    /**
     * 新建Session
     * 
     * @return
     */
    private final Session initSession()
    {
        ITransactionManager trans = new TransactionManager(this.dataSource);
        ITransform2Page pageTrans = new Transform2Page();
        ITransform2Sql sqlTrans = new Transform2Sql();
        return new Session(trans, pageTrans, sqlTrans);
    }

    public Configuration getConfig()
    {
        return config;
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

}
