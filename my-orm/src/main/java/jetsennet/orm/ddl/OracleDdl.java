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

public class OracleDdl extends AbsDdl
{

    private static final Logger logger = LoggerFactory.getLogger(OracleDdl.class);

    public OracleDdl(Configuration conf)
    {
        super(conf);
    }

    @Override
    public void create(TableInfo table)
    {
        ConnectionUtil.execute(this.conf, this.genCreateSql(table));
        if (!table.getKeyFields().isEmpty())
        {
            String pkCons = "JETSEN_PK_" + table.getTableName();
            ConnectionUtil.execute(this.conf, this.genCreatePrimarySql(table, pkCons));
        }
    }

    protected String genCreatePrimarySql(TableInfo table, String pkCons)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(table.getTableName()).append(" ADD CONSTRAINT ").append(pkCons).append(" PRIMARY KEY(");
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
            sql = "SELECT * FROM USER_TABLES WHERE TABLE_NAME LIKE '" + pre + "%'";
        }
        else
        {
            sql = "SELECT * FROM USER_TABLES WHERE TABLE_NAME";
        }
        return ConnectionUtil.getStringLst(this.conf, sql);
    }

    @Override
    public boolean isExist(String tableName)
    {
        tableName = tableName.toUpperCase();
        String sql = "SELECT COUNT(Tname) FROM TAB WHERE Tname = UPPER('" + tableName + "')";
        return ConnectionUtil.getSingleInt(this.conf, sql) > 0 ? true : false;
    }

    protected String genDeleteColumnSql(String tableName, String columnName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(tableName).append(" DROP COLUMN ").append(columnName);
        return sb.toString();
    }

    @Override
    public TableInfo getTableInfo(String tableName)
    {
        TableInfo retval = new TableInfo(null, tableName);
        // 获取字段信息
        String sql =
            "SELECT COLUMN_NAME, DATA_TYPE, DATA_PRECISION, DATA_SCALE, NULLABLE, DATA_LENGTH, DATA_DEFAULT FROM USER_TAB_COLUMNS WHERE TABLE_NAME ='"
                + tableName
                + "' ORDER BY 'COLUMN_ID'";
        // 获取主键信息
        String pSql =
            "SELECT cu.* FROM USER_CONS_COLUMNS cu, USER_CONSTRAINTS au WHERE cu.CONSTRAINT_NAME = au.CONSTRAINT_NAME AND au.CONSTRAINT_TYPE = 'P' AND au.TABLE_NAME = '"
                + tableName
                + "'";
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
                retval.field(this.genField(rs));
            }
            rs = stat.executeQuery(pSql);
            while (rs.next())
            {
                String keyName = rs.getString("COLUMN_NAME");
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

    private FieldInfo genField(ResultSet rs) throws Exception
    {
        FieldInfo retval = null;
        // 名称
        String columnName = rs.getString("COLUMN_NAME");
        // 可否为空
        String nullableStr = rs.getString("NULLABLE");
        boolean isNullable = nullableStr.equals("Y") ? true : false;
        // 类型
        String type = rs.getString("DATA_TYPE");
        // 默认值
        String defValue = rs.getString("DATA_DEFAULT");
        int length = -1;
        FieldTypeEnum typeEnum = null;
        if (type.equals("DATE"))
        {
            typeEnum = FieldTypeEnum.DATETIME;
            if (defValue != null)
            {
                if (defValue.equalsIgnoreCase("sysdate "))
                {
                    defValue = "now()";
                }
                else
                {
                    defValue = defValue.substring("to_date('".length(), "to_date('xxxx-xx-xx xx:xx:xx".length());
                }
            }
        }
        else if (type.equals("CLOB"))
        {
            typeEnum = FieldTypeEnum.TEXT;
        }
        else if (type.equals("VARCHAR2"))
        {
            typeEnum = FieldTypeEnum.STRING;
            length = Integer.valueOf(rs.getString("DATA_LENGTH"));
            defValue = defValue == null ? null : defValue.substring(1, defValue.lastIndexOf("'"));
        }
        else if (type.equals("NUMBER"))
        {
            String pricision = rs.getString("DATA_PRECISION");
            String scale = rs.getString("DATA_SCALE");
            defValue = defValue == null ? null : defValue.substring(1, defValue.lastIndexOf("'"));

            if (pricision == null && scale == null)
            {
                typeEnum = FieldTypeEnum.LONG;
            }
            else if (pricision == null && scale.equals("0"))
            {
                typeEnum = FieldTypeEnum.INT;
            }
            else if (pricision != null && scale != null)
            {
                typeEnum = FieldTypeEnum.NUMERIC;
            }
            else
            {
                throw new UncheckedOrmException("解析ORACLE数字类型出错：" + columnName);
            }
        }
        else
        {
            throw new UncheckedOrmException("无效的ORACLE数据类型：" + type);
        }
        System.out.println(columnName + " : " + defValue);
        retval = new FieldInfo(columnName, typeEnum);
        retval.length(length).defaultValue(defValue);
        if (!isNullable)
        {
            retval.disNullable();
        }
        return retval;
    }

    protected void appendFieldInfo(StringBuilder sb, FieldInfo field)
    {
        sb.append(field.getName()).append(" ").append(this.ensureFieldType(field.getType(), field.getLength()));
        //        if (field.isKey())
        //        {
        //            sb.append(" PRIMARY KEY");
        //        }
        if (field.getDefaultValue() != null && field.getType() != FieldTypeEnum.TEXT)
        {
            sb.append(" DEFAULT ");
            if (field.getType() == FieldTypeEnum.DATETIME)
            {
                if (field.getDefaultValue().equals(NOW_FUNCTION))
                {
                    sb.append("sysdate");
                }
                else
                {
                    sb.append("to_date('").append(field.getDefaultValue()).append("','yyyy-mm-dd hh24:mi:ss')");
                }
            }
            else
            {
                sb.append("'").append(field.getDefaultValue()).append("'");
            }
            sb.append("");
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
            retval = "NUMBER";
            break;
        case NUMERIC:
            retval = "NUMBER(38, 5)";
            break;
        case STRING:
            retval = "VARCHAR2(" + length + ")";
            break;
        case TEXT:
            retval = "CLOB";
            break;
        case DATETIME:
            retval = "DATE";
            break;
        default:
            throw new UncheckedOrmException("不支持数据类型：" + type.name());
        }
        return retval;
    }

}
