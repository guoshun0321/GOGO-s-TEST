/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jnmp.ins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.ins.InsConfig;
import jetsennet.jbmp.ins.InsResult;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.EditNode;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jnmp.ins.genTable.AbsCreateSnmpData;

/**
 * @author Guo
 */
public class MirandaInstance
{

    /**
     * 对象
     */
    private MObjectEntity mo;
    /**
     * miranda属性
     */
    private static final Map<String, AttributeEntity> mrdAttrMap = new HashMap<String, AttributeEntity>();
    /**
     * 主机名称对应主机对象
     */
    private Map<String, MObjectEntity> str2host;
    /**
     * 主机名称对应<插槽对应板卡>
     */
    private Map<String, Map<Integer, MObjectEntity>> str2boards;
    /**
     * 结果
     */
    private InsResult result;
    // 数据库操作
    private MObjectDal modal;
    private ObjAttribDal oadal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(MirandaInstance.class);

    static
    { // 初始化属性列表
        try
        {
            AttributeDal adal = ClassWrapper.wrapTrans(AttributeDal.class);
            List<AttributeEntity> attrs = adal.getAllMirandaAttr();
            for (AttributeEntity attr : attrs)
            {
                mrdAttrMap.put(attr.getAttribValue(), attr);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 构造方法
     */
    public MirandaInstance()
    {
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
        oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        str2host = new LinkedHashMap<String, MObjectEntity>();
        str2boards = new LinkedHashMap<String, Map<Integer, MObjectEntity>>();
        result = new InsResult();
    }

    /**
     * 实例化
     * @param mo 对象
     * @param attrs 属性
     * @param info 信息
     * @throws InstanceException 异常
     */
    public void ins(MObjectEntity mo, ArrayList<AttributeEntity> attrs, InsConfig info) throws InstanceException
    {
        this.ins(mo);
    }

    /**
     * @return 结果
     * @throws InstanceException 异常
     */
    public InsResult getInsResule() throws InstanceException
    {
        return result;
    }

    /**
     * @param imo 对象
     */
    public void ins(MObjectEntity imo)
    {
        if (imo == null)
        {
            return;
        }
        this.mo = imo;
        AbsCreateSnmpData acs = AbsCreateSnmpData.getInstance();
        String tableName = "NMP_SNMPDAY_" + mo.getObjId();
        if (!acs.checkExist(tableName))
        {
            acs.createTable(tableName);
        }
        str2host.clear();
        str2boards.clear();
        try
        {
            SnmpTable mrdTable = CommonSnmpTable.mirandaTable();
            logger.info("开始获取MIRANDA设备数据。");
            SnmpTableUtil.initSnmpTable1(mrdTable, mo, null, "1.3.6.1.4.1.3872.11.1.1");
            logger.info("MIRANDA设备数据获取结束。");
            ArrayList<ObjAttribEntity> oas = new ArrayList<ObjAttribEntity>();
            for (int i = 0; i < mrdTable.getRowNum(); i++)
            {
                ArrayList<EditNode> rows = mrdTable.getRow(i);
                MirandaInfo info = MirandaInfo.parse(rows);
                if (info == null)
                {
                    return;
                }
                MObjectEntity curHost = str2host.get(info.getHostName());
                if (curHost == null)
                {
                    this.newHostObject(info.getHostName()); // 主机
                }
                int trapIndex = info.getTrapIndex();
                if (trapIndex >= 100747 && trapIndex <= 100766)
                { // 板卡
                    if (info.getStatus() != -1)
                    {
                        this.newBoardObject(curHost, info.getName(), info.getSlotIndex());
                    }
                }
                else if ((trapIndex >= 100475 && trapIndex <= 100481) || trapIndex == 100813)
                {
                }
                else
                {
                    if (info.getSlotIndex() > 0)
                    {
                        ObjAttribEntity oa = this.newObjAttrib(info.getHostName(), info.getSlotIndex(), trapIndex, info.getUsedOid(), info.getName());
                        if (oa != null)
                        {
                            oas.add(oa);
                        }
                    }
                }
            }
            result.setResults(oas);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            this.clean();
        }
    }

    private void clean()
    {
        str2host.clear();
        str2boards.clear();
        str2host = null;
        str2boards = null;
    }

    private MObjectEntity newHostObject(String name)
    {
        MObjectEntity host = new MObjectEntity();
        mo.copyToSub(host);
        host.setParent(mo);
        host.setParentId(mo.getObjId());
        host.setClassId(1202);
        host.setObjName(name);
        host.setClassType("SNMP_NETDEV_MIRANDA_HOST");
        try
        {
            modal.insert(host);
            logger.debug("新建对象,名称：" + name);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        str2host.put(name, host);
        return host;
    }

    private MObjectEntity newBoardObject(MObjectEntity host, String name, int slot)
    {
        MObjectEntity board = new MObjectEntity();
        host.copyToSub(board);
        board.setParent(host);
        board.setParentId(host.getObjId());
        board.setClassId(1203);
        board.setClassType("SNMP_NETDEV_MIRANDA_BOARD");
        board.setObjName(host.getObjName() + " - " + name);
        try
        {
            modal.insert(board);
            logger.debug("新建板卡：" + board.getObjName());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        Map<Integer, MObjectEntity> id2boards = str2boards.get(host.getObjName());
        if (id2boards == null)
        {
            id2boards = new LinkedHashMap<Integer, MObjectEntity>();
            str2boards.put(host.getObjName(), id2boards);
        }
        if (id2boards.get(slot) == null)
        {
            id2boards.put(slot, board);
        }
        return host;
    }

    /**
     * @param host 参数
     * @param slot 参数
     * @param trapIndex 参数
     * @param oid 参数
     * @param name 参数
     * @return 结果
     */
    public ObjAttribEntity newObjAttrib(String host, int slot, int trapIndex, String oid, String name)
    {
        ObjAttribEntity oa = new ObjAttribEntity();
        AttributeEntity ae = mrdAttrMap.get(Integer.toString(trapIndex));
        if (ae == null)
        {
            return null;
        }
        Map<Integer, MObjectEntity> id2boards = str2boards.get(host);
        if (id2boards == null)
        {
            return null;
        }
        MObjectEntity board = id2boards.get(slot);
        if (board == null)
        {
            return null;
        }
        oa.setObjId(board.getObjId());
        oa.setAttribId(ae.getAttribId());
        oa.setAttribParam("exp:(OID:(" + oid + "))");
        // oa.setCollType(ObjAttribEntity.COLL_TYPE_NORMAL);
        oa.setObjattrName(name);
        logger.debug("新建属性：属性名称：" + name);
        return oa;
    }
}
