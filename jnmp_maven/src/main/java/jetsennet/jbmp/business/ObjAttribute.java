package jetsennet.jbmp.business;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.util.SqlQueryUtil;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author liwei拼接对象属性
 */
public class ObjAttribute
{

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ObjAttribute t = new ObjAttribute();
        System.out.println(t.getKpiByObjId(1, 23));

    }

    /**
     * 根据对象id把对象属性和属性拼成父子关系的xml
     * @param objId 对象id
     * @return 拼接好的字符串
     * @throws Exception 异常
     */
    @Business
    public String getAttribByObjId(int objId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        List<Map<String, String>> subLst1 =
            new SqlQueryUtil()
                .getLst("SELECT A.OBJATTR_ID,A.OBJ_ID,A.ATTRIB_ID,A.OBJATTR_NAME,A.DATA_ENCODING,A.COLL_TIMESPAN,C.ALARM_ID,C.ALARM_NAME ,E.CLASS_ID FROM BMP_OBJATTRIB A "
                    + "LEFT JOIN BMP_ATTRIBALARM B ON A.OBJATTR_ID=B.OBJATTR_ID  LEFT JOIN BMP_ALARM C ON B.ALARM_ID=C.ALARM_ID LEFT JOIN BMP_ATTRIB2CLASS D ON A.ATTRIB_ID=D.ATTRIB_ID "
                    + " LEFT JOIN BMP_OBJECT E ON A.OBJ_ID =  E.OBJ_ID WHERE (A.ATTRIB_TYPE IN (103)) AND (A.OBJ_ID =" + objId + ") ");
        List<Map<String, String>> subLst2 =
            new SqlQueryUtil()
                .getLst("SELECT A.ATTRIB_ID,B.ATTRIB_NAME FROM BMP_OBJATTRIB A LEFT JOIN  BMP_ATTRIBUTE B ON A.ATTRIB_ID = B.ATTRIB_ID "
                    + " WHERE A.ATTRIB_TYPE IN (103) AND A.OBJ_ID = " + objId + " GROUP BY A.ATTRIB_ID,B.ATTRIB_NAME");
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><RecordSet>");
        if (subLst1 != null && subLst1.size() > 0)
        {
            for (Map<String, String> map : subLst1)
            {
                if (map == null || map.size() == 0)
                {
                    return "";
                }
                sb.append("<Record>");
                Iterator iterr = map.keySet().iterator();
                while (iterr.hasNext())
                {
                    String keyString = (String) iterr.next();
                    sb.append("<" + keyString + ">");
                    if (!map.get(keyString).equals("null"))
                    {
                        sb.append(map.get(keyString));
                    }
                    sb.append("</" + keyString + ">");
                }
                sb.append("</Record>");
            }
            for (Map<String, String> map2 : subLst2)
            {
                if (map2 == null || map2.size() == 0)
                {
                    return "";
                }
                sb.append("<Record>");
                sb.append("<OBJATTR_ID>").append(map2.get("ATTRIB_ID")).append("</OBJATTR_ID>");
                sb.append("<OBJ_ID>").append(objId).append("</OBJ_ID>");
                sb.append("<ATTRIB_ID>").append("0").append("</ATTRIB_ID>");
                sb.append("<OBJATTR_NAME>").append(map2.get("ATTRIB_NAME")).append("</OBJATTR_NAME>");
                sb.append("<DATA_ENCODING></DATA_ENCODING>");
                sb.append("<COLL_TIMESPAN></COLL_TIMESPAN>");
                sb.append("<ALARM_ID></ALARM_ID>");
                sb.append("<ALARM_NAME></ALARM_NAME>");
                sb.append("<CLASS_ID></CLASS_ID>");
                sb.append("</Record>");
            }
        }
        sb.append("</RecordSet>");
        return sb.toString();
    }

    public String getKpiByObjId(int objId, int groupId) throws Exception
    {
        List<Map<String, String>> subLst1 =
            new SqlQueryUtil()
                .getLst("SELECT A.OBJ_ID,A.OBJ_NAME,E.ATTRIB_ID,E.ATTRIB_NAME,D.OBJATTR_ID,D.OBJATTR_NAME FROM BMP_OBJECT A LEFT JOIN BMP_OBJATTRIB D ON D.OBJ_ID = A.OBJ_ID LEFT JOIN BMP_ATTRIBUTE E ON E.ATTRIB_ID = D.ATTRIB_ID WHERE A.OBJ_ID = "
                    + objId + " AND (D.ATTRIB_TYPE = 103 OR D.ATTRIB_TYPE = 0)");
        List<Map<String, String>> subLst2 =
            new SqlQueryUtil()
                .getLst("SELECT A.ATTRIB_ID,B.ATTRIB_NAME FROM BMP_OBJATTRIB A LEFT JOIN  BMP_ATTRIBUTE B ON A.ATTRIB_ID = B.ATTRIB_ID "
                    + " WHERE A.ATTRIB_TYPE IN (0, 103) AND A.OBJ_ID = " + objId + " GROUP BY A.ATTRIB_ID,B.ATTRIB_NAME");
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><RecordSet>");
        if (subLst1 != null && subLst1.size() > 0)
        {
            for (Map<String, String> map : subLst1)
            {
                if (map == null || map.size() == 0)
                {
                    return "";
                }
                sb.append("<Record>");
                Iterator iterr = map.keySet().iterator();
                while (iterr.hasNext())
                {
                    String keyString = (String) iterr.next();
                    sb.append("<" + keyString + ">");
                    if (!map.get(keyString).equals("null"))
                    {
                        sb.append(map.get(keyString));
                    }
                    sb.append("</" + keyString + ">");
                }
                sb.append("</Record>");
            }
            for (Map<String, String> map2 : subLst2)
            {
                if (map2 == null || map2.size() == 0)
                {
                    return "";
                }
                sb.append("<Record>");
                sb.append("<OBJATTR_ID>").append(map2.get("ATTRIB_ID")).append("</OBJATTR_ID>");
                sb.append("<OBJ_ID>").append(objId).append("</OBJ_ID>");
                sb.append("<ATTRIB_ID>").append("0").append("</ATTRIB_ID>");
                sb.append("<OBJATTR_NAME>").append(map2.get("ATTRIB_NAME")).append("</OBJATTR_NAME>");
                sb.append("<GROUP_ID></GROUP_ID>");
                sb.append("<GROUP_NAME></GROUP_NAME>");
                sb.append("<OBJ_NAME></OBJ_NAME>");
                sb.append("<ATTRIB_NAME></ATTRIB_NAME>");
                sb.append("</Record>");
            }
        }
        sb.append("</RecordSet>");
        return sb.toString();
    }

}
