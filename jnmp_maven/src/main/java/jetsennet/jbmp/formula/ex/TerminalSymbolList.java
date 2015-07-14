/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula.ex;

import java.util.ArrayList;

/**
 * @author GuoXiang
 */
public class TerminalSymbolList
{

    private ArrayList<TerminalSymbol> symbols;

    /**
     * 构造函数
     */
    public TerminalSymbolList()
    {
        symbols = new ArrayList<TerminalSymbol>();
    }

    /**
     * 增加
     * @param ts 参数
     */
    public void addSymbol(TerminalSymbol ts)
    {
        symbols.add(ts);
    }

    /**
     * @return 复制
     */
    public TerminalSymbolList copy()
    {
        TerminalSymbolList retval = new TerminalSymbolList();
        for (TerminalSymbol ts : symbols)
        {
            retval.addSymbol(ts.copy());
        }
        return retval;
    }

    /**
     * @return 大小
     */
    public int size()
    {
        return symbols.size();
    }

    /**
     * @return 判断是否为空
     */
    public boolean isEmpty()
    {
        if (this.size() <= 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the symbols
     */
    public ArrayList<TerminalSymbol> getSymbols()
    {
        return symbols;
    }

    /**
     * @param symbols the symbols to set
     */
    public void setSymbols(ArrayList<TerminalSymbol> symbols)
    {
        this.symbols = symbols;
    }
    // </editor-fold>
}
