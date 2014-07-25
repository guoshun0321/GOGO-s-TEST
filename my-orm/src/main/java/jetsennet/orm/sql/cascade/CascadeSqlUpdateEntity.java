package jetsennet.orm.sql.cascade;

import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.SqlTypeEnum;

public class CascadeSqlUpdateEntity extends CascadeSqlEntity
{

    public CascadeSqlUpdateEntity(String tableName)
    {
        super(tableName);
    }

    public ISql genSql()
    {
        return this.tableInfo.map2Sql(this.valueMap, SqlTypeEnum.UPDATE);
    }

}
