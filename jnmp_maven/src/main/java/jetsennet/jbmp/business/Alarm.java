package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.AlarmActionDal;
import jetsennet.jbmp.dataaccess.AlarmDal;
import jetsennet.jbmp.dataaccess.AlarmLevelDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.util.TwoTuple;
import jetsennet.util.SerializerUtil;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author liwei 代码优化
 */
public class Alarm
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addAlarm(String objXml) throws Exception
    {
        AlarmDal dal = new AlarmDal();
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAlarm(String objXml) throws Exception
    {
        AlarmDal dal = new AlarmDal();
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarm(int keyId) throws Exception
    {
        AlarmDal dal = new AlarmDal();
        dal.delete(keyId);
    }

    /**
     * 设置报警规则
     * 
     * @param alarmConfig
     * @param objAttrId
     */
    @Business
    public void setAlarmConfig(String alarmConfig) throws Exception
    {
        try
        {
            TwoTuple<Map<String, String>, List<Map<String, String>>> alarmInfo = this.parserBatchAlarmStr(alarmConfig);
            Map<String, String> alarmMap = alarmInfo.first;

            // 更新报警规则
            String alarmId = alarmMap.get("ALARM_ID");
            AlarmDal adal = new AlarmDal();
            AlarmLevelDal aldal = new AlarmLevelDal();
            adal.update(alarmMap);

            // 更新或添加报警级别
            List<Map<String, String>> levels = alarmInfo.second;
            StringBuilder delSB = new StringBuilder();
            AlarmActionDal aadal = new AlarmActionDal();
            for (Map<String, String> level : levels)
            {
                String levelIdS = level.get("LEVEL_ID");
                if (levelIdS != null)
                {
                    int levelId = Integer.valueOf(levelIdS);
                    if (levelId > 0)
                    {
                        aldal.update(level);
                    }
                    else
                    {
                        levelId = aldal.insert(level);
                    }
                    delSB.append(levelId).append(",");

                    // 更新关联的报警动作
                    String actionStr = level.get("ACTION_ID");
                    aadal.updateLevelAction(levelId, actionStr);
                }
            }
            if (delSB.length() > 0)
            {
                delSB.deleteCharAt(delSB.length() - 1);
            }
            String delStr = null;
            if (delSB.length() > 0)
            {
                delStr = "DELETE FROM BMP_ALARMLEVEL WHERE ALARM_ID=%s AND LEVEL_ID NOT IN(%s)";
                delStr = String.format(delStr, alarmId, delSB.toString());
            }
            else
            {
                delStr = "DELETE FROM BMP_ALARMLEVEL WHERE ALARM_ID=" + alarmId;
            }
            DefaultDal.delete(delStr);
        }
        catch (Exception ex)
        {
            throw ex;
        }

    }

    /**
     * 设置报警规则
     * 
     * @param alarmConfig
     * @param objAttrId
     */
    @Business
    public void batchSetAlarmConfig(String alarmConfig, String alarmIds) throws Exception
    {
        try
        {
            AlarmActionDal aadal = new AlarmActionDal();
            TwoTuple<Map<String, String>, List<Map<String, String>>> alarmInfo = this.parserBatchAlarmStr(alarmConfig);
            Map<String, String> alarmMap = alarmInfo.first;

            String[] alarmLst = alarmIds.split(",");

            for (String alarmId : alarmLst)
            {

                AlarmDal adal = new AlarmDal();
                AlarmLevelDal aldal = new AlarmLevelDal();
                alarmMap.put("ALARM_ID", alarmId);
                adal.update(alarmMap);

                List<Map<String, String>> levels = alarmInfo.second;
                StringBuilder delSB = new StringBuilder();
                for (Map<String, String> level : levels)
                {
                    level.put("ALARM_ID", alarmId);
                    int levelId = aldal.insert(level);
                    delSB.append(levelId).append(",");

                    // 更新关联的报警动作
                    String actionStr = level.get("ACTION_ID");
                    aadal.updateLevelAction(levelId, actionStr);
                }
                if (delSB.length() > 0)
                {
                    delSB.deleteCharAt(delSB.length() - 1);
                }
                String delStr = null;
                if (delSB.length() > 0)
                {
                    delStr = "DELETE FROM BMP_ALARMLEVEL WHERE ALARM_ID=%s AND LEVEL_ID NOT IN(%s)";
                    delStr = String.format(delStr, alarmId, delSB.toString());
                }
                else
                {
                    delStr = "DELETE FROM BMP_ALARMLEVEL WHERE ALARM_ID=" + alarmId;
                }
                DefaultDal.delete(delStr);
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }

    }

    /**
     * 关联已有报警规则
     * @param ids 属性或指标id
     * @param idType id类型，用来区分是属性还是指标
     * @param oldAlarmId 要关联的报警规则id。
     * @throws Exception
     */
    @Business
    public String relateExistAlarm(String ids, String idType, int oldAlarmId) throws Exception
    {
        String newAlarmIds = "";
        AlarmDal dal = new AlarmDal();
        if ("attribute".equals(idType))
        {
            newAlarmIds = dal.batchCopyAlarmFromAttribute(ids, oldAlarmId);
        }
        else if ("objattr".equals(idType))
        {
            newAlarmIds = dal.batchCopyAlarmFromObjattr(ids, oldAlarmId);
        }
        return newAlarmIds;
    }

    private TwoTuple<Map<String, String>, List<Map<String, String>>> parserBatchAlarmStr(String alarmConfig) throws Exception
    {
        Map<String, String> alarmMap = null;
        List<Map<String, String>> levels = new ArrayList<Map<String, String>>();
        try
        {
            Document doc = DocumentHelper.parseText(alarmConfig);

            Element root = doc.getRootElement();
            Element alarmEle = root.element("Alarm").element("Record");
            String alarmS = alarmEle.asXML();
            alarmMap = SerializerUtil.deserialize(alarmS, "Record");

            List<Element> levelEles = (List<Element>) root.element("Levels").elements("Record");
            if (levelEles != null)
            {
                for (Element levelEle : levelEles)
                {
                    String levelS = levelEle.asXML();
                    Map<String, String> levelMap = SerializerUtil.deserialize(levelS, "Record");
                    levels.add(levelMap);
                }
            }

        }
        catch (Exception ex)
        {
            throw ex;
        }
        return new TwoTuple<Map<String, String>, List<Map<String, String>>>(alarmMap, levels);
    }
}
