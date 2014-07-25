package jetsennet.orm.tableinfo;

import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoMgr;
import jetsennet.orm.test.util.AllTypeEntity;
import jetsennet.util.SafeDateFormater;
import junit.framework.TestCase;

public class FieldInfoTest extends TestCase
{

    SqlSessionFactory factory = SqlSessionFactoryBuilder.builder();

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testGet()
    {
        AllTypeEntity all =
            new AllTypeEntity(3,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1927-12-11 11:22:33"),
                SafeDateFormater.parse("1927-12-11 11:22:33"));
        TableInfo info = factory.getTableInfo(all.getClass());
        assertEquals(3, info.getKey().get(all));
    }

    public void testSet()
    {
        AllTypeEntity all =
            new AllTypeEntity(3,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1927-12-11 11:22:33"),
                SafeDateFormater.parse("1927-12-11 11:22:33"));
        TableInfo info = factory.getTableInfo(all.getClass());
        info.getKey().set(all, 4l);
        assertEquals(4, info.getKey().get(all));
        info.getKey().set(all, 5);
        assertEquals(5, info.getKey().get(all));
    }

}
