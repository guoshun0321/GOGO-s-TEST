/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula;

import java.util.ArrayList;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.Token;

import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.TerminalSymbolList;

/**
 * @author GuoXiang
 */
public class FormulaElement
{

    /**
     * 子对象
     */
    private ArrayList<FormulaElement> children;
    /**
     * 节点
     */
    private Node node;
    /**
     * 字符串
     */
    private String value;
    /**
     * 包含的OID名称
     */
    private String[] oidNames;
    /**
     * 包含的OID
     */
    private String[] oids;
    /**
     * 终结符列表
     */
    private TerminalSymbolList list;
    /**
     * 用于计算的表达形式，是将语法树扁平化后的结果。主要被合并是
     */
    private CalcFormula calcFormula;

    /**
     * 添加节点，如果节点为Token类型，给value赋值
     * @param node 节点
     */
    public FormulaElement(Node node)
    {
        this.node = node;
        if (node != null && node instanceof Token)
        {
            value = ((Token) node).getImage();
        }
        oidNames = null;
        list = new TerminalSymbolList();
    }

    /**
     * @param child 节点
     */
    public void addChildren(FormulaElement child)
    {
        if (child == null)
        {
            return;
        }
        if (children == null)
        {
            children = new ArrayList<FormulaElement>();
        }
        children.add(child);
    }

    @Override
    public String toString()
    {
        return FormulaUtil.productionToString(node);
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the children
     */
    public ArrayList<FormulaElement> getChildren()
    {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ArrayList<FormulaElement> children)
    {
        this.children = children;
    }

    /**
     * @return the node
     */
    public Node getNode()
    {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(Node node)
    {
        this.node = node;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * @return the oidNames
     */
    public String[] getOidNames()
    {
        return oidNames;
    }

    /**
     * @param oidNames the oidNames to set
     */
    public void setOidNames(String[] oidNames)
    {
        this.oidNames = oidNames;
    }

    /**
     * @return the list
     */
    public TerminalSymbolList getList()
    {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(TerminalSymbolList list)
    {
        this.list = list;
    }

    /**
     * @return the oids
     */
    public String[] getOids()
    {
        return oids;
    }

    /**
     * @param oids the oids to set
     */
    public void setOids(String[] oids)
    {
        this.oids = oids;
    }

    /**
     * @return the calcFormula
     */
    public CalcFormula getCalcFormula()
    {
        return calcFormula;
    }

    /**
     * @param calcFormula the calcFormula to set
     */
    public void setCalcFormula(CalcFormula calcFormula)
    {
        this.calcFormula = calcFormula;
    }
    // </editor-fold>
}
