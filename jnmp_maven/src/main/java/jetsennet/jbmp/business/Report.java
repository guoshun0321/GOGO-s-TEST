package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ReportEntity;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.FormatUtil;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author ？
 */
public class Report
{
    /**
     * 构造函数
     */
    public Report()
    {
        bmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("bmp_driver"), DbConfig.getProperty("bmp_dburl"), DbConfig.getProperty("bmp_dbuser"), DbConfig
                .getProperty("bmp_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(bmpConnectionInfo);

    }

    private ConnectionInfo bmpConnectionInfo;
    private ISqlExecutor sqlExecutor;

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addReport(String objXml) throws Exception
    {
        DefaultDal<ReportEntity> dal = new DefaultDal<ReportEntity>(ReportEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateReport(String objXml) throws Exception
    {
        DefaultDal<ReportEntity> dal = new DefaultDal<ReportEntity>(ReportEntity.class);
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String typeId = map.get("ID");

        // 更新报表
        dal.update(map);

        // 检测循环，如发现则抛出异常
        String parentId = map.get("PARENT_ID");
        if (parentId != null && (!"".equals(parentId)))
        {
            checkCircle(Integer.parseInt(parentId), Integer.parseInt(typeId));
        }
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteReport(int keyId) throws Exception
    {
        DefaultDal<ReportEntity> dal = new DefaultDal<ReportEntity>(ReportEntity.class);
        dal.delete(keyId);
    }

    /**
     * 删除报表
     * @param reportXml 参数
     * @throws Exception 异常
     */
    public void deleteReports(String reportXml) throws Exception
    {
        Document xmlDoc = DocumentHelper.parseText(reportXml);
        Element rootNode = xmlDoc.getRootElement();

        // 递归
        String recursiveDelete = FormatUtil.tryGetItemText(rootNode, "Recursive", "0");
        List<Node> itemNodes = rootNode.selectNodes("Item");

        List<String> reportList = new ArrayList<String>();
        for (Node itemNode : itemNodes)
        {
            reportList.add(FormatUtil.tryGetItemText(itemNode, "Id", "-1"));
        }

        String[] arrReports = new String[reportList.size()];
        arrReports = reportList.toArray(arrReports);
        String reportIds = StringUtil.join(arrReports, ",");

        sqlExecutor.transBegin();
        try
        {
            if ("1".equals(recursiveDelete))
            {
                reportIds = getAllReportsByParentId(reportIds);
            }
            else
            {
                Object objItem =
                    sqlExecutor.executeScalar(sqlExecutor.getSqlParser().getSelectCommandString("BMP_REPORT", "1", null,
                        new SqlCondition("PARENT_ID", reportIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
                if (objItem != null)
                {
                    throw new Exception("该报表管理级别有下级报表存在，请先删除下级报表!");
                }
            }

            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("BMP_REPORT",
                new SqlCondition("ID", reportIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));

            sqlExecutor.transCommit();

        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }

    }

    /**
     * 取得子报表
     * @param parentId
     * @return
     * @throws Exception
     */
    private String getAllReportsByParentId(String parentId) throws Exception
    {
        StringBuilder sbReports = new StringBuilder();
        sbReports.append(parentId);

        List<String> reports =
            sqlExecutor.load(sqlExecutor.getSqlParser().getSelectCommandString("BMP_REPORT", "ID", "",
                new SqlCondition("PARENT_ID", parentId, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
                new SqlCondition("ID", "0", SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric)), true);

        if (reports.size() > 0)
        {
            sbReports.append(",");
            String[] arrFuns = new String[reports.size()];
            arrFuns = reports.toArray(arrFuns);
            sbReports.append(getAllReportsByParentId(StringUtil.join(arrFuns, ",")));
        }
        return sbReports.toString();
    }

    /**
     * 检测是否存在循环
     * @param compareId 父ID
     * @param id 当前ID
     * @return
     * @throws Exception
     */
    private void checkCircle(int compareId, int id) throws Exception
    {
        DefaultDal<ReportEntity> atdal = new DefaultDal<ReportEntity>(ReportEntity.class);
        SqlCondition cond = new SqlCondition("PARENT_ID", Integer.toString(id), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        List<ReportEntity> atLst = atdal.getLst(cond);
        for (ReportEntity at : atLst)
        {
            if (at.getId() == compareId)
            {
                throw new Exception("不能创建循环的组关系!");
            }
            checkCircle(compareId, at.getId());
        }
    }
}
