package jetsennet.orm.test.util;

import java.util.Date;

import jetsennet.util.SafeDateFormater;

public class StringUtil
{

    public static final String printArray(Object[] objs)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object obj : objs)
        {
            if (obj instanceof Date)
            {
                obj = SafeDateFormater.format((Date) obj);
            }
            sb.append(obj).append(", ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

}
