package jetsennet.orm.executor;

import java.util.Date;
import java.util.UUID;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.executor.keygen.KeyGenEnum;
import jetsennet.util.SafeDateFormater;

@Table("TEST_SIMPLE")
public class SimpleSqlEntity
{

    @Id(keyEnum = KeyGenEnum.DB)
    @Column("ID1")
    private int id1;

    @Id(keyEnum = KeyGenEnum.UUID)
    @Column("ID2")
    private String id2;

    @Column("FIELD1")
    private int field1;

    @Column("FIELD2")
    private long field2;

    @Column("FIELD3")
    private double field3;

    @Column("FIELD4")
    private String field4;

    @Column(value = "FIELD5", isText = true)
    private String field5;

    @Column(value = "FIELD6")
    private Date field6;

    public static SimpleSqlEntity instance(int base, boolean isOracle)
    {
        SimpleSqlEntity retval = new SimpleSqlEntity();
        retval.id1 = base;
        retval.id2 = UUID.randomUUID().toString();
        retval.field1 = base * 10;
        retval.field2 = base * 100l;
        retval.field3 = base * 100 + 0.78;
        retval.field4 = "short field4";
        retval.field5 = "long field5";
        StringBuilder sb = new StringBuilder(100000);
        if (!isOracle)
        {
            char next = 'a';
            for (int i = 0; i < 50000; i++)
            {
                sb.append(next);
                if (next == 'z')
                {
                    next = 'a';
                }
                else
                {
                    next++;
                }
            }
        }
        retval.field5 = sb.toString();
        retval.field6 = SafeDateFormater.parse("1977-09-08 11:22:33");
        return retval;
    }

    public int getId1()
    {
        return id1;
    }

    public void setId1(int id1)
    {
        this.id1 = id1;
    }

    public String getId2()
    {
        return id2;
    }

    public void setId2(String id2)
    {
        this.id2 = id2;
    }

    public int getField1()
    {
        return field1;
    }

    public void setField1(int field1)
    {
        this.field1 = field1;
    }

    public long getField2()
    {
        return field2;
    }

    public void setField2(long field2)
    {
        this.field2 = field2;
    }

    public double getField3()
    {
        return field3;
    }

    public void setField3(double field3)
    {
        this.field3 = field3;
    }

    public String getField4()
    {
        return field4;
    }

    public void setField4(String field4)
    {
        this.field4 = field4;
    }

    public String getField5()
    {
        return field5;
    }

    public void setField5(String field5)
    {
        this.field5 = field5;
    }

    public Date getField6()
    {
        return field6;
    }

    public void setField6(Date field6)
    {
        this.field6 = field6;
    }

}
