package jetsennet.orm.session;

import javax.sql.DataSource;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.datasource.DataSourceFactory;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoMgr;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transaction.TransactionManager;
import jetsennet.orm.transform.AbsTransform2Sql;
import jetsennet.orm.transform.ITransform2Sql;
import jetsennet.orm.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * 表信息
     */
    private final TableInfoMgr tableInfoMgr;
    /**
     * Session缓存
     */
    private final ThreadLocal<Session> localSession;
    /**
     * 连接管理
     */
    private final ITransform2Sql transform;
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
        this.transform = AbsTransform2Sql.ensureTrans(this.config);
        this.localSession = new ThreadLocal<Session>();
        this.tableInfoMgr = new TableInfoMgr();
    }

    /**
     * 获取session，该session和线程绑定
     * @return
     */
    public final Session openSession()
    {
        Session retval = localSession.get();
        if (retval == null)
        {
            retval = this.initSession();
            localSession.set(retval);
        }
        return retval;
    }

    /**
     * 注销和当前线程绑定的session
     */
    public final void closeSession()
    {
        localSession.set(null);
    }

    /**
     * 新建Session
     * 
     * @return
     */
    private final Session initSession()
    {
        ITransactionManager trans = new TransactionManager(this.dataSource, this);
        return new Session(trans, this);
    }

    /**
     * 获取配置信息
     * @return
     */
    public Configuration getConfig()
    {
        return config;
    }

    /**
     * 获取数据源
     * @return
     */
    public DataSource getDataSource()
    {
        return dataSource;
    }

    /**
     * 获取sql转换器
     * @return
     */
    public ITransform2Sql getTransform()
    {
        return transform;
    }

    /**
     * 获取表信息
     * @return
     */
    public TableInfoMgr getTableInfoMgr()
    {
        return tableInfoMgr;
    }

    public TableInfo getTableInfo(String tableName)
    {
        return this.tableInfoMgr.getTableInfo(tableName);
    }

    public TableInfo getTableInfo(Class<?> cls)
    {
        return this.tableInfoMgr.ensureTableInfo(cls);
    }

    public TableInfo tableInfoWithoutBasic(Class<?> cls)
    {
        TableInfo retval = null;
        if (Utils.isBasicType(cls) || cls.equals(String.class))
        {
            return retval;
        }
        try
        {
            retval = this.getTableInfo(cls);
        }
        catch (Exception ex)
        {
            // Ignore
        }
        return retval;
    }

    public boolean isDebug()
    {
        return config.isDebug;
    }

}
