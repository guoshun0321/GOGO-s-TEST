/************************************************************************
日 期：2012-2-24
作 者: 郭祥
版 本: v1.3
描 述: 自动发现对象实例化
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.AutoDisUtil;
import jetsennet.jbmp.dataaccess.AutoDisObjDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjGroupDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.buffer.AttribClassBuffer;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AutoDisObjEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.InsManager;
import jetsennet.jbmp.util.BMPConstants;

import org.apache.log4j.Logger;

/**
 * 自动发现对象实例化
 * @author 郭祥
 */
public class DisAutoIns
{

    /**
     * 缓存
     */
    protected AttribClassBuffer acBuffer;
    // 数据库访问
    protected MObjectDal modal;
    protected AutoDisObjDal ododal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DisAutoIns.class);

    /**
     * 构造方法
     */
    public DisAutoIns()
    {
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
        ododal = ClassWrapper.wrapTrans(AutoDisObjDal.class);
        acBuffer = new AttribClassBuffer();
    }

    /**
     * 批量实例化
     * @param autoObjs 参数
     * @param collId 参数
     * @param userId 参数
     */
    public void ins(List<AutoDisObjEntity> autoObjs, int collId, int userId)
    {
        if (autoObjs == null || autoObjs.isEmpty())
        {
            return;
        }
        int groupId = this.ensureGroupId(collId);
        if (groupId <= 0)
        {
            logger.error(String.format("找不到<%s>对应的采集组(%s)。", collId, groupId));
            return;
        }

        for (AutoDisObjEntity autoObj : autoObjs)
        {

            try
            {
                switch (autoObj.getOpStatus())
                {
                case AutoDisObjEntity.OP_STATUS_NEW:
                    this.createAndIns(autoObj, groupId, collId, userId);
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD1:
                    this.createAndIns(autoObj, groupId, collId, userId);
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD2:
                    modal.updateStateByIpAndClassId(autoObj.getIp(), autoObj.getClassId(), MObjectEntity.OBJ_STATE_MANAGEABLE);
                    this.createAndIns(autoObj, groupId, collId, userId);
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD3:
                    this.createAndIns(autoObj, groupId, collId, userId);
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD4:
                    modal.updateStateByIpAndClassId(autoObj.getIp(), autoObj.getClassId(), MObjectEntity.OBJ_STATE_MANAGEABLE);
                    this.createAndIns(autoObj, groupId, collId, userId);
                    break;
                case AutoDisObjEntity.OP_STATUS_DEL:
                    modal.updateStateByIpAndClassId(autoObj.getIp(), autoObj.getClassId(), MObjectEntity.OBJ_STATE_MAINTAIN);
                    break;
                default:
                    break;
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }

    /**
     * 创建对象并实例化
     * @param autoObj
     * @param groupId
     * @param collId
     * @throws Exception
     */
    protected void createAndIns(AutoDisObjEntity autoObj, int groupId, int collId, int userId) throws Exception
    {
        if (this.isCreatable(autoObj))
        {
            MObjectEntity mo = this.toMObject(autoObj);
            if (mo != null)
            {
                modal.insert(mo);
                Map<String, String> infos = this.getInfos(autoObj);
                InsManager.getInstance().autoIns(mo, null, groupId, collId, userId, infos, true);
                autoObj.setRecStatus(AutoDisObjEntity.RECORD_STATUS_CREATED);
                ododal.update(autoObj);
            }
        }
    }

    /**
     * 是否调用实例化。对象类型已知，且对象标识不存在时，返回true。
     * @return
     */
    protected boolean isCreatable(AutoDisObjEntity autoObj)
    {
        boolean retval = false;
        if (autoObj.getClassId() != BMPConstants.CLASS_ID_UNKNOWN)
        {
            List<MObjectEntity> mos = modal.getByIdent(autoObj.getClassId(), autoObj.getIp());
            if (mos == null || mos.isEmpty())
            {
                logger.debug(String.format("自动发现：不存在标识为<%s>，类型为<%s>的对象，创建新对象。", autoObj.getIp(), autoObj.getClassId()));
                retval = true;
            }
            else
            {
                logger.debug(String.format("自动发现：存在标识为<%s>，类型为<%s>的对象，不创建新对象。", autoObj.getIp(), autoObj.getClassId()));
            }
        }
        else
        {
            logger.debug(String.format("自动发现：标识为<%s>的对象，类型为UNKNOWN，不创建新对象。", autoObj.getIp()));
        }
        return retval;
    }

    /**
     * 自动实例化对象转换成可监视对象
     * @param autoObj
     * @return
     */
    protected MObjectEntity toMObject(AutoDisObjEntity autoObj)
    {
        AttribClassEntity ac = this.ensureClass(autoObj.getClassId());
        if (ac == null)
        {
            return null;
        }
        MObjectEntity mo = new MObjectEntity();
        mo.setObjName(this.ensureName(autoObj, ac.getClassName()));
        mo.setClassId(autoObj.getClassId());
        mo.setClassGroup(ensureClassGroup(ac.getClassId()));
        mo.setClassType(ac.getClassType());
        mo.setObjState(MObjectEntity.OBJ_STATE_MANAGEABLE);
        mo.setIpAddr(autoObj.getIp());
        String port = autoObj.getPort();
        mo.setIpPort(port == null ? 0 : Integer.valueOf(port));
        mo.setUserName(autoObj.getUserName());
        mo.setUserPwd(autoObj.getPassword());
        mo.setVersion(autoObj.getVersion());
        mo.setCreateUser("SYSTEM");
        mo.setCreateTime(new Date());
        mo.setParentId(0);
        mo.setManId(ac.getManId());
        this.pareseAppendInfo(mo, autoObj);
        return mo;
    }

    /**
     * 解析附加信息
     * @param mo
     * @param autoObj
     */
    protected void pareseAppendInfo(MObjectEntity mo, AutoDisObjEntity autoObj)
    {
        String mac = autoObj.getInfoFromXml(AutoDisConstant.ARP_MAC);
        String sysServices = autoObj.getInfoFromXml(AutoDisConstant.SNMP_SYSSERVICES);
        String ipForwarding = autoObj.getInfoFromXml(AutoDisConstant.SNMP_IPFORWORDING);
        String portNum = autoObj.getInfoFromXml(AutoDisConstant.SNMP_DOT1DBASENUMPORTS);

        // 同时通过ARP和SNMP的才进行设置
        if (mac != null && sysServices != null)
        {
            int linkType = AutoDisUtil.ensureLinkType(sysServices, ipForwarding, portNum);
            mo.setField2(mac);
            mo.setNumVal2(linkType);
        }

    }

    protected String ensureName(AutoDisObjEntity autoObj, String acName)
    {
        String autoName = autoObj.getObjName();
        if (autoName == null || "".equals(autoName.trim()))
        {
            autoName = String.format("%s(%s)", acName, autoObj.getIp());
        }
        else
        {
            autoName = String.format("%s(%s)", autoName, autoObj.getIp());
        }
        return autoName;
    }

    protected Map<String, String> getInfos(AutoDisObjEntity autoObj)
    {
        Map<String, String> retval = new HashMap<String, String>();
        if (autoObj != null)
        {
            retval.put("OBJ_DESC", autoObj.getObjDesc());
        }
        return retval;
    }

    protected int ensureGroupId(int collId)
    {
        ObjGroupDal ogdal = ClassWrapper.wrapTrans(ObjGroupDal.class);
        return ogdal.getGroupIdByCollId(collId);
    }

    protected int ensureClassGroup(int classId)
    {
        int retval = MObjectEntity.CLASS_GROUP_DEV;
        if (classId == BMPConstants.CATV_CLASS_ID_TS)
        {
            retval = MObjectEntity.CLASS_GROUP_TS;
        }
        else if (classId == BMPConstants.CATV_CLASS_ID_PGM)
        {
            retval = MObjectEntity.CLASS_GROUP_PGM;
        }
        return retval;
    }

    /**
     * 确定使用的属性分类
     * @param classId
     * @return
     */
    protected AttribClassEntity ensureClass(int classId)
    {
        AttribClassEntity retval = null;
        try
        {
            retval = acBuffer.get(classId);
            if (retval == null)
            {
                retval = acBuffer.get(BMPConstants.CLASS_ID_UNKNOWN);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }
}
