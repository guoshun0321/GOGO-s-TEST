package jetsennet.orm.sql;

import java.util.List;

public class ConditionEntryMulti extends ConditionEntry
{

    public final Object values[];

    public ConditionEntryMulti(String key, RelationshipEnum rel, Object... values)
    {
        super(key, null, rel);
        if (values.length == 1 && values[0] instanceof List<?>)
        {
            List<?> tempLst = (List<?>) values[0];
            int size = tempLst.size();
            this.values = new Object[size];
            for (int i = 0; i < size; i++)
            {
                this.values[i] = tempLst.get(i);
            }
        }
        else
        {
            this.values = values;
        }
    }

}
