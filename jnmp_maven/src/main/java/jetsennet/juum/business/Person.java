package jetsennet.juum.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.juum.dataaccess.PersonDal;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.FormatUtil;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author ?
 */
public class Person
{
    /**
     * 构造函数，初始化
     */
    public Person()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        dalPerson = new PersonDal(sqlExecutor);
    }

    private ConnectionInfo uumConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private PersonDal dalPerson;

    /**
     * 新增人员
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int addPerson(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        int personId = 0;
        sqlExecutor.transBegin();
        try
        {
            personId = sqlExecutor.getNewId(PersonDal.TABLE_NAME);

            model.put(PersonDal.PRIMARY_KEY, String.valueOf(personId));
            // new java.rmi.server.UID().toString();
            dalPerson.add(model);
            dalPerson.addToGroup(personId, Integer.parseInt(model.get("GROUP_ID")));
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
        return personId;
    }

    /**
     * 更新人员
     * @param objXml 参数
     * @throws Exception 异常
     */
    public void updatePerson(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {
            dalPerson.update(model);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 删除人员
     * @param keyId 参数
     * @throws Exception 异常
     */
    public void deletePerson(int keyId) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            sqlExecutor.executeNonQuery(" DELETE FROM UUM_PERSONTOGROUP WHERE PERSON_ID = " + keyId);
            dalPerson.deleteById(keyId);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 删除人员
     * @param personXml 参数
     * @throws Exception 异常
     */
    public void deletePersons(String personXml) throws Exception
    {
        Document xmlDoc = DocumentHelper.parseText(personXml);
        Element rootNode = xmlDoc.getRootElement();

        List<Node> itemNodes = rootNode.selectNodes("Item");

        List<String> personList = new ArrayList<String>();
        for (Node itemNode : itemNodes)
        {
            personList.add(FormatUtil.tryGetItemText(itemNode, "Id", "-1"));
        }

        String[] arrPersons = new String[personList.size()];
        arrPersons = personList.toArray(arrPersons);
        String personIds = StringUtil.join(arrPersons, ",");

        sqlExecutor.transBegin();
        try
        {
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_PERSONTOGROUP",
                new SqlCondition("PERSON_ID", personIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_PERSON",
                new SqlCondition("ID", personIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));

            sqlExecutor.transCommit();

        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }
}
