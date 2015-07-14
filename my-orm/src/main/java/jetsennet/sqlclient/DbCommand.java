/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.util.*;

import jetsennet.util.StringUtil;

/**
 * SQL命令
 * @author 李小敏
 */
public class DbCommand {
	
    /**
     * SQL命令
     * @param sqlParser
     */
    public DbCommand(ISqlParser sqlParser)
    {
        this.sqlParser = sqlParser;
    }
   
    public DbCommand(ISqlParser sqlParser, DbCommandType commandType)
    {
        this.sqlParser = sqlParser;
        this.commandType = commandType;
    }
    
    private ISqlParser sqlParser;
    private String tableName;
    private String orderString;
    private DbCommandType commandType = DbCommandType.SelectCommand;
    private List<SqlField> sqlFields = new ArrayList<SqlField>();
    private SqlCondition[] sqlCondition;
    
    public ISqlParser getSqlParser()
    {
        return sqlParser;
    }
    public void setSqlParser(ISqlParser value)
    {
    	this.sqlParser = value;
    }    
   
    public String getTableName()
    {
        return tableName;
    }
    public void setTableName(String value)
    {
    	this.tableName = value;
    }    
   
    public String getOrderString()
    {
       return orderString;
    }
    public void setOrderString(String value)
    {
    	this.orderString = value;
    }    
    
    public DbCommandType getCommandType()
    {
        return commandType;
    }
    public void setCommandType(DbCommandType value)
    {
    	this.commandType  = value;
    }
    
    public List<SqlField> getSqlFields()
    {
        return sqlFields;
    }
    public void setSqlFields(List<SqlField> value)
    {
    	this.sqlFields  = value;
    }    
   
    public SqlCondition[] getSqlConditions()
    {
        return sqlCondition;
    }
    public void setSqlConditions(SqlCondition[] value)
    {
    	this.sqlCondition  = value;
    }
    
    public void addField(SqlField field)
    {
        this.sqlFields.add(field);
    }
   
    public void addField(String fieldName,Object fieldValue,SqlParamType fieldType)
    {
        this.sqlFields.add(new SqlField(fieldName,fieldValue,fieldType));
    }
  
    public void addField(String fieldName, String fieldValue)
    {
        this.sqlFields.add(new SqlField(fieldName, fieldValue, SqlParamType.String));
    }
   
    public void addField(String fieldName, int fieldValue)
    {
        this.sqlFields.add(new SqlField(fieldName, fieldValue, SqlParamType.Numeric));
    }
   
    public void addField(String fieldName, Date fieldValue)
    {
        this.sqlFields.add(new SqlField(fieldName, fieldValue, SqlParamType.DateTime));
    }            
   
    public void setFilter(SqlCondition... condition)
    {
        this.sqlCondition = condition;
    }
   
    /**
     * 取得命令字符串
     * @return
     * @throws Exception
     */
    public String getCommandString() throws Exception
    {
        if(StringUtil.isNullOrEmpty(tableName))
        {
            return "";
        }
        switch (this.commandType)
        {
            case SelectCommand:

                String[] fieldArr = null;
                if (this.sqlFields.size() > 0)
                {
                    fieldArr = new String[this.sqlFields.size()];
                    for (int i = 0; i < this.sqlFields.size(); i++)
                        fieldArr[i] = this.sqlFields.get(i).getFieldName();
                }
                
                return this.sqlParser.getSelectCommandString(this.tableName, StringUtil.join(fieldArr,","), this.orderString, this.sqlCondition);
            case InsertCommand:
                return this.sqlParser.getInsertCommandString(this.tableName, this.sqlFields);
            case UpdateCommand:
                return this.sqlParser.getUpdateCommandString(this.tableName, this.sqlFields, this.sqlCondition);
            case DeleteCommand:
                return this.sqlParser.getDeleteCommandString(this.tableName, this.sqlCondition);
        }
        return null;
    }
    
    /**
     *  取得命令字符串
     */
    public String toString()
    {
    	try
    	{
    		return this.getCommandString();
    	}
    	catch(Exception ex)
    	{
    		return "";
    	}    	
    }
}