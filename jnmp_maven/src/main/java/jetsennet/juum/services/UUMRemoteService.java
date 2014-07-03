package jetsennet.juum.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;

import jetsennet.juum.dataaccess.UserProfileInfo;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.util.ConfigUtil;
import jetsennet.util.EncryptUtil;
import jetsennet.util.StringUtil;

/**
 * @author ？
 */
public class UUMRemoteService
{

    private ConnectionInfo uumConnectionInfo;
    private static String userAuthType = ConfigUtil.getProperty("UserAuthType");
    private static final String uumRemotingAddress = ConfigUtil.getProperty("UUMRemotingAddress");
    private static final String configFile = ConfigUtil.getProperty("UserAuthConfigFile");

    /*
     * static UUMRemoteService() { //加载webservice权限表 System.Xml.XmlDocument xDoc = new System.Xml.XmlDocument(); try { xDoc.Load(configFile); int len
     * = xDoc.DocumentElement.ChildNodes.Count; for (int i = 0; i < len; i++) { System.Xml.XmlNode node = xDoc.DocumentElement.ChildNodes[i]; String
     * _name = node.Attributes["name"].Value; String _param = node.Attributes["param"].Value; if (!String.IsNullOrEmpty(_name) &&
     * !String.IsNullOrEmpty(_param)) { if (!hTabServiceInfo.ContainsKey(_name)) hTabServiceInfo.Add(_name, _param); } } } catch { } }
     */

    /**
     * 构造函数
     */
    public UUMRemoteService()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));
    }

    public static HashMap<String, UserProfileInfo> hTabToken = new HashMap<String, UserProfileInfo>();
    // (HashMap<String,UserProfileInfo>)Collections.synchronizedMap(new
    // HashMap<String,UserProfileInfo>());

    private static HashMap<String, String> hTabServiceInfo = new HashMap<String, String>(); // (HashMap<String,String>)Collections.synchronizedMap(new

    // HashMap<String,String>());

    /**
     * 创建验证对象实例
     * @return 结果
     */
    public static UUMRemoteService UUMRemoteServiceInstance()
    {
        if ("1".equalsIgnoreCase(userAuthType))
        {
            UUMRemoteService uums = null;
            try
            {
                // _uums = (UUMRemoteService)Activator.GetObject(typeof(UUMRemoteService), uumRemotingAddress);
            }
            catch (Exception ex)
            {

                uums = new UUMRemoteService();
                userAuthType = "2";
            }
            return uums;
        }
        else
        {
            return new UUMRemoteService();
        }
    }

    /**
     * 创建验证服务端
     */
    public static void createUUMRemoteServiceServer()
    {
        if ("1".equalsIgnoreCase(userAuthType))
        {
            String[] arrTemp = uumRemotingAddress.split("/");
            String[] arrTempPort = arrTemp[arrTemp.length - 2].split(":");
            // TcpServerChannel channel = new TcpServerChannel("tcpUUM", Convert.ToInt32(arrTempPort[arrTempPort.Length - 1]));
            // ChannelServices.RegisterChannel(channel, false);

            // sRemotingConfiguration.RegisterWellKnownServiceType(typeof(UUMRemoteService), arrTemp[arrTemp.Length - 1],
            // WellKnownObjectMode.Singleton);
        }
    }

    /**
     * @param groupId 对象组
     * @param groupType 对象组类型
     * @return 结果
     * @throws Exception 异常
     */
    public Document uumGetUsersByGroup(int groupId, int groupType) throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        String groupIds = getAllGroupsByParentId(String.valueOf(groupId), groupType);

        groupIds = StringUtil.join(groupIds.split(","), ",");

        String sqlCmd =
            "SELECT ID,LOGIN_NAME,USER_NAME FROM UUM_USER WHERE ID IN (SELECT USER_ID FROM UUM_USERTOGROUP WHERE GROUP_ID IN (" + groupIds
                + ")) AND STATE=0";
        Document ds = dalUUM.fill(sqlCmd, "DataSource", "Table");
        return ds;
    }

    private String getAllGroupsByParentId(String parentId, int type) throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        StringBuilder sbGroups = new StringBuilder();
        sbGroups.append(parentId);
        sbGroups.append(",");

        List<String> groups =
            dalUUM
                .load("SELECT ID FROM UUM_USERGROUP WHERE ID<>0 AND PARENT_ID in (" + parentId + ")" + (type >= 0 ? " AND TYPE=" + type : ""), true);
        if (groups.size() > 0)
        {
            String[] gs = new String[groups.size()];
            groups.toArray(gs);
            sbGroups.append(getAllGroupsByParentId(StringUtil.join(gs, ","), type));
        }
        return sbGroups.toString();
    }

    /**
     * 查询用户分组
     * @param groupType 参数
     * @return 结果
     * @throws Exception 异常
     */
    public Document uumGetUserGroups(int groupType) throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        if (groupType >= 0)
        {
            return dalUUM.fill("SELECT ID,NAME,PARENT_ID FROM UUM_USERGROUP WHERE TYPE=" + groupType, "DataSource", "Table");
        }
        else
        {
            return dalUUM.fill("SELECT ID,NAME,PARENT_ID FROM UUM_USERGROUP", "DataSource", "Table");
        }
    }

    /**
     * 验证登录
     * @param userId 用户名
     * @param userToken ？
     * @return 结果
     */
    public int uumUserValidate(String userId, String userToken)
    {
        return uumUserValidate(userId, userToken, null);
    }

    /**
     * 验证权限
     * @param userId 用户名
     * @param userToken 参数
     * @param key 参数
     * @return 结果
     */
    public int uumUserValidate(String userId, String userToken, String key)
    {
        if ("2".equalsIgnoreCase(userAuthType))
        {
            return (userId.equals(userToken) || EncryptUtil.shaCompare(userId, userToken)) ? 0 : -99;
        }
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        UserProfileInfo uInfo = (UserProfileInfo) hTabToken.get(userId);
        if (uInfo == null || userToken != uInfo.getUserToken())
        {
            if ("1".equalsIgnoreCase(userAuthType) || "3".equalsIgnoreCase(userAuthType))
            {
                return -99;
            }
        }
        else
        {
            uInfo.setUpdateTime(new Date());
            if (!StringUtil.isNullOrEmpty(key))
            {

                if (uInfo.gethTabServiceAuth().containsKey(key))
                {
                    return (uInfo.gethTabServiceAuth().get(key)) ? 0 : -98;
                }
                else if (hTabServiceInfo.containsKey(key))
                {
                    String _param = hTabServiceInfo.get(key);
                    Object obj = null;
                    try
                    {
                        obj =
                            dalUUM.executeScalar(String.format(
                                "SELECT 1 FROM UUM_USERTOROLE WHERE ROLE_ID IN (SELECT ROLE_ID FROM UUM_ROLEAUTHORITY WHERE %s) AND USER_ID=%s",
                                _param, uInfo.getUserId()));
                    }
                    catch (Exception ex)
                    {
                    }

                    if (obj != null)
                    {
                        uInfo.gethTabServiceAuth().put(key, true);
                    }
                    else
                    {
                        uInfo.gethTabServiceAuth().put(key, false);
                        return -98;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 清除缓存的验证数据
     * @param loginId 参数
     */
    public void uumClearValideData(String loginId)
    {
        boolean isSingleUser = !StringUtil.isNullOrEmpty(loginId);
        for (String key : hTabToken.keySet())
        {
            if (isSingleUser)
            {
                if (loginId.equalsIgnoreCase(key))
                {
                    hTabToken.get(key).gethTabServiceAuth().clear();
                    break;
                }
            }
            else
            {
                hTabToken.get(key).gethTabServiceAuth().clear();
            }
        }
    }

    /**
     * 清除缓存的验证数据
     * @param userId 用户id
     */
    public void uumClearValideData(int userId)
    {
        Object obj = null;
        try
        {
            ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
            obj = dalUUM.executeScalar(" SELECT LOGIN_NAME FROM UUM_USER WHERE ID=" + userId);
        }
        catch (Exception ex)
        {
        }
        if (obj != null)
        {
            uumClearValideData(obj.toString());
        }
    }

    /**
     * 登录用户
     * @param uInfo 登录用户
     */
    public void uumLogin(UserProfileInfo uInfo)
    {
        if (hTabToken.containsKey(uInfo.getLoginId()))
        {
            hTabToken.remove(uInfo.getLoginId());
        }
        hTabToken.put(uInfo.getLoginId(), uInfo);
    }

    /**
     * 用户登出
     * @param loginId 参数
     */
    public void uumLogout(String loginId)
    {
        if (hTabToken.containsKey(loginId))
        {
            hTabToken.remove(loginId);
        }
    }

    /**
     * 取得用户资料
     * @param loginId 参数
     * @return 结果
     */
    public UserProfileInfo uumGetLoginUserInfo(String loginId)
    {
        UserProfileInfo uInfo = (UserProfileInfo) hTabToken.get(loginId);
        return uInfo;
    }

    /**
     * 根据用户ID查找对应权限信息
     * @param userId 用户名
     * @param fields 参数
     * @param conditions 参数
     * @return 结果
     * @throws Exception 异常
     */
    public String uumGetFunctionsByUserId(int userId, String fields, String conditions) throws Exception
    {
        String retXml = "<DataSource/>";
        fields = StringUtil.isNullOrEmpty(fields) ? "*" : fields.replace("'", "").replace(";", "");

        SqlCondition[] c = null;
        if (!StringUtil.isNullOrEmpty(conditions))
        {
            c = SqlCondition.loadXml(conditions);
        }
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        String strCondition = dalUUM.getSqlParser().parseSqlCondition(null, c);

        strCondition = StringUtil.isNullOrEmpty(strCondition) ? "WHERE 1=1" : strCondition;

        String strCMD =
            "SELECT " + fields + " FROM  UUM_FUNCTION " + strCondition
                + " AND  ID in (SELECT FUNCTION_ID FROM UUM_ROLEAUTHORITY WHERE ROLE_ID IN (SELECT ROLE_ID FROM UUM_USERTOROLE WHERE USER_ID="
                + userId + ")) "+"or ID in "+"(SELECT PARENT_ID FROM  UUM_FUNCTION " + strCondition
                + " AND  ID in (SELECT FUNCTION_ID FROM UUM_ROLEAUTHORITY WHERE ROLE_ID IN (SELECT ROLE_ID FROM UUM_USERTOROLE WHERE USER_ID="
                + userId + ")))";

        return dalUUM.fill(strCMD, "DataSource", "UUM_FUNCTION").asXML();
    }

    /**
     * @param functionId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public String uumGetUserByFuncId(int functionId) throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        String retXml = "<DataSource/>";

        String sqlCmd;
        if (functionId != -1)
        {
            sqlCmd =
                "SELECT ID , LOGIN_NAME,USER_NAME FROM UUM_USER WHERE ID IN "
                    + "(SELECT USER_ID FROM UUM_USERTOROLE WHERE ROLE_ID IN (SELECT ROLE_ID FROM UUM_ROLEAUTHORITY WHERE FUNCTION_ID =" + functionId
                    + ")) and state = 0";
        }
        else
        {
            sqlCmd = "SELECT ID , LOGIN_NAME,USER_NAME FROM UUM_USER";
        }

        retXml = dalUUM.fill(sqlCmd, "DataSource", "Table").asXML();
        return retXml;
    }

    /**
     * @param userGroup 参数
     * @param userRole 参数
     * @return 结果
     * @throws Exception 异常
     */
    public Document uumGetUsers(int userGroup, int userRole) throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        String strFilter = "WHERE STATE=0 ";

        // 用户组之用户
        if (userGroup >= 0)
        {
            strFilter += "AND ID IN (SELECT USER_ID FROM UUM_USERTOGROUP WHERE GROUP_ID=" + userGroup + ")";
        }
        // 角色之用户
        if (userRole >= 0)
        {
            strFilter += "AND ID IN (SELECT USER_ID FROM UUM_USERTOROLE WHERE ROLE_ID=" + userRole + ")";
        }

        return dalUUM.fill("SELECT * FROM UUM_USER " + strFilter, "DataSource", "Table");
    }

    /**
     * @return 结果
     * @throws Exception 异常
     */
    public Document uumGetDepartments() throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        return dalUUM.fill("SELECT ID,NAME,PARENT_ID FROM UUM_USERGROUP WHERE TYPE=0", "DataSource", "Table");
    }

    /**
     * @return 结果
     * @throws Exception 异常
     */
    public Document uumGetUserGroup() throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        return dalUUM.fill("SELECT ID,NAME,PARENT_ID FROM UUM_USERGROUP WHERE TYPE=2", "DataSource", "Table");
    }

    /**
     * @return 结果
     * @throws Exception 异常
     */
    public Document uumGetUserColumns() throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        return dalUUM.fill("SELECT ID,NAME,PARENT_ID FROM UUM_USERGROUP WHERE TYPE=1", "DataSource", "Table");
    }

    /**
     * @return 结果
     * @throws Exception 异常
     */
    public Document uumGetChannels() throws Exception
    {
        ISqlExecutor dalUUM = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        return dalUUM.fill("SELECT ID,NAME FROM UUM_USERGROUP WHERE TYPE=3", "DataSource", "Table");
    }
}
