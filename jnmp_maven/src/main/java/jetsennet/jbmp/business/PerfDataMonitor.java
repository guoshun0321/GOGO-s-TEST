/************************************************************************
 * 日 期：2011-11-24 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 性能数据查询 
 * 历 史：2011-11-29 添加getObjAttrValueByObjAndClass方法；
 ************************************************************************/
package jetsennet.jbmp.business;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.business.CollDataQuery.QueryParam;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.entity.QueryResult;
import jetsennet.jbmp.entity.ValueTableEntity;
import jetsennet.jbmp.util.ArrayUtils;
import jetsennet.jbmp.util.SqlQueryUtil;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.StringUtil;

/**
 * @author 余灵
 */
public class PerfDataMonitor
{

    private ConnectionInfo nmpConnectionInfo;
    private ISqlExecutor sqlExecutor;

    /**
     * 构造函数
     */
    public PerfDataMonitor()
    {
        nmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("nmp_driver"),
                DbConfig.getProperty("nmp_dburl"),
                DbConfig.getProperty("nmp_dbuser"),
                DbConfig.getProperty("nmp_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
    }

    /**
     * 获取单个对象属性的性能数据
     * @param objId 对象ID
     * @param objattrId 对象属性ID
     * @param attrType 类型
     * @param fetchSize 获取性能数据的条数
     * @return 性能数据XML
     * @throws Exception 异常
     */
    public String getPerfDataByObjAttr(int objId, int objattrId, int attrType, int fetchSize) throws Exception
    {
        // 定义一个Document作为返回结果
        Document resultDoc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource></DataSource>");

        // 调用接口获取性能数据
        Map<Integer, List<QueryResult>> values = CollDataQuery.getInstance().query(objId, fetchSize, new int[] { objattrId });

        // 组装数据
        if (values != null && values.size() > 0)
        {
            Element ele = resultDoc.getRootElement().addElement("Data");
            ele.addAttribute("OBJATTR_ID", String.valueOf(objattrId));

            List<QueryResult> resultLst = values.get(objattrId);

            if (resultLst != null && resultLst.size() > 0)
            {
                List<String> arr = new ArrayList<String>();
                arr.add(String.valueOf(objattrId));
                List<ValueTableEntity> valueTables = getValueTableByObjAttrIds(arr);

                // 循环读取每个QueryResult对象，并转化为指定XML格式
                ele.appendContent(queryResultListToXml(resultLst,
                    String.valueOf(objattrId),
                    "",
                    String.valueOf(fetchSize),
                    String.valueOf(attrType),
                    valueTables).getRootElement());
            }

        }

        return resultDoc.asXML();
    }

    /**
     * 获取某对象的某属性的性能数据，可能为多个对象属性
     * @param objId 对象ID
     * @param attrId 属性ID
     * @param fetchSize 获取性能数据的条数
     * @return 性能数据XML
     * @throws Exception 异常
     */
    public String getPerfDataByObjAndAttrib(int objId, int attrId, int fetchSize) throws Exception
    {
        // 定义一个Document作为返回结果
        Document result_doc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource></DataSource>");

        // 先查询该对象该属性下所有的对象属性
        List<ObjAttribEntity> lst = ClassWrapper.wrapTrans(ObjAttribDal.class).getByObjIdAndAttribId(objId, attrId, new Integer[] { 1 });

        if (lst != null && lst.size() > 0)
        {
            List<QueryParam> para = new ArrayList<QueryParam>();

            // 对所有对象属性组装QueryParam,并保存到List
            for (ObjAttribEntity entity : lst)
            {
                QueryParam qp = new QueryParam();
                qp.setObjId(objId);
                qp.setObjAttrId(entity.getObjAttrId());
                qp.setAttrType(entity.getAttribType());
                qp.setFetchSize(fetchSize);

                para.add(qp);
            }

            // 调用接口取性能数据
            QueryParam[] arr = para.toArray(new QueryParam[para.size()]);
            Map<Integer, List<QueryResult>> values = CollDataQuery.getInstance().query(Integer.valueOf(objId), arr);

            // 根据结果组装XML
            if (values != null && values.size() > 0)
            {
                // 获取该属性枚举信息
                List<String> attrIdArr = new ArrayList<String>();
                attrIdArr.add(String.valueOf(attrId));
                List<ValueTableEntity> valueTables = getValueTableByAttrIds(attrIdArr);

                // 组装
                for (ObjAttribEntity entity : lst)
                {
                    String objattrId = String.valueOf(entity.getObjAttrId());
                    String objattrName = String.valueOf(entity.getAttribType());

                    Element ele = result_doc.getRootElement().addElement("Data");
                    ele.addAttribute("OBJATTR_ID", objattrId);
                    ele.addAttribute("OBJATTR_NAME", objattrName);

                    List<QueryResult> resultLst = values.get(Integer.valueOf(objattrId));

                    // 循环读取每个QueryResult对象，并转化为指定XML格式
                    ele.appendContent(queryResultListToXml(resultLst,
                        objattrId,
                        objattrName,
                        String.valueOf(fetchSize),
                        String.valueOf(entity.getAttribType()),
                        valueTables).getRootElement());
                }
            }

        }

        return result_doc.asXML();
    }

    /**
     * 获取某对象的某类别的自定义属性或者配置信息、表格数据的值
     * @param objId 对象ID
     * @param classId 分类ID
     * @param isFresh 是否刷新
     * @return 自定义属性或者配置信息、表格数据的值XML
     * @throws Exception 异常
     */
    public String getObjAttrValueByObjAndClass(int objId, int classId, boolean isFresh) throws Exception
    {
        // 定义一个Document作为返回结果
        Document result_doc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource></DataSource>");

        // 查询该对象该分类下的所有对象属性
        List<Map<String, String>> lst =
            new SqlQueryUtil().getLst("SELECT A.OBJATTR_ID,A.OBJATTR_NAME,A.ATTRIB_TYPE,B.VALUE_TYPE,B.DATA_UNIT FROM BMP_OBJATTRIB A "
                + "LEFT JOIN BMP_ATTRIBUTE B ON A.ATTRIB_ID=B.ATTRIB_ID LEFT JOIN BMP_ATTRIB2CLASS C ON B.ATTRIB_ID=C.ATTRIB_ID "
                + "LEFT JOIN BMP_ATTRIBCLASS D ON C.CLASS_ID=D.CLASS_ID WHERE A.IS_VISIBLE=1 AND A.OBJ_ID=" + objId + " AND D.CLASS_ID=" + classId);

        if (lst != null && lst.size() > 0)
        {
            List<String> valueArr = new ArrayList<String>();
            List<Integer> objattrIdLst = new ArrayList<Integer>();
            for (Map<String, String> map : lst)
            {
                objattrIdLst.add(Integer.valueOf(map.get("OBJATTR_ID")));
                if (!StringUtil.isNullOrEmpty(map.get("VALUE_TYPE")) && !"0".equals(map.get("VALUE_TYPE")))
                {
                    valueArr.add(map.get("VALUE_TYPE"));
                }
            }

            // 查询枚举信息
            List<ValueTableEntity> valueTables = new ArrayList<ValueTableEntity>();
            if (valueArr != null && valueArr.size() > 0)
            {
                String valueTypes = StringUtils.join(valueArr, ',');
                DefaultDal<ValueTableEntity> dal = new DefaultDal<ValueTableEntity>(ValueTableEntity.class);

                valueTables = dal.getLst(new SqlCondition("VALUE_TYPE", valueTypes, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric));
            }

            // 调用接口
            int[] arr = ArrayUtils.listToIntArray(objattrIdLst);
            Map<Integer, QueryResult> values = CollDataQuery.getInstance().query(objId, arr, isFresh);

            // 根据结果组装XML
            if (values != null && values.size() > 0)
            {
                for (Map<String, String> map : lst)
                {
                    String objattrId = map.get("OBJATTR_ID");
                    String objattrName = map.get("OBJATTR_NAME");
                    String valueType = map.get("VALUE_TYPE");
                    String dataUnit = map.get("DATA_UNIT");
                    String attribType = map.get("ATTRIB_TYPE");

                    Element ele = result_doc.getRootElement().addElement("DataTable");
                    Element eleId = ele.addElement("OBJATTR_ID");
                    eleId.setText(objattrId);
                    Element eleName = ele.addElement("OBJATTR_NAME");
                    eleName.setText(objattrName);
                    Element eleType = ele.addElement("VALUE_TYPE");
                    eleType.setText(valueType);
                    Element eleUnit = ele.addElement("DATA_UNIT");
                    eleUnit.setText(dataUnit);

                    Element eleValue = ele.addElement("STR_VALUE");
                    QueryResult resultLst = values.get(Integer.valueOf(objattrId));
                    String value = resultLst.getValue();

                    // 如果存在格式化信息，则格式化
                    if (!StringUtil.isNullOrEmpty(valueType) && !"0".equals(valueType) && valueTables.size() > 0)
                    {
                        List<ValueTableEntity> valueLst = new ArrayList<ValueTableEntity>();
                        for (ValueTableEntity entity : valueTables)
                        {
                            if (valueType.equals(String.valueOf(entity.getValueType())))
                            {
                                valueLst.add(entity);
                            }
                        }

                        eleValue.setText(formatObjAttribValue(value, attribType, valueLst));
                    }
                    else
                    {
                        if (StringUtil.isNullOrEmpty(value))
                        {
                            eleValue.setText("");
                        }
                        else
                        {
                            eleValue.setText(value);
                        }
                    }
                }
            }
        }

        return result_doc.asXML();
    }

    /**
     * 获取所有对象属性的性能数据 主要用于获取拓扑图中的性能数据和性能面板中的性能数据
     * @param objXml 要获取性能数据的各对象属性及参数的XML
     * @return 返回性能数据XML
     * @throws Exception 异常
     */
    public String getObjAttrsPerfData(String objXml) throws Exception
    {
        // 用于组装返回结果
        Document result_doc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource></DataSource>");

        // 根据传递过来的XML获取要采集哪些对象属性的值
        Document doc = DocumentHelper.parseText(objXml);

        // 根据传过来的objXml组装QueryParam
        if (doc != null)
        {

            // 获取所有的Item，每个Item下是一个对象的所有要获取性能数据的对象属性相关参数
            List list = doc.selectNodes("/PerfObjects/Item");
            if (list != null)
            {
                Iterator it = list.iterator();
                if (it != null)
                {
                    while (it.hasNext())
                    {
                        Element itemEle = (Element) it.next();
                        String objId = itemEle.attribute("objId").getValue();

                        // 获取该对象下的所有要获取性能数据的对象属性相关参数
                        List<Element> child = itemEle.elements("PerfObject");
                        if (child != null && child.size() > 0)
                        {
                            List<QueryParam> para = new ArrayList<QueryParam>();
                            List<String> valueObjAttrArr = new ArrayList<String>();

                            // 组装QueryParam
                            for (Element e : child)
                            {
                                String objAttrId = e.attribute("objAttrId").getValue();
                                String attrType = e.attribute("attrType").getValue();
                                String fetchSize = e.attribute("fetchSize").getValue();

                                valueObjAttrArr.add(objAttrId);

                                QueryParam qp = new QueryParam();
                                qp.setObjId(Integer.parseInt(objId));
                                qp.setObjAttrId(Integer.parseInt(objAttrId));
                                qp.setAttrType(Integer.parseInt(attrType));
                                qp.setFetchSize(Integer.parseInt(fetchSize));

                                para.add(qp);
                            }

                            // 调用接口获取性能数据
                            QueryParam[] arr = para.toArray(new QueryParam[para.size()]);
                            Map<Integer, List<QueryResult>> values = CollDataQuery.getInstance().query(Integer.valueOf(objId), arr);

                            // 组装结果
                            if (values != null && values.size() > 0)
                            {
                                // 查询要格式化值的对象属性
                                List<Map<String, String>> lst =
                                    new SqlQueryUtil().getLst("SELECT A.OBJATTR_ID,A.ATTRIB_TYPE,B.VALUE_TYPE FROM BMP_OBJATTRIB A LEFT JOIN BMP_ATTRIBUTE B ON A.ATTRIB_ID = B.ATTRIB_ID WHERE B.VALUE_TYPE > 0 AND B.VALUE_TYPE IS NOT NULL AND A.OBJATTR_ID IN ("
                                        + StringUtils.join(valueObjAttrArr, ',') + ")");

                                // 查询枚举信息
                                List<ValueTableEntity> valueTables = getValueTableByObjAttrIds(valueObjAttrArr);

                                // 组装
                                String attribType = "";
                                String valueType = "";
                                List<ValueTableEntity> valueLst;
                                for (Element e : child)
                                {
                                    String objAttrId = e.attribute("objAttrId").getValue();
                                    String objAttrName = e.attribute("objAttrName").getValue();
                                    String bindId = e.attribute("bindId").getValue();
                                    String index = e.attribute("index").getValue();
                                    String fetchSize = e.attribute("fetchSize").getValue();

                                    Element ele = result_doc.getRootElement().addElement("Data");
                                    ele.addAttribute("bindId", bindId);
                                    ele.addAttribute("index", index);
                                    ele.addAttribute("objAttrId", objAttrId);
                                    ele.addAttribute("objAttrName", objAttrName);

                                    List<QueryResult> resultLst = values.get(Integer.valueOf(objAttrId));

                                    // 判断是否需要格式化值
                                    valueLst = new ArrayList<ValueTableEntity>();
                                    if (lst != null && lst.size() > 0 && valueTables.size() > 0)
                                    {
                                        for (Map<String, String> map : lst)
                                        {
                                            if (objAttrId.equals(map.get("OBJATTR_ID")))
                                            {
                                                attribType = map.get("ATTRIB_TYPE");
                                                valueType = map.get("VALUE_TYPE");

                                                for (ValueTableEntity entity : valueTables)
                                                {
                                                    if (valueType.equals(String.valueOf(entity.getValueType())))
                                                    {
                                                        valueLst.add(entity);
                                                    }
                                                }

                                                break;
                                            }
                                        }
                                    }

                                    // 将结果转化为指定格式XML字符串
                                    ele.appendContent(queryResultListToXml(resultLst, objAttrId, objAttrName, fetchSize, attribType, valueLst).getRootElement());
                                }
                            }
                        }
                    }
                }
            }
        }

        return result_doc.asXML();
    }

    /**
     * 获取其他类型的对象的性能数据
     * @param objXml 固定界面的对象的XML
     * @param type 对象类型
     * @param objID 对象ID
     * @return 性能数据XML
     * @throws Exception 异常
     */
    public String getElseObjCollValue(String objXml, String type, int objID) throws Exception
    {
        // 定义一个Document作为返回结果
        Document result_doc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource></DataSource>");

        // 解析objXml，获取从哪些表获取哪些数据等信息
        Document doc = DocumentHelper.parseText(objXml);
        List list = doc.selectNodes("ElseObject/" + type + "/DataTable");
        if (list != null)
        {
            Iterator it = list.iterator();
            while (it.hasNext())
            {
                // 解析要查询的表及数据条数
                Element tableEle = (Element) it.next();
                String table = tableEle.attribute("table").getValue();
                String collValueNum = tableEle.attribute("collValueNum").getValue();

                // 在结果Document的根节点下添加表节点
                Element ele = result_doc.getRootElement().addElement("DATA_TABLE");
                ele.addAttribute("TABLE_NAME", table);

                // DVB_C类型的对象属性与其他类型的查询方式不同
                // 获取最近的collValueNum个采集时间
                String cmd = "";
                if ("DVB_C".equals(type))
                {
                    cmd =
                        sqlExecutor.getSqlParser().getSelectCommandString(table,
                            Integer.parseInt(collValueNum),
                            false,
                            "COLL_TIME",
                            null,
                            "ORDER BY COLL_TIME DESC",
                            new SqlCondition("FREQ_ID",
                                "SELECT FREQ_ID FROM NMP_OBJDVBCTS WHERE OBJ_ID=" + objID + "",
                                SqlLogicType.And,
                                SqlRelationType.In,
                                SqlParamType.UnKnow,
                                true));
                }
                else
                {
                    cmd =
                        sqlExecutor.getSqlParser().getSelectCommandString(table,
                            Integer.parseInt(collValueNum),
                            false,
                            "COLL_TIME",
                            null,
                            "ORDER BY COLL_TIME DESC",
                            new SqlCondition("OBJ_ID", String.valueOf(objID), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
                }

                // 将采集时间存入List
                final List<String> timeList = new ArrayList<String>();
                DefaultDal.read(cmd, new IReadHandle()
                {
                    @Override
                    public void handle(ResultSet timeRS) throws Exception
                    {
                        while (timeRS.next())
                        {
                            timeList.add(timeRS.getString("COLL_TIME"));
                        }
                    }
                });

                // 针对每个采集时间，从指定的表中获取数据
                if (timeList != null && timeList.size() > 0)
                {
                    for (int k = 0; k < timeList.size(); k++)
                    {
                        // 如果是第一条，添加最大时间
                        if (k == 0)
                        {
                            ele.addAttribute("max_time", timeList.get(k));
                        }

                        Document coll = null;
                        if ("DVB_C".equals(type))
                        {
                            coll =
                                sqlExecutor.fill("SELECT * FROM " + table + " WHERE FREQ_ID IN (SELECT FREQ_ID FROM NMP_OBJDVBCTS WHERE OBJ_ID="
                                    + String.valueOf(objID) + ") AND COLL_TIME='" + timeList.get(k) + "'", "DataTable", table);
                        }
                        else
                        {
                            coll =
                                sqlExecutor.fill("SELECT * FROM " + table + " WHERE OBJ_ID=" + String.valueOf(objID) + " AND COLL_TIME='"
                                    + timeList.get(k) + "'", "DataTable", table);
                        }

                        // 组装结果
                        ele.appendContent(coll.getRootElement());
                    }
                }
            }
        }

        return result_doc.asXML();
    }

    /**
     * 查询SNMP历史数据
     * @param objId 对象ID
     * @param startTime 开始时间(单位是秒)
     * @param endTime 结束时间(单位是秒)
     * @param objAttrIds 对象属性id列表
     * @return 结果
     * @throws Exception 异常
     */
    public String querySNMPHistoryData(int objId, long startTime, long endTime, String objAttrIds) throws Exception
    {
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSources></DataSources>";

        if (!StringUtil.isNullOrEmpty(objAttrIds))
        {
            String[] arr = objAttrIds.split(",");

            Map<Integer, List<QueryResult>> values = CollDataQuery.getInstance().query(objId, startTime, endTime, ArrayUtils.stringToIntArray(arr));

            if (values != null)
            {
                // 查询要格式化值的对象属性
                List<Map<String, String>> lst =
                    new SqlQueryUtil().getLst("SELECT A.OBJATTR_ID,A.ATTRIB_TYPE,B.VALUE_TYPE FROM BMP_OBJATTRIB A LEFT JOIN BMP_ATTRIBUTE B ON A.ATTRIB_ID = B.ATTRIB_ID WHERE B.VALUE_TYPE > 0 AND B.VALUE_TYPE IS NOT NULL AND A.OBJATTR_ID IN ("
                        + objAttrIds + ")");

                // 查询枚举信息
                List<String> valueObjAttrArr = ArrayUtils.stringToStringArrayList(arr);
                List<ValueTableEntity> valueTables = getValueTableByObjAttrIds(valueObjAttrArr);

                // 组装
                Document result_doc = DocumentHelper.parseText(result);

                String objattrId;
                String attribType = "";
                String valueType = "";
                List<ValueTableEntity> valueLst;
                for (Map.Entry<Integer, List<QueryResult>> entry : values.entrySet())
                {
                    objattrId = entry.getKey().toString();

                    Element ele = result_doc.getRootElement().addElement("Data");
                    ele.addAttribute("OBJATTR_ID", objattrId);

                    valueLst = new ArrayList<ValueTableEntity>();
                    if (lst != null && lst.size() > 0 && valueTables.size() > 0)
                    {
                        for (Map<String, String> map : lst)
                        {
                            if (objattrId.equals(map.get("OBJATTR_ID")))
                            {
                                attribType = map.get("ATTRIB_TYPE");
                                valueType = map.get("VALUE_TYPE");

                                for (ValueTableEntity entity : valueTables)
                                {
                                    if (valueType.equals(String.valueOf(entity.getValueType())))
                                    {
                                        valueLst.add(entity);
                                    }
                                }

                                break;
                            }
                        }
                    }

                    // 循环读取每个QueryResult对象，并转化为指定XML格式
                    ele.appendContent(queryResultListToXml(entry.getValue(), entry.getKey().toString(), "", "", attribType, valueLst).getRootElement());
                }

                result = result_doc.asXML();
            }
        }

        return result;
    }

    /**
     * 将调用性能接口返回的结果转化为XML字符串
     * @param list 结果
     * @param objAttrId 对象属性ID
     * @param objAttrName 对象属性名
     * @param attribType 对象属性的类型
     * @param valueLst 格式化枚举值
     * @return 返回指定格式的XML字符串
     * @throws Exception
     */
    private Document queryResultListToXml(List<QueryResult> list, String objAttrId, String objAttrName, String fetchSize, String attribType,
            List<ValueTableEntity> valueLst) throws Exception
    {
        Document result_doc = DocumentHelper.parseText("<DataSource></DataSource>");

        if (list != null && list.size() > 0)
        {
            // 初始化截取条数，默认为所有结果，若fetchSize不为空，则截取fetchSize条
            int fs = list.size();
            if (!StringUtil.isNullOrEmpty(fetchSize))
            {
                fs = Integer.valueOf(fetchSize);
            }

            // 组装
            // 如果只取一条
            if (fs == 1)
            {
                QueryResult entity = list.get(list.size() - 1); // 最后一条

                Element ele = result_doc.getRootElement().addElement("DataTable");
                if (!StringUtil.isNullOrEmpty(objAttrId))
                {
                    Element eleObjAttrId = ele.addElement("OBJATTR_ID");
                    eleObjAttrId.setText(objAttrId);
                }
                if (!StringUtil.isNullOrEmpty(objAttrName))
                {
                    Element eleObjAttrName = ele.addElement("OBJATTR_NAME");
                    eleObjAttrName.setText(objAttrName);
                }

                if (list.size() == 1)
                {
                    // 如果只取一条，则若结果也只有一条，则截取该结果；
                    Element eleCollTime = ele.addElement("COLL_TIME");
                    eleCollTime.setText(String.valueOf(entity.getCollTime()));
                    Element eleVlue = ele.addElement("VALUE");
                    eleVlue.setText(formatObjAttribValue(String.valueOf(entity.getValue()), attribType, valueLst));
                }
                else
                {
                    // 若结果大于一条，则先取最后一条，若最后一条的VALUE为空，则取倒数第二条，不管倒数第二条的VALUE是否为空
                    if (StringUtil.isNullOrEmpty(entity.getValue()))
                    {
                        QueryResult entity2 = list.get(list.size() - 2);

                        Element eleCollTime = ele.addElement("COLL_TIME");
                        eleCollTime.setText(String.valueOf(entity2.getCollTime()));
                        Element eleVlue = ele.addElement("VALUE");
                        eleVlue.setText(formatObjAttribValue(String.valueOf(entity2.getValue()), attribType, valueLst));
                    }
                    else
                    {
                        Element eleCollTime = ele.addElement("COLL_TIME");
                        eleCollTime.setText(String.valueOf(entity.getCollTime()));
                        Element eleVlue = ele.addElement("VALUE");
                        eleVlue.setText(formatObjAttribValue(String.valueOf(entity.getValue()), attribType, valueLst));
                    }
                }
            }
            else
            {
                // 如果取多条，则取结果的最后fs条
                QueryResult entity;
                int count = 0;
                for (int i = list.size() - 1; i >= 0; i--)
                {
                    entity = list.get(i);

                    Element ele = result_doc.getRootElement().addElement("DataTable");
                    if (!StringUtil.isNullOrEmpty(objAttrId))
                    {
                        Element eleObjAttrId = ele.addElement("OBJATTR_ID");
                        eleObjAttrId.setText(objAttrId);
                    }
                    if (!StringUtil.isNullOrEmpty(objAttrName))
                    {
                        Element eleObjAttrName = ele.addElement("OBJATTR_NAME");
                        eleObjAttrName.setText(objAttrName);
                    }
                    Element eleCollTime = ele.addElement("COLL_TIME");
                    eleCollTime.setText(String.valueOf(entity.getCollTime()));
                    Element eleVlue = ele.addElement("VALUE");
                    eleVlue.setText(formatObjAttribValue(String.valueOf(entity.getValue()), attribType, valueLst));

                    count++;

                    if (count >= fs)
                    {
                        break;
                    }
                }
            }
        }

        return result_doc;
    }

    /**
     * 根据枚举信息格式化对象属性的值
     * @param objattrValue
     * @param attribType
     * @param valueTables
     * @return
     * @throws Exception
     */
    private String formatObjAttribValue(String objattrValue, String attribType, List<ValueTableEntity> valueTables) throws Exception
    {
        if (StringUtil.isNullOrEmpty(objattrValue))
        {
            return "";
        }

        String result = objattrValue;
        if (!StringUtil.isNullOrEmpty(attribType) && valueTables != null && valueTables.size() > 0)
        {
            if (!"106".equals(attribType))
            {
                for (ValueTableEntity entity : valueTables)
                {
                    if (entity.getAttribValue().equals(objattrValue))
                    {
                        if (!StringUtil.isNullOrEmpty(entity.getValueDesc()))
                        {
                            result = entity.getValueDesc();
                        }
                        else
                        {
                            result = entity.getValueName();
                        }
                    }
                }
            }
            else if (!StringUtil.isNullOrEmpty(objattrValue) && !"NULL".equals(objattrValue.toUpperCase()))
            {
                // 表格数据
                Document table_doc = DocumentHelper.parseText(objattrValue);

                if (table_doc != null)
                {
                    List tableValueList = table_doc.selectNodes("/DataSource/DataTable");

                    if (tableValueList != null)
                    {
                        Iterator it = tableValueList.iterator();
                        if (it != null)
                        {
                            Element itemEle;
                            String subvalue;

                            while (it.hasNext())
                            {
                                itemEle = (Element) it.next();
                                subvalue = itemEle.element("VALUE").getText();

                                for (ValueTableEntity entity : valueTables)
                                {
                                    if (entity.getAttribValue().equals(subvalue))
                                    {
                                        if (!StringUtil.isNullOrEmpty(entity.getValueDesc()))
                                        {
                                            itemEle.element("VALUE").setText(entity.getValueDesc());
                                        }
                                        else
                                        {
                                            itemEle.element("VALUE").setText(entity.getValueName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                result = table_doc.asXML();
            }
        }

        return result;
    }

    /**
     * 获取某些属性的枚举值
     * @param attrIdArr 属性ID集合
     * @return
     * @throws Exception
     */
    private List<ValueTableEntity> getValueTableByAttrIds(List<String> attrIdArr) throws Exception
    {
        List<ValueTableEntity> valueTables = new ArrayList<ValueTableEntity>();
        if (attrIdArr != null && attrIdArr.size() > 0)
        {
            DefaultDal<ValueTableEntity> dal = new DefaultDal<ValueTableEntity>(ValueTableEntity.class);

            valueTables =
                dal.getLst("SELECT * FROM BMP_VALUETABLE WHERE VALUE_TYPE IN (SELECT VALUE_TYPE FROM BMP_ATTRIBUTE WHERE ATTRIB_ID IN ("
                    + StringUtils.join(attrIdArr, ',') + "))");
        }

        return valueTables;
    }

    /**
     * 获取某些对象属性的枚举值
     * @param objAttrIdArr 对象属性ID集合
     * @return
     * @throws Exception
     */
    private List<ValueTableEntity> getValueTableByObjAttrIds(List<String> objAttrIdArr) throws Exception
    {
        List<ValueTableEntity> valueTables = new ArrayList<ValueTableEntity>();
        if (objAttrIdArr != null && objAttrIdArr.size() > 0)
        {
            DefaultDal<ValueTableEntity> dal = new DefaultDal<ValueTableEntity>(ValueTableEntity.class);

            valueTables =
                dal.getLst("SELECT * FROM BMP_VALUETABLE WHERE VALUE_TYPE IN (SELECT VALUE_TYPE FROM BMP_ATTRIBUTE WHERE ATTRIB_ID IN (SELECT ATTRIB_ID FROM BMP_OBJATTRIB WHERE OBJATTR_ID IN ("
                    + StringUtils.join(objAttrIdArr, ',') + ")))");
        }

        return valueTables;
    }
}
