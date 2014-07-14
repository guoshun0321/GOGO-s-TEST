package jetsennet.jbmp.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成泛型数组
 * @author GuoXiang
 */
public class ArrayUtils
{

    /**
     * 根据数组类型的class创建对应类型的数组
     * @param <T> 目标类型
     * @param clazz 参数
     * @param length 数组长度
     * @return 结果
     */
    public static <T> T[] newArrayByArrayClass(Class<T[]> clazz, int length)
    {
        return (T[]) Array.newInstance(clazz.getComponentType(), length);
    }

    /**
     * 根据普通类型的class创建数组
     * @param <T> 目标类型
     * @param clazz 参数
     * @param length 数组长度
     * @return 结果
     */
    public static <T> T[] newArrayByClass(Class<T> clazz, int length)
    {
        return (T[]) Array.newInstance(clazz, length);
    }

    /**
     * 将Integer[]数组转化为int[]
     * @param arr 数组
     * @return 结果
     */
    public static int[] toIntArray(Integer[] arr)
    {
        if (arr == null)
        {
            return null;
        }

        int len = arr.length;
        int[] result = new int[len];

        for (int i = 0; i < len; i++)
        {
            result[i] = arr[i];
        }

        return result;
    }

    /**
     * 将String[]转为int[]
     * @param arr 数组
     * @return 结果
     */
    public static int[] stringToIntArray(String[] arr)
    {
        if (arr == null)
        {
            return null;
        }

        int len = arr.length;
        int[] result = new int[len];

        for (int i = 0; i < len; i++)
        {
            result[i] = ConvertUtil.stringToInt(arr[i]);
        }

        return result;
    }

    /**
     * 将String[]转为ArrayList<String>
     * @param arr 数组
     * @return 结果
     */
    public static ArrayList<String> stringToStringArrayList(String[] arr)
    {
        if (arr == null)
        {
            return null;
        }

        int len = arr.length;
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < len; i++)
        {
            result.add(arr[i]);
        }

        return result;
    }

    /**
     * 将List<Integer>转化为int[]
     * @param list 数组
     * @return 结果
     */
    public static int[] listToIntArray(List<Integer> list)
    {
        if (list == null)
        {
            return null;
        }

        int len = list.size();
        int[] result = new int[len];

        for (int i = 0; i < len; i++)
        {
            result[i] = list.get(i);
        }

        return result;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        // 判断一个Class是否是数组类型，可以用Class实例的isArray方法。
        String[] byArray = newArrayByArrayClass(String[].class, 10);
        String[] byOne = newArrayByClass(String.class, 10);
    }
}
