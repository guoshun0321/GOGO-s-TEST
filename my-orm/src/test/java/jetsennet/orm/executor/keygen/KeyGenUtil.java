package jetsennet.orm.executor.keygen;

import java.util.List;

import jetsennet.orm.ddl.IDdl;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoParseClz;
import junit.framework.TestCase;

public class KeyGenUtil extends TestCase
{

    public static void genTableDb(IDdl ddl, Class<?> cls)
    {
        ddl.rebuild(TableInfoParseClz.parse(IdentifierEntity.class));
        ddl.rebuild(TableInfoParseClz.parse(EfficientPkEntity.class));
        ddl.rebuild(TableInfoParseClz.parse(cls));
    }

    public static void testGenKey(SqlSessionFactory factory, Class<?> cls)
    {
        Session session = factory.openSession();
        TableInfo table = session.getTableInfo(cls);
        List<FieldInfo> fields = table.getKeyFields();

        Object obj = KeyGen.genKey(table.getTableName(), fields.get(0), session);
        assertEquals(1, obj);
        obj = KeyGen.genKey(table.getTableName(), fields.get(1), session);
        assertEquals((long) 1, obj);
        obj = KeyGen.genKey(table.getTableName(), fields.get(2), session);
        assertEquals((short) 1, obj);
        obj = KeyGen.genKey(table.getTableName(), fields.get(3), session);
        assertNotNull(obj);
        obj = KeyGen.genKey(table.getTableName(), fields.get(4), session);
        assertNotNull(obj);
    }

    public static void testGenKeyArray(SqlSessionFactory factory, Class<?> cls)
    {
        Session session = factory.openSession();
        TableInfo table = session.getTableInfo(cls);
        List<FieldInfo> fields = table.getKeyFields();
        Object[] obj = KeyGen.genKey(table.getTableName(), fields.get(0), session, 5);
        assertEquals(5, obj.length);
        assertEquals(2, obj[0]);
        assertEquals(3, obj[1]);
        assertEquals(4, obj[2]);
        assertEquals(5, obj[3]);
        assertEquals(6, obj[4]);
        obj = KeyGen.genKey(table.getTableName(), fields.get(1), session, 5);
        assertEquals(5, obj.length);
        assertEquals(2l, obj[0]);
        assertEquals(3l, obj[1]);
        assertEquals(4l, obj[2]);
        assertEquals(5l, obj[3]);
        assertEquals(6l, obj[4]);
        obj = KeyGen.genKey(table.getTableName(), fields.get(1), session, 1000);
        assertEquals(1006l, obj[999]);
        obj = KeyGen.genKey(table.getTableName(), fields.get(1), session, 1000);
        assertEquals(2006l, obj[999]);
        obj = KeyGen.genKey(table.getTableName(), fields.get(2), session, 5);
        assertEquals(5, obj.length);
        assertEquals((short) 1, obj[0]);
        assertEquals((short) 2, obj[1]);
        assertEquals((short) 3, obj[2]);
        assertEquals((short) 4, obj[3]);
        assertEquals((short) 5, obj[4]);
        obj = KeyGen.genKey(table.getTableName(), fields.get(3), session, 5);
        assertEquals(5, obj.length);
        obj = KeyGen.genKey(table.getTableName(), fields.get(4), session, 5);
        assertEquals(5, obj.length);
    }

}
