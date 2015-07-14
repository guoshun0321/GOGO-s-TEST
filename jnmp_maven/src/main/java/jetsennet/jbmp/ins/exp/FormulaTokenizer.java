/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 终结符表达式
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.exp;

/**
 * 终结符表达式
 * @author 郭祥
 */
public class FormulaTokenizer
{

    /**
     * 标识一个token的正则表达式
     */
    private String regex;
    /**
     * 类型
     */
    private int type;

    /**
     * @param regex 参数
     * @param type 参数
     */
    public FormulaTokenizer(String regex, int type)
    {
        this.regex = regex;
        this.type = type;
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the regex
     */
    public String getRegex()
    {
        return regex;
    }

    /**
     * @param regex the regex to set
     */
    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }
    // </editor-fold>
}
