package jetsennet.orm.transform;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.Sql;
import junit.framework.TestCase;

public class Transform2SqlOracleTest extends TestCase
{

    private Configuration config = new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration();

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
        Transform2SqlOracle trans = new Transform2SqlOracle(config);

        SelectEntity select = Sql.select("*").from("all_type");
        PageSqlEntity page = trans.pageSelect(select, 2, 20);
        assertEquals("SELECT * FROM (SELECT ROWNUM AS RN, ORIGIN.* FROM (SELECT * FROM all_type) ORIGIN WHERE ROWNUM <= <%END%>) SUB WHERE RN >= <%BEGIN%>",
            page.pageSql);
        assertEquals("SELECT COUNT(*) FROM all_type", page.counterSql);
    }

    public void testFormatDateTimeString()
    {
        Transform2SqlOracle trans = new Transform2SqlOracle(config);
        assertEquals("to_date('1987-03-21 11:11:21','YYYY-MM-DD HH24:MI:SS')", trans.formatDateTimeString("1987-03-21 11:11:21"));
    }

    public void testGetLimitString()
    {
        String sql = "SELECT * FROM ALL_TYPE";
        Transform2SqlOracle trans = new Transform2SqlOracle(config);
        sql = trans.getLimitString(sql, 10, 20);
        assertEquals("select * from ( select row_.*, rownum rownum_ from ( SELECT * FROM ALL_TYPE ) row_ where rownum <= 30) where rownum_ > 10", sql);
    }

}
