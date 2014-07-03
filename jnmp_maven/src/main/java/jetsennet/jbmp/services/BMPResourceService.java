/************************************************************************
 * 日 期：2011-11-28 
 * 作 者: 
 * 版 本：v1.3 
 * 描 述: 
 * 历 史：
 ************************************************************************/

package jetsennet.jbmp.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.dom4j.Document;

import jetsennet.jbmp.autodiscovery.AutoDisConfigColl;
import jetsennet.jbmp.autodiscovery.AutoDisMethod;
import jetsennet.jbmp.business.AlarmStatistic;
import jetsennet.jbmp.business.Alarmlog;
import jetsennet.jbmp.business.InsObjAttr;
import jetsennet.jbmp.business.Knowledge;
import jetsennet.jbmp.business.MObject;
import jetsennet.jbmp.business.ObjAttrib;
import jetsennet.jbmp.business.ObjAttribute;
import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.AlarmEventLogDal;
import jetsennet.jbmp.dataaccess.AttribAlarmDal;
import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.AutoDisTaskDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.Obj2GroupDal;
import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmEventLogEntity;
import jetsennet.jbmp.entity.AlarmlogEntity;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.formula.FormulaValidate;
import jetsennet.jbmp.ins.InsManager;
import jetsennet.jbmp.ins.SubObjInsInfo;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.util.ArrayUtils;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.ConfigUtil;
import jetsennet.jbmp.util.ConvertUtil;
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
import jetsennet.sqlclient.SqlExecutor;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author
 */
@WebService(name = "BMPResourceService", serviceName = "BMPResourceService", targetNamespace = "http://JetsenNet/JNMP/")
public class BMPResourceService extends WebServiceBase
{

    private ConnectionInfo bmpConnectionInfo;
    private jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.JBMP");
    private static UUMRemoteService uumRemoteService;
    /**
     * 报表工程名
     */
    private String reportProName = ConfigUtil.getReportProName();
    /**
     * 报表工程ip
     */
    private String reportProIp = ConfigUtil.getReportProIp();
    /**
     * 报表工程端口
     */
    private String reportProPort = ConfigUtil.getReportProPort();

    static
    {
        UUMRemoteService.createUUMRemoteServiceServer();
        uumRemoteService = UUMRemoteService.UUMRemoteServiceInstance();
    }

    public BMPResourceService()
    {
        bmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("bmp_driver"),
                DbConfig.getProperty("bmp_dburl"),
                DbConfig.getProperty("bmp_dbuser"),
                DbConfig.getProperty("bmp_dbpwd"));
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
     * 批量修改属性的报警关联
     * @param alarmId
     * @param objattrIds
     * @return
     */
    public WSResult bmpUpdateAttribAlarm(int alarmId, String objattrIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            if (objattrIds != null && !objattrIds.isEmpty())
            {
                ClassWrapper.wrapTrans(AttributeDal.class).updateAttrAlarm(alarmId, objattrIds);
            }
            logOperator("批量修改关联报警");
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "关联报警出错", ex);
        }
        return result;
    }

    /**
     * 自动发现
     * @param collId 采集器ID
     * @return
     */
    public WSResult bmpAutoDisc(int taskId, int collId, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        AutoDisTaskDal adtdal = ClassWrapper.wrapTrans(AutoDisTaskDal.class);
        try
        {
            adtdal.updateState(taskId, AutoDisTaskEntity.STATUS_START);
            int userId = userAuth.getUserId();
            String userName = userAuth.getLoginId();
            AutoDisMethod dis = AutoDisMethod.getInstance();
            String msg = dis.remoteAutoDis(taskId, collId, userId, userName);
            if (msg == null)
            {
                result.setErrorCode(0);
            }
            else
            {
                result.setErrorCode(1);
                result.setErrorString(msg);
            }
        }
        catch (Throwable e)
        {
            result.setErrorCode(1);
            result.setErrorString(e.getMessage());
        }
        return result;
    }

    /**
     * 公式验证
     * @param formula
     * @param classType
     * @param attribId
     * @return
     */
    public WSResult bmpValidateFormula(String formula, int subClassId, int attribId)
    {
        WSResult retval = new WSResult();

        AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
        int mibId = BMPConstants.DEFAULT_MIB_NAME_ID;
        AttribClassEntity ac = null;
        try
        {
            if (subClassId <= 0)
            {
                if (attribId > 0)
                {
                    ac = acdal.getByAttribId(attribId);
                }
            }
            else
            {
                ac = acdal.get(subClassId);
            }
        }
        catch (Exception ex)
        {
            logger.debug(ex.toString());
        }
        if (ac != null)
        {
            mibId = ac.getMibId();
        }

        FormulaValidate fv = new FormulaValidate();
        try
        {
            int temp = fv.validate(formula, mibId);
            retval.resultVal = Integer.toString(temp);
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
            errorProcess(retval, "", ex);
        }
        return retval;
    }

    /**
     * 根据属性分类ID，和属性ID确定该使用的MIB库
     * @param classId
     * @return
     */
    public WSResult bmpEnsureMibBank(int subClassId, int attribId)
    {
        WSResult retval = new WSResult();
        try
        {
            AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
            int mibId = BMPConstants.DEFAULT_MIB_NAME_ID;
            AttribClassEntity ac = null;
            if (subClassId <= 0)
            {
                if (attribId > 0)
                {
                    ac = acdal.getByAttribId(attribId);
                }
            }
            else
            {
                ac = acdal.get(subClassId);
            }
            if (ac != null)
            {
                mibId = ac.getMibId();
            }
            retval.resultVal = Integer.toString(mibId);
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
            errorProcess(retval, "", ex);
        }
        return retval;
    }

    /**
     * 批量导入Trap
     * @param classId
     * @return
     */
    public WSResult bmpTrapImport(String trapIds, String user, int classId)
    {
        WSResult retval = new WSResult();
        try
        {
            TrapTableDal ttdal = ClassWrapper.wrapTrans(TrapTableDal.class);
            List<TrapTableEntity> traps = ttdal.getTraps(trapIds);
            if (traps != null)
            {
                AttributeDal adal = ClassWrapper.wrapTrans(AttributeDal.class);
                Date now = new Date();
                AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
                AttribClassEntity ac = acdal.get(classId);
                if (ac == null)
                {
                    return retval;
                }
                List<AttributeEntity> attrs = new ArrayList<AttributeEntity>();
                for (TrapTableEntity trap : traps)
                {
                    AttributeEntity attr = MibUtil.trapToAttr(trap, ac, user, now);
                    attrs.add(attr);
                }
                adal.insert(attrs, classId);
            }
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
            errorProcess(retval, "", ex);
        }
        return retval;
    }

    /**
     * 添加对象，并调用自动实例化
     * @param objType
     * @param objXml
     * @param userAuth
     * @return
     */
    public WSResult bmpInsertAndInstanceObject(String objType, String objXml, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("insertAndInsObject");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        String checkuserful = model.get("CHECKUSEFUL");// 获得通断性检查的时间间隔
        int groupId = -1;// 采集组Id
        int collId = -1;// 采集器ID
        try
        {
            groupId = Integer.parseInt(model.get("COLLGROUP_ID"));
            collId = Integer.valueOf(model.get("COLL_ID"));
        }
        catch (Exception e)
        {
            errorProcess(retObj, "", e);
        }

        String selectGroupIds = model.get("GROUP_ID");
        try
        {
            if (groupId != -1 && collId != -1)
            {
                MObjectEntity mo = ClassWrapper.wrap(MObject.class).addObj(objXml);
                InsManager.getInstance().autoIns(mo, null, groupId, collId, userAuth.getUserId(), null, false);
                if (selectGroupIds != null && !"".equals(selectGroupIds) && (mo != null))
                {
                    ClassWrapper.wrap(MObject.class).addMobj2Mgroup(Integer.toString(mo.getObjId()), selectGroupIds);
                }
                if (checkuserful != null)
                {
                    ClassWrapper.wrap(ObjAttrib.class).setCheckCollTime(mo.getObjId(), checkuserful);
                }
                retObj.resultVal = String.valueOf(mo.getObjId());
                logOperator("新增监控对象ID：" + mo.getObjId());
            }
        }
        catch (Exception ex)
        {
            logger.debug(objXml);
            errorProcess(retObj, "", ex);
        }
        return retObj;
    }

    /**
     * 实例化属性
     * @param objId 要实例化的对象
     * @param attrIds 要实例化的属性
     * @return
     */
    public WSResult bmpInstanceAttribute(int objId, String attrIds)
    {
        WSResult result = new WSResult();
        try
        {
            result.resultVal = new InsObjAttr().instanceAttribute(objId, attrIds);
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "实例化出错", ex);
        }
        return result;
    }

    /**
     * 批量解除关联告警
     * @param alarmId
     * @param objattrIds
     * @return
     */
    public WSResult bmpDeleteAttribAlarm(String objattrIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            if (!StringUtil.isNullOrEmpty(objattrIds))
            {
                String[] objattr = objattrIds.split(",");
                if (objattr != null && objattr.length > 0)
                {
                    for (String objattrId : objattr)
                    {
                        ClassWrapper.wrapTrans(AttribAlarmDal.class).deleteByObjAttribID(Integer.valueOf(objattrId));
                    }
                }
                logOperator("批量解除报警关联：" + objattrIds);
            }
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "批量解除报警关联出错：" + objattrIds, ex);
        }
        return result;
    }

    /**
     * 批量关联告警
     * @param alarmId
     * @param objattrIds
     * @return
     */
    public WSResult bmpAddAttribAlarm(int alarmId, String objattrIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            if (!StringUtil.isNullOrEmpty(objattrIds))
            {
                String[] objattr = objattrIds.split(",");
                ArrayList<String> objattrIdList = ArrayUtils.stringToStringArrayList(objattr);

                ClassWrapper.wrapTrans(AttribAlarmDal.class).insert(alarmId, objattrIdList);
                logOperator("批量关联报警。对象属性ID：" + objattrIds + "。报警ID：" + alarmId);
            }
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "批量关联报警出错。对象属性ID：" + objattrIds + "。报警ID：" + alarmId, ex);
        }
        return result;
    }

    /**
     * 获取MIB库对应的MIB文件信息 结果格式为 file1:file2:file3?file4:file5:file6 问号前面是未加载的文件，问号后面是已经加载的文件
     * @param mibId
     * @return
     */
    public WSResult bmpGetMibFileInfo(int mibId)
    {
        WSResult retval = new WSResult();
        String str = MibUtil.getMibFileInfo(mibId);
        retval.setResultVal(str);
        return retval;
    }

    /**
     * 加载MIB文件
     * @param mibId
     * @param str
     * @return
     */
    public WSResult bmpParseMib(int mibId, String str)
    {
        WSResult retval = new WSResult();
        try
        {
            MibUtil.updateOrInsertMib(mibId, str);
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
            retval.setErrorString(ConvertUtil.chopWhitespace(ex.getMessage()));
        }
        return retval;
    }

    /**
     * 获取MIB文件列表
     * @return
     */
    public WSResult bmpGetMibFileList()
    {
        WSResult retval = new WSResult();
        try
        {
            String list = MibUtil.getMibFileListString();
            retval.setResultVal(list);
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
            retval.setErrorString(ex.getMessage());
            errorProcess(retval, "读取MIB文件列表失败！", ex);
        }
        return retval;
    }

    /**
     * 删除MIB文件
     * @return
     */
    public WSResult bmpDeleteMibFile(String fileName)
    {
        WSResult retval = new WSResult();
        try
        {
            boolean success = MibUtil.deleteMibFile(fileName);
            if (!success)
            {
                retval.setErrorCode(1);
                retval.setErrorString("删除文件<" + fileName + ">失败！");
            }
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
            retval.setErrorString(ex.getMessage());
            errorProcess(retval, "删除文件<" + fileName + ">失败！", ex);
        }
        return retval;
    }

    /**
     * 批量删除MIB文件
     * @param prop
     * @return
     */
    public WSResult bmpDelMibFile(String fileStr, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retval = new WSResult();
        if (fileStr == null || fileStr.isEmpty())
        {
            return retval;
        }
        String[] fileNames = fileStr.split(";");
        try
        {
            for (int i = 0; i < fileNames.length; i++)
            {
                MibUtil.deleteMibFile(fileNames[i]);
            }
            logOperator("批量删除MIB文件：" + fileStr);
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
            retval.setErrorString(ex.getMessage());
            errorProcess(retval, "批量删除MIB文件出错：" + fileStr, ex);
        }
        return retval;
    }

    /**
     * 清除报警事件
     * @param objType
     * @param objXml
     * @param eventIds
     * @param userAuth
     * @return
     */
    public WSResult bmpRemoveAlarmEvent(String objType, String objXml, String eventIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();

        if (StringUtil.isNullOrEmpty(eventIds))
        {
            return retObj;
        }

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, objType);

        ISqlExecutor sqlExecutor = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            sqlExecutor.transBegin();
            User u = new User();
            String userName = userAuth.getLoginId();

            AlarmEventDal aedal = new AlarmEventDal();
            AlarmEventLogDal aeldal = new AlarmEventLogDal();
            Alarmlog adal = new Alarmlog();

            // 获取全部报警
            List<AlarmEventEntity> alarmEvents =
                aedal.getLst(new SqlCondition("ALARMEVT_ID", eventIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric));
            int eventSize = alarmEvents.size();
            for (int i = 0; i < eventSize; i++)
            {
                AlarmEventEntity event = alarmEvents.get(i);

                int isDel = aedal.delete(event.getAlarmEvtId());
                if (isDel > 0)
                {
                    // 将报警插入LOG表
                    if (model.get("TYPE") == null || model.get("TYPE") == "")
                    {

                        AlarmEventLogEntity eventLog = new AlarmEventLogEntity(event);
                        eventLog.setEventState(Integer.valueOf(model.get("EVENT_STATE")));
                        eventLog.setCheckUserId(Integer.valueOf(model.get("CHECK_USERID")));
                        eventLog.setCheckUser(userName);
                        eventLog.setCheckDesc(model.get("CHECK_DESC"));
                        eventLog.setCheckTime(new Date());
                        aeldal.insert(eventLog, false);
                        // 添加日志
                        AlarmlogEntity log = new AlarmlogEntity();
                        log.setAlarmevtId(eventLog.getAlarmEvtId());
                        log.setOperate("报警清除");
                        log.setUserid(eventLog.getCheckUserId());
                        log.setOperateTime(new Date());
                        log.setOperatetype(2);
                        log.setOperateUser(userName);
                        adal.addAlarmlogMany(log);
                        logOperator("插入报警清除日志，报警ID：" + eventIds);
                    }

                    // 从内存删掉相应对象的相应级别告警数目
                    AlarmStatistic.getInstance().removeAlarm(event);
                    logOperator("清除ID为" + eventIds + "的报警事件。");

                }
            }
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            logger.debug(objXml);
            errorProcess(retObj, "清除报警失败!", ex);
        }
        return retObj;
    }

    /**
     * 处理报警事件
     * @param objType
     * @param objXml
     * @param eventIds
     * @param userAuth
     * @return
     */
    public WSResult bmpCheckAlarmEvent(String objType, String objXml, String eventIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();

        if (StringUtil.isNullOrEmpty(eventIds))
        {
            return retObj;
        }

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, objType);

        ISqlExecutor sqlExecutor = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            sqlExecutor.transBegin();
            User u = new User();
            String userName = userAuth.getLoginId();

            AlarmEventDal aedal = new AlarmEventDal();
            AlarmEventLogDal aeldal = new AlarmEventLogDal();
            Alarmlog adal = new Alarmlog();

            // 获取全部报警
            List<AlarmEventEntity> alarmEvents =
                aedal.getLst(new SqlCondition("ALARMEVT_ID", eventIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric));
            int eventSize = alarmEvents.size();
            for (int i = 0; i < eventSize; i++)
            {
                AlarmEventEntity event = alarmEvents.get(i);

                int isDel = aedal.delete(event.getAlarmEvtId());
                if (isDel > 0)
                {
                    // 将报警插入LOG表
                    AlarmEventLogEntity eventLog = new AlarmEventLogEntity(event);
                    eventLog.setEventState(Integer.valueOf(model.get("EVENT_STATE")));
                    if (model.get("EVENT_TYPE") != null && model.get("EVENT_TYPE") != "")
                    {
                        eventLog.setEventType(Integer.valueOf(model.get("EVENT_TYPE")));
                    }
                    eventLog.setCheckUserId(Integer.valueOf(model.get("CHECK_USERID")));
                    eventLog.setCheckUser(userName);
                    eventLog.setCheckDesc(model.get("CHECK_DESC"));
                    eventLog.setCheckTime(new Date());
                    aeldal.insert(eventLog, false);

                    // 从内存删掉相应对象的相应级别告警数目
                    AlarmStatistic.getInstance().removeAlarm(event);

                    // 添加日志
                    AlarmlogEntity log = new AlarmlogEntity();
                    log.setAlarmevtId(eventLog.getAlarmEvtId());
                    log.setOperate("报警处理");
                    log.setUserid(eventLog.getCheckUserId());
                    log.setOperateTime(new Date());
                    log.setOperatetype(3);
                    log.setOperateUser(userName);
                    adal.addAlarmlogMany(log);
                    logOperator("处理ID为" + event.getAlarmEvtId() + "的报警事件。");
                }
            }
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            logger.debug(objXml);
            errorProcess(retObj, "处理报警失败!", ex);
        }

        return retObj;
    }

    /**
     * 确认报警事件
     * @param objType
     * @param objXml
     * @param eventIds
     * @param userAuth
     * @return
     */
    public WSResult bmpConfirmAlarmEvent(String objType, String objXml, String eventIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = new WSResult();

        if (StringUtil.isNullOrEmpty(eventIds))
        {
            return retObj;
        }

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, objType);
        String[] ids = eventIds.split(",");

        ISqlExecutor sqlExecutor = SqlExecutorFacotry.getSqlExecutor();

        try
        {
            sqlExecutor.transBegin();
            User u = new User();
            String userName = userAuth.getLoginId();

            AlarmEventDal aedal = new AlarmEventDal();
            Alarmlog adal = new Alarmlog();

            List<AlarmEventEntity> alarmEvents =
                aedal.getLst(new SqlCondition("ALARMEVT_ID", eventIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric));
            int eventSize = alarmEvents.size();
            for (int i = 0; i < eventSize; i++)
            {
                AlarmEventEntity alarmEvent = alarmEvents.get(i);
                if (alarmEvent.getEventState() == 0)
                {
                    SqlField[] param =
                        new SqlField[] {
                            SqlField.tryCreate("EVENT_STATE", model.get("EVENT_STATE"), SqlParamType.Numeric),
                            SqlField.tryCreate("CHECK_USERID", model.get("CHECK_USERID"), SqlParamType.Numeric),
                            SqlField.tryCreate("CHECK_USER", userName),
                            new SqlField("CHECK_TIME", new Date(), SqlParamType.DateTime) };
                    SqlCondition p = new SqlCondition("ALARMEVT_ID", ids[i], SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
                    sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getUpdateCommandString("BMP_ALARMEVENT", Arrays.asList(param), p));

                    // 从内存更新相应告警事件
                    alarmEvent.setEventState(Integer.valueOf(model.get("EVENT_STATE")));
                    AlarmStatistic.getInstance().updateAlarm(alarmEvent);

                    AlarmlogEntity log = new AlarmlogEntity();
                    log.setAlarmevtId(Integer.parseInt(ids[i]));
                    log.setOperate("报警确认");
                    log.setUserid(Integer.parseInt(model.get("CHECK_USERID")));
                    log.setOperateTime(new Date());
                    log.setOperatetype(1);
                    log.setOperateUser(userName);

                    // 插入日志
                    adal.addAlarmlogMany(log);

                    logOperator("确认ID为" + alarmEvent.getAlarmEvtId() + "的报警事件。");
                }
            }
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            logger.debug(objXml);
            errorProcess(retObj, "确认报警失败!", ex);
        }

        return retObj;
    }

    /**
     * 批量设置采集间隔
     * @param objAttribIds
     * @param time
     * @return
     */
    public WSResult bmpSetCollTime(String objAttribIds, String time, @WebParam(header = true) UserAuthHeader userAuth)
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
     * 批量修改码流类型
     * @param tsIds
     * @param type
     * @return
     */
    public WSResult bmpSetTsStreamType(String tsIds, String type, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            new MObject().setTsStreamType(tsIds, type);
            logOperator("批量修改码流类型");
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "批量修改码流类型出错", ex);
        }
        return result;
    }

    /**
     * 获取对象的自定义属性和属性值集合
     * @param attribIds 要获取的属性值的ID
     * @param attribNames 需要和属性值对应的KEY名称
     * @param conditions 模糊查询的条件集合
     * @param objName 模糊查询的对象名称
     * @return
     */
    public WSResult bmpGetAttribsAndValues(String attribIds, String attribNames, String conditions, String objName, String top,
            @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            result.setResultVal(new ObjAttrib().getAttribsAndValues(attribIds, attribNames, conditions, objName, top));
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "获取对象的自定义属性和属性值集合出错", ex);
        }
        return result;
    }

    /**
     * 将对象绑定到设备
     * @param deviceId 设备ID
     * @param objIds 对象ID集合
     * @param useType 关联类型
     * @return
     */
    public WSResult bmpBindObjectToDevice(String deviceId, String objIds, String useType, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            Obj2GroupDal dalObj2Group = new Obj2GroupDal();
            dalObj2Group.delete(MessageFormat.format("DELETE FROM BMP_OBJ2GROUP WHERE GROUP_ID IN (SELECT GROUP_ID FROM BMP_OBJGROUP WHERE GROUP_TYPE=7) AND USE_TYPE={0} AND OBJ_ID IN ({1})",
                useType,
                objIds));
            String[] objIdList = objIds.split(",");
            for (int i = 0; i < objIdList.length; i++)
            {
                dalObj2Group.add(objIdList[i], deviceId, useType);
            }
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "将对象绑定到设备出错", ex);
        }
        return result;
    }

    /**
     * 取消设备绑定的对象
     * @param deviceId 设备ID
     * @param objIds 对象ID集合
     * @param useType 关联类型
     * @return
     */
    public WSResult bmpRemoveObjectToDevice(String deviceId, String objIds, String useType, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            Obj2GroupDal dalObj2Group = new Obj2GroupDal();
            dalObj2Group.delete(new SqlCondition("GROUP_ID", deviceId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("OBJ_ID", objIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
                new SqlCondition("USE_TYPE", useType, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "取消设备绑定的对象出错", ex);
        }
        return result;
    }

    /**
     * 设置监控的信号
     * @param objIds 对象ID集合
     * @return
     */
    public WSResult bmpSetMonitorObject(String objIds, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        try
        {
            execNmp.transBegin();
            // MObjectDal dalMObject = new MObjectDal();
            // dalMObject.update("UPDATE BMP_OBJECT SET NUM_VAL3=0 WHERE CLASS_ID=7");
            String sql1 = "UPDATE BMP_OBJECT SET NUM_VAL3=0 WHERE CLASS_ID=7";
            ClassWrapper.wrap(MObject.class).updateBySql(sql1);
            if (objIds != null && !"".equals(objIds))
            {
                String sql2 = String.format("UPDATE BMP_OBJECT SET NUM_VAL3=1 WHERE OBJ_ID IN (%s)", objIds);
                // dalMObject.update(String.format("UPDATE BMP_OBJECT SET NUM_VAL3=1 WHERE OBJ_ID IN (%s)", objIds));
                ClassWrapper.wrap(MObject.class).updateBySql(sql2);
            }
            execNmp.transCommit();
        }
        catch (Exception ex)
        {
            execNmp.transRollback();
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "设置监控的信号出错", ex);
        }
        return result;
    }

    /**
     * 获取自动发现任务配置
     * @return
     */
    public WSResult bmpGetAutoDisConfig()
    {
        WSResult result = new WSResult();
        try
        {
            AutoDisConfigColl coll = AutoDisConfigColl.getInstance();
            result.resultVal = coll.toXml();
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "获取自动发现任务配置出错。", ex);
        }
        return result;
    }

    /**
     * 获得子对象
     * @param objId对象ID
     * @param classId对象类别
     * @param collId采集器ID
     * @return
     */
    public WSResult bmpGetSubObject(String objId, String classId, String collId)
    {
        WSResult result = new WSResult();
        try
        {

            InsManager insManager = InsManager.getInstance();
            SubObjInsInfo subObjInsInfo =
                insManager.getSubInsInfo(Integer.parseInt(objId), Integer.parseInt(classId), Integer.parseInt(collId), false);
            result.setResultVal(subObjInsInfo.toXml());
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "获取子对象出错！", ex);
        }
        return result;
    }

    /**
     * 实例化子对象
     * @param requestStr 前段传过来的数据格式：<RecordSet><Record><name>System Idle Process</name><info>1</info></Record></RecordSet>
     * @param objId父对象ID
     * @param classId对象类别
     * @param collId采集器ID
     * @return
     */
    public WSResult bmpInsSubObject(String requestStr, String objId, String classId, String collId, int userId)
    {
        WSResult result = new WSResult();
        try
        {
            MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
            MObjectEntity mo = modal.get(Integer.valueOf(objId));
            if (mo != null)
            {
                SubObjInsInfo subObjInsInfo = SubObjInsInfo.fromXml(requestStr, mo, Integer.parseInt(classId));
                InsManager insManager = InsManager.getInstance();
                insManager.insSelSub(subObjInsInfo, Integer.parseInt(collId), userId);
            }
            else
            {
                result.setErrorCode(1);
                result.setErrorString("资源编号<" + objId + ">不存在！");
            }
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "实例化子对象出错！", ex);
        }
        return result;
    }

    public String getReportProPath()
    {
        return "http://" + reportProIp + ":" + reportProPort + "/" + reportProName;
    }

    /**
     * 获得报表项目路径
     * @param userAuth
     * @return
     */
    public WSResult bmpGetReportWebPath(@WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult result = new WSResult();
        try
        {
            result.setResultVal(ConfigUtil.getReportWebPath());
        }
        catch (Exception ex)
        {
            result.setErrorCode(1);
            result.setErrorString(ex.getMessage());
            errorProcess(result, "获得报表项目路径失败！", ex);
        }
        return result;
    }

    /**
     * 根据对象id查看对象性能属性
     * @param objId 对象id
     * @return 结果
     */
    public WSResult bmpGetObjAttri(String objId)
    {
        WSResult retval = new WSResult();
        try
        {
            ObjAttribute f = new ObjAttribute();
            retval.resultVal = f.getAttribByObjId(Integer.valueOf(objId));
        }
        catch (Exception ex)
        {
            retval.resultVal = "";
        }
        return retval;
    }

    /**
     * 查询文章
     * @param title 文章标题
     * @param summary 文章摘要
     * @param type 文章类别(例如：计算机,监控)
     * @param typeId 设备类型ID
     * @param alarmId 报警规则ID
     * @param author 文章作者
     * @return 结果
     */
    public WSResult queryKnowledge(String title, String summary, String type, String typeId, String alarmId, String createUserId,
            @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("knowladgeQuery");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }
        try
        {
            retObj.resultVal = new Knowledge().queryKnowledge(title, summary, type, typeId, alarmId, createUserId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "查询文章失败!", ex);
        }
        return retObj;
    }

}
