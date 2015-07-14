package jetsennet.orm.ddl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.IConfigurationBuilder;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.FieldTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.UncheckedOrmException;

public class SqlServerDdl extends AbsDdl
{

    private static final Logger logger = LoggerFactory.getLogger(SqlServerDdl.class);

    public SqlServerDdl(Configuration conf)
    {
        super(conf);
    }

    @Override
    public void create(TableInfo table)
    {
        ConnectionUtil.execute(this.conf, this.genCreateSql(table));
        if (!table.getKeyFields().isEmpty())
        {
            ConnectionUtil.execute(this.conf, this.genCreatePrimarySql(table));
        }
    }

    protected String genCreatePrimarySql(TableInfo table)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(table.getTableName()).append(" ADD PRIMARY KEY(");
        List<FieldInfo> fields = table.getKeyFields();
        for (FieldInfo field : fields)
        {
            sb.append(field.getName()).append(",");
        }
        if (fields.size() > 0)
        {
            sb.setLength(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public List<String> listTable(String pre)
    {
        String sql = null;
        if (pre != null && !pre.isEmpty())
        {
            sql = "SELECT NAME FROM SYSOBJECTS WHERE XTYPE = 'U' AND NAME LIKE '" + pre + "%'";
        }
        else
        {
            sql = "SELECT NAME FROM SYSOBJECTS WHERE XTYPE = 'U'";
        }
        return ConnectionUtil.getStringLst(this.conf, sql);
    }

    @Override
    public boolean isExist(String tableName)
    {
        String sql = "SELECT OBJECTPROPERTY(object_id('dbo." + tableName + "'),'IsUserTable')";
        int temp = ConnectionUtil.getSingleInt(this.conf, sql);
        return temp > 0 ? true : false;
    }

    @Override
    public void deleteColumn(String tableName, String columnName)
    {
        tableName = tableName.toUpperCase();
        // 获取默认值约束
        String sql =
            "SELECT b.NAME FROM SYSCOLUMNS a, SYSOBJECTS b WHERE a.ID=OBJECT_ID('"
                + tableName
                + "') AND b.ID = a.CDEFAULT AND a.NAME='"
                + columnName
                + "' AND b.NAME LIKE 'DF%';";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = ConnectionUtil.getConnection(this.conf);
            conn.setAutoCommit(false);
            stat = conn.createStatement();
            logger.debug(sql);
            rs = stat.executeQuery(sql);
            String defValueCons = null;
            if (rs.next())
            {
                defValueCons = rs.getString(1);
            }
            if (defValueCons != null)
            {
                sql = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + defValueCons;
                logger.debug(sql);
                stat.execute(sql);
            }
            sql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
            logger.debug(sql);
            stat.execute(sql);
            conn.commit();
        }
        catch (Exception ex)
        {
            try
            {
                conn.rollback();
            }
            catch (Exception ex1)
            {
                throw new UncheckedOrmException(ex);
            }
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            ConnectionUtil.closeConnection(conn, stat, rs);
        }
    }

    @Override
    public TableInfo getTableInfo(String tableName)
    {
        TableInfo retval = new TableInfo(null, tableName);
        // 获取字段信息
        String sql =
            "SELECT a.NAME, b.NAME AS TYPE, a.ISNULLABLE, a.LENGTH, c.TEXT AS DEFVALUE FROM SYSCOLUMNS a INNER JOIN SYSTYPES b on a.XUSERTYPE = b.XUSERTYPE LEFT JOIN SYSCOMMENTS c ON a.CDEFAULT=c.ID  WHERE a.ID = OBJECT_ID('"
                + tableName
                + "')";
        // 获取主键信息
        String pSql =
            "SELECT a.NAME FROM SYSCOLUMNS a INNER JOIN SYSOBJECTS d on a.ID=d.ID WHERE d.NAME='"
                + tableName
                + "' AND EXISTS(SELECT 1 FROM SYSOBJECTS WHERE XTYPE='PK' AND PARENT_OBJ=a.ID and NAME IN(SELECT name FROM SYSINDEXES WHERE indid IN(SELECT indid FROM sysindexkeys WHERE id = a.id AND colid=a.colid)))";
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = ConnectionUtil.getConnection(this.conf);
            stat = conn.createStatement();
            logger.debug(sql);
            rs = stat.executeQuery(sql);
            int length = -1;
            while (rs.next())
            {
                // 名称
                String columnName = rs.getString("NAME");
                // 默认值
                String defValue = rs.getString("DEFVALUE");
                if (defValue != null)
                {
                    if (defValue.equalsIgnoreCase("(getdate())"))
                    {
                        defValue = NOW_FUNCTION;
                    }
                    else
                    {
                        defValue = defValue.substring(2, defValue.lastIndexOf('\''));
                    }
                }
                // 类型
                String type = rs.getString("TYPE");
                FieldTypeEnum typeEnum = null;
                if (type.equals("int"))
                {
                    typeEnum = FieldTypeEnum.INT;
                }
                else if (type.equals("bigint"))
                {
                    typeEnum = FieldTypeEnum.LONG;
                }
                else if (type.equals("numeric"))
                {
                    typeEnum = FieldTypeEnum.NUMERIC;
                }
                else if (type.equals("nvarchar"))
                {
                    typeEnum = FieldTypeEnum.STRING;
                    length = Integer.valueOf(rs.getString("LENGTH")) / 2;
                    if (defValue != null)
                    {
                        defValue = defValue.substring(1, defValue.lastIndexOf('\"'));
                    }
                }
                else if (type.equals("text"))
                {
                    typeEnum = FieldTypeEnum.TEXT;
                }
                else if (type.equals("datetime"))
                {
                    typeEnum = FieldTypeEnum.DATETIME;
                }
                else
                {
                    throw new UncheckedOrmException("暂时不支持类型：" + type);
                }
                // 可否为空
                String nullableStr = rs.getString("ISNULLABLE");
                boolean isNullable = nullableStr.equals("1") ? true : false;
                FieldInfo field = new FieldInfo(columnName, typeEnum);
                field.length(length).defaultValue(defValue);
                if (!isNullable)
                {
                    field.disNullable();
                }
                retval.field(field);
            }
            // 主键
            rs = stat.executeQuery(pSql);
            while (rs.next())
            {
                String keyName = rs.getString("NAME");
                retval.key(keyName);
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
                sb.append(" DEFAULT GETDATE()");
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
        if (!field.isNullable() || field.isKey())
        {
            sb.append(" NOT NULL");
        }
    }

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
            retval = "NUMERIC(18,4)";
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
