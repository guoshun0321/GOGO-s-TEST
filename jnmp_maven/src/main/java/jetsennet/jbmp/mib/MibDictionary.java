/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jetsennet.jbmp.entity.SnmpNodesEntity;

/**
 * MIB词典，初次实例化时加载。
 * @author Guo
 */
public class MibDictionary
{
    /**
     * 节点集合
     */
    private ArrayList<SnmpNodesEntity> nodes;
    /**
     * 名称索引
     */
    private Map<String, SnmpNodesEntity> name2Nodes;
    /**
     * OID索引
     */
    private Map<String, SnmpNodesEntity> oid2Nodes;
    /**
     * ID索引
     */
    private Map<Integer, SnmpNodesEntity> id2Nodes;

    /**
     * 构造函数
     */
    public MibDictionary()
    {
        nodes = new ArrayList<SnmpNodesEntity>();
        name2Nodes = new HashMap<String, SnmpNodesEntity>();
        oid2Nodes = new HashMap<String, SnmpNodesEntity>();
        id2Nodes = new HashMap<Integer, SnmpNodesEntity>();
    }

    /**
     * 添加节点
     * @param entity 参数
     */
    public void addNode(SnmpNodesEntity entity)
    {
        if (entity == null)
        {
            return;
        }
        if (name2Nodes.get(entity.getNodeName()) == null && oid2Nodes.get(entity.getNodeOid()) == null && id2Nodes.get(entity.getNodeId()) == null)
        {
            nodes.add(entity);
            name2Nodes.put(entity.getNodeName(), entity);
            oid2Nodes.put(entity.getNodeOid(), entity);
            id2Nodes.put(entity.getNodeId(), entity);
            int parentId = entity.getParentId();
            entity.setParent(id2Nodes.get(parentId));
        }
    }

    /**
     * 获取节点
     * @param obj 对象
     * @return 结果
     */
    public SnmpNodesEntity get(Object obj)
    {
        SnmpNodesEntity entity = null;
        if (obj instanceof String)
        {
            entity = name2Nodes.get((String) obj);
            if (entity == null)
            {
                entity = oid2Nodes.get((String) obj);
            }
        }
        else if (obj instanceof Integer)
        {
            entity = id2Nodes.get((Integer) obj);
        }
        return entity;
    }
}
