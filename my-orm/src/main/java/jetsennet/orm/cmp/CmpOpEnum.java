package jetsennet.orm.cmp;

public enum CmpOpEnum
{

    INSERT, UPDATE, DELETE, SELECT, NONE;

    public static CmpOpEnum ignoreCaseValueOf(String type)
    {
        return valueOf(type.toUpperCase());
    }

}
