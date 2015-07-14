/************************************************************************
日 期: 2012-1-6
作 者: 郭祥
版 本: v1.3
描 述: 操作NMP_SNMPNODE表
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.MibBanksEntity;
import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.entity.ValueTableEntity;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.StringUtil;

/**
 * @author 郭祥
 */
public class SnmpNodesDal extends DefaultDal<SnmpNodesEntity>
{

    public static Logger logger = Logger.getLogger(SnmpNodesDal.class);

    /**
     * 构造函数
     */
    public SnmpNodesDal()
    {
        super(SnmpNodesEntity.class);
    }

    /**
     * 将一个类型的数据全部插入数据库
     * @param oids 参数
     * @param mibId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(ArrayList<SnmpNodesEntity> oids, int mibId) throws Exception
    {
        if (oids == null || oids.isEmpty())
        {
            return;
        }

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (SnmpNodesEntity oid : oids)
        {
            ValueTableDal vtdal = new ValueTableDal();
            // 插入枚举类型
            int eid = vtdal.insert(oid, mibId);
            if (eid > 0)
            {
                oid.setValueId(eid);
            }

            // 插入该节点
            // 取相同NODE_OID数据中的中文描述，若有值则新数据采用该值。
            SnmpNodesDal snmpNodesDal = new SnmpNodesDal();
            SnmpNodesEntity oldData =
                snmpNodesDal.get(new SqlCondition("NODE_OID", oid.getNodeOid(), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String));
            if (oldData != null && !"".equals(oldData.getNodeExplain()))
            {
                oid.setNodeExplain(oldData.getNodeExplain());
            }

            oid.setMibId(mibId);
            SnmpNodesEntity parent = oid.getParent();
            if (parent != null)
            {
                Integer pid = map.get(parent.getNodeOid());
                if (pid != null)
                {
                    oid.setParentId(pid);
                }
            }
            insert(oid);
            map.put(oid.getNodeOid(), oid.getNodeId());
        }
    }

    /**
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<SnmpNodesEntity> getByType(int mibId) throws Exception
    {
        SqlCondition conds = new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Text);
        return (ArrayList<SnmpNodesEntity>) getLst(conds);
    }

    /**
     * 根据一批id删除
     * @param ids 参数
     * @throws Exception 异常
     */
    public void delete(Integer[] ids) throws Exception
    {
        if (ids == null || ids.length == 0)
        {
            return;
        }
        ArrayList<SqlCondition> conds = new ArrayList<SqlCondition>();
        String oidStr = "" + ids[0];
        for (int i = 1; i < ids.length; i++)
        {
            oidStr += "," + ids[i];
        }
        SqlCondition cond = new SqlCondition("NODE_ID", oidStr, SqlLogicType.And, SqlRelationType.In, SqlParamType.String);
        conds.add(cond);

        ValueTableDal vtdal = new ValueTableDal();
        List<SnmpNodesEntity> nodes = this.getLst(conds.toArray(new SqlCondition[0]));
        for (SnmpNodesEntity node : nodes)
        {
            int eid = node.getValueId();
            if (eid > 0)
            {
                vtdal.deleteByValueId(eid);
            }
        }
        this.delete(conds.toArray(new SqlCondition[0]));
    }

    /**
     * 删除某个库下面的所有节点
     * @param mibId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void deleteByType(int mibId) throws Exception
    {
        SqlCondition conds = new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);

        ValueTableDal vtdal = new ValueTableDal();
        List<SnmpNodesEntity> nodes = this.getLst(conds);
        for (SnmpNodesEntity node : nodes)
        {
            int eid = node.getValueId();
            if (eid > 0)
            {
                vtdal.deleteByValueId(eid);
            }
        }
        this.delete(conds);
    }

    /**
     * 确定类型对应的MIB库是否存在
     * @param classType 参数
     * @return 结果
     */
    @Transactional
    public int ensureMibType(String classType)
    {
        int retval = BMPConstants.DEFAULT_MIB_NAME_ID;
        AttribClassDal acdal = new AttribClassDal();
        MibBankDal mbdal = new MibBankDal();
        try
        {
            AttribClassEntity ac = acdal.getByClassType(classType);
            if (ac != null)
            {
                MibBanksEntity mb = mbdal.get(ac.getClassId());
                if (mb != null)
                {
                    retval = mb.getMibId();
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 获取指定MIB节点
     * @param mibType 参数
     * @param nodeName 节点名称
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public SnmpNodesEntity get(String mibType, String nodeName) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("MIB_TYPE", mibType, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String),
                new SqlCondition("NODE_NAME", nodeName, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
        return this.get(conds);
    }

    /**
     * 批量获取节点，取不到的节点的值为NULL
     * @param mibId 参数
     * @param names 名称
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public Map<String, SnmpNodesEntity> getArrayByName(int mibId, List<String> names) throws Exception
    {
        Map<String, SnmpNodesEntity> retval = new HashMap<String, SnmpNodesEntity>();
        if (names == null || names.isEmpty())
        {
            return retval;
        }
        ArrayList<SqlCondition> conds = new ArrayList<SqlCondition>();
        conds.add(new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.AndAll, SqlRelationType.Equal, SqlParamType.String));
        for (String name : names)
        {
            conds.add(new SqlCondition("NODE_NAME", name, SqlLogicType.Or, SqlRelationType.Equal, SqlParamType.String));
            retval.put(name, null);
        }
        List<SnmpNodesEntity> nodes = this.getLst(conds.toArray(new SqlCondition[0]));
        if (nodes == null || nodes.isEmpty())
        {
            return retval;
        }
        for (SnmpNodesEntity node : nodes)
        {
            String name = node.getNodeName();
            if (retval.containsKey(name))
            {
                retval.put(name, node);
            }
        }
        return retval;
    }

    /**
     * 批量获取节点，取不到的节点的值为NULL
     * @param mibId 参数
     * @param ids 名称
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public Map<Integer, SnmpNodesEntity> getArrayById(int mibId, List<Integer> ids) throws Exception
    {
        Map<Integer, SnmpNodesEntity> retval = new HashMap<Integer, SnmpNodesEntity>();
        if (ids == null || ids.isEmpty())
        {
            return retval;
        }
        ArrayList<SqlCondition> conds = new ArrayList<SqlCondition>();
        conds.add(new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.AndAll, SqlRelationType.Equal, SqlParamType.String));
        for (Integer id : ids)
        {
            if (id != null)
            {
                conds.add(new SqlCondition("NODE_ID", id.toString(), SqlLogicType.Or, SqlRelationType.Equal, SqlParamType.String));
            }
            retval.put(id, null);
        }
        List<SnmpNodesEntity> nodes = this.getLst(conds.toArray(new SqlCondition[0]));
        if (nodes == null || nodes.isEmpty())
        {
            return retval;
        }
        for (SnmpNodesEntity node : nodes)
        {
            int id = node.getNodeId();
            if (retval.containsKey(id))
            {
                retval.put(id, node);
            }
        }
        return retval;
    }

    /**
     * 删除节点，同时删除子节点，以及节点对应的枚举值
     * @param nodeId 节点
     * @throws Exception 异常
     */
    @Transactional
    public void deleteById(int nodeId) throws Exception
    {
        if (nodeId <= 0)
        {
            return;
        }
        ValueTableDal vtdal = new ValueTableDal();
        ArrayList<SnmpNodesEntity> ids = new ArrayList<SnmpNodesEntity>();
        ids.add(this.get(nodeId));
        for (int i = 0; i < ids.size(); i++)
        {
            SnmpNodesEntity id = ids.get(i);
            List<SnmpNodesEntity> nodes = this.getByParentId(id.getNodeId());
            for (SnmpNodesEntity node : nodes)
            {
                ids.add(node);
            }
            this.delete(id.getNodeId());
            if (id.getValueId() > 0)
            {
                vtdal.deleteByValueId(id.getValueId());
            }
        }
    }

    /**
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<SnmpNodesEntity> getByParentId(int parentId) throws Exception
    {
        List<SnmpNodesEntity> retval = new ArrayList<SnmpNodesEntity>();
        if (parentId <= 0)
        {
            return retval;
        }
        SqlCondition condition =
            new SqlCondition("PARENT_ID", Integer.toString(parentId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return this.getLst(condition);
    }

    /**
     * 根据节点名称在指定库中查找节点
     * @param mibId 参数
     * @param name 名称
     * @return 结果
     * @throws Exception 异常
     */
    public SnmpNodesEntity getByName(int mibId, String name) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String),
                new SqlCondition("NODE_NAME", name, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
        return this.get(conds);
    }

    /**
     * 传入MIB库ID，以及需要检索的OID。获取这些OID对应的枚举值。
     * 结果以Map的形式展现，key为(OID/ENUM_VALUE)，value为枚举对应的字符串
     * 
     * @param mibId
     * @param oids
     * @return
     */
    public Map<String, String> getEnumValue(int mibId, List<String> oids)
    {
        Map<String, String> retMap = new HashMap<String, String>();
        if (oids == null || oids.isEmpty())
        {
            return retMap;
        }

        StringBuilder sb = new StringBuilder();
        for (String oid : oids)
        {
            if (!StringUtil.isNullOrEmpty(oid))
            {
                sb.append("'").append(oid).append("'").append(",");
            }
        }
        if (sb.length() <= 0)
        {
            return retMap;
        }
        sb.deleteCharAt(sb.length() - 1);

        try
        {
            String sql = "SELECT NODE_OID,VALUE_ID FROM BMP_SNMPNODES WHERE MIB_ID = %s AND NODE_OID IN (%s) AND VALUE_ID <> 0";
            sql = String.format(sql, mibId, sb.toString());
            final StringBuilder nodeIdSb = new StringBuilder();
            // OID对应到VALUE_ID
            final Map<String, Integer> node2enumId = new HashMap<String, Integer>();
            DefaultDal.read(sql, new IReadHandle()
            {

                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        int valueId = rs.getInt("VALUE_ID");
                        nodeIdSb.append(valueId).append(",");
                        String nodeOid = rs.getString("NODE_OID");
                        node2enumId.put(nodeOid, valueId);
                    }
                }

            });

            if (nodeIdSb.length() <= 0)
            {
                return retMap;
            }
            nodeIdSb.deleteCharAt(nodeIdSb.length() - 1);

            sql = "SELECT VALUE_TYPE,ATTRIB_VALUE,VALUE_NAME,VALUE_DESC FROM BMP_VALUETABLE WHERE VALUE_TYPE IN (%s)";
            sql = String.format(sql, nodeIdSb.toString());
            final Map<Integer, Map<Integer, String>> enumId2enum = new HashMap<Integer, Map<Integer, String>>();
            DefaultDal.read(sql, new IReadHandle()
            {

                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        int valueType = rs.getInt("VALUE_TYPE");
                        int attribValue = rs.getInt("ATTRIB_VALUE");
                        String valueName = rs.getString("VALUE_NAME");
                        String valueDesc = rs.getString("VALUE_DESC");
                        Map<Integer, String> valMap = enumId2enum.get(valueType);
                        if (valMap == null)
                        {
                            valMap = new HashMap<Integer, String>();
                            enumId2enum.put(valueType, valMap);
                        }
                        valMap.put(attribValue, StringUtil.isNullOrEmpty(valueDesc) ? valueName : valueDesc);
                    }
                }

            });

            Set<String> oidKeys = node2enumId.keySet();
            for (String oidKey : oidKeys)
            {

                Map<Integer, String> enumValues = enumId2enum.get(node2enumId.get(oidKey));
                if (enumValues != null)
                {
                    Set<Integer> enumSet = enumValues.keySet();
                    for (Integer enumVal : enumSet)
                    {
                        retMap.put(oidKey + "/" + enumVal, enumValues.get(enumVal));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retMap;
    }

    public static void main(String[] args)
    {
        int mibId = 1;
        List<String> oids = new ArrayList<String>();
        oids.add("1.3.6.1.2.1.4.1");
        oids.add("1.3.6.1.2.1.6.1");
        oids.add("1.3.6.1.2.1.11.30");
        oids.add("1.3.6.1.2.1.8");
        SnmpNodesDal sndal = new SnmpNodesDal();
        Map<String, String> enumValue = sndal.getEnumValue(mibId, oids);
        System.out.println(enumValue);
    }
}
