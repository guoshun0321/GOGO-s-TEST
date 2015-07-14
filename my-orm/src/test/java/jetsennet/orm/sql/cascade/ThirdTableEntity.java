package jetsennet.orm.sql.cascade;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;

@Table("ThirdTable")
public class ThirdTableEntity
{

    @Id
    @Column("TF0")
    private int TF0;
    @Column("TF1")
    private int TF1;
    @Column("TF2")
    private String TF2;
    
    public static final String xml =
            "<table name=\"ThirdTable\"><field name=\"TF0\" type=\"INT\" iskey=\"true\" keygen=\"\"/><field name=\"TF1\" type=\"INT\"/><field name=\"TF2\" type=\"STRING\"/></table>";

}
