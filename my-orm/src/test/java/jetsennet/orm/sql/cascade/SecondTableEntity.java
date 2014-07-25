package jetsennet.orm.sql.cascade;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;

@Table("SecondTable")
public class SecondTableEntity
{

    @Id
    @Column("SF0")
    private int SF0;
    @Column("SF1")
    private int SF1;
    @Column("SF2")
    private String SF2;
    
    public static final String xml =
            "<table name=\"SecondTable\"><field name=\"SF0\" type=\"INT\" iskey=\"true\" keygen=\"\"/><field name=\"SF1\" type=\"INT\"/><field name=\"SF2\" type=\"STRING\"/></table>";

}
