/**********************************************************************
 * 日 期： 2012-4-13
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  CollDataReceiver.java
 * 历 史： 2012-4-13 创建
 *********************************************************************/
package jetsennet.jbmp.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.bus.CollDataBus;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * 性能数据接收器
 */
public class CollDataReceiver
{
    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void receive(String objXml) throws Exception
    {
        ObjAttribDal dal = new ObjAttribDal();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Document doc = DocumentHelper.parseText(objXml);
        List<Element> objLst = doc.selectNodes("ObjSet/Obj");
        for (Element obj : objLst)
        {
            List<CollData> dataLst = new ArrayList<CollData>();
            String id = obj.attributeValue("ID");
            String ip = obj.attributeValue("IP");
            ArrayList<ObjAttribEntity> oaLst = dal.getCollObjAttribByID(Integer.parseInt(id));

            List<Element> indexSetLst = obj.selectNodes("IndexSet");
            for (Element indexSet : indexSetLst)
            {
                boolean dataFlag = false;
                HashMap<String, Double> rrdValueMap = new HashMap<String, Double>();

                String dateTime = indexSet.attributeValue("DateTime");
                Date collTime = df.parse(dateTime);

                List<Element> indexLst = indexSet.selectNodes("Index");
                for (Element index : indexLst)
                {
                    String indexId = index.attributeValue("ID");
                    String value = index.attributeValue("Value");
                    ObjAttribEntity oa = findObjAttribEntity(oaLst, indexId);
                    if (oa == null)
                    {
                        continue;
                    }
                    CollData data = createCollData(ip, oa, collTime, value);
                    dataLst.add(data);
                    if (oa.getAttribType() == AttribClassEntity.CLASS_LEVEL_PERF)
                    {
                        rrdValueMap.put(Integer.toString(oa.getObjAttrId()), Double.parseDouble(data.value));
                        if (!dataFlag)
                        {
                            data.put(CollData.PARAMS_DATA, rrdValueMap);
                            dataFlag = true;
                        }
                    }
                }
            }

            for (CollData data : dataLst)
            {
                CollDataBus.getInstance().put(data);
            }
        }
    }

    /**
     * @param ip
     * @param oa
     * @param collTime
     * @param value
     * @return
     */
    private CollData createCollData(String ip, ObjAttribEntity oa, Date collTime, String value)
    {
        CollData data = new CollData();
        data.objID = oa.getObjId();
        data.objAttrID = oa.getObjAttrId();
        data.attrID = oa.getAttribId();
        data.dataType = CollData.DATATYPE_PERF;
        data.value = value;
        data.srcIP = ip;
        data.time = collTime;
        return data;
    }

    /**
     * @param oaLst
     * @param indexId
     * @return
     */
    private ObjAttribEntity findObjAttribEntity(ArrayList<ObjAttribEntity> oaLst, String indexId)
    {
        for (ObjAttribEntity oa : oaLst)
        {
            if (indexId.equals(oa.getAttribValue()))
            {
                return oa;
            }
        }
        return null;
    }
}
