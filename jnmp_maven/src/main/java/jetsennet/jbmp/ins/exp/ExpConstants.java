/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 表达式常量
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.exp;

/**
 * 表达式常量
 * @author 郭祥
 */
public class ExpConstants
{

    // SNMP公式解析的正则表达式
    // 1.基本元素
    /**
     * 单词
     */
    public static final String WORD = "[a-zA-Z]+";
    /**
     * 操作
     */
    public static final String OP = "[\\+\\-\\*/]";
    /**
     * 小括号
     */
    public static final String PH = "[\\(\\)]";
    /**
     * OID
     */
    public static final String OID_HEAD = "OID";
    public static final String OID = OID_HEAD + ":" + "\\(([\\d\\.]+)\\)";
    /**
     * OID的名称
     */
    public static final String EXP_BASIC_COMMEN = WORD;
    /**
     * 字符串
     */
    public static final String EXP_BASIC_TXT = "\"[^\\)]+\"";
    /**
     * 文本表达式
     */
    public static final String HEAD_STR = "str";
    public static final String EXP_STR = HEAD_STR + ":\\(((" + EXP_BASIC_COMMEN + ")|(" + EXP_BASIC_TXT + "))+\\)";
    /**
     * 计算表达式
     */
    public static final String HEAD_CAL = "exp";
    public static final String EXP_CAL = HEAD_CAL + ":\\(((" + EXP_BASIC_COMMEN + ")|(" + OP + ")|(" + PH + ")|(\\d))+\\)";
    /**
     * 差分表达式
     */
    public static final String HEAD_DIF = "dif";
    public static final String EXP_DIF = HEAD_DIF + ":\\(((" + EXP_BASIC_COMMEN + ")|(" + OP + ")|(" + PH + ")|(\\d))+\\)";
    /**
     * 名称表达式
     */
    public static final String HEAD_NAME = "name";
    public static final String EXP_NAME = HEAD_NAME + ":\\(((" + EXP_BASIC_COMMEN + ")|(" + EXP_BASIC_TXT + "))+\\)";
    /**
     * 条件表达式
     */
    public static final String HEAD_CONDITION = "con";
    public static final String EXP_CONDITION = HEAD_CONDITION + ":\\(" + "[^\\)]+" + "\\)";
    /**
     * 表达式第一部分
     */
    public static final String EXP_WHOLECAL = "((" + EXP_STR + ")|(" + EXP_CAL + ")|(" + EXP_DIF + "))";
    /**
     * 完整表达式
     */
    public static final String SNMP_TOTAL = "^(" + EXP_WHOLECAL + "(;(" + EXP_NAME + ")){0,1})$";

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        System.out.println(ExpConstants.SNMP_TOTAL);
    }
}
