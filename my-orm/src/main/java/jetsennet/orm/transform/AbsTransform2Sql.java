package jetsennet.orm.transform;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.util.UncheckedOrmException;

public abstract class AbsTransform2Sql implements ITransform2Sql
{

    public static ITransform2Sql ensureTrans(Configuration config)
    {
        ITransform2Sql retval = null;
        switch (config.dbType)
        {
        case SQL_SERVER:
            retval = new Transform2SqlSqlServer(config);
            break;
        case ORACLE:
            retval = new Transform2SqlOracle(config);
            break;
        case MYSQL:
            retval = new Transform2SqlMySql(config);
            break;
        default:
            throw new UncheckedOrmException("未知数据库类型：" + config.dbType);
        }
        return retval;
    }

}
