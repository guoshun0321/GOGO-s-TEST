/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 工具类，当函数需要返回三个对象时使用
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

/**
 * @author 郭祥
 * @param <A> 参数
 * @param <B> 参数
 * @param <C> 参数
 */
public class ThreeTuple<A, B, C>
{

    public A first;
    public B second;
    public C third;

    /**
     * @param first 参数
     * @param second 参数
     * @param third 参数
     */
    public ThreeTuple(A first, B second, C third)
    {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
