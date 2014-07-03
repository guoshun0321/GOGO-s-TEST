/************************************************************************
 * 日 期：2011-11-9 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 将SQL查询结果存入List<Map<String, String>>结构
 *        及将该结果转化为自定义XML格式等
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;

/**
 * @author ?
 */
public class SqlQueryUtil
{
    /**
     * 构造方法
     */
    public SqlQueryUtil()
    {
    }

    /**
     * 将List<Map<String,String>>转为xml字符串
     * @param lst 要转化的List<Map<String, String>>结构
     * @return 返回自定义XML结果，如下： <DataSource> <DataTable> <FIELD_EXAMPLE>fieldValue</FIELD_EXAMPLE> <FIELD_EXAMPLE2>fieldValue2</FIELD_EXAMPLE2>
     *         </DataTable> <DataTable> <FIELD_EXAMPLE3>fieldValue3</FIELD_EXAMPLE3> <FIELD_EXAMPLE4>fieldValue4</FIELD_EXAMPLE4> </DataTable>
     *         </DataSource>
     * @throws Exception 异常
     */
    public String listToXmlString(List<Map<String, String>> lst) throws Exception
    {
        String result = "<DataSource>";

        if (lst != null && lst.size() > 0)
        {
            for (Map<String, String> map : lst)
            {
                StringBuilder str = new StringBuilder();
                str.append("<DataTable>");
                Iterator it = map.keySet().iterator();
                while (it.hasNext())
                {
                    String key = it.next().toString();
                    str.append("<").append(key).append(">").append(map.get(key)).append("</").append(key).append(">");
                }
                str.append("</DataTable>");

                result += str.toString();
            }
        }

        result += "</DataSource>";
        return result;
    }

    /**
     * 将SQL查询结果转为List<Map<String, String>>结构
     * @param sqlStr SQL语句
     * @return 返回List<Map<String,String>>结构数据
     * @throws Exception 异常
     */
    public List<Map<String, String>> getLst(String sqlStr) throws Exception
    {

        final List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        DefaultDal.read(sqlStr, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int column = rsmd.getColumnCount(); // 取得列数
                    while (rs.next())
                    {
                        Map<String, String> map = new HashMap<String, String>();
                        for (int j = 1; j <= column; j++)
                        {
                            map.put(rsmd.getColumnName(j), rs.getObject(j) == null ? "null" : rs.getObject(j).toString());
                        }
                        result.add(map);
                    }
                }
            }
        });
        return result;
    }

    /**
     * 查询第一条结果
     * @param sqlStr SQL语句
     * @return 返回Map<String,String>结构数据
     * @throws Exception 异常
     */
    public Map<String, String> getFirstLst(String sqlStr) throws Exception
    {
        final Map<String, String> result = new HashMap<String, String>();

        DefaultDal.read(sqlStr, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs != null)
                {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int column = rsmd.getColumnCount(); // 取得列数
                    int count = 0;
                    while (rs.next())
                    {
                        if (count == 0)
                        {
                            for (int j = 1; j <= column; j++)
                            {
                                result.put(rsmd.getColumnName(j), rs.getObject(j).toString());
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
        });
        return result;
    }

}
