package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.util.UncheckedOrmException;

/**
 * 该结果处理器以HashMap的形式返回ResultSet的数据
 * 
 * @author 郭祥
 *
 */
public class ResultSetHandleListMapStringString extends AbsResultSetHandle<List<Map<String, String>>>
{

    public ResultSetHandleListMapStringString()
    {
        // TODO Auto-generated constructor stub
    }

    public ResultSetHandleListMapStringString(int max)
    {
        this.max = 1;
    }

    public List<Map<String, String>> handle(ResultSet rs)
    {
        List<Map<String, String>> retval = new ArrayList<Map<String, String>>();

        try
        {
            // 获取列数  
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            int pos = 0;
            // 遍历ResultSet中的每条数据  
            while (rs.next())
            {
                if (pos >= offset)
                {
                    Map<String, String> temp = new HashMap<String, String>(columnCount);
                    // 遍历每一列  
                    for (int i = 1; i <= columnCount; i++)
                    {
                        String key = metaData.getColumnLabel(i);
                        String value = this.result2String(rs.getObject(key));
                        temp.put(key, value);
                    }
                    retval.add(temp);
                }
                if (retval.size() >= max)
                {
                    break;
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
