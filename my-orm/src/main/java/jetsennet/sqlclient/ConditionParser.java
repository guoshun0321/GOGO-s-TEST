/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
 ************************************************************************/
package jetsennet.sqlclient;

import jetsennet.util.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sql条件解析
 * @author 李小敏
 */
public class ConditionParser extends CommandParser
{

	final String REGEX_COMPARE_TYPE = "(=|>=|<=|<>|>|<|like|in|contains\\s+in|not\\s+in|not\\s+like|is\\s+|is\\s+not)";
	final String REGEX_FIELD_NAME = "([^\\s'=><]+)";
	final String REGEX_JOIN = "(\\s+(and|or)+)";
	final String REGEX_FIELD_VALUE = "n?([^\\s=]+)";
	final String REGEX_SINGLE = "(\\[_\\d{1,4}\\])";
	final String REGEX_BETWEEN = "(between\\s+n?([^\\s=]+)\\s+and\\s+n?([^\\s=]+))";
	final String REGEX_EXISTS = "((exists|not\\s+exists)\\s*(\\[_\\d{1,4}\\]))";
	final String REGEX_COMPARE_OPERATION = REGEX_COMPARE_TYPE + "\\s*" + REGEX_FIELD_VALUE;
	final String REGEX_ALL = "(" + REGEX_SINGLE + "|(" + REGEX_FIELD_NAME + "\\s*(" + REGEX_BETWEEN + "|(" + REGEX_COMPARE_OPERATION + ")))|("
			+ REGEX_EXISTS + "))(" + REGEX_JOIN + "|(\\s*$))";

	private SqlCondition sqlCondtion;

	/**
	 * 条件解析,条件字串对象化
	 * 
	 * @param commandString
	 * @return
	 * @throws Exception
	 */
	public SqlCondition parseCondition(String commandString) throws Exception
	{
		if (StringUtil.isNullOrEmpty(commandString))
			return null;
		this.matchCount = 0;
		this.stringFields = new HashMap<String, String>();
		this.sqlCondtion = new SqlCondition();
		this.commandString = commandString;
		this.originalString = commandString;

		this.replaceStringField();
		this.replaceBrackets();
		this.parseUnit(this.commandString);
		return this.sqlCondtion;
	}

	/**
	 * 解析条件
	 * 
	 * @param commandString
	 * @throws Exception
	 */
	private void parseUnit(String commandString) throws Exception
	{
		Pattern pattern = Pattern.compile(REGEX_ALL, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(commandString);
		StringBuffer sb = new StringBuffer();

		while (m.find())
		{
			String singlesyntax = StringUtil.trim(m.group(2));// "singlesyntax"
			String exists = StringUtil.trim(m.group(15));// "exists"
			String join = StringUtil.trim(m.group(18));// "join"

			if (singlesyntax != null && singlesyntax.length() > 0)
			{

				SqlCondition tempc = sqlCondtion;
				SqlCondition c = new SqlCondition();
				c.setSqlLogicType(getLogicType(join));
				sqlCondtion.getSqlConditions().add(c);
				sqlCondtion = c;
				String svalue = getStringFieldValue(singlesyntax);
				svalue = StringUtil.trimEnd(StringUtil.trimStart(svalue, '('), ')');

				this.parseUnit(svalue);
				sqlCondtion = tempc;
			}
			else if (exists != null && exists.length() > 0)
			{
				String _existstype = StringUtil.trim(m.group(14));// "existstype";
				String evalue = getStringFieldValue(exists);
				evalue = StringUtil.trimEnd(StringUtil.trimStart(evalue, '('), ')');

				SqlCondition c = new SqlCondition();
				c.setSqlParamType(SqlParamType.UnKnow);
				c.setSqlLogicType(getLogicType(join));
				c.setSqlRelationType(getRelationType(_existstype));
				c.setIsSecurity(true);
				c.setParamValue(replaceAllStringFieldValue(evalue, true));
				sqlCondtion.getSqlConditions().add(c);
			}
			else
			{
				String fieldName = replaceAllStringFieldValue(m.group(4), true);// "fieldname"
				String between = m.group(6);// m.Groups["between"].Value;

				if (between != null && between.length() > 0)
				{
					String value1 = StringUtil.trim(m.group(7));// m.Groups["value1"].Value.Trim();
					String value2 = StringUtil.trim(m.group(8));// m.Groups["value2"].Value.Trim();
					if (value1.startsWith("[") || value2.startsWith("["))
					{
						if (value1.startsWith("["))
							value1 = getStringFieldValue(value1);
						if (value2.startsWith("["))
							value2 = getStringFieldValue(value2);
						sqlCondtion.getSqlConditions().add(
								new SqlCondition(fieldName, value1 + "," + value2, getLogicType(join), SqlRelationType.Between, SqlParamType.String,
										true));
					}
					else
					{
						sqlCondtion.getSqlConditions().add(
								new SqlCondition(fieldName, value1 + "," + value2, getLogicType(join), SqlRelationType.Between, SqlParamType.UnKnow,
										true));
					}
				}
				else
				{
					String compareType = m.group(10);// m.Groups["comparetype"].Value;
					SqlRelationType relation = getRelationType(compareType);
					String cvalue = StringUtil.trim(m.group(11));// m.Groups["value"].Value.Trim();

					cvalue = replaceAllStringFieldValue(cvalue, cvalue.startsWith("[") ? false : true);
					if (relation == SqlRelationType.Like || relation == SqlRelationType.NotLike || relation == SqlRelationType.CustomLike)
					{

						if (relation == SqlRelationType.Like || relation == SqlRelationType.NotLike)
						{
							cvalue = StringUtil.trimEnd(StringUtil.trimStart(cvalue, '%'), '%');
						}
						sqlCondtion.getSqlConditions().add(
								new SqlCondition(fieldName, cvalue, getLogicType(join), relation, SqlParamType.String, true));
					}
					else
					{
						if (this.originalString.indexOf("'" + cvalue.replace("'", "''") + "'") >= 0)
						{
							sqlCondtion.getSqlConditions().add(
									new SqlCondition(fieldName, cvalue, getLogicType(join), relation, SqlParamType.String, true));
						}
						else
						{
							sqlCondtion.getSqlConditions().add(
									new SqlCondition(fieldName, cvalue, getLogicType(join), relation, SqlParamType.UnKnow, true));
						}
					}
				}
			}

			m.appendReplacement(sb, "");
		}

		m.appendTail(sb);
		commandString = sb.toString().trim();

		if (commandString.length() > 0)
			throw new Exception("无效的条件表达式,错误出现在：" + replaceAllStringFieldValue(commandString, true));
	}

	/**
	 * 取得关系条件类型
	 * 
	 * @param typeStr
	 * @return
	 */
	private SqlRelationType getRelationType(String typeStr)
	{

		if (typeStr == null)
			return SqlRelationType.Custom;

		typeStr = StringUtil.join(typeStr.split(" "), " ").toLowerCase().trim();

		if (typeStr.equals("="))
			return SqlRelationType.Equal;
		if (typeStr.equals("in"))
			return SqlRelationType.In;
		if (typeStr.equals("<"))
			return SqlRelationType.Less;
		if (typeStr.equals("<="))
			return SqlRelationType.LessEqual;
		if (typeStr.equals("like"))
			return SqlRelationType.CustomLike;
		if (typeStr.equals("not like"))
			return SqlRelationType.NotLike;
		if (typeStr.equals("<>"))
			return SqlRelationType.NotEqual;
		if (typeStr.equals("not in"))
			return SqlRelationType.NotIn;
		if (typeStr.equals(">"))
			return SqlRelationType.Than;
		if (typeStr.equals(">="))
			return SqlRelationType.ThanEqual;
		if (typeStr.equals("is"))
			return SqlRelationType.IsNull;
		if (typeStr.equals("is not"))
			return SqlRelationType.IsNotNull;
		if (typeStr.equals("exists"))
			return SqlRelationType.Exists;
		if (typeStr.equals("not exists"))
			return SqlRelationType.NotExists;

		return SqlRelationType.Custom;

	}

	/**
	 * 取得逻辑条件类型
	 * 
	 * @param typeStr
	 * @return
	 */
	private SqlLogicType getLogicType(String typeStr)
	{

		if (typeStr == null)
			return SqlLogicType.And;

		typeStr = typeStr.toLowerCase().trim();

		if (typeStr.equals("and"))
			return SqlLogicType.And;
		if (typeStr.equals("or"))
			return SqlLogicType.Or;

		return SqlLogicType.And;

	}
}