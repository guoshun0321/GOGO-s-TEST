/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.util;

/**
 * 通用工具类
 * @author GuoXiang
 */
public class CommonUtil
{

    /**
     * 替换掉String内的所有空格
     * @param str 参数
     * @return 结果
     */
    public static String replaceAllSpace(String str)
    {
        if (str == null)
        {
            return null;
        }
        String reg = "\\s+";
        str = str.replaceAll(reg, " ");
        return str;
    }

    /**
     * int数组中是否包含int的值
     * @param types 参数
     * @param type 参数
     * @return 结果
     */
    public static boolean containInt(int[] types, int type)
    {
        if (types == null || types.length == 0)
        {
            return false;
        }
        for (int tp : types)
        {
            if (tp == type)
            {
                return true;
            }
        }
        return false;
    }
}
