package jetsennet.juum.dataaccess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jetsennet.sqlclient.DataAccessBase;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author？
 */
public class PersonGroupDal extends DataAccessBase
{
    public final static String TABLE_NAME = "UUM_PERSONTOGROUP";

    public final static String PRIMARY_KEY = "PERSON_ID";

    /**
     * @param sqlExecutor 参数
     */
    public PersonGroupDal(ISqlExecutor sqlExecutor)
    {
        super(sqlExecutor);
    }

    /**
     * @param model 参数
     * @throws Exception 异常
     */
    public void add(HashMap<String, String> model) throws Exception
    {
        SqlField[] param =
            new SqlField[] { new SqlField("PERSON_ID", model.get("PERSON_ID"), SqlParamType.Numeric),
                new SqlField("GROUP_ID", model.get("GROUP_ID"), SqlParamType.Numeric) };
        getSqlExecutor().executeNonQuery(getSqlParser().getInsertCommandString(TABLE_NAME, Arrays.asList(param)));
    }

    /**
     * @param model 参数
     * @throws Exception 异常
     */
    public void update(HashMap<String, String> model) throws Exception
    {
        SqlField[] param =
            new SqlField[] { SqlField.tryCreate("PERSON_ID", model.get("PERSON_ID"), SqlParamType.Numeric),
                SqlField.tryCreate("GROUP_ID", model.get("GROUP_ID"), SqlParamType.Numeric) };
        SqlCondition p = new SqlCondition(PRIMARY_KEY, model.get(PRIMARY_KEY), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);

        getSqlExecutor().executeNonQuery(getSqlParser().getUpdateCommandString(TABLE_NAME, Arrays.asList(param), p));
    }

    /**
     * @param updateItems 参数
     * @param p 参数
     * @throws Exception 异常
     */
    public void update(List<SqlField> updateItems, SqlCondition... p) throws Exception
    {
        if (updateItems == null)
        {
            return;
        }
        getSqlExecutor().executeNonQuery(getSqlParser().getUpdateCommandString(TABLE_NAME, updateItems, p));
    }

    /**
     * @param keyId 参数
     * @throws Exception 异常
     */
    public void deleteById(int keyId) throws Exception
    {
        SqlCondition p = new SqlCondition(PRIMARY_KEY, String.valueOf(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        delete(p);
    }

    /**
     * @param p 参数
     * @throws Exception 异常
     */
    public void delete(SqlCondition... p) throws Exception
    {
        getSqlExecutor().executeNonQuery(getSqlParser().getDeleteCommandString(TABLE_NAME, p));
    }
}
