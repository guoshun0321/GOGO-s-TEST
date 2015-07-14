/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.util;

/**
 * @author Guo
 * @param <A> 参数
 * @param <B> 参数
 * @param <C> 参数
 * @param <D> 参数
 * @param <E> 参数
 */
public class FiveTuple<A, B, C, D, E>
{

    public final A first;
    public final B second;
    public final C third;
    public final D fourth;
    public final E fifth;

    /**
     * @param first 参数
     * @param second 参数
     * @param third 参数
     * @param fourth 参数
     * @param fifth 参数
     */
    public FiveTuple(A first, B second, C third, D fourth, E fifth)
    {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }
}
