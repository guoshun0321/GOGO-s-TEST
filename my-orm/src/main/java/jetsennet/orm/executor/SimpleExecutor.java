package jetsennet.orm.executor;

import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.orm.executor.resultset.IResultSetHandle;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.convert.SqlTypeConvert;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uorm.orm.mapping.AsciiStream;

public class SimpleExecutor extends AbsExecutor
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(SimpleExecutor.class);

    protected SimpleExecutor()
    {

    }

    public int update(ITransactionManager trans, String sql)
    {
        int retval = DEFAULT_RETURN;
        // 用于判断是否需要打开关闭连接，下同
        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        Statement stat = null;
        try
        {
            conn = trans.openConnection();
            stat = conn.createStatement();
            if (trans.getFactory().isDebug())
            {
                logger.debug("sql:" + sql);
            }
            retval = stat.executeUpdate(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeStatement(stat);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    public int[] update(ITransactionManager trans, String[] sqls)
    {
        int length = sqls.length;
        int[] retval = new int[length];
        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        Statement stat = null;
        try
        {
            conn = trans.openConnection();
            stat = conn.createStatement();
            for (int i = 0; i < length; i++)
            {
                if (trans.getFactory().isDebug())
                {
                    logger.debug("sql:" + sqls[i]);
                }
                stat.addBatch(sqls[i]);
            }
            retval = stat.executeBatch();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeStatement(stat);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    public int[] update(ITransactionManager trans, String sql, TableInfo table, List<Map<String, Object>> objValMaps)
    {
        int[] retval = null;
        if (objValMaps == null || objValMaps.size() == 0)
        {
            return retval;
        }

        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        PreparedStatement stmt = null;
        List<Clob> clobs = new ArrayList<Clob>(2);
        List<Blob> blobs = new ArrayList<Blob>(2);
        try
        {
            conn = trans.openConnection();
            if (trans.getFactory().isDebug())
            {
                logger.debug("sql:" + sql);
            }
            stmt = conn.prepareStatement(sql);
            this.handleSet(trans, stmt, table, objValMaps, clobs, blobs, true);
            retval = stmt.executeBatch();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeClobs(clobs);
            closeBlobs(blobs);
            closeStatement(stmt);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    public int update(ITransactionManager trans, String sql, Object[] values)
    {
        int retval = -1;

        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        Statement stat = null;
        try
        {
            conn = trans.openConnection();
            if (trans.getFactory().isDebug())
            {
                logger.debug("sql:" + sql);
            }
            if (values == null || values.length == 0)
            {
                stat = conn.createStatement();
                retval = stat.executeUpdate(sql);
            }
            else
            {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stat = stmt;
                stat = stmt;
                int length = values.length;
                for (int i = 1; i <= length; i++)
                {
                    stmt.setObject(i, values[i - 1]);
                }
                retval = stmt.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeStatement(stat);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    public <T> T query(ITransactionManager trans, String sql, IResultSetHandle<T> handle)
    {
        T retval = null;
        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = trans.openConnection();
            stat = conn.createStatement();
            if (trans.getFactory().isDebug())
            {
                logger.debug("sql:" + sql);
            }
            rs = stat.executeQuery(sql);
            retval = handle.handle(rs);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(stat);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    @Override
    public <T> T query(ITransactionManager trans, String sql, TableInfo tableInfo, Map<String, Object> objValMap, IResultSetHandle<T> handle)
    {
        T retval = null;
        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Clob> clobs = new ArrayList<Clob>(2);
        List<Blob> blobs = new ArrayList<Blob>(2);
        try
        {
            conn = trans.openConnection();
            if (trans.getFactory().isDebug())
            {
                logger.debug("sql:" + sql);
            }
            stmt = conn.prepareStatement(sql);
            List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>(1);
            lst.add(objValMap);
            this.handleSet(trans, stmt, tableInfo, lst, clobs, blobs, false);
            rs = stmt.executeQuery();
            retval = handle.handle(rs);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeClobs(clobs);
            closeBlobs(blobs);
            closeResultSet(rs);
            closeStatement(stmt);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    public <T> T query(ITransactionManager trans, String sql, Object[] values, IResultSetHandle<T> handle)
    {
        T retval = null;
        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try
        {
            conn = trans.openConnection();
            if (trans.getFactory().isDebug())
            {
                logger.debug("sql:" + sql);
            }
            stmt = conn.prepareStatement(sql);
            int length = values.length;
            for (int i = 1; i <= length; i++)
            {
                stmt.setObject(i, values[i - 1]);
            }
            rs = stmt.executeQuery();
            retval = handle.handle(rs);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(stmt);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    public boolean executor(ITransactionManager trans, String sql, Object[] values)
    {
        boolean retval = false;
        boolean isSelf = !trans.isConnectionOpen();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = trans.openConnection();
            if (trans.getFactory().isDebug())
            {
                logger.debug("sql:" + sql);
            }
            if (values == null || values.length == 0)
            {
                stat = conn.createStatement();
                retval = stat.execute(sql);
            }
            else
            {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stat = stmt;
                int length = values.length;
                for (int i = 1; i <= length; i++)
                {
                    stmt.setObject(i, values[i - 1]);
                }
                retval = stmt.execute();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeResultSet(rs);
            closeStatement(stat);
            if (isSelf)
            {
                this.closeConnection(trans);
            }
        }
        return retval;
    }

    /**
     * 设置PreparedStatement中"?"的值
     * 
     * @param trans
     * @param stmt
     * @param tableName
     * @param objValMaps
     * @param clobs
     * @param blobs
     * @param addBatch 是否调用addBatch。对于oracle，在调用executeQuery之前调用addBatch会导致异常。
     * @throws SQLException
     */
    private void handleSet(ITransactionManager trans, PreparedStatement stmt, TableInfo tableInfo, List<Map<String, Object>> objValMaps,
            List<Clob> clobs, List<Blob> blobs, boolean addBatch) throws SQLException
    {
        for (Map<String, Object> objValMap : objValMaps)
        {
            int idx = 1;
            for (Map.Entry<String, Object> objValEntry : objValMap.entrySet())
            {
                String fieldName = objValEntry.getKey();
                Object val = objValEntry.getValue();
                FieldInfo fieldInfo = tableInfo.getFieldInfo(fieldName);
                if (fieldInfo == null)
                {
                    throw new UncheckedOrmException("未知列名：" + fieldName);
                }
                int sqlType = fieldInfo.getSqlType();

                if (val == null)
                {
                    stmt.setNull(idx, sqlType);
                }
                else
                {
                    if (trans.getFactory().getConfig().isOracle() && (sqlType == Types.CLOB || sqlType == Types.NCLOB || sqlType == Types.BLOB))
                    {
                        if (sqlType == Types.CLOB || sqlType == Types.NCLOB)
                        {
                            if (val instanceof byte[])
                            {
                                Clob lob = trans.createClob();
                                lob.setString(1, new String((byte[]) val));
                                stmt.setClob(idx, lob);
                                clobs.add(lob);
                            }
                            else if (val instanceof String)
                            {
                                Clob lob = trans.createClob();
                                lob.setString(1, (String) val);
                                stmt.setClob(idx, lob);
                                clobs.add(lob);
                            }
                            else if (val instanceof AsciiStream)
                            {
                                stmt.setClob(idx, new InputStreamReader(((AsciiStream) val).getInputStream()), ((AsciiStream) val).getLength());
                            }
                        }
                        else
                        {
                            Blob lob = trans.createBlob();
                            lob.setBytes(1, (byte[]) val);
                            stmt.setBlob(idx, lob);
                            blobs.add(lob);
                        }
                    }
                    else
                    {
                        val = SqlTypeConvert.convert(val, sqlType);
                        if (sqlType == Types.CLOB)
                        {
                            stmt.setObject(idx, val, Types.LONGVARCHAR);
                        }
                        else
                        {
                            stmt.setObject(idx, val, sqlType);
                        }
                    }
                }
                idx++;
            }
            if (addBatch)
            {
                stmt.addBatch();
            }
        }
    }
}
