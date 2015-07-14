package jetsennet.juum.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.juum.dataaccess.UserGroupDal;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.FormatUtil;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author？
 */
public class UserGroup
{
    /**
     * 构造函数，初始化
     */
    public UserGroup()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        dalUserGroup = new UserGroupDal(sqlExecutor);
    }

    private ConnectionInfo uumConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private UserGroupDal dalUserGroup;

    /**
     * 新增用户分组
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int addUserGroup(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        int groupId = 0;
        sqlExecutor.transBegin();
        try
        {
            // if (model.get("TYPE") == "0" && model.get("PARENT_ID") != "0")
            if ("0".equals(model.get("TYPE")) && !"0".equals(model.get("PARENT_ID")))
            {
                Object parentType =
                    sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand("SELECT TYPE FROM UUM_USERGROUP WHERE ID=%s",
                        new SqlValue(model.get("PARENT_ID"), SqlParamType.Numeric)));
                if (parentType == null || !"0".equals(parentType.toString()))
                {
                    throw new Exception("无效的父级分组,可能的原因是部门的父级不是部门!");
                }
            }

            groupId = sqlExecutor.getNewId(UserGroupDal.TABLE_NAME);
            model.put(UserGroupDal.PRIMARY_KEY, String.valueOf(groupId));
            // new java.rmi.server.UID().toString();
            dalUserGroup.add(model);

            String[] userIds = model.get("GROUP_USER").split(",");
            if (userIds.length > 0)
            {
                for (int i = 0; i < userIds.length; i++)
                {
                    if (userIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOGROUP (USER_ID,GROUP_ID) VALUES (%s,%s)", new SqlValue(userIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(UserGroupDal.PRIMARY_KEY), SqlParamType.Numeric)));
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

        return groupId;
    }

    /**
     * 更新用户分组
     * @param objXml 参数
     * @throws Exception 异常
     */
    public void updateUserGroup(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {
            if (model.get("ID").equals(model.get("PARENT_ID")))
            {
                throw new Exception("无效的父级分组!");
            }

            // if (model.get("TYPE") == "0" && model.get("PARENT_ID") != "0")
            if ("0".equals(model.get("TYPE")) && !"0".equals(model.get("PARENT_ID")))
            {
                Object parentType =
                    sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand("SELECT TYPE FROM UUM_USERGROUP WHERE ID=%s",
                        new SqlValue(model.get("PARENT_ID"), SqlParamType.Numeric)));
                if (parentType == null || !"0".equals(parentType.toString()))
                {
                    throw new Exception("无效的父级分组,可能的原因是部门的父级不是部门!");
                }
            }
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("DELETE FROM UUM_USERTOGROUP WHERE GROUP_ID=%s",
                new SqlValue(model.get(UserGroupDal.PRIMARY_KEY), SqlParamType.Numeric)));

            dalUserGroup.update(model);

            String[] userIds = model.get("GROUP_USER").split(",");
            if (userIds.length > 0)
            {
                for (int i = 0; i < userIds.length; i++)
                {
                    if (userIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOGROUP (USER_ID,GROUP_ID) VALUES (%s,%s)", new SqlValue(userIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(UserGroupDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            // 检测循环，如发现则抛出异常
            String parentId = model.get("PARENT_ID");
            if (parentId != null && (!"".equals(parentId)))
            {
                checkCircle(Integer.parseInt(parentId), Integer.parseInt(model.get("ID")));
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
     * 删除用户分组
     * @param keyId id
     * @throws Exception 异常
     */
    public void deleteUserGroup(int keyId) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            Object obj = sqlExecutor.executeScalar("SELECT 1 FROM UUM_USERGROUP WHERE PARENT_ID =" + keyId);
            if (obj != null)
            {
                throw new Exception("该用户组有下级组存在，请先删除其子用户组!");
            }
            else
            {
                sqlExecutor.executeNonQuery("DELETE FROM UUM_USERTOGROUP WHERE GROUP_ID = " + keyId);
                sqlExecutor.executeNonQuery("DELETE FROM UUM_USERGROUP WHERE ID = " + keyId);
            }

            dalUserGroup.deleteById(keyId);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 删除用户分组
     * @param groupXml 参数
     * @throws Exception 异常
     */
    public void deleteGroups(String groupXml) throws Exception
    {
        Document xmlDoc = DocumentHelper.parseText(groupXml);
        Element rootNode = xmlDoc.getRootElement();

        // 递归
        String recursiveDelete = FormatUtil.tryGetItemText(rootNode, "Recursive", "0");
        List<Node> itemNodes = rootNode.selectNodes("Item");

        List<String> groupList = new ArrayList<String>();
        for (Node itemNode : itemNodes)
        {
            groupList.add(FormatUtil.tryGetItemText(itemNode, "Id", "-1"));
        }
        String[] arrGroups = new String[groupList.size()];
        arrGroups = groupList.toArray(arrGroups);
        String groupIds = StringUtil.join(arrGroups, ",");

        sqlExecutor.transBegin();
        try
        {
            if ("1".equals(recursiveDelete))
            {
                groupIds = getAllGroupsByParentId(groupIds);
            }
            else
            {
                Object objItem =
                    sqlExecutor.executeScalar(sqlExecutor.getSqlParser().getSelectCommandString("UUM_USERGROUP", "1", null,
                        new SqlCondition("PARENT_ID", groupIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
                if (objItem != null)
                {
                    throw new Exception("该用户组有下级分组存在，请先删除下级分组!");
                }
            }

            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_PERSONTOGROUP",
                new SqlCondition("GROUP_ID", groupIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_USERTOGROUP",
                new SqlCondition("GROUP_ID", groupIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_USERGROUP",
                new SqlCondition("ID", groupIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));

            sqlExecutor.transCommit();

        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }

    }

    private String getAllGroupsByParentId(String parentId) throws Exception
    {
        StringBuilder sbGroups = new StringBuilder();
        sbGroups.append(parentId);

        List<String> groups =
            sqlExecutor.load(sqlExecutor.getSqlParser().getSelectCommandString("UUM_USERGROUP", "ID", "",
                new SqlCondition("PARENT_ID", parentId, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
                new SqlCondition("ID", "0", SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric)), true);

        if (groups.size() > 0)
        {
            sbGroups.append(",");
            String[] arrGroups = new String[groups.size()];
            arrGroups = groups.toArray(arrGroups);
            sbGroups.append(getAllGroupsByParentId(StringUtil.join(arrGroups, ",")));
        }
        return sbGroups.toString();
    }

    private void checkCircle(int compareId, int typeId) throws Exception
    {
        List<String> groups =
            sqlExecutor.load(sqlExecutor.getSqlParser().getSelectCommandString("UUM_USERGROUP", "ID", "",
                new SqlCondition("PARENT_ID", Integer.toString(typeId), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
                new SqlCondition("ID", "0", SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric)), true);

        for (String at : groups)
        {
            if (Integer.parseInt(at) == compareId)
            {
                throw new Exception("不能创建循环的组关系!");
            }
            checkCircle(compareId, Integer.parseInt(at));
        }
    }
}
