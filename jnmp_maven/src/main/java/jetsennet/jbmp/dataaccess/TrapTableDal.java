/************************************************************************
日 期：2012-1-6
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author 郭祥
 */
public class TrapTableDal extends DefaultDal<TrapTableEntity>
{

    public static Logger logger = Logger.getLogger(TrapTableDal.class);

    /**
     * 构造方法
     */
    public TrapTableDal()
    {
        super(TrapTableEntity.class);
    }

    /**
     * @param traps 参数
     * @param mibId 参数
     */
    @Transactional
    public void insert(List<TrapTableEntity> traps, int mibId)
    {
        TrapTableDal trapTableDal = new TrapTableDal();
        TrapTableEntity oldData = null;
        try
        {
            for (TrapTableEntity trap : traps)
            {
                // 取相同trap_oid数据中的中文描述和中文名称，若有值则新数据采用该值。
                oldData =
                    trapTableDal.get(new SqlCondition("Trap_OID", trap.getTrapOid(), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String));
                if (oldData != null && (!"".equals(oldData.getNameCn()) || !"".equals(oldData.getDescCn())))
                {
                    trap.setNameCn(oldData.getNameCn());
                    trap.setDescCn(oldData.getDescCn());
                }
                trap.setMibId(mibId);
                trap.setParentId(TrapTableEntity.PARENT_ID_DEF);
                int parentId = this.insert(trap);
                List<TrapTableEntity> subs = trap.getSubs();
                if (subs == null || subs.isEmpty())
                {
                    continue;
                }
                for (TrapTableEntity sub : subs)
                {
                    // 取相同trap_oid数据中的中文描述和中文名称，若有值则新数据采用该值。
                    oldData =
                        trapTableDal
                            .get(new SqlCondition("Trap_OID", sub.getTrapOid(), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String));
                    if (oldData != null && (!"".equals(oldData.getNameCn()) || !"".equals(oldData.getDescCn())))
                    {
                        sub.setNameCn(oldData.getNameCn());
                        sub.setDescCn(oldData.getDescCn());
                    }
                    sub.setMibId(mibId);
                    sub.setParentId(parentId);
                    this.insert(sub);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
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
        SqlCondition cond = new SqlCondition("TRAP_ID", oidStr, SqlLogicType.And, SqlRelationType.In, SqlParamType.String);
        conds.add(cond);

        this.delete(conds.toArray(new SqlCondition[0]));
    }

    /**
     * @param mibId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void deleteByType(int mibId) throws Exception
    {
        SqlCondition cond = new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        this.delete(cond);
    }

    /**
     * @param id 参数
     * @throws Exception 异常
     */
    public void deleteById(int id) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("TRAP_ID", Integer.toString(id), SqlLogicType.Or, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("PARENT_ID", Integer.toString(id), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        this.delete(conds);
    }

    /**
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<TrapTableEntity> getByParentId(int parentId) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("PARENT_ID", Integer.toString(parentId), SqlLogicType.And, SqlRelationType.Equal,
                SqlParamType.Numeric) };
        return this.getLst(conds);
    }

    /**
     * @param trapOid 参数
     * @param mibId 参数
     * @return 结果
     */
    @Transactional
    public TrapTableEntity getTrap(String trapOid, int mibId)
    {
        TrapTableEntity retval = null;
        SqlCondition[] conds =
            new SqlCondition[] {
                new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("PARENT_ID", Integer.toString(TrapTableEntity.PARENT_ID_DEF), SqlLogicType.And, SqlRelationType.Equal,
                    SqlParamType.Numeric), new SqlCondition("TRAP_OID", trapOid, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
        SqlCondition[] conds1 =
            new SqlCondition[] {
                new SqlCondition("MIB_ID", Integer.toString(BMPConstants.DEFAULT_MIB_NAME_ID), SqlLogicType.And, SqlRelationType.Equal,
                    SqlParamType.Numeric),
                new SqlCondition("PARENT_ID", Integer.toString(TrapTableEntity.PARENT_ID_DEF), SqlLogicType.And, SqlRelationType.Equal,
                    SqlParamType.Numeric), new SqlCondition("TRAP_OID", trapOid, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
        try
        {
            retval = this.get(conds);
            if (retval == null)
            {
                retval = this.get(conds1);
            }
            if (retval == null)
            {
                return null;
            }
            retval.setSubs(this.getByParentId(retval.getTrapId()));
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = null;
        }
        return retval;
    }

    /**
     * 使用形如"xx,xx,xx"的结构获取Trap，xx代表trapId
     * @param trapIds 参数
     * @return 异常
     */
    public List<TrapTableEntity> getTraps(String trapIds)
    {
        List<TrapTableEntity> retval = null;
        try
        {
            SqlCondition cond = new SqlCondition("TRAP_ID", trapIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
            retval = this.getLst(cond);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 更新数据库中该OID的所有数据的中文名称和中文描述
     * @param OID 参数
     * @param NAME_CN 参数
     * @param DESC_CN 参数
     * @throws Exception 异常
     */
    @Transactional
    public void updateMibTrapNodeByOID(String OID, String NAME_CN, String DESC_CN) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName("BMP_TRAPTABLE");
        cmd.addField("NAME_CN", NAME_CN);
        cmd.addField("DESC_CN", DESC_CN);
        cmd.setFilter(new SqlCondition("TRAP_OID", OID, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String));
        this.update(cmd.toString());
    }

    /**
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<TrapTableEntity> getByType(int mibId) throws Exception
    {
        SqlCondition conds = new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return this.getLst(conds);
    }

    /**
     * 根据trap OID查询trap详细信息
     * 
     * @param oid
     */
    @Transactional
    public Map<String, TrapTableEntity> getTrapByOid(String oid) throws Exception
    {
        Map<String, TrapTableEntity> retMap = new HashMap<String, TrapTableEntity>();
        SqlCondition conds = new SqlCondition("TRAP_OID", oid, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
        TrapTableEntity root = this.get(conds);
        if (root != null)
        {
            int rootId = root.getTrapId();
            conds = new SqlCondition("PARENT_ID", Integer.toString(rootId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
            List<TrapTableEntity> subLst = this.getLst(conds);
            if (subLst != null)
            {
                for (TrapTableEntity sub : subLst)
                {
                    retMap.put(sub.getTrapOid(), sub);
                }
            }
        }
        return retMap;
    }

}
