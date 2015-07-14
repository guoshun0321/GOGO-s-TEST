package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class SystemLog
{

    /**
     * 构造函数
     */
    public SystemLog()
    {
    }

    /**
     * @param objId 对象id
     * @throws Exception 异常
     */
    @Business
    public void deleteSystemLog(String objId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.executeNonQuery(exec.getSqlParser().getDeleteCommandString("NET_OPERATORLOG",
            new SqlCondition("ID", objId, SqlLogicType.And, SqlRelationType.In, SqlParamType.String)));
    }
}
