package jetsennet.orm.ddl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.util.UncheckedOrmException;

public class ConnectionUtil
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);

    /**
     * 获取连接
     * 
     * @return
     */
    public static final Connection getConnection(Configuration conf)
    {
        Connection retval = null;
        if (conf != null)
        {
            try
            {
                Class.forName(conf.connInfo.driver);
                retval = DriverManager.getConnection(conf.connInfo.url, conf.connInfo.user, conf.connInfo.pwd);
            }
            catch (Exception ex)
            {
                throw new UncheckedOrmException(ex);
            }
        }
        else
        {
            throw new UncheckedOrmException("配置信息为空");
        }
        return retval;
    }

    /**
     * 关闭连接
     */
    public static final void closeConnection(Connection conn, Statement stat, ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        if (stat != null)
        {
            try
            {
                stat.close();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        if (conn != null)
        {
            try
            {
                conn.close();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }

    public static final int getSingleInt(Configuration conf, String sql)
    {
        int retval = -1;
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(conf);
            stat = conn.createStatement();
            logger.debug(sql);
            rs = stat.executeQuery(sql);
            if (rs.next())
            {
                retval = rs.getInt(1);
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeConnection(conn, stat, rs);
        }
        return retval;
    }

    public static final List<String> getStringLst(Configuration conf, String sql)
    {
        List<String> retval = new ArrayList<String>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try
        {
            conn = getConnection(conf);
            stat = conn.createStatement();
            logger.debug(sql);
            rs = stat.executeQuery(sql);
            while (rs.next())
            {
                retval.add(rs.getString(1));
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeConnection(conn, stat, rs);
        }
        return retval;
    }

    public static final void execute(Configuration conf, String sql)
    {
        Connection conn = null;
        Statement stat = null;
        try
        {
            conn = getConnection(conf);
            stat = conn.createStatement();
            logger.debug(sql);
            stat.executeUpdate(sql);
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            closeConnection(conn, stat, null);
        }
    }

}
