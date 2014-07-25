package jetsennet.orm.executor.resultset;

import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoMgr;
import jetsennet.orm.test.util.AllTypeEntity;
import junit.framework.TestCase;

public class ResultSetHandlePojoTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNewInstance() throws Exception
    {
        AllTypeEntity type = new AllTypeEntity();
        TableInfo info = new TableInfoMgr().ensureTableInfo(AllTypeEntity.class);
        info.getFieldInfo("value2").getField().set(type, null);
    }

}
