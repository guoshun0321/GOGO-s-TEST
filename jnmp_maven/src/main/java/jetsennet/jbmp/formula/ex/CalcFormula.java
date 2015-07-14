/************************************************************************
日 期: 2012-1-31
作 者: 郭祥
版 本: v1.3
描 述: 语法树扁平化后的公式
历 史:
 ************************************************************************/
package jetsennet.jbmp.formula.ex;

import java.util.ArrayList;

/**
 * 语法树扁平化后的公式
 * @author 郭祥
 */
public class CalcFormula
{

    private ArrayList<CalcFormulaEntity> formula;

    /**
     * @param node 节点
     */
    public void addEntity(CalcFormulaEntity node)
    {
        if (formula == null)
        {
            formula = new ArrayList<CalcFormulaEntity>();
        }
        formula.add(node);
    }

    @Override
    public String toString()
    {
        if (formula == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (CalcFormulaEntity fa : formula)
        {
            sb.append(fa.value);
        }
        return sb.toString();
    }

    public ArrayList<CalcFormulaEntity> getFormula()
    {
        return formula;
    }
}
