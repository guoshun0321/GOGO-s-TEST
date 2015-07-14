/**
 * 
 */
package jetsennet.jbmp.dataaccess;

import java.util.List;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.Attrib2ClassEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author lianghongjie
 */
public class Attrib2ClassDal extends DefaultDal<Attrib2ClassEntity>
{
    /**
     * 构造方法
     */
    public Attrib2ClassDal()
    {
        super(Attrib2ClassEntity.class);
    }

    /**
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<Attrib2ClassEntity> getByClassId(int classId) throws Exception
    {
        SqlCondition cond = new SqlCondition("CLASS_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return getLst(cond);
    }

    /**
     * @param attribId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<Attrib2ClassEntity> getByAttribId(int attribId) throws Exception
    {
        SqlCondition cond = new SqlCondition("ATTRIB_ID", Integer.toString(attribId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return getLst(cond);
    }

    /**
     * @param attribId 属性
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByAttribId(int attribId) throws Exception
    {
        SqlCondition cond = new SqlCondition("ATTRIB_ID", Integer.toString(attribId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
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
     * @param classId 参数
     * @param attribId 属性
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByPk(int classId, int attribId) throws Exception
    {
        SqlCondition[] conds =
            { new SqlCondition("ATTRIB_ID", Integer.toString(attribId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("CLASS_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        return delete(conds);
    }

    /**
     * 更新分类ID对应的属性ID
     * @param classId 参数
     * @param attrIds 属性
     * @throws Exception 异常
     */
    @Transactional
    public void insert(int classId, List<Integer> attrIds) throws Exception
    {
        this.deleteByClassId(classId);
        if (attrIds != null || attrIds.size() > 0)
        {
            for (Integer attrId : attrIds)
            {
                this.insert(new Attrib2ClassEntity(classId, attrId));
            }
        }
    }

}
