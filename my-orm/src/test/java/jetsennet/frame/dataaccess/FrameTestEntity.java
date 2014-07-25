package jetsennet.frame.dataaccess;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.util.SafeDateFormater;

import org.uorm.orm.annotation.ClassMapping;
import org.uorm.orm.annotation.FieldMapping;
import org.uorm.orm.annotation.KeyGenertator;

@ClassMapping(tableName = "ORM_FRAME", keyGenerator = KeyGenertator.SELECT, keyOrder = "id2,id1,id3")
public class FrameTestEntity implements Serializable
{

    @FieldMapping(columnName = "ID1", columnType = Types.INTEGER, primary = true)
    private int id1;

    @FieldMapping(columnName = "ID2", columnType = Types.INTEGER, primary = true)
    private int id2;

    @FieldMapping(columnName = "ID3", columnType = Types.INTEGER, primary = true)
    private int id3;

    @FieldMapping(columnName = "FIELD1", columnType = Types.INTEGER)
    private int field1;

    @FieldMapping(columnName = "FIELD2", columnType = Types.BIGINT)
    private long field2;

    @FieldMapping(columnName = "FIELD3", columnType = Types.NUMERIC)
    private double field3;

    @FieldMapping(columnName = "FIELD4", columnType = Types.VARCHAR)
    private String field4;

    @FieldMapping(columnName = "TEXT5", columnType = Types.CLOB)
    private String text5;

    @FieldMapping(columnName = "FIELD6", columnType = Types.TIMESTAMP)
    private Date date;

    public static FrameTestEntity newFrame(int base)
    {
        FrameTestEntity retval = new FrameTestEntity();
        retval.setId1(base);
        retval.setId2(base * 10);
        retval.setId3(base * 100);
        retval.setField1(base + 123);
        retval.setField2(base + 5432111);
        retval.setField3(base + 1234567.321);
        retval.setField4("测试，abde,_*+!@#$%^&*" + base);
        retval.setText5("long text" + base);
        retval.setDate(SafeDateFormater.parse("1956-11-21 11:32:45"));
        return retval;
    }

    public static List<FrameTestEntity> newFrames(int num)
    {
        List<FrameTestEntity> retval = new ArrayList<FrameTestEntity>(num);
        for (int i = 1; i <= num; i++)
        {
            retval.add(newFrame(i));
        }
        return retval;
    }

    public static Map<String, Object>[] newFrameMaps(SqlSessionFactory factory)
    {
        Map<String, Object>[] retval = new HashMap[100];
        TableInfo info = factory.getTableInfo(FrameTestEntity.class);
        for (int i = 101; i <= 200; i++)
        {
            FrameTestEntity frame = newFrame(i);
            retval[i - 101] = info.obj2map(frame);
        }
        return retval;
    }

    public static Map<String, Object> newFrameMap(SqlSessionFactory factory)
    {
        Map<String, Object> retval = null;
        TableInfo info = factory.getTableInfo(FrameTestEntity.class);
        FrameTestEntity frame = newFrame(201);
        retval = info.obj2map(frame);
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

    public int getId2()
    {
        return id2;
    }

    public void setId2(int id2)
    {
        this.id2 = id2;
    }

    public int getId3()
    {
        return id3;
    }

    public void setId3(int id3)
    {
        this.id3 = id3;
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

    public String getText5()
    {
        return text5;
    }

    public void setText5(String text5)
    {
        this.text5 = text5;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

}
