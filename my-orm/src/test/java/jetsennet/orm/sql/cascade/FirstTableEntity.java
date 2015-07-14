package jetsennet.orm.sql.cascade;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;

@Table("FirstTable")
public class FirstTableEntity
{

    @Id
    @Column("FF0")
    private int FF0;
    @Column("FF1")
    private int FF1;
    @Column("FF2")
    private String FF2;

    public static final String xml =
        "<table name=\"FirstTable\"><field name=\"FF0\" type=\"INT\" iskey=\"true\" keygen=\"\"/><field name=\"FF1\" type=\"INT\"/><field name=\"FF2\" type=\"STRING\"/></table>";

}
