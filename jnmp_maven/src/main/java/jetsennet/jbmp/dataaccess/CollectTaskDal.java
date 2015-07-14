/************************************************************************
日 期：2012-04-05
作 者: 梁宏杰
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.CollectTaskEntity;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author？
 */
public class CollectTaskDal extends DefaultDal<CollectTaskEntity>
{

    private static final Logger logger = Logger.getLogger(CollectTaskDal.class);

    /**
     * 构造方法
     */
    public CollectTaskDal()
    {
        super(CollectTaskEntity.class);
    }

    /**
     * @param collId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<CollectTaskEntity> getByCollId(int collId) throws Exception
    {
        String sql = "SELECT * FROM BMP_COLLECTTASK WHERE COLL_ID=" + collId + " AND TASK_STATE=" + CollectTaskEntity.TASK_STATE_NEW;
        return (ArrayList<CollectTaskEntity>) getLst(sql);
    }

    /**
     * 初始化数据库中的任务<br/>
     * @param coll_id 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int initCollectTask(int coll_id) throws Exception
    {
        String sql =
            "UPDATE BMP_COLLECTTASK SET TASK_STATE=" + CollectTaskEntity.TASK_STATE_NEW + " WHERE COLL_ID=" + coll_id + " AND TASK_STATE="
                + CollectTaskEntity.TASK_STATE_RUNNING;
        return update(sql);
    }

    /**
     * 更新
     * @param ct 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int updateState(CollectTaskEntity ct) throws Exception
    {
        return this.updateState(ct.getTaskId(), ct.getTaskState());
    }

    /**
     * 更新
     * @param id 参数
     * @param state 状态
     * @return 结果
     * @throws Exception 异常
     */
    public int updateState(int id, int state) throws Exception
    {
        ISqlExecutor executor = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(executor.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.addField("TASK_STATE", state);
        cmd.setFilter(new SqlCondition("TASK_ID", Integer.toString(id), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));

        return update(cmd.toString());
    }
}
