package jetsennet.orm.sql;

public class ConditionEntry
{

    public final String key;
    public final Object value;
    public final RelationshipEnum rel;

    public ConditionEntry(String key, Object value, RelationshipEnum rel)
    {
        this.key = key;
        this.value = value;
        this.rel = rel;
    }

}
