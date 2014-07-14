/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 公式解析器
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.exp;

import java.util.HashMap;
import java.util.Map;

/**
 * 未解决二义性判断的解析器。在某个字符处理存在二义性时，注意添加RegularExpression的顺序。 解析部分需要修改。
 * @author Guo
 */
public class FormulaParse
{

    private static final RegularExpression calExp;
    private static final RegularExpression insExp;
    private static final Map<String, Formula> formulas;

    static
    {
        formulas = new HashMap<String, Formula>();
        calExp = new SequenceExpression();
        calExp.addExpression(new LiteralExpression(FormulaConstants.EXP_HEAD));
        calExp.addExpression(new LiteralExpression(FormulaConstants.EXP_COLON));
        calExp.addExpression(new LiteralExpression(FormulaConstants.EXP_LBRACKET));
        RepetitionExpression re = new RepetitionExpression();
        re.addExpression(new LiteralExpression(FormulaConstants.EXP_NUM));
        re.addExpression(new LiteralExpression(FormulaConstants.EXP_OID));
        re.addExpression(new LiteralExpression(FormulaConstants.EXP_OP));
        re.addExpression(new LiteralExpression(FormulaConstants.EXP_STR));
        re.addExpression(new LiteralExpression(FormulaConstants.EXP_LBRACKET));
        re.addExpression(new LiteralExpression(FormulaConstants.EXP_RBRACKET));
        calExp.addExpression(re);
        calExp.addExpression(new LiteralExpression(FormulaConstants.EXP_SEMICOLON));
        calExp.addExpression(new LiteralExpression(FormulaConstants.EXP_HEAD_NAME));
        calExp.addExpression(new LiteralExpression(FormulaConstants.EXP_COLON));
        calExp.addExpression(new LiteralExpression(FormulaConstants.EXP_LBRACKET));
        RepetitionExpression re1 = new RepetitionExpression();
        re1.addExpression(new LiteralExpression(FormulaConstants.EXP_NUM));
        re1.addExpression(new LiteralExpression(FormulaConstants.EXP_OID));
        re1.addExpression(new LiteralExpression(FormulaConstants.EXP_STR));
        re1.addExpression(new LiteralExpression(FormulaConstants.EXP_LBRACKET));
        re1.addExpression(new LiteralExpression(FormulaConstants.EXP_RBRACKET));
        calExp.addExpression(re1);

        insExp = new SequenceExpression();
        insExp.addExpression(new LiteralExpression(FormulaConstants.EXP_HEAD));
        insExp.addExpression(new LiteralExpression(FormulaConstants.EXP_COLON));
        insExp.addExpression(new LiteralExpression(FormulaConstants.EXP_LBRACKET));
        RepetitionExpression re2 = new RepetitionExpression();
        re2.addExpression(new LiteralExpression(FormulaConstants.EXP_OID_INS));
        re2.addExpression(new LiteralExpression(FormulaConstants.EXP_NUM));
        re2.addExpression(new LiteralExpression(FormulaConstants.EXP_OP));
        re2.addExpression(new LiteralExpression(FormulaConstants.EXP_LBRACKET));
        re2.addExpression(new LiteralExpression(FormulaConstants.EXP_RBRACKET));
        re2.addExpression(new LiteralExpression(FormulaConstants.EXP_STR));

        insExp.addExpression(re2);
    }

    /**
     * @param str 参数
     * @return 结果
     */
    public synchronized static Formula parseCal(String str)
    {
        if (!formulas.containsKey(str))
        {
            Formula formula = new Formula(str);
            calExp.interpret(formula);
            formulas.put(str, formula);
        }
        return formulas.get(str);
    }

    /**
     * @param str 参数
     * @return 结果
     */
    public synchronized static Formula parseCalIns(String str)
    {
        if (!formulas.containsKey(str))
        {
            Formula formula = new Formula(str);
            insExp.interpret(formula);
            formulas.put(str, formula);
        }
        return formulas.get(str);
    }

}
