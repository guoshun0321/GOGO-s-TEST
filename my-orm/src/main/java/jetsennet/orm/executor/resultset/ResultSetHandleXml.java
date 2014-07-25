package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import jetsennet.orm.util.UncheckedOrmException;

public class ResultSetHandleXml extends AbsResultSetHandle<String>
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

    public ResultSetHandleXml()
    {
        this(false, 0, 0);
    }

    public ResultSetHandleXml(boolean isPage, int count, int cur)
    {
        this.isPage = isPage;
        this.count = count;
        this.cur = cur;
    }

    public String handle(ResultSet rs)
    {
        SimpleXmlBuilder retval = new SimpleXmlBuilder(isPage, count, cur);

        try
        {
            // 获取列数  
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 遍历ResultSet中的每条数据  
            while (rs.next())
            {
                SimpleXmlBuilder.RecordEntry record = retval.newRecord();
                // 遍历每一列  
                for (int i = 1; i <= columnCount; i++)
                {
                    String key = metaData.getColumnLabel(i);
                    String value = this.result2String(rs.getObject(key));
                    record.add(key, value);
                }
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }

        return retval.build();
    }

}
