/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import net.percederberg.mibble.MibTypeSymbol;

/**
 * @author Guo
 */
public class TextNode extends AbstractNode
{

    private MibTypeSymbol symbol;

    /**
     * @param name 名称
     * @param oid 参数
     */
    public TextNode(String name, String oid)
    {
        super(name, oid);
    }

    /**
     * @return the symbol
     */
    public MibTypeSymbol getSymbol()
    {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(MibTypeSymbol symbol)
    {
        this.symbol = symbol;
    }
}
