package jetsennet.frame.dataaccess;

import java.sql.Types;

import jetsennet.orm.tableinfo.convert.SqlTypeConvert;

import org.apache.log4j.Logger;
import org.uorm.dao.common.SqlParameter;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.convert.TypeConvertException;

public class UormUtil
{

    private static final Logger logger = Logger.getLogger(UormUtil.class);

    /**
     * 将SqlParameter转换为适合PrepareStatement的值和类型
     * 
     * @param paramClass
     * @param params
     * @return
     * @throws Exception
     */
    public static final Object[] transSqlParameter(final Class<?> paramClass, SqlParameter... params) throws Exception
    {
        Object[] retval = new Object[params.length];
        for (int i = 0; i < params.length; i++)
        {
            SqlParameter param = params[i];
            Object val = param.getValue();

            FieldMapping mapping = ensureFieldMapping(paramClass, param);
            if (mapping != null)
            {
                val = SqlTypeConvert.convert(val, mapping.columnType());
            }
            else
            {
                // 默认转换为TIMESTAMP
                val = SqlTypeConvert.convert(val, Types.TIMESTAMP);
            }
            retval[i] = val;
        }
        return retval;
    }

    /**
     * 查找param对应的FieldMapping
     * 
     * @param paramClass
     * @param param
     * @return
     */
    public static FieldMapping ensureFieldMapping(final Class<?> paramClass, SqlParameter param)
    {
        FieldMapping retval = null;

//        // 确定字段对应的类描述
//        Map<String, FieldMapping> fieldMappings = null;
//        if (param.getOrmClass() != null)
//        {
//            fieldMappings = ObjectMappingCache.getInstance().getObjectFieldMap(param.getOrmClass());
//        }
//        else if (paramClass != null)
//        {
//            fieldMappings = ObjectMappingCache.getInstance().getObjectFieldMap(paramClass);
//        }
//
//        // 确定字段类型
//        String name = param.getName();
//        retval = fieldMappings.get(name);
//        if (retval == null)
//        {
//            for (FieldMapping fieldl : fieldMappings.values())
//            {
//                if (fieldl.columnName().equalsIgnoreCase(name))
//                {
//                    retval = fieldl;
//                    break;
//                }
//            }
//        }
        return retval;
    }

    public static Object trans2SqlValue(Object val, int sqlType) throws TypeConvertException
    {
        Object retval = val;
//        if (val != null)
//        {
//            Class<?> targetSqlCls = MappingUtil.getTargetSqlClass(sqlType);
//            // 部分数据需要转换类型
//            if (targetSqlCls != null)
//            {
//                Class<?> srcCls = val.getClass();
//                if (GenericConverterFactory.getInstance().needConvert(srcCls, targetSqlCls))
//                {
//                    ITypeConverter converter = GenericConverterFactory.getInstance().getSqlConverter();//.getConverter(srcCls, targetsqlcls);
//                    if (converter != null)
//                    {
//                        retval = converter.convert(val, targetSqlCls);
//                    }
//                }
//            }
//        }
        return retval;
    }

}
