package jetsennet.orm.transform;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.Sql;
import jetsennet.util.TwoTuple;
import junit.framework.TestCase;

public class Transform2SqlSqlServerTest extends TestCase
{

    private Configuration config = new ConfigurationBuilderProp("/dbconfig.properties").genConfiguration();

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testPageSelect()
    {
        Transform2SqlSqlServer trans = new Transform2SqlSqlServer(config);

        SelectEntity select = Sql.select("*").from("all_type").order("id");
        PageSqlEntity page = trans.pageSelect(select, 2, 20);
        assertEquals("SELECT * FROM (SELECT TOP <%END%> ROW_NUMBER() OVER(ORDER BY id) AS RN, * FROM (SELECT * FROM all_type) ORIGIN) SUB WHERE RN >= <%BEGIN%>",
            page.pageSql);
        assertEquals("SELECT COUNT(*) FROM all_type", page.counterSql);
    }

    public void testGetLimitString()
    {
        String sql = "SELECT * FROM ALL_TYPE";
        Transform2SqlSqlServer trans = new Transform2SqlSqlServer(config);
        sql = trans.getLimitString(sql, 2, 20);
        assertEquals("WITH query AS (SELECT *, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as __UORM_ROW_NR__ FROM ALL_TYPE) SELECT * FROM query WHERE __UORM_ROW_NR__ >= 3 AND __UORM_ROW_NR__ < 23",
            sql);
    }

}
