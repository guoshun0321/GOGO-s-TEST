/************************************************************************
日 期：2012-1-13
作 者: 郭祥
版 本: v1.3
描 述: 子对象实例化
历 史:
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.SnmpObjTypeDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;
import jetsennet.jbmp.ins.SubObjInsInfo.SubObjInsInfoEntry;
import jetsennet.jbmp.ins.helper.AttrsInsResult;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.jbmp.util.ErrorMessageConstant;
import jetsennet.jbmp.util.InsUtil;
import jetsennet.jbmp.util.OIDUtil;
import jetsennet.jbmp.util.SnmpUtil;
import jetsennet.jbmp.util.TwoTuple;

/**
 * 子对象实例化，BMP_SNMPNODES表的缓存开启
 * 
 * @author 郭祥
 */
public class SubObjsInsDef extends AbsSubObjIns
{

    // 数据库操作
    protected AttribClassDal acdal;
    protected MObjectDal modal;
    /**
     * 日志
     */
    public final Logger logger = Logger.getLogger(SubObjsInsDef.class);

    /**
     * 构造函数
     * @param collId 采集id
     */
    public SubObjsInsDef()
    {
        acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
    }

    /**
     * 选择需要实例化的子对象
     * @param objId 对象id
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public SubObjInsInfo getSubInfo(int objId, int classId, int collId, boolean isLocal) throws Exception
    {
        SubObjInsInfo info = new SubObjInsInfo();
        info.setObjId(objId);
        info.setClassId(classId);

        // 确定对象
        MObjectEntity mo = modal.get(objId);

        // 确定子分类以及子分类标识
        SnmpObjTypeDal sotdal = ClassWrapper.wrapTrans(SnmpObjTypeDal.class);
        TwoTuple<AttribClassEntity, List<SnmpObjTypeEntity>> temp = sotdal.getByClassId(classId);
        if (temp == null)
        {
            return info;
        }
        AttribClassEntity ac = temp.first;
        List<SnmpObjTypeEntity> types = temp.second;

        // 扫描
        if (mo != null && ac != null)
        {
            List<String> scanOids = this.getOids(types);
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

            // 结果处理
            if (scanRst != null)
            {
                Set<String> keys = scanRst.keySet();
                for (String key : keys)
                {
                    Map<String, VariableBinding> oidRsts = scanRst.get(key);
                    Set<String> oids = oidRsts.keySet();
                    for (String oid : oids)
                    {
                        VariableBinding vb = oidRsts.get(oid);
                        // OID值
                        String tvalue = SnmpValueTranser.getInstance().trans(vb, null, 0);
                        logger.debug("子对象实例化，采集到的值：" + tvalue);
                        if (InsUtil.validate(types, key, tvalue))
                        {
                            logger.debug("子对象实例化，可用的值：" + tvalue);
                            // 索引字段
                            String index = OIDUtil.getIndex(oid, key);
                            SubObjInsInfoEntry entry = new SubObjInsInfoEntry();

                            // 名称
                            entry.objName = this.genName(mo, ac, index, tvalue);

                            // 附加信息，格式：索引+“,”+取值
                            tvalue = tvalue == null ? "" : tvalue;
                            entry.addInfo = index + "," + tvalue;
                            info.addSubs(entry);
                        }
                    }
                }
            }
        }
        return info;
    }

    /**
     * 实例化选择的子对象，并插入数据库
     * 
     * @param info 信息
     * @throws Exception 异常
     */
    @Override
    public void ins(SubObjInsInfo info, int collId) throws Exception
    {
        SubObjsInsRst insRst = new SubObjsInsRst();
        try
        {
            // 确定对象
            MObjectEntity mo = modal.get(info.getObjId());

            // 确定分类
            AttribClassEntity ac = acdal.get(info.getClassId());

            // 需要实例化的属性
            List<AttributeEntity> attrs = InsUtil.ensureInsAttr(info.getClassId());

            List<SubObjInsInfoEntry> subs = info.getSubs();
            if (mo != null && ac != null && subs != null)
            {
                for (SubObjInsInfoEntry sub : subs)
                {
                    String addInfo = sub.addInfo;
                    int pos = addInfo.indexOf(",");

                    // 索引
                    String index = addInfo.substring(0, pos);
                    // 标识值(对进程为：进程名)
                    String identValue = addInfo.length() > pos ? addInfo.substring(pos + 1) : "";

                    String name = sub.objName;
                    String desc = sub.desc;
                    int state = sub.objState;

                    // 生成对象
                    MObjectEntity subObj = this.genObj(mo, name, ac, desc);
                    subObj.setField1(index);
                    subObj.setField2(identValue);
                    subObj.setObjState(state);

                    // 实例化类初始化
                    AbsObjIns ins = new ObjInsSnmpIndex(index);
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

    @Override
    public AttrsInsResult getInsResult(MObjectEntity mo, List<AttributeEntity> attrs, int collId, Map<String, String> infos, boolean isLocal)
            throws Exception
    {
        AttrsInsResult retval = new AttrsInsResult();
        try
        {
            // 获取索引字段
            String index = mo.getField1();

            if (index != null && !index.trim().isEmpty())
            {
                // 实例化类初始化
                AbsObjIns ins = new ObjInsSnmpIndex(index);
                ins.init();

                // 实例化
                retval = ins.getInsResult(mo, attrs, collId, null, false);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw ex;
        }
        return retval;
    }

    /**
     * 获取需要扫描的OID
     * @param subs
     * @return
     */
    private List<String> getOids(List<SnmpObjTypeEntity> types)
    {
        List<String> retval = new ArrayList<String>();
        if (types != null && !types.isEmpty())
        {
            for (SnmpObjTypeEntity type : types)
            {
                String column = type.getSnmpSysoid();
                if (!retval.contains(column))
                {
                    retval.add(column);
                }
            }
        }
        return retval;
    }

    /**
     * 远程扫描
     * @param oids
     * @return
     */
    protected Map<String, Map<String, VariableBinding>> scanRemote(MObjectEntity mo, List<String> oids, int collId)
    {
        Map<String, Map<String, VariableBinding>> retval = new LinkedHashMap<String, Map<String, VariableBinding>>();
        if (mo == null || oids == null || oids.isEmpty())
        {
            return retval;
        }
        String[] oidArray = oids.toArray(new String[0]);
        try
        {
            BMPServletContextListener listener = BMPServletContextListener.getInstance();
            Object obj =
                listener.callRemote(collId, "remoteSnmpWalk", new Object[] { mo.getObjId(), mo.getVersion(), oidArray }, new Class[] {
                    int.class,
                    String.class,
                    String[].class }, true);
            retval = (Map<String, Map<String, VariableBinding>>) obj;
        }
        catch (Exception ex)
        {
            logger.error(String.format(ErrorMessageConstant.RMI_ERROR, "remoteSnmpWalk"), ex);
        }
        return retval;
    }

    /**
     * 本地扫描
     * @param mo 对象
     * @param oids 参数
     * @param collId 参数
     * @return 结果
     */
    protected Map<String, Map<String, VariableBinding>> scanLocal(MObjectEntity mo, List<String> oids, int collId)
    {
        Map<String, Map<String, VariableBinding>> retval = new LinkedHashMap<String, Map<String, VariableBinding>>();
        if (mo == null || oids == null || oids.isEmpty())
        {
            return retval;
        }
        String[] oidArray = oids.toArray(new String[0]);
        retval = SnmpUtil.walk(mo.getObjId(), mo.getVersion(), oidArray);
        return retval;
    }

    /**
     * 生成子对象
     * @param mo
     * @param name
     * @param ac
     * @return
     */
    protected MObjectEntity genObj(MObjectEntity mo, String name, AttribClassEntity ac, String desc)
    {
        MObjectEntity sub = mo.copy();
        sub.setObjId(-1);
        sub.setObjName(name);
        sub.setParentId(mo.getObjId());
        sub.setClassId(ac.getClassId());
        sub.setClassType(ac.getClassType());
        sub.setObjDesc(desc);
        return sub;
    }

    /**
     * 生成名称
     * @param pmo
     * @param ac
     * @param index
     * @param value
     * @return
     */
    protected String genName(MObjectEntity pmo, AttribClassEntity ac, String index, String value)
    {
        String retval = null;
        if (value != null && ConvertUtil.containIllegalChar(value))
        {
            value = null;
        }
        if (value != null)
        {
            retval = String.format("%s-%s(%s,%s)", pmo.getObjName(), ac.getClassName(), value, index);
        }
        else
        {
            retval = String.format("%s-%s(%s)", pmo.getObjName(), ac.getClassName(), index);
        }
        return retval;
    }
}
