/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.util.Date;
import java.util.List;

import jetsennet.util.DateUtil;
import jetsennet.util.StringUtil;

/**
 * Oracle 解析接口
 * @author 李小敏
 */
public class OracleParser extends SqlParser {

	@Override
	public String getSelectCommandString(String tabName, int topRow, boolean isDistinct, String fields, String groupFields, String order, SqlCondition... p) {
		StringBuilder sbCMD = new StringBuilder();
		if (topRow > 0) {
			sbCMD.append("SELECT * FROM (SELECT TEMP_TAB.*,RowNum as RN FROM (");
		}

		sbCMD.append(" SELECT ");
		if (isDistinct)
			sbCMD.append("distinct ");
		if (fields == null || fields.length() == 0)
			sbCMD.append(" * ");
		else {
			sbCMD.append(fields);
		}

		sbCMD.append(" FROM ");
		sbCMD.append(tabName);
		sbCMD.append(" ");

		String strWhere = "";
		if (p != null && p.length > 0) {
			strWhere = parseSqlCondition(p);
		}
		if (!StringUtil.isNullOrEmpty(strWhere)) {
			sbCMD.append(strWhere);
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

		if (topRow > 0) {
			sbCMD.append(" ) TEMP_TAB)  WHERE ");
			sbCMD.append("RN <= " + String.valueOf(topRow));
		}

		return sbCMD.toString();
	}

	@Override
	public String formatDateTimeString(Date datetime) {
		return "to_date(\'" + DateUtil.formatDateString(datetime, "yyyy-MM-dd HH:mm:ss") + "\','YYYY-MM-DD HH24:MI:SS')";
	}

	@Override
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
					else if (item.getSqlParamType() == SqlParamType.String) {
						sbValue.append("'");
						sbValue.append(item.getFieldValue().replace("'", "''"));
						sbValue.append("',");
					}
					else if (item.getSqlParamType() == SqlParamType.Text) {
						sbValue.append("?,");
					}
					else if (item.getSqlParamType() == SqlParamType.DateTime) {
						sbValue.append(this.formatDateTimeString(DateUtil.parseDate(item.getFieldValue())));
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

	@Override
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
					if (item.getSqlParamType() == SqlParamType.String) {
						sbFV.append(String.format("%s='%s',", item.getFieldName(), item.getFieldValue().replace("'", "''")));
					}
					else if (item.getSqlParamType() == SqlParamType.Text) {
						sbFV.append(String.format("%s=?,", item.getFieldName()));
					}
					else if (item.getSqlParamType() == SqlParamType.DateTime) {
						sbFV.append(String.format("%s=%s,", item.getFieldName(), this.formatDateTimeString(DateUtil.parseDate(item.getFieldValue()))));
					}
					else if (item.getSqlParamType() == SqlParamType.Numeric) {
						sbFV.append(String.format("%s=%s,", item.getFieldName(), item.getFieldValue().replace("'", "")));
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
}