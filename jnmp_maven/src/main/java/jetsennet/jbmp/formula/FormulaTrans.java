/************************************************************************
日 期：2011-11-29
作 者: 郭祥
版 本：v1.3
描 述: 公式里面的节点名称用相应的OID替换
历 史：2011-11-30 郭祥 修改取索引的方式
 ************************************************************************/
package jetsennet.jbmp.formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.buffer.SnmpNodeBuffer;
import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.CalcFormulaEntity;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;

/**
 * 将公式里面的节点名称用相应的OID替换
 * 
 * @author 郭祥
 */
public class FormulaTrans
{

    /**
     * 输出
     */
    private String output;
    /**
     * 索引，当输入中存在表格形数据时输出
     */
    private String index;
    // 数据库访问
    private SnmpNodesDal sndal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(FormulaTrans.class);

    /**
     * 构造函数
     */
    public FormulaTrans()
    {
        sndal = ClassWrapper.wrapTrans(SnmpNodesDal.class);
    }

    /**
     * 将公式里面的节点名称用相应的OID替换
     * 
     * @param param 参数
     * @param mibId 参数
     * @param fromBuffer 参数
     * @throws InstanceException 异常
     */
    public void transform(String param, int mibId, boolean fromBuffer) throws InstanceException
    {
        StringBuilder sb = new StringBuilder();
        String tindex = null;

        FormulaElement fe = this.getFormulaElement(param);
        TwoTuple<Map<String, SnmpNodesEntity>, Map<Integer, SnmpNodesEntity>> all = null;
        if (fromBuffer)
        {
            all = this.ensureNodeFromBuffer(fe, mibId);
        }
        else
        {
            all = this.ensureNode(fe, mibId);
        }
        Map<String, SnmpNodesEntity> nodes = all.first;
        Map<Integer, SnmpNodesEntity> parents = all.second;
        CalcFormula cf = fe.getCalcFormula();
        for (CalcFormulaEntity cfe : cf.getFormula())
        {
            if (cfe.type == FormulaConstants.IDENTIFIER_STRING)
            {
                SnmpNodesEntity node = nodes.get(cfe.value);
                int type = node.getNodeType();
                String oid = node.getNodeOid();
                if (type == SnmpNodesEntity.MIBTYPE_SCALAR)
                {
                    this.addOid(oid, 0, sb);
                }
                else if (type == SnmpNodesEntity.MIBTYPE_COLUMN)
                {
                    this.addOid(oid, 1, sb);
                    SnmpNodesEntity parent = parents.get(node.getParentId());
                    tindex = this.ensureIndex(node, parent, tindex);
                }
                else
                {
                    throw new InstanceException("类型为<" + type + ">的节点无法实例化，节点<" + node + ">。");
                }
            }
            else
            {
                sb.append(cfe.value);
            }
        }
        output = sb.toString();
        index = tindex;
    }

    /**
     * 解析公式
     * @param param
     * @return
     * @throws InstanceException
     */
    private FormulaElement getFormulaElement(String param) throws InstanceException
    {
        FormulaElement retval = null;
        try
        {
            retval = FormulaCache.getInstance().getFormula(param);
        }
        catch (Exception ex)
        {
            throw new InstanceException(ex);
        }
        if (retval == null)
        {
            throw new InstanceException(String.format("无法解析公式<%s>。", param));
        }
        return retval;
    }

    /**
     * 从数据库获取数据
     * @param fe
     * @param mibId
     * @return
     * @throws InstanceException
     */
    private TwoTuple<Map<String, SnmpNodesEntity>, Map<Integer, SnmpNodesEntity>> ensureNode(FormulaElement fe, int mibId) throws InstanceException
    {
        try
        {
            String[] names = fe.getOidNames();
            Map<String, SnmpNodesEntity> nodes = sndal.getArrayByName(mibId, names == null ? null : Arrays.asList(names));
            Set<String> keys = nodes.keySet();
            List<Integer> parentIds = new ArrayList<Integer>();
            for (String key : keys)
            {
                SnmpNodesEntity node = nodes.get(key);
                if (node == null)
                {
                    throw new InstanceException(String.format("<%s>库中找不到节点<%s>对应的OID。", mibId, key));
                }
                if (node.getParentId() > 0 && !parentIds.contains(node.getParentId()))
                {
                    parentIds.add(node.getParentId());
                }
            }
            Map<Integer, SnmpNodesEntity> parents = sndal.getArrayById(mibId, parentIds);
            return new TwoTuple<Map<String, SnmpNodesEntity>, Map<Integer, SnmpNodesEntity>>(nodes, parents);
        }
        catch (Exception ex)
        {
            throw new InstanceException(ex);
        }
    }

    /**
     * 从缓存获取数据
     * @param fe
     * @param mibId
     * @return
     * @throws InstanceException
     */
    private TwoTuple<Map<String, SnmpNodesEntity>, Map<Integer, SnmpNodesEntity>> ensureNodeFromBuffer(FormulaElement fe, int mibId)
            throws InstanceException
    {
        Map<String, SnmpNodesEntity> nodes = new HashMap<String, SnmpNodesEntity>();
        Map<Integer, SnmpNodesEntity> parents = new HashMap<Integer, SnmpNodesEntity>();
        try
        {
            String[] names = fe.getOidNames();

            if (names != null)
            {
                for (String name : names)
                {
                    SnmpNodesEntity node = SnmpNodeBuffer.get().getByName(mibId, name);
                    if (node == null)
                    {
                        throw new InstanceException(String.format("<%s>库中找不到节点<%s>对应的OID。", mibId, name));
                    }
                    nodes.put(name, node);
                    if (node.getParentId() > 0)
                    {
                        SnmpNodesEntity parent = SnmpNodeBuffer.get().getById(mibId, node.getParentId());
                        parents.put(node.getParentId(), parent);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw new InstanceException(ex);
        }
        return new TwoTuple<Map<String, SnmpNodesEntity>, Map<Integer, SnmpNodesEntity>>(nodes, parents);
    }

    /**
     * 添加OID
     * @param oid OID
     * @param type 类型。0，标量；1，表格。
     * @param sb
     */
    private void addOid(String oid, int type, StringBuilder sb)
    {
        sb.append("OID:(");
        if (type == 0)
        {
            sb.append(oid);
            sb.append(".0");
        }
        else
        {
            sb.append(oid);
        }
        sb.append(")");
    }

    /**
     * 确认索引
     * @param node 节点
     * @param parent 父节点
     * @param index 位置
     * @return 结果
     * @throws InstanceException 异常
     */
    private String ensureIndex(SnmpNodesEntity node, SnmpNodesEntity parent, String index) throws InstanceException
    {
        String retval = null;
        if (index != null)
        { // 索引已经存在
            return index;
        }
        String sIndex = node.getNodeOid(); // 节点本身的OID
        // if (parent != null) // 父节点存在
        // {
        // String pIndex = parent.getNodeIndex(); // 父节点上包含的索引
        // if (pIndex == null || "".equals(pIndex.trim())) // 父节点不包含索引字段
        // {
        // retval = sIndex; // 索引为节点本身
        // }
        // else
        // // 父节点包含索引
        // {
        // String sSuperior = OIDUtil.getSuperiorOid(sIndex);
        // String pSuperior = OIDUtil.getSuperiorOid(pIndex);
        // if (!sSuperior.equals(pSuperior)) // 索引和节点本身不在同一张表内
        // {
        // retval = sIndex; // 索引为节点本身
        // }
        // else
        // {
        // retval = pIndex; // 索引不是节点本身
        // }
        // }
        // }
        // else
        // {
        // retval = sIndex;
        // logger.debug("找不到<" + node + ">对应节点的父节点。");
        // }
        retval = sIndex;
        return retval;
    }

    /**
     * @return the output
     */
    public String getOutput()
    {
        return output;
    }

    /**
     * @return the index
     */
    public String getIndex()
    {
        return index;
    }

    /**
     * 主方法
     * @param args 参数
     * @throws InstanceException 异常
     */
    public static void main(String[] args) throws InstanceException
    {
        FormulaTrans trans = new FormulaTrans();
        // trans.transform("exp:(hrStorageUsed/hrStorageSize*100);name:(hrStorageDescr\"郭祥\")", 1, false);
        trans.transform("str:(hrStorageUsed\":\"hrStorageSize)", 1, false);
        System.out.println(trans);
    }
}
