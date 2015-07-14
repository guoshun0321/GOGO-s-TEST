package jetsennet.orm.executor.keygen;

import java.util.List;

import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.session.SessionBase;

/**
 * 单机环境下线程安全
 * 
 * @author 郭祥
 */
public class KeyGenerationIncrement extends KeyGeneration
{

    @Override
    public synchronized Long[] genKeys(SessionBase session, String tableName, String fieldName, int num)
    {
        Long[] retval = new Long[num];
        String sql = "SELECT MAX(" + fieldName + ") FROM " + tableName;

        List<Long> temp = session.query(sql, new RowsResultSetExtractor<Long>(Long.class));

        long last = num;
        if (!temp.isEmpty())
        {
            last = temp.get(0) + num;
        }

        for (int i = num - 1; i >= 0; i--)
        {
            retval[i] = last--;
        }
        return retval;
    }

}
