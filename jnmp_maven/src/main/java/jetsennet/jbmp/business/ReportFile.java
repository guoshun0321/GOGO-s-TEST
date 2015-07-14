package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ReportFileEntity;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class ReportFile
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addReportFile(String objXml) throws Exception
    {
        DefaultDal<ReportFileEntity> dal = new DefaultDal<ReportFileEntity>(ReportFileEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateReportFile(String objXml) throws Exception
    {
        DefaultDal<ReportFileEntity> dal = new DefaultDal<ReportFileEntity>(ReportFileEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除*2013.09.05*
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteReportFile(String keyId) throws Exception
    {
    	ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
    	exec.executeNonQuery(exec.getSqlParser().getDeleteCommandString("BMP_REPORTFILE", 
    			new SqlCondition("FILE_ID", keyId, SqlLogicType.And, SqlRelationType.In, SqlParamType.String)));
    }
}
