/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.datacollect.collectorif.transmsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * 表格型数据
 * @author GuoXiang
 */
public class ColumnsMsg implements Serializable
{

    /**
     * 需要采集的表
     */
    private ArrayList<ColumnMsg> columns;
    private static final long serialVersionUID = -1L;

    /**
     * 构造函数
     */
    public ColumnsMsg()
    {
    }

    /**
     * @param tm 参数
     */
    public void addColumn(ColumnMsg tm)
    {
        if (columns == null)
        {
            columns = new ArrayList<ColumnMsg>();
        }
        columns.add(tm);
    }

    /**
     * 获取全部对象属性
     * @return 包含的对象属性，如果无对象属性则返回空List
     */
    public List<ObjAttribEntity> getObjAttribs()
    {
        List<ObjAttribEntity> retval = new ArrayList<ObjAttribEntity>();
        if (columns != null)
        {
            for (ColumnMsg column : columns)
            {
                ObjAttribEntity temp = column.getInput();
                if (temp != null)
                {
                    retval.add(temp);
                }
            }
        }
        return retval;
    }

    /**
     * 使用采集结果填充TransMsg
     * 
     * @param valMap
     */
    public void fillWithResult(Map<ObjAttribEntity, Object> valMap)
    {
        if (columns != null)
        {
            for (ColumnMsg column : columns)
            {
                column.fillWithResult(valMap);
            }
        }
    }

    /**
     * @return the columns
     */
    public ArrayList<ColumnMsg> getColumns()
    {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(ArrayList<ColumnMsg> columns)
    {
        this.columns = columns;
    }
}
