package jetsennet.orm.ddl;

import java.util.List;

import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;

public interface IDdl
{

    /**
     * 创建数据库表
     * 
     * @param table
     */
    public void create(TableInfo table);
    
    /**
     * 重建表。如果表存在，删除表后再建新表。
     * 
     * @param table
     */
    public void rebuild(TableInfo table);

    /**
     * 删除数据库表
     * 
     * @param tableName 表名
     */
    public void delete(String tableName);

    /**
     * 判断表是否存在
     */
    public boolean isExist(String tableName);

    /**
     * 获取数据库表信息
     * 
     * @param tableName
     * @return
     */
    public TableInfo getTableInfo(String tableName);

    /**
     * 列出以pre开头的用户表，如果pre为null，列出所有用户表
     * 
     * @param pre
     * @return
     */
    public List<String> listTable(String pre);

    /**
     * 新增列
     * 
     * @param table
     */
    public void addColumn(String tableName, FieldInfo field);

    /**
     * 删除列
     * 
     * @param columnName
     */
    public void deleteColumn(String tableName, String columnName);

}
