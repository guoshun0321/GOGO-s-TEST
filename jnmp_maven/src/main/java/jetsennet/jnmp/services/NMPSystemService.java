/************************************************************************
 * 日 期：2011-11-28 
 * 作 者: 
 * 版 本：v1.3 
 * 描 述: 
 * 历 史：
 ************************************************************************/
package jetsennet.jnmp.services;

import javax.jws.WebParam;
import javax.jws.WebService;

import jetsennet.jbmp.business.Attribute;
import jetsennet.jbmp.business.Duty;
import jetsennet.jbmp.business.DutyLog;
import jetsennet.jbmp.business.HomePage;
import jetsennet.jbmp.business.ImageClass;
import jetsennet.jbmp.business.Knowledge;
import jetsennet.jbmp.business.KnowledgeType;
import jetsennet.jbmp.business.MObject;
import jetsennet.jbmp.business.ObjAttrib;
import jetsennet.jbmp.business.Object2Group;
import jetsennet.jbmp.business.SnmpTrap;
import jetsennet.jbmp.business.TopoImage;
import jetsennet.jbmp.business.TopoMap;
import jetsennet.jbmp.business.TopoTemplate;
import jetsennet.jbmp.business.ValueTable;
import jetsennet.jbmp.business.WorkOrder;
import jetsennet.jbmp.business.WorkOrderProcess;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jnmp.business.Floor;
import jetsennet.jnmp.business.Room;
import jetsennet.jnmp.business.Topo2rf;
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
import jetsennet.util.ConfigUtil;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

import org.dom4j.Document;

/**
 * @author
 */
@WebService(name = "NMPSystemService", serviceName = "NMPSystemService", targetNamespace = "http://JetsenNet/JNMP/")
public class NMPSystemService extends WebServiceBase
{

    /**
     * 构造方法
     */
    public NMPSystemService()
    {
        nmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("nmp_driver"),
                DbConfig.getProperty("nmp_dburl"),
                DbConfig.getProperty("nmp_dbuser"),
                DbConfig.getProperty("nmp_dbpwd"));
    }

    static
    {
        UUMRemoteService.createUUMRemoteServiceServer();
        uumRemoteService = UUMRemoteService.UUMRemoteServiceInstance();
    }
    private ConnectionInfo nmpConnectionInfo;
    private jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.JNMP");
    private static UUMRemoteService uumRemoteService;

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
        logger.logOperator(nmpConnectionInfo, userId, userName, "JNMP", message);
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
     * 通用读取方法
     * @param queryInfo 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpObjQuery(String queryInfo, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("nmpObjQuery");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
        SqlQuery query = SerializerUtil.deserialize(SqlQuery.class, queryInfo);
        Document ds = null;
        try
        {
            ds = execNmp.fill(query);
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
     * 通用新增方法 nmpObjInsert
     * @param objType 检验
     * @param objXml 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpObjInsert(String objType, String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("nmpObjInsert");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
        try
        {
            execNmp.transBegin();
            String id = "";
            if ("NMP_TOPOMAP".equalsIgnoreCase(objType))
            {
                id = retObj.resultVal = new TopoMap().addTopoMap(objXml);
                logOperator("新建监控系统拓扑图ID：" + id);
            }
            else if ("NMP_TOPOTEMPLATE".equalsIgnoreCase(objType))
            {
                id = retObj.resultVal = new TopoTemplate().addTopoTemplate(objXml);
                logOperator("新建拓扑模板ID：" + id);
            }
            else if ("BMP_OBJ2GROUP".equalsIgnoreCase(objType))
            {
                String message = ClassWrapper.wrap(MObject.class).addObj2Group(objXml);
                String[] objId = message.split(",");
                logOperator("将对象ID为" + objId[0] + "的对象添加到ID为" + objId[1] + "的对象组中");
            }
            else if ("NMP_IMAGECLASS".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(ImageClass.class).addImageClass(objXml) + "";
                logOperator("新建拓扑图分类ID：" + id);
            }
            else if ("NMP_TOPOIMAGE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(TopoImage.class).addTopoImage(objXml) + "";
                logOperator("新建监控系统拓扑图标ID：" + id);
            }
            else if ("BMP_ATTRIBUTE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Attribute.class).addAttrib(objXml) + "";
                logOperator("新建监控属性ID：" + id);
            }
            else if ("BMP_DUTY".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Duty.class).addDuty(objXml) + "";
                logOperator("新建值班记录ID：" + id);
            }
            else if ("BMP_DUTYLOG".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(DutyLog.class).addDutyLog(objXml) + "";
                logOperator("新建值班日志ID：" + id);
            }
            else if ("BMP_WORKORDER".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(WorkOrder.class).addWorkOrder(objXml) + "";
                logOperator("新建工单ID：" + id);
            }
            else if ("BMP_WORKORDERPROCESS".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(WorkOrderProcess.class).addWorkOrderProcess(objXml) + "";
                logOperator("新建工单处理ID：" + id);
            }
            else if ("BMP_KNOWLEDGETYPE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(KnowledgeType.class).addKnowledgeType(objXml) + "";
                logOperator("新建知识库类别ID：" + id);
            }
            else if ("BMP_KNOWLEDGE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Knowledge.class).addKnowledge(objXml) + "";
                logOperator("新建知识库文章ID：" + id);
            }
            else if ("NMP_FLOOR".equalsIgnoreCase(objType))
			{
				id = ClassWrapper.wrap(Floor.class).addFloor(objXml);
				logOperator("新建楼层ID:" + id);
			}
            execNmp.transCommit();
        }
        catch (Exception ex)
        {
            execNmp.transRollback();
            logger.debug(objXml);
            errorProcess(retObj, "新增数据失败!", ex);
        }

        return retObj;
    }

    /**
     * 通用修改方法 nmpObjUpdate
     * @param objType 校验
     * @param objXml 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpObjUpdate(String objType, String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("nmpObjUpdate");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
        try
        {
            execNmp.transBegin();
            if ("NMP_TOPOMAP".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoMap().updateTopoMap(objXml);
                logOperator("修改系统拓扑图");
            }
            else if ("NMP_TOPOMAP_XML".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoMap().updateTopoMapXml(objXml);
                logOperator("修改拓扑图的xml文件");
            }
            else if ("NMP_TOPOTEMPLATE".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoTemplate().updateTopoTemplate(objXml);
                logOperator("修改拓扑模板");
            }
            else if ("NMP_TOPOTEMPLATE_XML".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoTemplate().updateTopoTemplateXml(objXml);
                logOperator("修改拓扑模板的xml文件");
            }
            else if ("BMP_OBJATTRIBVALUE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ObjAttrib.class).updateObjAttribStrValue(objXml);
                logOperator("修改监控对象属性值");
            }
            else if ("BMP_VALUETABLE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ValueTable.class).updateValueTable(objXml);
                logOperator("修改属性值字典");
            }
            else if ("NMP_SNMPTRAP".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(SnmpTrap.class).updateSnmpTrap(objXml);
                logOperator("修改TRAP配置");
            }
            else if ("NMP_OBJ2OBJ".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(MObject.class).updateObject2Object(objXml);
                logOperator("修改业务关系");
            }
            else if ("NMP_IMAGECLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ImageClass.class).updateImageClass(objXml);
                logOperator("修改拓扑图标分类");
            }
            else if ("NMP_TOPOIMAGE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(TopoImage.class).updateTopoImage(objXml);
                logOperator("修改监控系统拓扑图信息");
            }
            else if ("BMP_ATTRIBUTE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Attribute.class).updateAttribute(objXml);
                logOperator("修改监控属性");
            }
            else if ("BMP_DUTY".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Duty.class).updateDuty(objXml);
                logOperator("修改值班记录");
            }
            else if ("BMP_DUTYLOG".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(DutyLog.class).updateDutyLog(objXml);
                logOperator("修改值班日志");
            }
            else if ("BMP_WORKORDER".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(WorkOrder.class).updateWorkOrder(objXml);
                logOperator("修改工单状态");
            }
            else if ("BMP_KNOWLEDGETYPE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(KnowledgeType.class).updateKnowledgeType(objXml);
                logOperator("修改知识库类别");
            }
            else if ("BMP_KNOWLEDGE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Knowledge.class).updateKnowledge(objXml);
                logOperator("修改知识库文章");
            }
            else if ("NMP_HOMEPAGE".equalsIgnoreCase(objType))
            {
                retObj.resultVal = ClassWrapper.wrap(HomePage.class).updateHomePage(objXml);
                logOperator("修改主页");
            }
            else if ("NMP_FLOOR".equalsIgnoreCase(objType))
			{
				ClassWrapper.wrap(Floor.class).updateFloor(objXml);
				logOperator("修改楼层");
			}
            else if ("NMP_ROOM".equalsIgnoreCase(objType))
            {
            	ClassWrapper.wrap(Room.class).updateRoom(objXml);
            	logOperator("修改房间");
            }
            execNmp.transCommit();
        }
        catch (Exception ex)
        {
            execNmp.transRollback();
            logger.debug(objXml);
            errorProcess(retObj, "修改数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 通用删除方法 nmpObjDelete
     * @param objType 类型
     * @param objId 对象id
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpObjDelete(String objType, String objId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("nmpObjDelete");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
        try
        {
            execNmp.transBegin();
            if ("NMP_TOPOMAP".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoMap().deleteTopoMap(Integer.parseInt(objId));
                logOperator("删除ID为" + objId + "的系统拓扑");
            }
            else if ("NMP_TOPOTEMPLATE".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoTemplate().deleteTopoTemplate(Integer.parseInt(objId));
                logOperator("删除ID为" + objId + "的拓扑模板");
            }
            else if ("NMP_IMAGECLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ImageClass.class).deleteImageClass(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的拓扑分类");
            }
            else if ("NMP_TOPOIMAGE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(TopoImage.class).deleteTopoImage(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的拓扑图标");
            }
            else if ("BMP_ATTRIBUTE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Attribute.class).deleteAttribute(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的监控属性");
            }
            else if ("BMP_OBJ2GROUP".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Object2Group.class).deleteObjectById(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的对象和它的关系");
            }
            else if ("BMP_DUTY".equalsIgnoreCase(objType))
            {
                if (objId.indexOf(",") == -1)
                {
                    ClassWrapper.wrap(Duty.class).deleteDuty(Integer.valueOf(objId));
                }
                else
                {
                    ClassWrapper.wrap(Duty.class).deleteDutyByCondition(new SqlCondition("DUTY_ID",
                        objId,
                        SqlLogicType.And,
                        SqlRelationType.In,
                        SqlParamType.Numeric));
                }
                logOperator("删除ID为" + objId + "的值班记录");
            }
            else if ("BMP_DUTYLOG".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(DutyLog.class).deleteDutyLog(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的值班日志");
            }
            else if ("BMP_WORKORDER".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(WorkOrder.class).deleteWorkOrder(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的工单");
            }
            else if ("BMP_KNOWLEDGETYPE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(KnowledgeType.class).deleteKnowledgeType(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的知识库类别");
            }
            else if ("BMP_KNOWLEDGE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Knowledge.class).deleteKnowledge(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的知识库文章");
            }
            else if ("NMP_FLOOR".equalsIgnoreCase(objType))
			{
				ClassWrapper.wrap(Floor.class).deleteFloor(Integer.valueOf(objId));
				logOperator("删除楼层ID:" + objId);
			}
            else if ("NMP_ROOM".equalsIgnoreCase(objType))
            {
            	ClassWrapper.wrap(Room.class).deleteRoom(Integer.valueOf(objId));
            	logOperator("删除房间ID:" + objId);
            }
            execNmp.transCommit();
        }
        catch (Exception ex)
        {
            execNmp.transRollback();
            errorProcess(retObj, "删除数据失败!", ex);
        }

        return retObj;
    }

    /**
     * 用户权限
     * @param userId 用户id
     * @param fields 参数
     * @param filter 参数
     * @return 结果
     */
    public WSResult nmpGetFunctionByUserId(int userId, String fields, String filter)
    {
        WSResult retObj = new WSResult();
        try
        {
            retObj.resultVal = uumRemoteService.uumGetFunctionsByUserId(userId, fields, filter);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "查询权限信息失败!", ex);
        }

        return retObj;
    }

    /**
     * 读取配置信息
     * @param sectionNames 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpGetAppConfig(String sectionNames, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        sectionNames = sectionNames.replace("'", "");
        String[] arrTemp = sectionNames.split(",");

        StringBuilder sbConfig = new StringBuilder();
        sbConfig.append("<DataSource>");
        for (String item : arrTemp)
        {
            sbConfig.append(String.format("<%1$s>%2$s</%1$s>", item, ConfigUtil.getProperty(item)));
        }
        sbConfig.append("</DataSource>");
        retObj.resultVal = sbConfig.toString();
        return retObj;
    }

    /**
     * 读取数据库配置信息
     * @param sectionNames 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpGetSysConfig(String sectionNames, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();

        sectionNames = sectionNames.replace("'", "");
        StringBuilder sbCondtion = new StringBuilder();
        String[] arrTemp = sectionNames.split(",");
        for (String item : arrTemp)
        {
            if (!StringUtil.isNullOrEmpty(sbCondtion.toString()))
            {
                sbCondtion.append(" OR ");
            }
            sbCondtion.append("NAME LIKE '");
            sbCondtion.append(item);
            sbCondtion.append("%'");
        }

        try
        {

            if (!StringUtil.isNullOrEmpty(sbCondtion.toString()))
            {
                ISqlExecutor sqlExec = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
                retObj.resultVal =
                    sqlExec.fill("SELECT NAME,DATA FROM NET_SYSCONFIG WHERE " + sbCondtion.toString() + "", "DataSource", "Config").asXML();
            }
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "读取配置信息失败!", ex);
        }

        return retObj;
    }

    /**
     * @param groupIds 对象组ids
     * @param useType 类型
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpDeleteObj2Group(String groupIds, int useType, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrap(MObject.class).deleteObj2Group(groupIds, useType);
            logOperator("删除ID为" + groupIds + "的对象组和对象关系");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "读取拓扑图失败!", ex);
        }

        return retObj;
    }

    /**
     * @param objId 对象id
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpDeleteObjAndSub(int objId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrapTrans(MObjectDal.class).deleteObjAndSub(objId);
            logOperator("删除ID为" + objId + "的对象及其子对象");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "删除数据失败!", ex);
        }

        return retObj;
    }

    /**
     * 批量设置采集间隔
     * @param objAttribIds 对象属性ids
     * @param time 时间
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmpSetCollTime(String objAttribIds, String time, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            new ObjAttrib().setCollTimeBath(objAttribIds, Integer.parseInt(time));
            logOperator("批量设置采集时间间隔");
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "设置采集时间间隔出错", ex);
        }
        return result;
    }

    /**
     * 根据用户ID获取主页信息
     * @param userId 用户id
     * @return 结果
     */
    public WSResult nmpGetHomePageByUserId(int userId)
    {
        WSResult ret = new WSResult();

        try
        {
            ret.resultVal = ClassWrapper.wrap(HomePage.class).getHomePageByUserId(userId);
        }
        catch (Exception e)
        {
            ret.setErrorCode(1);
            ret.setErrorString(e.getMessage());
            errorProcess(ret, "获取主页出错！", e);
        }

        return ret;
    }
    
    public WSResult newRoom(String objXml, String floorId)
    {
        WSResult ret = new WSResult();

        try
        {
            String id = ClassWrapper.wrap(Room.class).addRoom(objXml, floorId);
            logOperator("新建房间ID:" + id);
        }
        catch (Exception e)
        {
            ret.setErrorCode(1);
            ret.setErrorString(e.getMessage());
            errorProcess(ret, "新建房间出错！", e);
        }

        return ret;
    }
    
    public WSResult addTopo2rfs(String mapIds, String rfId, String rfType)
    {
    	WSResult ret = new WSResult();
    	
    	try
    	{
    		String id = ClassWrapper.wrap(Topo2rf.class).addTopo2rf(mapIds, rfId, rfType);
    		logOperator("新建房间拓扑图关联数据ID:" + id);
    	}
    	catch (Exception e)
    	{
    		ret.setErrorCode(1);
    		ret.setErrorString(e.getMessage());
    		errorProcess(ret, "新建房间拓扑图关联数据出错！", e);
    	}
    	
    	return ret;
    }
}
