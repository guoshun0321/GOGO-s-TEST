/************************************************************************
日 期：2011-12-01
作 者:
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.Obj2GroupEntity;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author 郭祥
 */
public class Obj2GroupDal extends DefaultDal<Obj2GroupEntity>
{

    private static final Logger logger = Logger.getLogger(Obj2GroupDal.class);

    /**
     * 构造函数
     */
    public Obj2GroupDal()
    {
        super(Obj2GroupEntity.class);
    }

    /**
     * @param model 参数
     * @throws Exception 异常
     */
    @Transactional
    public void add(HashMap<String, String> model) throws Exception
    {
        String grp = model.get("GROUP_ID");
        if (grp == null || "".equals(grp.trim()))
        {
            return;
        }
        String[] groups = model.get("GROUP_ID").split(",");
        for (int i = 0; i < groups.length; i++)
        {
            this.add(model.get("OBJ_ID"), groups[i], "0");
        }
    }

    /**
     * @param objId 对象
     * @param groupId 对象组
     * @param useType 类型
     * @throws Exception 异常
     */
    public void add(String objId, String groupId, String useType) throws Exception
    {
        SqlField[] param =
            new SqlField[] { new SqlField("OBJ_ID", objId, SqlParamType.Numeric), new SqlField("GROUP_ID", groupId, SqlParamType.Numeric),
                new SqlField("USE_TYPE", useType, SqlParamType.Numeric), };
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.executeNonQuery(exec.getSqlParser().getInsertCommandString(tableInfo.tableName, Arrays.asList(param)));
    }

    /**
     * @param model 参数
     * @throws Exception 异常
     */
    @Transactional
    public void update(HashMap<String, String> model) throws Exception
    {
        String objId = model.get("OBJ_ID");
        this.deleteByObjId(objId);
        this.add(model);
    }

    /**
     * @param objId 参数
     * @throws Exception 异常
     */
    public void deleteByObjId(String objId) throws Exception
    {
        SqlCondition[] p =
            new SqlCondition[] { new SqlCondition("OBJ_ID", objId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("USE_TYPE", "0", SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        delete(p);
    }

    /**
     * @param grpId 参数
     * @throws Exception 异常
     */
    public void deleteByGrpId(String grpId) throws Exception
    {
        SqlCondition[] p = new SqlCondition[] { new SqlCondition("GROUP_ID", grpId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        delete(p);
    }

    /**
     * @param grpId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<Obj2GroupEntity> getObjLst(int grpId) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("GROUP_ID", "" + grpId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        return getLst(conds);
    }

    /**
     * 刷新监控组
     * @param groupIds 参数
     * @param ogs 参数
     * @throws Exception 异常
     */
    @Transactional
    public void refreshIpGroupRel(List<Integer> groupIds, List<Obj2GroupEntity> ogs) throws Exception
    {
        SqlCondition cond =
            new SqlCondition("GROUP_ID", ConvertUtil.listToString(groupIds, ",", false), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric,
                true);
        this.delete(cond);
        if (ogs != null && !ogs.isEmpty())
        {
            for (Obj2GroupEntity og : ogs)
            {
                this.insert(og);
            }
        }
    }

    /**
     * @param objId 参数
     * @param ogs 参数
     * @throws Exception 异常
     */
    public void insert(int objId, List<Obj2GroupEntity> ogs) throws Exception
    {
        SqlCondition cond = new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
        this.delete(cond);
        if (ogs != null && !ogs.isEmpty())
        {
            for (Obj2GroupEntity og : ogs)
            {
                this.insert(og);
            }
        }
    }

    /**
     * @param objId 参数
     * @param groupId 参数
     * @param useType 参数
     */
    @Transactional
    public void insertOrUpdateRel(int objId, int groupId, int useType)
    {
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("GROUP_ID", Integer.toString(groupId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("USE_TYPE", Integer.toString(useType), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
            this.delete(conds);
            Obj2GroupEntity o2g = new Obj2GroupEntity();
            o2g.setObjId(objId);
            o2g.setGroupId(groupId);
            o2g.setUseType(useType);
            this.insert(o2g);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * @param objtectIds 参数
     * @param groupIds 参数
     * @throws Exception 参数
     */
    public void deleteObjectsFromGroups(String[] objtectIds, String[] groupIds) throws Exception
    {
        String objectIdStr = "";
        String groupIdStr = "";
        String sql = "";
        if (objtectIds != null && objtectIds.length != 0 && groupIds != null && groupIds.length != 0)
        {
            for (int i = 0; i < objtectIds.length; i++)
            {
                if (i == objtectIds.length - 1)
                {
                    objectIdStr = objectIdStr + objtectIds[i];
                }
                else
                {
                    objectIdStr = objectIdStr + objtectIds[i] + ",";
                }
            }

            for (int i = 0; i < groupIds.length; i++)
            {
                if (i == groupIds.length - 1)
                {
                    groupIdStr = groupIdStr + groupIds[i];
                }
                else
                {
                    groupIdStr = groupIdStr + groupIds[i] + ",";
                }
            }
        }
        if (groupIdStr.length() == 0)
        {
            sql = "DELETE FROM BMP_OBJ2GROUP WHERE OBJ_ID IN (" + objectIdStr + ")";
        }
        else
        {
            sql = "DELETE FROM BMP_OBJ2GROUP WHERE OBJ_ID IN (" + objectIdStr + ") AND GROUP_ID IN (" + groupIdStr + ")";
        }

        this.update(sql);
    }

}
