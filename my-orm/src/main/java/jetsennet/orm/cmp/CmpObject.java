package jetsennet.orm.cmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.UncheckedOrmException;

public class CmpObject
{

    /**
     * 对象名
     */
    private String objName;
    /**
     * 主表
     */
    private TableInfo mainTable;
    /**
     * 所有表
     */
    private List<TableInfo> tables;
    /**
     * 表名到表的映射
     */
    private Map<String, TableInfo> tableMap;
    /**
     * 表关系
     */
    private List<CmpFieldRel> rels;

    public CmpObject(String objName)
    {
        this.objName = objName;
        this.tables = new ArrayList<TableInfo>(10);
        this.tableMap = new HashMap<String, TableInfo>(10);
        this.rels = new ArrayList<CmpFieldRel>(20);
    }

    public void addMainTable(TableInfo table)
    {
        this.addTable(table);
        this.mainTable = table;
    }

    public void addTable(TableInfo table)
    {
        if (!tableMap.containsKey(table))
        {
            this.tables.add(table);
            this.tableMap.put(table.getTableName(), table);
        }
        else
        {
            throw new UncheckedOrmException("表已经存在：" + table.getTableName());
        }
    }

    public void addRels(String pTable, String pField, String sTable, String sField)
    {
        this.rels.add(new CmpFieldRel(pTable, pField, sTable, sField));
    }

    /**
     * 获取父关系
     * 
     * @return
     */
    public List<CmpFieldRel> getParentRel(String tableName)
    {
        List<CmpFieldRel> retval = new ArrayList<CmpFieldRel>();
        for (CmpFieldRel rel : rels)
        {
            if (rel.sTable.equals(tableName) && !rel.pTable.equals(tableName))
            {
                retval.add(rel);
            }
        }
        return retval;
    }

    public List<CmpFieldRel> getSubRels(String tableName)
    {
        List<CmpFieldRel> retval = new ArrayList<CmpFieldRel>();
        for (CmpFieldRel rel : this.rels)
        {
            if (rel.pTable.equals(tableName) && !rel.sTable.equals(tableName))
            {
                retval.add(rel);
            }
        }
        return retval;
    }

    /**
     * 检查表是否自循环
     * 
     * @param tableName
     * @return
     */
    public String isSelfLoop(String tableName)
    {
        for (CmpFieldRel rel : this.rels)
        {
            if (rel.pTable.equals(tableName) && rel.sTable.equals(tableName))
            {
                return rel.sField;
            }
        }
        return null;
    }

    public String getMainTableName()
    {
        return this.mainTable.getTableName();
    }

    public String getObjName()
    {
        return objName;
    }

    public TableInfo getMainTable()
    {
        return mainTable;
    }

    public List<TableInfo> getTables()
    {
        return tables;
    }

    public Map<String, TableInfo> getTableMap()
    {
        return tableMap;
    }

    public List<CmpFieldRel> getRels()
    {
        return rels;
    }

}
