package jetsennet.orm.sql;


public class FilterUtil
{

    public static final FilterNode and(FilterNode... nodes)
    {
        return new FilterNode(nodes, FilterNode.TYPE_REL_AND);
    }

    public static final FilterNode or(FilterNode... nodes)
    {
        return new FilterNode(nodes, FilterNode.TYPE_REL_OR);
    }

    public static final FilterNode eq(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.Equal));
    }

    public static final FilterNode noeq(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.NotEqual));
    }

    public static final FilterNode nu(String key)
    {
        return new FilterNode(new ConditionEntry(key, null, RelationshipEnum.IsNull));
    }

    public static final FilterNode nonu(String key)
    {
        return new FilterNode(new ConditionEntry(key, null, RelationshipEnum.IsNotNull));
    }

    public static final FilterNode th(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.Than));
    }

    public static final FilterNode ls(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.Less));
    }

    public static final FilterNode theq(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.ThanEqual));
    }

    public static final FilterNode lseq(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.LessEqual));
    }

    public static final FilterNode lk(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.Like));
    }

    public static final FilterNode nolk(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.NotLike));
    }

    public static final FilterNode ilk(String key, Object value)
    {
        return new FilterNode(new ConditionEntry(key, value, RelationshipEnum.ILike));
    }

    public static final FilterNode in(String key, Object... values)
    {
        return new FilterNode(new ConditionEntryMulti(key, RelationshipEnum.In, values));
    }

    public static final FilterNode noin(String key, Object... values)
    {
        return new FilterNode(new ConditionEntryMulti(key, RelationshipEnum.NotIn, values));
    }

    public static final FilterNode bt(String key, Object first, Object second)
    {
        return new FilterNode(new ConditionEntryDuple(key, first, second, RelationshipEnum.Between));
    }

    public static final FilterNode ex(String value)
    {
        return new FilterNode(new ConditionEntry(null, value, RelationshipEnum.Exists));
    }

    public static final FilterNode noex(String value)
    {
        return new FilterNode(new ConditionEntry(null, value, RelationshipEnum.NotExists));
    }

}
