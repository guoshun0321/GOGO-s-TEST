package jetsennet.jbmp.business;

import java.util.HashMap;

import jetsennet.jbmp.dataaccess.AlarmDal;
import jetsennet.jbmp.dataaccess.Attrib2ClassDal;
import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.Attrib2ClassEntity;
import jetsennet.util.SerializerUtil;

/**
 * @author？
 */
public class Attribute
{
    /**
     * 新增
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void addAttribute(String objXml) throws Exception
    {
        AttributeDal dal = new AttributeDal();
        dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAttribute(String objXml) throws Exception
    {
        AttributeDal dal = new AttributeDal();
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAttribute(int keyId) throws Exception
    {
        AttributeDal dal = new AttributeDal();
        dal.delete(keyId);
    }

    /**
     * 新增属性，同时新建一条不生效的报警规则
     * 
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addAttrib(String objXml) throws Exception
    {
        AlarmDal adal = new AlarmDal();
        int alarmId = adal.insert(AlarmEntity.newUnValidAlarm("未命名规则"));

        AttributeDal dal = new AttributeDal();
        HashMap<String, String> attribMap = SerializerUtil.deserialize(objXml, "");
        attribMap.put("ALARM_ID", Integer.toString(alarmId));
        int attribId = dal.insert(attribMap);

        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String classId = map.get("CLASS_ID");
        Attrib2ClassDal acdal = new Attrib2ClassDal();
        acdal.insert(new Attrib2ClassEntity(Integer.valueOf(classId), attribId));
        return attribId;
    }

    /**
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addAttribAlarm(String objXml) throws Exception
    {
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String classId = map.get("CLASS_ID");
        String useid = map.get("CREATE_USER");
        String type = map.get("ATTRIB_TYPE");
        if ("100".equals(type) || "101".equals(type) || "106".equals(type))
        {
        }
        else
        {
            String alarmName = map.get("ATTRIB_NAME") + "报警";
            AlarmDal alarm = new AlarmDal();
            HashMap<String, String> alarmMap = new HashMap<String, String>();
            alarmMap.put("ALARM_NAME", alarmName);
            alarmMap.put("IS_VAILD", "0");
            alarmMap.put("ALARM_TYPE", "0");
            alarmMap.put("CREATE_USER", useid);
            alarmMap.put("CREATE_USER", useid);
            int alarmId = alarm.insert(alarmMap);
            map.put("ALARM_ID", Integer.toString(alarmId));
            objXml = SerializerUtil.serialize(map, "BMP_ATTRIBUTE");
        }
        AttributeDal dal = new AttributeDal();
        int attribId = dal.insertXml(objXml);
        Attrib2ClassDal acdal = new Attrib2ClassDal();
        acdal.insert(new Attrib2ClassEntity(Integer.valueOf(classId), attribId));
        return attribId;
    }
}
