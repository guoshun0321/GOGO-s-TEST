package jetsennet.orm.sql;

/**
 * 数据库语句枚举
 * 
 * @author 郭祥
 */
public enum SqlTypeEnum
{

    INSERT, UPDATE, DELETE, SELECT;

    public static SqlTypeEnum valueOfIgnoreCase(String str)
    {
        str = str.toUpperCase();
        return SqlTypeEnum.valueOf(str);
    }

}
