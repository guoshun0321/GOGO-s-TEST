/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：     12-21 实现SqlRelationType.Parser
************************************************************************/
package jetsennet.sqlclient;

import java.util.*;

import jetsennet.util.*;

/**
 * SQL 解析器
 * @author 李小敏
 */
public class SqlParser implements ISqlParser {
	
	private final String CONDITION_EQUAL = "%1$s = %2$s%3$s%2$s";
	private final String CONDITION_THAN = "%1$s > %2$s%3$s%2$s";
	private final String CONDITION_LESS = "%1$s < %2$s%3$s%2$s";
	private final String CONDITION_THANEQUAL = "%1$s >= %2$s%3$s%2$s";
	private final String CONDITION_LESSEQUAL = "%1$s <= %2$s%3$s%2$s";
	private final String CONDITION_NOTEQUAL = "%1$s <> %2$s%3$s%2$s";
	private final String CONDITION_LIKE = "%1$s LIKE %2$s%%%3$s%%%2$s";
	private final String CONDITION_NOTLIKE = "%1$s NOT LIKE %2$s%%%3$s%%%2$s";
	private final String CONDITION_CUSTOMLIKE = "%1$s LIKE '%2$s'";
	private final String CONDITION_IN = "%1$s %2$s (%3$s)";
	private final String CONDITION_BETWEEN = "%1$s BETWEEN %2$s%3$s%2$s And %2$s%4$s%2$s";
	private final String CONDITION_ISNULL = "%1$s IS NULL";
	private final String CONDITION_ISNOTNULL = "%1$s IS NOT NULL";
	private final String CONDITION_EXISTS = "EXISTS(%1$s)";
	private final String CONDITION_NOTEXISTS = "NOT EXISTS(%1$s)";
	
	private final String INLIKE_SPLIT_CHAR = ",|(\\s+)";

	protected ConditionParser conditionParser;

	private boolean ignoreSecurity;
	
	public boolean getIgnoreSecurity() {
		return ignoreSecurity;
	}

	public void setIgnoreSecurity(boolean value) {
		this.ignoreSecurity = value;
	}

	private ICustomParser customParser;

	public ICustomParser getCustomParser() {
		return customParser;
	}

	public void setCustomParser(ICustomParser value) {
		this.customParser = value;
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#isSecurityCondition(jetsennet.sqlclient.SqlCondition)
	 */
	public boolean isSecurityCondition(SqlCondition param) {
		if (param.getIsSecurity() 
				|| param.getSqlRelationType() == SqlRelationType.Custom 
				|| param.getSqlRelationType() == SqlRelationType.Parser)
			return true;
		
		if (param.getSqlParamType() == SqlParamType.UnKnow)
			return false;
		
		if (param.getParamName().indexOf('\'') >= 0 || param.getParamName().indexOf(';') >= 0)
			return false;
		
		if (param.getSqlRelationType() == SqlRelationType.IsNull 
				|| param.getSqlRelationType() == SqlRelationType.IsNotNull 
				|| param.getSqlParamType() == SqlParamType.SqlSelectCmd)
			return true;

		String paramValue = param.getParamValue();
		
		if (paramValue != null) 
		{
			if ((param.getSqlParamType() == SqlParamType.Numeric 
					|| param.getSqlParamType() == SqlParamType.Boolean) 
				&& (param.getParamName().equalsIgnoreCase(param.getParamValue()) 
						|| !ValidateUtil.isNumeric(param.getParamValue())))
			{
				return false;
			}

			if (param.getSqlParamType() == SqlParamType.Field) {
				paramValue = paramValue.trim();
				if (paramValue.indexOf('\'') >= 0 || paramValue.indexOf(';') >= 0 || paramValue.indexOf(' ') >= 0)
					return false;
			}
		}
		return true;
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#isSecurityField(jetsennet.sqlclient.SqlField)
	 */
	public boolean isSecurityField(SqlField field) {
		if (field.getIsSecurity())
			return true;
		if (field.getSqlParamType() == SqlParamType.UnKnow)
			return false;
		if (field.getFieldName().indexOf('\'') >= 0 || field.getFieldName().indexOf(';') >= 0)
			return false;

		String fieldValue = field.getFieldValue();
		if (fieldValue != null) {
			
			if ((field.getSqlParamType() == SqlParamType.Numeric 
					|| field.getSqlParamType() == SqlParamType.Boolean) && !ValidateUtil.isNumeric(fieldValue))
				return false;
			
			if (field.getSqlParamType() == SqlParamType.Field) {
				fieldValue = fieldValue.trim();
				if (fieldValue.indexOf('\'') >= 0 
						|| fieldValue.indexOf(';') >= 0 
						|| fieldValue.indexOf(' ') >= 0)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 解析查询条件
	 * 
	 * @see jetsennet.sqlclient.ISqlParser#parseSqlCondition(java.lang.String)
	 */
	public SqlCondition parseSqlCondition(String conditionString) {
		if (StringUtil.isNullOrEmpty(conditionString))
			return null;

		if (conditionParser == null)
			conditionParser = new ConditionParser();

		SqlCondition c = null;
		try {
			c = conditionParser.parseCondition(conditionString);
		}
		catch (Exception ex) {
		}

		return c;
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#parseSqlCondition(jetsennet.sqlclient.SqlCondition[])
	 */
	public String parseSqlCondition(SqlCondition... param) {
		return parseSqlCondition(null, true, param);
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#parseSqlCondition(jetsennet.sqlclient.ICustomParser,
	 *      jetsennet.sqlclient.SqlCondition[])
	 */
	public String parseSqlCondition(ICustomParser customParse, SqlCondition... param) {
		return parseSqlCondition(customParse, true, param);
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#parseSqlCondition(jetsennet.sqlclient.ICustomParser,
	 *      boolean, jetsennet.sqlclient.SqlCondition[])
	 */
	public String parseSqlCondition(ICustomParser customParser, boolean bIncludeWhere, SqlCondition... param) {
		String strCMD = parseConditionParams(customParser == null ? this.getCustomParser() : customParser, param);
		if (StringUtil.isNullOrEmpty(strCMD)) {
			return "";
		}
		if (!bIncludeWhere)
			return strCMD;
		return "WHERE " + strCMD;
	}

	/**
	 * @param customParser
	 * @param param
	 * @return
	 */
	private String parseConditionParams(ICustomParser customParser, SqlCondition... param) {
		if (param == null || param.length == 0)
			return "";
		int intCount = 0;
		String strConditionKey = "";
		StringBuilder sbWhere = new StringBuilder(100);
		String pValue;
		
		for (int i = 0; i < param.length; i++) {
			
			if (param[i] == null)
				continue;
			
			if (param[i].getSqlConditions().size() > 0) {
				SqlCondition[] subParam = new SqlCondition[param[i].getSqlConditions().size()];
				for (int j = 0; j < subParam.length; j++)
					subParam[j] = param[i].getSqlConditions().get(j);
				pValue = parseConditionParams(customParser, subParam);
			}
			else {
				pValue = generateConditionParam(customParser, param[i]);
			}
			
			if (pValue.length() == 0)
				continue;
			
			sbWhere.append(strConditionKey);
			sbWhere.append("(");
			sbWhere.append(pValue);
			sbWhere.append(")");
			
			if (strConditionKey.equals(" AND (") || strConditionKey.equals(" OR ("))
				intCount++;
			
			switch (param[i].getSqlLogicType()) {
			case And:
				strConditionKey = " AND ";
				break;
			case Or:
				strConditionKey = " OR ";
				break;
			case AndAll:
				strConditionKey = " AND (";
				break;
			case OrAll:
				strConditionKey = " OR (";
				break;
			}
		}
		
		for (int i = 0; i < intCount; i++)
			sbWhere.append(")");
		
		return sbWhere.toString();
	}

	/**
	 * @param customParser
	 * @param p
	 * @return
	 */
	private String generateConditionParam(ICustomParser customParser, SqlCondition p) {
		
		if (p == null || (StringUtil.isNullOrEmpty(p.getParamName()) && StringUtil.isNullOrEmpty(p.getParamValue())))
			return "";
		if (!this.getIgnoreSecurity() && !isSecurityCondition(p))
			return "";

		String strSqlParamTypeKey = "";
		String strParamValue = p.getParamValue();

		if (StringUtil.isNullOrEmpty(strParamValue) 
				&& (p.getSqlParamType() == SqlParamType.Numeric 
						|| p.getSqlParamType() == SqlParamType.DateTime 
						|| p.getSqlParamType() == SqlParamType.Boolean 
						|| (p.getSqlParamType() == SqlParamType.String && this.getClass() == OracleParser.class))) {
			p.setParamValue(null);
		}

		if (p.getParamValue() == null) {
			strParamValue = "NULL";
		}
		else {
			if (p.getSqlParamType() == SqlParamType.String || p.getSqlParamType() == SqlParamType.Text) {
				strParamValue = p.getParamValue().replace("'", "''");
				strSqlParamTypeKey = "'";
			}
			else if (p.getSqlParamType() == SqlParamType.DateTime 
					&& p.getSqlRelationType() != SqlRelationType.Between) {
				strParamValue = formatDateTimeString(DateUtil.parseDate(p.getParamValue()));
				strSqlParamTypeKey = "";
			}
			else if (p.getSqlParamType() == SqlParamType.Numeric 
					|| p.getSqlParamType() == SqlParamType.Boolean) {
				strParamValue = p.getParamValue().replace("'", "");
			}
			else if (p.getSqlParamType() == SqlParamType.SqlSelectCmd) {
				strParamValue = this.getSelectCommandString(SerializerUtil.deserialize(SqlQuery.class, p.getParamValue()));
			}
		}
		
		switch (p.getSqlRelationType()) {
		case Equal:
			if (p.getParamValue() == null)
				return String.format(CONDITION_ISNULL, p.getParamName());
			else
				return String.format(CONDITION_EQUAL, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case Than:
			return String.format(CONDITION_THAN, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case Less:
			return String.format(CONDITION_LESS, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case ThanEqual:
			return String.format(CONDITION_THANEQUAL, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case LessEqual:
			return String.format(CONDITION_LESSEQUAL, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case NotEqual:
			if (p.getParamValue() == null)
				return String.format(CONDITION_ISNOTNULL, p.getParamName());
			else
				return String.format(CONDITION_NOTEQUAL, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case Like:
			return String.format(CONDITION_LIKE, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case NotLike:
			return String.format(CONDITION_NOTLIKE, p.getParamName(), strSqlParamTypeKey, strParamValue);
		case CustomLike:
			return String.format(CONDITION_CUSTOMLIKE, p.getParamName(), strParamValue);
		case IsNull:
			return String.format(CONDITION_ISNULL, p.getParamName());
		case IsNotNull:
			return String.format(CONDITION_ISNOTNULL, p.getParamName());
		case In:
		case NotIn:
			if (StringUtil.isNullOrEmpty(p.getParamValue()))
				return "";
			String pValue = null;
			if (p.getSqlParamType() == SqlParamType.UnKnow) {
				pValue = strParamValue;
				return String.format(CONDITION_IN, p.getParamName(), p.getSqlRelationType() == SqlRelationType.In ? "IN" : "NOT IN", pValue);
			}
			else {
				// ORA-01795 maximum number of expressions in a list is 1000
				String[] pv = strParamValue.split(",");
				StringBuilder sbValue = new StringBuilder();
				StringBuilder sbResult = new StringBuilder();
				int subCount = 800;
				int curIndex = 0;
				int totalIndex = 0;
				if (pv.length > subCount) {
					sbResult.append("(");
				}
				for (int i = 0; i < pv.length; i++) {
					sbValue.append(strSqlParamTypeKey);
					sbValue.append(pv[i]);
					sbValue.append(strSqlParamTypeKey);
					sbValue.append(",");
					curIndex++;

					if (curIndex >= subCount) {
						if (totalIndex > 0)
							sbResult.append(" or ");
						sbValue = sbValue.deleteCharAt(sbValue.length() - 1);
						pValue = sbValue.toString();
						sbResult.append(String.format(CONDITION_IN, p.getParamName(), p.getSqlRelationType() == SqlRelationType.In ? "IN" : "NOT IN", pValue));

						sbValue = new StringBuilder();
						totalIndex++;
						curIndex = 0;
					}
				}
				if (sbValue.length() > 0) {
					if (totalIndex > 0)
						sbResult.append(" or ");
					sbValue = sbValue.deleteCharAt(sbValue.length() - 1);
					pValue = sbValue.toString();
					sbResult.append(String.format(CONDITION_IN, p.getParamName(), p.getSqlRelationType() == SqlRelationType.In ? "IN" : "NOT IN", pValue));
				}
				if (pv.length > subCount) {
					sbResult.append(")");
				}
				return sbResult.toString();
			}
		case Between:
			if (StringUtil.isNullOrEmpty(p.getParamValue()))
				return "";
			String[] pvBetween = strParamValue.split(",");
			if (pvBetween.length < 2) {
				if (p.getSqlParamType() == SqlParamType.DateTime) {
					try {
						pvBetween[0] = formatDateTimeString(DateUtil.parseDate(pvBetween[0]));
						strSqlParamTypeKey = "";
					}
					catch (Exception bex) {
						strSqlParamTypeKey = "'";
					}
				}
				return String.format(CONDITION_THANEQUAL, p.getParamName(), strSqlParamTypeKey, pvBetween[0]);
			}
			
			if (p.getSqlParamType() == SqlParamType.DateTime) {
				pvBetween[0] = formatDateTimeString(DateUtil.parseDate(pvBetween[0]));
				pvBetween[1] = formatDateTimeString(DateUtil.parseDate(pvBetween[1]));
				strSqlParamTypeKey = "";
			}
			return String.format(CONDITION_BETWEEN, p.getParamName(), strSqlParamTypeKey, pvBetween[0], pvBetween[1]);
		case Exists:
			return String.format(CONDITION_EXISTS, strParamValue);
		case NotExists:
			return String.format(CONDITION_NOTEXISTS, strParamValue);
		case ILike:
			return String.format(CONDITION_LIKE, String.format("%1$s(%2$s)", this.getSqlFunction(SqlFunction.ToUpper), p.getParamName()), strSqlParamTypeKey, strParamValue.toUpperCase());
		case IEqual:
			return String.format(CONDITION_EQUAL, String.format("%1$s(%2$s)", this.getSqlFunction(SqlFunction.ToUpper), p.getParamName()), strSqlParamTypeKey, strParamValue.toUpperCase());
		case InLike:
			if (StringUtil.isNullOrEmpty(p.getParamValue()))
				return "";
			String[] tempArr0 = strParamValue.trim().split(INLIKE_SPLIT_CHAR);
			if (tempArr0.length == 0)
				return "";

			StringBuilder sbCondition0 = new StringBuilder();
			sbCondition0.append("(");
			for (int i = 0; i < tempArr0.length; i++) {
				sbCondition0.append(String.format(CONDITION_LIKE, 
						String.format("%1$s(%2$s)", this.getSqlFunction(SqlFunction.ToUpper), 
								p.getParamName()), strSqlParamTypeKey, tempArr0[i].toUpperCase()));
				
				if (i < tempArr0.length - 1)
					sbCondition0.append(" OR ");
			}
			sbCondition0.append(")");
			return sbCondition0.toString();
		case NotInLike:
			if (StringUtil.isNullOrEmpty(p.getParamValue()))
				return "";
			String[] tempArr1 = strParamValue.trim().split(INLIKE_SPLIT_CHAR);
			if (tempArr1.length == 0)
				return "";

			StringBuilder sbCondition1 = new StringBuilder();
			sbCondition1.append("(");
			for (int i = 0; i < tempArr1.length; i++) {
				sbCondition1.append(String.format(CONDITION_NOTLIKE, p.getParamName(),
						strSqlParamTypeKey,tempArr1[i]));
				if (i < tempArr1.length - 1)
					sbCondition1.append(" AND ");
			}
			sbCondition1.append(")");
			return sbCondition1.toString();
		case SplitLike:
			return String.format(CONDITION_LIKE, p.getParamName(), 
					strSqlParamTypeKey,
					StringUtil.join(strParamValue.toCharArray(),"%"));
		case Custom:
			if (customParser != null)
				return customParser.parseCondition(p);
			else
				return "";
		case Parser:
			SqlCondition parerCodition = this.parseSqlCondition(p.getParamValue());
			if(parerCodition!=null)
			{
				return this.parseSqlCondition(customParser,false,parerCodition);
			}
		}
		return "";
	}

	/** 
	 * @see jetsennet.sqlclient.ISqlParser#getInsertCommandString(java.lang.String,
	 *      java.util.List)
	 */
	public String getInsertCommandString(String tabName, List<SqlField> insertItems) {
		if (insertItems == null || insertItems.size() == 0)
			return "";
		StringBuilder sbCMD = new StringBuilder(200);
		StringBuilder sbField = new StringBuilder(200);
		StringBuilder sbValue = new StringBuilder(200);
		for (SqlField item : insertItems) {
			if (item != null && !StringUtil.isNullOrEmpty(item.getFieldName())) {
				if (!this.getIgnoreSecurity() && !isSecurityField(item))
					continue;

				sbField.append(item.getFieldName());
				sbField.append(",");
				if (item.getFieldValue() == null) {
					sbValue.append("NULL,");
				}
				else {
					if (item.getSqlParamType() == SqlParamType.Numeric) {
						sbValue.append(item.getFieldValue().replace("'", ""));
						sbValue.append(",");
					}
					else if (item.getSqlParamType() == SqlParamType.String 
							|| item.getSqlParamType() == SqlParamType.Text) {
						sbValue.append("'");
						sbValue.append(item.getFieldValue().replace("'", "''"));
						sbValue.append("',");
					}
					else if (item.getSqlParamType() == SqlParamType.DateTime) {
						sbValue.append(formatDateTimeString(DateUtil.parseDate(item.getFieldValue())));
						sbValue.append(",");
					}
					else {
						sbValue.append(item.getFieldValue());
						sbValue.append(",");
					}
				}
			}
		}
		sbCMD.append("INSERT INTO ");
		sbCMD.append(tabName.replaceAll("([\\[]|[\\]])+", ""));
		sbCMD.append(" (");
		sbCMD.append(sbField.deleteCharAt(sbField.length() - 1).toString());
		sbCMD.append(") VALUES (");
		sbCMD.append(sbValue.deleteCharAt(sbValue.length() - 1).toString());
		sbCMD.append(")");
		return sbCMD.toString();
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#getUpdateCommandString(java.lang.String,
	 *      java.util.List, jetsennet.sqlclient.SqlCondition[])
	 */
	public String getUpdateCommandString(String tabName, List<SqlField> updateItems, SqlCondition... p) throws Exception {
		if (updateItems == null || updateItems.size() == 0)
			return "";
		StringBuilder sbCMD = new StringBuilder(400);
		StringBuilder sbFV = new StringBuilder(400);
		sbCMD.append("UPDATE ");
		sbCMD.append(tabName.replaceAll("([\\[]|[\\]])+", ""));
		sbCMD.append(" SET ");
		for (SqlField item : updateItems) {
			if (item != null && !StringUtil.isNullOrEmpty(item.getFieldName())) {
				if (!this.getIgnoreSecurity() && !isSecurityField(item))
					continue;

				if (item.getFieldValue() == null) {
					sbFV.append(String.format("%s=NULL,", item.getFieldName()));
				}
				else {
					if (item.getSqlParamType() == SqlParamType.String 
							|| item.getSqlParamType() == SqlParamType.Text) {
						sbFV.append(String.format("%s='%s',", item.getFieldName(), 
								item.getFieldValue().replace("'", "''")));
					}
					else if (item.getSqlParamType() == SqlParamType.DateTime) {
						sbFV.append(String.format("%s=%s,", 
								item.getFieldName(), 
								formatDateTimeString(DateUtil.parseDate(item.getFieldValue()))));
					}
					else if (item.getSqlParamType() == SqlParamType.Numeric) {
						sbFV.append(String.format("%s=%s,", 
								item.getFieldName(), 
								item.getFieldValue().replace("'", "")));
					}
					else {
						sbFV.append(String.format("%s=%s,", item.getFieldName(), item.getFieldValue()));
					}
				}
			}
		}
		sbCMD.append(sbFV.deleteCharAt(sbFV.length() - 1).toString());
		sbCMD.append(" ");
		String strCondition = parseSqlCondition(p);
		
		if (!this.getIgnoreSecurity() && StringUtil.isNullOrEmpty(strCondition))
			throw new Exception("SqlCommand Security Warnning.");
		
		sbCMD.append(strCondition);
		return sbCMD.toString();
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#getDeleteCommandString(java.lang.String,
	 *      jetsennet.sqlclient.SqlCondition[])
	 */
	public String getDeleteCommandString(String tabName, SqlCondition... p) throws Exception {
		StringBuilder sbCMD = new StringBuilder(400);
		sbCMD.append("DELETE FROM ");
		sbCMD.append(tabName.replaceAll("([\\[]|[\\]])+", ""));
		sbCMD.append(" ");
		String strCondition = parseSqlCondition(p);
		
		if (!this.getIgnoreSecurity() && StringUtil.isNullOrEmpty(strCondition))
			throw new Exception("SqlCommand Security Warnning.");
		
		sbCMD.append(strCondition);
		
		return sbCMD.toString();
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#getSelectCommandString(java.lang.String,
	 *      int, boolean, java.lang.String, java.lang.String, java.lang.String,
	 *      jetsennet.sqlclient.SqlCondition[])
	 */
	public String getSelectCommandString(String tabName, int topRow, boolean isDistinct, String fields, String groupFields, String order, SqlCondition... p) {
		StringBuilder sbCMD = new StringBuilder(400);
		sbCMD.append("SELECT ");
		if (topRow > 0) {
			// sbCMD.Append("top " + topRow.ToString());
			// sbCMD.Append(" ");
		}
		
		// if (isDistinct)
		// sbCMD.Append("distinct ");
		
		if (fields == null || fields.length() == 0)
			sbCMD.append(" * ");
		else
			sbCMD.append(fields);
		sbCMD.append(" FROM ");
		sbCMD.append(tabName);
		sbCMD.append(" ");

		if (p != null && p.length > 0) {
			sbCMD.append(parseSqlCondition(p));
			sbCMD.append(" ");
		}

		if (groupFields != null && groupFields.length() > 0) {
			sbCMD.append("GROUP BY " + groupFields);
			sbCMD.append(" ");
		}

		if (order != null) {
			sbCMD.append(order);
			sbCMD.append(" ");
		}
		return sbCMD.toString();
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#getSelectCommandString(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      jetsennet.sqlclient.SqlCondition[])
	 */
	public String getSelectCommandString(String tabName, String fields, String order, SqlCondition... p) {
		return getSelectCommandString(tabName, 0, false, fields, null, order, p);
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#formatDateTimeString(java.util.Date)
	 */
	public String formatDateTimeString(Date datetime) {
		return "'" + DateUtil.formatDateString(datetime, "yyyy-MM-dd HH:mm:ss") + "'";
	}

	/**
	 * @see jetsennet.sqlclient.ISqlParser#getSelectCommandString(jetsennet.sqlclient.SqlQuery)
	 */
	public String getSelectCommandString(SqlQuery sqlQuery) {
		String commandText = getSelectCommandString(
				this.parseQueryTable(sqlQuery.queryTable), 
				sqlQuery.topRows, 
				sqlQuery.isDistinct, 
				StringUtil.isNullOrEmpty(sqlQuery.resultFields) ? null : sqlQuery.resultFields, 
				sqlQuery.groupFields, 
				sqlQuery.orderString, 
				sqlQuery.conditions);

		if (sqlQuery.unionQuery != null) {
			commandText += sqlQuery.unionQuery.unionType == QueryUnionType.Union ? " UNION " : " UNION ALL ";
			commandText += getSelectCommandString(sqlQuery.unionQuery.sqlQuery);
		}
		return commandText;
	}

	/**
	 * 组合查询表
	 * 
	 * @see jetsennet.sqlclient.ISqlParser#parseQueryTable(jetsennet.sqlclient.QueryTable)
	 */
	public String parseQueryTable(QueryTable tableInfo) {
		if (tableInfo == null || StringUtil.isNullOrEmpty(tableInfo.tableName))
			return "";

		StringBuilder sbTable = new StringBuilder();
		// sbTable.append(tableInfo.tableName);

		String tableName = tableInfo.tableName;
		if (tableName.startsWith("<")) {
			sbTable.append("(");
			tableName = getSelectCommandString(SerializerUtil.deserialize(SqlQuery.class, tableName));
			sbTable.append(tableName);
			sbTable.append(")");
		}
		else {
			sbTable.append(tableName);
		}

		if (!StringUtil.isNullOrEmpty(tableInfo.aliasName)) {
			sbTable.append(" ");
			sbTable.append(tableInfo.aliasName);
		}

		if (tableInfo.joinTables != null && tableInfo.joinTables.size() > 0) {
			for (JoinTable item : tableInfo.joinTables) {
				sbTable.append(getJoinTable(item));
			}
		}
		return sbTable.toString();
	}

	private String getJoinTable(JoinTable item) {
		if (item == null || item.queryTable == null)
			return "";

		StringBuilder sbTable = new StringBuilder();
		QueryTable table = item.queryTable;
		
		if (!StringUtil.isNullOrEmpty(table.tableName)) {
			switch (item.joinType) {
			case Inner:
				sbTable.append(" INNER JOIN ");
				break;
			case Left:
				sbTable.append(" LEFT JOIN ");
				break;
			case Right:
				sbTable.append(" RIGHT JOIN ");
				break;
			case All:
				sbTable.append(" JOIN ");
				break;
			}

			String tableName = table.tableName;
			if (tableName.startsWith("<")) {
				sbTable.append("(");
				tableName = getSelectCommandString(SerializerUtil.deserialize(SqlQuery.class, tableName));
				sbTable.append(tableName);
				sbTable.append(")");
			}
			else {
				sbTable.append(tableName);
			}

			if (!StringUtil.isNullOrEmpty(table.aliasName)) {
				sbTable.append(" ");
				sbTable.append(table.aliasName);
			}
			sbTable.append(" ON ");

			if (item.joinCondition.indexOf("'") >= 0 || item.joinCondition.indexOf(";") >= 0)
			{
				SqlCondition sqlCon = null;
				try {
					sqlCon = this.parseSqlCondition(item.joinCondition);
				}
				catch (Exception ex) {
				}
				sbTable.append(this.parseSqlCondition(null, false, sqlCon));
			}
			else {
				sbTable.append(item.joinCondition);
			}

			if (table.joinTables != null && table.joinTables.size() > 0) {
				for (JoinTable subItem : table.joinTables) {
					sbTable.append(getJoinTable(subItem));
				}
			}
		}
		return sbTable.toString();
	}
	
	/**
	 * 格式化SQL命令
	 * @see com.jetsennet.sqlclient.ISqlParser#formatCommand(java.lang.String,
	 *      jetsennet.sqlclient.SqlValue[])
	 * @return sql command
	 */
	@SuppressWarnings("unchecked")
	public String formatCommand(String command, SqlValue... sqlValues) {
		if (sqlValues == null || sqlValues.length == 0)
			return command;

		Object[] tempArr = new String[sqlValues.length];

		for (int i = 0; i < sqlValues.length; i++) {
			SqlValue item = sqlValues[i];
			if (item == null)
				continue;
			
			if (item.getValue() == null)
			{
				if (item.getSqlType() == SqlParamType.String)
				{
					item.setValue("");
				}
				else
				{
					continue;
				}
			}

			if (item.getSqlType() == SqlParamType.String || item.getSqlType() == SqlParamType.Text) {
				tempArr[i] = String.format("'%s'", item.getValue().replace("'", "''"));
			}
			else if (item.getSqlType() == SqlParamType.DateTime) {
				tempArr[i] = formatDateTimeString(DateUtil.parseDate(item.getValue()));
			}
			else if (item.getSqlType() == SqlParamType.Numeric) {
				tempArr[i] = item.getValue().replace("'", "").replace(";", "");
			}
			else {
				tempArr[i] = item.getValue();
			}
		}

		return String.format(command, tempArr);
	}

	public String getSqlFunction(SqlFunction function) {
		switch (function) {
		case ToUpper:
			return "UPPER";
		case ToLower:
			return "LOWER";
		}
		return null;
	}
}