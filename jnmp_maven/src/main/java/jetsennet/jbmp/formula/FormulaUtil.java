/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula;

import java.util.ArrayList;
import java.util.Stack;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.CalcFormulaEntity;
import jetsennet.jbmp.formula.ex.TerminalSymbol;
import jetsennet.jbmp.formula.ex.TerminalSymbolList;
import jetsennet.jbmp.util.CommonUtil;

/**
 * 公式模块工具类
 * @author GuoXiang
 */
public class FormulaUtil
{

    /**
     * 将语法树转换成String
     * @param node 参数
     * @return 结果
     */
    public static String productionToString(Node node)
    {
        if (node == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Stack<Node> nodes = new Stack<Node>();
        nodes.push(node);
        while (!nodes.empty())
        {
            Node nd = nodes.pop();
            if (nd instanceof Token)
            {
                sb.append(((Token) nd).getImage());
            }
            else
            {
                for (int i = nd.getChildCount() - 1; i >= 0; i--)
                {
                    nodes.push(nd.getChildAt(i));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 从语法树中获取指定类型的节点
     * @param root 节点
     * @param type类型
     * @return 结果
     */
    public static TerminalSymbolList toTerminalList(FormulaElement root)
    {
        TerminalSymbolList retval = new TerminalSymbolList();
        Stack<FormulaElement> fes = new Stack<FormulaElement>();
        fes.add(root);
        while (!fes.isEmpty())
        {
            FormulaElement fe = fes.pop();
            if (fe.getValue() != null)
            {
                TerminalSymbol ts = new TerminalSymbol();
                ts.setStr(fe.getValue());
                ts.setType(fe.getNode().getId());
                ts.setNode((Token) fe.getNode());
                retval.addSymbol(ts);
            }
            else
            {
                ArrayList<FormulaElement> children = fe.getChildren();
                for (int i = children.size() - 1; i >= 0; i--)
                {
                    fes.push(children.get(i));
                }
            }
        }
        return retval;
    }

    /**
     * 根据给定的类型，将语法树扁平化
     * @param root 节点
     * @param types 类型
     * @return 结果
     */
    public static CalcFormula flat(Node root, int[] types)
    {
        CalcFormula retval = new CalcFormula();
        Stack<Node> fes = new Stack<Node>();
        fes.push(root);
        while (!fes.empty())
        {
            Node node = fes.pop();
            if (node instanceof Token)
            {
                Token token = (Token) node;
                CalcFormulaEntity ce = new CalcFormulaEntity(token.getImage(), node.getId(), node);
                retval.addEntity(ce);
            }
            if (node instanceof Production)
            {
                Production pd = (Production) node;
                if (CommonUtil.containInt(types, pd.getId()))
                {
                    CalcFormulaEntity ce = new CalcFormulaEntity(FormulaUtil.productionToString(node), node.getId(), node);
                    retval.addEntity(ce);
                }
                else
                {
                    for (int i = node.getChildCount() - 1; i >= 0; i--)
                    {
                        fes.add(node.getChildAt(i));
                    }
                }
            }
        }
        return retval;
    }

    /**
     * 从语法树中获取指定类型的节点
     * @param root 节点
     * @param type 类型
     * @param eles 其他
     */
    public static void getByType(FormulaElement root, int type, ArrayList<FormulaElement> eles)
    {
        if (root.getNode().getId() == type)
        {
            eles.add(root);
        }
        ArrayList<FormulaElement> children = root.getChildren();
        if (children == null || children.isEmpty())
        {
            return;
        }
        else
        {
            for (FormulaElement child : children)
            {
                getByType(child, type, eles);
            }
        }
    }

}
