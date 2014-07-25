package jetsennet.orm.tableinfo;

import java.lang.reflect.Field;

import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableInfoParseClz
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TableInfoParseClz.class);

    public static final TableInfo parse(Class<?> cls)
    {
        TableInfo retval = null;
        try
        {
            // 解析实体信息
            String tableName = TableInfoUtil.getTableName(cls);
            retval = new TableInfo(cls, tableName);

            // 解析字段信息
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields)
            {
                if (!TableInfoUtil.isColumn(f))
                {
                    continue;
                }
                FieldInfo field = new FieldInfo(f);
                retval.field(field);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }
}
