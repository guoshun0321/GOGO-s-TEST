/************************************************************************
日 期：2012-03-22
作 者: 
版 本：v1.3
描 述: 对象组的相关操作
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.Obj2GroupDal;
import jetsennet.jbmp.dataaccess.ObjGroupDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.Group2GroupEntity;
import jetsennet.jbmp.entity.Obj2GroupEntity;
import jetsennet.jbmp.entity.ObjGroupEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author ？
 */
public class ObjGroup
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addObjGroup(String objXml) throws Exception
    {
        ObjGroupDal dal = new ObjGroupDal();
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");

        // 创建对象组
        int groupId = dal.insert(map);

        // 建立对象组与其父对象组之间的关系
        createRelation(groupId, map);

        return groupId;
    }

    /**
     * 创建对象组关系
     * @param groupId 对象组id
     * @param map 参数
     * @throws Exception 异常
     */
    private void createRelation(int groupId, HashMap<String, String> map) throws Exception
    {
        String parentId = map.get("PARENT_IDS");
        if (parentId != null && (!"".equals(parentId)))
        {
            DefaultDal<Group2GroupEntity> g2gdal = new DefaultDal<Group2GroupEntity>(Group2GroupEntity.class);
            String useType = map.get("USE_TYPE");
            int iUseType = useType == null ? Group2GroupEntity.USE_TYPE_COMMON : Integer.parseInt(useType);
            String[] parentIds = parentId.split(",");
            for (int i = 0; i < parentIds.length; i++)
            {
                Group2GroupEntity entity = new Group2GroupEntity();
                entity.setGroupId(groupId);
                entity.setParentId(Integer.parseInt(parentIds[i]));
                entity.setUseType(iUseType);
                g2gdal.insert(entity);

                // 检测循环，如发现则抛出异常
                checkGroupCircle(Integer.parseInt(parentIds[i]), groupId, g2gdal);
            }
        }
    }

    /**
     * 检测是否存在循环
     * @param compareId
     * @param groupId
     * @param g2gdal
     * @return
     * @throws Exception
     */
    private void checkGroupCircle(int compareId, int groupId, DefaultDal<Group2GroupEntity> g2gdal) throws Exception
    {
        SqlCondition cond = new SqlCondition("PARENT_ID", Integer.toString(groupId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        List<Group2GroupEntity> g2gLst = g2gdal.getLst(cond);
        for (Group2GroupEntity g2g : g2gLst)
        {
            if (g2g.getGroupId() == compareId)
            {
                throw new Exception("不能创建循环的组关系!");
            }
            checkGroupCircle(compareId, g2g.getGroupId(), g2gdal);
        }
    }

    /**
     * 递归获取某对象组的所有子组
     * @param list 参数
     * @param parentGroupId 参数
     * @param useTypes 参数
     * @throws Exception 异常
     */
    public void getAllSubGroups(List<Group2GroupEntity> list, int parentGroupId, String useTypes) throws Exception
    {
        DefaultDal<Group2GroupEntity> g2gdal = new DefaultDal<Group2GroupEntity>(Group2GroupEntity.class);
        SqlCondition cond =
            new SqlCondition("PARENT_ID", Integer.toString(parentGroupId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        SqlCondition cond2 = new SqlCondition("USE_TYPE", useTypes, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
        List<Group2GroupEntity> g2gLst = g2gdal.getLst(cond, cond2);

        if (list == null)
        {
            list = new ArrayList<Group2GroupEntity>();
        }
        for (Group2GroupEntity g2g : g2gLst)
        {
            list.add(g2g);
            getAllSubGroups(list, g2g.getGroupId(), useTypes);
        }
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateObjGroup(String objXml) throws Exception
    {
        ObjGroupDal dalObjGroup = new ObjGroupDal();
        DefaultDal<Group2GroupEntity> g2gdal = new DefaultDal<Group2GroupEntity>(Group2GroupEntity.class);
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String groupId = map.get("GROUP_ID");

        // 更新对象组
        dalObjGroup.update(map);

        if (map.get("PARENT_IDS") != null)
        {
            // 删除对象组与其父对象组之间的关系
            g2gdal.delete(new SqlCondition("GROUP_ID", groupId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));

            // 重新建立对象组与其父对象组之间的关系
            createRelation(Integer.parseInt(groupId), map);
        }
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteObjGroup(int keyId) throws Exception
    {
        ObjGroupDal objGrpDal = new ObjGroupDal();
        Obj2GroupDal obj2grpDal = new Obj2GroupDal();
        DefaultDal<Group2GroupEntity> g2gdal = new DefaultDal<Group2GroupEntity>(Group2GroupEntity.class);

        // 递归删除子对象组
        SqlCondition cond = new SqlCondition("PARENT_ID", Integer.toString(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        List<Group2GroupEntity> g2gLst = g2gdal.getLst(cond);
        for (Group2GroupEntity g2g : g2gLst)
        {
            deleteObjGroup(g2g.getGroupId());
        }

        // 删除对象组之间的关系
        g2gdal.delete(new SqlCondition("GROUP_ID", String.valueOf(keyId), SqlLogicType.Or, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("PARENT_ID", String.valueOf(keyId), SqlLogicType.Or, SqlRelationType.Equal, SqlParamType.Numeric));

        // 删除对象与对象组之间的关系
        obj2grpDal.deleteByGrpId(String.valueOf(keyId));

        // 删除对象组
        objGrpDal.delete(keyId);
    }

    /**
     * 通过频道查询起前端
     * @param objId 对象id
     * @param obj2GrpType 关系类型
     * @param grpType 对象组类型
     * @return ObjGroupEntity 前端对象
     * @throws Exception
     */
    @Business
    public ObjGroupEntity getGroupByObjID(int objId, int obj2GrpType, int grpType) throws Exception
    {
        return new ObjGroupDal().get(String.format("SELECT og.* FROM BMP_OBJGROUP og "
            + "INNER JOIN BMP_OBJ2GROUP o2g ON o2g.GROUP_ID=og.GROUP_ID "
            + "INNER JOIN BMP_OBJECT oj ON oj.OBJ_ID=o2g.OBJ_ID "
            + "WHERE o2g.USE_TYPE=%s AND og.GROUP_TYPE=%s AND oj.OBJ_ID=%s", obj2GrpType, grpType, objId));
    }

    /**
     * 保存拓扑图时调用
     * @param groupId 组id
     * @param groupType 组类型
     * @param groupName 组名称
     * @param groupState 组状态
     * @param username 用户名称
     * @throws Exception 异常
     */
    public void saveObjGroupFromTOMAP(String groupId, int groupType, String groupName, int groupState, String username) throws Exception
    {
        ObjGroupDal dal = new ObjGroupDal();
        dal.saveObjGroupFromTOPO(Integer.parseInt(groupId), groupType, groupName, groupState, username);
    }

    /**
     * 得到业务系统的报警记录数目
     * @param groupIds
     * @return
     * @throws Exception
     */
    @Business
    public String getAlarmCount(String groupIds, String beiginTime, String endTime) throws Exception
    {
        Element root = DocumentHelper.createElement("RecordSet");
        //得到业务系统
        List<ObjGroupEntity> groups =
            new ObjGroupDal().getLst(String.format("SELECT * FROM BMP_OBJGROUP WHERE GROUP_TYPE = 1 AND GROUP_ID IN ( %s )", groupIds));
        for (ObjGroupEntity group : groups)
        {
            Element record = root.addElement("Record");
            addElement(record, "GROUP_NAME", group.getGroupName());

            Element level10 = addElement(record, "ALARM_LEVEL_10", "0");
            Element level20 = addElement(record, "ALARM_LEVEL_20", "0");
            Element level30 = addElement(record, "ALARM_LEVEL_30", "0");
            Element level40 = addElement(record, "ALARM_LEVEL_40", "0");
            Element levelTotal = addElement(record, "ALARM_LEVEL_TOTAL", "0");

            //得到业务系统关联的对象
            List<Obj2GroupEntity> objs =
                DefaultDal.getLst(Obj2GroupEntity.class, String.format("SELECT * FROM BMP_OBJ2GROUP WHERE GROUP_ID = %s", group.getGroupId()));
            if (objs == null || objs.size() == 0)
                continue;

            String objIds = "";
            for (int i = 0; i < objs.size(); i++)
            {
                objIds += objs.get(i).getObjId();
                if (i < objs.size() - 1)
                    objIds += ",";
            }
            //得到业务系统所关联的每个报警级别的报警记录数
            List<Map<String, Object>> list =
                DefaultDal.getMapLst(String.format("SELECT ALARM_LEVEL,COUNT(*) AS ALARM_COUNT FROM BMP_ALARMEVENT WHERE OBJ_ID IN ( %s ) AND COLL_TIME >= %s AND COLL_TIME <= %s GROUP BY ALARM_LEVEL",
                    objIds,
                    beiginTime,
                    endTime));
            if (list.size() == 0)
                continue;

            int total = 0;
            for (Map<String, Object> map : list)
            {
                int level = (Integer) map.get("ALARM_LEVEL");
                if (level == 10)
                    level10.setText(String.valueOf(map.get("ALARM_COUNT")));
                else if (level == 20)
                    level20.setText(String.valueOf(map.get("ALARM_COUNT")));
                else if (level == 30)
                    level30.setText(String.valueOf(map.get("ALARM_COUNT")));
                else if (level == 40)
                    level40.setText(String.valueOf(map.get("ALARM_COUNT")));
                total += (Integer) map.get("ALARM_COUNT");
            }
            levelTotal.setText(String.valueOf(total));
        }
        return root.asXML();
    }

    private Element addElement(org.dom4j.Element root, String name, String value)
    {
        org.dom4j.Element elm = root.addElement(name);
        elm.setText(value);
        return elm;
    }
}
