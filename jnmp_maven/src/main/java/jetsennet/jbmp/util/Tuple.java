package jetsennet.jbmp.util;

/**
 * @author？
 */
public class Tuple
{

    /**
     * @param <A> 参数
     * @param <B> 参数
     * @param a 参数
     * @param b 参数
     * @return 结果
     */
    public static <A, B> TwoTuple<A, B> tuple(A a, B b)
    {
        return new TwoTuple<A, B>(a, b);
    }

}
