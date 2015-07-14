package jetsennet.orm.test.util;

import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;

@Table("ALL_TYPE")
public class AllTypeEntity
{

    @Id
    @Column("id")
    private int id;

    @Column("num1")
    private long num1;

    @Column("value1")
    private String value1;

    @Column("value2")
    private String value2;

    @Column("value3")
    private String value3;

    @Column("date1")
    private Date date1;

    @Column("date2")
    private Date date2;

    public AllTypeEntity()
    {
    }

    public AllTypeEntity(int id, long num1, String value1, String value2, String value3, Date date1, Date date2)
    {
        this.id = id;
        this.num1 = num1;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.date1 = date1;
        this.date2 = date2;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public long getNum1()
    {
        return num1;
    }

    public void setNum1(long num1)
    {
        this.num1 = num1;
    }

    public String getValue1()
    {
        return value1;
    }

    public void setValue1(String value1)
    {
        this.value1 = value1;
    }

    public String getValue2()
    {
        return value2;
    }

    public void setValue2(String value2)
    {
        this.value2 = value2;
    }

    public String getValue3()
    {
        return value3;
    }

    public void setValue3(String value3)
    {
        this.value3 = value3;
    }

    public Date getDate1()
    {
        return date1;
    }

    public void setDate1(Date date1)
    {
        this.date1 = date1;
    }

    public Date getDate2()
    {
        return date2;
    }

    public void setDate2(Date date2)
    {
        this.date2 = date2;
    }

}
