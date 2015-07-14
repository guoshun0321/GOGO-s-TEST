package jetsennet.orm.session;

import static jetsennet.orm.sql.SqlTypeEnum.INSERT;

import java.util.List;
import java.util.Map;

import jetsennet.orm.executor.keygen.KeyGen;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.SimpleJsonParse;
import jetsennet.orm.transform.SimpleXmlParse;

public class SessionInsert extends SessionBase
{

    protected SessionInsert(ITransactionManager transaction, SqlSessionFactory factory)
    {
        super(transaction, factory);
    }

    public int insert(String sql)
    {
        return exec.update(transaction, sql);
    }

    public int[] insert(String[] sqls)
    {
        return exec.update(transaction, sqls);
    }

    public int insert(ISql sql)
    {
        return exec.update(transaction, transform.trans(sql));
    }

    public int insert(Object obj)
    {
        return this.insert(obj, true);
    }

    public int insert(Object obj, boolean autoKey)
    {
        if (autoKey)
        {
            this.setKey(obj);
        }
        return this.insert(transform.trans(this.tableInfoMgr.ensureTableInfo(obj.getClass()).obj2Sql(obj, INSERT)));
    }

    public int insert(Map<String, Object> map, Class<?> cls)
    {
        return this.insert(map, cls, true);
    }

    public int insert(Map<String, Object> map, Class<?> cls, boolean autoKey)
    {
        if (autoKey)
        {
            this.setKey(map, cls);
        }
        return this.insert(this.tableInfoMgr.ensureTableInfo(cls).map2Sql(map, INSERT));
    }

    public int[] insertMapList(List<Map<String, Object>> list, Class<?> cls)
    {
        return this.insertMapList(list, cls, true);
    }

    public int[] insertMapList(List<Map<String, Object>> list, Class<?> cls, boolean autoKey)
    {
        int[] retval = null;
        if (list != null && list.size() > 0)
        {
            if (autoKey)
            {
                this.setKeyMap(list, cls);
            }
            if (list.size() == 1)
            {
                // 一条语句使用普通的方式插入
                retval = new int[1];
                retval[0] = this.insert(list.get(0), cls, autoKey);
            }
            else
            {
                // 多条语句采用prepared的方式插入
                TableInfo tableInfo = this.tableInfoMgr.ensureTableInfo(cls);
                String sql = this.tableInfoMgr.ensureTableInfo(cls).preparedInsert();
                list = transform.prepareInsertMap(tableInfo, list);
                retval = this.update(sql, tableInfo.getTableName(), list);
            }
        }
        return retval;
    }

    public int[] insertMapList(Map<String, Object>[] list, Class<?> cls, boolean autoKey)
    {
        int[] retval = null;

        if (list != null && list.length > 0)
        {
            if (autoKey)
            {
                this.setKeyMap(list, cls);
            }
            if (list.length == 1)
            {
                // 一条语句使用普通的方式插入
                retval = new int[1];
                retval[0] = this.insert(list[0], cls, autoKey);
            }
            else
            {
                // 多条语句采用prepared的方式插入
                TableInfo tableInfo = this.tableInfoMgr.ensureTableInfo(cls);
                String sql = this.tableInfoMgr.ensureTableInfo(cls).preparedInsert();
                retval = this.update(sql, tableInfo.getTableName(), list);
            }
        }
        return retval;
    }

    public void insertJson(String json, Class<?> cls)
    {
        this.insertJson(json, cls, true);
    }

    public int[] insertJson(String json, Class<?> cls, boolean autoKey)
    {
        List<Map<String, Object>> list = SimpleJsonParse.parse(json);
        return this.insertMapList(list, cls, autoKey);
    }

    public int[] insertXml(String xml, Class<?> cls)
    {
        return this.insertXml(xml, cls, true);
    }

    public int[] insertXml(String xml, Class<?> cls, boolean autoKey)
    {
        List<Map<String, Object>> list = SimpleXmlParse.parse(xml);
        return this.insertMapList(list, cls, autoKey);
    }

    public void insertObjList(List<Object> list, Class<?> cls)
    {
        this.insertObjList(list, cls, true);
    }

    public void insertObjList(List<Object> list, Class<?> cls, boolean autoKey)
    {
        if (autoKey)
        {
            this.setKey(list, cls);
        }
        if (list != null && list.size() > 0)
        {
            if (list.size() == 1)
            {
                // 一条语句使用普通的方式插入
                this.insert(list.get(0), autoKey);
            }
            else
            {
                // 多条语句采用prepared的方式插入
                List<Map<String, Object>> params = transform.prepareInsertObj(this.tableInfoMgr.ensureTableInfo(cls), list);
                this.insertMapList(params, cls, autoKey);
            }
        }
    }

    /**
     * 设置主键字段的值
     * 
     * @param obj
     * @param session
     */
    private void setKey(Object obj)
    {
        TableInfo table = this.tableInfoMgr.ensureTableInfo(obj.getClass());
        List<FieldInfo> keys = table.getKeyFields();
        for (FieldInfo key : keys)
        {
            if (key != null)
            {
                Object keyValue = KeyGen.genKey(table.getTableName(), key, this);
                table.getKey().set(obj, keyValue);
            }
        }
    }

    /**
     * 设置主键字段的值
     * 
     * @param obj
     * @param session
     */
    private void setKey(List<Object> objs, Class<?> cls)
    {
        TableInfo table = this.tableInfoMgr.ensureTableInfo(cls);
        List<FieldInfo> keys = table.getKeyFields();
        for (FieldInfo key : keys)
        {
            Object[] keyValues = KeyGen.genKey(table.getTableName(), key, this, objs.size());
            for (int i = 0; i < keyValues.length; i++)
            {
                table.getKey().set(objs.get(i), keyValues[i]);
            }
        }
    }

    /**
     * 设置主键字段的值
     * 
     * @param obj
     * @param session
     */
    private void setKey(Map<String, Object> map, Class<?> cls)
    {
        TableInfo table = this.tableInfoMgr.ensureTableInfo(cls);

        List<FieldInfo> keys = table.getKeyFields();
        for (FieldInfo key : keys)
        {
            String fieldName = key.getName();
            if (!map.containsKey(fieldName))
            {
                Object keyValue = KeyGen.genKey(table.getTableName(), key, this);
                map.put(fieldName, keyValue);
            }
        }
    }

    /**
     * 批量设置主键字段的值
     * 
     * @param obj
     * @param session
     */
    private void setKeyMap(List<Map<String, Object>> list, Class<?> cls)
    {
        TableInfo table = this.tableInfoMgr.ensureTableInfo(cls);

        for (Map<String, Object> map : list)
        {
            List<FieldInfo> keys = table.getKeyFields();
            for (FieldInfo key : keys)
            {
                String fieldName = key.getName();
                if (!map.containsKey(fieldName))
                {
                    Object keyValue = KeyGen.genKey(table.getTableName(), key, this);
                    map.put(fieldName, keyValue);
                }
            }
        }
    }

    /**
     * 批量设置主键字段的值。如果存在主键值，那么不设置该值。
     * 
     * @param obj
     * @param session
     */
    private void setKeyMap(Map<String, Object>[] list, Class<?> cls)
    {
        TableInfo table = this.tableInfoMgr.ensureTableInfo(cls);

        for (Map<String, Object> map : list)
        {
            List<FieldInfo> keys = table.getKeyFields();
            for (FieldInfo key : keys)
            {
                String fieldName = key.getName();
                if (!map.containsKey(fieldName))
                {
                    Object keyValue = KeyGen.genKey(table.getTableName(), key, this);
                    map.put(fieldName, keyValue);
                }
            }
        }

    }
}
