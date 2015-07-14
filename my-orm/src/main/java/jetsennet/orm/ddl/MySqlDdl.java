package jetsennet.orm.ddl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.FieldTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDdl extends AbsDdl
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MySqlDdl.class);

    public MySqlDdl(Configuration conf)
    {
        super(conf);
    }

    @Override
    public List<String> listTable(String pre)
    {
        List<String> retval = ConnectionUtil.getStringLst(this.conf, "SHOW TABLES");
        if (pre != null && !pre.isEmpty())
        {
            pre = pre.toLowerCase();
            for (int i = 0; i < retval.size();)
            {
                if (!retval.get(i).startsWith(pre))
                {
                    retval.remove(i);
                }
                else
                {
                    i++;
                }
            }
        }
        return retval;
    }

    @Override
    public void delete(String tableName)
    {
        ConnectionUtil.execute(this.conf, this.genDropSql(tableName.toUpperCase()));
    }

    protected String genDropSql(String tableName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE IF EXISTS ").append(tableName);
        return sb.toString();
    }

    @Override
    public boolean isExist(String tableName)
    {
        boolean retval = false;
        tableName = tableName.toUpperCase();
        String existSql = "SELECT COUNT(0) FROM `INFORMATION_SCHEMA`.`TABLES` WHERE `TABLE_NAME`='" + tableName + "'";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = ConnectionUtil.getConnection(this.conf);
            stat = conn.createStatement();
            logger.debug(existSql);
            rs = stat.executeQuery(existSql);
            if (rs.next())
            {
                int temp = rs.getInt(1);
                if (temp > 0)
                {
                    retval = true;
                }
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            ConnectionUtil.closeConnection(conn, stat, rs);
        }
        return retval;
    }

    protected String genCreateSql(TableInfo table)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(table.getTableName()).append(" (");
        List<FieldInfo> fields = table.getFieldInfos();
        StringBuilder keyStr = new StringBuilder();
        for (FieldInfo field : fields)
        {
            if (field.isKey())
            {
                keyStr.append(field.getName()).append(",");
            }
            this.appendFieldInfo(sb, field);
            sb.append(",");
        }
        if (fields.size() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (keyStr.length() > 0)
        {
            keyStr.deleteCharAt(keyStr.length() - 1);
            sb.append(", PRIMARY KEY(").append(keyStr.toString()).append(")");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public TableInfo getTableInfo(String tableName)
    {
        TableInfo retval = new TableInfo(null, tableName);
        // 获取字段信息
        String sql = "desc " + tableName;
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = ConnectionUtil.getConnection(this.conf);
            stat = conn.createStatement();
            logger.debug(sql);
            rs = stat.executeQuery(sql);
            while (rs.next())
            {
                // 字段名
                String fieldName = rs.getString("Field");
                String type = rs.getString("Type");
                // 默认值
                String defValue = rs.getString("Default");
                // 字段类型
                FieldTypeEnum typeEnum = null;
                int length = -1;
                if (type.startsWith("int("))
                {
                    typeEnum = FieldTypeEnum.INT;
                }
                else if (type.startsWith("bigint("))
                {
                    typeEnum = FieldTypeEnum.LONG;
                }
                else if (type.startsWith("double"))
                {
                    typeEnum = FieldTypeEnum.NUMERIC;
                }
                else if (type.startsWith("varchar("))
                {
                    typeEnum = FieldTypeEnum.STRING;
                    length = Integer.valueOf(type.substring("varchar(".length(), type.length() - 1));
                    defValue = defValue == null ? null : defValue.substring(1, defValue.lastIndexOf('"'));
                }
                else if (type.startsWith("text"))
                {
                    typeEnum = FieldTypeEnum.TEXT;
                }
                else if (type.startsWith("datetime"))
                {
                    typeEnum = FieldTypeEnum.DATETIME;
                    if (defValue != null && defValue.equalsIgnoreCase("CURRENT_TIMESTAMP"))
                    {
                        defValue = NOW_FUNCTION;
                    }
                }
                else
                {
                    throw new UncheckedOrmException("暂时不支持类型：" + type);
                }
                // 是否可空
                String nullable = rs.getString("Null");
                boolean isNullable = nullable.equals("YES") ? true : false;
                // 是否主键
                String keyStr = rs.getString("Key");
                boolean isKey = keyStr.equals("PRI");
                FieldInfo field = new FieldInfo(fieldName, typeEnum);
                field.length(length).defaultValue(defValue);
                if (!isNullable)
                {
                    field.disNullable();
                }
                if (isKey)
                {
                    field.key();
                }
                retval.field(field);
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            ConnectionUtil.closeConnection(conn, stat, rs);
        }
        return retval;
    }

    /**
     * 添加字段信息
     * 
     * @param sb
     * @param field
     */
    protected void appendFieldInfo(StringBuilder sb, FieldInfo field)
    {
        sb.append(field.getName()).append(" ").append(this.ensureFieldType(field.getType(), field.getLength()));
        if (field.getDefaultValue() != null && field.getType() != FieldTypeEnum.TEXT)
        {
            if (field.getType() == FieldTypeEnum.DATETIME && field.getDefaultValue().equalsIgnoreCase(NOW_FUNCTION))
            {
                sb.append(" DEFAULT NOW()");
            }
            else
            {
                sb.append(" DEFAULT '");
                if (field.getType() == FieldTypeEnum.STRING)
                {
                    sb.append("\"").append(field.getDefaultValue()).append("\"");
                }
                else
                {
                    sb.append(field.getDefaultValue());
                }
                sb.append("'");
            }
        }
        if (!field.isNullable())
        {
            sb.append(" NOT NULL");
        }
    }

}
