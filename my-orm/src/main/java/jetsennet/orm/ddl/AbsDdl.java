package jetsennet.orm.ddl;

import java.util.List;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.FieldTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsDdl implements IDdl
{

    protected Configuration conf;
    /**
     * now函数
     */
    public static final String NOW_FUNCTION = "now()";
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AbsDdl.class);

    public AbsDdl(Configuration conf)
    {
        this.conf = conf;
    }

    @Override
    public void create(TableInfo table)
    {
        ConnectionUtil.execute(this.conf, this.genCreateSql(table));
    }

    @Override
    public void rebuild(TableInfo table)
    {
        if (this.isExist(table.getTableName()))
        {
            this.delete(table.getTableName());
        }
        this.create(table);
    }

    protected String genCreateSql(TableInfo table)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(table.getTableName()).append(" (");
        List<FieldInfo> fields = table.getFieldInfos();
        for (FieldInfo field : fields)
        {
            this.appendFieldInfo(sb, field);
            sb.append(",");
        }
        if (fields.size() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void delete(String tableName)
    {
        if (this.isExist(tableName))
        {
            ConnectionUtil.execute(this.conf, this.genDropSql(tableName.toUpperCase()));
        }
    }

    protected String genDropSql(String tableName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE ").append(tableName);
        return sb.toString();
    }

    @Override
    public void addColumn(String tableName, FieldInfo field)
    {
        ConnectionUtil.execute(this.conf, this.genAddColumnSql(tableName.toUpperCase(), field));
    }

    protected String genAddColumnSql(String tableName, FieldInfo field)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(tableName).append(" ADD ");
        this.appendFieldInfo(sb, field);
        return sb.toString();
    }

    @Override
    public void deleteColumn(String tableName, String columnName)
    {
        ConnectionUtil.execute(this.conf, this.genDeleteColumnSql(tableName.toUpperCase(), columnName.toUpperCase()));
    }

    protected String genDeleteColumnSql(String tableName, String columnName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(tableName).append(" DROP ").append(columnName);
        return sb.toString();
    }

    protected String genGetTableInfoSql(String tableName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(tableName).append(" LIMIT 0, 1");
        return sb.toString();
    }

    protected abstract void appendFieldInfo(StringBuilder sb, FieldInfo field);

    /**
     * 确定字段类型
     * 
     * @param type
     * @param length
     * @return
     */
    protected String ensureFieldType(FieldTypeEnum type, int length)
    {
        String retval = null;
        switch (type)
        {
        case INT:
            retval = "INT";
            break;
        case LONG:
            retval = "BIGINT";
            break;
        case NUMERIC:
            retval = "DOUBLE";
            break;
        case STRING:
            retval = "NVARCHAR(" + length + ")";
            break;
        case TEXT:
            retval = "TEXT";
            break;
        case DATETIME:
            retval = "DATETIME";
            break;
        default:
            throw new UncheckedOrmException("不支持数据类型：" + type.name());
        }
        return retval;
    }

}
