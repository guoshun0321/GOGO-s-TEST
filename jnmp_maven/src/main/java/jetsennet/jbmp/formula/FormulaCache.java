/************************************************************************
日 期: 2012-1-31
作 者: 郭祥
版 本: v1.3
描 述: 公式缓存
历 史:
 ************************************************************************/
package jetsennet.jbmp.formula;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.Token;

import org.apache.log4j.Logger;

import jetsennet.jbmp.formula.ex.TerminalSymbol;
import jetsennet.jbmp.formula.ex.TerminalSymbolList;

/**
 * 公式缓存
 * @author 郭祥
 */
public final class FormulaCache
{

    /**
     * 可解析公式
     */
    private Map<String, FormulaElement> str2Formula;
    /**
     * 错误公式，以及错误原因
     */
    private Map<String, String> str2Error;
    /**
     * 公式分析器。单个实例最好用于单线程，多线程不知道有没有问题。
     */
    private FormulaAnalyzerEx analyzer;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(FormulaCache.class);
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static FormulaCache instance = new FormulaCache();

    private FormulaCache()
    {
        str2Formula = new HashMap<String, FormulaElement>();
        str2Error = new HashMap<String, String>();
        analyzer = new FormulaAnalyzerEx();
    }

    public static FormulaCache getInstance()
    {
        return instance;
    }

    // </editor-fold>

    /**
     * 公式能正确解析时，返回解析后的公式的根节点。 公式不能正确解析时，抛出异常。
     * @param str 参数
     * @return 结果
     * @throws FormulaException 异常
     */
    public synchronized FormulaElement getFormula(String str) throws FormulaException
    {
        FormulaElement retval = str2Formula.get(str);
        String error = str2Error.get(str);
        if (retval == null)
        {
            if (error != null)
            {
                throw new FormulaException(error);
            }
            Object obj = this.parseFormula(str);
            if (obj != null && obj instanceof String)
            {
                str2Error.put(str, (String) obj);
                throw new FormulaException((String) obj);
            }
            else if (obj != null && obj instanceof FormulaElement)
            {
                str2Formula.put(str, (FormulaElement) obj);
                return (FormulaElement) obj;
            }
        }
        return retval;
    }

    /**
     * 从语法树中获取指定类型的节点
     * @param root 节点
     * @param list 其他
     */
    public void fillTerminalList(FormulaElement root, TerminalSymbolList list)
    {
        String value = root.getValue();
        Node temp = root.getNode();
        if (value != null)
        {
            TerminalSymbol ts = new TerminalSymbol();
            ts.setStr(value);
            ts.setType(temp.getId());
            ts.setNode((Token) temp);
            list.addSymbol(ts);
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
                fillTerminalList(child, list);
            }
        }
    }

    /**
     * 解析公式
     * @param str
     * @return
     * @throws FormulaException
     */
    private Object parseFormula(String str)
    {
        Object retval = null;
        Reader input = new StringReader(str);
        try
        {
            FormulaParse parse = new FormulaParse(input, analyzer);
            parse.parse();
            FormulaElement formula = analyzer.getRoot();
            this.fillOidNames(formula);
            this.fillOids(formula);
            formula.setList(FormulaUtil.toTerminalList(formula));
            formula
                .setCalcFormula(FormulaUtil.flat(formula.getNode(), new int[] { FormulaConstants.OID_STRING, FormulaConstants.IDENTIFIER_STRING }));
            retval = formula;
        }
        catch (Exception ex)
        {
            String msg = String.format("公式<%s>解析出错：%s", str, ex.getMessage());
            logger.error(msg, ex);
            retval = msg;
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (Exception ex1)
            {
                logger.error("", ex1);
            }
            finally
            {
                input = null;
            }
        }
        return retval;
    }

    private void fillOidNames(FormulaElement fe)
    {
        ArrayList<FormulaElement> fes = new ArrayList<FormulaElement>();
        FormulaUtil.getByType(fe, FormulaConstants.IDENTIFIER_STRING, fes);
        if (fes != null && !fes.isEmpty())
        {
            String[] oidNames = new String[fes.size()];
            for (int i = 0; i < fes.size(); i++)
            {
                oidNames[i] = fes.get(i).toString();
            }
            fe.setOidNames(oidNames);
        }
    }

    private void fillOids(FormulaElement fo)
    {
        ArrayList<FormulaElement> fes = new ArrayList<FormulaElement>();
        FormulaUtil.getByType(fo, FormulaConstants.OID, fes);
        if (fes != null && !fes.isEmpty())
        {
            String[] oids = new String[fes.size()];
            for (int i = 0; i < fes.size(); i++)
            {
                oids[i] = fes.get(i).toString();
            }
            fo.setOids(oids);
        }
    }
    
    public static void main(String[] args)
    {
        FormulaCache.getInstance().parseFormula("exp:(-4-4)");
    }
}
