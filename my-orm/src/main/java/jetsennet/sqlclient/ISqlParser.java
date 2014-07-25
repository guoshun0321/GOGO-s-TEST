/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.util.*;

/**
 * SQL解析接口
 * @author 李小敏
 */
public interface ISqlParser {
	
	/**
	 * 解析查询条件
	 * @param param
	 * @return
	 */
	String parseSqlCondition(SqlCondition... param);
	
	/**
	 * 解析查询条件
	 * @param customParse
	 * @param param
	 * @return
	 */
	String parseSqlCondition(ICustomParser customParse, SqlCondition... param);

	
	/**
	 * 解析查询条件
	 * @param customParse
	 * @param bIncludeWhere
	 * @param param
	 * @return
	 */
	String parseSqlCondition(ICustomParser customParse, boolean bIncludeWhere,
			SqlCondition... param);

	/**
	 * 解析查询条件
	 * @param conditionString
	 * @return
	 */
	SqlCondition parseSqlCondition(String conditionString);

	
	/**忽略安全
	 * @return
	 */
	boolean getIgnoreSecurity();
	void setIgnoreSecurity(boolean value);
	
	/**自定义解析
	 * @return
	 */
	ICustomParser getCustomParser();
	void setCustomParser(ICustomParser value);
	
	/**
	 * 是否安全条件
	 * @param param
	 * @return
	 */
	boolean isSecurityCondition(SqlCondition param);
	
	/**
	 * 是否安全的字段信息
	 * @param field
	 * @return
	 */
	boolean isSecurityField(SqlField field);
	
	/**
	 * 取得SQL新增语句
	 * @param tabName
	 * @param insertItems
	 * @return
	 */
	String getInsertCommandString(String tabName, List<SqlField> insertItems);

	
	/**
	 * 取得SQL更新语句
	 * @param tabName
	 * @param updateItems
	 * @param p
	 * @return
	 * @throws Exception
	 */
	String getUpdateCommandString(String tabName, List<SqlField> updateItems,
			SqlCondition... p) throws Exception;

	
	/**
	 * 取得SQL删除语句
	 * @param tabName
	 * @param p
	 * @return
	 * @throws Exception
	 */
	String getDeleteCommandString(String tabName, SqlCondition... p)
			throws Exception;
	
	/**
	 * 取得SQL查找语句
	 * @param tabName
	 * @param topRow
	 * @param isDistinct 忽略相同
	 * @param fields
	 * @param groupFields 分组字段
	 * @param order
	 * @param p 查询条件
	 * @return select sql command
	 */
	String getSelectCommandString(String tabName, int topRow,
			boolean isDistinct, String fields, String groupFields,
			String order, SqlCondition... p);

	
	/**
	 * 取得SQL查找语句
	 * @param tabName 表名
	 * @param fields 字段列表
	 * @param order 排序
	 * @param p 查询条件
	 * @return select SQL command
	 */
	String getSelectCommandString(String tabName, String fields,
			String order, SqlCondition... p);
	
	/**
	 * 取得SQL查找语句
	 * @param queryInfo
	 * @return select SQL command
	 */
	String getSelectCommandString(SqlQuery queryInfo);

	
	/**
	 * 解析组合查询表
	 * @param tableInfo
	 * @return table info
	 */
	String parseQueryTable(QueryTable tableInfo);

	
	/**
	 * 数据库日期表示
	 * @param datetime
	 * @return datetime string
	 */
	String formatDateTimeString(Date datetime);

	/**
	 * 格式化SQL命令
	 * 
	 * @param command
	 * @param sqlValues
	 * @return SQL命令
	 */
	String formatCommand(String command, SqlValue... sqlValues);
    
	/**
	 * 取得函数名称
	 * @param functon
	 * @return SQL function name
	 */
	/**
	 * @param functon
	 * @return
	 */
	String getSqlFunction(SqlFunction functon);
}