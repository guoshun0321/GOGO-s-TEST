/************************************************************************
日 期：2012-03-22
作 者: 
版 本：v1.3
描 述: 对象的相关操作
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.Obj2GroupDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.InsConfigColl;
import jetsennet.jbmp.ins.InsManager;
import jetsennet.jbmp.ins.helper.AttrsInsResult;
import jetsennet.jbmp.util.SqlQueryUtil;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * 对象的相关操作
 * @author
 */
public class MObject
{
    public static final String DVBC_TS = "10001";
    public static final String DVB_C = "10002";
    public static final String DVB_TS290 = "10003";
    public static final String TVProgram = "10010";
    public static final String TS = "10020";
    public static final String TSCH = "10030";
    private static InsConfigColl fac = InsConfigColl.getInstance();

    /**
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addObj2Group(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

        Obj2GroupDal dalObj2Group = new Obj2GroupDal();
        dalObj2Group.add(model.get("OBJ_ID"), model.get("GROUP_ID"), model.get("USE_TYPE"));
        return model.get("OBJ_ID") + "," + model.get("GROUP_ID");
    }

    /**
     * 批量将对象添加到对象组中
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void addMobj2Mgroup(String objXml) throws Exception
    {
        String[] objIdsAndGroupIds = objXml.split("_");
        if (objIdsAndGroupIds.length != 2)
        {
            return;
        }
        this.addMobj2Mgroup(objIdsAndGroupIds[0], objIdsAndGroupIds[1]);
    }

    /**
     * 批量将对象添加到对象组中
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void addMobj2Mgroup(String objIdsStr, String groupIdStr) throws Exception
    {
        String[] objIds = objIdsStr.split(",");
        String[] groupIds = groupIdStr.split(",");
        Obj2GroupDal dalObj2Group = new Obj2GroupDal();
        dalObj2Group.deleteObjectsFromGroups(objIds, groupIds);
        for (int i = 0; i < objIds.length; i++)
        {
            for (int j = 0; j < groupIds.length; j++)
            {
                dalObj2Group.add(objIds[i], groupIds[j], "0");
            }
        }
    }

    /**
     * 实例化属性(参数是要实例化的对象id和被实例化的属性id)
     * @param objId 要实例化的对象id
     * @param attrIds 被实例化的属性id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public List<Object> instanceAttrib(int objId, ArrayList<String> attrIds) throws Exception
    {
        List<Object> objList = new ArrayList<Object>();
        MObjectDal dal = ClassWrapper.wrapTrans(MObjectDal.class);
        AttributeDal dals = ClassWrapper.wrapTrans(AttributeDal.class);
        MObjectEntity mo = dal.get(objId);
        List<AttributeEntity> attrs = dals.getAttrs(attrIds);

        AttrsInsResult result = InsManager.getInstance().getInsResult(mo, attrs, -1, null, false);

        if (attrs != null)
        {
            for (AttributeEntity entity : attrs)
            {
                Object object = result.getByAttribId(entity.getAttribId());
                if (object != null)
                {
                    objList.add(object);
                }
            }
        }

        return objList;
    }

    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateObject2Object(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        SqlValue objIdValue = new SqlValue(model.get("OBJ_ID"));
        exec.executeNonQuery("DELETE FROM NMP_OBJ2OBJ WHERE OBJ_ID=%s", objIdValue);
        String nextIds = model.get("NEXT_ID");
        if (!StringUtil.isNullOrEmpty(nextIds))
        {
            String[] arrNextId = nextIds.split(",");
            for (String item : arrNextId)
            {
                exec.executeNonQuery("INSERT INTO NMP_OBJ2OBJ (OBJ_ID,NEXT_ID) VALUES (%s,%s)", objIdValue, new SqlValue(item));
            }
        }
    }

    /**
     * @param groupIds 对象组ids
     * @param useType 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteObj2Group(String groupIds, int useType) throws Exception
    {
        Obj2GroupDal dalObj2Group = new Obj2GroupDal();
        dalObj2Group.delete(new SqlCondition("GROUP_ID", groupIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
            new SqlCondition("USE_TYPE", String.valueOf(useType), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
    }

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addObject(String objXml) throws Exception
    {
        MObjectDal dal = new MObjectDal();
        String objId = "" + dal.insertXml(objXml);

        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        String groupId = model.get("GROUP_ID");
        if (groupId != null && !"".equals(groupId))
        {
            new Obj2GroupDal().add(objId, groupId, model.get("USE_TYPE"));
        }
        return objId;
    }

    /**
     * 添加并实例化对象
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void addAndInsObj(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        model.put("CLASS_GROUP", "1");

        MObjectDal dal = new MObjectDal();
        Obj2GroupDal dal1 = new Obj2GroupDal();
        int objId = dal.insert(model);
        model.put("OBJ_ID", "" + objId);
        dal1.add(model);

        if (objId < 0)
        {
            return;
        }
        MObjectDal modao = new MObjectDal();
        MObjectEntity mo = modao.get(objId);
    }

    /**
     * 添加对象
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public MObjectEntity addObj(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        model.put("CLASS_GROUP", "1");

        MObjectDal dal = new MObjectDal();
        Obj2GroupDal dal1 = new Obj2GroupDal();
        int objId = dal.insert(model);
        model.put("OBJ_ID", "" + objId);
        dal1.add(model);
        MObjectDal modao = new MObjectDal();
        MObjectEntity mo = modao.get(objId);
        return mo;
    }

    /**
     * @param map 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public MObjectEntity addObjFromMap(HashMap<String, String> map) throws Exception
    {
        map.put("CLASS_GROUP", "1");
        MObjectDal dal = new MObjectDal();
        Obj2GroupDal dal1 = new Obj2GroupDal();
        int objId = dal.insert(map);
        map.put("OBJ_ID", "" + objId);
        dal1.add(map);
        MObjectDal modao = new MObjectDal();
        MObjectEntity mo = modao.get(objId);
        return mo;
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateObject(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        String objId = model.get("OBJ_ID");
        String time = model.get("CHECKUSEFUL");
        MObjectDal dal = new MObjectDal();
        Obj2GroupDal dal1 = new Obj2GroupDal();
        ObjAttribDal dal2 = new ObjAttribDal();
        dal.update(model);
        dal1.update(model);
        dal2.updateCollTime(Integer.parseInt(objId), time);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteObject(int keyId) throws Exception
    {
        MObjectDal modao = new MObjectDal();
        modao.deleteById(keyId);
    }

    /**
     * 查询该对象的模板
     * @param objId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String getTemplateByObjId(int objId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document doc =
            exec.fill("SELECT * FROM BMP_TOPOTEMPLATE WHERE TEMP_TYPE=30 AND TEMP_STATE<>0 AND RELATE_ID = '" + objId + "'" + " UNION"
                + " SELECT * FROM BMP_TOPOTEMPLATE WHERE TEMP_TYPE=20 AND TEMP_STATE<>0 AND RELATE_ID = ("
                + "SELECT CLASS_ID FROM BMP_OBJECT WHERE OBJ_ID = '" + objId + "')");

        return doc.asXML();
    }

    /**
     * 获取某对象的所有子对象
     * @param objId 对象ID
     * @return 返回自定义所有子对象组成的XML
     * @throws Exception 异常
     */
    public String querySubObjectsByParentId(int objId) throws Exception
    {
        String result = "";

        try
        {
            List<Map<String, String>> subLst =
                new SqlQueryUtil().getLst("SELECT A.*,B.CLASS_NAME FROM BMP_OBJECT A LEFT JOIN BMP_ATTRIBCLASS B ON A.CLASS_ID=B.CLASS_ID"
                    + " WHERE A.PARENT_ID=" + objId);

            getSubObjects(subLst, subLst);

            result = new SqlQueryUtil().listToXmlString(subLst);
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return result;
    }

    /**
     * 批量修改码流类型
     * @param tsIds 码流ID集合
     * @param type 码流类型
     * @throws Exception 异常
     */
    public void setTsStreamType(String tsIds, String type) throws Exception
    {
        try
        {
            MObjectDal dal = new MObjectDal();
            List<SqlField> fields = new ArrayList<SqlField>();
            fields.add(new SqlField("NUM_VAL1", type));
            dal.update(fields, new SqlCondition("OBJ_ID", tsIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric));
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**
     * 递归获取子对象
     * @param lst 上一级查询的结果
     * @param result 存储每次递归获取到的结果
     * @throws Exception
     */
    private void getSubObjects(List<Map<String, String>> lst, List<Map<String, String>> result) throws Exception
    {
        try
        {
            if (lst != null && lst.size() > 0)
            {

                // 获取上一次查询结果中的所有对象ID
                String objIds = "";
                String oid = "";
                int size = lst.size();
                for (int i = 0; i < size; i++)
                {
                    oid = lst.get(i).get("OBJ_ID");
                    if (!StringUtil.isNullOrEmpty(oid))
                    {
                        objIds += oid;
                        if (i != (size - 1))
                        {
                            objIds += ",";
                        }
                    }
                }

                // 查询这些对象的子对象
                if (!StringUtil.isNullOrEmpty(objIds))
                {
                    List<Map<String, String>> subLst =
                        new SqlQueryUtil().getLst("SELECT A.*,B.CLASS_NAME FROM BMP_OBJECT A LEFT JOIN BMP_ATTRIBCLASS B ON A.CLASS_ID=B.CLASS_ID"
                            + " WHERE A.PARENT_ID IN (" + objIds + ")");

                    // 将每次的查询结果存入result，并递归获取子对象
                    if (subLst != null && subLst.size() > 0)
                    {
                        for (Map<String, String> map : subLst)
                        {
                            result.add(map);
                        }

                        getSubObjects(subLst, result);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**
     * @param sql 语句
     * @throws Exception 异常
     */
    @Business
    public void updateBySql(String sql) throws Exception
    {
        MObjectDal objDal = new MObjectDal();
        objDal.update(sql);
    }
}
