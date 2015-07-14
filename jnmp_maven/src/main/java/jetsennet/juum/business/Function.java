package jetsennet.juum.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.juum.dataaccess.FunctionDal;
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
 * @author？
 */
public class Function
{
    /**
     * 构造函数，初始化
     */
    public Function()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        dalFunction = new FunctionDal(sqlExecutor);
    }

    private ConnectionInfo uumConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private FunctionDal dalFunction;

    /**
     * 新增权限
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public String addFunction(String objXml) throws Exception
    {

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {
            // model.put(FunctionDal.PRIMARY_KEY, String.valueOf(sqlExecutor
            // .getNewId(FunctionDal.TABLE_NAME)));
            // new java.rmi.server.UID().toString();
            dalFunction.add(model);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
        return model.get(FunctionDal.PRIMARY_KEY);
    }

    /**
     * 更新权限
     * @param objXml 参数
     * @throws Exception 异常
     */
    public void updateFunction(String objXml) throws Exception
    {

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {
            dalFunction.update(model);

            // 检测循环，如发现则抛出异常
            String parentId = model.get("PARENT_ID");
            if (parentId != null && (!"".equals(parentId)))
            {
                checkCircle(Integer.parseInt(parentId), Integer.parseInt(model.get("ID")));
            }

            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 删除权限
     * @param keyId 参数
     * @throws Exception 异常
     */
    public void deleteFunction(int keyId) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            Object obj = sqlExecutor.executeScalar("SELECT 1 FROM UUM_FUNCTION WHERE PARENT_ID=" + keyId);
            if (obj != null)
            {
                throw new Exception("该权限有子权限存在，请先删除其子权限!");
            }
            sqlExecutor.executeNonQuery("DELETE FROM UUM_ROLEAUTHORITY WHERE FUNCTION_ID = " + keyId);

            dalFunction.deleteById(keyId);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 删除权限
     * @param functionXml 参数
     * @throws Exception 异常
     */
    public void deleteFunctions(String functionXml) throws Exception
    {
        Document xmlDoc = DocumentHelper.parseText(functionXml);
        Element rootNode = xmlDoc.getRootElement();

        // 递归
        String recursiveDelete = FormatUtil.tryGetItemText(rootNode, "Recursive", "0");
        List<Node> itemNodes = rootNode.selectNodes("Item");

        List<String> functionList = new ArrayList<String>();
        for (Node itemNode : itemNodes)
        {
            functionList.add(FormatUtil.tryGetItemText(itemNode, "Id", "-1"));
        }

        String[] arrFuns = new String[functionList.size()];
        arrFuns = functionList.toArray(arrFuns);
        String functionIds = StringUtil.join(arrFuns, ",");

        sqlExecutor.transBegin();
        try
        {
            // if (recursiveDelete == "1")
            if ("1".equals(recursiveDelete))
            {
                functionIds = getAllFunctionsByParentId(functionIds);
            }
            else
            {
                Object objItem =
                    sqlExecutor.executeScalar(sqlExecutor.getSqlParser().getSelectCommandString("UUM_FUNCTION", "1", null,
                        new SqlCondition("PARENT_ID", functionIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
                if (objItem != null)
                {
                    throw new Exception("该权限有下级权限存在，请先删除下级权限!");
                }
            }

            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_ROLEAUTHORITY",
                new SqlCondition("FUNCTION_ID", functionIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_FUNCTION",
                new SqlCondition("ID", functionIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));

            sqlExecutor.transCommit();

        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }

    }

    /**
     * 取得子权限
     * @param parentId
     * @return
     * @throws Exception
     */
    private String getAllFunctionsByParentId(String parentId) throws Exception
    {
        StringBuilder sbFunctions = new StringBuilder();
        sbFunctions.append(parentId);

        List<String> functions =
            sqlExecutor.load(sqlExecutor.getSqlParser().getSelectCommandString("UUM_FUNCTION", "ID", "",
                new SqlCondition("PARENT_ID", parentId, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
                new SqlCondition("ID", "0", SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric)), true);

        if (functions.size() > 0)
        {
            sbFunctions.append(",");
            String[] arrFuns = new String[functions.size()];
            arrFuns = functions.toArray(arrFuns);
            sbFunctions.append(getAllFunctionsByParentId(StringUtil.join(arrFuns, ",")));
        }
        return sbFunctions.toString();
    }

    private void checkCircle(int compareId, int typeId) throws Exception
    {
        List<String> groups =
            sqlExecutor.load(sqlExecutor.getSqlParser().getSelectCommandString("UUM_FUNCTION", "ID", "",
                new SqlCondition("PARENT_ID", Integer.toString(typeId), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
                new SqlCondition("ID", "0", SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric)), true);

        for (String at : groups)
        {
            if (Integer.parseInt(at) == compareId)
            {
                throw new Exception("不能创建循环的组关系!");
            }
            checkCircle(compareId, Integer.parseInt(at));
        }
    }
}
