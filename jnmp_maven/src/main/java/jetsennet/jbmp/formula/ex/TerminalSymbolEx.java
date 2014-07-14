/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.formula.ex;

/**
 * @author GuoXiang
 */
public class TerminalSymbolEx extends TerminalSymbol
{

    private int num;
    private int snmpType;
    private String[] oids;
    private String[] values;
    public static final int SNMP_TYPE_SCA = 1;
    public static final int SNMP_TYPE_TABLE = 2;

    /**
     * @param num 参数
     */
    public TerminalSymbolEx(int num)
    {
        this.num = num;
        oids = new String[num];
        values = new String[num];
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the num
     */
    public int getNum()
    {
        return num;
    }

    /**
     * @param num the num to set
     */
    public void setNum(int num)
    {
        this.num = num;
    }

    /**
     * @return the snmpType
     */
    public int getSnmpType()
    {
        return snmpType;
    }

    /**
     * @param snmpType the snmpType to set
     */
    public void setSnmpType(int snmpType)
    {
        this.snmpType = snmpType;
    }

    /**
     * @return the oids
     */
    public String[] getOids()
    {
        return oids;
    }

    /**
     * @param oids the oids to set
     */
    public void setOids(String[] oids)
    {
        this.oids = oids;
    }

    /**
     * @return the values
     */
    public String[] getValues()
    {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(String[] values)
    {
        this.values = values;
    }
    // </editor-fold>
}
