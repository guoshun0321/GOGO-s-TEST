package jetsennet.orm.sql.cascade;

import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.SqlTypeEnum;

public class CascadeSqlInsertEntity extends CascadeSqlEntity
{

    private boolean autoKey;

    public CascadeSqlInsertEntity(String tableName)
    {
        super(tableName);
        this.autoKey = false;
    }

    public ISql genSql()
    {
        return this.tableInfo.map2Sql(this.valueMap, SqlTypeEnum.INSERT);
    }

    public boolean isAutoKey()
    {
        return autoKey;
    }

    public void setAutoKey(boolean autoKey)
    {
        this.autoKey = autoKey;
    }

}
