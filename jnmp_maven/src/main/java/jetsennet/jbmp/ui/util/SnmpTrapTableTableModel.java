/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.ui.util;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpTrapType;

import jetsennet.jbmp.mib.node.TrapNode;
import jetsennet.jbmp.util.ThreeTuple;

/**
 * SnmpTrap表格在Swing中的TableModel
 * @author Guo
 */
public class SnmpTrapTableTableModel extends AbstractTableModel
{

    private ArrayList<TrapNode> oids;
    private String[] headNames = { "名称", "OID", "描述" };

    /**
     * @param oids 参数
     */
    public SnmpTrapTableTableModel(ArrayList<TrapNode> oids)
    {
        if (oids != null)
        {
            this.oids = oids;
        }
        else
        {
            this.oids = new ArrayList<TrapNode>();
        }
    }

    /**
     * @return 结果
     */
    public ArrayList<ThreeTuple<String, String, String>> snapshot()
    {
        ArrayList<ThreeTuple<String, String, String>> retval = new ArrayList<ThreeTuple<String, String, String>>();
        for (int i = 0; i < oids.size(); i++)
        {
            String first = this.getValueAt(i, 0).toString();
            String second = this.getValueAt(i, 1).toString();
            String third = this.getValueAt(i, 2).toString();
            ThreeTuple<String, String, String> temp = new ThreeTuple<String, String, String>(first, second, third);
            retval.add(temp);
        }
        return retval;
    }

    /**
     * 清空
     */
    public void clear()
    {
        oids.clear();
        this.fireTableStructureChanged();
    }

    /**
     * @param oids 参数
     */
    public void modify(ArrayList<TrapNode> oids)
    {
        this.clear();
        this.oids.addAll(oids);
        this.fireTableStructureChanged();
    }

    public ArrayList<TrapNode> getOids()
    {
        return oids;
    }

    public int getRowCount()
    {
        return oids.size();
    }

    public int getColumnCount()
    {
        return headNames.length;
    }

    /**
     * @param row 参数
     * @return 结果
     */
    public TrapNode getRow(int row)
    {
        return oids.get(row);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        TrapNode entity = oids.get(rowIndex);
        Object result = null;
        if (entity != null && entity.getSymbol() != null)
        {
            MibValueSymbol mvs = entity.getSymbol();
            String oid = "";
            String desc = "";
            if (mvs.getType() instanceof SnmpTrapType)
            {
                SnmpTrapType stt = (SnmpTrapType) mvs.getType();
                oid = stt.getEnterprise().toString();
                oid = oid + "/" + mvs.getValue().toString();
                desc = stt.getDescription();
            }
            switch (columnIndex)
            {
            case 0:
                result = entity.getName();
                break;
            case 1:
                result = oid;
                break;
            case 2:
                result = desc;
                break;
            default:
                result = null;
                break;
            }
        }
        return result;
    }

    @Override
    public String getColumnName(int column)
    {
        if (column < headNames.length && column >= 0)
        {
            return headNames[column];
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }
}
