/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 公式节点
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.exp;

import java.util.ArrayList;

import jetsennet.jbmp.mib.node.EditNode;

/**
 * 公式节点
 * @author 郭祥
 */
public class FormulaNode
{

    /**
     * 对应的文本表达式
     */
    private String str;
    /**
     * 类型，在FormulaConstants中定义具体的值
     */
    private int type;
    /**
     * 具体被使用的部分，初始化时设置为str。 实例化生成结果时，储存具体实例化后的值。 采集时为采集后的结果。
     */
    private String usable;
    /**
     * 实例化数据，供table型数据使用
     */
    private ArrayList<EditNode> insDatas;
    /**
     * 实例化数据
     */
    private EditNode insData;

    /**
     * 构造方法
     * @param str 参数
     * @param type 参数
     * @param usable 参数
     */
    public FormulaNode(String str, int type, String usable)
    {
        this.str = str;
        this.type = type;
        this.usable = usable;
        insDatas = null;
        insData = null;
    }

    /**
     * @return 复制
     */
    public FormulaNode copy()
    {
        return new FormulaNode(str, type, usable);
    }

    @Override
    public String toString()
    {
        return str + " : " + usable;
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

    /**
     * @return the usable
     */
    public String getUsable()
    {
        return usable;
    }

    /**
     * @param usable the usable to set
     */
    public void setUsable(String usable)
    {
        this.usable = usable;
    }

    /**
     * @return the insDatas
     */
    public ArrayList<EditNode> getInsDatas()
    {
        return insDatas;
    }

    /**
     * @param insDatas the insDatas to set
     */
    public void setInsDatas(ArrayList<EditNode> insDatas)
    {
        this.insDatas = insDatas;
    }

    /**
     * @return the insData
     */
    public EditNode getInsData()
    {
        return insData;
    }

    /**
     * @param insData the insData to set
     */
    public void setInsData(EditNode insData)
    {
        this.insData = insData;
    }
    // </editor-fold>
}
