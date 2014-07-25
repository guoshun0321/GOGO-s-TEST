package jetsennet.orm.ddl;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.DatabaseType;
import jetsennet.orm.util.UncheckedOrmException;

public class Ddl
{

    public static final IDdl getDdl(Configuration conf)
    {
        IDdl retval = null;
        DatabaseType type = conf.dbType;
        switch (type)
        {
        case MYSQL:
            retval = new MySqlDdl(conf);
            break;
        case SQL_SERVER:
            retval = new SqlServerDdl(conf);
            break;
        case ORACLE:
            retval = new OracleDdl(conf);
            break;
        default:
            throw new UncheckedOrmException("暂不支持数据库：" + type.name());
        }
        return retval;
    }

}
