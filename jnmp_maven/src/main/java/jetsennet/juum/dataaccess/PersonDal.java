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
 * @author ？
 */
public class PersonDal extends DataAccessBase
{
    public final static String TABLE_NAME = "UUM_PERSON";
    public final static String PRIMARY_KEY = "ID";

    /**
     * @param sqlExecutor 参数
     */
    public PersonDal(ISqlExecutor sqlExecutor)
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
                new SqlField("SEX", model.get("SEX"), SqlParamType.Numeric), new SqlField("DUTY_TITLE", model.get("DUTY_TITLE")),
                new SqlField("ADDRESS", model.get("ADDRESS")), new SqlField("EMAIL", model.get("EMAIL")),
                new SqlField("OFFICE_PHONE", model.get("OFFICE_PHONE")), new SqlField("HOME_PHONE", model.get("HOME_PHONE")),
                new SqlField("MOBILE_PHONE", model.get("MOBILE_PHONE")), new SqlField("RIGHT_LEVEL", model.get("RIGHT_LEVEL"), SqlParamType.Numeric),
                new SqlField("JOIN_DATE", model.get("JOIN_DATE"), SqlParamType.DateTime),
                new SqlField("CREATE_TIME", new Date(), SqlParamType.DateTime),
                new SqlField("BIRTHDAY", model.get("BIRTHDAY"), SqlParamType.DateTime), new SqlField("DESCRIPTION", model.get("DESCRIPTION")),
                new SqlField("USER_CARD", model.get("USER_CARD")), new SqlField("USER_CODE", model.get("USER_CODE")),
                new SqlField("STATE", model.get("STATE"), SqlParamType.Numeric), new SqlField("FIELD_1", model.get("FIELD_1")),
                new SqlField("FIELD_2", model.get("FIELD_2")) };
        getSqlExecutor().executeNonQuery(getSqlParser().getInsertCommandString(TABLE_NAME, Arrays.asList(param)));

    }

    /**
     * @param personId 参数
     * @param groupId 对象组id
     * @throws Exception 异常
     */
    public void addToGroup(int personId, int groupId) throws Exception
    {
        SqlField[] param =
            new SqlField[] { new SqlField("PERSON_ID", personId, SqlParamType.Numeric), new SqlField("GROUP_ID", groupId, SqlParamType.Numeric) };
        getSqlExecutor().executeNonQuery(getSqlParser().getInsertCommandString("UUM_PERSONTOGROUP", Arrays.asList(param)));
    }

    /**
     * @param model 参数
     * @throws Exception 更新
     */
    public void update(HashMap<String, String> model) throws Exception
    {
        SqlField[] param =
            new SqlField[] { SqlField.tryCreate("NAME", model.get("NAME")), SqlField.tryCreate("SEX", model.get("SEX"), SqlParamType.Numeric),
                SqlField.tryCreate("DUTY_TITLE", model.get("DUTY_TITLE")), SqlField.tryCreate("ADDRESS", model.get("ADDRESS")),
                SqlField.tryCreate("EMAIL", model.get("EMAIL")), SqlField.tryCreate("OFFICE_PHONE", model.get("OFFICE_PHONE")),
                SqlField.tryCreate("HOME_PHONE", model.get("HOME_PHONE")), SqlField.tryCreate("MOBILE_PHONE", model.get("MOBILE_PHONE")),
                SqlField.tryCreate("RIGHT_LEVEL", model.get("RIGHT_LEVEL"), SqlParamType.Numeric),
                SqlField.tryCreate("JOIN_DATE", model.get("JOIN_DATE"), SqlParamType.DateTime),
                SqlField.tryCreate("BIRTHDAY", model.get("BIRTHDAY"), SqlParamType.DateTime),
                SqlField.tryCreate("DESCRIPTION", model.get("DESCRIPTION")), SqlField.tryCreate("USER_CARD", model.get("USER_CARD")),
                SqlField.tryCreate("USER_CODE", model.get("USER_CODE")), SqlField.tryCreate("STATE", model.get("STATE"), SqlParamType.Numeric),
                SqlField.tryCreate("FIELD_1", model.get("FIELD_1")), SqlField.tryCreate("FIELD_2", model.get("FIELD_2")) };
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
