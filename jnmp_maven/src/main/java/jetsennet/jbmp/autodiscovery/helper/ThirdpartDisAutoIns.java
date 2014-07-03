package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AutoDisObjEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.InsManager;
import jetsennet.jbmp.util.BMPConstants;

/**
 * @author liwei 代码格式优化
 */
public class ThirdpartDisAutoIns extends DisAutoIns
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DisAutoIns.class);

    /**
     * 构造方法
     */
    public ThirdpartDisAutoIns()
    {
        super();
    }

    /*
     * 批量实例化
     * @param autoObj 参数
     * @param collId 采集器ID
     * @param userId 用户ID
     */
    @Override
    public void ins(List<AutoDisObjEntity> autoObjs, int collId, int userId)
    {
        if (autoObjs == null || autoObjs.isEmpty())
        {
            return;
        }

        int groupId = this.ensureGroupId(collId);
        if (groupId <= 0)
        {
            logger.debug(String.format("找不到采集器<%s>对应的采集组<%s>。", collId, groupId));
        }
        Map<String, Integer> tsPort2id = modal.getTsPortRel();
        for (AutoDisObjEntity autoObj : autoObjs)
        {
            // 判断是否有TS_PORT信息
            String tsPort = autoObj.getField1();
            if (tsPort == null)
            {
                logger.error(String.format("自动发现对象%s找不到TS_PORT", autoObj.getObjName()));
                continue;
            }
            try
            {
                switch (autoObj.getOpStatus())
                {
                case AutoDisObjEntity.OP_STATUS_NEW:
                    this.handleNew(autoObj, tsPort2id, tsPort, groupId, collId, userId);
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD1:
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD2:
                    modal.updateStateByIpAndClassId(autoObj.getIp(), autoObj.getClassId(), MObjectEntity.OBJ_STATE_MANAGEABLE);
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD3:
                    break;
                case AutoDisObjEntity.OP_STATUS_UPD4:
                    modal.updateStateByIpAndClassId(autoObj.getIp(), autoObj.getClassId(), MObjectEntity.OBJ_STATE_MANAGEABLE);
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

    private void handleNew(AutoDisObjEntity autoObj, Map<String, Integer> tsPort2id, String tsPort, int groupId, int collId, int userId)
            throws Exception
    {
        MObjectEntity mo = this.toMObject(autoObj);
        if (mo == null)
        {
            return;
        }
        if (mo.getClassId() == BMPConstants.CATV_CLASS_ID_TS)
        {
            int objId = modal.insert(mo);
            tsPort2id.put(tsPort, objId);
        }
        else
        {
            Integer temp = tsPort2id.get(tsPort);
            if (temp == null)
            {
                return;
            }
            mo.setParentId(temp);
            modal.insert(mo);
        }
        Map<String, String> infos = this.getInfos(autoObj);
        InsManager.getInstance().autoIns(mo, null, groupId, collId, userId, infos, true);
        autoObj.setRecStatus(AutoDisObjEntity.RECORD_STATUS_CREATED);
        ododal.update(autoObj);
    }

    /**
     * 自动实例化对象转换成可监视对象
     * @param autoObj
     * @return
     */
    protected MObjectEntity toMObject(AutoDisObjEntity autoObj)
    {
        MObjectEntity mo = super.toMObject(autoObj);
        // 第三方暂时使用USER_NAME字段存储IP地址
        mo.setIpAddr(autoObj.getUserName());
        return mo;
    }
}
