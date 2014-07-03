/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import java.util.ArrayList;

import net.percederberg.mibble.MibValueSymbol;

import org.snmp4j.smi.VariableBinding;

/**
 * @author Guo
 */
public class TrapNode extends AbstractNode
{

    /**
     * 对v1有效，对v2c为-1
     */
    private int specialTrap;
    /**
     * 对应mibble上的具体节点
     */
    private MibValueSymbol symbol;
    /**
     * 结果
     */
    private ArrayList<VariableBinding> binds;
    /**
     * trap时间
     */
    private long collTime;
    public static final int SPECIAL_TRAP_V2C = -1;

    /**
     * @param name 名称
     * @param oid 参数
     */
    public TrapNode(String name, String oid)
    {
        super(name, oid);
    }

    @Override
    public Object clone()
    {
        TrapNode node = new TrapNode(this.getName(), this.getOid());
        node.setSpecialTrap(this.specialTrap);
        return node;
    }

    /**
     * @return the specialTrap
     */
    public int getSpecialTrap()
    {
        return specialTrap;
    }

    /**
     * @param specialTrap the specialTrap to set
     */
    public void setSpecialTrap(int specialTrap)
    {
        this.specialTrap = specialTrap;
    }

    /**
     * @return the binds
     */
    public ArrayList<VariableBinding> getBinds()
    {
        return binds;
    }

    /**
     * @param binds the binds to set
     */
    public void setBinds(ArrayList<VariableBinding> binds)
    {
        this.binds = binds;
    }

    /**
     * @return the collTime
     */
    public long getCollTime()
    {
        return collTime;
    }

    /**
     * @param collTime the collTime to set
     */
    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    /**
     * @return the symbol
     */
    public MibValueSymbol getSymbol()
    {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(MibValueSymbol symbol)
    {
        this.symbol = symbol;
    }
}
