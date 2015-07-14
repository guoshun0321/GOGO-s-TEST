/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author Guo
 */
public class DBTestUtil
{

    private static final Logger logger = Logger.getLogger(DBTestUtil.class);

    /**
     * @return 结果
     */
    public static boolean testDB()
    {
        return testDB(ConfigUtil.getDriver(), ConfigUtil.getDbUrl(), ConfigUtil.getUser(), ConfigUtil.getPassword());
    }

    /**
     * @param dirver 驱动
     * @param url url
     * @param user 用户名
     * @param pwd 密码
     * @return 结果
     */
    public static boolean testDB(String dirver, String url, String user, String pwd)
    {
        boolean result = true;
        Connection conn = null;
        try
        {
            Class.forName(dirver);
            conn = DriverManager.getConnection(url, user, pwd);
        }
        catch (ClassNotFoundException ex)
        {
            logger.error("数据库驱动不存在：" + dirver, ex);
            result = false;
        }
        catch (SQLException ex)
        {
            logger.error("数据库连接错误，用户名：" + user + ";密码：" + pwd + ";连接字符串：" + url, ex);
            result = false;
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException ex)
                {
                    logger.error("关闭数据库连接出错");
                }
                finally
                {
                    conn = null;
                }
            }
        }
        return result;
    }
}
