package jetsennet.juum.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.juum.dataaccess.RoleDal;
import jetsennet.juum.dataaccess.UserDal;
import jetsennet.juum.dataaccess.UserGroupDal;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.ConfigUtil;
import jetsennet.util.EncryptUtil;
import jetsennet.util.FormatUtil;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author？
 */
public class User
{
    private ConnectionInfo uumConnectionInfo;
    private String encryptUserPassword = ConfigUtil.getProperty("EncryptUserPassword");

    /**
     * 构造函数，初始化
     */
    public User()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));

        sqlExecutor = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        dal = new UserDal(sqlExecutor);
        dalUserGroup = new UserGroupDal(sqlExecutor);
        dalRole = new RoleDal(sqlExecutor);
    }

    private ISqlExecutor sqlExecutor;
    private UserDal dal;
    private UserGroupDal dalUserGroup;
    private RoleDal dalRole;

    /**
     * 新增用户
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int addUser(String objXml) throws Exception
    {

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        int newId = 0;
        sqlExecutor.transBegin();
        try
        {
            Object existUser =
                sqlExecutor.executeScalar(sqlExecutor.getSqlParser().getSelectCommandString("UUM_USER", "1", null,
                    new SqlCondition("LOGIN_NAME", model.get("LOGIN_NAME"), SqlLogicType.And, SqlRelationType.IEqual, SqlParamType.String)));
            if (existUser != null)
            {
                throw new Exception("用户帐号已存在!");
            }

            newId = sqlExecutor.getNewId(UserDal.TABLE_NAME);
            model.put(UserDal.PRIMARY_KEY, String.valueOf(newId));
            // if (encryptUserPassword != "0")
            if (!"0".equals(encryptUserPassword))
            {
                model.put("PASSWORD", EncryptUtil.md5Encrypt(model.get("PASSWORD")));
            }

            dal.add(model);

            // user group
            String[] groupIds = model.get("GROUP_USER").split(",");
            if (groupIds.length > 0)
            {
                for (int i = 0; i < groupIds.length; i++)
                {
                    if (groupIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOGROUP (USER_ID,GROUP_ID) VALUES (%s,%s)",
                            new SqlValue(model.get(UserDal.PRIMARY_KEY), SqlParamType.Numeric), new SqlValue(groupIds[i], SqlParamType.Numeric)));
                    }
                }
            }

            // user role
            String[] roleIds = model.get("ROLE_USER").split(",");
            if (roleIds.length > 0)
            {
                for (int i = 0; i < roleIds.length; i++)
                {
                    if (roleIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOROLE (USER_ID,ROLE_ID) VALUES (%s,%s)",
                            new SqlValue(model.get(UserDal.PRIMARY_KEY), SqlParamType.Numeric), new SqlValue(roleIds[i], SqlParamType.Numeric)));
                    }
                }
            }

            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            sqlExecutor.transRollback();
            throw ex;
        }

        return newId;
    }

    /**
     * 更新用户
     * @param objXml 参数
     * @throws Exception 异常
     */
    public void updateUser(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {

            if ("1".equals(model.get("MODIFY_PW")))
            {
                // if (encryptUserPassword != "0")
                if (!"0".equals(encryptUserPassword))
                {
                    model.put("PASSWORD", EncryptUtil.md5Encrypt(model.get("PASSWORD")));
                }
            }
            else
            {
                model.remove("PASSWORD");
            }
            dal.update(model);

            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("DELETE FROM UUM_USERTOGROUP WHERE USER_ID=%S",
                new SqlValue(model.get(UserDal.PRIMARY_KEY), SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("DELETE FROM UUM_USERTOROLE WHERE USER_ID=%s",
                new SqlValue(model.get(UserDal.PRIMARY_KEY), SqlParamType.Numeric)));

            String[] groupIds = model.get("GROUP_USER").split(",");
            if (groupIds.length > 0)
            {
                for (int i = 0; i < groupIds.length; i++)
                {
                    if (groupIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOGROUP (USER_ID,GROUP_ID) VALUES (%s,%s)",
                            new SqlValue(model.get(UserDal.PRIMARY_KEY), SqlParamType.Numeric), new SqlValue(groupIds[i], SqlParamType.Numeric)));
                    }
                }
            }

            String[] roleIds = model.get("ROLE_USER").split(",");
            if (roleIds.length > 0)
            {
                for (int i = 0; i < roleIds.length; i++)
                {
                    if (roleIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOROLE (USER_ID,ROLE_ID) VALUES (%s,%S)",
                            new SqlValue(model.get(UserDal.PRIMARY_KEY), SqlParamType.Numeric), new SqlValue(roleIds[i], SqlParamType.Numeric)));
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
     * @param updateItems 参数
     * @param p 参数
     * @throws Exception 异常
     */
    public void updateUser(List<SqlField> updateItems, SqlCondition... p) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            dal.update(updateItems, p);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * @param pID 参数
     * @throws Exception 异常
     */
    public void deleteUserById(int pID) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            sqlExecutor.executeNonQuery("DELETE FROM UUM_USERTOROLE WHERE USER_ID = " + pID);
            sqlExecutor.executeNonQuery("DELETE FROM UUM_USERTOGROUP WHERE USER_ID = " + pID);

            dal.deleteById(pID);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 删除用户
     * @param userXml 参数
     * @throws Exception 异常
     */
    public void deleteUsers(String userXml) throws Exception
    {
        Document xmlDoc = DocumentHelper.parseText(userXml);
        Element rootNode = xmlDoc.getRootElement();

        List<Node> itemNodes = rootNode.selectNodes("Item");

        List<String> userList = new ArrayList<String>();
        for (Node itemNode : itemNodes)
        {
            String userId = FormatUtil.tryGetItemText(itemNode, "Id", "-1");
            // if (userId != "1")
            if (!"1".equals(userId))
            {
                userList.add(userId);
            }
        }

        if (userList.size() == 0)
        {
            return;
        }

        String[] arrUsers = new String[userList.size()];
        arrUsers = userList.toArray(arrUsers);
        String userIds = StringUtil.join(arrUsers, ",");

        sqlExecutor.transBegin();
        try
        {
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_USERTOROLE",
                new SqlCondition("USER_ID", userIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_USERTOGROUP",
                new SqlCondition("USER_ID", userIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_USER",
                new SqlCondition("ID", userIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));

            sqlExecutor.transCommit();

        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 修改个人资料
     * @param model 参数
     * @throws Exception 异常
     */
    public void modifyUserInfo(HashMap<String, String> model) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            if ("1".equals(model.get("MODIFY_PW")))
            {
                // if (encryptUserPassword != "0")
                if (!"0".equals(encryptUserPassword))
                {
                    model.put("PASSWORD", EncryptUtil.md5Encrypt(model.get("PASSWORD")));
                }
            }
            else
            {
                model.remove("PASSWORD");
            }
            dal.update(model);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 修改密码
     * @param userId 用户id
     * @param oldPassword 老密码
     * @param newPassword 新密码
     * @throws Exception 异常
     */
    public void changePassword(int userId, String oldPassword, String newPassword) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            Object objRet = sqlExecutor.executeScalar("SELECT PASSWORD FROM UUM_USER WHERE ID=" + userId);
            String encryptPassword = oldPassword;
            // if (encryptUserPassword != "0")
            if (!"0".equals(encryptUserPassword))
            {
                encryptPassword = EncryptUtil.md5Encrypt(oldPassword);
            }

            String newEncryptPassword = newPassword;
            // if (encryptUserPassword != "0")
            if (!"0".equals(encryptUserPassword))
            {
                newEncryptPassword = EncryptUtil.md5Encrypt(newPassword);
            }

            if (objRet == null || encryptPassword != objRet.toString())
            {
                throw new Exception("旧密码不正确!");
            }
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("UPDATE UUM_USER SET PASSWORD=%s WHERE ID=" + userId,
                new SqlValue(newEncryptPassword)));
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 修改密码
     * @param loginId 登陆id
     * @param oldPassword 老密码
     * @param newPassword 新密码
     * @throws Exception 异常
     */
    public void changePassword(String loginId, String oldPassword, String newPassword) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            Object objRet =
                sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand("SELECT PASSWORD FROM UUM_USER WHERE LOGIN_NAME=%s",
                    new SqlValue(loginId)));
            String encryptPassword = oldPassword;
            // if (!encryptUserPassword.equals("0"))
            if (!"0".equals(encryptUserPassword))
            {
                encryptPassword = EncryptUtil.md5Encrypt(oldPassword);
            }

            String newEncryptPassword = newPassword;
            // if (!encryptUserPassword.equals("0"))
            if (!"0".equals(encryptUserPassword))
            {
                newEncryptPassword = EncryptUtil.md5Encrypt(newPassword);
            }

            if (objRet == null || !encryptPassword.equals(objRet.toString()))
            {
                throw new Exception("旧密码不正确!");
            }
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("UPDATE UUM_USER SET PASSWORD=%s WHERE LOGIN_NAME=%s",
                new SqlValue(newEncryptPassword), new SqlValue(loginId)));
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 获得用户的权限树
     * @param userId 用户id
     * @return 结果
     * @throws Exception 异常
     */
    public String getUserFunctionTree(int userId) throws Exception
    {
        StringBuilder sbResult = new StringBuilder();
        sbResult.append("<DataSource>");

        Document dsAllRole =
            sqlExecutor.fill("SELECT ID,PARENT_ID,NAME,PARAM,TYPE,0 as FLAG FROM UUM_FUNCTION WHERE TYPE=0 AND STATE=0 ORDER BY VIEW_POS",
                "DataSource", "UUM_FUNCTION");
        Document dsUserRole =
            sqlExecutor.fill("SELECT ID,PARENT_ID,NAME,PARAM,TYPE FROM UUM_FUNCTION WHERE ID IN "
                + "(SELECT FUNCTION_ID FROM UUM_ROLEAUTHORITY WHERE ROLE_ID IN (SELECT ROLE_ID FROM UUM_USERTOROLE WHERE USER_ID=" + userId + "))",
                "DataSource", "UUM_FUNCTION");

        List<Element> elmentsAllRole = dsAllRole.getRootElement().elements();
        List<Element> elmentsUserRole = dsUserRole.getRootElement().elements();
        for (Element itemUser : elmentsUserRole)
        {
            for (Element itemAll : elmentsAllRole)
            {
                String _type = String.format("%s", itemUser.selectSingleNode("TYPE").getText());
                String _a_id = String.format("%s", itemAll.selectSingleNode("ID").getText());
                String _u_id = String.format("%s", itemUser.selectSingleNode("ID").getText());
                String _u_pid = String.format("%s", itemUser.selectSingleNode("PARENT_ID").getText());
                // if ((_type.equals("1") && _a_id.equals(_u_pid)) || (_type.equals("0") && _a_id.equals(_u_id)))
                if (("1".equals(_type) && _a_id.equals(_u_pid)) || ("0".equals(_type) && _a_id.equals(_u_id)))
                {
                    itemAll.selectSingleNode("FLAG").setText("1");
                    break;
                }
            }
        }
        for (Element itemAll : elmentsAllRole)
        {
            // if (itemAll.selectSingleNode("FLAG").getText().equals("1")
            if ("1".equals(itemAll.selectSingleNode("FLAG").getText())
                || hadSubItemRight(dsAllRole, Integer.parseInt(itemAll.selectSingleNode("ID").getText())))
            {
                String _param = String.format("%s", itemAll.selectSingleNode("PARAM").getText());
                sbResult.append(String.format("<Table><ID>%s</ID><PARENT_ID>%s</PARENT_ID><NAME>%s</NAME><PARAM>%s</PARAM></Table>", itemAll
                    .selectSingleNode("ID").getText(), itemAll.selectSingleNode("PARENT_ID").getText(), itemAll.selectSingleNode("NAME").getText(),
                    _param.replace("&", "&amp;")));
            }
        }
        sbResult.append("</DataSource>");

        return sbResult.toString();
    }

    // region hadSubItemRight
    /**
     * @param ds 参数
     * @param parentId 参数
     * @return 结果
     */
    private boolean hadSubItemRight(Document ds, int parentId)
    {
        List<Element> elments = ds.getRootElement().selectNodes("UUM_FUNCTION[ID!=0 and PARENT_ID=" + parentId + "]");
        for (Element item : elments)
        {
            String strId = item.selectSingleNode("ID").getText();
            String strPId = item.selectSingleNode("PARENT_ID").getText();
            // if (!strId.equals("0") && strPId.equals(parentId)) {
            if ("1".equals(item.selectSingleNode("FLAG").getText()) || hadSubItemRight(ds, Integer.parseInt(strId)))
            {
                return true;
            }
            // }
        }
        return false;
    }

    /**
     * 用户登录
     * @param loginId 参数
     * @param passWord 密码
     * @return 结果
     * @throws Exception 异常
     */
    public HashMap<String, String> login(String loginId, String passWord) throws Exception
    {
        HashMap<String, String> nvList =
            sqlExecutor.find(sqlExecutor.getSqlParser().getSelectCommandString(dal.TABLE_NAME, null, null,
                new SqlCondition("LOGIN_NAME", loginId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String),
                new SqlCondition("STATE", "0", SqlLogicType.And, SqlRelationType.IEqual, SqlParamType.Numeric)), true);

        if (nvList == null)
        {
            throw new Exception("无效的登录用户!");
        }

        String strPassword = passWord;
        if (!"0".equals(encryptUserPassword))
        {
            strPassword = EncryptUtil.md5Encrypt(passWord);
        }

        if (!nvList.get("PASSWORD").equals(strPassword))
        {
            throw new Exception("密码错误!");
        }

        List<String> groupIds = sqlExecutor.load(String.format("SELECT GROUP_ID FROM UUM_USERTOGROUP WHERE USER_ID=%s", nvList.get("ID")), true);
        List<String> roleIds = sqlExecutor.load(String.format("SELECT ROLE_ID FROM UUM_USERTOROLE WHERE USER_ID=%s", nvList.get("ID")), true);

        String[] arrTemp = new String[groupIds.size()];
        groupIds.toArray(arrTemp);
        nvList.put("UserGroups", StringUtil.join(arrTemp, ","));
        arrTemp = new String[roleIds.size()];
        roleIds.toArray(arrTemp);
        nvList.put("UserRoles", StringUtil.join(arrTemp, ","));

        return nvList;
    }

    /**
     * 用户登录
     * @param loginId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public HashMap<String, String> loginWithOutPassword(String loginId) throws Exception
    {
        HashMap<String, String> nvList =
            sqlExecutor.find(sqlExecutor.getSqlParser().getSelectCommandString(dal.TABLE_NAME, null, null,
                new SqlCondition("LOGIN_NAME", loginId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String),
                new SqlCondition("STATE", "0", SqlLogicType.And, SqlRelationType.IEqual, SqlParamType.Numeric)), true);

        if (nvList == null)
        {
            throw new Exception("无效的登录用户!");
        }

        List<String> groupIds = sqlExecutor.load(String.format("SELECT GROUP_ID FROM UUM_USERTOGROUP WHERE USER_ID=%s", nvList.get("ID")), true);
        List<String> roleIds = sqlExecutor.load(String.format("SELECT ROLE_ID FROM UUM_USERTOROLE WHERE USER_ID=%s", nvList.get("ID")), true);

        String[] arrTemp = new String[groupIds.size()];
        groupIds.toArray(arrTemp);
        nvList.put("UserGroups", StringUtil.join(arrTemp, ","));
        arrTemp = new String[roleIds.size()];
        roleIds.toArray(arrTemp);
        nvList.put("UserRoles", StringUtil.join(arrTemp, ","));

        return nvList;
    }

    /**
     * @param userId 用户id
     * @return 结果
     * @throws Exception 异常
     */
    public String getUserNameById(String userId) throws Exception
    {
        List<String> userNames = sqlExecutor.load(String.format("SELECT USER_NAME FROM UUM_USER WHERE ID=%s", userId), true);
        if (userNames != null && userNames.size() > 0)
        {
            return userNames.get(0);
        }
        else
        {
            return "";
        }
    }

    /**
     * 获取用户名
     * @param loginName 登陆名称
     * @param 参数
     * @return 结果
     * @throws Exception 异常
     */
    public String getUserName(String loginName) throws Exception
    {
        String userName = "";
        try
        {
            Object user =
                sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand("SELECT USER_NAME FROM UUM_USER WHERE LOGIN_NAME=%s",
                    new SqlValue(loginName)));
            userName = user.toString();
        }
        catch (Exception ex)
        {
            throw new Exception("获取用户名失败");
        }
        return userName;
    }

    /**
     * 判断用户是否为管理员用户
     * 
     * @param userId 用户ID
     * @return 用户是否为管理员用户
     * @throws Exception
     */
    public static boolean isAdmin(int userId) throws Exception
    {
        boolean retval = true;
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] { new SqlCondition("ROLE_ID", "1", SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("USER_ID", Integer.toString(userId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
            int exist = DefaultDal.isExist("UUM_USERTOROLE", conds);
            if (exist <= 0)
            {
                retval = false;
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
        return retval;
    }
}
