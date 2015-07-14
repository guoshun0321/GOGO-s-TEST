/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.util.*;

import jetsennet.util.*;

/**
 * 表达式解析
 * @author 李小敏
 */
public class ConditionOperation {
	
    /**
     * 条件解析前事件(用于在解析条件之前进处的一些处理)
     */
    public IConditionOperationBefore operationBefore;
    public IConditionOperationBefore getOperationBefore()
    {
    	return operationBefore;
    }
    public void setOperationBefore(IConditionOperationBefore value)
    {
    	operationBefore = value;
    }

    
    /**条件表达式运算
     * @param param
     * @return
     */
    public boolean parseConditionParams(SqlCondition... param) throws Exception
    {
        if (param == null || param.length == 0)
            return false;

        boolean bCurrentResult = false;            

        for (int i = 0; i < param.length; i++)
        {
        	SqlCondition paramItem = param[i];
            if (paramItem == null)
                continue;
            if (paramItem.getSqlConditions().size() > 0)
            {
                SqlCondition[] subParam = new SqlCondition[paramItem.getSqlConditions().size()];
                for (int j = 0; j < subParam.length; j++)
                    subParam[j] = paramItem.getSqlConditions().get(j);

                bCurrentResult = parseConditionParams(subParam);                    
            }
            else
            {
                bCurrentResult = getConditionResult(paramItem);                   
            }
          
            switch (paramItem.getSqlLogicType())
            {
                case And:
                case AndAll:
                    if (bCurrentResult == false)
                        return false;                        
                    break;
                case Or:
                case OrAll:
                    if (bCurrentResult == true)
                        return true;  
                    break;
            }               
        }

        return bCurrentResult;
    }

    /**取得二元运算结果
     * @param p
     * @return
     */
    private boolean getConditionResult(SqlCondition p)  throws Exception
    {
        if (p == null || StringUtil.isNullOrEmpty(p.getParamName()))
            return false;

        if (this.operationBefore != null)
        	operationBefore.operationBefore(p);
       
        String pValue = p.getParamValue();
        if (p.getParamValue() == null)
        {
            return false;
        }
        
        if (p.getSqlParamType() == SqlParamType.DateTime)
        {                
            return compareDateTime(p.getSqlRelationType(), p.getParamName(), pValue);
        }
        else if (p.getSqlParamType() == SqlParamType.Numeric || p.getSqlParamType() == SqlParamType.Boolean)
        {
        	pValue = p.getParamValue();
            return compareNumeric(p.getSqlRelationType(), p.getParamName(), pValue);
        }
        else
        {
        	pValue = p.getParamValue();
            return compareString(p.getSqlRelationType(), p.getParamName(), pValue);
        }
    }
       
    /**比较数字
     * @param srType
     * @param val1
     * @param val2
     * @return
     */
    private boolean compareNumeric(SqlRelationType srType, String val1, String val2)
    {
        double dVal1 = Double.parseDouble(val1);
        switch (srType)
        {
            case Equal:
                return dVal1 == Double.parseDouble(val2);
            case Than:
                return dVal1 > Double.parseDouble(val2);
            case Less:
                return dVal1 < Double.parseDouble(val2);
            case ThanEqual:
                return dVal1 >= Double.parseDouble(val2);
            case LessEqual:
                return dVal1 <= Double.parseDouble(val2);
            case NotEqual:
                return dVal1 != Double.parseDouble(val2);
            case Like:               
            case NotLike:
            case CustomLike:
                return false;
            case In:
            case NotIn:
                if (!StringUtil.isNullOrEmpty(val2))
                {                       
                    String[] pv = val2.split(",");
                    for (int i = 0; i < pv.length; i++)
                    {
                        if (dVal1 == Double.parseDouble(pv[i]))
                        {
                            if (srType == SqlRelationType.In)
                                return true;
                            else
                                return false;
                        }
                    }
                }
                if (srType == SqlRelationType.In)
                    return false;
                else
                    return true;
            case Between:
                String[] pvBetween = val2.split(",");
                return (dVal1 >= Double.parseDouble(pvBetween[0]) && dVal1 <= Double.parseDouble(pvBetween[1]));                   
            case Custom:
                return false;
        }
        return false;
    }
    
    /**比较字符
     * @param srType
     * @param val1
     * @param val2
     * @return
     */
    private boolean compareString(SqlRelationType srType, String val1, String val2)
    {           
        switch (srType)
        {
            case Equal:
                return val1.compareTo(val2) == 0;
            case Than:
                return val1.compareTo(val2) > 0;
            case Less:
                return val1.compareTo(val2) < 0;
            case ThanEqual:
                return val1.compareTo(val2) >= 0;
            case LessEqual:
                return val1.compareTo(val2) <= 0;
            case NotEqual:
                return val1.compareTo(val2) != 0;
            case Like:
            case CustomLike:
                val2 = StringUtil.trimStart(StringUtil.trimEnd(val2,'%'),'%');
                return val1.indexOf(val2) >= 0;
            case NotLike:
                val2 =StringUtil.trimStart(StringUtil.trimEnd(val2,'%'),'%');
                return val1.indexOf(val2) < 0;
            case In:
            case NotIn:
                if (!StringUtil.isNullOrEmpty(val2))
                {
                	String[] pv = val2.split(",");
                    for (int i = 0; i < pv.length; i++)
                    {
                        if (val1.compareTo(pv[i]) == 0)
                        {
                            if (srType == SqlRelationType.In)
                                return true;
                            else
                                return false;
                        }
                    }
                }
                if (srType == SqlRelationType.In)
                    return false;
                else
                    return true;
            case Between:
                String[] pvBetween = val2.split(",");
                return (val1.compareTo(pvBetween[0]) >= 0 && val1.compareTo(pvBetween[1])<=0);
            case Custom:
                return false;
        }
        return false;
    }
    
    /**比较时间
     * @param srType
     * @param val1
     * @param val2
     * @return
     */
    private boolean compareDateTime(SqlRelationType srType, String val1, String val2)
    {
        Date dVal1 = DateUtil.parseDate(val1);
        switch (srType)
        {
            case Equal:
                return dVal1.compareTo(DateUtil.parseDate(val2)) == 0;
            case Than:
                return dVal1.compareTo(DateUtil.parseDate(val2)) > 0;
            case Less:
                return dVal1.compareTo(DateUtil.parseDate(val2)) < 0;
            case ThanEqual:
                return dVal1.compareTo(DateUtil.parseDate(val2)) >= 0;
            case LessEqual:
                return dVal1.compareTo(DateUtil.parseDate(val2)) <= 0;
            case NotEqual:
                return dVal1.compareTo(DateUtil.parseDate(val2)) != 0;
            case Like:
            case NotLike:
            case CustomLike:
                return false;
            case In:
            case NotIn:
                if (!StringUtil.isNullOrEmpty(val2))
                {
                    String[] pv = val2.split(",");
                    for (int i = 0; i < pv.length; i++)
                    {
                        if (dVal1.compareTo(DateUtil.parseDate(pv[i])) == 0)
                        {
                            if (srType == SqlRelationType.In)
                                return true;
                            else
                                return false;
                        }
                    }
                }
                if (srType == SqlRelationType.In)
                    return false;
                else
                    return true;
            case Between:
                String[] pvBetween = val2.split(",");
                return (dVal1.compareTo(DateUtil.parseDate(pvBetween[0])) >= 0 && dVal1.compareTo(DateUtil.parseDate(pvBetween[1])) <= 0);
            case Custom:
                return false;
        }
        return false;
    }
}