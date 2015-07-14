package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将采集结果转换成字符串集合
 * 
 * @author 郭祥
 */
public class ResultSetHandleListString extends AbsResultSetHandle<List<String>>
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ResultSetHandleListString.class);

    public ResultSetHandleListString()
    {
    }

    public List<String> handle(ResultSet rs)
    {
        List<String> retval = new ArrayList<String>();
        try
        {
            // 获取列数  
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 遍历ResultSet中的每条数据  
            while (rs.next())
            {
                for (int i = 1; i <= columnCount; i++)
                {
                    retval.add(rs.getString(i));
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
