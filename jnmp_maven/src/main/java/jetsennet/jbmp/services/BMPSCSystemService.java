/************************************************************************
 * 日 期：2011-11-28 
 * 作 者: 
 * 版 本：v1.3 
 * 描 述: BMP通用增删改查
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.services;

import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;

import jetsennet.jbmp.business.CheckTemplate;
import jetsennet.jbmp.business.Collector;
import jetsennet.jbmp.business.KpiTemplate;
import jetsennet.jbmp.business.ObjAttrib;
import jetsennet.jbmp.business.ObjAttribute;
import jetsennet.jbmp.business.ObjGroup;
import jetsennet.jbmp.business.RoleGroup;
import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.AlarmEventLogDal;
import jetsennet.jbmp.dataaccess.AlarmObjAttrRelDal;
import jetsennet.jbmp.dataaccess.AlarmObjRelDal;
import jetsennet.jbmp.dataaccess.MscObjectDal;
import jetsennet.jbmp.dataaccess.TrapEventDal;
import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmEventLogEntity;
import jetsennet.jbmp.entity.AlarmObjAttrRelEntity;
import jetsennet.jbmp.entity.AlarmObjRelEntity;
import jetsennet.jbmp.entity.TrapEventEntity;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.formula.inspur.FormulaHandleInspur;
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.jbmp.util.ReturnXML;
import jetsennet.juum.services.UUMRemoteService;
import jetsennet.net.UserAuthHeader;
import jetsennet.net.WSResult;
import jetsennet.net.WebServiceBase;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;

/**
 * BMPsc四川版本
 * @author
 */
@WebService(name = "BMPSCSystemService", serviceName = "BMPSCSystemService", targetNamespace = "http://JetsenNet/JNMP/")
public class BMPSCSystemService extends WebServiceBase
{

    private ConnectionInfo bmpConnectionInfo;
    private jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("JetsenNet.JBMP");
    private static UUMRemoteService uumRemoteService;

    static
    {
        UUMRemoteService.createUUMRemoteServiceServer();
        uumRemoteService = UUMRemoteService.UUMRemoteServiceInstance();
    }

    public BMPSCSystemService()
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

    // 通用新增方法
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
            if (objType.equalsIgnoreCase("BMP_OBJGROUP"))
            {
                id = ClassWrapper.wrap(ObjGroup.class).addObjGroup(objXml) + "";
                logOperator("新建对象组ID：" + id);
                int groupId = Integer.parseInt(id);
                new RoleGroup().saveRoleGroup(groupId, userAuth.getUserId());
            }
            else if (objType.equalsIgnoreCase("BMP_COLLECTOR"))
            {
                id = ClassWrapper.wrap(Collector.class).addCollector(objXml) + "";
                logOperator("新建数据采集器ID：" + id);
                int collId = Integer.parseInt(id);
                new RoleGroup().saveCollRole(collId, userAuth.getUserId());

            }
            else if (objType.equalsIgnoreCase("BMP_KPITEMPLATE"))
            {
                id = ClassWrapper.wrap(KpiTemplate.class).addKpiTemplate(objXml) + "";
                logOperator("新增KPI指标报表模板ID：" + id);
            }
            else if (objType.equalsIgnoreCase("BMP_CHECKTEMPLATE"))
            {
                id = ClassWrapper.wrap(CheckTemplate.class).addCheckTemplate(objXml) + "";
                logOperator("新增拓扑巡视模板ID：" + id);
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

    // 通用删除
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
            if ("BMP_CHECKTEMPLATE".equalsIgnoreCase(objType))
            {
                ClassWrapper.wrap(CheckTemplate.class).deleteCheckTemplate(Integer.valueOf(objId));
                logOperator("删除ID为" + objId + "的拓扑巡视模板");
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
     * 获取对象所属的组
     * @param objId
     * @return
     */
    public WSResult bmpQueryObjGroup(int objId, int userId)
    {
        // this.userAuth = userAuth;
        MscObjectDal modao = ClassWrapper.wrapTrans(MscObjectDal.class);
        WSResult result = new WSResult();
        try
        {
            String s = modao.getGroup(objId, userId);
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
     * 手动设置对象属性的值
     * @param objId
     * @param objAttrXml
     * @return
     */
    public WSResult bmpSetValueManu(int objId, String objAttrXml)
    {
        WSResult retval = new WSResult();
        try
        {
            BMPServletContextListener listener = BMPServletContextListener.getInstance();
            if (listener.isOnline(Integer.toString(objId)))
            {
                int i =
                    (Integer) listener.callRemote(Integer.toString(objId), "remoteSetValueManu", new Object[] { objId, objAttrXml }, new Class[] {
                        int.class, String.class }, true);
                if (i == 0)
                {
                    retval.errorCode = 1;
                    retval.errorString = "设置属性值失败！";
                }
            }
            else
            {
                retval.errorCode = 2;
                retval.errorString = "无法连接到对象对应的采集器！";
            }
        }
        catch (Exception ex)
        {
            retval.errorCode = 3;
            retval.errorString = "设置属性值失败！" + ex.getMessage();
        }
        return retval;
    }

    /**
     * 资源类型中校验浪潮机房公式是否合法
     * @param str 公式
     * @return 结果
     */
    public WSResult bmpValidateExpression(String str)
    {
        WSResult retval = new WSResult();
        try
        {
            FormulaHandleInspur f = new FormulaHandleInspur();
            retval.resultVal = f.validate(str);
        }
        catch (Exception ex)
        {
            retval.resultVal = "校验失败";
        }
        return retval;
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
     * 根据对象ID，资源组ID查询kpi指标
     * @param objId
     * @param groupId
     * @return
     */
    public WSResult bmpGetKpi(String objId, String groupId)
    {
        WSResult retval = new WSResult();
        try
        {
            ObjAttribute f = new ObjAttribute();
            retval.resultVal = f.getKpiByObjId(Integer.valueOf(objId), Integer.parseInt(groupId));
        }
        catch (Exception ex)
        {
            retval.resultVal = "";
        }
        return retval;
    }

    /**
     * 通用批量删除方法 bmpObjDeleteMany
     * @param objType 对象类型
     * @param ids 参数
     * @param userAuth 权限验证
     * @return 结果
     */
    public WSResult bmpObjDeleteMany(String objType, String ids, @WebParam(header = true) UserAuthHeader userAuth)
    {
        this.userAuth = userAuth;
        WSResult retObj = valideAuth("bmpObjDeleteMany");
        if (retObj.errorCode != 0)
        {
            return retObj;
        }
        ISqlExecutor execNmp = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);
        try
        {
            execNmp.transBegin();
            String[] idArray = ids.split(",");
            if (objType.equalsIgnoreCase("BMP_OBJATTRIB"))
            {
                for (String id : idArray)
                {
                    ClassWrapper.wrap(ObjAttrib.class).deleteObjAttrib(Integer.parseInt(id));
                    logOperator("删除的对象属性ID为" + id);
                }
            }
            else if (objType.equalsIgnoreCase("BMP_OBJECTMANY"))
            {
                for (String id : idArray)
                {
                    int objId = Integer.parseInt(id);
                    new ObjAttrib().deleteObjAttribByObjId(objId);
                    logOperator("删除ID为" + id + "的对象及关联关系");
                }
            }
            else if (objType.equalsIgnoreCase("BMP_KPITEMPLATE"))
            {
                for (String id : idArray)
                {
                    ClassWrapper.wrap(KpiTemplate.class).deleteKpiTemplate(Integer.parseInt(id));
                    logOperator("删除ID为" + id + "的KPI模板");
                }

            }
            execNmp.transCommit();
        }
        catch (Exception ex)
        {
            execNmp.transRollback();
            retObj.setErrorCode(1);
            retObj.setErrorString(ex.getMessage());
            errorProcess(retObj, "删除数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 查询关联设备
     * @param collId采集器ID
     * @param relId节点关系ID
     * @return
     */
    public WSResult queryObject(String collId, String relId, String classId, String flag)
    {
        WSResult retval = new WSResult();
        MscObjectDal modao = ClassWrapper.wrapTrans(MscObjectDal.class);
        try
        {
            retval.resultVal = modao.queryObject(collId, relId, classId, flag);
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
        }
        return retval;
    }

    /**
     * 批量添加关联设备
     * @param parentId父关联ID
     * @param objIds对象ID字符串
     * @param collId采集器ID
     * @return
     */
    public WSResult batchAdd(String parentId, String objIds, String collId)
    {
        WSResult retval = new WSResult();
        AlarmObjRelDal aor = ClassWrapper.wrapTrans(AlarmObjRelDal.class);
        try
        {
            if (objIds != null && !"".equals(objIds))
            {
                String[] objId = objIds.split(",");
                if (objId != null && objId.length > 0)
                {
                    for (int i = 0; i < objId.length; i++)
                    {
                        AlarmObjRelEntity entity = new AlarmObjRelEntity();
                        entity.setCollId(Integer.parseInt(collId));
                        entity.setObjId(Integer.parseInt(objId[i]));
                        if ("".equals(parentId))
                        {
                            entity.setParentId(0);
                        }
                        else
                        {
                            entity.setParentId(Integer.parseInt(parentId));
                        }
                        aor.insert(entity, true);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
        }
        return retval;
    }

    /**
     * 批量删除关联设备
     * @param relIds关联关系ID
     * @return
     */
    public WSResult batchDelete(String relIds)
    {
        WSResult retval = new WSResult();
        try
        {
            if (relIds != null && !"".equals(relIds))
            {
                String[] rels = relIds.split(",");
                if (rels != null && rels.length > 0)
                {
                    for (int i = 0; i < rels.length; i++)
                    {
                        deepDelete(rels[i]);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
        }
        return retval;
    }

    /**
     * 深度删除，删除该关系下的所有子节点
     * @param relId
     */
    private void deepDelete(String relId) throws Exception
    {
        AlarmObjRelDal aor = ClassWrapper.wrapTrans(AlarmObjRelDal.class);
        List<AlarmObjRelEntity> list = aor.getAll();
        StringBuilder sb = new StringBuilder();
        if (!"".equals(relId) && list.size() != 0)
        {
            sb.append(relId);
            sb.append(",");
            String relIds = getAllDeleteRelId(Integer.parseInt(relId), list, sb);
            String sql = "DELETE FROM BMP_ALARMOBJREL WHERE REL_ID IN (" + relIds.substring(0, relIds.length() - 1) + ")";
            AlarmObjRelDal.delete(sql);
        }
    }

    private String getAllDeleteRelId(int relId, List<AlarmObjRelEntity> list, StringBuilder sb)
    {
        for (AlarmObjRelEntity entity : list)
        {
            if (entity.getParentId() == relId)
            {
                sb.append(entity.getRelId());
                sb.append(",");
                getAllDeleteRelId(entity.getRelId(), list, sb);
            }
        }
        return sb.toString();
    }

    /**
     * 查询某对象属性的来源
     * @param objAttrId对象属性ID
     * @return
     */
    public WSResult querySuper(String objAttrId)
    {
        WSResult retval = new WSResult();
        MscObjectDal modao = ClassWrapper.wrapTrans(MscObjectDal.class);
        try
        {
            retval.resultVal = modao.querySuper(objAttrId);
        }
        catch (Exception ex)
        {
            retval.setErrorCode(1);
        }
        return retval;
    }

    /**
     * 查询某对象属性的衍生
     * @param objAttrId对象属性ID
     * @return
     */
    public WSResult queryChild(String objAttrId)
    {
        WSResult retval = new WSResult();
        MscObjectDal modao = ClassWrapper.wrapTrans(MscObjectDal.class);
        try
        {
            retval.resultVal = modao.queryChild(objAttrId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 删除关联设备
     * @param objAttrId1
     * @param objAttrId2
     * @return
     */
    public WSResult deleteAlarmed(String objAttrId1, String objAttrId2)
    {
        WSResult retval = new WSResult();
        try
        {
            String sql = "DELETE FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_ID = " + objAttrId1 + " AND OBJATTR_PID = " + objAttrId2;
            MscObjectDal.delete(sql);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 批量删除关联设备
     * @param fromObjAttrId 来源的对象属性ID
     * @param toObjAttrId 衍生的对象属性ID
     * @param objAttrId
     * @return
     */
    public WSResult batchDeleteAlarmed(String fromObjAttrId, String toObjAttrId, String objAttrId)
    {
        WSResult retval = new WSResult();
        try
        {
            String[] fromIds = fromObjAttrId.split(",");
            String[] toIds = toObjAttrId.split(",");
            if (fromIds != null && fromIds.length > 0)
            {
                for (int i = 0; i < fromIds.length; i++)
                {
                    if (!"".equals(fromIds[i]))
                    {
                        String sql = "DELETE FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_ID = " + objAttrId + " AND OBJATTR_PID = " + fromIds[i];
                        MscObjectDal.delete(sql);
                    }

                }
            }
            if (toIds != null && toIds.length > 0)
            {
                for (int i = 0; i < toIds.length; i++)
                {
                    if (!"".equals(toIds[i]))
                    {
                        String sql = "DELETE FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_ID = " + toIds[i] + " AND OBJATTR_PID = " + objAttrId;
                        MscObjectDal.delete(sql);
                    }
                }
            }

        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询某对象的对象属性，并判断对象属性是否已近关联
     * @param objId 对象ID
     * @return
     */
    public WSResult queryAttrWithRel(String objId)
    {
        WSResult retval = new WSResult();
        MscObjectDal modao = ClassWrapper.wrapTrans(MscObjectDal.class);
        try
        {
            retval.resultVal = modao.queryAttrWithRel(objId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 单个删除关联报警,将关联到该属性的来源和衍生关联关系都删掉
     * @param objAttrId对象属性ID
     * @return
     */
    public WSResult deleteObjAttrRel(String objAttrId)
    {
        WSResult retval = new WSResult();
        try
        {
            String sql = "DELETE FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_ID = " + objAttrId + " OR OBJATTR_PID = " + objAttrId;
            AlarmObjAttrRelDal.delete(sql);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 批量删除关联报警，将关联到该属性的来源和衍生关联关系都删掉
     * @param objAttrIds
     * @return
     */
    public WSResult batchDeleteObjAttrRel(String objAttrIds)
    {
        WSResult retval = new WSResult();
        try
        {

            String sql = "DELETE FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_ID IN(" + objAttrIds + ") OR OBJATTR_PID IN(" + objAttrIds + ")";
            AlarmObjAttrRelDal.delete(sql);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 添加关联报警关系
     * @param insertXml
     * @return
     */
    public WSResult addObjAttrRel(String insertXml)
    {
        WSResult retval = new WSResult();
        AlarmObjAttrRelDal adao = ClassWrapper.wrapTrans(AlarmObjAttrRelDal.class);
        try
        {
            adao.insertXml(insertXml);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 批量添加报警关联关系
     * @param objAttrIds被加关联的对象属性ID字符串
     * @param oldObjAttrId需添加关联的对象属性ID
     * @param oldObjId需添加关联的对象ID
     * @param currObjId被添加关联的对象ID
     * @param oldTreeIndex需添加关联的设备在树种的位置
     * @param currTreeIndex被添加关联的设备在树种的位置
     * @return
     */
    public WSResult batchAddObjAttrRel(String objAttrIds, String oldObjAttrId, String oldObjId, String currObjId, String oldTreeIndex,
            String currTreeIndex)
    {
        WSResult retval = new WSResult();
        AlarmObjAttrRelDal adao = ClassWrapper.wrapTrans(AlarmObjAttrRelDal.class);
        try
        {
            AlarmObjAttrRelEntity entity;
            String[] objAttrIdArr = objAttrIds.split(",");
            for (int i = 0; i < objAttrIdArr.length; i++)
            {
                entity = new AlarmObjAttrRelEntity();
                if (Integer.parseInt(oldTreeIndex) < Integer.parseInt(currTreeIndex))
                {
                    entity.setObjId(Integer.parseInt(currObjId));
                    entity.setObjAttrId(Integer.parseInt(objAttrIdArr[i]));
                    entity.setObjPid(Integer.parseInt(oldObjId));
                    entity.setObjAttrPid(Integer.parseInt(oldObjAttrId));
                    adao.insert(entity);
                }
                else if (Integer.parseInt(oldTreeIndex) > Integer.parseInt(currTreeIndex))
                {
                    entity.setObjId(Integer.parseInt(oldObjId));
                    entity.setObjAttrId(Integer.parseInt(oldObjAttrId));
                    entity.setObjPid(Integer.parseInt(currObjId));
                    entity.setObjAttrPid(Integer.parseInt(objAttrIdArr[i]));
                    adao.insert(entity);
                }
            }
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询某个对象属性的报警关联来源
     * @param objAttrId
     * @return
     */
    public WSResult queryFromByObjArrId(String objAttrId)
    {
        WSResult retval = new WSResult();
        AlarmObjAttrRelDal adao = ClassWrapper.wrapTrans(AlarmObjAttrRelDal.class);
        try
        {
            retval.resultVal = adao.queryFromByObjArrId(objAttrId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询某个对象属性的报警关联衍生
     * @param objAttrId
     * @return
     */
    public WSResult queryToByObjArrId(String objAttrId)
    {
        WSResult retval = new WSResult();
        AlarmObjAttrRelDal adao = ClassWrapper.wrapTrans(AlarmObjAttrRelDal.class);
        try
        {
            retval.resultVal = adao.queryToByObjArrId(objAttrId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 删除左侧节点树
     * @param relId
     * @param objId
     * @return
     */
    public WSResult deleteAlarmObjRel(String relId, String objId)
    {
        WSResult retval = new WSResult();
        AlarmObjRelDal aod = ClassWrapper.wrapTrans(AlarmObjRelDal.class);
        try
        {
            aod.deleteAlarmObjRel(relId, objId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询图形状态下的来源数据
     * @param objAttrId对象属性ID
     * @return
     */
    public WSResult queryIamgeStateFrom(String objAttrId)
    {
        WSResult retval = new WSResult();
        AlarmObjAttrRelDal aord = ClassWrapper.wrapTrans(AlarmObjAttrRelDal.class);
        try
        {
            retval.resultVal = aord.queryAllFromByObjAttId(objAttrId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询图形状态下的衍生数据
     * @param objAttrId对象属性ID
     * @return
     */
    public WSResult queryIamgeStateTo(String objAttrId)
    {
        WSResult retval = new WSResult();
        AlarmObjAttrRelDal aord = ClassWrapper.wrapTrans(AlarmObjAttrRelDal.class);
        try
        {
            retval.resultVal = aord.queryAllToByObjAttId(objAttrId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询设备的关联关系 如果该设备被删除了，那么该设备下的所有关联都要删除
     * @return
     */
    public WSResult queryRelation()
    {
        WSResult retval = new WSResult();
        AlarmObjRelDal aod = ClassWrapper.wrapTrans(AlarmObjRelDal.class);
        try
        {
            retval.resultVal = aod.queryRelation();
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询所有采集器，如果该采集器删除了，那么该采集器下的所有关联都要删除
     * @return
     */
    public WSResult queryAllColl()
    {
        WSResult retval = new WSResult();
        AlarmObjRelDal aod = ClassWrapper.wrapTrans(AlarmObjRelDal.class);
        try
        {
            retval.resultVal = aod.queryAllColl();
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询当前节点所在的树
     * @param objId
     * @return
     */
    public WSResult getRelByCollId(String objId)
    {
        WSResult retval = new WSResult();
        AlarmObjRelDal aod = ClassWrapper.wrapTrans(AlarmObjRelDal.class);
        try
        {
            retval.resultVal = aod.queryCurrentTree(objId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 根据资源组ID来查询该组下的所有对象包括子对象
     * @param groupId 组ID
     * @return
     */
    public WSResult queryAllObjectByGroupId(String groupId)
    {
        WSResult retval = new WSResult();
        MscObjectDal aod = ClassWrapper.wrapTrans(MscObjectDal.class);
        try
        {
            retval.resultVal = aod.queryAllObjectByGroupId(groupId);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 判断KPI模板名称是否存在
     * @param templateName 模板名称
     * @return
     */
    public WSResult checkTemplateNameIsExsit(String templateName)
    {
        WSResult retval = new WSResult();
        KpiTemplate kpiAod = ClassWrapper.wrapTrans(KpiTemplate.class);
        try
        {
            retval.resultVal = kpiAod.queryTemplateByName(templateName);
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
        }
        return retval;
    }

    /**
     * 查询Trap详细信息
     * 
     * @param trapEvtId Trap事件ID
     * @return
     */
    public WSResult bmpGetTrapDetailInfo(int trapEvtId)
    {
        WSResult retval = new WSResult();
        ReturnXML xml = new ReturnXML(new String[] { "OID", "OID_NAME", "OID_VALUE" });
        try
        {
            TrapEventDal tedal = ClassWrapper.wrap(TrapEventDal.class);
            TrapTableDal ttdal = ClassWrapper.wrap(TrapTableDal.class);

            TrapEventEntity trap = tedal.get(trapEvtId);
            if (trap != null)
            {
                String trapOid = trap.getTrapOid();
                String trapValue = trap.getTrapValue();

                // trap描述信息
                Map<String, TrapTableEntity> trapMap = ttdal.getTrapByOid(trapOid);

                if (trapValue != null)
                {
                    String[] values = trapValue.split(";");
                    for (String value : values)
                    {
                        String[] temp = value.split(":");
                        if (temp.length == 2)
                        {
                            String oid = temp[0];
                            String oidValue = temp[1];
                            TrapTableEntity tt = trapMap.get(oid);
                            String oidName = tt != null ? tt.getTrapName() : "";
                            xml.addRow(oid, oidName, oidValue);
                        }
                    }
                }
            }
            retval.resultVal = xml.toXml().asXML();
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
            retval.errorString = ex.getMessage();
        }
        return retval;
    }

    public WSResult ensureAlarmEvent(int alarmEvtId)
    {
        WSResult retval = new WSResult();
        try
        {
            AlarmEventDal aedal = ClassWrapper.wrapTrans(AlarmEventDal.class);
            AlarmEventLogDal aeldal = ClassWrapper.wrapTrans(AlarmEventLogDal.class);

            AlarmEventLogEntity aele = null;
            AlarmEventEntity aee = aedal.get(alarmEvtId);
            if (aee == null)
            {
                aele = aeldal.get(alarmEvtId);
            }
            else
            {
                aele = new AlarmEventLogEntity(aee);
            }

            StringBuilder sb = new StringBuilder();
            if (aele != null)
            {
                String eventDesc = aele.getEventDesc();
                eventDesc = eventDesc == null ? "" : eventDesc;
                sb.append("{");
                sb.append("\"ALARMEVT_ID\"").append(":").append("\"").append(aele.getAlarmEvtId()).append("\"").append(",");
                sb.append("\"EVENT_STATE\"").append(":").append("\"").append(aele.getEventState()).append("\"").append(",");
                sb.append("\"EVENT_DESC\"").append(":").append("\"").append(eventDesc).append("\"");
                sb.append("}");
            }
            retval.resultVal = sb.toString();
        }
        catch (Exception ex)
        {
            retval.errorCode = 1;
            retval.errorString = ex.getMessage();
        }
        return retval;
    }

}
