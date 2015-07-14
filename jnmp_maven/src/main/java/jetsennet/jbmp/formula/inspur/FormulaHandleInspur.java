package jetsennet.jbmp.formula.inspur;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.formula.FormulaCache;
import jetsennet.jbmp.formula.FormulaConstants;
import jetsennet.jbmp.formula.FormulaElement;
import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.CalcFormulaEntity;

import org.apache.log4j.Logger;

public class FormulaHandleInspur extends FormulaHandle
{

    /**
     * 合法变量
     */
    private static final String[] LEGAL_PARAM =
        { "analog_in", "analog_in0", "analog_in1", "analog_in2", "analog_in3", "analog_in4", "analog_in5", "analog_in6", "analog_in7", "switch_in",
            "switch_in0", "switch_in1", "switch_in2", "switch_in3", "switch_in4", "switch_in5", "switch_in6", "switch_in7" };
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(Logger.class);

    @Override
    public String validate(String str)
    {
        String retval = null;
        try
        {
            FormulaElement rootEle = FormulaCache.getInstance().getFormula(str);
            CalcFormula cf = rootEle.getCalcFormula();
            for (CalcFormulaEntity cfe : cf.getFormula())
            {
                int type = cfe.type;
                String value = cfe.value;
                if (type == FormulaConstants.IDENTIFIER_STRING)
                {
                    if (!this.isLegalParam(value))
                    {
                        retval = "公式中包含非法变量：" + value;
                        break;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            retval = ex.getMessage();
        }
        return retval;
    }

    /**
     * 是否为合法参数
     * @param str
     * @return
     */
    private boolean isLegalParam(String str)
    {
        boolean retval = false;
        for (String param : LEGAL_PARAM)
        {
            if (param.equals(str))
            {
                retval = true;
                break;
            }
        }
        return retval;
    }

    /**
     * 生成实例化后的公式
     * @param str
     * @return
     */
    public List<String> genInsFormula(String str)
    {
        List<String> retval = new ArrayList<String>();
        try
        {
            // 确定生成多少个公式
            int num = 1;
            FormulaElement rootEle = FormulaCache.getInstance().getFormula(str);
            CalcFormula cf = rootEle.getCalcFormula();
            for (CalcFormulaEntity cfe : cf.getFormula())
            {
                int type = cfe.type;
                String value = cfe.value;
                if (type == FormulaConstants.IDENTIFIER_STRING)
                {
                    if ("analog_in".equals(value))
                    {
                        num = 8;
                        break;
                    }
                }
            }

            // 生成公式
            for (int i = 0; i < num; i++)
            {
                StringBuilder sb = new StringBuilder();
                for (CalcFormulaEntity cfe : cf.getFormula())
                {
                    int type = cfe.type;
                    String value = cfe.value;
                    if (type == FormulaConstants.IDENTIFIER_STRING && "analog_in".equals(value))
                    {
                        value = value + i;
                    }
                    sb.append(value);
                }
                retval.add(sb.toString());
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    public static void main(String[] args)
    {
        FormulaHandleInspur inspur = new FormulaHandleInspur();
        List<String> strs = inspur.genInsFormula("exp:(analog_in/100);name:(\"xxx\")");
        System.out.println(strs);
    }

}
