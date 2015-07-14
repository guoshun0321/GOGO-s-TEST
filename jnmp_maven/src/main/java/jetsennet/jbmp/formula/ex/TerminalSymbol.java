/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula.ex;

import net.percederberg.grammatica.parser.Token;

/**
 * @author GuoXiang
 */
public class TerminalSymbol
{

    /**
     * 值
     */
    private String str;
    /**
     * 节点
     */
    private Token node;
    /**
     * 类型
     */
    private int type;
    /**
     * 类型未知
     */
    public static int TYPE_UNKNOWN = -1;

    /**
     * 构造函数
     */
    public TerminalSymbol()
    {
    }

    /**
     * 构造函数
     * @param node 节点
     */
    public TerminalSymbol(Token node)
    {
        this.node = node;
        this.str = node.getImage();
        this.type = TYPE_UNKNOWN;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("<");
        sb.append(type);
        sb.append(">");
        return sb.toString();
    }

    /**
     * @return 复制
     */
    public TerminalSymbol copy()
    {
        TerminalSymbol retval = new TerminalSymbol();
        retval.str = this.str;
        retval.type = this.type;
        retval.node = this.node;
        return retval;
    }

    /**
     * @param copy 复制给对象
     */
    public void copyTo(TerminalSymbol copy)
    {
        if (copy == null)
        {
            throw new NullPointerException();
        }
        copy.str = this.str;
        copy.type = this.type;
        copy.node = this.node;
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the str
     */
    public String getStr()
    {
        return str;
    }

    /**
     * @param str the str to set
     */
    public void setStr(String str)
    {
        this.str = str;
    }

    /**
     * @return the node
     */
    public Token getNode()
    {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(Token node)
    {
        this.node = node;
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
