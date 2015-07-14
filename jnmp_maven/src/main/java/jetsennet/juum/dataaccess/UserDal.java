package jetsennet.juum.dataaccess;

import java.util.ArrayList;
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
import jetsennet.util.StringUtil;

/**
 * @author？
 */
public class UserDal extends DataAccessBase
{
    // / <summary>
    // / Database Table Name
    // / </summary>
    public final static String TABLE_NAME = "UUM_USER";
    // / <summary>
    // / Table KEY Name
    // / </summary>
    public final static String PRIMARY_KEY = "ID";
    // / <summary>
    // / new instance
    // / </summary>
    // / <param name="sqlExecute">sqlExecute</param>
    /**
     * @param sqlExecutor 参数
     */
    public UserDal(ISqlExecutor sqlExecutor)
    {
        super(sqlExecutor);
    }

    // / <summary>
    // / Inesrt User Record
    // / </summary>
    // / <param name="model">model</param>
    /**
     * @param model 参数
     * @throws Exception 异常
     */
    public void add(HashMap<String, String> model) throws Exception
    {
        SqlField[] param =
            new SqlField[] { new SqlField("BIRTHDAY", model.get("BIRTHDAY"), SqlParamType.DateTime),
                new SqlField("ID", model.get("ID"), SqlParamType.Numeric), new SqlField("LOGIN_NAME", model.get("LOGIN_NAME")),
                new SqlField("USER_NAME", model.get("USER_NAME")), new SqlField("PASSWORD", model.get("PASSWORD")),
                new SqlField("USER_CARD", model.get("USER_CARD")), new SqlField("USER_CODE", model.get("USER_CODE")),
                new SqlField("USER_TYPE", model.get("USER_TYPE"), SqlParamType.Numeric),
                new SqlField("PERSON_ID", model.get("PERSON_ID"), SqlParamType.Numeric),
                new SqlField("RIGHT_LEVEL", model.get("RIGHT_LEVEL"), SqlParamType.Numeric),
                new SqlField("EMAIL", model.get("EMAIL"), SqlParamType.String), new SqlField("SEX", model.get("SEX"), SqlParamType.Numeric),
                new SqlField("ADDRESS", model.get("ADDRESS")), new SqlField("DUTY_TITLE", model.get("DUTY_TITLE")),
                new SqlField("OFFICE_PHONE", model.get("OFFICE_PHONE")), new SqlField("HOME_PHONE", model.get("HOME_PHONE")),
                new SqlField("MOBILE_PHONE", model.get("MOBILE_PHONE")), new SqlField("HOME_PATH", model.get("HOME_PATH")),
                new SqlField("QUOTA_SIZE", model.get("QUOTA_SIZE"), SqlParamType.Numeric),
                new SqlField("QUOTA_USED", model.get("QUOTA_USED"), SqlParamType.Numeric), new SqlField("APP_PARAM", model.get("APP_PARAM")),
                new SqlField("DESCRIPTION", model.get("DESCRIPTION")), new SqlField("CREATE_TIME", new Date(), SqlParamType.DateTime),
                new SqlField("STATE", model.get("STATE"), SqlParamType.Numeric), new SqlField("FIELD_1", model.get("FIELD_1")),
                new SqlField("FIELD_2", model.get("FIELD_2")), new SqlField("FIELD_3", model.get("FIELD_3")),
                new SqlField("FIELD_4", model.get("FIELD_4")), new SqlField("FIELD_5", model.get("FIELD_5")),
                new SqlField("PATH_SIZE", model.get("PATH_SIZE"), SqlParamType.Numeric) };
        ArrayList paras = new ArrayList();
        paras.addAll(Arrays.asList(param));
        if (StringUtil.isNullOrEmpty(model.get("BIRTHDAY")))
        {
            paras.remove(0);
        }
        getSqlExecutor().executeNonQuery(getSqlParser().getInsertCommandString(TABLE_NAME, paras)); // Arrays.asList(param)));
    }

    // / <summary>
    // / Update User Record
    // / </summary>
    // / <param name="model">model</param>
    /**
     * @param model 参数
     * @throws Exception 异常
     */
    public void update(HashMap<String, String> model) throws Exception
    {
        SqlField[] param =
            new SqlField[] { SqlField.tryCreate("LOGIN_NAME", model.get("LOGIN_NAME")), SqlField.tryCreate("USER_NAME", model.get("USER_NAME")),
                SqlField.tryCreate("PASSWORD", model.get("PASSWORD")), SqlField.tryCreate("USER_CARD", model.get("USER_CARD")),
                SqlField.tryCreate("USER_CODE", model.get("USER_CODE")),
                SqlField.tryCreate("USER_TYPE", model.get("USER_TYPE"), SqlParamType.Numeric),
                SqlField.tryCreate("PERSON_ID", model.get("PERSON_ID"), SqlParamType.Numeric),
                SqlField.tryCreate("RIGHT_LEVEL", model.get("RIGHT_LEVEL"), SqlParamType.Numeric),
                SqlField.tryCreate("EMAIL", model.get("EMAIL"), SqlParamType.String),
                SqlField.tryCreate("SEX", model.get("SEX"), SqlParamType.Numeric), SqlField.tryCreate("ADDRESS", model.get("ADDRESS")),
                SqlField.tryCreate("BIRTHDAY", model.get("BIRTHDAY"), SqlParamType.DateTime),
                SqlField.tryCreate("DUTY_TITLE", model.get("DUTY_TITLE")), SqlField.tryCreate("OFFICE_PHONE", model.get("OFFICE_PHONE")),
                SqlField.tryCreate("HOME_PHONE", model.get("HOME_PHONE")), SqlField.tryCreate("MOBILE_PHONE", model.get("MOBILE_PHONE")),
                SqlField.tryCreate("HOME_PATH", model.get("HOME_PATH")),
                SqlField.tryCreate("QUOTA_SIZE", model.get("QUOTA_SIZE"), SqlParamType.Numeric),
                SqlField.tryCreate("QUOTA_USED", model.get("QUOTA_USED"), SqlParamType.Numeric),
                SqlField.tryCreate("APP_PARAM", model.get("APP_PARAM")), SqlField.tryCreate("DESCRIPTION", model.get("DESCRIPTION")),
                SqlField.tryCreate("STATE", model.get("STATE"), SqlParamType.Numeric), SqlField.tryCreate("FIELD_1", model.get("FIELD_1")),
                SqlField.tryCreate("FIELD_2", model.get("FIELD_2")), SqlField.tryCreate("FIELD_3", model.get("FIELD_3")),
                SqlField.tryCreate("FIELD_4", model.get("FIELD_4")), SqlField.tryCreate("FIELD_5", model.get("FIELD_5")),
                SqlField.tryCreate("PATH_SIZE", model.get("PATH_SIZE"), SqlParamType.Numeric),
                new SqlField("UPDATE_TIME", new Date(), SqlParamType.DateTime) };
        SqlCondition p = new SqlCondition(PRIMARY_KEY, model.get(PRIMARY_KEY), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);

        getSqlExecutor().executeNonQuery(getSqlParser().getUpdateCommandString(TABLE_NAME, Arrays.asList(param), p));
    }

    // / <summary>
    // / Update User Record
    // / </summary>
    // / <param name="updateItems">Update Info Items</param>
    // / <param name="p">Condition</param>
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

    // / <summary>
    // / Delete User Records
    // / </summary>
    // / <param name="pID">pID</param>
    /**
     * @param keyId 参数
     * @throws Exception 异常
     */
    public void deleteById(int keyId) throws Exception
    {
        SqlCondition p = new SqlCondition(PRIMARY_KEY, String.valueOf(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        delete(p);
    }

    // / <summary>
    // / Delete User Records
    // / </summary>
    // / <param name="p">Search Condition</param>
    /**
     * @param p 参数
     * @throws Exception 异常
     */
    public void delete(SqlCondition... p) throws Exception
    {
        getSqlExecutor().executeNonQuery(getSqlParser().getDeleteCommandString(TABLE_NAME, p));
    }
}
