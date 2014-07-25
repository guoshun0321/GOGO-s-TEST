package jetsennet.orm.session;

import static jetsennet.orm.sql.FilterUtil.in;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.orm.executor.keygen.KeyGen;
import jetsennet.orm.executor.resultset.ResultSetHandleFactory;
import jetsennet.orm.executor.resultset.ResultSetHandleListMapStringString;
import jetsennet.orm.executor.resultset.ResultSetHandleListString;
import jetsennet.orm.executor.resultset.ResultSetHandleMapStringString;
import jetsennet.orm.sql.FilterNode;
import jetsennet.orm.sql.FilterUtil;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.sql.cascade.CascadeSqlDeleteEntity;
import jetsennet.orm.sql.cascade.CascadeSqlEntity;
import jetsennet.orm.sql.cascade.CascadeSqlInsertEntity;
import jetsennet.orm.sql.cascade.CascadeSqlSelectEntity;
import jetsennet.orm.sql.cascade.CascadeSqlUpdateEntity;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.ITransform2Sql;
import jetsennet.orm.util.UncheckedOrmException;

public class SessionCmp extends SessionQuery
{

    protected SessionCmp(ITransactionManager transaction, SqlSessionFactory factory)
    {
        super(transaction, factory);
    }

    private List<String> cscQueryAllKey(String table, String keyField, String relField, FilterNode filter)
    {
        List<String> retval = new ArrayList<String>();
        ISql sql = Sql.select(keyField).from(table).where(filter);
        List<String> subKeys = this.query(sql, new ResultSetHandleListString());
        while (!subKeys.isEmpty())
        {
            retval.addAll(subKeys);
            sql = Sql.select(keyField).from(table).where(in(relField, subKeys));
            subKeys = this.query(sql, new ResultSetHandleListString());
        }
        return retval;
    }

    /**
     * 批量操作
     * 
     * @param batch
     */
    public String csc(CascadeSqlEntity batch)
    {
        String retval = null;
        boolean isSelf = false;
        try
        {
            isSelf = this.transBegin();
            batch.marcoReplace();
            TableInfo tableInfo = batch.getTableInfo();
            if (batch instanceof CascadeSqlInsertEntity)
            {
                CascadeSqlInsertEntity insertB = (CascadeSqlInsertEntity) batch;
                if (insertB.isAutoKey())
                {
                    if (tableInfo.getKey() != null)
                    {
                        Object key = KeyGen.genKey(tableInfo.getTableName(), tableInfo.getKey(), this);
                        batch.addValue(tableInfo.getKey().getName(), key);
                    }
                    else
                    {
                        throw new UncheckedOrmException("表无主键：" + batch.getTableName());
                    }
                }
                retval = batch.getValueMap().get(tableInfo.getKey().getName()).toString();
                this.insert(insertB.genSql());
            }
            else if (batch instanceof CascadeSqlUpdateEntity)
            {
                CascadeSqlUpdateEntity updateB = (CascadeSqlUpdateEntity) batch;
                retval = batch.getValueMap().get(tableInfo.getKey().getName()).toString();
                this.update(updateB.genSql());
            }
            else if (batch instanceof CascadeSqlDeleteEntity)
            {
                CascadeSqlDeleteEntity deleteB = (CascadeSqlDeleteEntity) batch;
                String tableName = deleteB.getTableName();
                String keyName = deleteB.getTableInfo().getKey().getName();

                // 获取自循环的键
                String selLoop = deleteB.getSelfLoop();
                if (selLoop != null)
                {
                    List<String> keys = this.cscQueryAllKey(tableName, deleteB.getFilterField(), selLoop, deleteB.genFilter());
                    deleteB.addValue(keyName, keys);
                    deleteB.setFilterField(keyName);
                }
                else if (deleteB.isAffected())
                {
                    ISql selSql = deleteB.genAffectedSql();
                    if (selSql != null)
                    {
                        List<String> keys = this.query(selSql, ResultSetHandleFactory.getStringListHandle());
                        deleteB.addValue(keyName, keys);
                        deleteB.setFilterField(keyName);
                    }
                }
                ISql delSql = deleteB.genDeleteSql();
                if (delSql != null)
                {
                    this.update(delSql);
                }
            }
            else if (batch instanceof CascadeSqlEntity)
            {
                // ignore do nothing
            }
            else
            {
                throw new UncheckedOrmException("暂时不支持操作：" + batch.getType().name());
            }

            // 执行子SQL
            List<CascadeSqlEntity> subs = batch.getSubs();
            if (subs != null && !subs.isEmpty())
            {
                for (CascadeSqlEntity sub : subs)
                {
                    this.csc(sub);
                }
            }
            this.transCommit(isSelf);
        }
        catch (Exception ex)
        {
            this.transRollback(isSelf);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    /**
     * 级联查找
     * 
     * @param batch
     */
    public CscQueryResult cscQuery(CascadeSqlSelectEntity csc)
    {
        CscQueryResult retval = null;
        boolean isSelf = false;
        try
        {
            isSelf = this.transBegin();
            String tableName = csc.getTableInfo().getTableName();
            String keyName = csc.getTableInfo().getKey().getName();

            // 宏替换，生成ISQL
            csc.marcoReplace();
            ISql selSql = csc.genSql();
            // 查找
            Map<String, String> selList = this.query(selSql, new ResultSetHandleMapStringString());

            if (selList != null && !selList.isEmpty())
            {
                retval = new CscQueryResult();
                retval.setTableName(tableName);
                retval.setValues(selList);

                String keyValue = selList.get(keyName);
                csc.addValue(keyName, keyValue);

                // 执行子SQL
                if (selList.size() > 0)
                {
                    List<CascadeSqlEntity> subs = csc.getSubs();
                    if (subs != null && !subs.isEmpty())
                    {
                        for (CascadeSqlEntity sub : subs)
                        {
                            List<CscQueryResult> temp = this.cscSubQuery((CascadeSqlSelectEntity) sub);
                            if (temp != null && !temp.isEmpty())
                            {
                                retval.addSub(temp);
                            }
                        }
                    }
                }
            }
            this.transCommit(isSelf);
        }
        catch (Exception ex)
        {
            this.transRollback(isSelf);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    /**
     * 级联查找，子查找
     * 
     * @param csc
     * @return
     */
    private List<CscQueryResult> cscSubQuery(CascadeSqlSelectEntity csc)
    {
        List<CscQueryResult> retval = new ArrayList<CscQueryResult>();
        try
        {
            String tableName = csc.getTableInfo().getTableName();
            String keyName = csc.getTableInfo().getKey().getName();

            // 宏替换，生成ISQL
            csc.marcoReplace();
            ISql selSql = csc.genSql();
            // 查找
            List<Map<String, String>> selList = this.query(selSql, new ResultSetHandleListMapStringString());
            List<String> keyLst = this.getKeyList(keyName, selList);
            csc.addValue(keyName, keyLst);
            // 生成结果
            for (Map<String, String> sel : selList)
            {
                CscQueryResult temp = new CscQueryResult();
                temp.setTableName(tableName);
                temp.setValues(sel);
                retval.add(temp);
            }

            // 执行子SQL
            List<CascadeSqlEntity> subs = csc.getSubs();
            if (selList.size() > 0 && subs != null && !subs.isEmpty())
            {
                for (CascadeSqlEntity sub : subs)
                {
                    if (!(sub instanceof CascadeSqlSelectEntity))
                    {
                        continue;
                    }
                    List<CscQueryResult> subResults = this.cscSubQuery((CascadeSqlSelectEntity) sub);
                    for (CscQueryResult subResult : subResults)
                    {
                        String parentPriValue = subResult.getValues().get(((CascadeSqlSelectEntity) sub).getFilterField());
                        if (parentPriValue != null)
                        {
                            for (CscQueryResult pResult : retval)
                            {
                                if (pResult.getValues().get(keyName).equals(parentPriValue))
                                {
                                    pResult.addSub(subResult);
                                }
                            }
                        }
                    }
                }
            }

        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    /**
     * 自循环查找
     * 
     * @param csc
     */
    public CscQueryResult cscLoopQuery(CascadeSqlSelectEntity csc)
    {
        CscQueryResult retval = new CscQueryResult();
        boolean isSelf = false;
        try
        {
            isSelf = this.transBegin();
            String tableName = csc.getTableInfo().getTableName();
            String keyName = csc.getTableInfo().getKey().getName();

            // 宏替换，生成ISQL
            csc.marcoReplace();
            ISql selSql = csc.genSql();
            // 查找
            Map<String, String> selMap = this.query(selSql, new ResultSetHandleMapStringString());
            String keyValue = selMap.get(keyName);
            retval.setTableName(tableName);
            retval.setValues(selMap);
            // 自循环
            String selLoop = csc.getSelfLoop();
            if (selLoop != null)
            {
                ISql loopSql = this.genSelfLoopSql(csc.getTableInfo(), keyValue, selLoop);
                List<Map<String, String>> loopSelList = this.query(loopSql, new ResultSetHandleListMapStringString());
                while (!loopSelList.isEmpty())
                {
                    List<String> tempKeyLst = this.getKeyList(tableName, loopSelList);
                    for (Map<String, String> loopSel : loopSelList)
                    {
                        CscQueryResult temp = new CscQueryResult();
                        temp.setTableName(tableName);
                        temp.setValues(loopSel);
                        retval.addSub(temp);
                    }

                    loopSql = this.genSelfLoopSql(csc.getTableInfo(), tempKeyLst, selLoop);
                    loopSelList = this.query(loopSql, new ResultSetHandleListMapStringString());
                }
            }
            this.transCommit(isSelf);
        }
        catch (Exception ex)
        {
            this.transRollback(isSelf);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    private List<String> getKeyList(String key, List<Map<String, String>> selList)
    {
        List<String> retval = new ArrayList<String>();
        for (Map<String, String> sel : selList)
        {
            String temp = sel.get(key);
            retval.add(temp);
        }
        return retval;
    }

    private ISql genSelfLoopSql(TableInfo tableInfo, String keyValue, String sField)
    {
        return Sql.select("*").from(tableInfo.getTableName()).where(FilterUtil.eq(sField, keyValue));
    }

    private ISql genSelfLoopSql(TableInfo tableInfo, List<String> keyValueLst, String sField)
    {
        return Sql.select("*").from(tableInfo.getTableName()).where(FilterUtil.in(sField, keyValueLst));
    }

}
