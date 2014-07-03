/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 代表一个表达式
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.exp;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import jetsennet.jbmp.mib.node.EditNode;

/**
 * 代表一个表达式
 * @author 郭祥
 */
public class Formula
{

    /**
     * 被解析的字符串
     */
    private String str;
    /**
     * 当前位置
     */
    private int pos;
    /**
     * 解析出来的节点集合
     */
    private ArrayList<FormulaNode> nodes;
    private static final Logger logger = Logger.getLogger(Formula.class);

    /**
     * 深clone
     * @return 结果
     */
    public Formula copy()
    {
        Formula result = new Formula(str);
        ArrayList<FormulaNode> nds = new ArrayList<FormulaNode>();
        for (int i = 0; i < nodes.size(); i++)
        {
            nds.add(nodes.get(i).copy());
        }
        result.setNodes(nds);
        return result;
    }

    /**
     * 添加节点，解析时使用
     * @param str
     * @param ft
     */
    public void addNode(String str, int type)
    {
        if (str != null && str.length() > 0)
        {
            if (nodes == null)
            {
                nodes = new ArrayList<FormulaNode>();
            }
            pos = pos + str.length();
            FormulaNode node = new FormulaNode(str, type, str);
            nodes.add(node);
        }
    }

    public Formula(String str)
    {
        this.str = str;
    }

    public String[] oidCollection()
    {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < nodes.size(); i++)
        {
            FormulaNode node = nodes.get(i);
            if (node.getType() == FormulaConstants.FORMULA_OID)
            {
                result.add(node.getUsable());
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public ArrayList<FormulaNode> getByType(int type)
    {
        ArrayList<FormulaNode> result = new ArrayList<FormulaNode>();
        for (int i = 0; i < nodes.size(); i++)
        {
            FormulaNode node = nodes.get(i);
            if (node.getType() == type)
            {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++)
        {
            sb.append(nodes.get(i).getUsable());
        }
        return sb.toString();
    }

    public ArrayList<String> insFormula()
    {
        int num = this.getInsNum();
        if (num <= 0)
        {
            logger.error("无法实例化：" + this.toString());
            return null;
        }
        ArrayList<String> results = new ArrayList<String>();
        for (int i = 0; i < num; i++)
        {
            boolean isCal = true;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < nodes.size(); j++)
            {
                FormulaNode node = nodes.get(j);
                if (isCal)
                {
                    if (node.getType() == FormulaConstants.FORMULA_OID)
                    {
                        sb.append("OID:(");
                        if (node.getInsData() != null && node.getInsData().getValue() != null && node.getInsData().getValue().getOid() != null)
                        {
                            sb.append(node.getInsData().getValue().getOid());
                        }
                        else if (node.getInsDatas() != null)
                        {
                            sb.append(node.getInsDatas().get(i).getValue().getOid());
                        }
                        else
                        {
                            sb = null;
                            break;
                        }
                        sb.append(")");
                    }
                    else if (node.getType() == FormulaConstants.FORMULA_SEMICOLON)
                    {
                        sb.append(node.getUsable());
                        isCal = false;
                    }
                    else
                    {
                        sb.append(node.getUsable());
                    }
                }
                else
                {
                    if (node.getType() == FormulaConstants.FORMULA_OID)
                    {
                        if (node.getInsData() != null)
                        {
                            sb.append(node.getInsData().getValue().getVariable().toString());
                        }
                        else if (node.getInsDatas() != null)
                        {
                            sb.append(node.getInsDatas().get(i).getValue().getVariable().toString());
                        }
                        else
                        {
                            sb = null;
                            break;
                        }
                    }
                    else
                    {
                        sb.append(node.getUsable());
                    }
                }
            }
            if (sb != null)
            {
                results.add(sb.toString());
            }
            else
            {
                return null;
            }
        }
        return results;
    }

    /**
     * 实例化出来的对象的数量
     * @return -1，实例化出错，表格型数据的数量不相等；0，无实例化对象；
     */
    public int getInsNum()
    {
        int num = 0;
        boolean isTable = false;
        for (FormulaNode node : nodes)
        {
            ArrayList<EditNode> insDatas = node.getInsDatas();
            EditNode insData = node.getInsData();
            if (node.getType() == FormulaConstants.FORMULA_OID)
            {
                if (insDatas != null)
                {
                    isTable = true;
                    if (num == 0)
                    {
                        num = insDatas.size();
                    }
                    else
                    {
                        if (num != insDatas.size())
                        {
                            return -1;
                        }
                    }
                }
                else
                {
                    if (insData == null)
                    {
                        return -1;
                    }
                }
            }
        }
        if (!isTable)
        {
            num = 1;
        }
        return num;
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
     * @return the pos
     */
    public int getPos()
    {
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(int pos)
    {
        this.pos = pos;
    }

    /**
     * @return the nodes
     */
    public ArrayList<FormulaNode> getNodes()
    {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(ArrayList<FormulaNode> nodes)
    {
        this.nodes = nodes;
    }
    // </editor-fold>
}
