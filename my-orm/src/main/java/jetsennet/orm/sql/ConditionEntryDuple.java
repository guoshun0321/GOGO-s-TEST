package jetsennet.orm.sql;

public class ConditionEntryDuple extends ConditionEntry
{

    /**
     * 第一个参数
     */
    public final Object first;
    /**
     * 第二个参数
     */
    public final Object second;

    public ConditionEntryDuple(String key, Object first, Object second, RelationshipEnum rel)
    {
        super(key, null, rel);
        this.first = first;
        this.second = second;
    }

}
