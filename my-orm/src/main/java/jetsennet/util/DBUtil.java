/**
 * 日 期: 2014年5月14日
 * 作 者: 梁洪杰
 * 版 本: v2.1
 * 描 述: DBUtil.java
 * 历 史: 2014年5月14日 创建
 */
package jetsennet.util;

import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

import org.apache.log4j.Logger;

/**
 * db操作工具
 */
public class DBUtil
{
    private static final Logger logger = Logger.getLogger(DBUtil.class);

    /**
     * 获取相等条件
     * @param field 列
     * @param value 值
     * @return sql条件
     */
    public static SqlCondition getECond(String field, long value)
    {
        return new SqlCondition(field, String.valueOf(value), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
    }

    /**
     * 获取相等条件
     * @param field 列
     * @param value 值
     * @return sql条件
     */
    public static SqlCondition getECond(String field, int value)
    {
        return new SqlCondition(field, String.valueOf(value), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
    }

    /**
     * 获取相等条件
     * @param field 列
     * @param value 值
     * @return sql条件
     */
    public static SqlCondition getECond(String field, String value)
    {
        return new SqlCondition(field, value, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
    }

    /**
     * 获取in条件
     * @param field 列
     * @param value 值
     * @return sql条件
     */
    public static SqlCondition getInCond(String field, String value)
    {
        return new SqlCondition(field, value, SqlLogicType.And, SqlRelationType.In, SqlParamType.String);
    }
}
