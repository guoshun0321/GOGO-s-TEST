package jetsennet.orm.transform;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.Sql;
import junit.framework.TestCase;

public class Transform2SqlMySqlTest extends TestCase
{

    private Configuration config = new ConfigurationBuilderProp("/dbconfig.mysql.properties").genConfiguration();

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
        Transform2SqlMySql trans = new Transform2SqlMySql(config);

        SelectEntity select = Sql.select("*").from("all_type");
        PageSqlEntity page = trans.pageSelect(select, 2, 20);
        assertEquals("SELECT * FROM all_type LIMIT <%BEGIN%>,<%SIZE%>", page.pageSql);
        assertEquals("SELECT COUNT(*) FROM all_type", page.counterSql);
    }

    public void testGetLimitString()
    {
        String sql = "SELECT * FROM ALL_TYPE";
        Transform2SqlMySql trans = new Transform2SqlMySql(config);
        sql = trans.getLimitString(sql, 10, 20);
        assertEquals("SELECT * FROM ALL_TYPE limit 10,20", sql);
    }

}
