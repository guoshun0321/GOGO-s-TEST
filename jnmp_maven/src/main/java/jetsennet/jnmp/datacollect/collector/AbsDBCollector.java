package jetsennet.jnmp.datacollect.collector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.datacollect.collector.AbsCollector;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;

import org.apache.log4j.Logger;

/**
 * @author lianghongjie DB采集器
 */
public abstract class AbsDBCollector extends AbsCollector
{
    private static final Logger logger = Logger.getLogger(AbsDBCollector.class);

    /**
     * 连接信息
     */
    protected Connection con;
    protected Statement stm;
    protected int connTime;

    @Override
    public void connect() throws CollectorException
    {
        long startTime = System.currentTimeMillis();
        String driverClassName = getDriverClassName();
        try
        {
            Class.forName(driverClassName);
        }
        catch (ClassNotFoundException e)
        {
            String msg = "找不到数据库的JDBC驱动:" + driverClassName;
            logger.error(msg, e);
            throw new CollectorException(msg, e);
        }
        try
        {
            con = DriverManager.getConnection(getConnectionURL(), mo.getUserName(), mo.getUserPwd());
        }
        catch (SQLException e)
        {
            String msg = "无法连接数据库：" + mo.getIpAddr();
            logger.error(msg, e);
            throw new CollectorException(msg, e);
        }
        try
        {
            stm = con.createStatement();
        }
        catch (SQLException e)
        {
            throw new CollectorException(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        connTime = (int) (endTime - startTime);
    }

    /**
     * 获取驱动器类名
     * @return
     */
    protected abstract String getDriverClassName();

    /**
     * 获取数据库连接URL
     * @return
     */
    protected abstract String getConnectionURL();

    @Override
    public abstract Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg);

    @Override
    public void close()
    {
        try
        {
            if (stm != null)
            {
                stm.close();
            }
            if (con != null)
            {
                con.close();
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
        }
        finally
        {
            stm = null;
            con = null;
        }
    }

    /**
     * 确定数据库名称
     * @param name
     * @return
     */
    protected String ensureDbName(String name)
    {
        return name == null ? "" : name;
    }
}
