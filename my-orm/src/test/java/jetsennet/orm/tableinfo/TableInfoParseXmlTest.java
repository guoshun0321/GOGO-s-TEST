package jetsennet.orm.tableinfo;

import java.sql.Types;
import java.util.Date;

import jetsennet.orm.executor.keygen.KeyGenEnum;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoParseXml;
import junit.framework.TestCase;

public class TableInfoParseXmlTest extends TestCase
{

    private static String str =
        "<table name=\"ACM\"><field name=\"key\" type=\"INT\" iskey=\"true\" keygen=\"\"/><field name=\"field1\" type=\"INT\"/><field name=\"field2\" type=\"LONG\"/><field name=\"field3\" type=\"NUMERIC\"/><field name=\"field4\" type=\"STRING\"/><field name=\"field5\" type=\"TEXT\"/><field name=\"field6\" type=\"DATETIME\"/></table>";

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testParse()
    {
        TableInfo info = TableInfoParseXml.parse(str);
        assertNotNull(info);
        assertEquals("ACM", info.getTableName());
        assertEquals(7, info.getFieldInfos().size());

        FieldInfo field = info.getFieldInfos().get(0);
        assertEquals("key", field.getName());
        assertEquals(int.class, field.getCls());
        assertEquals(FieldTypeEnum.INT, field.getType());
        assertEquals(Types.INTEGER, field.getSqlType());
        assertEquals(true, field.isKey());
        assertEquals("", field.getKeyGen());
        assertEquals(KeyGenEnum.UUID, field.getKeyEnum());

        field = info.getFieldInfos().get(1);
        assertEquals("field1", field.getName());
        assertEquals(int.class, field.getCls());
        assertEquals(FieldTypeEnum.INT, field.getType());
        assertEquals(Types.INTEGER, field.getSqlType());

        field = info.getFieldInfos().get(2);
        assertEquals("field2", field.getName());
        assertEquals(long.class, field.getCls());
        assertEquals(FieldTypeEnum.LONG, field.getType());
        assertEquals(Types.BIGINT, field.getSqlType());

        field = info.getFieldInfos().get(3);
        assertEquals("field3", field.getName());
        assertEquals(double.class, field.getCls());
        assertEquals(FieldTypeEnum.NUMERIC, field.getType());
        assertEquals(Types.NUMERIC, field.getSqlType());

        field = info.getFieldInfos().get(4);
        assertEquals("field4", field.getName());
        assertEquals(String.class, field.getCls());
        assertEquals(FieldTypeEnum.STRING, field.getType());
        assertEquals(Types.VARCHAR, field.getSqlType());

        field = info.getFieldInfos().get(5);
        assertEquals("field5", field.getName());
        assertEquals(String.class, field.getCls());
        assertEquals(FieldTypeEnum.TEXT, field.getType());
        assertEquals(Types.CLOB, field.getSqlType());

        field = info.getFieldInfos().get(6);
        assertEquals("field6", field.getName());
        assertEquals(Date.class, field.getCls());
        assertEquals(FieldTypeEnum.DATETIME, field.getType());
        assertEquals(Types.TIMESTAMP, field.getSqlType());
    }

}
