package jetsennet.orm.executor.keygen;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;

@Table("IDENTIFIER_TABLE")
public class IdentifierEntity
{

    @Column("TABLE_NAME")
    private String tableName;

    @Column("SERIALIZE_VALUE")
    private long value;

}
