package jetsennet.orm.tableinfo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 表和实体信息管理
 */
public class TableInfoMgr
{

    /**
     * 表信息
     */
    private final ConcurrentMap<String, TableInfo> tableInfoMap = new ConcurrentHashMap<String, TableInfo>();
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TableInfoMgr.class);

    /**
     * 采用clz的方式注册表信息，不允许重复注册
     * 
     * @param cls 类名
     * @return 表格信息
     */
    public final TableInfo registerTableInfo(Class<?> cls)
    {
        TableInfo info = null;
        String tableName = TableInfoUtil.getTableName(cls);
        boolean isContain = tableInfoMap.containsKey(tableName);
        if (!isContain)
        {
            if (TableInfoUtil.isUorm(cls))
            {
                info = TableInfoParseClzUorm.parse(cls);
            }
            else if (TableInfoUtil.isDef(cls))
            {
                info = TableInfoParseClz.parse(cls);
            }
            TableInfo temp = tableInfoMap.putIfAbsent(tableName, info);
            if (temp != null)
            {
                info = temp;
            }
        }
        else
        {
            info = tableInfoMap.get(tableName);
        }
        return info;
    }

    /**
     * 采用xml的方式注册表信息。允许重复注册，后注册的数据会覆盖先注册的数据。
     * 
     * @param cls 类名
     * @return 表格信息
     */
    public final TableInfo registerTableInfo(String tableName, String xml)
    {

        TableInfo info = TableInfoParseXml.parse(xml);
        tableInfoMap.put(tableName, info);
        return info;
    }

    /**
     * 采用实体的方式添加表信息
     * 
     * @param cls 类名
     * @return 表格信息
     */
    public final TableInfo registerTableInfo(String tableName, TableInfo info)
    {
        tableInfoMap.put(tableName, info);
        return info;
    }

    /**
     * 通过反射获取表格实体信息，获取信息失败时，会抛出异常
     * 
     * @param cls
     * @return
     */
    public final TableInfo ensureTableInfo(Class<?> cls)
    {
        TableInfo info = registerTableInfo(cls);
        if (info == null)
        {
            throw new UncheckedOrmException("获取表格实体信息失败：" + cls);
        }
        return info;
    }

    /**
     * 获取数据表信息
     * 
     * @param tableName 表名
     * @return 表格信息
     */
    public final TableInfo getTableInfo(String tableName)
    {
        return tableInfoMap.get(tableName);
    }

    /**
     * 将对象转换为ISql
     * 
     * @param obj
     * @param type
     * @return
     */
    public final ISql obj2ISql(Object obj, SqlTypeEnum type)
    {
        return this.ensureTableInfo(obj.getClass()).obj2Sql(obj, type);
    }

}
