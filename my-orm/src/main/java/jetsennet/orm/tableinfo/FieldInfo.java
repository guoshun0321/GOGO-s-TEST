package jetsennet.orm.tableinfo;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.executor.keygen.KeyGenEnum;
import jetsennet.orm.util.UncheckedOrmException;
import jetsennet.util.SafeDateFormater;

/**
 * 字段信息
 * 
 * @author 郭祥
 */
public class FieldInfo
{

    /**
     * 表信息
     */
    private TableInfo table;
    /**
     * 列名
     */
    private String name;
    /**
     * 类型
     */
    private FieldTypeEnum type;
    /**
     * SQL类型
     */
    private int sqlType;
    /**
     * 字段长度
     */
    private int length = -1;
    /**
     * 是否可以为空
     */
    private boolean isNullable = true;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否主键
     */
    private boolean isKey = false;
    /**
     * 实体中对应的字段
     */
    private Field field;
    /**
     * 实体中字段对应类型
     */
    private Class<?> cls;
    /**
     * 主键生成策略
     */
    private KeyGenEnum keyEnum;
    /**
     * 主键生成策略
     */
    private String keyGen;
    /**
     * 兼容uorm，是否在插入时使用
     */
    private boolean saveOpt = true;
    /**
     * 兼容uorm，在更新时起作用
     */
    private boolean updateOpt = true;
    /**
     * 序号
     */
    private int order;
    /**
     * 描述
     */
    private String desc;
    /**
     * 子字段
     */
    private List<FieldInfo> subs;
    /**
     * 父字段
     */
    private List<FieldInfo> parents;
    /**
     * 默认长度
     */
    private static final int DEFAULT_LENGTH = 200;

    /**
     * 构造函数，字段合法性的判断交给上层
     * @param field
     * @throws Exception 
     */
    public FieldInfo(Field field) throws Exception
    {
        Column col = field.getAnnotation(Column.class);
        this.name = col.value();
        if (TableInfoUtil.isId(field))
        {
            isKey = true;
            keyGen = TableInfoUtil.getKeygen(field);
            keyEnum = field.getAnnotation(Id.class).keyEnum();
        }
        this.field = field;
        this.field.setAccessible(true);
        this.cls = field.getType();
        boolean isText = col.isText();
        this.type = FieldTypeEnum.valueOf(this.cls, isText);
        if (this.type == null)
        {
            throw new UncheckedOrmException("暂时不支持类型：" + this.cls);
        }
        if (this.type == FieldTypeEnum.STRING)
        {
            this.length = DEFAULT_LENGTH;
        }
        this.sqlType = FieldTypeEnum.toSqlType(this.type);
    }

    /**
     * 构造函数，for uorm
     * @param field
     * @param name
     */
    public FieldInfo(Field field, String name, boolean isKey, String keyGen, KeyGenEnum keyEnum, int sqlType)
    {
        this.name = name;
        if (isKey)
        {
            this.isKey = true;
            this.keyGen = keyGen;
            this.keyEnum = keyEnum;
        }
        this.field = field;
        this.field.setAccessible(true);
        this.cls = field.getType();
        this.type = FieldTypeEnum.sqlType2FieldType(sqlType);
        if (this.type == FieldTypeEnum.STRING)
        {
            this.length = DEFAULT_LENGTH;
        }
        this.sqlType = sqlType;
    }

    /**
     * 构造函数，for cmp
     * @param name
     * @param cls
     * @param type
     * @param isKey
     * @param keyGen
     */
    public FieldInfo(String name, Class<?> cls, FieldTypeEnum type, boolean isKey, String keyGen)
    {
        this.name = name;
        if (isKey)
        {
            this.isKey = isKey;
            this.keyEnum = KeyGenEnum.NONE;
            this.keyGen = keyGen;
            if (keyGen == null || keyGen.trim().isEmpty())
            {
                this.keyEnum = KeyGenEnum.UUID;
                this.keyGen = "";
            }
        }
        this.field = null;
        this.cls = cls;
        this.type = type;
        if (this.type == FieldTypeEnum.STRING)
        {
            this.length = DEFAULT_LENGTH;
        }
        this.sqlType = FieldTypeEnum.toSqlType(this.type);
    }

    public FieldInfo(String name, String type)
    {
        this(name, FieldTypeEnum.valueOfIgnorCase(type));
    }

    public FieldInfo(String name, Class<?> cls, boolean isText)
    {
        this(name, cls, FieldTypeEnum.valueOf(cls, isText), false, null);
    }

    public FieldInfo(String name, FieldTypeEnum type)
    {
        this(name, FieldTypeEnum.ensureClz(type), type, false, null);
    }

    public FieldInfo length(int length)
    {
        this.length = length;
        return this;
    }

    public FieldInfo disNullable()
    {
        this.isNullable = false;
        return this;
    }

    public FieldInfo defaultValue(String defValue)
    {
        this.defaultValue = defValue;
        return this;
    }

    public FieldInfo key()
    {
        this.isKey = true;
        if (this.table != null && !this.table.getKeyFields().contains(this))
        {
            this.table.getKeyFields().add(this);
        }
        return this;
    }

    public void addSub(FieldInfo sub)
    {
        this.subs.add(sub);
    }

    public void addParent(FieldInfo parent)
    {
        this.parents.add(parent);
    }

    /**
     * get 方法
     * @param obj
     * @return
     */
    public final Object get(Object obj)
    {
        try
        {
            return field.get(obj);
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
    }

    /**
     * set方法
     * @param obj
     * @param param
     * @return
     */
    public final Object set(Object obj, Object param)
    {
        try
        {
            if (param != null)
            {
                field.set(obj, this.handleParam(param));
            }
            return field.get(obj);
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
    }

    /**
     * 将param转换成适合POJO的类型
     * 
     * @param param
     * @return
     */
    public Object handleParam(Object param)
    {
        Object retval = param;
        if (this.cls == int.class || this.cls == Integer.class)
        {
            retval = Integer.valueOf(param.toString());
        }
        else if (this.cls == long.class || this.cls == Long.class)
        {
            retval = Long.valueOf(param.toString());
        }
        else if (this.cls == double.class || this.cls == Double.class)
        {
            retval = Double.valueOf(param.toString());
        }
        else if (this.cls == float.class || this.cls == Float.class)
        {
            retval = Float.valueOf(param.toString());
        }
        else if (this.cls == short.class || this.cls == Short.class)
        {
            retval = Short.valueOf(param.toString());
        }
        else if (this.cls == String.class)
        {
            retval = param.toString();
        }
        else if (this.cls == Date.class)
        {
            if (param instanceof String)
            {
                retval = SafeDateFormater.parse((String) param);
            }
        }
        return retval;
    }

    public TableInfo getTable()
    {
        return table;
    }

    public void setTable(TableInfo table)
    {
        this.table = table;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public FieldTypeEnum getType()
    {
        return type;
    }

    public void setType(FieldTypeEnum type)
    {
        this.type = type;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public boolean isNullable()
    {
        return isNullable;
    }

    public void setNullable(boolean isNullable)
    {
        this.isNullable = isNullable;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public boolean isKey()
    {
        return isKey;
    }

    public void setKey(boolean isKey)
    {
        this.isKey = isKey;
    }

    public Field getField()
    {
        return field;
    }

    public void setField(Field field)
    {
        this.field = field;
    }

    public Class<?> getCls()
    {
        return cls;
    }

    public void setCls(Class<?> cls)
    {
        this.cls = cls;
    }

    public String getKeyGen()
    {
        return keyGen;
    }

    public void setKeyGen(String keyGen)
    {
        this.keyGen = keyGen;
    }

    public List<FieldInfo> getSubs()
    {
        return subs;
    }

    public void setSubs(List<FieldInfo> subs)
    {
        this.subs = subs;
    }

    public List<FieldInfo> getParents()
    {
        return parents;
    }

    public void setParents(List<FieldInfo> parents)
    {
        this.parents = parents;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public boolean isSaveOpt()
    {
        return saveOpt;
    }

    public void setSaveOpt(boolean saveOpt)
    {
        this.saveOpt = saveOpt;
    }

    public boolean isUpdateOpt()
    {
        return updateOpt;
    }

    public void setUpdateOpt(boolean updateOpt)
    {
        this.updateOpt = updateOpt;
    }

    public KeyGenEnum getKeyEnum()
    {
        return keyEnum;
    }

    public void setKeyEnum(KeyGenEnum keyEnum)
    {
        this.keyEnum = keyEnum;
    }

    public int getSqlType()
    {
        return sqlType;
    }

    public void setSqlType(int sqlType)
    {
        this.sqlType = sqlType;
    }

}
