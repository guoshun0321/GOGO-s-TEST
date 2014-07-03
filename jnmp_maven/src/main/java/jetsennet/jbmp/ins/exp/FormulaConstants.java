/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 公式常用变量
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.exp;

/**
 * 公式常用变量
 * @author 郭祥
 */
public class FormulaConstants
{

    /**
     * 公式头
     */
    public static final String HEAD = "(dif|exp|str)";
    public static final int FORMULA_HEAD = 0;
    public static TerminalExpression EXP_HEAD = new TerminalExpression(new FormulaTokenizer(HEAD, FORMULA_HEAD));
    /**
     * 冒号
     */
    public static final String COLON = ":";
    public static final int FORMULA_COLON = 1;
    public static TerminalExpression EXP_COLON = new TerminalExpression(new FormulaTokenizer(COLON, FORMULA_COLON));
    /**
     * 左括号
     */
    public static final String LBRACKET = "\\(";
    public static final int FORMULA_LBRACKET = 2;
    public static TerminalExpression EXP_LBRACKET = new TerminalExpression(new FormulaTokenizer(LBRACKET, FORMULA_LBRACKET));
    /**
     * 右括号
     */
    public static final String RBRACKET = "\\)";
    public static final int FORMULA_RBRACKET = 3;
    public static TerminalExpression EXP_RBRACKET = new TerminalExpression(new FormulaTokenizer(RBRACKET, FORMULA_RBRACKET));
    /**
     * 操作符
     */
    public static final String OP = "(\\+|\\-|\\*|/)";
    public static final int FORMULA_OP = 4;
    public static TerminalExpression EXP_OP = new TerminalExpression(new FormulaTokenizer(OP, FORMULA_OP));
    /**
     * 数字
     */
    public static final String NUM = "(\\d)+";
    public static final int FORMULA_NUM = 5;
    public static TerminalExpression EXP_NUM = new TerminalExpression(new FormulaTokenizer(NUM, FORMULA_NUM));
    /**
     * 标识OID名称，实例化前
     */
    public static final String OID = "[a-zA-Z\\d]+";
    public static final int FORMULA_OID = 6;
    public static TerminalExpression EXP_OID = new TerminalExpression(new FormulaTokenizer(OID, FORMULA_OID));
    /**
     * 文本表达式，实例化前
     */
    public static final String STR = "\".*?\"";
    public static final int FORMULA_STR = 7;
    public static TerminalExpression EXP_STR = new TerminalExpression(new FormulaTokenizer(STR, FORMULA_STR));
    /**
     * OID，实例化后
     */
    public static final String OID_INS = "OID:\\(((\\d)+\\.)+(\\d)+\\)";
    public static final int FORMULA_OID_INS = 8;
    public static TerminalExpression EXP_OID_INS = new TerminalExpression(new FormulaTokenizer(OID_INS, FORMULA_OID_INS));
    /**
     * 名称头部
     */
    public static final String HEAD_NAME = "name";
    public static final int FORMULA_HEAD_NAME = 9;
    public static TerminalExpression EXP_HEAD_NAME = new TerminalExpression(new FormulaTokenizer(HEAD_NAME, FORMULA_HEAD_NAME));
    /**
     * 分号
     */
    public static final String SEMICOLON = ";";
    public static final int FORMULA_SEMICOLON = 10;
    public static TerminalExpression EXP_SEMICOLON = new TerminalExpression(new FormulaTokenizer(SEMICOLON, FORMULA_SEMICOLON));
}
