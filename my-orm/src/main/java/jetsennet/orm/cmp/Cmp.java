package jetsennet.orm.cmp;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import jetsennet.orm.annotation.Transactional;
import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.session.CscQueryResult;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.sql.cascade.CascadeSqlEntity;
import jetsennet.orm.sql.cascade.CascadeSqlSelectEntity;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.AssertUtil;
import jetsennet.orm.util.UncheckedOrmException;

public class Cmp
{

    /**
     * 配置
     */
    private Configuration config;
    /**
     * Session工厂
     */
    private SqlSessionFactory factory;
    /**
     * cmp对象
     */
    private ConcurrentHashMap<String, CmpObject> cmpObjectMap;

    /**
     * 构造函数。外部通过CmpMgr来获取对象实例。
     * 
     * @param config
     */
    protected Cmp(Configuration config)
    {
        this.config = config;
        this.cmpObjectMap = new ConcurrentHashMap<String, CmpObject>();
        this.factory = SqlSessionFactoryBuilder.builder(this.config);
    }

    /**
     * 注册CMP对象
     * 
     * @param cmpObject
     */
    public void registerCmpObject(CmpObject cmpObject)
    {
        this.cmpObjectMap.put(cmpObject.getObjName(), cmpObject);
        List<TableInfo> tables = cmpObject.getTables();
        for (TableInfo table : tables)
        {
            factory.getTableInfoMgr().registerTableInfo(table.getTableName(), table);
        }
    }

    /**
     * 注销CMP对象
     * 
     * @param objName
     */
    public void unregisterCmpObject(String objName)
    {
        cmpObjectMap.remove(objName);
    }

    /**
     * 判断对象是否已经注册
     * 
     * @param objName
     * @return
     */
    public boolean isRegistered(String objName)
    {
        return this.cmpObjectMap.containsKey(objName);
    }

    /**
     * 执行组合的添加、删除、修改操作
     * 
     * @param objName
     * @param entity
     * @return
     */
    @Transactional
    public String modify(String objName, CmpOpEntity entity)
    {
        CmpObject cmpObj = cmpObjectMap.get(objName);
        AssertUtil.assertNotNull(cmpObj, "不存在CMP对象：" + objName);

        String retval = null;
        CascadeSqlEntity sql = CmpUtil.genCscEntity(factory, entity, cmpObj);
        Session session = factory.openSession();
        boolean isSelf = session.transBegin();
        try
        {
            retval = session.csc(sql);
            session.transCommit(isSelf);
        }
        catch (Exception ex)
        {
            session.transRollback(isSelf);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    /**
     * 执行组合的添加、删除、修改操作
     * 
     * @param objName
     * @param entity
     * @return
     */
    @Transactional
    public String modify(String objName, String xml)
    {
        return this.modify(objName, CmpXmlParse.parse(xml));
    }

    /**
     * 根据主表主键的值，执行删除操作
     * 
     * @param objName
     * @param value
     * @return
     */
    @Transactional
    public String delete(String objName, String value)
    {
        CmpObject cmpObj = cmpObjectMap.get(objName);
        AssertUtil.assertNotNull(cmpObj, "不存在CMP对象：" + objName);

        TableInfo tableInfo = cmpObj.getMainTable();
        CmpOpEntity entity = CmpOpEntity.genDelete(tableInfo.getTableName(), tableInfo.getKey().getName(), value);
        return this.modify(objName, entity);
    }

    /**
     * 查找操作。只考虑父子关系为两张不同表的情况。
     * 
     * @param objName
     * @param keyValue
     * @return
     */
    public CscQueryResult cscQuery(String objName, String keyValue)
    {
        return this.query(objName, keyValue, false);
    }

    /**
     * 查找操作。只考虑父子关系为两张相同表的情况。
     * 
     * @param objName
     * @param keyValue
     * @return
     */
    public CscQueryResult loopQuery(String objName, String keyValue)
    {
        return this.query(objName, keyValue, true);
    }

    /**
     * 根据对象名，表名获取表信息
     * 
     * @param objName
     * @param tableName
     * @return
     */
    public TableInfo getTableInfo(String objName, String tableName)
    {
        TableInfo retval = null;
        CmpObject cmpObj = this.cmpObjectMap.get(objName);
        if (cmpObj != null)
        {
            retval = cmpObj.getTableMap().get(tableName);
        }
        return retval;
    }

    /**
     * 查找操作
     * 
     * @param objName
     * @param value
     * @param isLoop
     * @return
     */
    @Transactional
    private CscQueryResult query(String objName, String value, boolean isLoop)
    {
        CmpObject cmpObj = cmpObjectMap.get(objName);
        AssertUtil.assertNotNull(cmpObj, "不存在CMP对象：" + objName);

        TableInfo tableInfo = cmpObj.getMainTable();
        CmpOpEntity entity = CmpOpEntity.genSelect(tableInfo.getTableName(), tableInfo.getKey().getName(), value);
        CascadeSqlSelectEntity csc = (CascadeSqlSelectEntity) CmpUtil.genCscEntity(factory, entity, cmpObj);

        CscQueryResult retval = null;
        Session session = factory.openSession();
        boolean isSelf = session.transBegin();
        try
        {
            if (isLoop)
            {
                retval = session.cscLoopQuery(csc);
            }
            else
            {
                retval = session.cscQuery(csc);
            }
            session.transCommit(isSelf);
        }
        catch (Exception ex)
        {
            session.transRollback(isSelf);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }
}
