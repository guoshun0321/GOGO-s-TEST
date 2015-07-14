package jetsennet.frame.dataaccess;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.executor.resultset.IResultSetHandle;
import jetsennet.orm.executor.resultset.ResultSetExtractorAdapter;
import jetsennet.orm.executor.resultset.ResultSetHandleFactory;
import jetsennet.orm.executor.resultset.ResultSetHandleListMapStringString;
import jetsennet.orm.executor.resultset.RowArrayResultSetExtractor;
import jetsennet.orm.executor.resultset.RowMapResultSetExtractor;
import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.executor.resultset.RowsResultSetJsonExtractor;
import jetsennet.orm.executor.resultset.RowsResultSetXmlExtractor;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.transform.ITransform2Sql;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlParser;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.util.DBUtil;
import jetsennet.util.StringUtil;
import jetsennet.util.TwoTuple;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.uorm.dao.common.ConnectionCallback;
import org.uorm.dao.common.PaginationSupport;
import org.uorm.dao.common.ResultSetExtractor;
import org.uorm.dao.common.SqlParameter;
import org.uorm.dao.common.StatementCallback;

public class BaseDaoNew implements IDao
{

    /**
     * session工厂
     */
    private SqlSessionFactory factory;
    /**
     * SQL转换工具
     */
    private ITransform2Sql trans;
    /**
     * 兼容旧jetsenlib的解析器
     */
    private ISqlParser parser;
    /**
     * 是否自动事务
     */
    private boolean isAutoTransaction;

    private static final String XML_ROOT_NAME = "Records";

    private static final String XML_ITEM_NAME = "Record";

    private static final int DISABLE_PAGE = -1;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BaseDaoNew.class);

    public BaseDaoNew(Configuration config)
    {
        factory = SqlSessionFactoryBuilder.builder(config);
        trans = factory.getTransform();
        parser = SqlClientObjFactory.createSqlParser(factory.getConfig().connInfo.driver);
    }

    private Session getSession()
    {
        return factory.openSession();
    }

    @Override
    public void beginTransation() throws SQLException
    {
        try
        {
            Session session = getSession();
            session.transBegin();
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
    }

    @Override
    public void commitTransation() throws SQLException
    {
        try
        {
            Session session = getSession();
            session.transCommit(true);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
    }

    @Override
    public void rollbackTransation() throws SQLException
    {
        try
        {
            Session session = getSession();
            session.transRollback(true);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
    }

    @Override
    public boolean isAutoManagerTransaction()
    {
        return this.isAutoTransaction;
    }

    @Override
    public void setAutoManagerTransaction(boolean autoManagerTransaction)
    {
        this.isAutoTransaction = autoManagerTransaction;
    }

    @Override
    public Document fill(String sql, SqlParameter... params) throws SQLException
    {
        return fill(sql, XML_ROOT_NAME, XML_ITEM_NAME, DISABLE_PAGE, DISABLE_PAGE, params);
    }

    @Override
    public Document fill(String sql, String rootName, String itemName, SqlParameter... params) throws SQLException
    {
        return fill(sql, rootName, itemName, DISABLE_PAGE, DISABLE_PAGE, params);
    }

    @Override
    public Document fill(String sql, int startRecord, int maxRecord, SqlParameter... params) throws SQLException
    {
        return fill(sql, XML_ROOT_NAME, XML_ITEM_NAME, startRecord, maxRecord, params);
    }

    @Override
    public Document fill(String sql, String rootName, String itemName, int startRecord, int maxRecord, SqlParameter... params) throws SQLException
    {
        Document retval = null;
        try
        {
            retval = this.fillDoc(sql, rootName, itemName, startRecord, maxRecord, params);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public Document fillByPagedQuery(String sql, int startPage, int pageSize, SqlParameter... params) throws SQLException
    {
        return this.fillByPagedQuery(sql, XML_ROOT_NAME, XML_ITEM_NAME, startPage, pageSize, params);
    }

    @Override
    public Document fillByPagedQuery(String countSql, String sql, int startPage, int pageSize, SqlParameter... params) throws SQLException
    {
        return this.fillByPagedQuery(countSql, sql, XML_ROOT_NAME, XML_ITEM_NAME, startPage, pageSize, params);
    }

    @Override
    public Document fillByPagedQuery(String sql, String rootName, String itemName, int startPage, int pageSize, SqlParameter... params)
            throws SQLException
    {
        String countSql = trans.getCountSql(sql);
        return this.fillByPagedQuery(countSql, sql, rootName, itemName, startPage, pageSize, params);
    }

    @Override
    public Document fillByPagedQuery(String countSql, String sql, String rootName, String itemName, int startPage, int pageSize,
            SqlParameter... params) throws SQLException
    {
        boolean isPage = true;
        if (startPage < 0 || pageSize < 0)
        {
            isPage = false;
        }

        boolean isParam = true;
        Object[] paramValues = null;
        Document retval = null;
        try
        {
            int startRecord = -1;
            if (isPage)
            {
                Session session = this.getSession();
                if (params == null || params.length == 0)
                {
                    isParam = false;
                }
                else
                {
                    paramValues = UormUtil.transSqlParameter(null, params);
                }
                // 分页信息
                int count = -1;
                if (!isParam)
                {
                    count = (Integer) session.querySingle(countSql);
                }
                else
                {
                    List<Integer> tmp = session.query(countSql, paramValues, new RowsResultSetExtractor<Integer>(Integer.class));
                    count = tmp.get(0);
                }
                int pageCount = (int) Math.ceil((double) count / (double) pageSize);
                int curPage = startPage;
                if (curPage >= pageCount)
                {
                    curPage = pageCount - 1;
                }
                startRecord = curPage >= 0 ? curPage * pageSize : 0;
            }
            retval = this.fillDoc(sql, rootName, itemName, startRecord, pageSize, params);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    /**
     * 查询，返回Document
     * 
     * @param sql 查询SQL
     * @param rootName xml根节点名称
     * @param itemName xml记录节点名称
     * @param startRecord 开始记录
     * @param maxRecord 最大记录条数
     * @param params SQL参数
     * @return
     * @throws SQLException
     */
    private Document fillDoc(String sql, String rootName, String itemName, int startRecord, int maxRecord, SqlParameter... params)
            throws SQLException
    {
        boolean isPage = true;
        if (startRecord < 0 || maxRecord < 0)
        {
            isPage = false;
        }

        boolean isParam = true;
        Object[] paramValues = null;
        Document retval = null;
        try
        {
            if (params == null || params.length == 0)
            {
                isParam = false;
            }
            else
            {
                paramValues = UormUtil.transSqlParameter(null, params);
            }

            Session session = this.getSession();
            RowsResultSetXmlExtractor rsHandle = RowsResultSetXmlExtractor.gen(rootName, itemName);

            if (isPage)
            {
                // 分页信息
                if (trans.supportsOffset())
                {
                    sql = trans.getLimitString(sql, startRecord, maxRecord);
                }
                else
                {
                    int endRecord = startRecord + maxRecord;
                    sql = trans.getLimitString(sql, 0, endRecord);
                    rsHandle.setOffset(startRecord);
                }
            }

            if (!isParam)
            {
                retval = session.query(sql, rsHandle);
            }
            else
            {

                retval = session.query(sql, paramValues, rsHandle);
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public String fillJson(String sql, SqlParameter... params) throws SQLException
    {
        return query(sql, new RowsResultSetJsonExtractor(), params);
    }

    @Override
    public String fillJson(String sql, int startRecord, int maxRecord, SqlParameter... params) throws SQLException
    {
        String items = null;
        String sqlforLimit = null;
        RowsResultSetJsonExtractor rse = new RowsResultSetJsonExtractor();
        if (trans.supportsOffset())
        {
            sqlforLimit = trans.getLimitString(sql, startRecord, maxRecord);
        }
        else
        {
            sqlforLimit = trans.getLimitString(sql, 0, startRecord + maxRecord);
            rse.setOffset(startRecord);
        }
        items = query(sqlforLimit, rse, params);
        return items;
    }

    @Override
    public String fillJsonByPagedQuery(String sql, int startPage, int pageSize, SqlParameter... params) throws SQLException
    {
        String countSql = trans.getCountSql(sql);
        return this.fillJsonByPagedQuery(countSql, sql, startPage, pageSize, params);
    }

    @Override
    public String fillJsonByPagedQuery(String countSql, String sql, int startPage, int pageSize, SqlParameter... params) throws SQLException
    {
        Long totalCountL = queryForObject(Long.class, countSql, params);
        if (totalCountL != null)
        {
            long totalCount = totalCountL.longValue();
            int pagecount = (int) Math.ceil((double) totalCount / (double) pageSize);
            int curpage = startPage;
            if (curpage >= pagecount)
                curpage = pagecount - 1;
            int startRecord = curpage >= 0 ? curpage * pageSize : 0;
            StringBuilder items = new StringBuilder();
            if (0 == totalCount)
            {
                items.append("{\"total\":0,\"rows\":[]}");
            }
            else
            {
                items.append("{\"total\":").append(totalCount).append(",\"rows\":");

                RowsResultSetJsonExtractor rse = new RowsResultSetJsonExtractor();
                String sqlforLimit = null;
                if (trans.supportsOffset())
                {
                    sqlforLimit = trans.getLimitString(sql, startRecord, pageSize);
                    items.append(query(sqlforLimit, rse, params));
                }
                else
                {
                    int endRecord = (curpage + 1) * pageSize;
                    if (endRecord > totalCount)
                    {
                        endRecord = (int) totalCount;
                    }
                    sqlforLimit = trans.getLimitString(sql, 0, endRecord);
                    rse.setOffset(startRecord);
                    items.append(query(sqlforLimit, rse, params));
                }
                items.append('}');
            }
            return items.toString();
        }
        return null;
    }

    @Override
    public <T> T queryBusinessObjByPk(Class<T> cls, Serializable... pkVals) throws SQLException
    {
        T retval = null;
        try
        {
            Session session = getSession();
            TableInfo table = session.getTableInfo(cls);
            String sql = table.preparedQueryByPk();
            List<FieldInfo> fields = table.getKeyFields();
            SqlParameter[] params = new SqlParameter[pkVals.length];

            int fieldSize = fields.size();
            if (fieldSize != params.length)
            {
                throw new SQLException("主键和主键参数的数量不吻合。");
            }
            for (int i = 0; i < fieldSize; i++)
            {
                FieldInfo field = fields.get(i);
                params[i] = new SqlParameter(field.getName(), pkVals[i]);
            }
            retval = queryForObject(cls, sql, params);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    /**
     * 查询返回单个对象
     * 
     * @param <T>
     * @param cls 对象类型
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public <T> T queryForObject(Class<T> cls, String query, final SqlParameter... params) throws SQLException
    {
        List<T> results = this.queryBusinessObjs(cls, 1, query, params);
        if (results != null && results.size() > 0)
        {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> queryForListMap(String sql, SqlParameter... params) throws SQLException
    {
        return query(sql, new RowMapResultSetExtractor(), params);
    }

    @Override
    public List<Object[]> queryForListArray(String sql, SqlParameter... params) throws SQLException
    {
        return query(sql, new RowArrayResultSetExtractor(), params);
    }

    /**
     * TODO 未测试
     */
    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse, SqlParameter... params) throws SQLException
    {
        return (T) query(sql, new ResultSetExtractorAdapter(rse), params);
    }

    private <T> T query(String sql, IResultSetHandle<T> rse, SqlParameter... params) throws SQLException
    {
        T retval = null;
        try
        {
            Session session = getSession();
            if (params == null || params.length == 0)
            {
                retval = session.query(sql, rse);
            }
            else
            {
                Object[] values = UormUtil.transSqlParameter(null, params);
                retval = session.query(sql, values, rse);
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public <T> T querySingleObject(Class<T> cls, String sql, SqlParameter... params) throws SQLException
    {
        return queryForObject(cls, sql, params);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, SqlParameter... params) throws SQLException
    {
        List<Map<String, Object>> maps = query(sql, new RowMapResultSetExtractor(1), params);
        if (maps.size() > 0)
        {
            return maps.get(0);
        }
        return null;
    }

    @Override
    public Object[] queryForArray(String sql, SqlParameter... params) throws SQLException
    {
        List<Object[]> lst = query(sql, new RowArrayResultSetExtractor(1), params);
        if (lst.size() > 0)
        {
            return lst.get(0);
        }
        return null;
    }

    @Override
    public <T> List<T> queryBusinessObjs(Class<T> cls, String sql, int startRecord, int maxRecord, SqlParameter... params) throws SQLException
    {
        List<T> items = null;
        String sqlforLimit = null;
        if (trans.supportsOffset())
        {
            sqlforLimit = trans.getLimitString(sql, startRecord, maxRecord);
            items = queryBusinessObjs(cls, sqlforLimit, params);
        }
        else
        {
            sqlforLimit = trans.getLimitString(sql, 0, startRecord + maxRecord);
            items = queryBusinessObjs(cls, startRecord, maxRecord, sqlforLimit, params);
        }
        return items;
    }

    @Override
    public <T> List<T> queryBusinessObjs(Class<T> cls, String query, SqlParameter... params) throws SQLException
    {
        return this.queryBusinessObjs(cls, 0, Integer.MAX_VALUE, query, params);
    }

    public <T> List<T> queryBusinessObjs(Class<T> cls, int max, String query, SqlParameter... params) throws SQLException
    {
        return queryBusinessObjs(cls, 0, max, query, params);
    }

    public <T> List<T> queryBusinessObjs(Class<T> cls, int offset, int max, String query, SqlParameter... params) throws SQLException
    {
        TableInfo info = factory.tableInfoWithoutBasic(cls);
        IResultSetHandle<List<T>> extractor = new RowsResultSetExtractor<T>(cls, info);
        extractor.setMax(max);
        extractor.setOffset(offset);
        List<T> results = null;
        if (params == null || params.length == 0)
        {
            results = query(query, extractor);
        }
        else
        {
            results = query(query, extractor, params);
        }
        return results;
    }

    @Override
    public <T> List<T> queryAllBusinessObjs(Class<T> cls) throws SQLException
    {
        String sql = factory.getTableInfo(cls).sqlQueryAll();
        return this.queryBusinessObjs(cls, sql);
    }

    @Override
    public <T> PaginationSupport<T> queryByPagedQuery(Class<T> cls, String sql, int startPage, int pageSize, SqlParameter... params)
            throws SQLException
    {
        String countSql = trans.getCountSql(sql);
        return queryByPagedQuery(cls, countSql, sql, startPage, pageSize, params);
    }

    @Override
    public <T> PaginationSupport<T> queryByPagedQuery(Class<T> cls, String countSql, String sql, int startPage, int pageSize, SqlParameter... params)
            throws SQLException
    {
        PaginationSupport<T> retval = null;
        Long totalCountL = queryForObject(Long.class, countSql, params);
        if (totalCountL != null)
        {
            long totalCount = totalCountL.longValue();
            int pageCount = (int) Math.ceil((double) totalCount / (double) pageSize);
            int curPage = startPage >= pageCount ? pageCount - 1 : startPage;
            int startRecord = curPage >= 0 ? curPage * pageSize : 0;
            List<T> items = null;
            if (0 == totalCount)
            {
                items = new ArrayList<T>(0);
            }
            else
            {
                items = queryBusinessObjs(cls, sql, startRecord, pageSize, params);
            }
            retval = new PaginationSupport<T>(pageSize, totalCount, curPage, items);
        }
        return retval;
    }

    @Override
    public int saveModelData(Class<?> cls, Map<String, Object>[] models) throws SQLException
    {
        int retval = -1;
        try
        {
            Session session = getSession();
            retval = session.insertMapList(models, cls, true).length;
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int saveModelData(Class<?> cls, Map<String, Object> model) throws SQLException
    {
        int retval = -1;
        try
        {
            Session session = getSession();
            retval = session.insert(model, cls);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int saveModelDataCol(Class<?> cls, Collection<Map<String, Object>> models) throws SQLException
    {
        int retval = -1;
        try
        {
            Session session = getSession();
            retval = session.insertMapList(new ArrayList<Map<String, Object>>(models), cls).length;
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int saveBusinessObjs(Object... pojos) throws SQLException
    {
        int retval = 0;
        if (pojos == null || pojos.length == 0)
        {
            return retval;
        }
        try
        {
            Session session = getSession();
            for (Object pojo : pojos)
            {
                TableInfo info = session.getTableInfo(pojo.getClass());
                int temp = session.update(info.obj2Sql(pojo, SqlTypeEnum.INSERT));
                retval += temp;
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int saveBusinessObjsCol(Collection<? extends Serializable> pojos) throws SQLException
    {
        return this.saveBusinessObjs(pojos.toArray());
    }

    @Override
    public int deleteBusiness(Class<?> cls, Serializable... pkVals) throws SQLException
    {
        int retval = 0;
        if (pkVals == null || pkVals.length == 0)
        {
            return retval;
        }
        try
        {
            Session session = getSession();
            TableInfo tableInfo = session.getTableInfo(cls);
            String sql = tableInfo.preparedDeleteByPk();

            Map<String, Object> params = new HashMap<String, Object>();
            List<FieldInfo> fields = tableInfo.getKeyFields();
            int fieldSize = fields.size();
            for (int i = 0; i < fieldSize; i++)
            {
                params.put(fields.get(i).getName(), pkVals[i]);
            }
            session.update(sql, tableInfo.getTableName(), params);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }

        return 0;
    }

    @Override
    public int deleteBusiness(Object... pojos) throws SQLException
    {
        int retval = 0;
        if (pojos == null || pojos.length == 0)
        {
            return retval;
        }
        try
        {
            Session session = getSession();
            for (Object pojo : pojos)
            {
                TableInfo info = session.getTableInfo(pojo.getClass());
                int temp = session.delete(info.obj2Sql(pojo, SqlTypeEnum.DELETE));
                retval += temp;
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int deleteBusinessCol(Collection<? extends Serializable> pojos) throws SQLException
    {
        return this.deleteBusiness(pojos.toArray());
    }

    @Override
    public int updateBusinessObjs(boolean isFilterNull, Object... pojos) throws SQLException
    {
        int retval = 0;
        if (pojos == null)
        {
            return retval;
        }
        try
        {
            Session session = getSession();
            for (Object pojo : pojos)
            {
                TableInfo info = factory.getTableInfo(pojo.getClass());
                TwoTuple<String, Map<String, Object>> temp = info.preparedUpdateWithObj(isFilterNull, pojo);
                retval += session.update(temp.first, info.getTableName(), temp.second);
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int updateBusinessObjsCol(boolean isFilterNull, Collection<? extends Serializable> pojos) throws SQLException
    {
        int retval = 0;
        if (pojos == null)
        {
            return retval;
        }
        try
        {
            Session session = getSession();
            for (Object pojo : pojos)
            {
                TableInfo info = factory.getTableInfo(pojo.getClass());
                TwoTuple<String, Map<String, Object>> temp = info.preparedUpdateWithObj(isFilterNull, pojo);
                retval += session.update(temp.first, info.getTableName(), temp.second);
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int update(String sql, SqlParameter... params) throws SQLException
    {
        int retval = 0;
        try
        {
            Object[] values = UormUtil.transSqlParameter(null, params);
            Session session = getSession();
            retval += session.update(sql, values);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public int[] batchUpdate(String[] sqls) throws SQLException
    {
        int[] retval = null;
        try
        {
            Session session = getSession();
            retval = session.update(sqls);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    @Override
    public boolean execute(String sql, SqlParameter... params) throws SQLException
    {
        boolean retval = false;
        try
        {
            Session session = getSession();
            Object[] values = UormUtil.transSqlParameter(null, params);
            retval = session.executor(sql, values);
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    /**
     * TODO 未测试
     */
    @Override
    public <T> T execute(ConnectionCallback<T> action) throws SQLException
    {
        T retval = null;
        Session session = getSession();
        boolean isOpen = session.isConnectionOpen();
        try
        {
            if (!isOpen)
            {
                session.openConnection();
            }
            retval = action.doInConnection(session.openConnection());
            if (!isOpen)
            {
                session.closeConnection();
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return retval;
    }

    /**
     * TODO 未测试
     */
    @Override
    public <T> T execute(StatementCallback<T> action) throws SQLException
    {
        T retval = null;
        Session session = getSession();
        Statement stm = null;
        boolean isOpen = session.isConnectionOpen();
        try
        {
            if (!isOpen)
            {
                session.openConnection();
            }
            stm = session.openConnection().createStatement();
            retval = action.doInStatement(stm);
            if (!isOpen)
            {
                session.closeConnection();
            }
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        finally
        {
            jetsennet.orm.util.DBUtil.closeStatement(stm);
        }
        return retval;
    }

    @Override
    public String getDriverClass()
    {
        return factory.getConfig().connInfo.driver;
    }

    @Override
    public ISqlParser getSqlParser()
    {
        return this.parser;
    }

    @Override
    public <X> X get(Class<X> c, SqlCondition... conds) throws Exception
    {
        return this.get(c, null, conds);
    }

    @Override
    public <X> X get(Class<X> c, String order, SqlCondition... conds) throws Exception
    {
        X retval = null;
        try
        {
            Session session = getSession();
            TableInfo info = session.getTableInfo(c);
            String sql = parser.getSelectCommandString(info.getTableName(), "*", order, conds);
            RowsResultSetExtractor<X> rsHandle = RowsResultSetExtractor.gen(c, info);
            rsHandle.setMax(1);
            List<X> temp = session.query(sql, rsHandle);
            if (!temp.isEmpty())
            {
                retval = temp.get(0);
            }
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        }
        return retval;
    }

    @Override
    public Map<String, Object> getMap(Class<?> c, SqlCondition... conds) throws Exception
    {
        return this.getMap(c, "*", null, conds);
    }

    @Override
    public Map<String, Object> getMap(Class<?> c, String order, SqlCondition... conds) throws Exception
    {
        return this.getMap(c, "*", order, conds);
    }

    @Override
    public Map<String, Object> getMap(Class<?> c, String fields, String order, SqlCondition... conds) throws Exception
    {
        Map<String, Object> retval = null;
        try
        {
            Session session = getSession();
            TableInfo info = session.getTableInfo(c);
            String sql = parser.getSelectCommandString(info.getTableName(), fields, order, conds);
            RowMapResultSetExtractor rsHandle = RowMapResultSetExtractor.gen();
            rsHandle.setMax(1);
            List<Map<String, Object>> temp = session.query(sql, rsHandle);
            if (!temp.isEmpty())
            {
                retval = temp.get(0);
            }
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        }
        return retval;
    }

    @Override
    public Map<String, String> getStrMap(Class<?> c, SqlCondition... conds) throws Exception
    {
        return this.getStrMap(c, "*", null, conds);
    }

    @Override
    public Map<String, String> getStrMap(Class<?> c, String order, SqlCondition... conds) throws Exception
    {
        return this.getStrMap(c, "*", order, conds);
    }

    @Override
    public Map<String, String> getStrMap(Class<?> c, String fields, String order, SqlCondition... conds) throws Exception
    {
        Map<String, String> retval = null;
        try
        {
            Session session = getSession();
            TableInfo info = session.getTableInfo(c);
            String sql = parser.getSelectCommandString(info.getTableName(), fields, order, conds);
            retval = session.query(sql, ResultSetHandleFactory.getMapStringStringHandle());
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        }
        return retval;
    }

    @Override
    public Map<String, String> getStrMap(String sql, SqlParameter... params) throws SQLException
    {
        List<Map<String, String>> results = query(sql, new ResultSetHandleListMapStringString(1), params);
        if (results != null && results.size() > 0)
        {
            return results.get(0);
        }
        return null;
    }

    @Override
    public <X> X getFirst(Class<X> retCla, Class<?> c, String field, SqlCondition... conds) throws Exception
    {
        return this.getFirst(retCla, c, field, null, conds);
    }

    @Override
    public <X> X getFirst(Class<X> retCla, Class<?> c, String field, String order, SqlCondition... conds) throws Exception
    {
        String tableName = getTableName(c);
        String sql = getSqlParser().getSelectCommandString(tableName, field, order, conds);
        return querySingleObject(retCla, sql);
    }

    @Override
    public <X> List<X> getLst(Class<X> c, SqlCondition... conds) throws Exception
    {
        return this.getLst(c, null, conds);
    }

    @Override
    public <X> List<X> getLst(Class<X> c, String order, SqlCondition... conds) throws Exception
    {
        String tableName = getTableName(c);
        String sql = getSqlParser().getSelectCommandString(tableName, null, order, conds);
        return queryBusinessObjs(c, sql);
    }

    @Override
    public <X> List<Map<String, Object>> getMapLst(Class<X> c, SqlCondition... conds) throws Exception
    {
        return this.getMapLst(c, "*", null, conds);
    }

    @Override
    public <X> List<Map<String, Object>> getMapLst(Class<X> c, String order, SqlCondition... conds) throws Exception
    {
        return this.getMapLst(c, "*", order, conds);
    }

    @Override
    public <X> List<Map<String, Object>> getMapLst(Class<X> c, String fields, String order, SqlCondition... conds) throws Exception
    {
        String tableName = getTableName(c);
        String sql = getSqlParser().getSelectCommandString(tableName, fields, order, conds);
        return queryForListMap(sql);
    }

    @Override
    public <X> List<Map<String, String>> getStrMapLst(Class<X> c, SqlCondition... conds) throws Exception
    {
        return this.getStrMapLst(c, "*", null, conds);
    }

    @Override
    public <X> List<Map<String, String>> getStrMapLst(Class<X> c, String order, SqlCondition... conds) throws Exception
    {
        return this.getStrMapLst(c, "*", order, conds);
    }

    @Override
    public <X> List<Map<String, String>> getStrMapLst(Class<X> c, String fields, String order, SqlCondition... conds) throws Exception
    {
        List<Map<String, String>> retval = null;
        try
        {
            Session session = getSession();
            TableInfo info = session.getTableInfo(c);
            String sql = parser.getSelectCommandString(info.getTableName(), fields, order, conds);
            retval = session.query(sql, new ResultSetHandleListMapStringString());
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        }
        return retval;
    }

    @Override
    public <X> List<Map<String, String>> getStrMapLst(String sql, SqlParameter... params) throws SQLException
    {
        return query(sql, new ResultSetHandleListMapStringString(), params);
    }

    @Override
    public <X> List<X> getFirstLst(Class<X> retCla, Class<?> c, String field, SqlCondition... conds) throws Exception
    {
        return this.getFirstLst(retCla, c, field, null, conds);
    }

    @Override
    public <X> List<X> getFirstLst(Class<X> retCla, Class<?> c, String field, String order, SqlCondition... conds) throws Exception
    {
        List<X> retval = null;
        try
        {
            Session session = getSession();
            String tableName = getTableName(c);
            String sql = getSqlParser().getSelectCommandString(tableName, field, order, conds);
            retval = session.query(sql, RowsResultSetExtractor.gen(retCla));
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        }
        return retval;
    }

    @Override
    public boolean isExist(Class<?> c, SqlCondition... conds) throws Exception
    {
        try
        {
            Session session = getSession();
            TableInfo info = session.getTableInfo(c);
            DbCommand cmd = new DbCommand(getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("COUNT(0) AS A", "");
            cmd.setTableName(info.getTableName());
            cmd.setFilter(conds);
            String sql = cmd.toString();
            Long count = querySingleObject(Long.class, sql);
            return count > 0;
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        }
    }

    /**
     * TODO 未测试
     */
    @Override
    public <X> List<X> getAllChildIdsByParent(Class<X> retCla, Class<?> c, String parentVals, String parentFld, String childFld) throws Exception
    {
        List<X> childLst = getFirstLst(retCla, c, childFld, DBUtil.getInCond(parentFld, parentVals));
        if (childLst.size() == 0)
        {
            return childLst;
        }
        parentVals = StringUtil.join(childLst);
        childLst.addAll(getAllChildIdsByParent(retCla, c, parentVals, parentFld, childFld));
        return childLst;
    }

    /**
     * TODO 未测试
     */
    @Override
    public boolean isCircle(Class<?> c, int parentVal, String childVals, String parentFld, String childFld) throws Exception
    {
        List<Integer> childLst = getFirstLst(Integer.class, c, childFld, DBUtil.getInCond(parentFld, childVals));
        if (childLst.size() == 0)
        {
            return false;
        }
        if (childLst.contains(parentVal))
        {
            return true;
        }
        childVals = StringUtil.join(childLst);
        return isCircle(c, parentVal, childVals, parentFld, childFld);
    }

    @Override
    public int update(Class<?> c, SqlField field, SqlCondition... conds) throws Exception
    {
        List<SqlField> fields = new LinkedList<SqlField>();
        fields.add(field);
        return update(c, fields, conds);
    }

    @Override
    public int update(Class<?> c, List<SqlField> fields, SqlCondition... conds) throws Exception
    {
        String tableName = getTableName(c);
        String sql = parser.getUpdateCommandString(tableName, fields, conds);
        return update(sql);
    }

    @Override
    public int delete(Class<?> c, SqlCondition... conds) throws Exception
    {
        String tableName = getTableName(c);
        String sql = parser.getDeleteCommandString(tableName, conds);
        return update(sql);
    }

    private String getTableName(Class<?> c)
    {
        String tableName = null;
        TableInfo table = factory.getTableInfo(c);
        if (table != null)
        {
            tableName = table.getTableName();
        }
        return tableName == null ? c.getSimpleName().toUpperCase() : tableName;
    }

    private <X> X getListFirst(List<X> lst)
    {
        X retval = null;
        if (lst != null && !lst.isEmpty())
        {
            retval = lst.get(0);
        }
        return retval;
    }

}
