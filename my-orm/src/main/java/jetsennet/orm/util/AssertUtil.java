package jetsennet.orm.util;

public class AssertUtil
{

    public static final void assertNotNull(Object obj, String msg)
    {
        if (obj == null)
        {
            throw new UncheckedOrmException(msg);
        }
    }

}
