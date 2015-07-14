package jetsennet.orm.tableinfo;

import java.sql.Types;
import java.util.Date;

import jetsennet.orm.util.UncheckedOrmException;

public enum FieldTypeEnum
{

    INT, LONG, NUMERIC, STRING, TEXT, DATETIME;

    public static Class<?> ensureClz(FieldTypeEnum fEnum)
    {
        Class<?> retval = null;
        switch (fEnum)
        {
        case INT:
            retval = int.class;
            break;
        case LONG:
            retval = long.class;
            break;
        case NUMERIC:
            retval = double.class;
            break;
        case STRING:
            retval = String.class;
            break;
        case TEXT:
            retval = String.class;
            break;
        case DATETIME:
            retval = Date.class;
            break;
        default:
            break;
        }
        return retval;
    }

    public static FieldTypeEnum valueOf(Class<?> clz)
    {
        return valueOf(clz, false);
    }

    public static FieldTypeEnum valueOf(Class<?> clz, boolean isText)
    {
        FieldTypeEnum retval = null;
        if (clz.equals(int.class) || clz.equals(short.class))
        {
            retval = INT;
        }
        else if (clz.equals(long.class))
        {
            retval = LONG;
        }
        else if (clz.equals(double.class))
        {
            retval = NUMERIC;
        }
        else if (clz.equals(String.class))
        {
            if (!isText)
            {
                retval = STRING;
            }
            else
            {
                retval = TEXT;
            }
        }
        else if (clz.equals(Date.class))
        {
            retval = DATETIME;
        }
        return retval;
    }

    public static FieldTypeEnum valueOfIgnorCase(String type)
    {
        return valueOf(type.toUpperCase());
    }

    public static int toSqlType(FieldTypeEnum fieldType)
    {
        int retval = Types.INTEGER;
        switch (fieldType)
        {
        case INT:
            retval = Types.INTEGER;
            break;
        case LONG:
            retval = Types.BIGINT;
            break;
        case NUMERIC:
            retval = Types.NUMERIC;
            break;
        case STRING:
            retval = Types.VARCHAR;
            break;
        case TEXT:
            retval = Types.CLOB;
            break;
        case DATETIME:
            retval = Types.TIMESTAMP;
            break;
        default:
            break;
        }
        return retval;
    }

    /**
     * 将SqlType转到FieldTypeEnum。其中很多类型都是不支持的。
     * @param sqlType
     * @return
     */
    public static FieldTypeEnum sqlType2FieldType(int sqlType)
    {
        FieldTypeEnum retval = null;
        switch (sqlType)
        {
        case Types.INTEGER:
            retval = FieldTypeEnum.INT;
            break;
        case Types.BIGINT:
            retval = FieldTypeEnum.LONG;
            break;
        case Types.NUMERIC:
            retval = FieldTypeEnum.NUMERIC;
            break;
        case Types.VARCHAR:
            retval = FieldTypeEnum.STRING;
            break;
        case Types.TIMESTAMP:
            retval = FieldTypeEnum.DATETIME;
            break;
        case Types.CLOB:
            retval = FieldTypeEnum.TEXT;
            break;
        default:
            throw new UncheckedOrmException("不支持SQL类型：" + sqlType);
        }
        return retval;
    }

}
