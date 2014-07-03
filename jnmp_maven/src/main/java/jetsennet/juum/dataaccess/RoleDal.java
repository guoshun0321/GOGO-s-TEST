package jetsennet.juum.dataaccess;

import java.util.Arrays;
import java.util.Date;
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
public class RoleDal extends DataAccessBase
{

    public final static String TABLE_NAME = "UUM_ROLE";

    public final static String PRIMARY_KEY = "ID";

    /**
     * @param sqlExecutor 参数
     */
    public RoleDal(ISqlExecutor sqlExecutor)
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
            new SqlField[] { new SqlField("ID", model.get("ID"), SqlParamType.Numeric), new SqlField("NAME", model.get("NAME")),
                new SqlField("CREATE_TIME", new Date(), SqlParamType.DateTime), new SqlField("DESCRIPTION", model.get("DESCRIPTION")),
                new SqlField("TYPE", model.get("TYPE"), SqlParamType.Numeric) };
        getSqlExecutor().executeNonQuery(getSqlParser().getInsertCommandString(TABLE_NAME, Arrays.asList(param)));
    }

    /**
     * @param model 参数
     * @throws Exception 异常
     */
    public void update(HashMap<String, String> model) throws Exception
    {
        SqlField[] param =
            new SqlField[] { SqlField.tryCreate("NAME", model.get("NAME")), SqlField.tryCreate("DESCRIPTION", model.get("DESCRIPTION")),
                SqlField.tryCreate("TYPE", model.get("TYPE"), SqlParamType.Numeric) };
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
