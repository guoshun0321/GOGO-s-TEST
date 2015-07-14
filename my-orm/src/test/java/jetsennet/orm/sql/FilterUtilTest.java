package jetsennet.orm.sql;

import junit.framework.TestCase;
import static jetsennet.orm.sql.FilterUtil.*;

public class FilterUtilTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testSql()
    {
        FilterNode node = and(eq("id", 1), noeq("id", 2), or(eq("value1", 1), noeq("value2", 2)));
        System.out.println(node);
    }
}
