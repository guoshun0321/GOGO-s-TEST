/************************************************************************
日 期：2012-2-1
作 者: 郭祥
版 本: v1.3
描 述: 公式验证。
历 史:
 ************************************************************************/
package jetsennet.jbmp.formula;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.util.BMPConstants;

/**
 * 公式验证。验证公式是否合法。 不合法，抛出异常；合法，查看公式是否存在枚举值。
 * @author 郭祥
 */
public class FormulaValidate
{

    /**
     * 验证
     * @param formula 参数
     * @param mibId 参数
     * @return 结果
     */
    public int validate(String formula, int mibId)
    {
        FormulaElement fe = this.validateFormula(formula);
        Map<String, SnmpNodesEntity> nodes = this.validateNode(fe, mibId);
        return this.ensureValueType(nodes);
    }

    /**
     * 验证公式
     * @param formula
     * @return
     */
    private FormulaElement validateFormula(String formula)
    {
        FormulaElement retval = null;
        try
        {
            retval = FormulaCache.getInstance().getFormula(formula);
        }
        catch (Exception ex)
        {
            throw new FormulaException(ex.getMessage(), ex);
        }
        return retval;
    }

    /**
     * 验证节点
     * @param root
     * @param mibId
     * @return
     */
    private Map<String, SnmpNodesEntity> validateNode(FormulaElement root, int mibId)
    {
        Map<String, SnmpNodesEntity> retval = null;
        try
        {
            String[] oids = root.getOidNames();
            SnmpNodesDal sndal = ClassWrapper.wrapTrans(SnmpNodesDal.class);
            retval = sndal.getArrayByName(mibId, Arrays.asList(oids));
        }
        catch (Exception ex)
        {
            throw new FormulaException(ex);
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = retval.keySet();

        for (String key : keys)
        {
            SnmpNodesEntity node = retval.get(key);
            if (node == null)
            {
                if (sb.length() == 0)
                {
                    sb.append("MIB库<");
                    sb.append(mibId);
                    sb.append(">找不到以下节点：");
                }
                sb.append("\n");
                sb.append(key);
            }
        }
        if (sb.length() > 0)
        {
            throw new FormulaException(sb.toString());
        }
        return retval;
    }

    /**
     * 处理枚举值
     * @param nodes
     * @param mibType
     * @return
     */
    private int ensureValueType(Map<String, SnmpNodesEntity> nodes)
    {
        if (nodes.size() == 1)
        {
            SnmpNodesEntity node = null;
            Set<String> keys = nodes.keySet();
            for (String key : keys)
            {
                node = nodes.get(key);
            }
            if (node != null)
            {
                return node.getValueId();
            }
        }
        return 0;
    }

    /**
     * 主函数
     * @param args 参数
     */
    public static void main(String[] args)
    {
        try
        {
            FormulaValidate fv = new FormulaValidate();
            int index = fv.validate("xxx" + ")", BMPConstants.DEFAULT_MIB_NAME_ID);
            System.out.println(index);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
