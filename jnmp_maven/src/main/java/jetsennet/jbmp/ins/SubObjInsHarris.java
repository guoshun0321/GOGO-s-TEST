package jetsennet.jbmp.ins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.SubObjInsInfo.SubObjInsInfoEntry;
import jetsennet.jbmp.ins.helper.AttrsInsResult;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;
import jetsennet.jbmp.util.InsUtil;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

public class SubObjInsHarris extends SubObjsInsDef
{

    // 数据库操作
    private AttribClassDal acdal;
    private MObjectDal modal;
    /**
     * harris板卡扫描开始的OID
     */
    private static final String CARD_OID = "1.3.6.1.4.1.3142.2.7.294.1";
    /**
     * 板卡信息OID，前缀
     */
    private static final String CARD_INFO_OID_PREFIX = "1.3.6.1.4.1.3142.2.7.294.1.";
    /**
     * 板卡信息OID，中间部分
     */
    private static final String CARD_INFO_OID_APPEND = ".1.81.";
    /**
     * 插槽为空时，显示的板卡名称
     */
    private static final String EMPTY_NAME = "empty";
    /**
     * 日志
     */
    public final Logger logger = Logger.getLogger(SubObjInsHarris.class);

    /**
     * 构造函数
     * @param collId 采集id
     */
    public SubObjInsHarris()
    {
        acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
    }

    @Override
    public AttrsInsResult getInsResult(MObjectEntity mo, List<AttributeEntity> attrs, int collId, Map<String, String> infos, boolean isLocal)
            throws Exception
    {
        String slot = mo.getField1();

        // 实例化类初始化
        // 实例号=槽位号+256
        AbsObjIns ins = new ObjInsSnmpIndex(Integer.toString(Integer.valueOf(slot) + 256));
        ins.init();

        // 实例化
        return ins.getInsResult(mo, attrs, collId, null, false);
    }

    /**
     * 获取板卡信息
     * 
     * @param objId 父对象ID
     * @param classId 板卡类型信息
     * @param collId 采集器ID
     * @param isLocal 是否为本地采集
     */
    @Override
    public SubObjInsInfo getSubInfo(int objId, int classId, int collId, boolean isLocal) throws Exception
    {
        SubObjInsInfo info = new SubObjInsInfo();
        info.setObjId(objId);
        info.setClassId(classId);

        // 确定对象、分类，扫描
        MObjectEntity mo = modal.get(objId);
        AttribClassEntity ac = acdal.get(classId);
        Map<String, VariableBinding> cardInfoMap = this.scan(mo, ac, isLocal, collId);

        if (cardInfoMap != null && !cardInfoMap.isEmpty() && !ac.getField1().isEmpty())
        {
            String suffix = this.ensureSuffix(cardInfoMap);
            if (suffix != null)
            {
                // 槽位共20个，OID从11开始计算
                for (int slotOid = 11; slotOid <= 30; slotOid++)
                {
                    // 槽位编号
                    int slot = slotOid - 10;
                    // 拼接板卡记录OID
                    String nameOID = CARD_INFO_OID_PREFIX + slotOid + CARD_INFO_OID_APPEND + suffix;
                    VariableBinding nameVB = cardInfoMap.get(nameOID);
                    String name = SnmpValueTranser.getInstance().trans(nameVB, null, 0).trim();
                    if (!name.equalsIgnoreCase(EMPTY_NAME) && name.equalsIgnoreCase(ac.getField1()))
                    {
                        SubObjInsInfoEntry entry = new SubObjInsInfoEntry();
                        entry.objName = this.genName(mo.getObjName(), name, slot);
                        entry.addInfo = slot + "," + suffix;
                        info.addSubs(entry);
                    }
                }
            } else {
                logger.error("harris子对象实例化失败，无法获取后缀标识！");
            }
        }
        return info;
    }

    /**
     * 确定OID最后一位数字
     * 
     * @param cardInfoMap
     * @return
     */
    private String ensureSuffix(Map<String, VariableBinding> cardInfoMap)
    {
        String suffix = null;
        for (Map.Entry<String, VariableBinding> entry : cardInfoMap.entrySet())
        {
            String key = entry.getKey();
            int suffixPos = key.lastIndexOf(".");
            if (suffixPos >= 0 && suffixPos < key.length())
            {
                suffix = key.substring(suffixPos + 1);
            }
            break;
        }
        return suffix;
    }

    @Override
    public void ins(SubObjInsInfo info, int collId) throws Exception
    {
        SubObjsInsRst insRst = new SubObjsInsRst();
        try
        {
            MObjectEntity mo = modal.get(info.getObjId());
            AttribClassEntity ac = acdal.get(info.getClassId());
            List<AttributeEntity> attrs = InsUtil.ensureInsAttr(info.getClassId());

            List<SubObjInsInfoEntry> subs = info.getSubs();
            if (mo != null && ac != null && subs != null)
            {
                for (SubObjInsInfoEntry sub : subs)
                {
                    String addInfo = sub.addInfo;
                    String[] addInfos = addInfo.split(",");
                    if (addInfos.length != 2)
                    {
                        continue;
                    }

                    String slot = addInfos[0];
                    String suffix = addInfos[1];

                    // 生成对象
                    MObjectEntity subObj = this.genObj(mo, sub.objName, ac, sub.desc);
                    subObj.setField1(slot);
                    subObj.setField2(suffix);
                    subObj.setObjState(sub.objState);

                    // 实例化类初始化
                    // 实例号=槽位号+256
                    AbsObjIns ins = new ObjInsSnmpIndex(Integer.toString(Integer.valueOf(slot) + 256));
                    ins.init();

                    // 实例化
                    AttrsInsResult ir = ins.getInsResult(subObj, attrs, collId, null, false);
                    insRst.add(subObj, ir);
                }
            }
            modal.insertSubObj(insRst);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw ex;
        }
    }

    private Map<String, VariableBinding> scan(MObjectEntity mo, AttribClassEntity ac, boolean isLocal, int collId)
    {
        Map<String, VariableBinding> retval = null;
        if (mo != null && ac != null)
        {
            ArrayList<String> scanOids = new ArrayList<String>(1);
            scanOids.add(CARD_OID);
            Map<String, Map<String, VariableBinding>> scanRst = null;
            if (isLocal)
            {
                // 本地扫描
                scanRst = this.scanLocal(mo, scanOids, collId);
            }
            else
            {
                // 远程扫描
                scanRst = this.scanRemote(mo, scanOids, collId);
            }

            if (scanRst != null)
            {
                retval = scanRst.get(CARD_OID);
            }
        }
        return retval;
    }

    private String genName(String objName, String cardName, int cardIndex)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(objName).append("-").append(cardName).append("(").append(cardIndex).append(")");
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception
    {
        SubObjInsHarris sub = new SubObjInsHarris();
        SubObjInsInfo subInfo = sub.getSubInfo(59, 10362, -1, true);
        sub.ins(subInfo, -1);
    }

}
