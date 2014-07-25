package jetsennet.orm.util;

import jetsennet.util.TwoTuple;

public class SpecialCharOracle
{

    public static final String trans(String str)
    {
        return trans(str, false).first;
    }

    public static final TwoTuple<String, Boolean> transLikeParam(String str)
    {
        return trans(str, true);
    }

    private static final TwoTuple<String, Boolean> trans(String str, boolean isLike)
    {
        if (str == null || str.isEmpty())
        {
            return TwoTuple.gen(str, false);
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length << 2);
        boolean isLikeTrans = false;
        char[] chs = str.toCharArray();
        for (int i = 0; i < length; i++)
        {
            char ch = chs[i];
            if (ch == '\'')
            {
                sb.append(ch).append(ch);
            }
            else if (ch == '&')
            {
                sb.append("'||chr(38)||'");
            }
            else
            {
                if (isLike && (ch == '%' || ch == '_'))
                {
                    sb.append('/').append(ch);
                    isLikeTrans = true;
                }
                else
                {
                    sb.append(ch);
                }
            }
        }
        return TwoTuple.gen(sb.toString(), isLikeTrans);
    }

}
