package jetsennet.jbmp.dataaccess;

import java.util.List;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.TrapEventEntity;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class TrapEventDal extends DefaultDal<TrapEventEntity>
{

    /**
     * 构造方法
     */
    public TrapEventDal()
    {
        super(TrapEventEntity.class);
    }

    /**
     * @param tes 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(List<TrapEventEntity> tes) throws Exception
    {
        for (TrapEventEntity te : tes)
        {
            this.insert(te);
        }
    }

    /**
     * @param te 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insertWithTrans(TrapEventEntity te) throws Exception
    {
        this.insert(te);
    }

    /**
     * 更新trap对应的报警事件ID
     * 
     * @param trapId trap事件ID
     * @param alarmEvtId 报警事件ID
     */
    public void updateAlarmEvtId(int trapId, int alarmEvtId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.addField("ALARMEVT_ID", Integer.toString(alarmEvtId));
        SqlCondition cond = new SqlCondition("TRAPEVT_ID", Integer.toString(trapId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        cmd.setFilter(cond);
        update(cmd.toString());
    }
}
