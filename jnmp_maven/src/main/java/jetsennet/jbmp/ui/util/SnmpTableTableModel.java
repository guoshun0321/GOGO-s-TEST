/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.ui.util;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.mib.parse.SnmpEnumEntity;

/**
 * Snmp表格在Swing中的TableModel
 * @author Guo
 */
public class SnmpTableTableModel extends AbstractTableModel
{

    private ArrayList<SnmpNodesEntity> oids;
    private String[] headNames = { "编号", "父编号", "名称", "OID", "节点类型", "值类型", "文件" };

    /**
     * @param oids 参数
     */
    public SnmpTableTableModel(ArrayList<SnmpNodesEntity> oids)
    {
        if (oids != null)
        {
            this.oids = oids;
        }
        else
        {
            this.oids = new ArrayList<SnmpNodesEntity>();
        }
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
    public void modify(ArrayList<SnmpNodesEntity> oids)
    {
        this.clear();
        if (oids != null)
        {
            this.oids.addAll(oids);
        }
        this.fireTableStructureChanged();
    }

    public ArrayList<SnmpNodesEntity> getOids()
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

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        SnmpNodesEntity entity = oids.get(rowIndex);
        Object result = null;
        switch (columnIndex)
        {
        case 0:
            result = entity.getNodeId();
            break;
        case 1:
            result = entity.getParentId();
            break;
        case 2:
            result = entity.getNodeName();
            break;
        case 3:
            result = entity.getNodeOid();
            break;
        case 4:
            int nodeType = entity.getNodeType();
            switch (nodeType)
            {
            case SnmpNodesEntity.MIBTYPE_OID:
                result = "可管理对象";
                break;
            case SnmpNodesEntity.MIBTYPE_SCALAR:
                result = "标量";
                break;
            case SnmpNodesEntity.MIBTYPE_TABLE:
                result = "表";
                break;
            case SnmpNodesEntity.MIBTYPE_ROW:
                result = "行";
                break;
            case SnmpNodesEntity.MIBTYPE_COLUMN:
                result = "列";
                break;
            default:
                result = "未知";
                break;
            }
            break;
        case 5:
            SnmpEnumEntity ee = entity.getEnumE();
            if (ee == null)
            {
                result = "";
            }
            else
            {
                result = ee.toString();
            }
            break;
        case 6:
            result = entity.getMibFile();
            break;
        default:
            result = null;
            break;
        }
        return result;
    }

    @Override
    public String getColumnName(int column)
    {
        return headNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }
}
