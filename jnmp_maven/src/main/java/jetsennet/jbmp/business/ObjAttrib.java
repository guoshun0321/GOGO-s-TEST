/************************************************************************
日 期：2012-03-22
作 者: 
版 本：v1.3
描 述: 对象的相关操作
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.AttribAlarmDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.Obj2GroupDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.ObjAttribValueDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.entity.ObjAttribValueEntity;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * 对象属性的相关操作
 * @author
 */
public class ObjAttrib
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addObjAttrib(String objXml) throws Exception
    {
        ObjAttribDal dal = new ObjAttribDal();
        int result = dal.insertXml(objXml);
        return result;
    }

    /**
     * 修改属性值
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int updateObjAttribStrValue(String objXml) throws Exception
    {
        int result = -1;

        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String objattrId = map.get("OBJATTR_ID");
        String objattrName = map.get("OBJATTR_NAME");
        String strValue = map.get("STR_VALUE");
        String objId = map.get("OBJ_ID");
        long nowTime = new Date().getTime();

        ObjAttribDal dal = new ObjAttribDal();
        ObjAttribValueDal dalValue = new ObjAttribValueDal();

        // 修改名称
        ObjAttribEntity entity = dal.get(Integer.valueOf(objattrId));
        if (entity != null)
        {
            entity.setObjattrName(objattrName);
            result = dal.update(entity);
        }

        // 修改属性值，若不存在，则新增
        ObjAttribValueEntity entityValue = dalValue.get(Integer.valueOf(objattrId));
        if (entityValue != null)
        {
            // 更新
            entityValue.setStrValue(strValue);
            entityValue.setCollTime(nowTime);

            result = dalValue.update(entityValue);
        }
        else
        {
            // 新增
            ObjAttribValueEntity newEntity = new ObjAttribValueEntity();
            newEntity.setObjAttrId(Integer.valueOf(objattrId));
            newEntity.setObjId(Integer.valueOf(objId));
            newEntity.setStrValue(strValue);
            newEntity.setCollTime(nowTime);

            result = dalValue.insert(newEntity, false);
        }

        return result;
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateObjAttrib(String objXml) throws Exception
    {
        ObjAttribDal dal = new ObjAttribDal();
        dal.updateXml(objXml);

        // 如果STR_VALUE存在且不为空，则插入或修改BMP_OBJATTRIBVALUE
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String strValue = map.get("STR_VALUE");
        if (!StringUtil.isNullOrEmpty(strValue))
        {
            ObjAttribValueDal dalValue = new ObjAttribValueDal();

            String objattrId = map.get("OBJATTR_ID");
            String objId = map.get("OBJ_ID");
            long nowTime = new Date().getTime();

            ObjAttribValueEntity entity = dalValue.get(Integer.valueOf(objattrId));
            if (entity != null)
            {
                // 更新
                entity.setStrValue(strValue);
                entity.setCollTime(nowTime);

                dalValue.update(entity);
            }
            else
            {
                // 新增
                ObjAttribValueEntity newEntity = new ObjAttribValueEntity();
                newEntity.setObjAttrId(Integer.valueOf(objattrId));
                newEntity.setObjId(Integer.valueOf(objId));
                newEntity.setStrValue(strValue);
                newEntity.setCollTime(nowTime);

                dalValue.insert(newEntity, false);
            }
        }
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteObjAttrib(int keyId) throws Exception
    {
        ObjAttribDal dal = new ObjAttribDal();
        dal.delete(keyId);

        ObjAttribValueDal dalValue = new ObjAttribValueDal();
        dalValue.delete(keyId);

        // 删除告警关联
        AttribAlarmDal alarmDal = new AttribAlarmDal();
        alarmDal.deleteByObjAttribID(keyId);
    }

    /**
     * 删对象的时候删除所有关联关系
     * @param objId (对象ID)
     * @throws Exception 异常
     */
    @Business
    public void deleteObjAttribByObjId(int objId) throws Exception
    {
        ArrayList<ObjAttribEntity> list = ClassWrapper.wrapTrans(ObjAttribDal.class).getAllByObjID(objId);
        AttribAlarmDal attribAlarmdal = new AttribAlarmDal();
        for (ObjAttribEntity objAttrib : list)
        {
            attribAlarmdal.deleteByObjAttribID(objAttrib.getObjAttrId()); // 删除报警
        }
        // 删除所有子对象
        ClassWrapper.wrapTrans(MObjectDal.class).deleteObjAndSub(objId);

        // 删除对象组关联表
        new Obj2GroupDal().deleteByObjId(String.valueOf(objId));

        // 删除该对象属性值
        new ObjAttribValueDal().deleteByObjId(objId);

        // 删除该对象的对象属性
        new ObjAttribDal().deleteByObjId(objId);
    }

    /**
     * @param objAttribIds (对象属性ID数组)
     * @param time (采集时间间隔)
     * @throws Exception 异常
     */
    @Business
    public void setCollTimeBath(String objAttribIds, int time) throws Exception
    {
        new ObjAttribDal().updateCollTime(objAttribIds, time);
    }

    /**
     * 设置设备的通断性检查
     * @param objId 对象id
     * @param time 时间
     * @throws Exception 异常
     */
    @Business
    public void setCheckCollTime(int objId, String time) throws Exception
    {
        new ObjAttribDal().updateCollTime(objId, time);
    }

    /**
     * 获取对象的自定义属性和属性值集合
     * @param attribIds 属性ids
     * @param attribNames 属性名称
     * @param conditions 条件
     * @param objName 对象名称
     * @param top 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String getAttribsAndValues(String attribIds, String attribNames, String conditions, String objName, String top) throws Exception
    {
        Map<String, String> idAndNamePair = new HashMap<String, String>();
        String[] attribIdList = attribIds.split(",");
        String[] attribNameList = attribNames.split(",");
        String[] conditionList = conditions.split(",");
        int topNum = Integer.parseInt(top);
        for (int i = 0; i < attribIdList.length; i++)
        {
            idAndNamePair.put(attribIdList[i], attribNameList[i]);
        }

        ObjAttribDal objAttribDal = new ObjAttribDal();
        List<Map<String, Object>> objAttribs =
            objAttribDal.getMapLst(MessageFormat.format(
                "SELECT oa.ATTRIB_ID,oa.OBJATTR_NAME,oa.OBJATTR_ID,OBJ_NAME,oa.OBJ_ID,STR_VALUE FROM BMP_OBJATTRIB oa "
                    + " INNER JOIN BMP_OBJECT o ON o.OBJ_ID=oa.OBJ_ID LEFT JOIN BMP_OBJATTRIBVALUE ov ON oa.OBJATTR_ID=ov.OBJATTR_ID "
                    + " WHERE oa.ATTRIB_ID IN ({0}) {1} ORDER BY OBJ_ID", attribIds, "".equals(objName) ? "" : "AND o.OBJ_NAME LIKE '%" + objName
                    + "%'"));
        StringBuilder resultVal = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><RecordSet>");
        String objId = "0";
        HashMap<String, String> resultObj = new HashMap<String, String>();
        int total = 0;
        for (int i = 0; i < objAttribs.size(); i++)
        {
            Map<String, Object> objAttrib = objAttribs.get(i);
            String tmpObjId = objAttrib.get("OBJ_ID").toString();
            if (!objId.equals(tmpObjId))
            {
                if (!"0".equals(objId))
                {
                    boolean flag = true;
                    for (int j = 0; j < conditionList.length; j++)
                    {
                        if (!"".equals(conditionList[j]))
                        {
                            String value = resultObj.get(attribNameList[j]);
                            if (value == null || "".equals(value) || value.indexOf(conditionList[j]) == -1)
                            {
                                flag = false;
                                break;
                            }
                        }
                    }
                    if (flag)
                    {
                        total++;
                        resultVal.append(SerializerUtil.serialize(resultObj, "Record").substring(
                            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length()));
                        if (topNum != -1 && total == topNum)
                        {
                            break;
                        }
                    }
                }
                resultObj = new HashMap<String, String>();
            }
            objId = tmpObjId;
            resultObj.put(idAndNamePair.get(objAttrib.get("ATTRIB_ID").toString()), objAttrib.get("STR_VALUE") == null ? "" : objAttrib.get(
                "STR_VALUE").toString());
            resultObj.put("OBJ_NAME", objAttrib.get("OBJ_NAME") == null ? "" : objAttrib.get("OBJ_NAME").toString());
        }
        if (objAttribs.size() > 0)
        {
            boolean flag = true;
            for (int j = 0; j < conditionList.length; j++)
            {
                if (!"".equals(conditionList[j]))
                {
                    String value = resultObj.get(attribNameList[j]);
                    if (value == null || "".equals(value) || value.indexOf(conditionList[j]) == -1)
                    {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag && (topNum == -1 || total < topNum))
            {
                total++;
                resultVal.append(SerializerUtil.serialize(resultObj, "Record").substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length()));
            }
        }
        resultVal.append(MessageFormat.format("<Record1><TotalCount>{0}</TotalCount></Record1>", total));
        resultVal.append("</RecordSet>");
        return resultVal.toString();
    }

    /**
     * 直接根据objxml内容进行更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateObjAttributeAll(String objXml) throws Exception
    {
        ObjAttribDal dal = new ObjAttribDal();
        dal.updateXml(objXml);
    }
}
