/************************************************************************
 * 日 期：2011-11-28 
 * 作 者: 
 * 版 本：v1.3 
 * 描 述: BMP通用增删改查
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.services;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import jetsennet.jbmp.business.Action;
import jetsennet.jbmp.business.Alarm;
import jetsennet.jbmp.business.AlarmConfigTemplate;
import jetsennet.jbmp.business.AlarmEvent;
import jetsennet.jbmp.business.AlarmEventLog;
import jetsennet.jbmp.business.AlarmLevel;
import jetsennet.jbmp.business.AlarmType;
import jetsennet.jbmp.business.Alarmlog;
import jetsennet.jbmp.business.Announcement;
import jetsennet.jbmp.business.Attachment;
import jetsennet.jbmp.business.AttribClass;
import jetsennet.jbmp.business.Attribute;
import jetsennet.jbmp.business.AutoDisTask;
import jetsennet.jbmp.business.AutoDiscoryObject;
import jetsennet.jbmp.business.CollectTask;
import jetsennet.jbmp.business.CollectTaskLog;
import jetsennet.jbmp.business.Collector;
import jetsennet.jbmp.business.Comment;
import jetsennet.jbmp.business.CtrlClass;
import jetsennet.jbmp.business.CtrlWord;
import jetsennet.jbmp.business.Department;
import jetsennet.jbmp.business.DiskArray;
import jetsennet.jbmp.business.DiskArrayOwner;
import jetsennet.jbmp.business.Duty;
import jetsennet.jbmp.business.DutyLog;
import jetsennet.jbmp.business.DutyLogAttachment;
import jetsennet.jbmp.business.HomePage;
import jetsennet.jbmp.business.ImageClass;
import jetsennet.jbmp.business.InsObjAttr;
import jetsennet.jbmp.business.Knowledge;
import jetsennet.jbmp.business.KnowledgeType;
import jetsennet.jbmp.business.MObject;
import jetsennet.jbmp.business.Manufacturers;
import jetsennet.jbmp.business.MibBanks;
import jetsennet.jbmp.business.ObjAttrib;
import jetsennet.jbmp.business.ObjGroup;
import jetsennet.jbmp.business.Object2Group;
import jetsennet.jbmp.business.Picture;
import jetsennet.jbmp.business.Port2Port;
import jetsennet.jbmp.business.Project;
import jetsennet.jbmp.business.Report;
import jetsennet.jbmp.business.ReportFile;
import jetsennet.jbmp.business.ReportTime;
import jetsennet.jbmp.business.SnmpNodes;
import jetsennet.jbmp.business.SnmpObjType;
import jetsennet.jbmp.business.Sysconfig;
import jetsennet.jbmp.business.SystemLog;
import jetsennet.jbmp.business.TopoImage;
import jetsennet.jbmp.business.TopoMap;
import jetsennet.jbmp.business.TopoTemplate;
import jetsennet.jbmp.business.TrapTable;
import jetsennet.jbmp.business.ValueTable;
import jetsennet.jbmp.business.WorkOrder;
import jetsennet.jbmp.business.WorkOrderProcess;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.Role2GroupDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AnnouncementEntity;
import jetsennet.jbmp.protocols.linklayer.LinkLayerDisc;
import jetsennet.jbmp.servlets.BMPServletContextListener;
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
import jetsennet.util.StringUtil;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * BMP通用增删改查
 * @author
 */
@WebService(name = "BMPSystemService", serviceName = "BMPSystemService", targetNamespace = "http://JetsenNet/JNMP/")
public class BMPSystemService extends WebServiceBase
{

    private ConnectionInfo bmpConnectionInfo;
    private jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.JBMP");
    private static UUMRemoteService uumRemoteService;

    static
    {
        UUMRemoteService.createUUMRemoteServiceServer();
        uumRemoteService = UUMRemoteService.UUMRemoteServiceInstance();
    }

    /**
     * 构造函数，初始化
     */
    public BMPSystemService()
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
     * 通用读取方法
     * @param queryInfo 信息
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpObjQuery(String queryInfo, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("bmpObjQuery");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        SqlQuery query = SerializerUtil.deserialize(SqlQuery.class, queryInfo);
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

    /**
     * 通用新增方法
     * @param objType 校验
     * @param objXml 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpObjInsert(String objType, String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("bmpObjInsert");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);

        try
        {
            execNmp.transBegin();
            String id = "";
            if ("BMP_CTRLWORD".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(CtrlWord.class).addCtrlWord(objXml);
                logOperator("新建受控词ID：" + id);
            }
            else if ("BMP_ATTRIBUTE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Attribute.class).addAttrib(objXml) + "";
                logOperator("新建属性：" + id);
            }
            else if ("BMP_AUTODISTASK".equalsIgnoreCase(objType))
            {

                id = ClassWrapper.wrap(AutoDisTask.class).addAutoDisTask(objXml);
                logOperator("新建自动发现任务：" + id);
            }
            else if ("BMP_OBJGROUP".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(ObjGroup.class).addObjGroup(objXml) + "";
                logOperator("新建对象组ID：" + id);
            }
            else if ("BMP_ATTRIBCLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AttribClass.class).addAttribClass(objXml);
                logOperator("新建监控属性集");
            }
            else if ("BMP_ALARMTYPE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(AlarmType.class).addAlarmType(objXml) + "";
                logOperator("新建报警类型：" + id);
            }
            else if ("BMP_SNMPOBJTYPE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(SnmpObjType.class).addSnmpObjType(objXml) + "";
                logOperator("新建设备标识：" + id);
            }
            else if ("BMP_MAYNOJB2MANYGROUP".equalsIgnoreCase(objType))
            {
                // 将多个对象添加到多个组中
                ClassWrapper.wrap(MObject.class).addMobj2Mgroup(objXml);
                logOperator("批量将对象添加到对象组");
            }
            else if ("BMP_IMAGECLASS".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(ImageClass.class).addImageClass(objXml) + "";
                logOperator("新建拓扑图分类ID：" + id);
            }
            else if ("BMP_TOPOIMAGE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(TopoImage.class).addTopoImage(objXml) + "";
                logOperator("新建监控系统拓扑图标ID：" + id);
            }
            else if ("BMP_TOPOTEMPLATE".equalsIgnoreCase(objType))
            {
                id = retObj.resultVal = new TopoTemplate().addTopoTemplate(objXml);
                logOperator("新建拓扑模板ID：" + id);
            }
            else if ("BMP_TOPOMAP".equalsIgnoreCase(objType))
            {
                id = retObj.resultVal = new TopoMap().addTopoMap(objXml);
                logOperator("新建监控系统拓扑图ID：" + id);
            }
            else if ("BMP_OBJATTRIB".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(ObjAttrib.class).addObjAttrib(objXml) + "";
                logOperator("新建监控对象属性ID：" + id);
            }
            else if ("BMP_OBJECT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(MObject.class).addObject(objXml);
                logOperator("新建监控对象ID：" + id);
            }
            else if ("NET_SYSCONFIG".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Sysconfig.class).addSysconfig(objXml);
                logOperator("新建系统配置ID：" + id);
            }
            else if ("BMP_MANUFACTURERS".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Manufacturers.class).addManufacturers(objXml);
                logOperator("新建设备厂商ID：" + id);
            }
            else if ("BMP_SNMPNODES".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(SnmpNodes.class).addSnmpNodes(objXml) + "";
                logOperator("新建SNMP节点ID：" + id);
            }
            else if ("BMP_TRAPTABLE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(TrapTable.class).addTrapTable(objXml) + "";
                logOperator("新建TRAP字典表ID：" + id);
            }
            else if ("BMP_MIBBANKS".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(MibBanks.class).addMibBanks(objXml);
                logOperator("新建MIB库ID：" + id);
            }
            else if ("BMP_ACTION".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Action.class).addAction(objXml);
                logOperator("新建报警动作库ID：" + id);
            }
            else if ("BMP_ALARMLEVEL".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(AlarmLevel.class).addAlarmLevel(objXml);
                logOperator("新建报警规则库ID：" + id);
            }
            else if ("BMP_ALARM".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Alarm.class).addAlarm(objXml);
                logOperator("新建报警配置库ID：" + id);
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
            else if ("BMP_KNOWLEDGEATTACHMENT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Attachment.class).addAttachment(objXml) + "";
                logOperator("新建知识库附件ID：" + id);
            }
            else if ("BMP_KNOWLEDGECOMMENT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Comment.class).addComment(objXml) + "";
                logOperator("新建知识库评论ID：" + id);
            }
            else if ("BMP_PROJECT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Project.class).addProject(objXml) + "";
                logOperator("新建项目ID：" + id);
            }
            else if ("BMP_VALUETABLE".equalsIgnoreCase(objType))
            {
                id = Integer.toString(ClassWrapper.wrap(ValueTable.class).addValueTable(objXml));
                logOperator("新建SNMP枚举，ID：" + id);
            }
            else if ("BMP_DEPARTMENT".equalsIgnoreCase(objType))
            {
                id = Integer.toString(ClassWrapper.wrap(Department.class).addDepartment(objXml));
                logOperator("新建科室，ID：" + id);
            }
            else if ("BMP_DISKARRAY".equalsIgnoreCase(objType))
            {
                id = Integer.toString(ClassWrapper.wrap(DiskArray.class).addDiskArray(objXml));
                logOperator("新建盘阵，ID：" + id);
            }
            else if ("BMP_DISKARRAYOWNER".equalsIgnoreCase(objType))
            {
                id = Integer.toString(ClassWrapper.wrap(DiskArrayOwner.class).addDiskArrayOwner(objXml));
                logOperator("新建盘阵拥有者，ID：" + id);
            }
            else if ("BMP_REPORT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Report.class).addReport(objXml);
                logOperator("新建报表ID：" + id);
            }
            else if ("BMP_REPORTTIME".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(ReportTime.class).addReportTime(objXml);
                logOperator("新建定制报表ID：" + id);
            }
            else if ("BMP_REPORTFILE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(ReportFile.class).addReportFile(objXml);
                logOperator("新建报表文件ID：" + id);
            }
            else if ("BMP_COLLECTOR".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Collector.class).addCollector(objXml) + "";
                logOperator("新建数据采集器ID：" + id);
            }
            else if ("BMP_COLLECTTASK".equalsIgnoreCase(objType))
            {
                id = retObj.resultVal = ClassWrapper.wrap(CollectTask.class).addCollectTask(objXml);
                logOperator("新建采集任务ID：" + id);
            }
            else if ("BMP_COLLECTTASKLOG".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(CollectTaskLog.class).addCollectTaskLog(objXml);
                logOperator("新建采集任务ID：" + id);
            }
            else if ("BMP_DUTYLOGATTACHMENT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(DutyLogAttachment.class).addAttachment(objXml);
                logOperator("上传值班日志附件ID：" + id);
            }
            else if ("BMP_CTRLCLASS".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(CtrlClass.class).addCtrlClass(objXml);
                logOperator("新建地区ID：" + id);
            }
            else if ("BMP_PICTURE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Picture.class).addPicture(objXml);
                logOperator("上传图片ID:" + id);
            }
            else if ("BMP_PORT2PORT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(Port2Port.class).addP2p(objXml);
                logOperator("新建ID:" + id);
            }
            else if ("BMP_ALARMEVENT".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(AlarmEvent.class).addAlarmEvent(objXml);
                logOperator("新建报警ID：" + id);
            }
            else if ("BMP_ALARMEVENTLOG".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(AlarmEventLog.class).addAlarmEventLog(objXml);
                logOperator("新建历史报警ID：" + id);
            }
            else if ("BMP_ALARMCONFIGTEMPLATE".equalsIgnoreCase(objType))
            {
                id = ClassWrapper.wrap(AlarmConfigTemplate.class).addAlarmConfigTemplate(objXml);
                logOperator("新建报警配置模板ID：" + id);
                
            }
            retObj.resultVal = id;
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
     * 通用修改方法 bmpObjUpdate
     * @param objType 校验
     * @param objXml 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpObjUpdate(String objType, String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("bmpObjUpdate");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);

        try
        {
            execNmp.transBegin();
            if ("BMP_CTRLWORD".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CtrlWord.class).updateCtrlWord(objXml);
                logOperator("修改受控词");
            }
            else if ("BMP_ATTRIBUTE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Attribute.class).updateAttribute(objXml);
                logOperator("修改属性");
            }
            else if ("BMP_AUTODISTASK".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AutoDisTask.class).updateAutoDisTask(objXml);
                logOperator("修改自动发现任务");
            }
            else if ("BMP_OBJGROUP".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ObjGroup.class).updateObjGroup(objXml);
                logOperator("修改对象组信息");
            }
            else if ("BMP_ATTRIBCLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AttribClass.class).updateAttribClass(objXml);
                logOperator("修改监控属性集");
            }
            else if ("BMP_AUTODISOBJ".equalsIgnoreCase(objType))
            {
                // 新增对BMP_AUTODISOBJ表的修改
                ClassWrapper.wrap(AutoDiscoryObject.class).updateAutoDiscoryObject(objXml);
                logOperator("修改自动发现结果");
            }
            else if ("BMP_ALARMTYPE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AlarmType.class).updateAlarmType(objXml);
                logOperator("修改报警类型");
            }
            else if ("BMP_SNMPOBJTYPE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(SnmpObjType.class).updateSnmpObjType(objXml);
                logOperator("修改设备标识");
            }
            else if ("BMP_HOMEPAGE".equalsIgnoreCase(objType))
            {
                retObj.resultVal = ClassWrapper.wrap(HomePage.class).updateHomePage(objXml);
                logOperator("修改主页");
            }
            else if ("BMP_IMAGECLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ImageClass.class).updateImageClass(objXml);
                logOperator("修改拓扑图标分类");
            }
            else if ("BMP_TOPOIMAGE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(TopoImage.class).updateTopoImage(objXml);
                logOperator("修改监控系统拓扑图信息");
            }
            else if ("BMP_TOPOTEMPLATE".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoTemplate().updateTopoTemplate(objXml);
                logOperator("修改拓扑模板");
            }
            else if ("BMP_TOPOTEMPLATE_XML".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoTemplate().updateTopoTemplateXml(objXml);
                logOperator("修改拓扑模板的xml文件");
            }
            else if ("BMP_TOPOMAP".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoMap().updateTopoMap(objXml);
                logOperator("修改系统拓扑图");
            }
            else if ("BMP_TOPOMAP_XML".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoMap().updateTopoMapXml(objXml);
                logOperator("修改拓扑图的xml文件");
            }
            else if ("BMP_OBJECT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(MObject.class).updateObject(objXml);
                logOperator("修改监控对象信息");
            }
            else if ("BMP_OBJATTRIB".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ObjAttrib.class).updateObjAttrib(objXml);
                logOperator("修改监控对象属性");
            }
            else if ("BMP_OBJATTRIBVALUE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ObjAttrib.class).updateObjAttribStrValue(objXml);
                logOperator("修改监控对象属性值");
            }
            else if ("NET_SYSCONFIG".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Sysconfig.class).updateSysconfig(objXml);
                logOperator("修改系统配置");
            }
            else if ("BMP_MANUFACTURERS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Manufacturers.class).updateManufacturers(objXml);
                logOperator("修改设备厂商信息");
            }
            else if ("BMP_SNMPNODES".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(SnmpNodes.class).updateSnmpNodes(objXml);
                logOperator("修改SNMP节点");
            }
            else if ("BMP_TRAPTABLE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(TrapTable.class).updateTrapTable(objXml);
                logOperator("修改TRAP字典");
            }
            else if ("BMP_MIBBANKS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(MibBanks.class).updateMibBanks(objXml);
                logOperator("修改MIB库信息");
            }
            else if ("BMP_ACTION".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Action.class).updateAction(objXml);
                logOperator("修改报警动作库信息");
            }
            else if ("BMP_ALARMLEVEL".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AlarmLevel.class).updateAlarmLevel(objXml);
                logOperator("修改报警规则库信息");
            }
            else if ("BMP_ALARM".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Alarm.class).updateAlarm(objXml);
                logOperator("修改报警配置库信息");
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
                if (objXml.indexOf("<ONLY_VIEW>true</ONLY_VIEW>") == -1)
                {
                    logOperator("修改知识库文章");
                }
            }
            else if ("BMP_KNOWLEDGECOMMENT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Comment.class).updateComment(objXml);
                logOperator("修改知识库评论");
            }
            else if ("BMP_PROJECT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Project.class).updateProject(objXml);
                logOperator("修改项目");
            }
            else if ("BMP_VALUETABLE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ValueTable.class).updateValueTable(objXml);
                logOperator("修改SNMP枚举");
            }
            else if ("BMP_DEPARTMENT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Department.class).updateDepartment(objXml);
                logOperator("修改科室");
            }
            else if ("BMP_DISKARRAY".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(DiskArray.class).updateDiskArray(objXml);
                logOperator("修改盘阵");
            }
            else if ("BMP_DISKARRAYOWNER".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(DiskArrayOwner.class).updateDiskArrayOwner(objXml);
                logOperator("修改盘阵拥有者");
            }
            else if ("BMP_REPORT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Report.class).updateReport(objXml);
                logOperator("修改报表");
            }
            else if ("BMP_REPORTTIME".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ReportTime.class).updateReportTime(objXml);
                logOperator("修改报表文件");
            }
            else if ("BMP_ANNOUNCEMENT".equalsIgnoreCase(objType))
            {
                Announcement announcement = ClassWrapper.wrap(Announcement.class);
                HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
                // if (map.get("IS_TOP").equals("1"))
                if ("1".equals(map.get("IS_TOP")))
                {
                    announcement.updateBySql();
                }
                announcement.updateAnnouncement(objXml);
                logOperator("修改公告信息");
            }
            else if ("BMP_COLLECTTASK".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CollectTask.class).updateCollectTask(objXml);
                logOperator("修改采集任务");
            }
            else if ("BMP_COLLECTTASKLOG".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CollectTaskLog.class).updateCollectTaskLog(objXml);
                logOperator("修改采集任务");
            }
            else if ("BMP_COLLECTOR".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Collector.class).updateCollector(objXml);
                logOperator("修改数据采集器");
            }
            else if ("BMP_CTRLCLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CtrlClass.class).updateCtrlClass(objXml);
                logOperator(" 编辑地区");
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
     * 通用删除方法 bmpObjDelete
     * @param objType 校验
     * @param objId 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpObjDelete(String objType, String objId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("bmpObjDelete");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);

        try
        {
            execNmp.transBegin();
            if ("BMP_CTRLWORD".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CtrlWord.class).deleteCtrlWord(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的受控词");
            }
            else if ("BMP_ATTRIBUTE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Attribute.class).deleteAttribute(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的属性");
            }
            else if ("BMP_AUTODISTASK".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AutoDisTask.class).deleteAutoDisTask(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的自动发现任务");
            }
            else if ("BMP_OBJGROUP".equalsIgnoreCase(objType))
            {
                if (objId.indexOf(",") != -1)
                {
                    String[] objIds = objId.split(",");
                    for (String strId : objIds)
                    {
                        ClassWrapper.wrap(ObjGroup.class).deleteObjGroup(Integer.valueOf(strId));
                        logOperator("删除ID为" + strId + "的对象组");
                    }
                }
                else
                {
                    ClassWrapper.wrap(ObjGroup.class).deleteObjGroup(Integer.valueOf(objId));
                    logOperator("删除ID为" + objId + "的对象组");
                }
            }
            else if ("BMP_ATTRIBCLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AttribClass.class).deleteAttribClass(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的监控属性集");
            }
            else if ("BMP_ATTRIBCLASSSET".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AttribClass.class).deleteAttribClassSet(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的设备类型");
            }
            else if ("BMP_AUTODISTASK".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AutoDisTask.class).deleteAutoDisTask(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的自动发现任务");
            }
            else if ("BMP_ALARMTYPE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AlarmType.class).deleteAlarmType(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的报警类型");
            }
            else if ("BMP_SNMPOBJTYPE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(SnmpObjType.class).deleteSnmpObjType(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的SNMP设备标识");
            }
            else if ("BMP_IMAGECLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ImageClass.class).deleteImageClass(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的拓扑分类");
            }
            else if ("BMP_TOPOIMAGE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(TopoImage.class).deleteTopoImage(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的拓扑图标");
            }
            else if ("BMP_TOPOTEMPLATE".equalsIgnoreCase(objType))
            {
                // retObj.resultVal = new TopoTemplate().deleteTopoTemplate(Integer.parseInt(objId));
                ClassWrapper.wrap(TopoTemplate.class).deleteTopoTemplate(Integer.parseInt(objId));
                logOperator("删除ID为" + objId + "的拓扑模板");
            }
            else if ("BMP_TOPOMAP".equalsIgnoreCase(objType))
            {
                retObj.resultVal = new TopoMap().deleteTopoMap(Integer.parseInt(objId));
                logOperator("删除ID为" + objId + "的系统拓扑");
            }
            else if ("BMP_OBJECT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(MObject.class).deleteObject(Integer.parseInt(objId));
                logOperator("删除ID为" + objId + "的监控对象");
            }
            else if ("BMP_OBJATTRIB".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ObjAttrib.class).deleteObjAttrib(Integer.parseInt(objId));
                logOperator("删除ID为" + objId + "的监控对象属性");
            }
            else if ("NET_SYSCONFIG".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Sysconfig.class).deleteSysconfig(objId);
                logOperator("删除ID为" + objId + "的系统配置");
            }
            else if ("BMP_MANUFACTURERS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Manufacturers.class).deleteManufacturers(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的设备厂商");
            }
            else if ("BMP_SNMPNODES".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(SnmpNodes.class).deleteSnmpNodes(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的SNMP节点");
            }
            else if ("BMP_TRAPTABLE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(TrapTable.class).deleteTrapTable(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的TRAP字典");
            }
            else if ("BMP_MIBBANKS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(MibBanks.class).deleteMibBanks(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的MIB库信息");
            }
            else if ("BMP_ACTION".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Action.class).deleteAction(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的报警动作库信息");
            }
            else if ("BMP_ALARMLEVEL".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AlarmLevel.class).deleteAlarmLevel(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的报警规则库信息");
            }
            else if ("BMP_ALARM".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Alarm.class).deleteAlarm(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的报警配置库信息");
            }
            else if ("BMP_DUTY".equalsIgnoreCase(objType))
            {
                if (objId.indexOf(",") == -1)
                {
                    ClassWrapper.wrap(Duty.class).deleteDuty(Integer.valueOf(objId));
                }
                else
                {
                    ClassWrapper.wrap(Duty.class).deleteDutyByCondition(
                        new SqlCondition("DUTY_ID", objId, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric));
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
            else if ("BMP_KNOWLEDGEATTACHMENT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Attachment.class).deleteAttachment(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的知识库附件");
            }
            else if ("BMP_KNOWLEDGECOMMENT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Comment.class).deleteComment(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的知识库评论");
            }
            else if ("BMP_PROJECT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Project.class).deleteProject(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的项目");
            }
            else if ("BMP_VALUETABLE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ValueTable.class).deleteValueTable(Integer.valueOf(objId));
                logOperator("删除SNMP枚举，ID：" + objId);
            }
            else if ("BMP_DEPARTMENT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Department.class).deleteDepartment(Integer.valueOf(objId));
                logOperator("删除科室，ID：" + objId);
            }
            else if ("BMP_DISKARRAY".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(DiskArray.class).deleteDiskArray(Integer.valueOf(objId));
                logOperator("删除科室，ID：" + objId);
            }
            else if ("BMP_DISKARRAYOWNER".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(DiskArrayOwner.class).deleteDiskArrayOwner(Integer.valueOf(objId));
                logOperator("删除科室，ID：" + objId);
            }
            else if ("BMP_REPORT".equalsIgnoreCase(objType))
            {
                new Report().deleteReports(objId);
                logOperator("删除ID为" + objId + "的报表");
            }
            else if ("BMP_REPORTTIME".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(ReportTime.class).deleteReportTime(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的报表定制");
            }
            else if ("BMP_REPORTFILE".equalsIgnoreCase(objType))
            {
                // step1 删除下载文件*2013.09.05*
                if (StringUtil.isNullOrEmpty(objId))
                {
                    return retObj;
                }
                ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo); //System.out.println("execBmp : " + execBmp);
                Document ds = null;
                try
                {
                    SqlCondition InCond = new SqlCondition("FILE_ID", objId, SqlLogicType.And, SqlRelationType.In, SqlParamType.String);
                    String inCond = execBmp.getSqlParser().parseSqlCondition(InCond);
                    ds = execBmp.fill("select FILE_PATH,FILE_NAME from BMP_REPORTFILE " + inCond + " "); //System.out.println("ds :" + ds.asXML());
                    // <DataSource><DataTable>
                    // 		<FILE_PATH type="NVARCHAR">E:\jnmp13\jbmp\test</FILE_PATH><FILE_NAME type="NVARCHAR">bz.xsl</FILE_NAME>
                    // </DataTable></DataSource>
                    List<Element> eles = ds.selectNodes("//DataTable");
                    if (!eles.isEmpty())
                    {
                        for (Element ele : eles)
                        {
                            Element filePath = ele.element("FILE_PATH");
                            String path = filePath.getText(); //System.out.println("path : " + path);
                            Element fileName = ele.element("FILE_NAME");
                            String name = fileName.getText(); //System.out.println("name : " + name);
                            File file = new File(path + "\\" + name); //System.out.println("file : " + file);
                            if (file.isFile() && file.exists())
                            { //System.out.println("执行删除操作、、、");                                 
                                file.delete();
                            }
                        }
                    }
                }
                catch (SQLException e)
                {
                    logger.debug("删除下载文件失败！");
                }

                // step2 删除数据库中相关数据	注意：FILE_ID的类型是varchar，所以不需要转化Integer.valueOf(strId)
                if (objId.indexOf(",") != -1)
                {
                    String[] objIds = objId.split(",");
                    for (String strId : objIds)
                    {
                        ClassWrapper.wrap(ReportFile.class).deleteReportFile(strId);
                        logOperator("删除ID为" + strId + "的报表文件");
                    }
                }
                else
                {
                    ClassWrapper.wrap(ReportFile.class).deleteReportFile(objId);
                    logOperator("删除ID为" + objId + "的报表文件");
                }
            }
            else if ("NET_OPERATORLOG".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(SystemLog.class).deleteSystemLog(objId);
                logOperator("删除ID为" + objId + "的日志信息");
            }
            else if ("BMP_ANNOUNCEMENT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Announcement.class).deleteAnnouncement(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的公告");
            }
            else if ("BMP_COLLECTTASK".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CollectTask.class).deleteCollectTask(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的采集任务");
            }
            else if ("BMP_COLLECTTASKLOG".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CollectTaskLog.class).deleteCollectTaskLog(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的采集任务");
            }
            else if ("BMP_COLLECTOR".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Collector.class).deleteCollector(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的数据采集器");
            }
            else if ("BMP_DUTYLOGATTACHMENT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(DutyLogAttachment.class).deleteAttachment(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的值班日志文件");
            }
            else if ("BMP_CTRLCLASS".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CtrlClass.class).deleteCtrlClass(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的地区");
            }
            else if ("BMP_PICTURE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Picture.class).deletePicture(Integer.valueOf(objId));
                logOperator("删除图片ID:" + objId);
            }
            else if ("BMP_PORT2PORT".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(Port2Port.class).deleteP2ps(objId);
                logOperator("删除ID:" + objId);
            }
            else if ("BMP_ALARMCONFIGTEMPLATE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(AlarmConfigTemplate.class).deleteAlarmConfigTemplate(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的报警配置模板");
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
     * 从组中批量删除对象
     * @param ids 删除的ids
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpDelObjectFromGroup(String ids, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        try
        {
            String[] groupIdAndObjectIds = ids.split("_");
            int groupId = Integer.parseInt(groupIdAndObjectIds[0]);
            String[] objecIdsStr = groupIdAndObjectIds[1].split(",");
            int[] objectIds = new int[objecIdsStr.length];
            for (int i = 0; i < objecIdsStr.length; i++)
            {
                objectIds[i] = Integer.parseInt(objecIdsStr[i]);
            }

            ClassWrapper.wrap(Object2Group.class).deleteObjectFromGroup(groupId, objectIds);
            String id = "";
            for (int j = 0; j < objecIdsStr.length; j++)
            {
                if (j == (objecIdsStr.length - 1))
                {
                    id += objecIdsStr[j];
                }
                else
                {
                    id += objecIdsStr[j] + ",";
                }
            }
            logOperator("删除：从ID为" + groupId + "的对象组中删除ID为" + id + "的对象");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "删除出错!", ex);
        }
        return retObj;
    }

    /**
     * 添加指定的对象属性 如果NMP_INDEXALARM中存在该分类与该属性关联的告警，则插入新对象属性与该告警的关联关系到BMP_ATTRIBALARM
     * @param classId 对象属性所属父对象的分类ID
     * @param objAttrXml 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpInsertObjAttrib(int classId, String objAttrXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();

        try
        {
            if (classId != 0)
            {
                new InsObjAttr().insertObjAttrib(classId, objAttrXml);
                logOperator("新建对象属性");
            }
            else
            {
                new InsObjAttr().insertAttrib(objAttrXml);
            }
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "新增对象属性失败!", ex);
        }

        return retObj;
    }

    /**
     * 新建对象时将对象添加到对象组中
     * @param ids 参数
     * @return 结果
     */
    public WSResult bmpAddObj2ObjGroup(String ids)
    {
        WSResult result = new WSResult();
        try
        {
            ClassWrapper.wrap(MObject.class).addMobj2Mgroup(ids);
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "将对象添加到对象组出错", ex);
        }
        return result;
    }

    /**
     * 删除对象时，删除相关联的东西
     * @param objIdStr 参数
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpDeleteObjAndRelation(String objIdStr, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            int objId = Integer.parseInt(objIdStr);
            new ObjAttrib().deleteObjAttribByObjId(objId);
            logOperator("删除ID为" + objIdStr + "的对象及关联关系");
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "删除对象及关联时出错", ex);
        }
        return result;
    }

    /**
     * 获得设备厂商
     * @param classId 参数
     * @return 结果
     */
    public WSResult bmpGetMaunfactureByClassId(String classId)
    {
        WSResult result = new WSResult();
        try
        {
            String maiId = ClassWrapper.wrap(AttribClass.class).getManufacturersByClassId(classId) + "";
            result.setResultVal(maiId + "");
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "获取设备厂商错误", ex);
        }
        return result;
    }

    /**
     * 获取对象所属组
     * @param objId 对象id
     * @return 结果
     */
    public WSResult bmpGetSelGroupStr(int objId)
    {
        MObjectDal modao = ClassWrapper.wrapTrans(MObjectDal.class);
        WSResult result = new WSResult();
        try
        {
            String s = modao.getSelGroup(objId);
            result.setErrorCode(0);
            result.setResultVal(s);
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
        }
        return result;
    }

    /**
     * 根据用户ID获取主页信息
     * @param userId 用户id
     * @return 结果
     */
    public WSResult bmpGetHomePageByUserId(int userId)
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

    /**
     * 获取对象所属的组
     * @param objId 用户id
     * @return 结果
     */
    public WSResult bmpQueryObjGroup(int objId)
    {
        MObjectDal modao = ClassWrapper.wrapTrans(MObjectDal.class);
        WSResult result = new WSResult();
        try
        {
            String s = modao.getGroup(objId);
            result.setErrorCode(0);
            result.setResultVal(s);
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
        }
        return result;
    }

    /**
     * 获取对象所属的组
     * @param desc 参数
     * @param oid 参数
     * @return 结果
     */
    public WSResult bmpUpdateNodeByOID(String oid, String desc)
    {
        WSResult result = new WSResult();
        try
        {
            MibBanks mb = ClassWrapper.wrapTrans(MibBanks.class);
            mb.updateNodeByOID(oid, desc);
            result.setErrorCode(0);
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
        }
        return result;
    }

    /**
     * 修改该OID的所有数据的中文名称和中文描述
     * @param OID 参数
     * @param NAME_CN 参数
     * @param DESC_CN 参数
     * @return 结果
     */
    public WSResult bmpUpdateMibTrapNodeByOID(String OID, String NAME_CN, String DESC_CN)
    {
        WSResult result = new WSResult();
        try
        {
            TrapTable mb = ClassWrapper.wrapTrans(TrapTable.class);
            mb.updateMibTrapNodeByOID(OID, NAME_CN, DESC_CN);
            result.setErrorCode(0);
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
        }
        return result;
    }

    /**
     * 根据编号修改盘阵拥有者拥有容量大小
     * @param ownerNo 拥有者
     * @param size 大小
     * @return 结果
     */
    public WSResult updateDiskArrayOwner(String ownerNo, int size)
    {
        WSResult result = new WSResult();
        try
        {
            DiskArrayOwner diskArrayOwner = ClassWrapper.wrapTrans(DiskArrayOwner.class);
            diskArrayOwner.updateDiskArrayOwner(ownerNo, size);
            result.setErrorCode(0);
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
        }
        return result;
    }

    /*
     * 新建公告
     */
    /**
     * @param title 标题
     * @param content 内容
     * @param top 参数
     * @param createUser 创建者
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult nmp_addAnnoucement(String title, String content, String top, String createUser, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            AnnouncementEntity entity = new AnnouncementEntity();
            entity.setAnnouncementTitle(title);
            entity.setAnnouncementContent(content);
            entity.setTop(Integer.parseInt(top));
            entity.setCreateUser(createUser);
            entity.setCreateTime(new Date());
            Announcement announcement = ClassWrapper.wrap(Announcement.class);
            if (entity.getTop() == 1)
            {
                announcement.updateBySql();
            }
            String id = announcement.addAnnouncement(entity);
            logOperator("新建公告ID：" + id);
        }
        catch (Exception e)
        {
            result.setErrorCode(1);
            result.setErrorString(e.getMessage());
            errorProcess(result, "新建公告出错！", e);
        }
        return result;
    }

    /**
     * @param taskId 任务
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpStartCollectTask(String taskId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrap(CollectTask.class).startCollectTask(Integer.parseInt(taskId));
            logOperator("启动采集任务");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "启动采集任务出错!", ex);
        }
        return retObj;
    }

    /**
     * @param taskId 任务
     * @param objId 对象id
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpStartObjectTask(String taskId, String objId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrap(CollectTask.class).startCollectTask(Integer.parseInt(taskId), Integer.parseInt(objId));
            logOperator("启动采集任务");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "启动采集任务出错!", ex);
        }
        return retObj;
    }

    /**
     * @param taskId 任务
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpStopCollectTask(String taskId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrap(CollectTask.class).stopCollectTask(Integer.parseInt(taskId));
            logOperator("停止采集任务");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "停止采集任务出错!", ex);
        }
        return retObj;
    }

    /**
     * @param taskId 任务
     * @param objId 对象id
     * @param userAuth 权限
     * @return 结果
     * @return
     */
    public WSResult bmpStopObjectTask(String taskId, String objId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrap(CollectTask.class).stopCollectTask(Integer.parseInt(taskId), Integer.parseInt(objId));
            logOperator("停止采集任务");
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "停止采集任务出错!", ex);
        }
        return retObj;
    }

    /**
     * @param taskId 任务
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpGetCollectTaskInfo(String taskId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            String info = ClassWrapper.wrap(CollectTask.class).getCollectTaskInfo(Integer.parseInt(taskId));
            result.setErrorCode(0);
            result.setResultVal(info);
        }
        catch (Exception ex)
        {
            errorProcess(result, "获取采集任务信息出错!", ex);
        }
        return result;
    }

    /**
     * 采集器信息读取
     * @param queryInfo 信息
     * @return 结果
     */
    public WSResult bmpCollectorList(String queryInfo)
    {
        WSResult retObj = new WSResult();
        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        SqlQuery query = SerializerUtil.deserialize(SqlQuery.class, queryInfo);
        Document ds = null;
        try
        {
            ds = execBmp.fill(query);
            List<Element> eles = ds.selectNodes("//Record");
            for (Element ele : eles)
            {
                Element collElement = ele.element("COLL_ID");
                int tempId = Integer.valueOf(collElement.getText());
                Element onlineEle = ele.addElement("IS_ONLINE");
                boolean isOnline = BMPServletContextListener.getInstance().isOnline(tempId);
                if (isOnline)
                {
                    onlineEle.setText("1");
                }
                else
                {
                    onlineEle.setText("0");
                }
            }
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
     * 根据用户查询权限信息
     * @param userId 用户ID
     * @param fields 查询字段
     * @param filter 查询条件XML
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpGetFunctionByUserId(int userId, String fields, String filter, @WebParam(header = true) UserAuthHeader userAuth)
    {
        WSResult retObj = new WSResult();
        try
        {
            retObj.resultVal = uumRemoteService.uumGetFunctionsByUserId(userId, fields, filter);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "取得用户权限信息失败!", ex);
        }
        return retObj;
    }

    /**
     * 判断portAID和portBID这连个端口是否连接
     * @param portAID 参数
     * @param portBID 参数
     * @param userAuth 参数
     * @return 结果
     */
    public WSResult p2pIsExists(String portAID, String portBID, @WebParam(header = true) UserAuthHeader userAuth)
    {
        WSResult retObj = new WSResult();
        try
        {
            retObj.resultVal = "" + ClassWrapper.wrap(Port2Port.class).p2pIsExists(portAID, portBID);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "取得用户权限信息失败!", ex);
        }
        return retObj;
    }

    /**
     * 刷新子网设备连接关系连接关系
     * @param groupId 参数
     * @return 结果
     */
    public WSResult refreshPortLink(int groupId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("refreshPortLink");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }
        LinkLayerDisc disc = new LinkLayerDisc();
        disc.disc(groupId);
        return retObj;
    }

    /**
     * 通用批量删除方法 bmpObjDeleteMany
     * @param objType 类型
     * @param objIds 参数id
     * @param userAuth 权限
     * @return 结果
     */
    public WSResult bmpObjDeleteMany(String objType, String objIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("bmpObjDeleteMany");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }
        String[] idArray = objIds.split(",");
        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);

        try
        {
            execNmp.transBegin();
            if ("BMP_ALARMLOG".equalsIgnoreCase(objType))
            {
                String sql = "DELETE FROM BMP_ALARMLOG WHERE ID IN ( " + objIds + ")";
                ClassWrapper.wrap(Alarmlog.class).deleteAlarmlogMany(sql);
                logOperator("批量删除报警日志ID:" + objIds);
            }
            else if ("BMP_OBJATTRIB".equalsIgnoreCase(objType))
            {
                for (String id : idArray)
                {
                    ClassWrapper.wrap(ObjAttrib.class).deleteObjAttrib(Integer.parseInt(id));
                    logOperator("删除的对象属性ID为" + id);
                }
            }
            else if ("BMP_OBJECTMANY".equalsIgnoreCase(objType))
            {
                for (String id : idArray)
                {
                    int objId = Integer.parseInt(id);
                    new ObjAttrib().deleteObjAttribByObjId(objId);
                    logOperator("删除ID为" + id + "的对象及关联关系");
                }
            }
            else if ("BMP_WORKORDER".equalsIgnoreCase(objType))
            {
                for (String id : idArray)
                {
                    ClassWrapper.wrap(WorkOrder.class).deleteWorkOrder(Integer.valueOf(id));
                    logOperator("删除ID为" + id + "的工单");
                }
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
     * 判断是否是管理员
     * @param userId 用户id
     * @return 结果
     */
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
            logOperator("判断用户是否是管理员出错!");
        }
        int resultCount = ds.selectNodes("//DataTable").size();
        return resultCount > 0 ? true : false;
    }

    /**
     * 判断资源类型中设备下是否有子设备
     * @param keyId id
     * @return 结果
     */
    public boolean isEquipmentChild(int keyId)
    {
        boolean result = false;
        try
        {
            result = ClassWrapper.wrap(AttribClass.class).AttribClassCHild(Integer.valueOf(keyId));
        }
        catch (Exception e)
        {
            logOperator("判断资源类型中设备下是否有子设备出错!");
        }
        return result;
    }

    /**
     * 通用更新方法
     * @param sql 
     * @return 结果
     */
    public WSResult bmpUpdateBySql(String sql, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("bmpObjQuery");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }

        ISqlExecutor execBmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        try
        {
            execBmp.executeNonQuery(sql);
        }
        catch (Exception ex)
        {
            logger.debug(sql);
            errorProcess(retObj, "更新数据列表失败!", ex);
        }
        return retObj;
    }

    /**
     * 用于首页获取报警事件
     * 
     * @param topNum 条数
     * @param isAdmin 是否为管理员
     * @param objIds 对象ID
     * @return
     */
    public WSResult bmpIndexLastAlarm(int topNum, boolean isAdmin, String objIds)
    {
        WSResult retObj = new WSResult();
        try
        {
            AlarmEvent ae = ClassWrapper.wrapTrans(AlarmEvent.class);
            retObj.resultVal = ae.indexAlarmEvent(topNum, isAdmin, objIds);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            errorProcess(retObj, "获取报警数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 获取用户可访问的资源ID
     * 
     * @param userId
     * @return
     */
    public WSResult bmpGetUserGroup(int userId)
    {
        WSResult retObj = new WSResult();
        try
        {
            Role2GroupDal rgdal = ClassWrapper.wrapTrans(Role2GroupDal.class);
            String objIdS = rgdal.getObjIdsByUserId(userId);
            retObj.resultVal = objIdS;
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            errorProcess(retObj, "获取用户可访问资源ID失败!", ex);
        }
        return retObj;
    }

    /**
     * 关联已有报警规则
     * @param ids 要进行关联已有报警规则的属性id或指标id
     * @param idType id的类型，用来区分是属性还是指标
     * @param oldAlarmId 要进行复制操作的报警规则id
     * @param userAuth
     */
    public WSResult relateExistAlarm(String ids, String idType, int oldAlarmId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        WSResult retObj = new WSResult();
        try
        {
            retObj.resultVal = ClassWrapper.wrapTrans(Alarm.class).relateExistAlarm(ids, idType, oldAlarmId);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            errorProcess(retObj, "关联已有报警操作失败!", ex);
        }
        return retObj;
    }
}
