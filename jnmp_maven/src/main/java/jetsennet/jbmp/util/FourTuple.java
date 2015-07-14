package jetsennet.jbmp.util;

/**
 * @author Guo
 * @param <A> 参数
 * @param <B> 参数
 * @param <C> 参数
 * @param <D> 参数
 */
public class FourTuple<A, B, C, D>
{

    public final A first;
    public final B second;
    public final C third;
    public final D fourth;

    /**
     * @param first 参数
     * @param second 参数
     * @param third 参数
     * @param fourth 参数
     */
    public FourTuple(A first, B second, C third, D fourth)
    {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }
}
