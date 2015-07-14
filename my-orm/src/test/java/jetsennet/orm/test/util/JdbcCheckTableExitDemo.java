package jetsennet.orm.test.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @blog http://www.micmiu.com
 * @author Michael
 */
public class JdbcCheckTableExitDemo
{
    
    private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String url = "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST";
    private static String user = "sa";
    private static String pwd = "jetsen";

    // private static String url = "jdbc:oracle:thin:@localhost:1521:ORA11g";
    // private static String user = "demo";
    // private static String password = "111111";
    // private static String driver = "oracle.jdbc.driver.OracleDriver";

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Connection conn = null;
        String tableName = "TB_MYTEST";
        try
        {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(false);

            DatabaseMetaData meta = conn.getMetaData();

            // 第一个参数catalog在MySQL中对应数据库名：michaeldemo
            ResultSet rsTables = meta.getTables(null, null, "", new String[] { "TABLE" });

            // 第二个参数schemaPattern在ORACLE中对应用户名：demo
            // ResultSet rsTables = meta.getTables(null, "DEMO", tableName,
            // new String[] { "TABLE" });

            System.out.println("getTables查询信息如下：");
            System.out.println("TABLE_CAT \t TABLE_SCHEM \t TABLE_NAME \t TABLE_TYPE");

            while (rsTables.next())
            {
                System.out.println(rsTables.getString("TABLE_CAT")
                    + "\t"
                    + rsTables.getString("TABLE_SCHEM")
                    + "\t"
                    + rsTables.getString("TABLE_NAME")
                    + "\t"
                    + rsTables.getString("TABLE_TYPE"));
            }
            rsTables.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != conn)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
