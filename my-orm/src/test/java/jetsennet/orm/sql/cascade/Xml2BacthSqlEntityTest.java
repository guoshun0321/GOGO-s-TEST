package jetsennet.orm.sql.cascade;

import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.sql.cascade.CascadeSqlDeleteEntity;
import jetsennet.orm.sql.cascade.CascadeSqlInsertEntity;
import jetsennet.orm.sql.cascade.CascadeSqlUpdateEntity;
import jetsennet.orm.sql.cascade.Xml2CascadeSqlEntity;
import jetsennet.orm.tableinfo.TableInfoMgr;
import junit.framework.TestCase;

public class Xml2BacthSqlEntityTest extends TestCase
{

    private static SqlSessionFactory factory = SqlSessionFactoryBuilder.builder();

    public static final String xml1 =
        "<FirstTable action=\"insert\" autokey=\"false\"><FF0>1</FF0><FF1>3</FF1><FF2>test</FF2><SecondTable action=\"update\"><SF0>2</SF0><SF1>$FirstTable.FF1</SF1><SF2>test.second</SF2></SecondTable></FirstTable>";

    public static final String xml2 =
        "<FirstTable action=\"delete\" filter=\"FF0\"><FF0>1</FF0><SecondTable action=\"delete\" filter=\"SF1\" affected=\"true\"><SF1>$FirstTable.FF0</SF1><ThirdTable action=\"delete\" filter=\"TF1\"><TF1>$SecondTable.INFO#AFFECTED</TF1></ThirdTable></SecondTable></FirstTable>";

    static
    {
        factory.getTableInfoMgr().registerTableInfo(FirstTableEntity.class);
        factory.getTableInfoMgr().registerTableInfo(SecondTableEntity.class);
        factory.getTableInfoMgr().registerTableInfo(ThirdTableEntity.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testInsertUpdate()
    {
        //        CascadeSqlInsertEntity batch = (CascadeSqlInsertEntity) Xml2CascadeSqlEntity.parse(xml1);
        //        assertNotNull(batch);
        //        assertEquals("FirstTable", batch.tableName);
        //        assertEquals("INSERT", batch.getType().name());
        //        assertEquals(false, batch.isAutoKey());
        //        assertEquals("{FF0=1, FF1=3, FF2=test}", batch.getValueMap().toString());
        //
        //        CascadeSqlUpdateEntity batch1 = (CascadeSqlUpdateEntity) (batch.subs.get(0));
        //        assertNotNull(batch);
        //        assertEquals("SecondTable", batch1.tableName);
        //        assertEquals("UPDATE", batch1.getType().name());
        //        assertEquals("{SF1=$FirstTable.FF1, SF2=test.second, SF0=2}", batch1.getValueMap().toString());
    }

    public void testDelete()
    {
        //        CascadeSqlDeleteEntity batch = (CascadeSqlDeleteEntity) Xml2CascadeSqlEntity.parse(xml2);
        //        assertNotNull(batch);
        //        assertEquals("FirstTable", batch.tableName);
        //        assertEquals("DELETE", batch.getType().name());
        //        assertEquals("FF0", batch.getFilterFiled());
        //        assertEquals(false, batch.isAffected());
        //        assertEquals("{FF0=1}", batch.getValueMap().toString());
        //
        //        batch = (CascadeSqlDeleteEntity) batch.subs.get(0);
        //        assertNotNull(batch);
        //        assertEquals("SecondTable", batch.tableName);
        //        assertEquals("DELETE", batch.getType().name());
        //        assertEquals("SF1", batch.getFilterFiled());
        //        assertEquals(true, batch.isAffected());
        //        assertEquals("{SF1=$FirstTable.FF0}", batch.getValueMap().toString());
        //
        //        batch = (CascadeSqlDeleteEntity) batch.subs.get(0);
        //        assertNotNull(batch);
        //        assertEquals("ThirdTable", batch.tableName);
        //        assertEquals("DELETE", batch.getType().name());
        //        assertEquals("TF1", batch.getFilterFiled());
        //        assertEquals(false, batch.isAffected());
        //        assertEquals("{TF1=$SecondTable.INFO#AFFECTED}", batch.getValueMap().toString());
    }

}
