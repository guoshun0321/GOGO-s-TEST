package jetsennet.orm.util;

public class SqlUtil
{

    /**
     * 找到第一个不在括号的search字符串的位置。忽略大小写。
     * 
     * @param sb
     * @param search
     * @param fromIndex
     * @return
     */
    public static int shallowIndexOf(String str, String search, int fromIndex)
    {
        final String upperCase = str.toUpperCase();
        search = search.toUpperCase();

        final int len = upperCase.length();
        final int searchLen = search.length();
        int pos = -1, depth = 0, cur = fromIndex;
        do
        {
            pos = upperCase.indexOf(search, cur);
            if (pos != -1)
            {
                for (int iter = cur; iter < pos; iter++)
                {
                    char c = upperCase.charAt(iter);
                    if (c == '(')
                    {
                        depth = depth + 1;
                    }
                    else if (c == ')')
                    {
                        depth = depth - 1;
                    }
                }
                cur = pos + searchLen;
            }
        }
        while (cur < len && depth != 0 && pos != -1);
        return depth == 0 ? pos : -1;
    }

    public static String shallowReplace(String str, String search, int fromIndex, String rep)
    {
        String retval = str;
        int pos = shallowIndexOf(str, search, fromIndex);
        if (pos >= 0)
        {
            StringBuilder sb = new StringBuilder(str.length() + rep.length());
            sb.append(str.substring(0, pos));
            sb.append(rep);
            sb.append(str.substring(pos + search.length()));
            retval = sb.toString();
        }
        return retval;
    }

    public static String shallowRemoveOrderBy(String str, int fromIndex)
    {
        String retval = str;
        String search = "ORDER BY";
        int pos = shallowIndexOf(str, search, fromIndex);
        if (pos >= 0)
        {
            StringBuilder sb = new StringBuilder(str.length());
            sb.append(str.substring(0, pos));
            retval = sb.toString();
        }
        return retval;
    }

    public static void main(String[] args)
    {
        String sql = "select * from (select * from all_type order by id1) order by id2";
        sql = SqlUtil.shallowRemoveOrderBy(sql, 0);
        System.out.println(sql);
    }

}
