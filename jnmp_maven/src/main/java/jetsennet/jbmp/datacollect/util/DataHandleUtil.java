/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.datacollect.util;

import java.util.ArrayList;

import jetsennet.jbmp.ins.exp.Formula;
import jetsennet.jbmp.ins.exp.FormulaConstants;
import jetsennet.jbmp.ins.exp.FormulaNode;

/**
 * @author Guo
 */
public class DataHandleUtil
{

    /**
     * 获取可以计算的表达式
     * @param formula 参数
     * @return 结果
     */
    public static String getCalString(Formula formula)
    {
        ArrayList<FormulaNode> nodes = formula.getNodes();
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < nodes.size(); i++)
        {
            if (i < (nodes.size() - 1) && nodes.get(i + 1).getType() != FormulaConstants.FORMULA_SEMICOLON)
            {
                sb.append(nodes.get(i).getUsable());
            }
        }
        return sb.toString();
    }
}
