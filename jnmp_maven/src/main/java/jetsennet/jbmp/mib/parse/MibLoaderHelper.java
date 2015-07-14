/************************************************************************
日 期：2011-12-20
作 者: 郭祥
版 本：v1.3
描 述: MIB文件加载工具
历 史：
 ************************************************************************/
package jetsennet.jbmp.mib.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.node.TextNode;
import jetsennet.jbmp.mib.node.TrapNode;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.jbmp.util.PreorderMibOidComparator;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibTypeSymbol;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpIndex;
import net.percederberg.mibble.snmp.SnmpNotificationType;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.snmp.SnmpTrapType;
import net.percederberg.mibble.type.IntegerType;
import net.percederberg.mibble.value.ObjectIdentifierValue;

import org.apache.log4j.Logger;

/**
 * MIB文件加载工具 保存Mib数据。保存用于实例化的数据。数据来源包含Mib文件和数据库。 在重新加载MIB文件之前，调用reset()清除之前的数据。
 * @author 郭祥
 */
public class MibLoaderHelper
{

    /**
     * Mib文件加载器
     */
    private MibLoader loader;
    /**
     * 被管理的对象集合，包含类型为：OBJECT-TYPE,MODULE-IDENTITY,OBJECT IDENTIFIER,OBJECT-IDENTITY
     */
    private HashMap<String, SnmpNodesEntity> oid2Entity;
    /**
     * 被管理对象
     */
    private ArrayList<SnmpNodesEntity> oids;
    /**
     * Trap对象集合，包含类型为：NOTIFICATION-TYPE,TRAP-TYPE
     */
    private ArrayList<TrapNode> traps;
    /**
     * 名称映射到Trap节点
     */
    private Map<String, TrapNode> name2TrapNode;
    /**
     * 转换后的Trap
     */
    private ArrayList<TrapTableEntity> transTraps;
    /**
     * 类型集合，包含类型为：TEXTUAL-CONVENTION，以及一般类型
     */
    private ArrayList<TextNode> types;
    /**
     * 名称映射到类型节点
     */
    private Map<String, TextNode> name2TypeNode;
    /**
     * 枚举节点
     */
    private ArrayList<SnmpEnumEntity> ees;
    /**
     * 错误处理
     */
    private MibDatasErrorLog log;
    /**
     * 用于查找mib文件的路径
     */
    private String[] mibDirs;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(MibLoaderHelper.class);

    /**
     * 构造函数
     */
    public MibLoaderHelper(String[] dirs)
    {
        oids = new ArrayList<SnmpNodesEntity>();
        oid2Entity = new HashMap<String, SnmpNodesEntity>();
        traps = new ArrayList<TrapNode>();
        types = new ArrayList<TextNode>();
        transTraps = new ArrayList<TrapTableEntity>();
        name2TypeNode = new HashMap<String, TextNode>();
        name2TrapNode = new HashMap<String, TrapNode>();
        ees = new ArrayList<SnmpEnumEntity>();
        log = new MibDatasErrorLog();
        this.mibDirs = dirs;
    }

    /**
     * 重置数据，全部清空
     */
    public void reset()
    {
        oids.clear();
        oid2Entity.clear();
        traps.clear();
        types.clear();
        transTraps.clear();
        name2TrapNode.clear();
        name2TypeNode.clear();
        ees.clear();
        this.initLoader();
    }

    /**
     * 初始化加载器，并添加搜索路径
     */
    private void initLoader()
    {
        logger.debug("初始化MIB加载器。");
        this.loader = new MibLoader();

        // 添加搜索路径
        logger.debug("开始添加搜索路径。");
        if (mibDirs != null)
        {
            for (String mibDir : mibDirs)
            {
                if (mibDir != null && !mibDir.trim().isEmpty())
                {
                    logger.debug("添加搜索路径：" + mibDir);
                    File f = new File(mibDir);
                    if (f.exists())
                    {
                        loader.addAllDirs(f);
                    }
                }
            }
        }
        // String path = MibUtil.getMibDirPath();
        // String path = "E:\\JNMP\\JnmpMibUtil\\MIBS";
        // loader.addAllDirs(new File(path));
    }

    /**
     * 加载MIB文件，注意path参数必须为完整的文件路径
     * @param path 参数
     * @return 结果
     */
    public Mib loadFile(String path)
    {
        File tFile = new File(path);
        return this.loadFile(tFile);
    }

    /**
     * 加载MIB文件
     * @param file 参数
     * @return 结果
     */
    public Mib loadFile(File file)
    {
        Mib result = null;
        String path = file.getAbsolutePath();
        if (!file.exists())
        {
            throw new MibException("无法加载文件：" + file.getAbsolutePath() + "，该文件不存在。");
        }
        try
        {
            logger.info("加载文件：" + path);
            result = loader.load(file);
        }
        catch (IOException ex)
        {
            logger.error("加载文件：" + path + "失败！");
            throw new MibException(ex);
        }
        catch (MibLoaderException ex)
        {
            logger.error("加载文件：" + path + "失败！");
            throw new MibException(MibLoaderUtil.handleMibLoaderException(ex));
        }
        return result;
    }

    /**
     * 加载文件
     * @param files 参数
     * @return 结果
     */
    public String loadFile(File[] files)
    {
        StringBuilder sb = new StringBuilder();
        this.reset();
        for (File file : files)
        {
            try
            {
                this.loadFile(file);
            }
            catch (Exception ex)
            {
                if (sb.length() == 0)
                {
                    sb.append("文件：");
                    sb.append(file.getName());
                }
                else
                {
                    sb.append(",").append(file.getName());
                }
                logger.error("", ex);
            }
        }
        if (sb.length() != 0)
        {
            sb.append("加载异常。");
        }
        this.fillData();
        return sb.toString();
    }

    /**
     * 该函数不成熟，尽量不要使用
     * @param file 参数
     */
    public void unLoadFile(String file)
    {
        try
        {
            logger.info("卸载文件：" + file);
            loader.unload(file);
        }
        catch (MibLoaderException ex)
        {
            throw new MibException(MibLoaderUtil.handleMibLoaderException(ex));
        }
    }

    /**
     * 填充数据，在加载完所有文件后调用
     */
    public void fillData()
    {
        Mib[] mibs = loader.getAllMibs();
        if (mibs != null)
        {
            for (int i = 0; i < mibs.length; i++)
            {
                logger.debug("file :" + mibs[i].getName());
                this.handleMib(mibs[i]);
            }
        }
        else
        {
            logger.error("调用fillData前请先调用loadFile加载文件");
            return;
        }
        SnmpNodesEntity[] temp = oids.toArray(new SnmpNodesEntity[oids.size()]);
        Arrays.sort(temp, new PreorderMibOidComparator());
        this.oids = new ArrayList<SnmpNodesEntity>(Arrays.asList(temp));

        for (SnmpNodesEntity oid : oids)
        {
            // 节点之间的父子关系
            this.setParentId(oid);
            // 节点包含的枚举类型值
            this.handleEnum(oid, oid.getSymbol());
        }

        // Trap数据解析
        for (TrapNode trap : traps)
        {
            this.parseTrap(trap.getSymbol());
        }
    }

    /**
     * 处理Mib文件
     * @param mib
     */
    private void handleMib(Mib mib)
    {
        Iterator it = mib.getAllSymbols().iterator();
        Object obj = null;
        while (it.hasNext())
        {
            obj = it.next();
            if (obj != null && obj instanceof MibSymbol)
            {
                this.handleSymbol((MibSymbol) obj);
            }
        }
    }

    /**
     * 处理所有节点
     * @param symbol
     */
    private void handleSymbol(MibSymbol symbol)
    {
        if (symbol instanceof MibValueSymbol)
        {
            // 节点类型
            MibValueSymbol vSymbol = (MibValueSymbol) symbol;
            if (MibLoaderUtil.isOid(vSymbol))
            {
                // OID
                if (oid2Entity.get(vSymbol.getValue().toString()) == null)
                {
                    this.addNode(vSymbol);
                }
            }
            else if (MibLoaderUtil.isTrap(vSymbol))
            {
                // Trap
                String tName = vSymbol.getName();
                TrapNode trapNode = new TrapNode(tName, null);
                trapNode.setSymbol(vSymbol);
                if (name2TrapNode.get(tName) == null)
                {
                    traps.add(trapNode);
                    name2TrapNode.put(tName, trapNode);
                }
            }
        }
        else if (symbol instanceof MibTypeSymbol)
        {
            // 类型定义类型
            String tName = symbol.getName();
            TextNode textNode = new TextNode(tName, null);
            textNode.setSymbol((MibTypeSymbol) symbol);
            if (name2TypeNode.get(tName) == null)
            {
                types.add(textNode);
                name2TypeNode.put(tName, textNode);
            }
        }
    }

    /**
     * 添加OID类型数据
     * @param symbol
     * @return
     */
    private void addNode(MibValueSymbol symbol)
    {
        String oid = symbol.getValue().toString();
        SnmpNodesEntity entity = new SnmpNodesEntity();
        entity.setNodeName(this.filterNodeName(symbol.getName()));
        entity.setNodeOid(oid);
        this.setNodeType(symbol, entity);
        entity.setMibFile(symbol.getLocation().getFile().getName());
        this.setNodeDesc(symbol, entity);
        entity.setSymbol(symbol);
        oids.add(entity);
        oid2Entity.put(oid, entity);
    }

    private String filterNodeName(String name)
    {
        if (name.contains("-"))
        {
            name = name.replaceAll("-", "_");
        }
        return name;
    }

    /**
     * 父对象ID
     * @param symbol
     * @param entity
     */
    private void setParentId(SnmpNodesEntity entity)
    {
        String parent = MibLoaderUtil.parentOid(entity.getNodeOid());
        if (parent != null)
        {
            SnmpNodesEntity pEntity = oid2Entity.get(parent);
            if (pEntity != null)
            {
                entity.setParent(pEntity);
            }
            else
            {
                entity.setParent(null);
                logger.debug(entity.toString() + "无父对象");
            }
        }
        else
        {
            entity.setParent(null);
            logger.debug(entity.toString() + "无父对象");
        }
    }

    /**
     * 设置节点类型
     * @param symbol
     * @param entity
     */
    private void setNodeType(MibValueSymbol symbol, SnmpNodesEntity entity)
    {
        if (symbol.isScalar())
        {
            entity.setNodeType(SnmpNodesEntity.MIBTYPE_SCALAR);
        }
        else if (symbol.isTable())
        {
            entity.setNodeType(SnmpNodesEntity.MIBTYPE_TABLE);
        }
        else if (symbol.isTableRow())
        {
            entity.setNodeType(SnmpNodesEntity.MIBTYPE_ROW);
            MibType type = symbol.getType();
            if (type instanceof SnmpObjectType)
            {
                SnmpObjectType sotype = (SnmpObjectType) type;
                ArrayList indexs = sotype.getIndex();
                StringBuilder sb = new StringBuilder();
                for (Object index : indexs)
                {
                    if (index == null || (SnmpIndex) index == null || ((SnmpIndex) index).getValue() == null)
                    {
                        logger.debug("节点索引有误：" + symbol.getName());
                    }
                    else
                    {
                        sb.append(((SnmpIndex) index).getValue().toString());
                        sb.append(",");
                    }
                }
                if (sb.length() > 0)
                {
                    sb.deleteCharAt(sb.length() - 1);
                }
                entity.setNodeIndex(sb.toString());
            }
        }
        else if (symbol.isTableColumn())
        {
            entity.setNodeType(SnmpNodesEntity.MIBTYPE_COLUMN);
        }
        else
        {
            entity.setNodeType(SnmpNodesEntity.MIBTYPE_OID);
        }
    }

    private void setNodeDesc(MibValueSymbol symbol, SnmpNodesEntity entity)
    {
        MibType type = symbol.getType();
        if (type != null && type instanceof SnmpObjectType)
        {
            SnmpObjectType soType = (SnmpObjectType) type;
            entity.setNodeDesc(ConvertUtil.replaceAllSpace(soType.getDescription()));
        }
    }

    private void handleEnum(SnmpNodesEntity moe, MibSymbol symbol)
    {
        if (!(symbol instanceof MibValueSymbol))
        {
            return;
        }
        MibValueSymbol mv = (MibValueSymbol) symbol;
        MibType type = mv.getType();
        if (!(type instanceof SnmpObjectType))
        {
            return;
        }
        MibType sType = ((SnmpObjectType) type).getSyntax();
        if (sType instanceof IntegerType)
        {
            IntegerType iType = (IntegerType) sType;
            MibValueSymbol[] symbols = iType.getAllSymbols();
            if (symbols.length > 0)
            {
                SnmpEnumEntity ee = new SnmpEnumEntity();
                ee.setMoid(moe);
                ee.setName(symbol.getName());
                for (MibValueSymbol temp : symbols)
                {
                    ee.add(Integer.valueOf(temp.getValue().toString()), temp.getName());
                }
                moe.setEnumE(ee);
                ees.add(ee);
            }
        }
    }

    /**
     * trap解析
     * @param symbol 参数
     * @return 结果
     */
    public TrapTableEntity parseTrap(MibValueSymbol symbol)
    {
        if (symbol == null)
        {
            return null;
        }
        TrapTableEntity retval = new TrapTableEntity();
        String name = symbol.getName();
        retval.setTrapName(name);
        retval.setParentId(-1);
        retval.setMibFile(symbol.getLocation().getFile().getName());

        MibValue value = symbol.getValue();
        MibType type = symbol.getType();

        ArrayList vars = null;
        if (type instanceof SnmpTrapType)
        {
            SnmpTrapType trap = (SnmpTrapType) type;
            String desc = ConvertUtil.replaceAllSpace(trap.getDescription());
            retval.setTrapDesc(desc);
            String oid = trap.getEnterprise().toString();
            oid = oid + "." + value.toString();
            retval.setTrapOid(oid);
            retval.setTrapVersion("TRAP-TYPE");
            vars = trap.getVariables();
        }
        else if (type instanceof SnmpNotificationType)
        {
            SnmpNotificationType trap = (SnmpNotificationType) type;
            String desc = ConvertUtil.replaceAllSpace(trap.getDescription());
            retval.setTrapDesc(desc);
            String oid = value.toString();
            retval.setTrapOid(oid);
            retval.setTrapVersion("NOTIFICATION-TYPE");
            vars = trap.getObjects();
        }
        if (vars != null && !vars.isEmpty())
        {
            for (Object var : vars)
            {
                TrapTableEntity temp = this.parseTrapType(var);
                if (temp != null)
                {
                    retval.addSub(temp);
                }
            }
        }
        if (retval != null)
        {
            transTraps.add(retval);
        }
        return retval;
    }

    /**
     * @param var 参数
     * @return 结果
     */
    public TrapTableEntity parseTrapType(Object var)
    {
        if (var == null || !(var instanceof ObjectIdentifierValue))
        {
            return null;
        }
        ObjectIdentifierValue oiv = (ObjectIdentifierValue) var;
        String sname = oiv.getName();
        String soid = oiv.toString();
        MibType stype = oiv.getSymbol().getType();
        String sdesc = null;
        if (stype instanceof SnmpObjectType)
        {
            sdesc = ((SnmpObjectType) stype).getDescription();
        }
        else
        {
            sdesc = oiv.getSymbol().getComment();
        }
        sdesc = ConvertUtil.replaceAllSpace(sdesc);
        TrapTableEntity retval = new TrapTableEntity();
        retval.setTrapName(sname);
        retval.setTrapOid(soid);
        retval.setTrapDesc(sdesc);
        return retval;
    }

    /**
     * 获取所有已经加载的MIB模块
     * @return 结果
     */
    public Mib[] getMib()
    {
        return loader.getAllMibs();
    }

    /**
     * @return the oid2Entity
     */
    public HashMap<String, SnmpNodesEntity> getOid2Entity()
    {
        return oid2Entity;
    }

    /**
     * @param oid2Entity the oid2Entity to set
     */
    public void setOid2Entity(HashMap<String, SnmpNodesEntity> oid2Entity)
    {
        this.oid2Entity = oid2Entity;
    }

    /**
     * @return the oids
     */
    public ArrayList<SnmpNodesEntity> getOids()
    {
        return oids;
    }

    /**
     * @param oids the oids to set
     */
    public void setOids(ArrayList<SnmpNodesEntity> oids)
    {
        this.oids = oids;
    }

    /**
     * @return the traps
     */
    public ArrayList<TrapNode> getTraps()
    {
        return traps;
    }

    /**
     * @param traps the traps to set
     */
    public void setTraps(ArrayList<TrapNode> traps)
    {
        this.traps = traps;
    }

    /**
     * @return the name2TrapNode
     */
    public Map<String, TrapNode> getName2TrapNode()
    {
        return name2TrapNode;
    }

    /**
     * @param name2TrapNode the name2TrapNode to set
     */
    public void setName2TrapNode(Map<String, TrapNode> name2TrapNode)
    {
        this.name2TrapNode = name2TrapNode;
    }

    /**
     * @return the types
     */
    public ArrayList<TextNode> getTypes()
    {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(ArrayList<TextNode> types)
    {
        this.types = types;
    }

    /**
     * @return the name2TypeNode
     */
    public Map<String, TextNode> getName2TypeNode()
    {
        return name2TypeNode;
    }

    /**
     * @param name2TypeNode the name2TypeNode to set
     */
    public void setName2TypeNode(Map<String, TextNode> name2TypeNode)
    {
        this.name2TypeNode = name2TypeNode;
    }

    /**
     * @return the transTraps
     */
    public ArrayList<TrapTableEntity> getTransTraps()
    {
        return transTraps;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        String[] mibDir = { "D:/mibs/CISCO 3750G", "D:\\mibs\\huawei1" };
        MibLoaderHelper helper = new MibLoaderHelper(mibDir);
        String mibStr1 = "E:\\WEB\\tomcat6\\webapps\\jnmp_nanning\\mib";
        String mibStr2 = "D:\\mibs\\CISCO 3750G1";
        String mibStr3 = "D:\\mibs\\huawei1";
        helper.loadFile(MibLoaderUtil.getDirFile(new File(mibStr3)).toArray(new File[0]));
        System.out.println(helper);
    }
}
