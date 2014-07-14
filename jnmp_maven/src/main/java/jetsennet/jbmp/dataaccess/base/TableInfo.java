package jetsennet.jbmp.dataaccess.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 获取表信息
 * @author ？
 */
public class TableInfo
{
    public Class tableClass;
    public String tableName;
    public Field[] columns;
    public String[] columnNames;
    public Method[] getterMethods;
    public Method[] setterMethods;
    public Field keyColumn;
    public String keyColumnName;

    public TableInfo(Class c)
    {
        this.tableClass = c;
        this.tableName = ((Table) tableClass.getAnnotation(Table.class)).name();
        Field[] fields = tableClass.getDeclaredFields();
        List<Field> columnLst = new ArrayList<Field>(fields.length);
        List<String> columnNameLst = new ArrayList<String>(fields.length);
        List<Method> getMethodLst = new ArrayList<Method>(fields.length);
        List<Method> setMethodLst = new ArrayList<Method>(fields.length);
        for (Field f : fields)
        {
            if (!f.isAnnotationPresent(Column.class))
            {
                continue;
            }
            if (f.isAnnotationPresent(Id.class))
            {
                this.keyColumn = f;
                this.keyColumnName = f.getAnnotation(Column.class).name();
            }
            f.setAccessible(true);
            columnLst.add(f);
            columnNameLst.add(f.getAnnotation(Column.class).name());
            try
            {
                String fieldName = f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
                Method m = c.getDeclaredMethod("get" + fieldName);
                m.setAccessible(true);
                getMethodLst.add(m);
                m = c.getDeclaredMethod("set" + fieldName, f.getType());
                m.setAccessible(true);
                setMethodLst.add(m);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        columns = columnLst.toArray(new Field[columnLst.size()]);
        columnNames = columnNameLst.toArray(new String[columnLst.size()]);
        getterMethods = getMethodLst.toArray(new Method[columnLst.size()]);
        setterMethods = setMethodLst.toArray(new Method[columnLst.size()]);
    }
}
