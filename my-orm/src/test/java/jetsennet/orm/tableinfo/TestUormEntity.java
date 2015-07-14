package jetsennet.orm.tableinfo;

import java.sql.Types;
import java.util.Date;

import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.annotation.KeyGenertator;

@ClassMapping(tableName = "ACM", keyGenerator = KeyGenertator.UUID, keyOrder = "key2,key1")
public class TestUormEntity
{

    @FieldMapping(columnName = "key1", columnType = Types.INTEGER, primary = true, includeInWrites = true, includeInUpdate = false)
    private int key1;

    @FieldMapping(columnName = "key2", columnType = Types.BIGINT, primary = true, includeInWrites = true, includeInUpdate = false)
    private long key2;

    @FieldMapping(columnName = "field1", columnType = Types.INTEGER)
    private int field1;

    @FieldMapping(columnName = "field2", columnType = Types.BIGINT)
    private long field2;

    @FieldMapping(columnName = "field3", columnType = Types.NUMERIC)
    private double field3;

    @FieldMapping(columnName = "field4", columnType = Types.VARCHAR)
    private String field4;

    @FieldMapping(columnName = "field5", columnType = Types.CLOB)
    private String field5;

    @FieldMapping(columnName = "field6", columnType = Types.TIMESTAMP)
    private Date field6;

}
