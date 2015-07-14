package jetsennet.orm.session;

import static jetsennet.orm.sql.SqlTypeEnum.UPDATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.orm.executor.Executors;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.SimpleJsonParse;
import jetsennet.orm.transform.SimpleXmlParse;

public class SessionUpdate extends SessionInsert
{

    protected SessionUpdate(ITransactionManager transaction, SqlSessionFactory factory)
    {
        super(transaction, factory);
    }

    public int update(String sql)
    {
        return Executors.getSimpleExecutor().update(transaction, sql);
    }

    public int update(ISql sql)
    {
        return this.update(transform.trans(sql));
    }

    public int update(Object obj)
    {
        return this.update(this.tableInfoMgr.ensureTableInfo(obj.getClass()).obj2Sql(obj, UPDATE));
    }

    public int update(Map<String, Object> map, Class<?> cls)
    {
        return this.update(this.tableInfoMgr.ensureTableInfo(cls).map2Sql(map, UPDATE));
    }

    public int update(String sql, String tableName, Map<String, Object> param)
    {
        List<Map<String, Object>> params = new ArrayList<Map<String, Object>>(1);
        params.add(param);
        int[] temp = this.update(sql, tableName, params);
        if (temp == null && temp.length == 0)
        {
            return -1;
        }
        return temp[0];
    }

    public int[] updateMapList(List<Map<String, Object>> list, Class<?> cls)
    {
        int[] retval = null;
        if (list != null && list.size() > 0)
        {
            int size = list.size();
            String[] sqls = new String[size];
            for (int i = 0; i < size; i++)
            {
                Map<String, Object> map = list.get(i);
                sqls[i] = transform.trans(this.tableInfoMgr.ensureTableInfo(cls).map2Sql(map, UPDATE));
            }
            retval = Executors.getSimpleExecutor().update(transaction, sqls);
        }
        return retval;
    }

    public int[] updateJson(String json, Class<?> cls)
    {
        List<Map<String, Object>> list = SimpleJsonParse.parse(json);
        return this.updateMapList(list, cls);
    }

    public int[] updateXml(String xml, Class<?> cls)
    {
        List<Map<String, Object>> list = SimpleXmlParse.parse(xml);
        return this.updateMapList(list, cls);
    }
}
