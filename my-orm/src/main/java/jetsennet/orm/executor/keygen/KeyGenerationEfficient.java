package jetsennet.orm.executor.keygen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jetsennet.orm.executor.resultset.AbsResultSetHandle;
import jetsennet.orm.session.SessionBase;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyGenerationEfficient extends KeyGeneration
{

    /**主键种子*/
    private static Map<String, Long> longKeyMap = new HashMap<String, Long>();
    /**主键上限*/
    private static Map<String, Long> longKeyBoundMap = new HashMap<String, Long>();
    /**每次取主键的个数*/
    private static int longKeyInterval = 1000;
    /**
     * version column
     */
    private String vercolumn = "VER";
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(KeyGenerationEfficient.class);

    public KeyGenerationEfficient()
    {
        dbTableName = "EFFICIENT_PKGENERATOR";
    }

    @Override
    public synchronized Long[] genKeys(SessionBase session, String tableName, String fieldName, int num)
    {
        Long[] retval = new Long[num];
        try
        {
            String columnIdentifier = genIdentifier(tableName, fieldName);

            long longKey = longKeyMap.containsKey(columnIdentifier) ? longKeyMap.get(columnIdentifier).longValue() : 0l;
            long longKeyBound = longKeyBoundMap.containsKey(columnIdentifier) ? longKeyBoundMap.get(columnIdentifier).longValue() : 0l;

            long expect = longKey + num;
            if (expect > longKeyBound)
            {
                long step = num + longKeyInterval;
                genIntKeyInterval(session, columnIdentifier, step);
                longKeyBound = longKeyBound + step;
            }
            for (int i = 0; i < num; i++)
            {
                longKey++;
                retval[i] = longKey;
            }

            longKeyMap.put(columnIdentifier, retval[retval.length - 1]);
            longKeyBoundMap.put(columnIdentifier, longKeyBound);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    private void genIntKeyInterval(SessionBase session, String columnIdentifier, long step) throws SQLException
    {
        String sql =
            new StringBuilder("SELECT ").append(dbValueColumn)
                .append(", ")
                .append(vercolumn)
                .append(" FROM ")
                .append(dbTableName)
                .append(" WHERE ")
                .append(dbNameColumn)
                .append(" = '")
                .append(columnIdentifier)
                .append('\'')
                .toString();
        int count = 0;
        int errorNum = 0;
        do
        {
            Object[] arrs = session.query(sql, new AbsResultSetHandle<Object[]>()
            {
                @Override
                public Object[] handle(ResultSet rs) throws Exception
                {
                    Object[] datas = null;
                    while (rs.next())
                    {
                        datas = new Object[2];
                        datas[0] = rs.getLong(1);
                        datas[1] = rs.getInt(2);
                        break;
                    }
                    return datas;
                }
            });
            if (arrs != null && arrs.length > 0)
            {//如果种子表里已经有记录
                long keyBase = (Long) arrs[0];
                int version = (Integer) arrs[1];
                long nextValue = keyBase + step;
                int nextVersion = version + 1;
                StringBuilder updateSql = new StringBuilder("UPDATE ");
                updateSql.append(dbTableName).append(" SET ").append(dbValueColumn).append("=").append(nextValue).append(", ");
                updateSql.append(vercolumn).append("=").append(nextVersion).append(" WHERE ");
                updateSql.append(dbNameColumn).append(" = '").append(columnIdentifier).append("' AND ").append(vercolumn).append("=").append(version);
                count = session.update(updateSql.toString());
            }
            else
            {
                StringBuilder insertsql = new StringBuilder("INSERT INTO ");
                insertsql.append(dbTableName);
                insertsql.append('(').append(dbNameColumn).append(", ").append(dbValueColumn).append(", ").append(vercolumn).append(") VALUES('");
                insertsql.append(columnIdentifier).append("',").append(step).append(", 0)");
                try
                {
                    count = session.update(insertsql.toString());
                }
                catch (Exception e)
                {
                    // 3次尝试，避免陷入死循环
                    errorNum++;
                    if (errorNum > 3)
                    {
                        throw new UncheckedOrmException("表竞争激烈：" + dbTableName, e);
                    }
                }
            }
        }
        while (count != 1);
    }

}
