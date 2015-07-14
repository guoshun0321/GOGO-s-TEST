/************************************************************************
日 期：2012-04-05
作 者: 梁宏杰
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.Class2ClassEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author lianghongjie
 */
public class Class2ClassDal extends DefaultDal<Class2ClassEntity>
{

    /**
     * 构造方法
     */
    public Class2ClassDal()
    {
        super(Class2ClassEntity.class);
    }

    /**
     * @param classid 参数
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<Class2ClassEntity> getByClassId(int classid) throws Exception
    {
        SqlCondition cond = new SqlCondition("CLASS_ID", Integer.toString(classid), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return (ArrayList<Class2ClassEntity>) getLst(cond);
    }

    /**
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<Class2ClassEntity> getByParentId(int parentId) throws Exception
    {
        SqlCondition cond = new SqlCondition("PARENT_ID", Integer.toString(parentId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return (ArrayList<Class2ClassEntity>) getLst(cond);
    }

    /**
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByClassId(int classId) throws Exception
    {
        SqlCondition cond = new SqlCondition("CLASS_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
    }

    /**
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByParentId(int parentId) throws Exception
    {
        SqlCondition cond = new SqlCondition("PARENT_ID", Integer.toString(parentId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
    }

    /**
     * @param classId 参数
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByPk(int classId, int parentId) throws Exception
    {
        SqlCondition[] conds =
            { new SqlCondition("PARENT_ID", Integer.toString(parentId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("CLASS_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        return delete(conds);
    }

    /**
     * @param classId 参数
     * @param parents 参数
     * @throws Exception 异常
     */
    @Transactional
    public void updateRelation(int classId, ArrayList<Integer> parents) throws Exception
    {
        this.deleteByParentId(classId);
        if (parents == null || !parents.isEmpty())
        {
            for (Integer pid : parents)
            {
                if (pid != null)
                {
                    this.insert(new Class2ClassEntity(pid, classId));
                }
            }
        }
    }

    /**
     * 根据parentID获取所属分类
     * @param parentId 参数
     * @param useType 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<Class2ClassEntity> getEntityByParentId(int parentId, int useType) throws Exception
    {
        SqlCondition[] conds =
            { new SqlCondition("PARENT_ID", Integer.toString(parentId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("USE_TYPE", Integer.toString(useType), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        return (ArrayList<Class2ClassEntity>) getLst(conds);
    }
}
