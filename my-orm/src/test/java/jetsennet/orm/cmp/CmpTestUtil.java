package jetsennet.orm.cmp;

import java.util.List;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilder;
import jetsennet.orm.configuration.IConfigurationBuilder;
import jetsennet.orm.ddl.Ddl;
import jetsennet.orm.ddl.IDdl;
import jetsennet.orm.executor.keygen.KeyGen;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;

public class CmpTestUtil
{

    public static void build() throws Exception
    {
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST";
        String user = "sa";
        String pwd = "jetsen";

        IConfigurationBuilder builder = new ConfigurationBuilder(driver, url, user, pwd);
        Configuration config = builder.genConfiguration();

        IDdl ddl = Ddl.getDdl(config);

        CmpObject obj = genTestObject();
        List<TableInfo> tables = obj.getTables();
        for (TableInfo table : tables)
        {
            ddl.delete(table.getTableName());
            ddl.create(table);
        }
    }

    public static CmpObject genTestObject() throws Exception
    {
        CmpObject obj = new CmpObject("test");

        // TABLE1
        FieldInfo key = new FieldInfo("TABLE1_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        TableInfo table = new TableInfo(null, "TABLE1");
        table.field(key);
        table.field(new FieldInfo("TABLE1_PKEY", "String"));
        table.field(new FieldInfo("TABLE1_FIELD1", "int"));
        table.field(new FieldInfo("TABLE1_FIELD2", "long"));
        obj.addMainTable(table);

        // TABLE11
        key = new FieldInfo("TABLE11_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        table = new TableInfo(null, "TABLE11");
        table.field(key);
        table.field(new FieldInfo("TABLE11_PKEY", "String"));
        table.field(new FieldInfo("TABLE11_FIELD1", "int"));
        table.field(new FieldInfo("TABLE11_FIELD2", "long"));
        obj.addTable(table);

        // TABLE12
        key = new FieldInfo("TABLE12_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        table = new TableInfo(null, "TABLE12");
        table.field(key);
        table.field(new FieldInfo("TABLE12_PKEY", "String"));
        table.field(new FieldInfo("TABLE12_FIELD1", "int"));
        table.field(new FieldInfo("TABLE12_FIELD2", "long"));
        obj.addTable(table);

        // TABLE111
        key = new FieldInfo("TABLE111_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        table = new TableInfo(null, "TABLE111");
        table.field(key);
        table.field(new FieldInfo("TABLE111_PKEY", "String"));
        table.field(new FieldInfo("TABLE111_FIELD1", "int"));
        table.field(new FieldInfo("TABLE111_FIELD2", "long"));
        obj.addTable(table);

        // TABLE112
        key = new FieldInfo("TABLE112_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        table = new TableInfo(null, "TABLE112");
        table.field(key);
        table.field(new FieldInfo("TABLE112_PKEY", "String"));
        table.field(new FieldInfo("TABLE112_FIELD1", "int"));
        table.field(new FieldInfo("TABLE112_FIELD2", "long"));
        obj.addTable(table);

        // TABLE121
        key = new FieldInfo("TABLE121_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        table = new TableInfo(null, "TABLE121");
        table.field(key);
        table.field(new FieldInfo("TABLE121_PKEY", "String"));
        table.field(new FieldInfo("TABLE121_FIELD1", "int"));
        table.field(new FieldInfo("TABLE121_FIELD2", "long"));
        obj.addTable(table);

        // TABLE122
        key = new FieldInfo("TABLE122_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        table = new TableInfo(null, "TABLE122");
        table.field(key);
        table.field(new FieldInfo("TABLE122_PKEY", "String"));
        table.field(new FieldInfo("TABLE122_FIELD1", "int"));
        table.field(new FieldInfo("TABLE122_FIELD2", "long"));
        obj.addTable(table);

        // TABLE1221
        key = new FieldInfo("TABLE1221_KEY", "String").key();
        key.setKeyGen(KeyGen.KEYGEN_GUID);
        table = new TableInfo(null, "TABLE1221");
        table.field(key);
        table.field(new FieldInfo("TABLE1221_PKEY", "String"));
        table.field(new FieldInfo("TABLE1221_FIELD1", "int"));
        table.field(new FieldInfo("TABLE1221_FIELD2", "long"));
        obj.addTable(table);

        // 自循环
        obj.addRels("TABLE1", "TABLE1_KEY", "TABLE1", "TABLE1_PKEY");
        // 父子
        obj.addRels("TABLE1", "TABLE1_KEY", "TABLE11", "TABLE11_PKEY");
        obj.addRels("TABLE1", "TABLE1_KEY", "TABLE12", "TABLE12_PKEY");
        obj.addRels("TABLE11", "TABLE11_KEY", "TABLE111", "TABLE111_PKEY");
        obj.addRels("TABLE11", "TABLE11_KEY", "TABLE112", "TABLE112_PKEY");
        obj.addRels("TABLE12", "TABLE12_KEY", "TABLE121", "TABLE121_PKEY");
        obj.addRels("TABLE12", "TABLE12_KEY", "TABLE122", "TABLE122_PKEY");
        obj.addRels("TABLE122", "TABLE122_KEY", "TABLE1221", "TABLE1221_PKEY");

        return obj;
    }

    public static final CmpOpEntity genInsert()
    {
        CmpOpEntity retval = new CmpOpEntity("TABLE1", "insert");
        retval.addValue("TABLE1_PKEY", null);
        retval.addValue("TABLE1_FIELD1", "1");
        retval.addValue("TABLE1_FIELD2", "10");

        CmpOpEntity entity11 = new CmpOpEntity("TABLE11", "insert");
        entity11.addValue("TABLE11_FIELD1", "11");
        entity11.addValue("TABLE11_FIELD2", "110");
        retval.addSub(entity11);

        CmpOpEntity entity111 = new CmpOpEntity("TABLE111", "insert");
        entity111.addValue("TABLE111_FIELD1", "111");
        entity111.addValue("TABLE111_FIELD2", "1110");
        entity11.addSub(entity111);

        CmpOpEntity entity112 = new CmpOpEntity("TABLE112", "insert");
        entity112.addValue("TABLE112_FIELD1", "112");
        entity112.addValue("TABLE112_FIELD2", "1120");
        entity11.addSub(entity112);

        CmpOpEntity entity12 = new CmpOpEntity("TABLE12", "insert");
        entity12.addValue("TABLE11_FIELD1", "12");
        entity12.addValue("TABLE11_FIELD2", "120");
        retval.addSub(entity12);

        CmpOpEntity entity121 = new CmpOpEntity("TABLE121", "insert");
        entity121.addValue("TABLE121_FIELD1", "121");
        entity121.addValue("TABLE121_FIELD2", "1210");
        entity12.addSub(entity121);

        CmpOpEntity entity122 = new CmpOpEntity("TABLE122", "insert");
        entity122.addValue("TABLE122_FIELD1", "122");
        entity122.addValue("TABLE122_FIELD2", "1220");
        entity12.addSub(entity122);

        return retval;
    }

    public static final CmpOpEntity genInsert1()
    {
        CmpOpEntity retval = new CmpOpEntity("TABLE1", "insert");
        retval.addValue("TABLE1_PKEY", null);
        retval.addValue("TABLE1_FIELD1", "1");
        retval.addValue("TABLE1_FIELD2", "10");

        CmpOpEntity entity11 = new CmpOpEntity("TABLE11", "insert");
        entity11.addValue("TABLE11_FIELD1", "11");
        entity11.addValue("TABLE11_FIELD2", "110");
        retval.addSub(entity11);

        CmpOpEntity entity111 = new CmpOpEntity("TABLE111", "insert");
        entity111.addValue("TABLE111_FIELD1", "111");
        entity111.addValue("TABLE111_FIELD2", "1110");
        entity11.addSub(entity111);

        CmpOpEntity entity112 = new CmpOpEntity("TABLE112", "insert");
        entity112.addValue("TABLE112_FIELD1", "112");
        entity112.addValue("TABLE112_FIELD2", "1120");
        entity11.addSub(entity112);

        CmpOpEntity entity12 = new CmpOpEntity("TABLE12", "insert");
        entity12.addValue("TABLE11_FIELD1", "12");
        entity12.addValue("TABLE11_FIELD2", "120");
        retval.addSub(entity12);

        CmpOpEntity entity121 = new CmpOpEntity("TABLE121", "insert");
        entity121.addValue("TABLE121_FIELD1", "121");
        entity121.addValue("TABLE121_FIELD2", "1210");
        entity12.addSub(entity121);

        CmpOpEntity entity122 = new CmpOpEntity("TABLE122", "insert");
        entity122.addValue("TABLE122_FIELD1", "122");
        entity122.addValue("TABLE122_FIELD2", "1220");
        entity12.addSub(entity122);

        CmpOpEntity entity1221 = new CmpOpEntity("TABLE1221", "insert");
        entity1221.addValue("TABLE1221_FIELD1", "122");
        entity1221.addValue("TABLE1221_FIELD2", "1220");
        entity122.addSub(entity1221);

        return retval;
    }

    public static final CmpOpEntity genUpdateInsert(String id)
    {
        CmpOpEntity retval = new CmpOpEntity("TABLE1", "update");
        retval.addValue("TABLE1_KEY", id);
        retval.addValue("TABLE1_PKEY", null);
        retval.addValue("TABLE1_FIELD1", "2");
        retval.addValue("TABLE1_FIELD2", "20");

        CmpOpEntity entity11 = new CmpOpEntity("TABLE11", "insert");
        entity11.addValue("TABLE11_FIELD1", "11");
        entity11.addValue("TABLE11_FIELD2", "110");
        retval.addSub(entity11);

        CmpOpEntity entity111 = new CmpOpEntity("TABLE111", "insert");
        entity111.addValue("TABLE111_FIELD1", "111");
        entity111.addValue("TABLE111_FIELD2", "1110");
        entity11.addSub(entity111);

        CmpOpEntity entity112 = new CmpOpEntity("TABLE112", "insert");
        entity112.addValue("TABLE112_FIELD1", "112");
        entity112.addValue("TABLE112_FIELD2", "1120");
        entity11.addSub(entity112);

        CmpOpEntity entity12 = new CmpOpEntity("TABLE12", "insert");
        entity12.addValue("TABLE11_FIELD1", "12");
        entity12.addValue("TABLE11_FIELD2", "120");
        retval.addSub(entity12);

        CmpOpEntity entity121 = new CmpOpEntity("TABLE121", "insert");
        entity121.addValue("TABLE121_FIELD1", "121");
        entity121.addValue("TABLE121_FIELD2", "1210");
        entity12.addSub(entity121);

        CmpOpEntity entity122 = new CmpOpEntity("TABLE122", "insert");
        entity122.addValue("TABLE122_FIELD1", "122");
        entity122.addValue("TABLE122_FIELD2", "1220");
        entity12.addSub(entity122);

        CmpOpEntity entity1221 = new CmpOpEntity("TABLE1221", "insert");
        entity1221.addValue("TABLE1221_FIELD1", "122");
        entity1221.addValue("TABLE1221_FIELD2", "1220");
        entity122.addSub(entity1221);

        return retval;
    }

    public static final CmpOpEntity genUpdateDelete(String id)
    {
        CmpOpEntity retval = new CmpOpEntity("TABLE1", "update");
        retval.addValue("TABLE1_KEY", id);
        retval.addValue("TABLE1_PKEY", null);
        retval.addValue("TABLE1_FIELD1", "3");
        retval.addValue("TABLE1_FIELD2", "30");

        CmpOpEntity entity11 = CmpOpEntity.genDelete("TABLE11", "TABLE11_PKEY", id);
        retval.addSub(entity11);

        return retval;
    }

    public static void main(String[] args)
    {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@192.168.9.166:1521:JSMPORA";
        String user = "jcop";
        String pwd = "jetsen";

        IConfigurationBuilder builder = new ConfigurationBuilder(driver, url, user, pwd);
        Configuration configuration = builder.genConfiguration();
        Cmp cmp = CmpMgr.ensureCmp(configuration);
    }
}
