package jetsennet.jnmp.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;

import jetsennet.jbmp.business.Alarm;
import jetsennet.jbmp.dataaccess.AttribAlarmDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.ObjGroupDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jnmp.business.RFTopo2Role;
import jetsennet.juum.business.User;
import jetsennet.juum.services.UUMRemoteService;
import jetsennet.net.UserAuthHeader;
import jetsennet.net.WSResult;
import jetsennet.net.WebServiceBase;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlQuery;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;

import org.dom4j.Document;
import org.dom4j.Element;

@WebService(name = "NMPPermissionsService", serviceName = "NMPPermissionsService", targetNamespace = "http://JetsenNet/JNMP/")
public class NMPPermissionsService extends WebServiceBase
{

    private ConnectionInfo bmpConnectionInfo;
    private jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.JBMP");
    private static UUMRemoteService uumRemoteService;

    static
    {
        UUMRemoteService.createUUMRemoteServiceServer();
        uumRemoteService = UUMRemoteService.UUMRemoteServiceInstance();
    }

    public NMPPermissionsService()
    {
        bmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("bmp_driver"), DbConfig.getProperty("bmp_dburl"), DbConfig.getProperty("bmp_dbuser"), DbConfig
                .getProperty("bmp_dbpwd"));
    }

    private void errorProcess(WSResult retObj, String message, Exception ex)
    {
        logger.error(message, ex);
        retObj.errorCode = -1;
        retObj.errorString = message + ex.getMessage();
    }

    private void logOperator(String message)
    {
        logOperator(userAuth.getUserId(), userAuth.getLoginId(), message);
    }

    private void logOperator(int userId, String userName, String message)
    {
        logger.logOperator(bmpConnectionInfo, userId, userName, "JBMP", message);
    }

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
     * 带权限过滤的通用查询
     * @param queryInfo	查询信息xml
     * @param idName	需要过滤的id名。注：如果是多表查询，此处要带上表别名，例：a.GROUP_ID
     * @param flag		1:对象组过滤;2:对象过滤
     * @param userAuth	当前用户信息
     * @return
     */
    public WSResult nmpPermissionsQuery(String queryInfo, String idName, String flag, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("nmpPermissionsQuery");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        String queryInfoAddCond = "";
        //管理员不过滤
        boolean isAdmin = isAdministrator(userAuth.getUserId());
        if ("1".equals(flag) && !isAdmin)
        {
            queryInfoAddCond = addGroupCondition(queryInfo, idName);
        }
        else if ("2".equals(flag) && !isAdmin)
        {
            queryInfoAddCond = addObjCondition(queryInfo, idName);
        }
        else
        {
            queryInfoAddCond = queryInfo;
        }
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        SqlQuery query = SerializerUtil.deserialize(SqlQuery.class, queryInfoAddCond);
        Document ds = null;
        try
        {
            ds = execBmp.fill(query);
            retObj.resultVal = ds.asXML();
        }
        catch (Exception ex)
        {
            logger.debug(queryInfo);
            errorProcess(retObj, "读取数据列表失败!", ex);
        }

        return retObj;
    }

    private String addGroupCondition(String queryInfo, String idName)
    {
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        Document ds = null;
        try
        {
            ds =
                execBmp.fill("select b.GROUP_ID from UUM_USERTOROLE a " + "inner join BMP_ROLE2GROUP b on a.ROLE_ID = b.ROLE_ID "
                    + "where a.USER_ID = " + userAuth.getUserId());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        Element e = ds.getRootElement();
        List<Element> groupIds = e.selectNodes("//GROUP_ID");
        Iterator<Element> it = groupIds.iterator();
        String groupIdsStr = "";
        String addCond = "";
        while (it.hasNext())
        {
            groupIdsStr += it.next().getData() + ",";
        }
        if (!"".equals(groupIdsStr) && !",".equals(groupIdsStr))
        {
            groupIdsStr = groupIdsStr.substring(0, groupIdsStr.length() - 1);
            addCond = "<SC><SLT>0</SLT><PN>" + idName + "</PN><PV>" + groupIdsStr + "</PV><SRT>7</SRT><SPT>0</SPT></SC>";
        }
        else
        {
            addCond =
                "<SC><SLT>0</SLT><PN>" + idName + "</PN><PV>" + groupIdsStr + "</PV><SRT>0</SRT><SPT>0</SPT></SC>" + "<SC><SLT>0</SLT><PN>" + idName
                    + "</PN><PV>" + groupIdsStr + "</PV><SRT>5</SRT><SPT>0</SPT></SC>";
        }

        int StrBreak = queryInfo.indexOf("</Conditions>");
        //原语句无条件的情况下，添加<Conditions>标签。
        if (StrBreak == -1)
        {
            StrBreak = queryInfo.indexOf("</SqlQuery>");
            addCond = "<Conditions>" + addCond + "</Conditions>";
        }
        return queryInfo.substring(0, StrBreak) + addCond + queryInfo.substring(StrBreak);
    }

    private String addObjCondition(String queryInfo, String idName)
    {
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        Document ds = null;
        try
        {
            ds =
                execBmp.fill("select c.OBJ_ID from UUM_USERTOROLE a " + "inner join BMP_ROLE2GROUP b on a.ROLE_ID = b.ROLE_ID "
                    + "inner join BMP_OBJ2GROUP c on b.GROUP_ID = c.GROUP_ID " + "where a.USER_ID = " + userAuth.getUserId());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        Element e = ds.getRootElement();
        List<Element> objIds = e.selectNodes("//OBJ_ID");
        Iterator<Element> it = objIds.iterator();
        String objIdsStr = "";
        String addCond = "";
        while (it.hasNext())
        {
            objIdsStr += it.next().getData() + ",";
        }
        if (!"".equals(objIdsStr) && !",".equals(objIdsStr))
        {
            objIdsStr = objIdsStr.substring(0, objIdsStr.length() - 1);
            addCond = "<SC><SLT>0</SLT><PN>" + idName + "</PN><PV>" + objIdsStr + "</PV><SRT>7</SRT><SPT>0</SPT></SC>";
        }
        else
        {
            addCond =
                "<SC><SLT>0</SLT><PN>" + idName + "</PN><PV>" + objIdsStr + "</PV><SRT>0</SRT><SPT>0</SPT></SC>" + "<SC><SLT>0</SLT><PN>" + idName
                    + "</PN><PV>" + objIdsStr + "</PV><SRT>5</SRT><SPT>0</SPT></SC>";
        }

        int StrBreak = queryInfo.indexOf("</Conditions>");
        //原语句无条件的情况下，添加<Conditions>标签。
        if (StrBreak == -1)
        {
            StrBreak = queryInfo.indexOf("</SqlQuery>");
            addCond = "<Conditions>" + addCond + "</Conditions>";
        }
        return queryInfo.substring(0, StrBreak) + addCond + queryInfo.substring(StrBreak);
    }

    /**
     * 返回系统组、网段组、采集组、一般组树形结构xml
     * @return
     */
    public String getGroup()
    {
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        Document ds = null;
        String result = "";
        try
        {
            //因分组数据是造出来的,为防止id冲突采用group_type*-1.
            String sqlStr =
                "select a.GROUP_ID, a.GROUP_NAME, b.PARENT_ID, a.GROUP_TYPE "
                    + "from BMP_OBJGROUP a left join bmp_group2group b on a.group_id = b.group_id " + "where a.GROUP_TYPE in (1,3,4,0)";
            ds = execBmp.fill(sqlStr);
        }
        catch (SQLException e)
        {
            logger.error("", e);
        }
        String groupTypeXml =
            "<DataTable><GROUP_ID>-1</GROUP_ID><GROUP_NAME>系统组</GROUP_NAME><PARENT_ID/><GROUP_TYPE>1</GROUP_TYPE></DataTable>"
                + "<DataTable><GROUP_ID>-3</GROUP_ID><GROUP_NAME>采集组</GROUP_NAME><PARENT_ID/><GROUP_TYPE>3</GROUP_TYPE></DataTable>"
                + "<DataTable><GROUP_ID>-4</GROUP_ID><GROUP_NAME>网段组</GROUP_NAME><PARENT_ID/><GROUP_TYPE>4</GROUP_TYPE></DataTable>"
                + "<DataTable><GROUP_ID>0</GROUP_ID><GROUP_NAME>一般组</GROUP_NAME><PARENT_ID/><GROUP_TYPE>0</GROUP_TYPE></DataTable>";
        List<Element> list = ds.selectNodes("//DataTable");
        if (list == null || list.size() == 0)
        {
            result = "<?xml version='1.0' encoding='UTF-8'?><DataSource>" + groupTypeXml + "</DataSource>";
        }
        else
        {
            Iterator<Element> it = list.iterator();
            //有父节点不做处理，若没有父节点，则让GROUP_TYPE*-1，让之前伪造的组数据作为父节点。
            while (it.hasNext())
            {
                Element e = it.next();
                Element eParentId = e.element("PARENT_ID");
                Element eGroupType = e.element("GROUP_TYPE");
                String parentId = (String) eParentId.getData();
                String groupType = (String) eGroupType.getData();
                if ("".equals(parentId))
                {
                    eParentId.setText(-Integer.parseInt(groupType) + "");
                }
            }
            String xml = ds.asXML();
            int breakNum = xml.indexOf("</DataSource>");
            result = xml.substring(0, breakNum) + groupTypeXml + xml.substring(breakNum);
            ;
        }
        return result;
    }

    /**
     * 角色对象组权限新增
     * @param roleId
     * @param groupIdsStr
     */
    public void RoleGroupInsert(String roleId, String groupIdsStr)
    {
        if (roleId == null || "".equals(roleId) || groupIdsStr == null || "".equals(groupIdsStr))
        {
            return;
        }
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        String[] groupIds = groupIdsStr.split(",");
        try
        {
            String insertSql = "";
            for (int i = 0; i < groupIds.length; i++)
            {
                insertSql = "insert into BMP_ROLE2GROUP(ROLE_ID, GROUP_ID) values(" + roleId + ", " + groupIds[i] + ")";
                execBmp.executeNonQuery(insertSql);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 角色对象组权限修改
     * @param roleId
     * @param groupIdsStr
     */
    public void RoleGroupUpdate(String roleId, String groupIdsStr)
    {
        if (roleId == null || "".equals(roleId))
        {
            return;
        }
        RoleGroupDelete(roleId);
        RoleGroupInsert(roleId, groupIdsStr);
    }

    /**
     * 角色对象组权限删除
     * @param roleId
     */
    public void RoleGroupDelete(String roleId)
    {
        if (roleId == null || "".equals(roleId))
        {
            return;
        }
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        try
        {
            execBmp.executeNonQuery("delete from BMP_ROLE2GROUP where ROLE_ID = " + roleId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是管理员
     * 请使用isAdmin
     * 
     * @param userId
     * @return
     */
    @Deprecated
    public boolean isAdministrator(int userId)
    {
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        Document ds = null;
        try
        {
            ds = execBmp.fill("select * from UUM_USERTOROLE where ROLE_ID = 1 and USER_ID = " + userId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        int resultCount = ds.selectNodes("//DataTable").size();
        if (resultCount > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getTopo2RFTreeXml()
    {
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        Document ds1 = null;
        Document ds2 = null;
        String result = "";
        try
        {
            ds1 = execBmp.fill("SELECT FLOOR_ID AS NODE_ID, FLOOR_NAME AS NODE_NAME, '' AS PARENT_ID FROM NMP_FLOOR");
            ds2 =
                execBmp
                    .fill("SELECT A.ROOM_ID AS NODE_ID, A.ROOM_NAME AS NODE_NAME, B.FLOOR_ID AS PARENT_ID FROM NMP_ROOM A INNER JOIN NMP_ROOM2FLOOR B ON A.ROOM_ID = B.ROOM_ID");

            List<Element> list = ds1.selectNodes("//NODE_ID");
            for (Element e : list)
            {
                e.setText("f" + e.getText());
            }

            list = ds2.selectNodes("//PARENT_ID");
            for (Element e : list)
            {
                e.setText("f" + e.getText());
            }
        }
        catch (SQLException e)
        {
            logger.error("", e);
        }

        result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource>";
        String str1 = ds1.asXML();
        result += str1.substring(str1.indexOf("<DataSource>") + "<DataSource>".length(), str1.indexOf("</DataSource>"));

        String str2 = ds2.asXML();
        result += str2.substring(str2.indexOf("<DataSource>") + "<DataSource>".length(), str2.indexOf("</DataSource>"));

        result += "</DataSource>";

        return result;
    }

    /**
     * 添加角色和房间拓扑图的关联关系
     * @param roleId
     * @param mapIds
     * @return
     */
    public WSResult addRFTopo2Role(String roleId, String mapIds)
    {
        WSResult ret = new WSResult();

        try
        {
            String id = ClassWrapper.wrap(RFTopo2Role.class).addRFTopo2Role(roleId, mapIds);
            logger.info("新建房间拓扑图与角色关联数据ID:" + id);
        }
        catch (Exception e)
        {
            logger.error("新建房间拓扑图与角色关联数据出错！", e);
        }

        return ret;
    }

    /**
     * 判断userId是否为管理员
     * 
     * @param userId
     * @return
     */
    public WSResult isAdmin(int userId)
    {
        WSResult retval = new WSResult();
        try
        {
            boolean isAdmin = User.isAdmin(userId);
            retval.resultVal = Boolean.toString(isAdmin);
        }
        catch (Exception ex)
        {
            this.errorProcess(retval, ex.getMessage(), ex);
        }
        return retval;
    }

    /**
     * 批量修改组设置
     * 
     * @param reqS
     * @return
     */
    public WSResult batchSetGroup(String reqS)
    {
        WSResult retval = new WSResult();
        try
        {
            String[] tempLst = reqS.split(";");
            List<Integer> objIds = new ArrayList<Integer>();
            List<Integer> types = new ArrayList<Integer>(4);
            List<Integer> groupIds = new ArrayList<Integer>();

            for (int i = 0; i < tempLst.length; i++)
            {
                if (tempLst[i] != null && !tempLst[i].isEmpty())
                {
                    String[] tempArray = tempLst[i].split(",");
                    for (String temp : tempArray)
                    {
                        if (i == 0)
                        {
                            objIds.add(Integer.valueOf(temp));
                        }
                        else if (i == 1)
                        {
                            types.add(Integer.valueOf(temp));
                        }
                        else if (i == 2)
                        {
                            groupIds.add(Integer.valueOf(temp));
                        }
                    }
                }
            }
            ObjGroupDal ogdal = ClassWrapper.wrapTrans(ObjGroupDal.class);
            ogdal.batchSetGroup(objIds, types, groupIds);
        }
        catch (Exception ex)
        {
            this.errorProcess(retval, "格式错误：" + reqS, ex);
        }
        return retval;
    }

    /**
     * 设置报警规则
     * 
     * @param alarmConfig
     * @param objAttrId
     * @return
     */
    public WSResult setAlarmConfig(String alarmConfig)
    {
        WSResult retval = new WSResult();
        try
        {
            Alarm alarm = ClassWrapper.wrapTrans(Alarm.class);
            alarm.setAlarmConfig(alarmConfig);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            this.errorProcess(retval, "设置报警规则出错", ex);
        }
        return retval;
    }

    /**
     * 批量设置报警规则
     * 
     * @param alarmConfig
     * @param alarms
     * @return
     */
    public WSResult batchSetAlarmConfig(String alarmConfig, String alarms)
    {
        WSResult retval = new WSResult();
        try
        {
            Alarm alarm = ClassWrapper.wrapTrans(Alarm.class);
            alarm.batchSetAlarmConfig(alarmConfig, alarms);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            this.errorProcess(retval, "批量设置报警规则出错", ex);
        }
        return retval;
    }

    /**
     * 获取对象属性对应的报警规则
     * @param objAttrIdS
     * @return
     */
    public WSResult getObjAttrAlarmId(String objAttrIdS)
    {
        WSResult retval = new WSResult();
        try
        {
            AttribAlarmDal aadal = new AttribAlarmDal();
            retval.resultVal = aadal.getObjAttrAlarms(objAttrIdS);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            this.errorProcess(retval, "批量设置报警规则出错", ex);
        }
        return retval;
    }
}
