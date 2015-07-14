package jetsennet.orm.tableinfo;

import java.lang.reflect.Field;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;

import org.uorm.orm.annotation.ClassMapping;

public class TableInfoUtil
{

    public static final String getTableName(Class<?> cls)
    {
        String retval = null;
        if (cls.isAnnotationPresent(Table.class))
        {
            retval = ((Table) cls.getAnnotation(Table.class)).value();
        }
        else if (cls.isAnnotationPresent(ClassMapping.class))
        {
            retval = ((ClassMapping) cls.getAnnotation(ClassMapping.class)).tableName();
        }
        return retval;
    }

    public static final boolean isColumn(Field f)
    {
        return f.isAnnotationPresent(Column.class);
    }

    public static final boolean isId(Field f)
    {
        return f.isAnnotationPresent(Id.class);
    }

    public static final String getKeygen(Field f)
    {
        return f.getAnnotation(Id.class).keyGen();
    }

    public static final boolean isUorm(Class<?> clz)
    {
        return clz.isAnnotationPresent(ClassMapping.class);
    }

    public static final boolean isDef(Class<?> clz)
    {
        return clz.isAnnotationPresent(Table.class);
    }

}
