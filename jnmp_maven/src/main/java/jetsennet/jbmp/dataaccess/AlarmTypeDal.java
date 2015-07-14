/************************************************************************
 * 日 期：2012-1-4 
 * 作 者: 郭祥
 * 版 本：v1.3 
 * 描 述: 报警类型
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.entity.AlarmTypeEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class AlarmTypeDal extends DefaultDal<AlarmTypeEntity>
{
    /**
     * 构造方法
     */
    public AlarmTypeDal()
    {
        super(AlarmTypeEntity.class);
    }

    @Override
    public int delete(int objId) throws Exception
    {
        int retval = super.delete(objId);
        this.deleteByList(this.getFromRoot(objId));
        return retval;
    }

    /**
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<Integer> getFromRoot(int parentId) throws Exception
    {
        List<AlarmTypeEntity> acs = this.getAll();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(parentId);
        for (AlarmTypeEntity ac : acs)
        {
            if (ids.contains(ac.getParentId()))
            {
                ids.add(ac.getTypeId());
            }
        }
        return ids;
    }

    /**
     * @param ids 参数
     * @throws Exception 异常
     */
    public void deleteByList(List<Integer> ids) throws Exception
    {
        if (ids == null || ids.isEmpty())
        {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Integer id : ids)
        {
            sb.append(id);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        SqlCondition cond = new SqlCondition("TYPE_ID", sb.toString(), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
        this.delete(cond);
    }
}
