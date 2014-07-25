package jetsennet.orm.tableinfo;

import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.executor.keygen.KeyGenEnum;

@Table("ACM")
public class TestEntity
{

    @Id
    @Column("key1")
    private int key1;

    @Id(keyGen = "UUID", keyEnum = KeyGenEnum.GUID)
    @Column("key2")
    private long key2;

    @Column("field1")
    private int field1;

    @Column("field2")
    private long field2;

    @Column("field3")
    private double field3;

    @Column("field4")
    private String field4;

    @Column(value = "field5", isText = true)
    private String field5;

    @Column("field6")
    private Date field6;

}
