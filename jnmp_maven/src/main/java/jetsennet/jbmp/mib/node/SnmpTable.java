/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.protocols.snmp.datahandle.AbsSnmpValueTrans;

/**
 * SNMP表格数据。 必要元素：主机IP，community，端口，需要扫描的表格列，表格的索引列。 使用方法：<br/> 1、调用构造函数；<br/> 2、如果需要设置全局编码，调用setCoding，默认编码为ASCII；<br/>
 * 3、如果需要设置全局值处理类，调用setHandle。默认值处理类为jetsennet.jnmp.datacollect.collectutil. SnmpNodeHandle；<br/> 4、调用addColumn添加列，添加列时可以为不同列设置不同的编码和值处理类；<br/>
 * 5、调用setIndex设置索引列；<br/> 填充表格时，调用SnmpTableUtil.initSnmpTable。
 * @author 郭祥
 */
public class SnmpTable implements Serializable
{

    /**
     * 表名称，可为空
     */
    private String tableName;
    /**
     * 索引OID
     */
    private String index;
    /**
     * 表头
     */
    private ArrayList<EditNode> headers;
    /**
     * 表单元格，单元格的排序为从上到下，从左到右
     */
    private ArrayList<EditNode> cells;
    /**
     * 行数
     */
    private int rowNum;
    /**
     * 列数
     */
    private int columnNum;
    /**
     * 默认编码
     */
    private String coding;
    /**
     * 值处理
     */
    private transient AbsSnmpValueTrans handle;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpTable.class);
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;

    /**
     * 初始化
     * @param tableName 表名称，可为null
     */
    public SnmpTable(String tableName)
    {
        this.tableName = tableName;
        this.rowNum = 0;
        this.columnNum = 0;
        coding = MibConstants.CODING_DEFAULT;
        cells = new ArrayList<EditNode>();
        headers = new ArrayList<EditNode>();
    }

    // <editor-fold defaultstate="collapsed" desc="表格构建">
    /**
     * 添加列，默认添加的第一列为索引
     * @param col 参数
     * @return 结果
     */
    public EditNode addColumn(EditNode col)
    {
        if (col == null)
        {
            throw new NullPointerException();
        }
        String oid = col.getOid();
        if (!this.isContain(oid))
        {
            headers.add(col);
            columnNum++;
            if (index == null)
            {
                index = col.getSymbol().getNodeOid();
            }
            if (col.getCoding() == null)
            {
                col.setCoding(this.coding);
            }
            if (col.getHandle() == null)
            {
                col.setHandle(this.handle);
            }
        }
        return col;
    }

    /**
     * @param oid 参数
     * @return 结果
     */
    public EditNode addColumn(String oid)
    {
        EditNode node = new EditNode(new SnmpNodesEntity("", oid), null, null);
        this.addColumn(node);
        return node;
    }

    /**
     * 设置索引列
     * @param index 参数
     * @return 结果
     */
    public String setIndex(String index)
    {
        this.index = index;
        return this.index;
    }

    /**
     * 表格中是否包含给定OID
     * @param oid
     * @return
     */
    private boolean isContain(String oid)
    {
        boolean retval = false;
        for (EditNode header : headers)
        {
            if (header.getOid().equals(oid))
            {
                retval = true;
                break;
            }
        }
        return retval;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="表格操作">
    /**
     * 填充表格
     * @param cell 参数
     * @return 结果
     */
    public EditNode addCell(EditNode cell)
    {
        if (cells == null)
        {
            cells = new ArrayList<EditNode>();
        }
        cells.add(cell);
        return cell;
    }

    /**
     * 移除一行数据
     * @param rowIndex 参数
     */
    public void removeRow(int rowIndex)
    {
        if (rowIndex < rowNum && rowIndex >= 0)
        {
            for (int i = 0; i < columnNum; i++)
            {
                cells.remove(rowIndex + rowNum * i - i);
            }
            rowNum--;
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * 表头的OID集合
     * @return 结果
     */
    public String[] getHeaderOids()
    {
        int size = headers.size();
        String[] oids = new String[size];
        for (int i = 0; i < size; i++)
        {
            oids[i] = headers.get(i).getSymbol().getNodeOid();
        }
        return oids;
    }

    /**
     * @return 结果
     */
    public String[] getAllOids()
    {
        String[] temp = new String[cells.size()];
        for (int i = 0; i < cells.size(); i++)
        {
            temp[i] = cells.get(i).getOid();
        }
        return temp;
    }

    /**
     * 获取指定位置的单元格
     * @param row 行数。从0开始
     * @param column 列数。从0开始
     * @return 结果
     */
    public EditNode getCell(int row, int column)
    {
        if (row >= 0 && column >= 0 && row < rowNum && column < columnNum)
        {
            return cells.get(row + column * rowNum);
        }
        return null;
    }

    /**
     * 以String的方式获取指定位置的单元格
     * @param row 行数。从0开始
     * @param column 列数。从0开始
     * @return 结果
     */
    public String getCellString(int row, int column)
    {
        String retval = null;
        EditNode node = getCell(row, column);
        if (node != null)
        {
            retval = node.getEditValue();
        }
        return retval;
    }

    /**
     * 以Integer的方式获取指定位置的单元格
     * @param row 行数。从0开始
     * @param column 列数。从0开始
     * @return 结果
     */
    public Integer getCellInteger(int row, int column)
    {
        Integer retval = null;
        EditNode node = getCell(row, column);
        if (node != null)
        {
            try
            {
                retval = Integer.valueOf(node.getEditValue());
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        return retval;
    }

    /**
     * 获取一行数据
     * @param rowIndex 参数
     * @return 结果
     */
    public ArrayList<EditNode> getRow(int rowIndex)
    {
        ArrayList<EditNode> result = new ArrayList<EditNode>();
        if (rowIndex < rowNum && rowIndex >= 0)
        {
            for (int i = 0; i < columnNum; i++)
            {
                result.add(this.getCell(rowIndex, i));
            }
        }
        return result;
    }

    /**
     * 获取一列数据
     * @param columnIndex 参数
     * @return 结果
     */
    public ArrayList<EditNode> getColumn(int columnIndex)
    {
        ArrayList<EditNode> result = new ArrayList<EditNode>();
        if (columnIndex < columnNum && columnIndex >= 0)
        {
            for (int i = 0; i < rowNum; i++)
            {
                result.add(this.getCell(i, columnIndex));
            }
        }
        return result;
    }

    /**
     * 获取指定位置的单元格
     * @param row 行数。从0开始
     * @param columnStr 列名称
     * @return 结果
     */
    public EditNode getCell(int row, String columnStr)
    {
        int column = this.getColumn(columnStr);
        if (row < rowNum && column < columnNum && row >= 0 && column >= 0)
        {
            return cells.get(row + column * rowNum);
        }
        return null;
    }

    /**
     * 根据列名称获取列的位置
     * @param name 名称
     * @return -1，如果没有该列
     */
    public int getColumn(String name)
    {
        for (int i = 0; i < headers.size(); i++)
        {
            if (headers.get(i).getSymbol().getNodeName().equals(name))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param row 列
     * @return 结果
     */
    public String getRowIndex(int row)
    {
        EditNode node = this.getCell(row, 0);
        String exp = node.getOid();
        exp = exp.substring(headers.get(0).getSymbol().getNodeOid().length() + 1);
        return exp;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="填充表格">
    /**
     * 根据索引的OID集合，计算全部单元格的OID
     * @param oids 索引的OID集合
     * @param preOid 索引的原始OID
     */
    public void fillTableCellOid(ArrayList<String> oids, String preOid)
    {
        int length = preOid.length();
        ArrayList<String> trails = new ArrayList<String>();
        for (int i = 0; i < oids.size(); i++)
        {
            String temp = oids.get(i);
            temp = temp.substring(length);
            trails.add(temp);
        }
        this.rowNum = oids.size();
        for (int i = 0; i < headers.size(); i++)
        {
            EditNode tHeader = headers.get(i);
            String tHeaderOid = tHeader.getSymbol().getNodeOid();
            for (int j = 0; j < trails.size(); j++)
            {
                EditNode tCell = new EditNode();
                tCell.setOid(tHeaderOid + trails.get(j));
                tCell.setCoding(tHeader.getCoding());
                tCell.setHandle(tHeader.getHandle());
                tCell.setSymbol(tHeader.getSymbol());
                cells.add(tCell);
            }
        }
    }

    /**
     * 计算完单元格后，填充这些单元格的值
     * @param bindings 参数
     */
    public void fillTableCellValue(Map<String, VariableBinding> bindings)
    {
        for (int i = 0; i < getCells().size(); i++)
        {
            EditNode tCell = getCells().get(i);
            String tOid = tCell.getOid();
            VariableBinding tBind = bindings.get(tOid);
            tCell.setValue(tBind);
        }
    }

    /**
     * 当一行数据的值全部为空时，移除这一行
     */
    public void checkResultTable()
    {
        for (int i = 0; i < rowNum;)
        {
            boolean delete = true;
            for (int j = 0; j < columnNum; j++)
            {
                if (this.getCell(i, j).getValue() != null)
                {
                    delete = false;
                    break;
                }
            }
            if (delete)
            {
                this.removeRow(i);
            }
            else
            {
                i++;
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="自动生成的数据访问">
    /**
     * @return the tableName
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    /**
     * @return the index
     */
    public String getIndex()
    {
        return index;
    }

    /**
     * @return the headers
     */
    public ArrayList<EditNode> getHeaders()
    {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(ArrayList<EditNode> headers)
    {
        this.headers = headers;
    }

    /**
     * @return the cells
     */
    public ArrayList<EditNode> getCells()
    {
        return cells;
    }

    /**
     * @param cells the cells to set
     */
    public void setCells(ArrayList<EditNode> cells)
    {
        this.cells = cells;
    }

    /**
     * @return the rowNum
     */
    public int getRowNum()
    {
        return rowNum;
    }

    /**
     * @param rowNum the rowNum to set
     */
    public void setRowNum(int rowNum)
    {
        this.rowNum = rowNum;
    }

    /**
     * @return the columnNum
     */
    public int getColumnNum()
    {
        return columnNum;
    }

    /**
     * @param columnNum the columnNum to set
     */
    public void setColumnNum(int columnNum)
    {
        this.columnNum = columnNum;
    }

    /**
     * @return the coding
     */
    public String getCoding()
    {
        return coding;
    }

    /**
     * @param coding the coding to set
     */
    public void setCoding(String coding)
    {
        this.coding = coding;
    }

    /**
     * @return the handle
     */
    public AbsSnmpValueTrans getHandle()
    {
        return handle;
    }

    /**
     * @param handle the handle to set
     */
    public void setHandle(AbsSnmpValueTrans handle)
    {
        this.handle = handle;
    }

    // </editor-fold>

    public void reset()
    {
        this.rowNum = 0;
        if (cells != null)
        {
            cells.clear();
        }
    }

    public SnmpTable copy()
    {
        SnmpTable copy = new SnmpTable(this.tableName);
        copy.setCoding(this.coding);
        for (int i = 0; i < headers.size(); i++)
        {
            EditNode header = headers.get(i);
            if (header != null)
            {
                copy.addColumn((EditNode) header.clone());
            }
        }
        copy.setIndex(index);
        return copy;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        int[] headSpan = new int[columnNum];
        for (int i = 0; i < columnNum; i++)
        {
            EditNode header = headers.get(i);
            String headStr = header.getSymbol() == null ? header.getOid() : header.getSymbol().getNodeName();
            int tempSpan = headStr.length() + 20;
            headSpan[i] = tempSpan;
            String s = String.format("%1$-" + tempSpan + "s", headStr);
            sb.append(s);
        }
        sb.append("\r\n");
        for (int i = 0; i < rowNum; i++)
        {
            for (int j = 0; j < columnNum; j++)
            {
                EditNode temp = this.getCell(i, j);
                String s = null;
                int span = headSpan[j];
                if (temp != null && temp.getValue() != null)
                {
                    s = String.format("%1$-" + span + "s", temp.getEditValue());
                }
                else
                {
                    s = String.format("%1$-" + span + "s", "null");
                }
                sb.append(s);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
