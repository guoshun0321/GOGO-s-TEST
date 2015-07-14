package jetsennet.jbmp.formula.inspur;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jetsennet.jbmp.formula.FormulaCache;
import jetsennet.jbmp.formula.FormulaConstants;
import jetsennet.jbmp.formula.FormulaElement;
import jetsennet.jbmp.formula.FormulaException;
import jetsennet.jbmp.formula.FormulaUtil;
import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.CalcFormulaEntity;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;
import org.mvel2.MVEL;

public class FormulaHandle implements IFormulaHandle
{

    /**
     * 差分缓存。第一个Integer是对象属性ID，第二个Double是采集时间，第三个是当次采集值
     */
    protected static final Map<Integer, TwoTuple<Long, Double>> rstCache = new HashMap<Integer, TwoTuple<Long, Double>>();
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(FormulaHandle.class);

    @Override
    public String validate(String str)
    {
        String retval = null;
        try
        {
            FormulaCache.getInstance().getFormula(str);
        }
        catch (Exception ex)
        {
            retval = ex.getMessage();
        }
        return retval;
    }

    @Override
    public FormulaElement analyze(String str) throws FormulaException
    {
        FormulaElement retval = null;
        retval = FormulaCache.getInstance().getFormula(str);
        if (retval == null)
        {
            throw new FormulaException(String.format("无法解析公式<%s>。", str));
        }
        return retval;
    }

    @Override
    public FormulaResult calculate(int objAttribId, String str, Map<String, String> replacement) throws FormulaException
    {
        FormulaResult fr = new FormulaResult();
        if (replacement == null)
        {
            throw new NullPointerException();
        }
        try
        {
            FormulaElement root = this.analyze(str);
            ArrayList<FormulaElement> formulaAndName = root.getChildren();
            FormulaElement formulaEle = formulaAndName.get(0);
            FormulaElement nameEle = formulaAndName.size() > 1 ? formulaAndName.get(1) : null;

            // 解析公式
            this.parse(formulaEle, replacement, fr, false);
            if (nameEle != null)
            {
                this.parse(nameEle, replacement, fr, true);
            }

            // 计算公式
            this.operate(objAttribId, fr);
        }
        catch (Exception ex)
        {
            removeLast(objAttribId);
            throw new FormulaException(ex);
        }
        return fr;
    }

    /**
     * 处理公式
     * @param ele
     * @param replacement
     * @param fr
     * @throws FormulaException
     */
    private void parse(FormulaElement ele, Map<String, String> replacement, FormulaResult fr, boolean isName) throws FormulaException
    {
        StringBuilder retval = new StringBuilder();
        CalcFormula cf = FormulaUtil.flat(ele.getNode(), new int[] { FormulaConstants.OID_STRING, FormulaConstants.IDENTIFIER_STRING });
        for (CalcFormulaEntity cfe : cf.getFormula())
        {
            int type = cfe.type;
            String value = cfe.value;
            if (type == FormulaConstants.IDENTIFIER_STRING)
            {
                String temp = replacement.get(value);
                if (temp == null)
                {
                    throw new FormulaException("找不到变量：" + value + "对应的值。");
                }
                else
                {
                    retval.append(temp);
                }
            }
            else if (type == FormulaConstants.EXP_HEAD || type == FormulaConstants.STR_HEAD || type == FormulaConstants.DIF_HEAD)
            {
                fr.setExpHead(value);
            }
            else if (type == FormulaConstants.NAME_HEAD)
            {
                fr.setNameHead(value);
            }
            else if (type == FormulaConstants.STR_STRING)
            {
                String temp = value.substring(1, value.length() - 1);
                retval.append(temp);
            }
            else if (type == FormulaConstants.COLON || type == FormulaConstants.SEMI_COLON)
            {

            }
            else
            {
                retval.append(value);
            }
        }
        String formula = retval.toString();
        formula = formula.substring(1, formula.length() - 1);
        if (isName)
        {
            fr.setNameStr(formula);
            fr.setNameRst(formula);
        }
        else
        {
            fr.setExpStr(formula);
        }
    }

    /**
     * 计算
     * @param fr
     */
    private void operate(int objAttribId, FormulaResult fr)
    {
        String expHead = fr.getExpHead();
        String expStr = fr.getExpStr();
        if (!expHead.equals("str"))
        {
            try
            {
                double curValue = Double.parseDouble(MVEL.eval(expStr).toString());
                if (expHead.equals("exp"))
                {
                    fr.setExpRst(curValue);
                }
                else
                {
                    // 差分,时间单位为秒
                    TwoTuple<Long, Double> last = rstCache.get(objAttribId);
                    long curTime = new Date().getTime();
                    if (last == null)
                    {
                        logger.debug("对象属性：" + objAttribId + "第一次采集。");
                        rstCache.put(objAttribId, new TwoTuple<Long, Double>(curTime, curValue));
                    }
                    else
                    {
                        long lastTime = last.first;
                        double lastValue = last.second;
                        try
                        {
                            double dif = (curValue - lastValue) / ((curTime - lastTime) / 1000);
                            fr.setExpRst(dif);
                            rstCache.put(objAttribId, new TwoTuple<Long, Double>(curTime, curValue));
                        }
                        catch (Exception ex)
                        {
                            logger.error("", ex);
                            removeLast(objAttribId);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                fr.setExpRst(Double.NaN);
                logger.error("公式:" + expStr + "计算错误!", ex);
            }
        }
    }

    /**
     * 在缓存中清掉该属性上一次的值
     * @param objAttribId
     */
    public static void removeLast(int objAttribId)
    {
        rstCache.put(objAttribId, null);
    }

    public static void main(String[] args) throws Exception
    {
        FormulaHandle handle = new FormulaHandle();
        Map<String, String> replacement = new HashMap<String, String>();
        replacement.put("analog1", "20");
        handle.calculate(1, "dif:(analog2)", replacement);
        TimeUnit.SECONDS.sleep(10);
        replacement.put("analog1", "30");
        handle.calculate(1, "dif:(analog1)", replacement);
    }

}
