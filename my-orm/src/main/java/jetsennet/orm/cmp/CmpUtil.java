package jetsennet.orm.cmp;

import java.util.List;

import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.sql.cascade.CascadeRepEntity;
import jetsennet.orm.sql.cascade.CascadeSqlDeleteEntity;
import jetsennet.orm.sql.cascade.CascadeSqlEntity;
import jetsennet.orm.sql.cascade.CascadeSqlInsertEntity;
import jetsennet.orm.sql.cascade.CascadeSqlSelectEntity;
import jetsennet.orm.sql.cascade.CascadeSqlUpdateEntity;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.AssertUtil;

public class CmpUtil
{

    public static final CascadeSqlEntity genCscEntity(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj)
    {
        CascadeSqlEntity retval = null;
        switch (entity.getAction())
        {
        case INSERT:
            retval = genInsert(factory, entity, cmpObj);
            break;
        case UPDATE:
            retval = genUpdate(factory, entity, cmpObj);
            break;
        case DELETE:
            retval = genDelete(factory, entity, cmpObj);
            break;
        case SELECT:
            retval = genSelect(factory, entity, cmpObj);
            break;
        case NONE:
            retval = genCsc(factory, entity, cmpObj);
        }
        if (entity.getSubs() != null)
        {
            for (CmpOpEntity sub : entity.getSubs())
            {
                CascadeSqlEntity temp = genCscEntity(factory, sub, cmpObj);
                retval.addSub(temp);
                preSearch(temp, cmpObj);
            }
        }
        return retval;
    }

    public static final CascadeSqlInsertEntity genInsert(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj)
    {
        String tableName = entity.getTableName();
        TableInfo tableInfo = factory.getTableInfo(tableName);
        AssertUtil.assertNotNull(tableInfo, "表未注册：" + tableName);

        CascadeSqlInsertEntity retval = new CascadeSqlInsertEntity(entity.getTableName());
        retval.setTableInfo(tableInfo);
        retval.addValue(entity.getValueMap());

        if (!entity.getValueMap().containsKey(tableInfo.getKey().getName()))
        {
            retval.setAutoKey(true);
        }
        return retval;
    }

    public static final CascadeSqlUpdateEntity genUpdate(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj)
    {
        String tableName = entity.getTableName();
        TableInfo tableInfo = factory.getTableInfo(tableName);
        AssertUtil.assertNotNull(tableInfo, "表未注册：" + tableName);

        CascadeSqlUpdateEntity retval = new CascadeSqlUpdateEntity(entity.getTableName());
        retval.setTableInfo(tableInfo);
        retval.addValue(entity.getValueMap());

        return retval;
    }

    public static final CascadeSqlDeleteEntity genDelete(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj)
    {
        String tableName = entity.getTableName();
        TableInfo tableInfo = factory.getTableInfo(tableName);
        AssertUtil.assertNotNull(tableInfo, "表未注册：" + tableName);

        CascadeSqlDeleteEntity delete = new CascadeSqlDeleteEntity(tableName);
        delete.setTableInfo(tableInfo);
        delete.setSelfLoop(cmpObj.isSelfLoop(tableName));
        delete.setFilterField(entity.getFilterName());
        delete.addValue(entity.getFilterName(), entity.getFilterValue());
        delete.setAffected(isAffected(delete, cmpObj, tableName));

        List<CmpFieldRel> subRels = cmpObj.getSubRels(tableName);
        for (CmpFieldRel subRel : subRels)
        {
            delete.addSub(genSubDelete(factory, entity, cmpObj, subRel));
        }
        return delete;
    }

    private static final CascadeSqlEntity genSubDelete(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj, CmpFieldRel rel)
    {
        String tableName = rel.sTable;
        CascadeSqlDeleteEntity delete = new CascadeSqlDeleteEntity(tableName);
        TableInfo tableInfo = factory.getTableInfo(tableName);
        AssertUtil.assertNotNull(tableInfo, "表未注册：" + tableName);
        delete.setTableInfo(tableInfo);
        delete.setSelfLoop(cmpObj.isSelfLoop(tableName));
        delete.setFilterField(rel.sField);
        delete.addRep(rel.sField, rel.pTable, rel.pField);

        List<CmpFieldRel> subRels = cmpObj.getSubRels(tableName);
        delete.setAffected(subRels.size() > 0 ? true : false);
        for (CmpFieldRel subRel : subRels)
        {
            CascadeSqlEntity temp = genSubDelete(factory, entity, cmpObj, subRel);
            delete.addSub(temp);
        }
        return delete;
    }

    private static final boolean isAffected(CascadeSqlDeleteEntity delete, CmpObject cmpObj, String tableName)
    {
        boolean retval = false;
        String keyName = delete.getTableInfo().getKey().getName();
        if (!delete.getValueMap().containsKey(keyName))
        {
            if (cmpObj.getSubRels(tableName).size() > 0)
            {
                retval = true;
            }
        }
        return retval;
    }

    public static final CascadeSqlSelectEntity genSelect(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj)
    {
        String tableName = entity.getTableName();
        TableInfo tableInfo = factory.getTableInfo(tableName);
        AssertUtil.assertNotNull(tableInfo, "表未注册：" + tableName);

        CascadeSqlSelectEntity select = new CascadeSqlSelectEntity(tableName);
        select.setTableInfo(cmpObj.getMainTable());
        select.setSelfLoop(cmpObj.isSelfLoop(tableName));
        select.setFilterField(entity.getFilterName());
        select.addValue(entity.getFilterName(), entity.getFilterValue());

        List<CmpFieldRel> subRels = cmpObj.getSubRels(tableName);
        for (CmpFieldRel subRel : subRels)
        {
            select.addSub(genSubSelect(factory, entity, cmpObj, subRel));
        }
        return select;
    }

    private static final CascadeSqlEntity genSubSelect(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj, CmpFieldRel rel)
    {
        String tableName = rel.sTable;
        TableInfo tableInfo = factory.getTableInfo(tableName);
        AssertUtil.assertNotNull(tableInfo, "表未注册：" + tableName);

        CascadeSqlSelectEntity select = new CascadeSqlSelectEntity(tableName);
        select.setTableInfo(tableInfo);
        select.setSelfLoop(cmpObj.isSelfLoop(tableName));
        select.setFilterField(rel.sField);
        select.addRep(rel.sField, rel.pTable, rel.pField);

        List<CmpFieldRel> subRels = cmpObj.getSubRels(tableName);
        for (CmpFieldRel subRel : subRels)
        {
            CascadeSqlEntity temp = genSubSelect(factory, entity, cmpObj, subRel);
            select.addSub(temp);
        }
        return select;
    }

    /**
     * 不执行任何操作的CascadeSql 
     * 
     * @param factory
     * @param entity
     * @param cmpObj
     * @return
     */
    public static final CascadeSqlEntity genCsc(SqlSessionFactory factory, CmpOpEntity entity, CmpObject cmpObj)
    {
        String tableName = entity.getTableName();
        TableInfo tableInfo = factory.getTableInfo(tableName);
        AssertUtil.assertNotNull(tableInfo, "表未注册：" + tableName);

        CascadeSqlEntity retval = new CascadeSqlEntity(entity.getTableName());
        retval.setTableInfo(tableInfo);
        retval.addValue(entity.getValueMap());

        return retval;
    }

    /**
     * 沿树分支向上寻找需要关联的字段
     */
    private static final void preSearch(CascadeSqlEntity csc, CmpObject cmpObj)
    {
        List<CmpFieldRel> rels = cmpObj.getParentRel(csc.getTableName());
        for (CmpFieldRel rel : rels)
        {
            if (!csc.getValueMap().containsKey(rel.sField))
            {
                CascadeSqlEntity parent = csc.getParent();
                while (parent != null)
                {
                    if (parent.getTableName().equals(rel.pTable))
                    {
                        csc.addRep(rel.sField, new CascadeRepEntity(rel.pTable, rel.pField));
                        break;
                    }
                }
            }
        }
    }
}
