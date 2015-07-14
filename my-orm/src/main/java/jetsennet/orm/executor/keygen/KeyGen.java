package jetsennet.orm.executor.keygen;

import java.util.HashMap;
import java.util.Map;

import jetsennet.orm.session.SessionBase;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.util.UncheckedOrmException;

import org.uorm.utils.PropertyHolderUtil;

public class KeyGen
{
    /**
     * 默认主键生成器
     */
    public static final KeyGeneration KEY_GEN;
    /**
     * GUID主键生成器
     */
    public static final IKeyGeneration<?> KEY_GEN_GUID;
    /**
     * GUID方式，CMP项目遗留
     */
    public static final String KEYGEN_GUID = "GUID";

    public static final Map<KeyGenEnum, IKeyGeneration<?>> map = new HashMap<KeyGenEnum, IKeyGeneration<?>>();
    static
    {
        KEY_GEN = new KeyGeneration();
        String prop = PropertyHolderUtil.getProperty("ID.SelectGenerator.tablename");
        if (prop != null && prop.trim().length() > 0)
        {
            KEY_GEN.setDbTableName(prop);
        }
        prop = PropertyHolderUtil.getProperty("ID.SelectGenerator.namecolumn");
        if (prop != null && prop.trim().length() > 0)
        {
            KEY_GEN.setDbNameColumn(prop);
        }
        prop = PropertyHolderUtil.getProperty("ID.SelectGenerator.valuecolumn");
        if (prop != null && prop.trim().length() > 0)
        {
            KEY_GEN.setDbValueColumn(prop);
        }
        KEY_GEN_GUID = new KeyGenerationUuid();
        map.put(KeyGenEnum.ERROR, new KeyGenerationError());
        map.put(KeyGenEnum.DB, KEY_GEN);
        map.put(KeyGenEnum.DB_BATCH, new KeyGenerationEfficient());
        map.put(KeyGenEnum.GUID, new KeyGenerationGuid());
        map.put(KeyGenEnum.UUID, KEY_GEN_GUID);
        map.put(KeyGenEnum.INCRE, new KeyGenerationIncrement());
        map.put(KeyGenEnum.SEQ, new KeyGenerationSequence());
    }

    /**
     * 获取单个主键
     * 
     * @param tableName 表名
     * @param keyGenCls 主键生成策略
     * @param session session
     * @return
     */
    public static final Object genKey(String tableName, FieldInfo field, SessionBase session)
    {
        Object retval = null;
        IKeyGeneration<?> keyGen = ensureKeyGen(field.getKeyEnum(), field.getKeyGen());
        Object[] objs = keyGen.genKeys(session, tableName, field.getName(), 1);
        if (objs != null && objs.length > 0 && objs[0] != null)
        {
            retval = field.handleParam(objs[0]);
        }
        return retval;
    }

    /**
     * 获取多个主键
     * 
     * @param cls
     * @return
     */
    public static final Object[] genKey(String tableName, FieldInfo field, SessionBase session, int num)
    {
        Object[] retval = new Object[num];
        IKeyGeneration<?> keyGen = ensureKeyGen(field.getKeyEnum(), field.getKeyGen());
        Object[] objs = keyGen.genKeys(session, tableName, field.getName(), num);
        if (objs != null)
        {
            for (int i = 0; i < num; i++)
            {
                if (objs[i] != null)
                {
                    retval[i] = field.handleParam(objs[i]);
                }
            }
        }
        return retval;
    }

    /**
     * 确定主键生成策略
     * 
     * @param info
     * @return
     */
    private static final IKeyGeneration<?> ensureKeyGen(KeyGenEnum enumValue, String info)
    {
        IKeyGeneration<?> retval = null;
        if (enumValue == null || enumValue == KeyGenEnum.NONE)
        {
            if (info == null || info.isEmpty())
            {
                retval = KEY_GEN;
            }
            else
            {
                if (info.equalsIgnoreCase(KEYGEN_GUID))
                {
                    retval = KEY_GEN_GUID;
                }
                else
                {
                    try
                    {
                        retval = (IKeyGeneration<?>) Class.forName(info).newInstance();
                    }
                    catch (Exception ex)
                    {
                        throw new UncheckedOrmException(ex);
                    }
                }
            }
        }
        else
        {
            retval = map.get(enumValue);
        }
        return retval;
    }

}
