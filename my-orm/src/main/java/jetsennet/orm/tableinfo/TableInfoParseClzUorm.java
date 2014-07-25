package jetsennet.orm.tableinfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import jetsennet.orm.executor.keygen.KeyGenEnum;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.annotation.KeyGenertator;

public class TableInfoParseClzUorm
{

    private static Map<String, KeyGenEnum> uormIdGenMap = null;

    static
    {
        uormIdGenMap = new HashMap<String, KeyGenEnum>(10);
        uormIdGenMap.put(KeyGenertator.IDENTITY, KeyGenEnum.ERROR);
        uormIdGenMap.put(KeyGenertator.UUID, KeyGenEnum.UUID);
        uormIdGenMap.put(KeyGenertator.UUIDHEX, KeyGenEnum.UUID);
        uormIdGenMap.put(KeyGenertator.GUID, KeyGenEnum.GUID);
        uormIdGenMap.put(KeyGenertator.INCREMENT, KeyGenEnum.INCRE);
        uormIdGenMap.put(KeyGenertator.SEQUENCE, KeyGenEnum.SEQ);
        uormIdGenMap.put(KeyGenertator.SELECT, KeyGenEnum.DB);
        uormIdGenMap.put(KeyGenertator.EFFICSELECT, KeyGenEnum.DB_BATCH);
        uormIdGenMap.put(KeyGenertator.NATIVE, KeyGenEnum.ERROR);
    }
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TableInfoParseClzUorm.class);

    public static final TableInfo parse(Class<?> cls)
    {
        TableInfo retval = null;
        try
        {
            // 解析实体信息
            ClassMapping clsInfo = ((ClassMapping) cls.getAnnotation(ClassMapping.class));
            retval = new TableInfo(cls, clsInfo.tableName());
            retval.setPattern(clsInfo.pattern());

            // 主键信息
            KeyGenEnum keyGenEnum = idGenTransFromUrom(clsInfo.keyGenerator());
            String keyGen = "";
            if (keyGenEnum == KeyGenEnum.NONE)
            {
                keyGen = clsInfo.keyGenerator();
            }

            // 主键排序信息
            String orderS = clsInfo.keyOrder();
            String[] orders = orderS.split(",");

            // 解析字段信息
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields)
            {
                if (f.isAnnotationPresent(FieldMapping.class))
                {
                    FieldMapping fieldInfo = (FieldMapping) f.getAnnotation(FieldMapping.class);
                    FieldInfo field = new FieldInfo(f, fieldInfo.columnName(), fieldInfo.primary(), keyGen, keyGenEnum, fieldInfo.columnType());
                    if (fieldInfo.primary())
                    {
                        field.setKeyEnum(keyGenEnum);
                    }
                    field.setSaveOpt(fieldInfo.includeInWrites());
                    field.setUpdateOpt(fieldInfo.includeInUpdate());
                    retval.field(field);
                }
            }

            retval.reorderKeys(orders);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    private static KeyGenEnum idGenTransFromUrom(String gen)
    {
        KeyGenEnum retval = uormIdGenMap.get(gen);
        if (retval == null)
        {
            retval = KeyGenEnum.NONE;
        }
        return retval;
    }
}
