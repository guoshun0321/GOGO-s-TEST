package jetsennet.jbmp.formula.inspur;

import java.util.Map;

import jetsennet.jbmp.formula.FormulaElement;
import jetsennet.jbmp.formula.FormulaException;

public interface IFormulaHandle
{

    /**
     * 判断公式是否合法
     * @param str 公式
     * @return 公式合法时，返回NULL；公式不合法时，返回错误信息
     */
    public String validate(String str);

    /**
     * 解析公式
     * @param str 公式
     * @return
     */
    public FormulaElement analyze(String str) throws FormulaException;

    /**
     * 计算公式 能取到值时返回，String或Double，计算出错时返回Double.NAN，其他返回null
     * @param str 公式
     * @param replacement 替换参数
     */
    public FormulaResult calculate(int objAttribId, String str, Map<String, String> replacement) throws FormulaException;
}
