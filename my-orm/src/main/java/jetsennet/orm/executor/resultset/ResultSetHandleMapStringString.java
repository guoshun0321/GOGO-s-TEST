package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.util.UncheckedOrmException;

/**
 * 该结果处理器以HashMap的形式返回ResultSet的第一个数据
 * 
 * @author 郭祥
 *
 */
public class ResultSetHandleMapStringString extends AbsResultSetHandle<Map<String, String>>
{

    public Map<String, String> handle(ResultSet rs)
    {
        Map<String, String> retval = null;

        try
        {
            // 获取列数  
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 遍历ResultSet中的每条数据  
            if (rs.next())
            {
                retval = new HashMap<String, String>(columnCount);
                // 遍历每一列  
                for (int i = 1; i <= columnCount; i++)
                {
                    String key = metaData.getColumnLabel(i);
                    String value = this.result2String(rs.getObject(key));
                    retval.put(key, value);
                }
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }

        return retval;
    }
}
