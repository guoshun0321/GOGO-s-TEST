package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * Trap告警事件
 * 
 * @author 郭祥
 */
@Table(name = "BMP_TRAPEVENT")
public class TrapEventEntity
{

    @Id
    @Column(name = "TRAPEVT_ID")
    private int trapEvtId;
    @Column(name = "OBJ_ID")
    private int objId;
    @Column(name = "OBJATTR_ID")
    private int objAttrId;
    @Column(name = "COLL_TIME")
    private long collTime;
    @Column(name = "TRAP_TIME")
    private long trapTime;
    @Column(name = "TRAP_OID")
    private String trapOid;
    @Column(name = "TRAP_VALUE")
    private String trapValue;
    /**
     * 报警事件ID
     */
    @Column(name = "ALARMEVT_ID")
    private int alarmEvtId;
    /**
     * 默认报警事件ID
     */
    public static final int DEF_ALARMEVT_ID = -1;

    /**
     * 构造函数
     */
    public TrapEventEntity()
    {
        alarmEvtId = DEF_ALARMEVT_ID;
    }

    /**
     * @return the trapEvtId
     */
    public int getTrapEvtId()
    {
        return trapEvtId;
    }

    /**
     * @param trapEvtId the trapEvtId to set
     */
    public void setTrapEvtId(int trapEvtId)
    {
        this.trapEvtId = trapEvtId;
    }

    /**
     * @return the objId
     */
    public int getObjId()
    {
        return objId;
    }

    /**
     * @param objId the objId to set
     */
    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    /**
     * @return the objAttrId
     */
    public int getObjAttrId()
    {
        return objAttrId;
    }

    /**
     * @param objAttrId the objAttrId to set
     */
    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    /**
     * @return the collTime
     */
    public long getCollTime()
    {
        return collTime;
    }

    /**
     * @param collTime the collTime to set
     */
    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    /**
     * @return the trapTime
     */
    public long getTrapTime()
    {
        return trapTime;
    }

    /**
     * @param trapTime the trapTime to set
     */
    public void setTrapTime(long trapTime)
    {
        this.trapTime = trapTime;
    }

    /**
     * @return the trapOid
     */
    public String getTrapOid()
    {
        return trapOid;
    }

    /**
     * @param trapOid the trapOid to set
     */
    public void setTrapOid(String trapOid)
    {
        this.trapOid = trapOid;
    }

    /**
     * @return the trapValue
     */
    public String getTrapValue()
    {
        return trapValue;
    }

    /**
     * @param trapValue the trapValue to set
     */
    public void setTrapValue(String trapValue)
    {
        this.trapValue = trapValue;
    }

    public int getAlarmEvtId()
    {
        return alarmEvtId;
    }

    public void setAlarmEvtId(int alarmEvtId)
    {
        this.alarmEvtId = alarmEvtId;
    }

    @Override
    public String toString()
    {
        return "" + trapEvtId + "\t" + objId + "\t" + objAttrId + "\t" + collTime + "\t" + trapTime + "\t" + trapOid + "\t" + trapValue;
    }

    /**
     * @param time 时间
     * @return 查询
     */
    public static String getQuerySql(Long time)
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName("BMP_TRAPEVENT");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("COLL_TIME", Long.toString(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.Numeric) };
        cmd.setFilter(conds);
        cmd.setOrderString("ORDER BY TRAPEVT_ID");
        return cmd.toString();
    }

    /**
     * @param time 时间
     * @return 删除
     */
    public static String getDelSql(Long time)
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.DeleteCommand);
        cmd.setTableName("BMP_TRAPEVENT");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("COLL_TIME", Long.toString(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.Numeric) };
        cmd.setFilter(conds);
        return cmd.toString();
    }

    public static void main(String[] args) throws Exception
    {
        DefaultDal dal = ClassWrapper.wrapTrans(DefaultDal.class);
        dal.get(TrapEventEntity.getQuerySql(1111l));
        dal.delete(TrapEventEntity.getDelSql(1111l));
    }
}
