package jetsennet.orm.session;

import static jetsennet.orm.sql.FilterUtil.eq;
import static jetsennet.orm.sql.FilterUtil.in;
import jetsennet.orm.executor.Executors;
import jetsennet.orm.sql.DeleteEntity;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.transaction.ITransactionManager;

public class SessionDelete extends SessionUpdate
{

    protected SessionDelete(ITransactionManager transaction, SqlSessionFactory factory)
    {
        super(transaction, factory);
    }

    public int delete(String sql)
    {
        return Executors.getSimpleExecutor().update(transaction, sql);
    }

    public int delete(ISql delete)
    {
        return Executors.getSimpleExecutor().update(transaction, transform.trans(delete));
    }

    public int deleteAll(String table)
    {
        return delete("DELETE FROM " + table);
    }

    public int deleteById(Class<?> cls, Object id)
    {
        TableInfo info = this.tableInfoMgr.ensureTableInfo(cls);
        DeleteEntity delete = Sql.delete(info.getTableName()).where(eq(info.getKey().getName(), id));
        return this.delete(delete);
    }

    public int deleteByIds(String tableName, Object... ids)
    {
        TableInfo info = this.tableInfoMgr.getTableInfo(tableName);
        DeleteEntity delete = Sql.delete(tableName).where(in(info.getKey().getName(), ids));
        return this.delete(delete);
    }

    public int deleteByObj(Object obj)
    {
        TableInfo info = this.tableInfoMgr.ensureTableInfo(obj.getClass());
        return this.delete(info.obj2Sql(obj, SqlTypeEnum.DELETE));
    }

}
