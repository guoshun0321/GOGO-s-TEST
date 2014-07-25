package jetsennet.util;

public class TwoTuple<A, B>
{

    public final A first;

    public final B second;

    public TwoTuple(A first, B second)
    {
        this.first = first;
        this.second = second;
    }

    public static <X, Y> TwoTuple<X, Y> gen(X first, Y second)
    {
        return new TwoTuple<X, Y>(first, second);
    }

}
