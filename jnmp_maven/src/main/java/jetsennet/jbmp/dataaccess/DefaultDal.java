/************************************************************************
日 期：2012-04-05
作 者: 梁宏杰
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.CLOB;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.TableInfo;
import jetsennet.jbmp.dataaccess.base.TableInfoMgr;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.util.TwoTuple;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.OracleExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlQuery;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.SerializerUtil;

/**
 * 数据库访问基础类
 * @author lianghongjie
 * @param <T> 参数
 */
public class DefaultDal<T>
{

    /**
     * 通过反射Class从annotation得到的数据库表信息
     */
    protected TableInfo tableInfo;
    private static final Logger logger = Logger.getLogger(DefaultDal.class);

    /**
     * 构造方法
     */
    public DefaultDal()
    {
        super();
    }

    /**
     * 构造方法
     * @param c 参数
     */
    public DefaultDal(Class<T> c)
    {
        super();
        tableInfo = TableInfoMgr.getTableInfo(c);
    }

    /**
     * 增
     * @param entity 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int insert(T entity) throws Exception
    {
        return insert(entity, true);
    }

    /**
     * 增
     * @param entity 参数
     * @param newKey 是否生成主键
     * @return 结果
     * @throws Exception 异常
     */
    public int insert(T entity, boolean newKey) throws Exception
    {
        return insert(this.tableInfo, entity, newKey);
    }

    /**
     * 增
     * @param c 类型
     * @param entity 参数
     * @param newKey 是否生成主键
     * @return 结果
     * @throws Exception 异常
     */
    public static <X> int insert(Class<X> c, X entity, boolean newKey) throws Exception
    {
        TableInfo tbInfo = TableInfoMgr.getTableInfo(c);
        return insert(tbInfo, entity, newKey);
    }

    private static <X> int insert(TableInfo tbInfo, X entity, boolean newKey) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getInsertCommandString(tbInfo.tableName, getSqlFields(tbInfo, entity, newKey));
        int re = exec.executeNonQuery(sql);
        if (tbInfo.keyColumn != null)
        {
            return (Integer) tbInfo.keyColumn.get(entity);
        }
        return re;
    }

    /**
     * 增
     * @param xml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int insertXml(String xml) throws Exception
    {
        HashMap<String, String> map = SerializerUtil.deserialize(xml, "");
        return insert(map);
    }

    /**
     * 增
     * @param map 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int insert(Map<String, String> map) throws Exception
    {
        return insert(map, true);
    }

    /**
     * 增
     * @param map 参数
     * @param newKey 是否生成主键
     * @return 结果
     * @throws Exception 异常
     */
    public int insert(Map<String, String> map, boolean newKey) throws Exception
    {
        return insert(this.tableInfo, map, newKey);
    }

    /**
     * 增
     * @param c 类型
     * @param map 参数
     * @param newKey 是否生成主键
     * @return 结果
     * @throws Exception 异常
     */
    public static <X> int insert(Class<X> c, Map<String, String> map, boolean newKey) throws Exception
    {
        TableInfo tbInfo = TableInfoMgr.getTableInfo(c);
        return insert(tbInfo, map, newKey);
    }

    private static <X> int insert(TableInfo tbInfo, Map<String, String> map, boolean newKey) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getInsertCommandString(tbInfo.tableName, getSqlFields(tbInfo, map, newKey));
        int re = exec.executeNonQuery(sql);
        String id = map.get("ID");
        if (id != null)
        {
            return Integer.parseInt(id);
        }
        return re;
    }

    /**
     * 改
     * @param entity 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(T entity) throws Exception
    {
        return update(entity, getKeyCondition(entity));
    }

    /**
     * 改
     * @param entity 参数
     * @param conds 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(T entity, SqlCondition... conds) throws Exception
    {
        return update(getSqlFields(entity, false), conds);
    }

    /**
     * 改
     * @param entity 参数
     * @param fields 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(T entity, List<SqlField> fields) throws Exception
    {
        return update(fields, getKeyCondition(entity));
    }

    /**
     * 改
     * @param key 参数
     * @param fields 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(int key, List<SqlField> fields) throws Exception
    {
        return update(fields, getKeyCondition(key));
    }

    /**
     * 改
     * @param fields 参数
     * @param conds 条件
     * @return 结果
     * @throws Exception 异常
     */
    public int update(List<SqlField> fields, SqlCondition... conds) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getUpdateCommandString(getTableName(), fields, conds);
        return exec.executeNonQuery(sql);
    }

    /**
     * 改
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(String sql) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        return exec.executeNonQuery(sql);
    }

    /**
     * 改
     * @param xml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int updateXml(String xml) throws Exception
    {
        HashMap<String, String> map = SerializerUtil.deserialize(xml, "");
        return update(map);
    }

    /**
     * 改
     * @param map 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(Map<String, String> map) throws Exception
    {
        return update(getSqlFields(map, false), getKeyCondition(map));
    }

    /**
     * 改
     * @param map 参数
     * @param conds 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(Map<String, String> map, SqlCondition... conds) throws Exception
    {
        return update(getSqlFields(map, false), conds);
    }

    /**
     * 删
     * @param entity 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int delete(T entity) throws Exception
    {
        return delete(getKeyCondition(entity));
    }

    /**
     * 删
     * @param key 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int delete(int key) throws Exception
    {
        return delete(getKeyCondition(key));
    }

    /**
     * 删
     * @param conds 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int delete(SqlCondition... conds) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getDeleteCommandString(getTableName(), conds);
        return exec.executeNonQuery(sql);
    }

    /**
     * 删
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static int delete(String sql) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        return exec.executeNonQuery(sql);
    }

    /**
     * 删
     * @param c 类型
     * @param conds 参数
     * @return 结果
     * @throws Exception
     */
    public static <X> int delete(Class<X> c, int key) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        TableInfo tbInfo = TableInfoMgr.getTableInfo(c);
        String sql = exec.getSqlParser().getDeleteCommandString(tbInfo.tableName, getKeyCondition(tbInfo, key));
        return exec.executeNonQuery(sql);
    }

    /**
     * 删
     * @param c 类型
     * @param conds 参数
     * @return 结果
     * @throws Exception
     */
    public static <X> int delete(Class<X> c, SqlCondition... conds) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getDeleteCommandString(TableInfoMgr.getTableInfo(c).tableName, conds);
        return exec.executeNonQuery(sql);
    }

    /**
     * 查
     * @param entity 参数
     * @return 结果
     * @throws Exception 异常
     */
    public T get(T entity) throws Exception
    {
        return get(getKeyCondition(entity));
    }

    /**
     * 查
     * @param key 参数
     * @return 结果
     * @throws Exception 异常
     */
    public T get(int key) throws Exception
    {
        return get(getKeyCondition(key));
    }

    /**
     * 查
     * @param conds 参数
     * @return 结果
     * @throws Exception 异常
     */
    public T get(SqlCondition... conds) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getSelectCommandString(getTableName(), null, null, conds);
        return get(sql);
    }

    /**
     * 查
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public T get(String sql) throws Exception
    {
        final List<T> retval = new ArrayList<T>(1);
        retval.add(null);
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null && rs.next())
                {
                    retval.set(0, genEntity(rs));
                }
            }
        });

        return retval.get(0);
    }

    /**
     * 查
     * @param key 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static <X> X get(Class<X> c, int key) throws Exception
    {
        final TableInfo tbInfo = TableInfoMgr.getTableInfo(c);
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getSelectCommandString(tbInfo.tableName, null, null, getKeyCondition(tbInfo, key));
        final Map<String, X> result = new HashMap<String, X>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null && rs.next())
                {
                    result.put("result", (X) genEntity(tbInfo, rs));
                }
            }
        });
        return result.get("result");
    }

    /**
     * 查
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static Map<String, Object> getMap(String sql) throws Exception
    {
        final Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null && rs.next())
                {
                    result.put("result", genMap(rs));
                }
            }
        });
        return result.get("result");
    }

    /**
     * 查
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static Map<String, String> getStrMap(String sql) throws Exception
    {
        final Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null && rs.next())
                {
                    result.put("result", genStrMap(rs));
                }
            }
        });
        return result.get("result");
    }

    /**
     * 查
     * @return 参数
     * @throws Exception 异常
     */
    public List<T> getAll() throws Exception
    {
        return getLst();
    }

    /**
     * 查
     * @param conds 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<T> getLst(SqlCondition... conds) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        return getLst(exec.getSqlParser().getSelectCommandString(getTableName(), null, null, conds));
    }

    /**
     * 查
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<T> getLst(String sql) throws Exception
    {
        final List<T> result = new ArrayList<T>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    while (rs.next())
                    {
                        result.add(genEntity(rs));
                    }
                }
            }
        });
        return result;
    }

    /**
     * 查
     * @param c 类型
     * @param conds 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static <X> List<X> getLst(Class<X> c, SqlCondition... conds) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        return getLst(c, exec.getSqlParser().getSelectCommandString(TableInfoMgr.getTableInfo(c).tableName, null, null, conds));
    }

    /**
     * 查
     * @param <X> 参数
     * @param c 参数
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static <X> List<X> getLst(final Class<X> c, String sql) throws Exception
    {
        final List<X> result = new ArrayList<X>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    TableInfo tbInfo = TableInfoMgr.getTableInfo(c);
                    while (rs.next())
                    {
                        result.add((X) genEntity(tbInfo, rs));
                    }
                }
            }
        });
        return result;
    }

    /**
     * 查
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static List<Map<String, Object>> getMapLst(String sql) throws Exception
    {
        final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    while (rs.next())
                    {
                        result.add(genMap(rs));
                    }
                }
            }
        });
        return result;
    }

    /**
     * 查
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static List<Map<String, String>> getStrMapLst(String sql) throws Exception
    {
        final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    while (rs.next())
                    {
                        result.add(genStrMap(rs));
                    }
                }
            }
        });
        return result;
    }

    /**
     * 查
     * @param sql 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static List<Object> getFirstLst(String sql) throws Exception
    {
        final List<Object> result = new ArrayList<Object>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    while (rs.next())
                    {
                        Object value = rs.getObject(1);
                        if (value instanceof Date)
                        {
                            value = rs.getTimestamp(1);
                        }
                        result.add(value);
                    }
                }
            }
        });
        return result;
    }

    /**
     * 获取查询中的前两个字段
     * @param sql
     * @return
     * @throws Exception
     */
    public static List<TwoTuple<Object, Object>> getTwoList(String sql) throws Exception
    {
        final List<TwoTuple<Object, Object>> retval = new ArrayList<TwoTuple<Object, Object>>();
        read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    while (rs.next())
                    {
                        Object value = rs.getObject(1);
                        if (value instanceof Date)
                        {
                            value = rs.getTimestamp(1);
                        }
                        Object value1 = rs.getObject(2);
                        if (value1 instanceof Date)
                        {
                            value1 = rs.getTimestamp(2);
                        }
                        retval.add(new TwoTuple<Object, Object>(value, value1));
                    }
                }
            }
        });
        return retval;
    }

    /**
     * 是否存在
     * @param table 表
     * @param conds 条件
     * @return -1，不存在；>0，存在
     * @throws Exception 异常
     */
    public static int isExist(String table, SqlCondition... conds) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.addField("COUNT(0) AS A", "");
        cmd.setTableName(table);
        cmd.setFilter(conds);
        String sql = cmd.toString();

        final int[] retval = new int[] { -1 };
        DefaultDal.read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    if (rs.next())
                    {
                        retval[0] = rs.getInt("A");
                    }
                }
            }
        });

        return retval[0];
    }

    /**
     * 查
     * @param xml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static Document getXml(String xml) throws Exception
    {
        SqlQuery query = SerializerUtil.deserialize(SqlQuery.class, xml);
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        return exec.fill(query);
    }

    /**
     * 
     * @param sql
     * @param values
     * @return
     * @throws Exception
     */
    public static int executeNoQuery(String sql, SqlValue[] values) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        return exec.executeNonQuery(sql, values);
    }

    /**
     * 替换ISqlExecutor.executeReader(sql)函数。具体用法如下： 
     * final List<Integer> ret = new ArrayList<Integer>();
     * DefaultDal.read("select * from bmp_object", new IReadHandle() {
     *      @Override 
     *      public void handle(ResultSet rs) throws Exception { 
     *          while (rs.next()) { 
     *          ret.add(rs.getInt("OBJ_ID")); 
     *          } 
     *      } 
     * }); 
     * 注意：
     * 1、IReadHandle.handle()里面不需要关闭ResultSet 
     * 2、IReadHandle.handle()函数里如果需要用到外部参数，则该参数必须为final.
     * @param sql 需要执行的SQL语句
     * @param handle 执行完SQL语句后，需要使用ResultSet的操作
     * @return
     */
    public static void read(String sql, IReadHandle handle) throws Exception
    {
        ResultSet rs = null;
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        boolean isTrans = exec.getIsTransing();
        try
        {
            if (!isTrans)
            {
                exec.transBegin();
            }
            rs = exec.executeReader(sql);
            if (handle != null)
            {
                handle.handle(rs);
            }
            if (!isTrans)
            {
                exec.transCommit();
            }
        }
        catch (Exception ex)
        {
            if (!isTrans)
            {
                exec.transRollback();
            }
            throw ex;
        }
        finally
        {
            if (rs != null)
            {
                if (isOracle(exec))
                {
                    Statement stat = rs.getStatement();
                    if (stat != null)
                    {
                        try
                        {
                            stat.close();
                        }
                        catch (Exception ex)
                        {
                            logger.error("关闭Statement失败。", ex);
                        }
                        finally
                        {
                            stat = null;
                        }
                    }
                }
                try
                {
                    rs.close();
                }
                catch (Exception ex)
                {
                    logger.error("关闭ResultSet失败。", ex);
                }
                finally
                {
                    rs = null;
                }
            }
            if (!isTrans)
            {
                SqlExecutorFacotry.unbindSqlExecutor();
            }
        }
    }

    /**
     * 开启事务
     */
    public void transBegin() throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.transBegin();
    }

    /**
     * 提交事务
     * @throws Exception 异常
     */
    public void transCommit() throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.transCommit();
    }

    /**
     * 回滚事务
     */
    public void transRollback()
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.transRollback();
    }

    public String getTableName()
    {
        return tableInfo.tableName;
    }

    protected T genEntity(ResultSet rs) throws Exception
    {
        return (T) genEntity(this.tableInfo, rs);
    }

    public static <X> X genEntity(Class<X> c, ResultSet rs) throws Exception
    {
        TableInfo tbInfo = TableInfoMgr.getTableInfo(c);
        return (X) genEntity(tbInfo, rs);
    }

    public static Object genEntity(TableInfo info, ResultSet rs) throws Exception
    {
        Object entity = info.tableClass.newInstance();
        for (int i = 0; i < info.columns.length; i++)
        {
            try
            {
                Object value = rs.getObject(info.columnNames[i]);
                if (value == null)
                {
                    continue;
                }
                if (value instanceof Date)
                {
                    value = rs.getTimestamp(info.columnNames[i]);
                }
                invokeSet(entity, info.setterMethods[i], value);
            }
            catch (Exception e)
            {
            }
        }
        return entity;
    }

    public static Map<String, Object> genMap(ResultSet rs) throws Exception
    {
        Map<String, Object> result = new HashMap<String, Object>();
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++)
        {
            Object value = rs.getObject(i);
            if (value instanceof Date)
            {
                value = rs.getTimestamp(i);
            }
            result.put(meta.getColumnLabel(i), value);
        }
        return result;
    }

    public static Map<String, String> genStrMap(ResultSet rs) throws Exception
    {
        Map<String, String> result = new HashMap<String, String>();
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++)
        {
            Object value = rs.getObject(i);
            if (value instanceof Date)
            {
                value = rs.getTimestamp(i);
            }
            result.put(meta.getColumnLabel(i), String.valueOf(value));
        }
        return result;
    }

    protected SqlCondition getKeyCondition(T entity) throws Exception
    {
        if (tableInfo.keyColumn == null)
        {
            return null;
        }
        return new SqlCondition(tableInfo.keyColumnName, String.valueOf(tableInfo.keyColumn.get(entity)), SqlLogicType.And, SqlRelationType.Equal,
            getParamType(tableInfo.keyColumn.getType()));
    }

    protected SqlCondition getKeyCondition(int id) throws Exception
    {
        return getKeyCondition(this.tableInfo, id);
    }

    protected SqlCondition getKeyCondition(Map<String, String> map) throws Exception
    {
        return getKeyCondition(this.tableInfo, map);
    }

    public static SqlCondition getKeyCondition(TableInfo tableInfo, int id) throws Exception
    {
        if (tableInfo.keyColumn == null)
        {
            return null;
        }
        return new SqlCondition(tableInfo.keyColumnName, String.valueOf(id), SqlLogicType.And, SqlRelationType.Equal,
            getParamType(tableInfo.keyColumn.getType()));
    }

    public static SqlCondition getKeyCondition(TableInfo tableInfo, Map<String, String> map) throws Exception
    {
        if (tableInfo.keyColumn == null)
        {
            return null;
        }
        String value = map.get(tableInfo.keyColumnName);
        if (value == null)
        {
            return null;
        }
        return new SqlCondition(tableInfo.keyColumnName, value, SqlLogicType.And, SqlRelationType.Equal, getParamType(tableInfo.keyColumn.getType()));
    }

    protected List<SqlField> getSqlFields(Object entity, boolean newKey) throws Exception
    {
        return getSqlFields(this.tableInfo, entity, newKey);
    }

    public static List<SqlField> getSqlFields(TableInfo tableInfo, Object entity, boolean newKey) throws Exception
    {
        List<SqlField> fieldLst = new ArrayList<SqlField>();
        for (int i = 0; i < tableInfo.columns.length; i++)
        {
            Field f = tableInfo.columns[i];
            if (f == tableInfo.keyColumn)
            {
                if (newKey)
                {
                    ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
                    boolean isTrans = exec.getIsTransing();
                    try
                    {
                        if (!isTrans)
                        {
                            exec.transBegin();
                        }
                        int id = exec.getNewId(tableInfo.tableName);
                        tableInfo.setterMethods[i].invoke(entity, id);
                        if (!isTrans)
                        {
                            exec.transCommit();
                        }
                    }
                    catch (Exception ex)
                    {
                        if (!isTrans)
                        {
                            exec.transRollback();
                        }
                        throw ex;
                    }
                }
            }
            Object value = tableInfo.getterMethods[i].invoke(entity);
            if (value == null)
            {
                continue;
            }
            fieldLst.add(new SqlField(tableInfo.columnNames[i], value, getParamType(f.getType())));
        }
        return fieldLst;
    }

    protected List<SqlField> getSqlFields(Map<String, String> map, boolean newKey) throws Exception
    {
        return getSqlFields(this.tableInfo, map, newKey);
    }

    public static List<SqlField> getSqlFields(TableInfo tableInfo, Map<String, String> map, boolean newKey) throws Exception
    {
        List<SqlField> fieldLst = new ArrayList<SqlField>();
        for (int i = 0; i < tableInfo.columns.length; i++)
        {
            Field f = tableInfo.columns[i];
            if (f == tableInfo.keyColumn && newKey)
            {
                ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
                boolean isTrans = exec.getIsTransing();
                try
                {
                    if (!isTrans)
                    {
                        exec.transBegin();
                    }
                    int id = exec.getNewId(tableInfo.tableName);
                    map.put("ID", Integer.toString(id));
                    fieldLst.add(new SqlField(tableInfo.columnNames[i], id, getParamType(f.getType())));
                    if (!isTrans)
                    {
                        exec.transCommit();
                    }
                }
                catch (Exception ex)
                {
                    if (!isTrans)
                    {
                        exec.transRollback();
                    }
                    throw ex;
                }
            }
            else
            {
                String value = map.get(tableInfo.columnNames[i]);
                if (value == null)
                {
                    continue;
                }
                fieldLst.add(new SqlField(tableInfo.columnNames[i], value, getParamType(f.getType())));
            }
        }
        return fieldLst;
    }

    private static void invokeSet(Object entity, Method method, Object value) throws Exception
    {
        if (value instanceof BigDecimal)
        {
            Class<?> paramClass = method.getParameterTypes()[0];
            if (paramClass == Integer.class || paramClass == int.class)
            {
                method.invoke(entity, ((BigDecimal) value).intValue());
            }
            else if (paramClass == Long.class || paramClass == long.class)
            {
                method.invoke(entity, ((BigDecimal) value).longValue());
            }
            else if (paramClass == Double.class || paramClass == double.class)
            {
                method.invoke(entity, ((BigDecimal) value).doubleValue());
            }
            else
            {
                method.invoke(entity, ((BigDecimal) value).intValue());
            }
        }
        // 处理ORACLE CLOB类型数据
        else if (value instanceof CLOB)
        {
            CLOB clob = (CLOB) value;
            String temp = clob.getSubString((long) 1, (int) clob.length());
            method.invoke(entity, temp);
        }
        else
        {
            method.invoke(entity, value);
        }
    }

    protected static SqlParamType getParamType(Class c)
    {
        if (c == String.class)
        {
            return SqlParamType.String;
        }
        else if (c == Integer.class || c == int.class)
        {
            return SqlParamType.Numeric;
        }
        else if (c == Long.class || c == long.class)
        {
            return SqlParamType.Numeric;
        }
        else if (c == Double.class || c == double.class)
        {
            return SqlParamType.Numeric;
        }
        else if (c == Float.class || c == float.class)
        {
            return SqlParamType.Numeric;
        }
        else if (c == Boolean.class || c == boolean.class)
        {
            return SqlParamType.Boolean;
        }
        else if (c == Date.class || c == Timestamp.class)
        {
            return SqlParamType.DateTime;
        }
        else
        {
            return SqlParamType.UnKnow;
        }
    }

    /**
     * 判断该连接使用的是否为ORACLE数据库
     * @param exec
     * @return
     */
    public static boolean isOracle(ISqlExecutor exec)
    {
        if (exec != null && exec instanceof OracleExecutor)
        {
            return true;
        }
        return false;
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        final List<Integer> ret = new ArrayList<Integer>();
        DefaultDal.read("select * from bmp_object", new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                while (rs.next())
                {
                    ret.add(rs.getInt("OBJ_ID"));
                }
            }
        });
    }
}
