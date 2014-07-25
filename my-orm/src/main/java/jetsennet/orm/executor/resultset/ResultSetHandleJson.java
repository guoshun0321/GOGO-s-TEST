package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import jetsennet.orm.util.UncheckedOrmException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ResultSetHandleJson extends AbsResultSetHandle<String>
{

    /**
     * 是否page
     */
    private boolean isPage;
    /**
     * 总页数
     */
    private int count;
    /**
     * 当前位置
     */
    private int cur;
    /**
     * 记录
     */
    private static final String RECORDS = "Records";
    /**
     * 页面信息
     */
    private static final String INFO = "Info";
    /**
     * 总页数
     */
    private static final String COUNT = "Count";
    /**
     * 当前页码
     */
    private static final String CUR = "Cur";

    public ResultSetHandleJson()
    {
        this(false, 0, 0);
    }

    public ResultSetHandleJson(boolean isPage, int count, int cur)
    {
        this.isPage = isPage;
        this.count = count;
        this.cur = cur;
    }

    public String handle(ResultSet rs)
    {
        String retval = null;

        try
        {
            JSONObject ret = new JSONObject();

            // json数组
            JSONArray array = new JSONArray();

            // 获取列数  
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 遍历ResultSet中的每条数据  
            while (rs.next())
            {
                JSONObject jsonObj = new JSONObject();

                // 遍历每一列  
                for (int i = 1; i <= columnCount; i++)
                {
                    String key = metaData.getColumnLabel(i);
                    String value = this.result2String(rs.getObject(key));
                    jsonObj.put(key, value);
                }
                array.add(jsonObj);
            }

            ret.put(RECORDS, array);

            if (isPage)
            {
                JSONObject pageObj = new JSONObject();
                pageObj.put(COUNT, this.count);
                pageObj.put(CUR, this.cur);
                ret.put(INFO, pageObj);
            }

            retval = ret.toJSONString();
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }

        return retval;
    }
}
