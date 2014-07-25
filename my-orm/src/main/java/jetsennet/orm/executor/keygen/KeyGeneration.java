package jetsennet.orm.executor.keygen;

import java.util.List;

import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.session.SessionBase;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.util.UncheckedOrmException;

/**
 * 默认的主键生成器。
 * 通过向NET_SEQUENCE插入或更新数据来获得最新主键。
 * 在NET_SEQUENCE中不存在该表数据时可能出现主键重复的情况，请确保主键的存在。
 * 或者可以通过重试的方式进行主键的二次修改。
 * 
 * @author 郭祥
 */
public class KeyGeneration implements IKeyGeneration<Long>
{

    protected String dbTableName = "IDENTIFIER_TABLE";
    protected String dbNameColumn = "TABLE_NAME";
    protected String dbValueColumn = "SERIALIZE_VALUE";

    public Long genKey(SessionBase session, String tableName)
    {
        Long[] temp = genKeys(session, tableName, 1);
        return temp[0];
    }

    public Long[] genKeys(SessionBase session, String tableName, int num)
    {

        FieldInfo field = session.getFirstkey(tableName);
        return this.genKeys(session, tableName, field.getName(), num);
    }

    @Override
    public Long[] genKeys(SessionBase session, String tableName, String fieldName, int num)
    {
        final Long[] retval = new Long[num];
        long last = -1;
        boolean isSelf = false;
        try
        {
            isSelf = session.transBegin();
            String columnIdentifier = genIdentifier(tableName, fieldName);
            int updateRet = session.update(this.genUpdate(columnIdentifier, num));
            if (updateRet > 0)
            {
                List<Long> tempLst = session.query(this.genSelect(columnIdentifier), new RowsResultSetExtractor<Long>(Long.class));
                last = tempLst.get(0).longValue();
            }
            else
            {
                session.update(this.genInsert(columnIdentifier, num));
                last = num;
            }
            session.transCommit(isSelf);
        }
        catch (Exception ex)
        {
            session.transRollback(isSelf);
            throw new UncheckedOrmException(ex);
        }
        if (last > 0)
        {
            for (int i = num - 1; i >= 0; i--)
            {
                retval[i] = last--;
            }
        }
        return retval;
    }

    private final String genUpdate(String columnIdentifier, int num)
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("UPDATE ")
            .append(dbTableName)
            .append(" SET ")
            .append(dbValueColumn)
            .append(" = ")
            .append(dbValueColumn)
            .append("+")
            .append(num)
            .append(" WHERE ")
            .append(dbNameColumn)
            .append("='")
            .append(columnIdentifier)
            .append("'");
        return sb.toString();
    }

    private final String genSelect(String columnIdentifier)
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("SELECT ")
            .append(dbValueColumn)
            .append(" FROM ")
            .append(dbTableName)
            .append(" WHERE TABLE_NAME='")
            .append(columnIdentifier)
            .append("'");
        return sb.toString();
    }

    private final String genInsert(String columnIdentifier, int num)
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("INSERT INTO ")
            .append(dbTableName)
            .append(" (")
            .append(dbNameColumn)
            .append(",")
            .append(dbValueColumn)
            .append(") VALUES('")
            .append(columnIdentifier)
            .append("', ")
            .append(num)
            .append(")");
        return sb.toString();
    }

    protected String genIdentifier(String tableName, String fieldName)
    {
        return tableName + "." + fieldName;
    }

    public void setDbTableName(String dbTableName)
    {
        this.dbTableName = dbTableName;
    }

    public void setDbNameColumn(String dbNameColumn)
    {
        this.dbNameColumn = dbNameColumn;
    }

    public void setDbValueColumn(String dbValueColumn)
    {
        this.dbValueColumn = dbValueColumn;
    }

}
