/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * SQL Server解析
 * @author 李小敏
 */
public class SqlServerParser extends SqlParser {

	@Override
	public String getSelectCommandString(String tabName, int topRow, boolean isDistinct, String fields, String groupFields, String order, SqlCondition... p) {
		StringBuilder sbCMD = new StringBuilder();
		sbCMD.append("SELECT ");
		if (isDistinct)
			sbCMD.append("distinct ");
		if (topRow > 0) {
			sbCMD.append("TOP " + String.valueOf(topRow));
			sbCMD.append(" ");
		}

		if (fields == null || fields.length() == 0)
			sbCMD.append(" * ");
		else {
			sbCMD.append(fields);
		}
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
}