package jetsennet.juum.services;

import java.util.HashMap;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.dom4j.Document;

import jetsennet.jbmp.util.ResolveXml;
import jetsennet.juum.business.Function;
import jetsennet.juum.business.Person;
import jetsennet.juum.business.PersonGroup;
import jetsennet.juum.business.Role;
import jetsennet.juum.business.User;
import jetsennet.juum.business.UserGroup;
import jetsennet.juum.dataaccess.UserProfileInfo;
import jetsennet.net.UserAuthHeader;
import jetsennet.net.WSResult;
import jetsennet.net.WebServiceBase;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ICustomParser;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlQuery;
import jetsennet.util.ConfigUtil;
import jetsennet.util.EncryptUtil;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author？
 */
@WebService(name = "UUMSystemService", serviceName = "UUMSystemService", targetNamespace = "http://JetsenNet/JUUM/")
public class UUMSystemService extends WebServiceBase
{
    /**
     * 用户相关处理
     */
    public UUMSystemService()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));
    }

    private static HashMap<String, String> hTabCard = new HashMap<String, String>(); // (HashMap<String,String>)Collections.synchronizedMap(new
    // HashMap<String,String>());
    static
    {
        // 创建用于验证的服务对象
        UUMRemoteService.createUUMRemoteServiceServer();
        uumRemoteService = UUMRemoteService.UUMRemoteServiceInstance();
    }
    /**
     * 用户数据库连接
     */
    private ConnectionInfo uumConnectionInfo;
    /**
     * 日志记录器
     */
    private jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.JUUM");
    /**
     * 远程验证
     */
    private static UUMRemoteService uumRemoteService;

    private void errorProcess(WSResult retObj, String message, Exception ex)
    {
        logger.error(message, ex);
        retObj.errorCode = -1;
        retObj.errorString = message + ex.getMessage();
    }

    /**
     * 操作日志
     * @param message
     */
    private void logOperator(String message)
    {
        logOperator(userAuth.getUserId(), userAuth.getLoginId(), message);
    }

    /**
     * 操作日志
     * @param userId
     * @param userName
     * @param message
     */
    private void logOperator(int userId, String userName, String message)
    {
        logger.logOperator(uumConnectionInfo, userId, userName, "JUUM", message);
    }

    /**
     * 去认证中心验证登录
     * @param key
     * @return
     */
    private WSResult valideAuth(String key)
    {
        WSResult retObj = new WSResult();
        int valiateRet = -99;
        try
        {
            valiateRet = uumRemoteService.uumUserValidate(userAuth.getLoginId(), userAuth.getUserToken(), key);
        }
        catch (Exception ex)
        {
            logger.error("接口验证异常", ex);
        }
        if (valiateRet != 0)
        {
            retObj.errorCode = valiateRet;
            retObj.errorString = "接口验证失败!";
        }
        return retObj;
    }

    private WSResult valideAuth()
    {
        return valideAuth("");
    }

    /**
     * 通用新增方法 uumObjInsert
     * @param objType 参数
     * @param objXml 从那时
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult uumObjInsert(String objType, String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {

        this.userAuth = userAuth;
        WSResult retObj = valideAuth("uumObjInsert");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        try
        {
            if ("UUM_USER".equalsIgnoreCase(objType))
            {
                retObj.resultVal = String.valueOf(new User().addUser(objXml));
                logOperator("新增用户ID：" + retObj.resultVal);
            }
            else if ("UUM_ROLE".equalsIgnoreCase(objType))
            {
                retObj.resultVal = String.valueOf(new Role().addRole(objXml));
                logOperator("新增用户角色ID：" + retObj.resultVal);
            }
            else if ("UUM_USERGROUP".equalsIgnoreCase(objType))
            {
                retObj.resultVal = String.valueOf(new UserGroup().addUserGroup(objXml));
                logOperator("新增用户组ID：" + retObj.resultVal);
            }
            else if ("UUM_FUNCTION".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new Function().addFunction(objXml);
                logOperator("新增系统权限ID：" + retObj.resultVal);
            }
            else if ("UUM_PERSON".equalsIgnoreCase(objType))
            {
                retObj.resultVal = String.valueOf(new Person().addPerson(objXml));
                logOperator("新增人员ID：" + retObj.resultVal);
            }

        }
        catch (Exception ex)
        {
            logger.debug(objXml);
            errorProcess(retObj, "添加对象失败!", ex);
        }

        return retObj;
    }

    /**
     * 通用修改方法 uumObjUpdate
     * @param objType 参数
     * @param objXml 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult uumObjUpdate(String objType, String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth();
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        try
        {
            if ("UUM_USER".equalsIgnoreCase(objType))
            {
                new User().updateUser(objXml);
                logOperator("修改用户");
            }
            else if ("UUM_USERGROUP".equalsIgnoreCase(objType))
            {
                new UserGroup().updateUserGroup(objXml);
                logOperator("修改用户组");
            }
            else if ("UUM_ROLE".equalsIgnoreCase(objType))
            {
                new Role().updateRole(objXml);
                logOperator("修改用户角色");
            }
            else if ("UUM_FUNCTION".equalsIgnoreCase(objType))
            {
                new Function().updateFunction(objXml);
                logOperator("修改系统权限");
            }
            else if ("UUM_PERSONGROUP".equalsIgnoreCase(objType))
            {
                new PersonGroup().updatePersonGroup(objXml);
                logOperator("修改人员分组");
            }
            else if ("UUM_PERSON".equalsIgnoreCase(objType))
            {
                new Person().updatePerson(objXml);
                logOperator("修改人员");
            }
        }
        catch (Exception ex)
        {
            logger.debug(objXml);
            errorProcess(retObj, "修改对象失败!", ex);
        }

        return retObj;
    }

    /**
     * 通用删除方法 uumObjDelete
     * @param objType 参数
     * @param objId 参数
     * @param userAuth 权限
     * @return 结果
     */
    @SuppressWarnings("unchecked")
    public WSResult uumObjDelete(String objType, String objId, @WebParam(header = true) UserAuthHeader userAuth)
    {

        this.userAuth = userAuth;
        WSResult retObj = valideAuth();
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        try
        {
            if ("UUM_USER".equalsIgnoreCase(objType))
            {
                new User().deleteUsers(objId);
                logOperator("删除用户, ID: " + ResolveXml.getXmlId(objId));
            }
            else if ("UUM_USERGROUP".equalsIgnoreCase(objType))
            {
                new UserGroup().deleteUserGroup(Integer.parseInt(objId));
                logOperator(String.format("删除用户组 ID:%s", objId));
            }
            else if ("UUM_ROLE".equalsIgnoreCase(objType))
            {
                new Role().deleteRole(Integer.parseInt(objId));
                logOperator(String.format("删除用户角色 ID:%s", objId));
            }
            else if ("UUM_FUNCTION".equalsIgnoreCase(objType))
            {
                new Function().deleteFunctions(objId);
                logOperator("删除系统权限,ID：" + ResolveXml.getXmlId(objId));
            }
            else if ("UUM_PERSON".equalsIgnoreCase(objType))
            {
                new Person().deletePersons(objId);
                // logOperator(String.format("删除人员 ID:%s", objId));
                logOperator("删除人员 ,ID: " + ResolveXml.getXmlId(objId));
            }
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "删除对象失败!", ex);
        }

        return retObj;
    }

    /**
     * @param queryInfo 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult uumObjQuery(String queryInfo, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("uumObjQuery");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor sqlExec = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        SqlQuery query = SerializerUtil.deserialize(SqlQuery.class, queryInfo);
        ICustomParser customparse = new ICustomParser()
        {
            public String parseCondition(SqlCondition c)
            {
                // 用户之用户组
                if ("UUM_USERGROUP.USER_ID".equals(c.getParamName()))
                {
                    return "ID IN (SELECT GROUP_ID FROM UUM_USERTOGROUP WHERE USER_ID=" + c.getParamValue().replace("'", "").replace(";", "") + ")";
                }
                // 用户组之用户
                if ("UUM_USER.GROUP_ID".equals(c.getParamName()))
                {
                    return "ID IN (SELECT USER_ID FROM UUM_USERTOGROUP WHERE GROUP_ID=" + c.getParamValue().replace("'", "").replace(";", "") + ")";
                }
                // 人员之用户组
                if ("UUM_USERGROUP.PERSON_ID".equals(c.getParamName()))
                {
                    return "ID IN (SELECT GROUP_ID FROM UUM_PERSONTOGROUP WHERE PERSON_ID=" + c.getParamValue().replace("'", "").replace(";", "")
                        + ")";
                }
                // 用户组之人员
                if ("UUM_PERSON.GROUP_ID".equals(c.getParamName()))
                {
                    return "ID IN (SELECT PERSON_ID FROM UUM_PERSONTOGROUP WHERE GROUP_ID=" + c.getParamValue().replace("'", "").replace(";", "")
                        + ")";
                }
                // 用户之角色
                if ("UUM_ROLE.USER_ID".equals(c.getParamName()))
                {
                    return "ID IN (SELECT ROLE_ID FROM UUM_USERTOROLE WHERE USER_ID=" + c.getParamValue().replace("'", "").replace(";", "") + ")";
                }
                // 角色之用户
                if ("UUM_USER.ROLE_ID".equals(c.getParamName()))
                {
                    return "ID IN (SELECT USER_ID FROM UUM_USERTOROLE WHERE ROLE_ID=" + c.getParamValue().replace("'", "").replace(";", "") + ")";
                }
                return "";
            }
        };

        sqlExec.getSqlParser().setCustomParser(customparse);
        Document ds = null;

        try
        {
            ds = sqlExec.fill(query);
            retObj.resultVal = ds.asXML();
        }
        catch (Exception ex)
        {
            logger.debug(queryInfo);
            errorProcess(retObj, "读取数据列表失败!", ex);
        }
        return retObj;
    }

    /**
     * 修改用户密码 uumUserModifyPWD
     * @param userId 用户id
     * @param oldPWD 老密码
     * @param newPWD 新密码
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult uumUserModifyPWD(int userId, String oldPWD, String newPWD, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("uumUserModifyPWD");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        try
        {
            User user = new User();
            user.changePassword(userId, oldPWD, newPWD);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "修改用户密码失败!", ex);
        }
        return retObj;
    }

    /**
     * 修改个人资料
     * @param objXml 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult uumModifyUserInfo(String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("uumModifyUserInfo");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        try
        {
            User user = new User();
            user.modifyUserInfo(SerializerUtil.deserialize(objXml, "User"));

            logOperator("修改个人资料 " + userAuth.getLoginId());
        }
        catch (Exception ex)
        {
            logger.debug(objXml);
            errorProcess(retObj, "修改个人资料失败!", ex);
        }
        return retObj;
    }

    /**
     * 用户登录 uumUserLogin
     * @param loginId 参数
     * @param passWord 密码
     * @return 结果
     */
    public WSResult uumUserLogin(String loginId, String passWord)
    {
        WSResult retObj = new WSResult();
        // if(!ProductVerification.checkProduct())
        // {
        // logger.error("产品未授权,无法使用!", null);
        // retObj.errorCode = -1;
        // retObj.errorString = "产品未授权,无法使用!";
        // return retObj;
        // }

        try
        {
            User user = new User();
            HashMap<String, String> mapList = user.login(loginId, passWord);

            String userId = mapList.get("ID");
            String userName = mapList.get("USER_NAME");
            String homePath = mapList.get("HOME_PATH");
            int pathSize = mapList.get("QUOTA_SIZE") == null ? 0 : Integer.parseInt(mapList.get("QUOTA_SIZE"));
            int userType = mapList.get("USER_TYPE") == null ? 0 : Integer.parseInt(mapList.get("USER_TYPE"));
            int rightLevel = mapList.get("RIGHT_LEVEL") == null ? 0 : Integer.parseInt(mapList.get("RIGHT_LEVEL"));
            loginId = mapList.get("LOGIN_NAME");

            // 用户信息
            UserProfileInfo uInfo = uumRemoteService.uumGetLoginUserInfo(loginId);

            uInfo = new UserProfileInfo(userId, loginId, userName, EncryptUtil.shaEncrypt(loginId), homePath, pathSize);
            uInfo.setUserType(userType);
            uInfo.setUserGroups(mapList.get("UserGroups"));
            uInfo.setUserRoles(mapList.get("UserRoles"));
            uInfo.setRightLevel(rightLevel);
            uInfo.setUserParam(mapList.get("APP_PARAM"));

            uumRemoteService.uumLogin(uInfo);
            retObj.resultVal = SerializerUtil.serialize(uInfo, "UserProfile");

            logOperator(Integer.parseInt(userId), loginId, "用户登录");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "用户登录失败!", ex);
        }
        return retObj;
    }

    /**
     * 取得用户认证 uumGetUserCard
     * @param guid 参数
     * @return 结果
     */
    public WSResult uumGetUserCard(String guid)
    {
        // 根据唯一识别码取得取得认证，如果存在则返回用户信息并移除认证，一个认证只能用一次
        WSResult retObj = new WSResult();
        if (hTabCard.containsKey(guid))
        {
            String _userId = (String) hTabCard.get(guid);
            if (StringUtil.isNullOrEmpty(_userId))
            {
                retObj.errorString = "无法取得用户认证!";
                retObj.errorCode = -1;
                return retObj;
            }

            hTabCard.remove(guid);

            // UserProfileInfo uInfo = (UserProfileInfo)hTabToken[_userId];
            UserProfileInfo uInfo = uumRemoteService.uumGetLoginUserInfo(_userId);
            if (uInfo == null)
            {
                retObj.errorString = "无法取得用户认证!";
                retObj.errorCode = -1;
                return retObj;
            }

            retObj.resultVal = SerializerUtil.serialize(uInfo, "UserProfile");
        }
        else
        {
            retObj.errorString = "无法取得用户认证!";
            retObj.errorCode = -1;
            return retObj;
        }
        return retObj;
    }

    /**
     * 生成用户认证 uumCreateUserCard
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult uumCreateUserCard(@WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("uumCreateUserCard");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        String cardId = new java.rmi.server.UID().toString();
        hTabCard.put(cardId, userAuth.getLoginId());
        retObj.resultVal = cardId;

        return retObj;
    }

    /**
     * 用户登出 uumLogout
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult uumLogout(@WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("uumLogout");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        logOperator("用户退出");
        // uumRemoteService.uumLogout(userAuth.LoginId);
        return retObj;
    }

    /**
     * @param userId 权限
     * @return 结果
     */
    public WSResult uumGetUserFunctionTree(int userId)
    {
        WSResult retObj = new WSResult();
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        try
        {
            User user = new User();
            retObj.resultVal = user.getUserFunctionTree(userId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获得用户的权限信息失败!", ex);
        }

        return retObj;
    }

    /**
     * 读取配置信息 uumGetAppConfig
     * @param sectionNames 参数
     * @return 结果
     */
    public WSResult uumGetAppConfig(String sectionNames)
    {
        WSResult retObj = new WSResult();
        sectionNames = sectionNames.replace("'", "");
        String[] arrTemp = sectionNames.split(",");

        StringBuilder sbConfig = new StringBuilder();
        sbConfig.append("<DataSource>");
        for (String item : arrTemp)
        {
            if (item.toUpperCase().indexOf("DB_STR") < 0)
            {
                sbConfig.append(String.format("<%1$s>%2$s</%1$s>", item, ConfigUtil.getProperty(item)));
            }
        }
        sbConfig.append("</DataSource>");
        retObj.resultVal = sbConfig.toString();
        return retObj;
    }
}
