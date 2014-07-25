package jetsennet.orm.executor.keygen;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;

@Table("EFFICIENT_PKGENERATOR")
public class EfficientPkEntity
{
    
    @Column("TABLE_NAME")
    private String tableName;
    
    @Column("SERIALIZE_VALUE")
    private long value;
    
    @Column("VER")
    private int ver;

}
