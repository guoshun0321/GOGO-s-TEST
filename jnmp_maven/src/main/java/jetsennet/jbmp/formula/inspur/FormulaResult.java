package jetsennet.jbmp.formula.inspur;

/**
 * 处理结果
 * @author GoGo
 */
public class FormulaResult
{
    /**
     * 计算部分头
     */
    private String expHead;
    /**
     * 计算部分公式
     */
    private String expStr;
    /**
     * 计算部分结果
     */
    private Object expRst;
    /**
     * 名称部分头
     */
    private String nameHead;
    /**
     * 名称部分公式
     */
    private String nameStr;
    /**
     * 名称部分结果
     */
    private String nameRst;

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("计算部分：").append(expHead).append(",").append(expStr).append(",").append(expRst).append("\n");
        sb.append("名称部分：").append(nameHead).append(",").append(nameStr).append(",").append(nameRst);
        return sb.toString();
    }

    public String getExpHead()
    {
        return expHead;
    }

    public void setExpHead(String expHead)
    {
        this.expHead = expHead;
    }

    public String getExpStr()
    {
        return expStr;
    }

    public void setExpStr(String expStr)
    {
        this.expStr = expStr;
    }

    public Object getExpRst()
    {
        return expRst;
    }

    public void setExpRst(Object expRst)
    {
        this.expRst = expRst;
    }

    public String getNameHead()
    {
        return nameHead;
    }

    public void setNameHead(String nameHead)
    {
        this.nameHead = nameHead;
    }

    public String getNameStr()
    {
        return nameStr;
    }

    public void setNameStr(String nameStr)
    {
        this.nameStr = nameStr;
    }

    public String getNameRst()
    {
        return nameRst;
    }

    public void setNameRst(String nameRst)
    {
        this.nameRst = nameRst;
    }

}
