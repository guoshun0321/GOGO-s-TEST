package jetsennet.juum.business;

import java.util.HashMap;

import jetsennet.juum.dataaccess.PersonGroupDal;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.SerializerUtil;

/**
 * @author？
 */
public class PersonGroup
{
    /**
     * 构造函数，初始化
     */
    public PersonGroup()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        dalPersonGroup = new PersonGroupDal(sqlExecutor);
    }

    private ConnectionInfo uumConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private PersonGroupDal dalPersonGroup;

    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    public void addPersonGroup(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {
            model.put(PersonGroupDal.PRIMARY_KEY, String.valueOf(sqlExecutor.getNewId(PersonGroupDal.TABLE_NAME)));
            // new java.rmi.server.UID().toString();
            dalPersonGroup.add(model);

            String[] personIds = model.get("PERSONTOGROUP").split(",");
            if (personIds.length > 0)
            {
                for (int i = 0; i < personIds.length; i++)
                {
                    if (personIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_PERSONTOGROUP (PERSON_ID,GROUP_ID) VALUES (%s,%s)", new SqlValue(personIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(PersonGroupDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    public void updatePersonGroup(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {
            dalPersonGroup.update(model);

            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("DELETE FROM UUM_PERSONTOGROUP WHERE GROUP_ID=%s",
                new SqlValue(model.get(PersonGroupDal.PRIMARY_KEY), SqlParamType.Numeric)));

            String[] personIds = model.get("PERSONTOGROUP").split(",");
            if (personIds.length > 0)
            {
                for (int i = 0; i < personIds.length; i++)
                {
                    if (personIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(String.format("INSERT INTO UUM_PERSONTOGROUP (PERSON_ID,GROUP_ID) VALUES (%s,%s)", new SqlValue(
                            personIds[i], SqlParamType.Numeric), new SqlValue(model.get(PersonGroupDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * @param keyId 参数
     * @throws Exception 异常
     */
    public void deletePersonGroup(int keyId) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            Object obj = sqlExecutor.executeScalar("SELECT ID FROM UUM_PERSONGROUP WHERE PARENT_ID =" + keyId);
            if (obj != null)
            {
                throw new Exception("该组有下级组存在，请先删除其子组!");
            }
            sqlExecutor.executeNonQuery(" DELETE FROM UUM_PERSONTOGROUP WHERE GROUP_ID = " + keyId);
            dalPersonGroup.deleteById(keyId);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }
}
