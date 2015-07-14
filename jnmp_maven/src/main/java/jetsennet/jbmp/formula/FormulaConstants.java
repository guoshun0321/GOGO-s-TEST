/************************************************************************
日 期：2011-12-05
作 者: 郭祥
版 本：v1.3
描 述: TOKEN和上下文无关文法元素的编号
历 史：
 ************************************************************************/
package jetsennet.jbmp.formula;

/**
 * TOKEN和上下文无关文法元素的编号
 * @author 郭祥
 */
public class FormulaConstants
{

    // TOKEN的编号
    public static final int EXP_HEAD = 1001;
    public static final int DIF_HEAD = 1002;
    public static final int STR_HEAD = 1003;
    public static final int NAME_HEAD = 1004;
    public static final int OID_HEAD = 1005;
    public static final int COLON = 1006;
    public static final int LEFT_PAREN = 1007;
    public static final int RIGHT_PAREN = 1008;
    public static final int SEMI_COLON = 1009;
    public static final int COMMA = 1010;
    public static final int ADDOP = 1011;
    public static final int MULOP = 1012;

    public static final int STR = 1013;
    public static final int OID = 1014;
    public static final int NUM = 1015;

    public static final int STR_STRING = 1102;

    // 上下文无关文法元素的编号
    public static final int S = 2001;
    public static final int R = 2002;
    public static final int N = 2003;

    public static final int EXP = 2004;
    public static final int EXP1 = 2005;
    public static final int TERM = 2006;
    public static final int TERM1 = 2007;
    public static final int FACTOR = 2008;
    public static final int FUNC_STRING = 2009;
    public static final int FUNC_LIST = 2010;
    public static final int FUNC_ELE = 2011;
    public static final int FUNC_ELE1 = 2012;

    public static final int SEXP = 2013;
    public static final int SEXP_TERM = 2014;

    public static final int OID_STRING = 2015;
    public static final int IDENTIFIER_STRING = 2016;
    public static final int FUNC_NAME = 2017;
}
