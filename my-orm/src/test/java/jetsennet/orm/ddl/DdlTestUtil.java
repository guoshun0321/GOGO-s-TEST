package jetsennet.orm.ddl;

import static jetsennet.orm.tableinfo.FieldTypeEnum.DATETIME;
import static jetsennet.orm.tableinfo.FieldTypeEnum.INT;
import static jetsennet.orm.tableinfo.FieldTypeEnum.NUMERIC;
import static jetsennet.orm.tableinfo.FieldTypeEnum.STRING;

import java.util.List;

import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.FieldTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;
import junit.framework.TestCase;

public class DdlTestUtil extends TestCase
{

    public void testEmpty()
    {
        ddlTest(null);
    }

    public static final void ddlTest(IDdl ddl)
    {
        if (ddl == null)
        {
            return;
        }
        String tableName = "type_test".toUpperCase();
        // 删除表
        ddl.delete(tableName);
        boolean isExist = ddl.isExist(tableName);
        assertEquals(false, isExist);

        // 创建表
        TableInfo table = new TableInfo(null, tableName);
        table.field("ID", INT).key();
        table.field("FIELD1", STRING).length(20).disNullable().defaultValue("test");
        table.field("FIELD2", "text").defaultValue("test");
        table.field("FIELD3", INT).disNullable().defaultValue("30");
        table.field("FIELD4", "Long").defaultValue("655365");
        table.field("FIELD5", NUMERIC).defaultValue("655365.55");
        table.field("FIELD6", DATETIME).disNullable().defaultValue("now()");
        table.field("FIELD7", DATETIME).disNullable().defaultValue("1987-01-13 12:12:34");
        ddl.create(table);
        isExist = ddl.isExist(tableName);
        assertEquals(true, isExist);

        List<String> tables = ddl.listTable("TYPE");
        System.out.println(tables);
        boolean isMatch = false;
        for (String tName : tables)
        {
            if (tName.equalsIgnoreCase("type_test"))
            {
                isMatch = true;
            }
        }
        assertEquals(true, isMatch);

        // 添加字段
        FieldInfo field = new FieldInfo("Alter1", "int").disNullable().defaultValue("123");
        ddl.addColumn(tableName, field);
        field = new FieldInfo("Alter2", "long").disNullable().defaultValue("123");
        ddl.addColumn(tableName, field);
        field = new FieldInfo("Alter3", "numeric").disNullable().defaultValue("123.123");
        ddl.addColumn(tableName, field);
        field = new FieldInfo("Alter4", "string").length(30).disNullable().defaultValue("test");
        ddl.addColumn(tableName, field);
        field = new FieldInfo("Alter5", "text").disNullable().defaultValue("test");
        ddl.addColumn(tableName, field);
        field = new FieldInfo("Alter6", "datetime").disNullable().defaultValue("1987-01-13 12:12:34");
        ddl.addColumn(tableName, field);
        field = new FieldInfo("Alter7", "string").length(30).disNullable();
        ddl.addColumn(tableName, field);

        // 删除字段
        ddl.deleteColumn(tableName, "ALter1");
        ddl.deleteColumn(tableName, "ALter5");
        ddl.deleteColumn(tableName, "ALter7");

        table = ddl.getTableInfo(tableName);
        assertNotNull(table);
        assertNotNull(table.getKey());
        field = table.getFieldInfo("ID");
        assertEquals("ID", field.getName());
        assertEquals(FieldTypeEnum.INT, field.getType());
        assertEquals(true, field.isKey());
        assertEquals(false, field.isNullable());
        assertEquals(-1, field.getLength());
        if (ddl instanceof MySqlDdl)
        {
            assertEquals("0", field.getDefaultValue());
        }
        else
        {
            assertEquals(null, field.getDefaultValue());
        }

        field = table.getFieldInfo("FIELD1");
        assertEquals("FIELD1", field.getName());
        assertEquals(FieldTypeEnum.STRING, field.getType());
        assertEquals(false, field.isKey());
        assertEquals(false, field.isNullable());
        assertEquals(20, field.getLength());
        assertEquals("test", field.getDefaultValue());

        field = table.getFieldInfo("FIELD2");
        assertEquals("FIELD2", field.getName());
        assertEquals(FieldTypeEnum.TEXT, field.getType());
        assertEquals(false, field.isKey());
        assertEquals(true, field.isNullable());
        assertEquals(null, field.getDefaultValue());

        field = table.getFieldInfo("FIELD3");
        assertEquals("FIELD3", field.getName());
        assertEquals(FieldTypeEnum.INT, field.getType());
        assertEquals(false, field.isKey());
        assertEquals(false, field.isNullable());
        assertEquals("30", field.getDefaultValue());

        field = table.getFieldInfo("FIELD4");
        assertEquals("FIELD4", field.getName());
        assertEquals(FieldTypeEnum.LONG, field.getType());
        assertEquals(false, field.isKey());
        assertEquals(true, field.isNullable());
        assertEquals("655365", field.getDefaultValue());

        field = table.getFieldInfo("FIELD5");
        assertEquals("FIELD5", field.getName());
        assertEquals(FieldTypeEnum.NUMERIC, field.getType());
        assertEquals(false, field.isKey());
        assertEquals(true, field.isNullable());
        assertEquals("655365.55", field.getDefaultValue());

        field = table.getFieldInfo("FIELD6");
        assertEquals("FIELD6", field.getName());
        assertEquals(FieldTypeEnum.DATETIME, field.getType());
        assertEquals(false, field.isKey());
        assertEquals(false, field.isNullable());
        assertEquals("now()", field.getDefaultValue());

        field = table.getFieldInfo("FIELD7");
        assertEquals("FIELD7", field.getName());
        assertEquals(FieldTypeEnum.DATETIME, field.getType());
        assertEquals(false, field.isKey());
        assertEquals(false, field.isNullable());
        assertEquals("1987-01-13 12:12:34", field.getDefaultValue());
    }

}
