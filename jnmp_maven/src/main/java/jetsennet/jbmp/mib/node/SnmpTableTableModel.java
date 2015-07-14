/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * Snmp表格在Swing中的TableModel
 * @author Guo
 */
public class SnmpTableTableModel extends AbstractTableModel
{

    private SnmpTable snmp;
    private String[] headNames;

    /**
     * 构造函数
     * @param snmp 参数
     */
    public SnmpTableTableModel(SnmpTable snmp)
    {
        if (snmp != null)
        {
            modify(snmp);
        }
        else
        {
            headNames = new String[0];
            this.snmp = null;
        }
    }

    /**
     * 清除
     */
    public void clear()
    {
        snmp = null;
        headNames = new String[0];
        this.fireTableStructureChanged();
    }

    /**
     * @param snmp 参数
     */
    public void modify(SnmpTable snmp)
    {
        this.snmp = snmp;
        ArrayList<EditNode> temp = snmp.getHeaders();
        headNames = new String[temp.size()];
        for (int i = 0; i < temp.size(); i++)
        {
            headNames[i] = temp.get(i).getSymbol().getNodeName();
        }
        this.fireTableStructureChanged();
    }

    @Override
    public int getRowCount()
    {
        if (snmp != null)
        {
            return snmp.getRowNum();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int getColumnCount()
    {
        if (snmp != null)
        {
            return snmp.getColumnNum();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (snmp != null && rowIndex < snmp.getRowNum() && columnIndex < snmp.getColumnNum())
        {
            return snmp.getCell(rowIndex, columnIndex).getValue();
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getColumnName(int column)
    {
        if (column < headNames.length)
        {
            return headNames[column];
        }
        else
        {
            return null;
        }
    }
}
