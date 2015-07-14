package jetsennet.jbmp.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.MibBankDal;
import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.ValueTableDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.MibBanksEntity;
import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.entity.ValueTableEntity;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author liwei
 */
public class ExportImportMib
{

    private static final Logger logger = Logger.getLogger(ExportImportMib.class);
    private ArrayList<SnmpNodesEntity> snmpOrderList = new ArrayList<SnmpNodesEntity>();
    private ArrayList<TrapTableEntity> trapOrderList = new ArrayList<TrapTableEntity>();
    private ArrayList<ValueTableEntity> enumsOrderList = new ArrayList<ValueTableEntity>();
    private HashMap<String, Integer> mapEnum = new HashMap<String, Integer>();
    // 数据库操作
    private TrapTableDal trapDal = ClassWrapper.wrapTrans(TrapTableDal.class);
    private ValueTableDal enumDal = ClassWrapper.wrapTrans(ValueTableDal.class);
    private MibBankDal mibDal = ClassWrapper.wrapTrans(MibBankDal.class);
    private SnmpNodesDal snmpDal = ClassWrapper.wrapTrans(SnmpNodesDal.class);

    /**
     * 构造方法
     */
    public ExportImportMib()
    {
    }

    /**
     * 查找mib 返回list
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public ArrayList<MibBanksEntity> queryMibList(int mibId) throws Exception
    {
        MibBankDal mbdal = new MibBankDal();
        return mbdal.getByType(mibId);
    }

    /**
     * 查找 snmpnode返回list
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public ArrayList<SnmpNodesEntity> querySnmpNodeList(int mibId) throws Exception
    {
        SnmpNodesDal sndal = new SnmpNodesDal();
        return sndal.getByType(mibId);
    }

    /**
     * 查找 trap 返回list
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public ArrayList<TrapTableEntity> queryTrapList(int mibId) throws Exception
    {
        TrapTableDal dal = new TrapTableDal();
        return (ArrayList<TrapTableEntity>) dal.getByType(mibId);
    }

    /**
     * 查找mib中的枚举 返回list
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public ArrayList<ValueTableEntity> queryEnumList(int mibId) throws Exception
    {
        ValueTableDal enumDal = new ValueTableDal();
        return enumDal.getByType(mibId);
    }

    /**
     * @param mibId id
     * @return 结果
     */
    public Document getMibXml(int mibId)
    {
        Document document = DocumentHelper.createDocument();
        Element data = document.addElement("Data");
        try
        {
            // mib库
            Element mib = data.addElement("mibtable");
            addMibElement(mib, mibId);
            // trap
            Element traps = data.addElement("traps");
            trap(traps, mibId);
            // enum
            Element enums = data.addElement("enums");
            enums(enums, mibId);
            // snmpnodeTable
            Element snmpNode = data.addElement("snmpnodes");
            snmpNodes(snmpNode, mibId);

        }
        catch (Exception e)
        {
            logger.error("导出mib库中的xml出现异常" + e);
        }
        return document;
    }

    /**
     * @param e 元素
     * @param mibId mibid
     * @return 结果
     */
    public Element snmpNodes(Element e, int mibId)
    {
        try
        {
            ArrayList<SnmpNodesEntity> snmpList = querySnmpNodeList(mibId);
            SnmpNodesEntity en = new SnmpNodesEntity();
            en.setNodeId(0);
            en.setParentId(-1);
            sortSnmp(en, snmpList);
            Iterator<SnmpNodesEntity> iter = snmpOrderList.iterator();
            while (iter.hasNext())
            {
                SnmpNodesEntity p = iter.next();
                e = addSnmpElement(p, e);
            }
        }
        catch (Exception e1)
        {
            logger.error("导出snmpNodes库时异常+" + e1);
        }
        return e;
    }

    /**
     * trap 获取元素
     * @param e 元素
     * @param mibId mibid
     * @return 结果
     */
    public Element trap(Element e, int mibId)
    {
        try
        {
            ArrayList<TrapTableEntity> trapList = queryTrapList(mibId);
            TrapTableEntity trap = new TrapTableEntity();
            trap.setTrapId(-1);
            trap.setParentId(-2);
            sortTrap(trap, trapList);
            Iterator<TrapTableEntity> iter = trapOrderList.iterator();
            while (iter.hasNext())
            {
                TrapTableEntity p = iter.next();
                e = addTrapElement(p, e);
            }
        }
        catch (Exception e1)
        {
            logger.error("导出trap库时异常+" + e1);
        }
        return e;
    }

    /**
     * 获取枚举属性
     * @param e 元素
     * @param mibId mibid
     * @return 结果
     */
    public Element enums(Element e, int mibId)
    {
        try
        {
            ArrayList<ValueTableEntity> enumsList = queryEnumList(mibId);
            ValueTableEntity enums = new ValueTableEntity();
            enums.setValueId(-1);
            enums.setValueType(-2);
            sortEnums(enums, enumsList);
            Iterator<ValueTableEntity> iter = enumsOrderList.iterator();
            while (iter.hasNext())
            {
                ValueTableEntity p = iter.next();
                e = addEnumsElement(p, e);
            }
        }
        catch (Exception e1)
        {
            logger.error("导出enums库时异常+" + e1);
        }
        return e;
    }

    /**
     * snmpNode 排序将子实体类放入父实体类
     * @param parent 父类实体
     * @param child 子类
     */
    public void sortSnmp(SnmpNodesEntity parent, ArrayList<SnmpNodesEntity> child)
    {
        if (parent.getParentId() == 0)
        {
            snmpOrderList.add(parent);
        }
        for (int i = 0; i < child.size(); i++)
        {
            SnmpNodesEntity entiry = child.get(i);
            if (entiry.getParentId() == parent.getNodeId())
            {
                parent.add(entiry);
                sortSnmp(entiry, child);
            }
        }
    }

    /**
     * 找到trap所有的父类
     * @param parent 父类实体
     * @param child 子类
     */
    public void sortTrap(TrapTableEntity parent, ArrayList<TrapTableEntity> child)
    {
        if (parent.getParentId() == -1)
        {
            trapOrderList.add(parent);
        }
        for (int i = 0; i < child.size(); i++)
        {
            TrapTableEntity entiry = child.get(i);
            if (entiry.getParentId() == parent.getTrapId())
            {
                parent.add(entiry);
                sortTrap(entiry, child);
            }
        }
    }

    /**
     * 找到enums所有的父类
     * @param parent 父类实体
     * @param child 子类
     */
    public void sortEnums(ValueTableEntity parent, ArrayList<ValueTableEntity> child)
    {
        if (parent.getValueType() == -1)
        {
            enumsOrderList.add(parent);
        }
        for (int i = 0; i < child.size(); i++)
        {
            ValueTableEntity entiry = child.get(i);
            if (entiry.getValueType() == parent.getValueId())
            {
                parent.add(entiry);
                sortEnums(entiry, child);
            }
        }
    }

    /**
     * @param mib 元素
     * @param mibId id
     * @return 结果
     */
    public Element addMibElement(Element mib, int mibId)
    {
        ArrayList<MibBanksEntity> listMib;
        try
        {
            listMib = queryMibList(mibId);
            MibBanksEntity mibEntity = listMib.get(0);
            Element mibTableId = mib.addElement("MIB_ID");
            mibTableId.setText(mibEntity.getMibId() + "");
            Element mibTableName = mib.addElement("MIB_NAME");
            mibTableName.setText(mibEntity.getMibName());
            Element mibTableAlias = mib.addElement("MIB_ALIAS");
            mibTableAlias.setText(mibEntity.getMibAlias() + "");
            Element mibTableDesc = mib.addElement("MIB_DESC");
            mibTableDesc.setText(mibEntity.getMibDesc() + "");
            Element mibTableFile = mib.addElement("MIB_FILE");
            mibTableFile.setText(mibEntity.getMibFile() + "");
            Element mibTableUser = mib.addElement("CREATE_USER");
            mibTableUser.setText(mibEntity.getCreateUser() + "");
            Element mibTableTime = mib.addElement("CREATE_TIME");
            mibTableTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(mibEntity.getCreateTime()));
        }
        catch (Exception e)
        {
            logger.error("导出mib库异常" + e);
        }
        return mib;
    }

    /**
     * @param snmpentity 实体
     * @param e 元素
     * @return 结果
     */
    public Element addSnmpElement(SnmpNodesEntity snmpentity, Element e)
    {
        Element snmp = e.addElement("snmpnodestable");
        Element snmpNodeId = snmp.addElement("NODE_ID");
        snmpNodeId.setText(snmpentity.getNodeId() + "");
        Element snmpParentId = snmp.addElement("PARENT_ID");
        snmpParentId.setText(snmpentity.getParentId() + "");
        Element snmpMibId = snmp.addElement("MIB_ID");
        snmpMibId.setText(snmpentity.getMibId() + "");
        Element snmpNodeName = snmp.addElement("NODE_NAME");
        snmpNodeName.setText(snmpentity.getNodeName() + "");
        Element snmpNodeOid = snmp.addElement("NODE_OID");
        snmpNodeOid.setText(snmpentity.getNodeOid() + "");
        Element snmpNodeType = snmp.addElement("NODE_TYPE");
        snmpNodeType.setText(snmpentity.getNodeType() + "");
        Element snmpNodeIndex = snmp.addElement("NODE_INDEX");
        snmpNodeIndex.setText(snmpentity.getNodeIndex() + "");
        Element snmpValueType = snmp.addElement("VALUE_TYPE");
        snmpValueType.setText(snmpentity.getValueType() + "");
        Element snmpHandle = snmp.addElement("HANDLE");
        snmpHandle.setText(snmpentity.getHandle() + "");
        Element snmpValueId = snmp.addElement("VALUE_ID");
        snmpValueId.setText(snmpentity.getValueId() + "");
        Element snmpMibFile = snmp.addElement("MIB_FILE");
        snmpMibFile.setText(snmpentity.getMibFile() + "");
        Element snmpSourceType = snmp.addElement("SOURCE_TYPE");
        snmpSourceType.setText(snmpentity.getSourceType() + "");
        Element snmpNodeDesc = snmp.addElement("NODE_DESC");
        snmpNodeDesc.setText(snmpentity.getNodeDesc() + "");
        Element snmpNodeExplain = snmp.addElement("NODE_EXPLAIN");
        snmpNodeExplain.setText(snmpentity.getNodeExplain() + "");
        Element snmpChild = snmp.addElement("CHILD");
        ArrayList<SnmpNodesEntity> childlist = snmpentity.getChild();
        Iterator iter = childlist.iterator();
        while (iter.hasNext())
        {
            SnmpNodesEntity entity = (SnmpNodesEntity) iter.next();
            addSnmpElement(entity, snmpChild);
        }
        return e;
    }

    /**
     * @param trapEntity 实体
     * @param e 元素
     * @return 结果
     */
    public Element addTrapElement(TrapTableEntity trapEntity, Element e)
    {
        Element trapTable = e.addElement("trapTable");
        Element trapId = trapTable.addElement("TRAP_ID");
        trapId.setText(trapEntity.getTrapId() + "");
        Element trapParentId = trapTable.addElement("PARENT_ID");
        trapParentId.setText(trapEntity.getParentId() + "");
        Element trapMibId = trapTable.addElement("MIB_ID");
        trapMibId.setText(trapEntity.getMibId() + "");
        Element trapName = trapTable.addElement("TRAP_NAME");
        trapName.setText(trapEntity.getTrapName() + "");
        Element trapOid = trapTable.addElement("TRAP_OID");
        trapOid.setText(trapEntity.getTrapOid() + "");
        Element trapDesc = trapTable.addElement("TRAP_DESC");
        trapDesc.setText(trapEntity.getTrapDesc() + "");
        Element trapVersion = trapTable.addElement("TRAP_VERSION");
        trapVersion.setText(trapEntity.getTrapVersion() + "");
        Element trapNameCn = trapTable.addElement("NAME_CN");
        trapNameCn.setText(trapEntity.getNameCn() + "");
        Element trapDescCn = trapTable.addElement("DESC_CN");
        trapDescCn.setText(trapEntity.getDescCn() + "");
        Element trapMibFile = trapTable.addElement("MIB_FILE");
        trapMibFile.setText(trapEntity.getMibFile() + "");
        Element trapsChild = trapTable.addElement("CHILD");
        ArrayList<TrapTableEntity> childlist = trapEntity.getChilds();
        Iterator iter = childlist.iterator();
        while (iter.hasNext())
        {
            TrapTableEntity entity = (TrapTableEntity) iter.next();
            addTrapElement(entity, trapsChild);
        }
        return e;
    }

    /**
     * @param enumEntity ValueTableEntity 实体
     * @param e 元素
     * @return 结果
     */
    public Element addEnumsElement(ValueTableEntity enumEntity, Element e)
    {
        Element enumTable = e.addElement("enumTable");

        Element enumId = enumTable.addElement("VALUE_ID");
        enumId.setText(enumEntity.getValueId() + "");
        Element enumValueType = enumTable.addElement("VALUE_TYPE");
        enumValueType.setText(enumEntity.getValueType() + "");
        Element enumMibId = enumTable.addElement("MIB_ID");
        enumMibId.setText(enumEntity.getMibId() + "");
        Element enumAttribValue = enumTable.addElement("ATTRIB_VALUE");
        enumAttribValue.setText(enumEntity.getAttribValue() + "");
        Element enumValueName = enumTable.addElement("VALUE_NAME");
        enumValueName.setText(enumEntity.getValueName() + "");
        Element enumValueDesc = enumTable.addElement("VALUE_DESC");
        enumValueDesc.setText(enumEntity.getValueDesc() + "");
        Element enumsChild = enumTable.addElement("CHILD");
        ArrayList<ValueTableEntity> childlist = enumEntity.getChild();
        Iterator iter = childlist.iterator();
        while (iter.hasNext())
        {
            ValueTableEntity entity = (ValueTableEntity) iter.next();
            addEnumsElement(entity, enumsChild);
        }
        return e;
    }

    /**
     * 导入xml
     * @param xml mibxml字符串
     * @return 结果
     */
    public String importMibXml(String xml)
    {
        String fag = "success";
        boolean f;
        Document doc = null;
        try
        {
            doc = DocumentHelper.parseText(xml);
        }
        catch (DocumentException e2)
        {
            logger.info("字符串转换成xml格式出错，信息：" + e2);
            fag = "fail";
            return fag;
        }
        Element data = doc.getRootElement(); // 获取根节点

        fag = getByMibName(data);
        if (!"success".equals(fag))
        {
            return fag;
        }

        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            exec.transBegin();

            // mib
            int mibId = insertMib(data);
            // trap导入
            Element traps = data.element("traps");
            insertTraps(traps, mibId, -1);
            // enum导入
            Element enums = data.element("enums");
            insertEnums(enums, mibId, -1);

            // 设子snmpNodes
            Element snmpnodes = data.element("snmpnodes");
            insertSnmp(snmpnodes, mibId, 0);

            logger.debug("插入成功");
            exec.transCommit();
        }
        catch (Exception ex)
        {
            exec.transRollback();
            logger.error("", ex);
            fag = "fail";
            return fag;
        }
        finally
        {

            SqlExecutorFacotry.unbindSqlExecutor();
        }
        return fag;
    }

    /**
     * @param data 元素
     * @return 结果
     * @throws Exception 异常
     */
    public int insertMib(Element data) throws Exception
    {

        int i = 0;
        MibBanksEntity miben = new MibBanksEntity();
        Element mibtable = data.element("mibtable");
        String mibName = mibtable.elementTextTrim("MIB_NAME");
        String mibAlias = mibtable.elementTextTrim("MIB_ALIAS");
        String mibDesc = mibtable.elementTextTrim("MIB_DESC");
        String mibFile = mibtable.elementTextTrim("MIB_FILE");
        String mibCreateUser = mibtable.elementTextTrim("CREATE_USER");
        String mibCreateTime = mibtable.elementTextTrim("CREATE_TIME");
        miben.setMibName(mibName);
        miben.setMibAlias(mibAlias);
        // miben.setMibDesc(mibDesc);
        if (!"".equals(mibDesc) && mibDesc != null)
        {
            if ("null".equals(mibDesc))
            {
                miben.setMibDesc("");
            }
            else
            {
                miben.setMibDesc(mibDesc);
            }
        }
        if (!"".equals(mibFile) && mibFile != null)
        {
            if ("null".equals(mibFile))
            {
                miben.setMibFile("");
            }
            else
            {
                miben.setMibFile(mibFile);
            }
        }
        // miben.setMibFile(mibFile);
        // miben.setCreateUser(mibCreateUser);
        if (!"".equals(mibCreateUser) && mibCreateUser != null)
        {
            if ("null".equals(mibCreateUser))
            {
                miben.setCreateUser("");
            }
            else
            {
                miben.setCreateUser(mibCreateUser);
            }
        }

        try
        {
            miben.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(mibCreateTime));
        }
        catch (ParseException e)
        {
            logger.error("mib库中时间转换异常" + e);
        }
        i = mibDal.insert(miben);
        return i;
    }

    /**
     * 递归 插入snmpnodes
     * @param snmpnodes 元素
     * @param mibId id
     * @param parentId 父id
     * @throws Exception 异常
     */
    public void insertSnmp(Element snmpnodes, int mibId, int parentId) throws Exception
    {
        try
        {
            Iterator iterSnmp = snmpnodes.elementIterator("snmpnodestable");
            int snmpid = -3;
            while (iterSnmp.hasNext())
            {
                Element snmpnodeTable = (Element) iterSnmp.next();
                String snmpNodeName = snmpnodeTable.elementTextTrim("NODE_NAME");
                String snmpNodeOid = snmpnodeTable.elementTextTrim("NODE_OID");
                String snmpNodeType = snmpnodeTable.elementTextTrim("NODE_TYPE");
                String snmpNodeIndex = snmpnodeTable.elementTextTrim("NODE_INDEX");
                String snmpValueType = snmpnodeTable.elementTextTrim("VALUE_TYPE");
                String snmpHande = snmpnodeTable.elementTextTrim("HANDLE");
                String snmpValueId = snmpnodeTable.elementTextTrim("VALUE_ID");
                String snmpMibFile = snmpnodeTable.elementTextTrim("MIB_FILE");
                String snmpSourceType = snmpnodeTable.elementTextTrim("SOURCE_TYPE");
                String snmpNodeDesc = snmpnodeTable.elementTextTrim("NODE_DESC");
                String snmpNodeExplain = snmpnodeTable.elementTextTrim("NODE_EXPLAIN");
                SnmpNodesEntity snmpent = new SnmpNodesEntity();
                snmpent.setParentId(parentId);
                snmpent.setMibId(mibId);
                snmpent.setNodeName(snmpNodeName);
                snmpent.setNodeOid(snmpNodeOid);
                if (snmpNodeType != null && !"".equals(snmpNodeType))
                {
                    snmpent.setNodeType(Integer.valueOf(snmpNodeType));
                }
                // snmpent.setNodeIndex(snmpNodeIndex);
                if (!"".equals(snmpNodeIndex) && snmpNodeIndex != null)
                {
                    if ("null".equals(snmpNodeIndex))
                    {
                        snmpent.setNodeIndex("");
                    }
                    else
                    {
                        snmpent.setNodeIndex(snmpNodeIndex);
                    }
                }
                if (!"".equals(snmpValueType) && snmpValueType != null)
                {
                    if ("null".equals(snmpValueType))
                    {
                        snmpent.setValueType("");
                    }
                    else
                    {
                        snmpent.setValueType(snmpValueType);
                    }
                }
                // snmpent.setValueType(snmpValueType);
                if (snmpHande != null && !"".equals(snmpHande))
                {
                    snmpent.setHandle(Integer.valueOf(snmpHande));
                }
                try
                {
                    if (snmpValueId != null && !"".equals(snmpValueId) && !"0".equals(snmpValueId))
                    {
                        snmpent.setValueId(mapEnum.get(snmpValueId));
                    }
                }
                catch (Exception ee1)
                {
                    logger.info("把枚举值插入snmpNode中出现异常！");
                }
                if (!"".equals(snmpMibFile) && snmpMibFile != null)
                {
                    if ("null".equals(snmpMibFile))
                    {
                        snmpent.setMibFile("");
                    }
                    else
                    {
                        snmpent.setMibFile(snmpMibFile);
                    }
                }
                // snmpent.setMibFile(snmpMibFile);
                if (snmpSourceType != null && !"".equals(snmpSourceType))
                {
                    snmpent.setSourceType(Integer.valueOf(snmpSourceType));
                }
                // snmpent.setNodeDesc(snmpNodeDesc);
                if (!"".equals(snmpNodeDesc) && snmpNodeDesc != null)
                {
                    if ("null".equals(snmpNodeDesc))
                    {
                        snmpent.setNodeDesc("");
                    }
                    else
                    {
                        snmpent.setNodeDesc(snmpNodeDesc);
                    }
                }
                if (!"".equals(snmpNodeExplain) && snmpNodeExplain != null)
                {
                    if ("null".equals(snmpNodeExplain))
                    {
                        snmpent.setNodeExplain("");
                    }
                    else
                    {
                        snmpent.setNodeExplain(snmpNodeExplain);
                    }
                }
                // snmpent.setNodeExplain(snmpNodeExplain);

                // SnmpNodesDal snmpDal = new SnmpNodesDal();

                snmpid = snmpDal.insert(snmpent);
                Element child = snmpnodeTable.element("CHILD");
                if (child.elements().size() > 0)
                {
                    insertSnmp(child, mibId, snmpid);
                }
            }
        }
        catch (Exception enode)
        {
            logger.info("导入snmpnodes 节点出现异常." + enode);
            throw new Exception("导入snmpnodes 节点出现异常：");
        }
    }

    /**
     * 递归 插入traps
     * @param traps 元素
     * @param mibId id
     * @param parentId 父id
     * @throws Exception 异常
     */
    public void insertTraps(Element traps, int mibId, int parentId) throws Exception
    {
        try
        {
            int trapParentId = -3;
            Iterator iterTrap = traps.elementIterator("trapTable");
            while (iterTrap.hasNext())
            {
                Element trapTable = (Element) iterTrap.next();
                // String trapId = trapTable.elementTextTrim("TRAP_ID");
                // String trapParentId = trapTable.elementTextTrim("PARENT_ID");
                // String trapMibId = trapTable.elementTextTrim("MIB_ID");
                String trapName = trapTable.elementTextTrim("TRAP_NAME");
                String trapOid = trapTable.elementTextTrim("TRAP_OID");
                String trapDesc = trapTable.elementTextTrim("TRAP_DESC");
                String trapVersion = trapTable.elementTextTrim("TRAP_VERSION");
                String trapNameCn = trapTable.elementTextTrim("NAME_CN");
                String trapDescCn = trapTable.elementTextTrim("DESC_CN");
                String trapMibFile = trapTable.elementTextTrim("MIB_FILE");
                TrapTableEntity trap = new TrapTableEntity();
                trap.setParentId(parentId);
                trap.setMibId(mibId);
                trap.setTrapName(trapName);
                trap.setTrapOid(trapOid);
                // trap.setTrapDesc(trapDesc);
                if (!"".equals(trapDesc) && trapDesc != null)
                {
                    if ("null".equals(trapDesc))
                    {
                        trap.setTrapDesc("");
                    }
                    else
                    {
                        trap.setTrapDesc(trapDesc);
                    }
                }
                if (!"".equals(trapVersion) && trapVersion != null)
                {
                    if ("null".equals(trapVersion))
                    {
                        trap.setTrapVersion("");
                    }
                    else
                    {
                        trap.setTrapVersion(trapVersion);
                    }
                }
                // trap.setTrapVersion(trapVersion);
                // trap.setNameCn(trapNameCn);
                if (!"".equals(trapNameCn) && trapNameCn != null)
                {
                    if ("null".equals(trapNameCn))
                    {
                        trap.setNameCn("");
                    }
                    else
                    {
                        trap.setNameCn(trapNameCn);
                    }
                }
                if (!"".equals(trapDescCn) && trapDescCn != null)
                {
                    if ("null".equals(trapDescCn))
                    {
                        trap.setDescCn("");
                    }
                    else
                    {
                        trap.setDescCn(trapDescCn);
                    }
                }
                // trap.setDescCn(trapDescCn);
                // trap.setMibFile(trapMibFile);
                if (!"".equals(trapMibFile) && trapMibFile != null)
                {
                    if ("null".equals(trapMibFile))
                    {
                        trap.setMibFile("");
                    }
                    else
                    {
                        trap.setMibFile(trapMibFile);
                    }
                }
                // AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);

                trapParentId = trapDal.insert(trap);
                Element child = trapTable.element("CHILD");
                if (child.elements().size() > 0)
                {
                    insertTraps(child, mibId, trapParentId);
                }
            }
        }
        catch (Exception trap)
        {
            logger.info("导入traps 节点出现异常." + trap);
            throw new Exception("导入traps 节点出现异常：");
        }
    }

    /**
     * 递归 插入enums
     * @param enums 元素
     * @param mibId id
     * @param parentId 父id
     * @throws Exception 异常
     */
    public void insertEnums(Element enums, int mibId, int parentId) throws Exception
    {
        try
        {
            boolean fag = true;
            int enumsParentId = -3;
            Iterator iterEnum = enums.elementIterator("enumTable");
            while (iterEnum.hasNext())
            {
                Element enumTable = (Element) iterEnum.next();
                String enumAttribValueId = enumTable.elementTextTrim("VALUE_ID");
                String enumAttribValue = enumTable.elementTextTrim("ATTRIB_VALUE");
                String enumValueName = enumTable.elementTextTrim("VALUE_NAME");
                String enumValueDesc = enumTable.elementTextTrim("VALUE_DESC");
                ValueTableEntity enument = new ValueTableEntity();
                enument.setValueType(parentId);
                enument.setMibId(mibId);
                enument.setAttribValue(enumAttribValue);
                enument.setValueName(enumValueName);
                // enument.setValueDesc(enumValueDesc);
                if (!"".equals(enumValueDesc) && enumValueDesc != null)
                {
                    if ("null".equals(enumValueDesc))
                    {
                        enument.setValueDesc("");
                    }
                    else
                    {
                        enument.setValueDesc(enumValueDesc);
                    }
                }
                // ValueTableDal enumDal = new ValueTableDal();

                enumsParentId = enumDal.insert(enument);

                mapEnum.put(enumAttribValueId, enumsParentId);
                Element child = enumTable.element("CHILD");
                if (child.elements().size() > 0)
                {
                    insertEnums(child, mibId, enumsParentId);
                }
            }
        }
        catch (Exception enumnode)
        {
            logger.info("导入enumnode 节点出现异常." + enumnode);
            throw new Exception("导入enumnode 节点出现异常：");
        }
    }

    /**
     * 判断是否有相同的别名
     * @param e 元素
     * @return 结果
     */
    public String getByMibName(Element e)
    {
        String fag = "success";
        Element mibtable = e.element("mibtable");
        String mibName = mibtable.elementTextTrim("MIB_NAME");
        String mibAlias = mibtable.elementTextTrim("MIB_ALIAS");
        MibBankDal t = new MibBankDal();
        try
        {
            List<MibBanksEntity> list2 = t.getListByMibName(mibName);
            if (list2.size() > 0)
            {
                fag = "@" + mibName;
                return fag;
            }
            List<MibBanksEntity> list = t.getListByName(mibAlias);
            if (list.size() > 0)
            {
                fag = mibAlias;
                return fag;
            }
        }
        catch (Exception exp)
        {
            logger.error("根据别名获取属性集报错" + exp);
            fag = "fail";
            return fag;
        }
        return fag;
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        ExportImportMib test = new ExportImportMib();
        try
        {
            test.getMibXml(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
