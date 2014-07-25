package jetsennet.orm.sql;

public enum RelationshipEnum
{

    Equal, NotEqual, IsNull, IsNotNull, // 相等性比较
    Than, Less, ThanEqual, LessEqual, // 大于小于
    Like, NotLike, ILike, In, NotIn, Exists, NotExists, Between, // 区间
    Custom, CustomLike, IEqual; // 自定义

}
