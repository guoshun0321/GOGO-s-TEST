package jetsennet.orm.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDataInfo
{

    private static String driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://192.168.8.171:3307/test";
    private static String user = "root";
    private static String pwd = "jetsen";

    public static final String CREATE = "create table BASE_TEST (id int primary key, value1 varchar(100), value2 varchar(200))";
    public static final String DROP = "drop table BASE_TEST;";
    public static final String INSERT = "insert into BASE_TEST values(1, 'value1', 'value2')";
    public static final String UPDATE = "update BASE_TEST set value1='value11', value2='value12' where id = 1";
    public static final String DELETE = "delete from BASE_TEST where id = 1";
    public static final String PREPARED = "insert into BASE_TEST values(?, ?, ?)";
    public static final String QUERY = "select * from BASE_TEST";

    private static final Logger logger = LoggerFactory.getLogger(MySqlDataInfo.class);

    public static void clearTable(String table) throws Exception
    {
        try
        {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, pwd);

            Statement stat = conn.createStatement();
            String sql = "DELETE FROM " + table;
            stat.execute(sql);
            sql = "DELETE FROM NET_SEQUENCE WHERE TABLE_NAME='" + table + "'";
            stat.executeUpdate(sql);
            stat.close();
            conn.close();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

}
