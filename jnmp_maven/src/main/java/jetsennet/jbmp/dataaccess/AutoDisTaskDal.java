/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 自动发现任务表，持久化操作
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * 自动发现任务表，持久化操作
 * @author 郭祥
 */
public class AutoDisTaskDal extends DefaultDal<AutoDisTaskEntity>
{

    private static final Logger logger = Logger.getLogger(AutoDisTaskDal.class);

    /**
     * 构造方法
     */
    public AutoDisTaskDal()
    {
        super(AutoDisTaskEntity.class);
    }

    /**
     * 通过采集ID获取任务
     * @param collId 任务ID
     * @return 结果
     * @throws Exception 异常
     */
    public AutoDisTaskEntity getByCollId(int collId) throws Exception
    {
        SqlCondition cond = new SqlCondition("COLL_ID", Integer.toString(collId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return this.get(cond);
    }

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param state 任务状态
     */
    @Transactional
    public void updateState(int taskId, int state)
    {
        try
        {
            String sql = "UPDATE BMP_AUTODISTASK SET STATUS = " + state + " WHERE TASK_ID = " + taskId;
            this.update(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    @Override
    public int update(AutoDisTaskEntity entity)
    {
        int retval = -1;
        try
        {
            retval = super.update(entity);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }
}
