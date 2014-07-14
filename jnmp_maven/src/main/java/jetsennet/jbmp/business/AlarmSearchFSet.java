package jetsennet.jbmp.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.bus.CollDataBus;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.StringUtil;

/**
 * @author liwei代码格式优化
 */
public class AlarmSearchFSet
{
    private static final Logger logger = Logger.getLogger(ReceiveTaskBase.class);

    /**
     * @param msg 参数
     * @throws Exception 异常
     */
    public void onReceiveMessage(String msg) throws Exception
    {
        ISqlExecutor sqlExecutor = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            if (!StringUtil.isNullOrEmpty(msg))
            {
                Document doc = DocumentHelper.parseText(msg);
                Node node = (Node) doc.selectSingleNode("/Msg/ReturnInfo/AlarmSearchFSet");
                String freqNum = ((Element) node).attributeValue("Freq");

                String[] objItem = null;
                if (!StringUtil.isNullOrEmpty(freqNum))
                {
                    objItem =
                        sqlExecutor.find(sqlExecutor.getSqlParser().formatCommand(
                            "SELECT OBJ_ID FROM BMP_OBJECT WHERE FIELD_1 LIKE '%" + freqNum + "'" + " and CLASS_ID=7", new SqlValue[0]));
                }

                if (objItem != null)
                {
                    List list = node.selectNodes("AlarmSearchF");
                    if (list != null && list.size() > 0)
                    {
                        List<CollData> datas = new ArrayList<CollData>();

                        for (int i = 0; i < list.size(); i++)
                        {
                            Element ele = (Element) list.get(i);
                            String type = ele.attributeValue("Type");
                            String alarmId = ele.attributeValue("AlarmID");
                            String desc = ele.attributeValue("Desc");
                            String value = ele.attributeValue("Value");
                            String time = ele.attributeValue("Time");
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date collTime = df.parse(time);

                            // 对象属性信息
                            String[] objAttrItem =
                                sqlExecutor.find(sqlExecutor.getSqlParser().formatCommand(
                                    "SELECT OBJATTR_ID,ATTRIB_ID FROM BMP_OBJATTRIB WHERE ATTRIB_VALUE=%s and OBJ_ID=%s",
                                    new SqlValue[] { new SqlValue(type), new SqlValue(objItem[0], SqlParamType.Numeric) }));

                            if (objAttrItem != null)
                            {
                                CollData data = new CollData();
                                data.objID = Integer.parseInt(objItem[0]);
                                data.objAttrID = Integer.parseInt(objAttrItem[0]);
                                data.attrID = Integer.parseInt(objAttrItem[1]);
                                data.dataType = CollData.DATATYPE_TRAP;
                                data.value = value;
                                data.time = collTime;

                                datas.add(data);
                            }
                        }

                        for (CollData data : datas)
                        {
                            CollDataBus.getInstance().put(data);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
            throw ex;
        }
    }
}
