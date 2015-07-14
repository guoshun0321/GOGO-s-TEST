package jetsennet.orm.tableinfo;

import static jetsennet.orm.sql.FilterUtil.eq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.sql.DeleteEntity;
import jetsennet.orm.sql.FilterNode;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.InsertEntity;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.sql.UpdateEntity;
import jetsennet.orm.util.UncheckedOrmException;
import jetsennet.orm.util.Utils;
import jetsennet.util.SafeDateFormater;
import jetsennet.util.TwoTuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 表和实体信息
 * 
 * @author 郭祥
 */
public class TableInfo
{

    /**
     * 数据库表名
     */
    private String tableName;
    /**
     * 对应的实体类型
     */
    private Class<?> cls;
    /**
     * 主键字段
     */
    private List<FieldInfo> keyFields;
    /**
     * 字段信息
     */
    private List<FieldInfo> fieldInfos;
    /**
     * 字段map。key：数据库列名；值：字段信息
     */
    private Map<String, FieldInfo> fieldMap = new HashMap<String, FieldInfo>();
    /**
     * 整合UORM
     */
    private String pattern;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TableInfo.class);

    public TableInfo(Class<?> cls, String tableName)
    {
        this.cls = cls;
        this.tableName = tableName;
        this.keyFields = new ArrayList<FieldInfo>(1);
        this.fieldInfos = new ArrayList<FieldInfo>(15);
    }

    public void key(String fieldName)
    {
        fieldName = fieldName.toUpperCase();
        for (FieldInfo fieldInfo : fieldInfos)
        {
            if (fieldInfo.getName().equals(fieldName) && !fieldInfo.isKey())
            {
                fieldInfo.setKey(true);
                this.keyFields.add(fieldInfo);
                break;
            }
        }
    }

    public FieldInfo field(FieldInfo field)
    {
        field.setTable(this);
        this.fieldInfos.add(field);
        if (field.isKey())
        {
            this.keyFields.add(field);
        }
        this.fieldMap.put(field.getName().toUpperCase(), field);
        return field;
    }

    public FieldInfo field(String fieldName, FieldTypeEnum type)
    {
        FieldInfo field = new FieldInfo(fieldName, type);
        return this.field(field);
    }

    public FieldInfo field(String fieldName, String type)
    {
        FieldInfo field = new FieldInfo(fieldName, FieldTypeEnum.valueOfIgnorCase(type));
        return this.field(field);
    }

    public final FieldInfo getFieldInfo(String fieldName)
    {
        return fieldMap.get(fieldName.toUpperCase());
    }

    public final FieldInfo getKey()
    {
        if (this.keyFields.size() <= 0)
        {
            throw new IllegalArgumentException("表不包含主键信息：" + this.tableName);
        }
        return this.keyFields.get(0);
    }

    /**
     * 重新排序主键字段
     * 
     * @param orders
     */
    public void reorderKeys(String[] orders)
    {
        if (orders == null || orders.length == 0)
        {
            return;
        }
        List<FieldInfo> newKeys = new ArrayList<FieldInfo>(orders.length);
        int size = orders.length;
        for (int i = 0; i < size; i++)
        {
            FieldInfo field = fieldMap.get(orders[i].toUpperCase());
            if (field == null || !field.isKey())
            {
                throw new UncheckedOrmException("字段不存在或不是主键：" + orders[i]);
            }
            newKeys.add(field);
        }
        if (newKeys.size() != keyFields.size())
        {
            throw new UncheckedOrmException("部分主键未参与排序：" + orders);
        }
        this.keyFields = newKeys;
    }

    /**
     * 将对象转换为ISql
     * 
     * @param obj
     * @param type
     * @return
     */
    public final ISql obj2Sql(Object obj, SqlTypeEnum type)
    {
        ISql sql = null;
        switch (type)
        {
        case INSERT:
            sql = this.obj2Insert(obj);
            break;
        case UPDATE:
            sql = this.obj2Update(obj);
            break;
        case DELETE:
            sql = this.obj2Delete(obj);
            break;
        case SELECT:
            throw new IllegalArgumentException("不能处理select类型语句生成");
        default:
            throw new IllegalArgumentException("sql类型未知：" + type);
        }
        return sql;
    }

    /**
     * 将map转换为ISql
     * 
     * @param obj
     * @param type
     * @return
     */
    public final ISql map2Sql(Map<String, Object> map, SqlTypeEnum type)
    {
        ISql sql = null;
        switch (type)
        {
        case INSERT:
            sql = this.map2Insert(map);
            break;
        case UPDATE:
            sql = this.map2Update(map);
            break;
        case DELETE:
            sql = this.map2Delete(map);
            break;
        case SELECT:
            throw new IllegalArgumentException("不能处理select类型语句生成");
        default:
            throw new IllegalArgumentException("sql类型未知：" + type);
        }
        return sql;
    }

    /**
     * 转换为insert语句
     * 
     * @param obj
     * @return
     */
    protected final InsertEntity obj2Insert(Object obj)
    {
        if (obj == null)
        {
            throw new NullPointerException();
        }
        InsertEntity retval = null;
        try
        {
            if (cls.isInstance(obj))
            {
                int length = fieldInfos.size();
                String[] keys = new String[length];
                Object[] values = new Object[length];
                for (int i = 0; i < length; i++)
                {
                    keys[i] = fieldInfos.get(i).getName();
                    values[i] = fieldInfos.get(i).get(obj);
                }
                retval = Sql.insert(this.tableName).columns(keys).values(values);
            }
            else
            {
                throw new IllegalArgumentException("错误的参数类型：" + obj.getClass());
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    /**
     * 转换为insert语句
     * 
     * @param obj
     * @return
     */
    protected final InsertEntity map2Insert(Map<String, Object> map)
    {
        if (map == null)
        {
            throw new NullPointerException();
        }
        InsertEntity retval = null;
        try
        {
            int length = fieldInfos.size();
            List<String> keys = new ArrayList<String>(length);
            List<Object> values = new ArrayList<Object>(length);
            for (int i = 0; i < length; i++)
            {
                FieldInfo tempFieldInfo = fieldInfos.get(i);
                String tempKeyName = tempFieldInfo.getName();
                Object tempKeyValue = this.paramHandle(map.get(tempKeyName), tempFieldInfo);
                // 类型为int/long/short时，如果传入值为null/空，不设置该值
                Class<?> cls = tempFieldInfo.getCls();
                if (tempKeyValue == null && Utils.isBasicType(cls))
                {
                    // 跳过该字段的添加
                }
                else
                {
                    keys.add(tempKeyName);
                    values.add(tempKeyValue);
                }
            }
            retval = Sql.insert(this.tableName).columns(keys.toArray(new String[0])).values(values.toArray(new Object[0]));
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    /**
     * 转换为update语句
     * 
     * @param obj
     * @return
     */
    protected final UpdateEntity obj2Update(Object obj)
    {
        if (obj == null)
        {
            throw new NullPointerException();
        }
        if (!cls.isInstance(obj))
        {
            throw new IllegalArgumentException("错误的参数类型：" + obj.getClass());
        }

        UpdateEntity retval = null;

        // 值信息
        int length = fieldInfos.size();
        int fieldLength = length - this.keyFields.size();
        String[] keys = new String[fieldLength];
        Object[] values = new Object[fieldLength];
        for (int i = 0, j = 0; i < length; i++)
        {
            if (!fieldInfos.get(i).isKey())
            {
                keys[j] = fieldInfos.get(i).getName();
                values[j] = fieldInfos.get(i).get(obj);
                j++;
            }
        }

        retval = Sql.update(this.tableName).columns(keys).values(values).where(this.genFilterFromObject(obj));
        return retval;
    }

    /**
     * 转换为update语句
     * 
     * @param obj
     * @return
     */
    protected final UpdateEntity map2Update(Map<String, Object> map)
    {
        if (map == null)
        {
            throw new NullPointerException();
        }

        // 主键信息
        FieldInfo keyField = getKey();
        String key = keyField.getName();
        Object value = this.paramHandle(map.get(key), keyField);
        if (value == null)
        {
            throw new IllegalArgumentException("map不包含主键信息，无法生成update语句");
        }

        // 值信息
        int length = fieldInfos.size();
        String[] keys = new String[length - 1];
        Object[] values = new Object[length - 1];
        for (int i = 0, j = 0; i < length; i++)
        {
            if (!fieldInfos.get(i).isKey())
            {
                keys[j] = fieldInfos.get(i).getName();
                values[j] = this.paramHandle(map.get(fieldInfos.get(i).getName()), fieldInfos.get(i));
                j++;
            }
        }

        UpdateEntity retval = Sql.update(this.tableName).columns(keys).values(values).where(eq(key, value));
        return retval;
    }

    /**
     * 转换为delete语句
     * 
     * @param obj
     * @return
     */
    protected final DeleteEntity obj2Delete(Object obj)
    {
        if (obj == null)
        {
            throw new NullPointerException();
        }
        return Sql.delete(this.tableName).where(genFilterFromObject(obj));
    }

    /**
     * 转换为delete语句
     * 
     * @param obj
     * @return
     */
    protected final DeleteEntity map2Delete(Map<String, Object> map)
    {
        if (map == null)
        {
            throw new NullPointerException();
        }

        // 主键信息
        FieldInfo keyField = getKey();
        String key = keyField.getName();
        Object value = this.paramHandle(map.get(key), keyField);
        if (value == null)
        {
            throw new IllegalArgumentException("map不包含主键信息，无法生成delete语句");
        }

        DeleteEntity retval = null;
        retval = Sql.delete(this.tableName).where(eq(key, value));
        return retval;
    }

    /**
     * 从对象生成过滤条件
     * 
     * @param obj
     * @return
     */
    public FilterNode genFilterFromObject(Object obj)
    {
        int keySize = keyFields.size();
        FilterNode fNode = null;
        for (int i = 0; i < keySize; i++)
        {
            String keyName = keyFields.get(i).getName();
            Object keyValue = keyFields.get(i).get(obj);
            if (keyValue == null)
            {
                throw new UncheckedOrmException("主键的值不存在：" + keyName);
            }
            keyValue = this.paramHandle(keyValue, keyFields.get(i));
            if (fNode == null)
            {
                fNode = eq(keyName, keyValue);
            }
            else
            {
                fNode = fNode.and(eq(keyName, keyValue));
            }
        }
        return fNode;
    }

    /**
     * 从Map生成过滤条件
     * 
     * @param obj
     * @return
     */
    public FilterNode genFilterFromMap(Class<?> clz, Map<String, Object> map)
    {
        int keySize = keyFields.size();
        FilterNode fNode = null;
        for (int i = 0; i < keySize; i++)
        {
            String keyName = keyFields.get(i).getName();
            Object keyValue = map.get(keyName);
            if (keyValue == null)
            {
                throw new UncheckedOrmException("主键的值不存在：" + keyName);
            }
            keyValue = this.paramHandle(keyValue, keyFields.get(i));
            if (fNode == null)
            {
                fNode = eq(keyName, keyValue);
            }
            else
            {
                fNode = fNode.and(eq(keyName, keyValue));
            }
        }
        return fNode;
    }

    /**
     * 生成用于preparedStatement的语句
     * 
     * @return
     */
    public final String preparedInsert()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(this.tableName).append("(");
        for (FieldInfo field : this.fieldInfos)
        {
            sb.append(field.getName()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")").append(" VALUES(");
        for (int i = 0; i < this.fieldInfos.size(); i++)
        {
            sb.append("?,");
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    public final TwoTuple<String, Map<String, Object>> preparedUpdateWithObj(boolean filterNull, Object obj)
    {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(this.tableName).append(" SET ");
        for (FieldInfo field : this.fieldInfos)
        {
            if (!field.isKey())
            {
                Object temp = field.get(obj);
                if (filterNull && temp == null)
                {
                    continue;
                }
                map.put(field.getName(), temp);
                sb.append(field.getName()).append("= ?, ");
            }
        }
        sb.setLength(sb.length() - 2);

        sb.append(" WHERE ");

        for (FieldInfo field : this.keyFields)
        {
            Object temp = field.get(obj);
            map.put(field.getName(), temp);
            sb.append(field.getName()).append(" = ? AND ");
            
        }
        sb.setLength(sb.length() - " AND ".length());
        return TwoTuple.gen(sb.toString(), map);
    }

    /**
     * 生成用于preparedStatement的语句
     * 
     * @return
     */
    public final String preparedQueryByPk()
    {
        if (this.keyFields.size() == 0)
        {
            throw new UncheckedOrmException("表不包含主键：" + this.tableName);
        }
        StringBuilder sb = new StringBuilder(100);
        sb.append("SELECT * FROM ").append(this.tableName).append(" WHERE ");

        for (FieldInfo field : this.keyFields)
        {
            sb.append(field.getName()).append(" = ? AND ");
        }
        sb.setLength(sb.length() - " AND ".length());
        return sb.toString();
    }

    /**
     * 生成用于preparedStatement的语句
     * 
     * @return
     */
    public final String preparedDeleteByPk()
    {
        if (this.keyFields.size() == 0)
        {
            throw new UncheckedOrmException("表不包含主键：" + this.tableName);
        }
        StringBuilder sb = new StringBuilder(100);
        sb.append("DELETE FROM ").append(this.tableName).append(" WHERE ");

        for (FieldInfo field : this.keyFields)
        {
            sb.append(field.getName()).append(" = ? AND ");
        }
        sb.setLength(sb.length() - " AND ".length());
        return sb.toString();
    }

    /**
     * 生成用于preparedStatement的语句
     * 
     * @return
     */
    public final String sqlQueryAll()
    {
        StringBuilder sb = new StringBuilder(30);
        sb.append("SELECT * FROM ").append(this.tableName);
        return sb.toString();
    }

    public final String sqlDeleteAll()
    {
        StringBuilder sb = new StringBuilder(30);
        sb.append("DELETE FROM ").append(this.tableName);
        return sb.toString();
    }

    public final Map<String, Object> obj2map(Object obj)
    {
        if (!this.cls.isInstance(obj))
        {
            throw new IllegalArgumentException();
        }
        Map<String, Object> retval = new LinkedHashMap<String, Object>(fieldInfos.size());
        for (FieldInfo field : fieldInfos)
        {
            retval.put(field.getName(), field.get(obj));
        }
        return retval;
    }

    /**
     * 将字段对应的值转换成合适的类型
     * 
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public final Object fieldValueTrans(String fieldName, Object fieldValue)
    {
        Object retval = fieldValue;
        FieldInfo field = fieldMap.get(fieldName);
        if (field != null)
        {
            retval = this.paramHandle(fieldValue, field);
        }
        return retval;
    }

    /**
     * 将字段对应的值转换成合适的类型
     * 
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public final List<Object> fieldValueListTrans(String fieldName, List<Object> fieldValues)
    {
        List<Object> retval = fieldValues;
        FieldInfo field = fieldMap.get(fieldName);
        if (field != null)
        {
            retval = new ArrayList<Object>(fieldValues.size());
            for (Object fieldValue : fieldValues)
            {
                Object temp = this.paramHandle(fieldValue, field);
                retval.add(temp);
            }
        }
        return retval;
    }

    /**
     * 将map里面的数据类型转换为合适的数据类型
     * 
     * @param obj
     * @return
     */
    public final Map<String, Object> mapDateTypeTrans(Map<String, Object> map)
    {
        if (map == null)
        {
            throw new NullPointerException();
        }
        try
        {
            int length = fieldInfos.size();
            for (int i = 0; i < length; i++)
            {
                String key = fieldInfos.get(i).getName();
                Object value = this.paramHandle(map.get(key), fieldInfos.get(i));
                if (value != null)
                {
                    map.put(key, value);
                }
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        return map;
    }

    /**
     * 将数据转换成合适的数据类型
     * 
     * @param obj 值
     * @param field 字段类型
     * @return
     */
    private Object paramHandle(Object obj, FieldInfo field)
    {
        if (obj == null)
        {
            return null;
        }
        Object retval = null;
        Class<?> cls = field.getCls();
        if (cls == int.class || cls == Integer.class)
        {
            if (obj == null || (obj instanceof String && ((String) obj).trim().equals("")))
            {
                retval = null;
            }
            else if (obj instanceof Integer)
            {
                retval = obj;
            }
            else
            {
                retval = Integer.valueOf(obj.toString());
            }
        }
        else if (cls == double.class || cls == Double.class)
        {
            if (obj == null || (obj instanceof String && ((String) obj).trim().equals("")))
            {
                retval = null;
            }
            else if (obj instanceof Double)
            {
                retval = obj;
            }
            else
            {
                retval = Double.valueOf(obj.toString());
            }
        }
        else if (cls == long.class || cls == Long.class)
        {
            if (obj == null || (obj instanceof String && ((String) obj).trim().equals("")))
            {
                retval = null;
            }
            else if (obj instanceof Long)
            {
                retval = obj;
            }
            else
            {
                retval = Long.valueOf(obj.toString());
            }
        }
        else if (cls == short.class || cls == Short.class)
        {
            if (obj == null || (obj instanceof String && ((String) obj).trim().equals("")))
            {
                retval = null;
            }
            else if (obj instanceof Short)
            {
                retval = obj;
            }
            else
            {
                retval = Short.valueOf(obj.toString());
            }
        }
        else if (cls == String.class)
        {
            retval = obj.toString();
        }
        else if (cls == Date.class)
        {
            if (obj instanceof String)
            {
                String str = obj.toString();
                retval = SafeDateFormater.parse(str);
            }
            else
            {
                retval = obj;
            }
        }
        else
        {
            throw new IllegalArgumentException("目前只至此int/Integer, long/Long, short/Short, double/Double, String, Date类型的数据，不支持数据类型：" + cls);
        }
        return retval;
    }

    @Override
    public int hashCode()
    {
        if (tableName != null)
        {
            return tableName.hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof TableInfo && this.tableName != null)
        {
            TableInfo temp = (TableInfo) obj;
            return this.tableName.equals(temp.tableName);
        }
        else
        {
            return super.equals(obj);
        }
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public Class<?> getCls()
    {
        return cls;
    }

    public void setCls(Class<?> cls)
    {
        this.cls = cls;
    }

    public List<FieldInfo> getFieldInfos()
    {
        return fieldInfos;
    }

    public List<FieldInfo> getKeyFields()
    {
        return keyFields;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

}
