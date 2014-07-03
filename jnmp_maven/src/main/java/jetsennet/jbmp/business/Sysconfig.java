package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.SysconfigDal;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ?
 */
public class Sysconfig
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addSysconfig(String objXml) throws Exception
    {
        SysconfigDal dal = new SysconfigDal();
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateSysconfig(String objXml) throws Exception
    {
        SysconfigDal dal = new SysconfigDal();
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteSysconfig(String keyId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.executeNonQuery(exec.getSqlParser().getDeleteCommandString("NET_SYSCONFIG",
            new SqlCondition("NAME", keyId, SqlLogicType.And, SqlRelationType.In, SqlParamType.String)));
    }
}
