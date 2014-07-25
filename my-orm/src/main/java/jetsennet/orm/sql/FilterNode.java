package jetsennet.orm.sql;

public class FilterNode
{

    /**
     * 节点类型。关系型（AND/OR），条件型
     */
    public final int type;
    /**
     * 子节点
     */
    private FilterNode[] children;
    /**
     * 条件
     */
    private ConditionEntry cond;

    public static final int TYPE_REL_AND = 0;

    public static final int TYPE_REL_OR = 1;

    public static final int TYPE_COND = 2;

    public FilterNode(FilterNode[] children, int type)
    {
        this.type = type;
        this.children = children;
    }

    public FilterNode(ConditionEntry cond)
    {
        this.cond = cond;
        this.type = TYPE_COND;
    }

    public final void add(FilterNode node)
    {
        FilterNode[] temp = null;
        if (children == null)
        {
            temp = new FilterNode[1];
            temp[0] = node;
        }
        else
        {
            temp = new FilterNode[children.length + 1];
            System.arraycopy(children, 0, temp, 0, children.length);
            temp[temp.length - 1] = node;
        }
        this.children = temp;
    }

    public final ConditionEntry getCond()
    {
        return cond;
    }

    public final FilterNode[] getChildren()
    {
        return children;
    }

    public final FilterNode and(FilterNode node)
    {
        FilterNode retval = new FilterNode(null, FilterNode.TYPE_REL_AND);
        retval.add(this);
        retval.add(node);
        return retval;
    }

    public final FilterNode or(FilterNode node)
    {
        FilterNode retval = new FilterNode(null, FilterNode.TYPE_REL_OR);
        retval.add(this);
        retval.add(node);
        return retval;
    }

}
