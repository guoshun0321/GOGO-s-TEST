/************************************************************************
日 期：2011-12-5
作 者: 余灵
版 本：v1.3
描 述: 
历 史： 
 ************************************************************************/
package jetsennet.jbmp.business;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.AlarmDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.util.ArrayUtils;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author 余灵
 */
public class InsObjAttr
{
    private ConnectionInfo nmpConnectionInfo;
    private ISqlExecutor sqlExecutor;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(InsObjAttr.class);

    /**
     * 构造函数
     */
    public InsObjAttr()
    {
        nmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("nmp_driver"), DbConfig.getProperty("nmp_dburl"), DbConfig.getProperty("nmp_dbuser"), DbConfig
                .getProperty("nmp_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
    }

    /**
     * 获取实例化属性后的结果
     * @param objId 对象ID
     * @param attrIds 要实例化的属性ID，逗号连接
     * @return 结果
     * @throws Exception 异常
     */
    public String instanceAttribute(int objId, String attrIds) throws Exception
    {
        String result = "";

        try
        {
            String[] ids = attrIds.split(",");
            ArrayList<String> attrIdList = ArrayUtils.stringToStringArrayList(ids);

            List<Object> objectList = ClassWrapper.wrapTrans(MObject.class).instanceAttrib(objId, attrIdList);

            StringBuilder sb = new StringBuilder();
            sb.append("<RecordSet>");

            if (objectList != null)
            {
                int n = 0;
                for (Object object : objectList)
                {
                    if (object instanceof ObjAttribEntity[])
                    {
                        ObjAttribEntity[] objAttribArray = (ObjAttribEntity[]) object;
                        if (objAttribArray != null && objAttribArray.length > 0)
                        {
                            for (int i = 0; i < objAttribArray.length; i++)
                            {
                                n++;

                                ObjAttribEntity entity = objAttribArray[i];
                                sb.append("<Record>");
                                sb.append("<OBJATTR_ID>");
                                sb.append(n);
                                sb.append("</OBJATTR_ID>");
                                sb.append("<OBJ_ID>");
                                sb.append(entity.getObjId());
                                sb.append("</OBJ_ID>");
                                sb.append("<ATTRIB_ID>");
                                sb.append(entity.getAttribId());
                                sb.append("</ATTRIB_ID>");
                                sb.append("<ATTRIB_VALUE>");
                                sb.append(entity.getAttribValue());
                                sb.append("</ATTRIB_VALUE>");
                                sb.append("<ATTRIB_PARAM>");
                                sb.append(entity.getAttribParam() == null ? "" : entity.getAttribParam());
                                sb.append("</ATTRIB_PARAM>");
                                sb.append("<OBJATTR_NAME>");
                                sb.append(entity.getObjattrName() == null ? "" : entity.getObjattrName());
                                sb.append("</OBJATTR_NAME>");
                                sb.append("<DATA_ENCODING>");
                                sb.append(entity.getDataEncoding());
                                sb.append("</DATA_ENCODING>");
                                sb.append("<ATTRIB_TYPE>");
                                sb.append(entity.getAttribType());
                                sb.append("</ATTRIB_TYPE>");
                                sb.append("<COLL_TIMESPAN>");
                                sb.append(entity.getCollTimespan());
                                sb.append("</COLL_TIMESPAN>");
                                sb.append("<IS_VISIBLE>");
                                sb.append(entity.getIsVisible());
                                sb.append("</IS_VISIBLE>");
                                sb.append("<FIELD_1>");
                                sb.append(entity.getField1());
                                sb.append("</FIELD_1>");
                                sb.append("</Record>");
                            }
                        }
                    }
                    else if (object instanceof String)
                    {
                        logger.error("属性实例化出错：" + object);
                    }
                    else
                    {
                        sb.append("<Record>");
                        sb.append("</Record>");
                    }
                }
            }

            sb.append("</RecordSet>");
            result = sb.toString();
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return result;
    }

    /**
     * 添加指定的对象属性 如果NMP_INDEXALARM中存在该分类与该属性关联的告警，则插入新对象属性与该告警的关联关系到BMP_ATTRIBALARM
     * @param classId 对象属性所属父对象的分类ID
     * @param objAttrXml 要添加的对象属性xml
     * @throws Exception 异常
     */
    public void insertObjAttrib(int classId, String objAttrXml) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();

        try
        {
            exec.transBegin();
            Document doc = DocumentHelper.parseText(objAttrXml);

            if (doc != null)
            {
                Element root = doc.getRootElement();
                List<Element> child = root.elements("BMP_OBJATTRIB");
                if (child != null && child.size() > 0)
                {
                    AlarmDal adal = new AlarmDal();
                    for (Element e : child)
                    {
                        int newId = ClassWrapper.wrap(ObjAttrib.class).addObjAttrib(e.asXML());
                        HashMap<String, String> map = SerializerUtil.deserialize(e.asXML(), "");
                        String attrId = map.get("ATTRIB_ID");

                        int alarmId = adal.copyAlarm(Integer.valueOf(attrId));
                        exec.executeNonQuery("INSERT INTO BMP_ATTRIBALARM VALUES (" + newId + "," + alarmId + ")");
                    }
                }
            }

            exec.transCommit();
        }
        catch (Exception ex)
        {
            exec.transRollback();
            throw ex;
        }
    }

    /**
     * 添加BMP_ATTRIBALARM
     * @param objAttrXml
     * @throws Exception
     */
    public void insertAttrib(String objAttrXml) throws Exception
    {
        try
        {
            String objattr_Id = objAttrXml.split(",")[0];
            String alarmId = objAttrXml.split(",")[1];
            sqlExecutor.transBegin();
            if (!StringUtil.isNullOrEmpty(alarmId) && !StringUtil.isNullOrEmpty(objattr_Id))
            {
                sqlExecutor.executeNonQuery("INSERT INTO BMP_ATTRIBALARM VALUES (" + objattr_Id + "," + alarmId + ")");
            }
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

}
