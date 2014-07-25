package jetsennet.orm.executor.keygen;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;

@Table("KEY_GEN_ORACLE")
public class KeyGenOracleEntity
{

    @Id(keyEnum = KeyGenEnum.DB)
    @Column("ID1")
    private int id1;

    @Id(keyEnum = KeyGenEnum.DB_BATCH)
    @Column("ID2")
    private long id2;

    @Id(keyEnum = KeyGenEnum.INCRE)
    @Column("ID3")
    private short id3;

    @Id(keyEnum = KeyGenEnum.GUID)
    @Column("ID4")
    private String id4;

    @Id(keyEnum = KeyGenEnum.UUID)
    @Column("ID5")
    private String id5;

    @Id(keyEnum = KeyGenEnum.SEQ)
    @Column("ID6")
    private long id6;

    public int getId1()
    {
        return id1;
    }

    public void setId1(int id1)
    {
        this.id1 = id1;
    }

    public long getId2()
    {
        return id2;
    }

    public void setId2(long id2)
    {
        this.id2 = id2;
    }

    public short getId3()
    {
        return id3;
    }

    public void setId3(short id3)
    {
        this.id3 = id3;
    }

    public String getId4()
    {
        return id4;
    }

    public void setId4(String id4)
    {
        this.id4 = id4;
    }

    public String getId5()
    {
        return id5;
    }

    public void setId5(String id5)
    {
        this.id5 = id5;
    }

}
