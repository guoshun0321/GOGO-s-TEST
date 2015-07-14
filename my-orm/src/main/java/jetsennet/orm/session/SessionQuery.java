package jetsennet.orm.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.orm.executor.Executors;
import jetsennet.orm.executor.resultset.IResultSetHandle;
import jetsennet.orm.executor.resultset.ResultSetHandleFactory;
import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.executor.resultset.RowsResultSetJsonExtractor;
import jetsennet.orm.executor.resultset.RowsResultSetXmlExtractor;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.PageResult;
import jetsennet.orm.transform.PageSqlEntity;

import org.dom4j.Document;

public class SessionQuery extends SessionDelete
{

    protected SessionQuery(ITransactionManager transaction, SqlSessionFactory factory)
    {
        super(transaction, factory);
    }

    public <T> T query(String sql, IResultSetHandle<T> handle)
    {
        return Executors.getSimpleExecutor().query(transaction, sql, handle);
    }

    public <T> T query(ISql sql, IResultSetHandle<T> handle)
    {
        return this.query(transform.trans(sql), handle);
    }

    public Object querySingle(String sql)
    {
        return this.query(sql, RowsResultSetExtractor.gen(Object.class));
    }

    public <T> List<T> query(String sql, Class<T> cls)
    {
        return query(sql, RowsResultSetExtractor.gen(cls, this.getTableInfo(cls)));
    }

    /**
     * 查询并以JSON格式返回
     * 
     * @param sql
     * @return
     */
    public String queryJson(String sql)
    {
        return query(sql, RowsResultSetJsonExtractor.gen());
    }

    /**
     * 查询并以JSON格式返回
     * 
     * @param sql
     * @return
     */
    public String queryJson(ISql sql)
    {
        return query(sql, RowsResultSetJsonExtractor.gen());
    }

    /**
     * 查询并以XML格式返回
     * 
     * @param sql
     * @return
     */
    public String queryXml(String sql)
    {
        Document doc = query(sql, RowsResultSetXmlExtractor.gen("Records", "Record"));
        return doc.asXML();
    }

    /**
     * 查询并以XML格式返回
     * 
     * @param sql
     * @return
     */
    public String queryXml(ISql sql)
    {
        Document doc = query(sql, RowsResultSetXmlExtractor.gen("Records", "Record"));
        return doc.asXML();
    }

    public <T> T query(String sql, String tableName, Map<String, Object> objValMap, IResultSetHandle<T> handle)
    {
        List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>(1);
        return this.query(sql, tableName, objValMap, handle);
    }

    private PageResult page(ISql sql, int page, int pageSize)
    {
        PageSqlEntity ps = transform.pageSelect((SelectEntity) sql, page, pageSize);
        List<Integer> temp = this.query(ps.counterSql, RowsResultSetExtractor.gen(Integer.class));
        int count = 0;
        if (!temp.isEmpty())
        {
            count = temp.get(0);
        }

        return ps.querySql(count);
    }

    public String pageJson(ISql sql, int page, int pageSize)
    {
        PageResult pr = page(sql, page, pageSize);
        return query(pr.pageSql, ResultSetHandleFactory.getJsonHandle(pr.count, pr.cur));
    }

    public String pageXml(ISql sql, int page, int pageSize)
    {
        PageResult pr = page(sql, page, pageSize);
        return query(pr.pageSql, ResultSetHandleFactory.getXmlHandle(pr.count, pr.cur));
    }

}
