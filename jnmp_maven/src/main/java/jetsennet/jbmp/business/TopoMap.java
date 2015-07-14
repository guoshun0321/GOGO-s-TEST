/************************************************************************
 * 日 期：2011-11-24 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 拓扑图相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.TopoMapDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.Group2GroupEntity;
import jetsennet.jbmp.entity.TopoMapEntity;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author 余灵
 */
public class TopoMap
{
    // private ConnectionInfo nmpConnectionInfo;
    private ISqlExecutor sqlExecutor;

    /**
     * 构造函数
     */
    public TopoMap()
    {
        // nmpConnectionInfo =
        // new ConnectionInfo(DbConfig.getProperty("nmp_driver"), DbConfig.getProperty("nmp_dburl"), DbConfig.getProperty("nmp_dbuser"), DbConfig
        // .getProperty("nmp_dbpwd"));
        sqlExecutor = SqlExecutorFacotry.getSqlExecutor();
    }

    /**
     * 添加 1、传来的"MAP_INFO"是拓扑图的XML信息，先将该XML信息保存为文件，然后将路径赋值给"MAP_INFO"； 2、先创建同名的类型为5的对象组，然后将新建对象组的ID赋值给"GROUP_ID"；
     * 3、如果传入的"PARENT_ID"不为空，还要添加拓扑图父子关系到BMP_GROUP2GROUP表； 4、对激活或缺省激活的拓扑图，重新处理绑定关系；
     * @param objXml 参数
     * @return 返回新拓扑图ID或XML文件创建结果
     * @throws Exception 异常
     */
    public String addTopoMap(String objXml) throws Exception
    {
        String result = "";
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

            DefaultDal<TopoMapEntity> dal = new DefaultDal<TopoMapEntity>(TopoMapEntity.class);
            SqlCondition cond = new SqlCondition("MAP_NAME", model.get("MAP_NAME"), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
            if (dal.isExist(dal.getTableName(), cond) > 0)
            {
                result = "-3"; // 已存在同名拓扑
            }
            else
            {
                String mapNewId = String.valueOf(exec.getNewId(TopoMapDal.TABLE_NAME));
                model.put(TopoMapDal.PRIMARY_KEY, mapNewId);

                String xmlInfo = model.get("MAP_INFO");
                String parentGroupId = model.get("PARENT_ID");

                // 将xml信息保存为文件,并返回保存路径
                String path = new TopoXmlFile().writeTopoMapFile("BMP_TOPOMAP", mapNewId, xmlInfo);
                if (!"-1".equals(path)) // 如果成功
                {
                    // 更改MAP_INFO的值，存相对路径;
                    model.put("MAP_INFO", path);

                    // 先自动创建同名对象组,类型为5
                    String newGroupId = String.valueOf(exec.getNewId("BMP_OBJGROUP"));

                    // exec.transBegin();//郭世平修改(外面已经有事物了，这里再包事物会报错)
                    ObjGroup objGroup = new ObjGroup();
                    objGroup.saveObjGroupFromTOMAP(newGroupId, 5, model.get("MAP_NAME"), 0, model.get("CREATE_USER"));

                    // 更改GROUP_ID值，存自动创建的对象组ID
                    model.put("GROUP_ID", newGroupId);

                    new TopoMapDal().add(model, false);

                    // 将关系存到BMP_GROUP2GROUP,类型2代表父子关系
                    if (!StringUtil.isNullOrEmpty(parentGroupId))
                    {
                        exec.executeNonQuery("INSERT INTO BMP_GROUP2GROUP VALUES ('" + newGroupId + "','" + parentGroupId + "',2)");
                    }

                    // 将拓扑图与创建图的用户所属的角色的权限关联
                    if (!"-1".equals(model.get("CREATE_USERID")) && !StringUtil.isNullOrEmpty(model.get("CREATE_ROLEID")))
                    {
                        String[] arr = model.get("CREATE_ROLEID").split(",");
                        if (arr != null && arr.length > 0)
                        {
                            if (checkContainSystemRole(arr))
                            {
                                for (String roleId : arr)
                                {
                                    if (!StringUtil.isNullOrEmpty(roleId))
                                    {
                                        exec.executeNonQuery("INSERT INTO BMP_ROLETOPOAUTHORITY VALUES ('" + mapNewId + "','" + roleId + "')");
                                    }
                                }
                            }
                            else
                            {
                                for (String roleId : arr)
                                {
                                    if (!StringUtil.isNullOrEmpty(roleId))
                                    {
                                        exec.executeNonQuery("INSERT INTO BMP_ROLETOPOAUTHORITY VALUES ('" + mapNewId + "','" + roleId + "')");
                                        exec.executeNonQuery("INSERT INTO BMP_ROLETOPOAUTHORITY VALUES ('" + mapNewId + "','" + "1" + "')");
                                    }
                                }
                            }
                        }
                    }

                    // 对激活或缺省激活的拓扑图，重新处理绑定关系
                    if (!"0".equals(model.get("MAP_STATE")))
                    {
                        topoHandle(mapNewId, newGroupId, xmlInfo);
                    }

                    result = mapNewId;
                }
                else
                {
                    result = path;
                }

                // exec.transCommit();//郭世平修改(外面已经有事物了，这里再包事物会报错)
            }
        }
        catch (Exception ex)
        {
            // exec.transRollback();//郭世平修改(外面已经有事物了，这里再包事物会报错)
            result = "-1";
            throw ex;
        }

        return result;
    }

    /**
     * 判断角色数组中是否包含系统管理员（即：角色ID为1的角色）
     * @param arr角色数组
     * @return
     */
    private boolean checkContainSystemRole(String[] arr)
    {
        boolean flag = false;
        if (arr == null || arr.length <= 0)
        {
            return flag;
        }
        for (String id : arr)
        {
            if ("1".equals(id))
            {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 修改拓扑图 若PARENT_ID不为空，则代表要修改该拓扑图关联的上级拓扑图； 若修改后状态不为新建，则重新处理绑定关系; 若修改后状态为新建，但修改前本身的状态不为新建，则删除原来的绑定关系
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public String updateTopoMap(String objXml) throws Exception
    {
        String result = "";

        try
        {
            // sqlExecutor.transBegin();

            HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

            DefaultDal<TopoMapEntity> dal = new DefaultDal<TopoMapEntity>(TopoMapEntity.class);
            SqlCondition cond = new SqlCondition("MAP_NAME", model.get("MAP_NAME"), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
            SqlCondition cond2 = new SqlCondition("MAP_ID", model.get("MAP_ID"), SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric);
            if (dal.isExist(dal.getTableName(), cond, cond2) > 0)
            {
                result = "-3"; // 已存在同名拓扑
            }
            else
            {
                TopoMapEntity entity = dal.get(Integer.parseInt(model.get("MAP_ID")));

                if (entity != null)
                {
                    String groupId = String.valueOf(entity.getGroupId()); // 默认组ID
                    String mapState = String.valueOf(entity.getMapState()); // 状态
                    String filePath = entity.getMapInfo(); // 文件路径

                    // 如果上级拓扑图不为空,则修改图的上级图关联
                    if (model.get("PARENT_ID") != null && !"".equals(model.get("PARENT_ID")))
                    {
                        sqlExecutor.executeNonQuery("UPDATE BMP_GROUP2GROUP SET PARENT_ID='" + model.get("PARENT_ID") + "' WHERE GROUP_ID='"
                            + groupId + "' AND USE_TYPE=2 "); // USE_TYPE=2代表拓扑图与拓扑图的父子关系
                    }

                    // 对激活或缺省激活的拓扑图，重新处理绑定关系
                    if (model.get("MAP_STATE") != null)
                    {
                        if (!"0".equals(model.get("MAP_STATE")))
                        {

                            String mapInfo = new TopoXmlFile().readTopoMapFile(filePath);

                            if ("-1".equals(mapInfo) || "-2".equals(mapInfo))
                            {
                                sqlExecutor.transRollback();
                                return mapInfo;
                            }

                            // 重新处理绑定关系
                            topoHandle(model.get("MAP_ID"), groupId, mapInfo);
                        }
                        else if (!"0".equals(mapState))
                        {
                            // 若修改后状态为新建，但修改前本身的状态不为新建，则删除原来的绑定关系
                            deleteTopoBind(model.get("MAP_ID"), groupId);
                        }
                    }
                    else
                    {
                        if (!"0".equals(mapState))
                        {
                            String mapInfo = new TopoXmlFile().readTopoMapFile(filePath);
                            if ("-1".equals(mapInfo) || "-2".equals(mapInfo))
                            {
                                sqlExecutor.transRollback();
                                return mapInfo;
                            }

                            // 重新处理绑定关系
                            topoHandle(model.get("MAP_ID"), groupId, mapInfo);
                        }
                    }

                    new TopoMapDal().update(model);
                }
            }

            // sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            // sqlExecutor.transRollback();
            throw ex;
        }

        return result;
    }

    /**
     * 修改拓扑图XML 若传入objXml中"MAP_INFO"为空，代表不修改拓扑图XML文件； 若不为空，代表要修改XML文件； 若修改了XML文件，则数据库中的路径也要修改； 若未修改XML文件，则读取原XML文件信息； 若修改后状态不为新建，则重新处理绑定关系。
     * 若修改后为新建，修改前不为新建，则删除原有的绑定关系;
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public String updateTopoMapXml(String objXml) throws Exception
    {
        String result = "";

        try
        {
            HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

            Document doc =
                sqlExecutor.fill("SELECT MAP_INFO,MAP_STATE,GROUP_ID FROM BMP_TOPOMAP WHERE MAP_ID=" + model.get("MAP_ID"), "DataSource",
                    "BMP_TOPOMAP");

            List list = doc.selectNodes("/DataSource/BMP_TOPOMAP");
            Element ele = (Element) list.get(0);
            String filePath = ele.element("MAP_INFO").getText(); // 文件路径
            String mapState = ele.element("MAP_STATE").getText(); // 状态
            String groupId = ele.element("GROUP_ID").getText(); // 对象组ID

            String mapInfo = "";
            if ((model.get("MAP_INFO") != null) && (!"".equals(model.get("MAP_INFO"))))
            {
                mapInfo = model.get("MAP_INFO"); // 如果存在新的拓扑图信息，则赋新值
                String path = new TopoXmlFile().writeTopoMapFile("BMP_TOPOMAP", model.get(TopoMapDal.PRIMARY_KEY), mapInfo);
                if (!"-1".equals(path)) // 如果成功
                {
                    model.put("MAP_INFO", path); // 更改MAP_INFO的值，存相对路径
                }
                else
                {
                    result = path;
                }
            }
            else
            {
                mapInfo = new TopoXmlFile().readTopoMapFile(filePath); // 如果未修改拓扑图信息，则读取XML文件，赋旧值
            }

            boolean flag = false;

            // 对激活或缺省激活的拓扑图，重新处理绑定关系
            if (!"0".equals(mapState))
            {

                // 若修改前的状态不为新建，则重新绑定关联
                flag = true;
            }

            if (flag && !"-1".equals(mapInfo) && !"-2".equals(mapInfo))
            {

                // sqlExecutor.transBegin();

                // 重新处理绑定关系
                topoHandle(model.get("MAP_ID"), groupId, mapInfo);
            }

            // sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            // sqlExecutor.transRollback();
            throw ex;
        }

        return result;
    }

    /**
     * 删除
     * @param keyId 主键ID
     * @return 1：删除成功； -1：删除不成功或异常； -2：图标文件不存在；
     * @throws Exception 异常
     */
    public String deleteTopoMap(int keyId) throws Exception
    {
        String result = "";
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();

        // exec.transBegin();
        try
        {
            String groupId = "";

            TopoMapEntity mapEntity = new TopoMapDal().get(keyId);
            if (mapEntity != null)
            {
                groupId = Integer.toString(mapEntity.getGroupId());
            }

            // 递归删除子对象组
            List<Group2GroupEntity> list = new ArrayList<Group2GroupEntity>();
            new ObjGroup().getAllSubGroups(list, Integer.valueOf(groupId), "2");

            String groupIds = groupId;
            if (list != null && list.size() > 0)
            {
                for (Group2GroupEntity et : list)
                {
                    groupIds += "," + et.getGroupId();
                }
            }

            // 获取子对象组关联的所有子拓扑图
            String mapIds = "";
            List<TopoMapEntity> mapList = new TopoMapDal().getLst("SELECT * FROM BMP_TOPOMAP WHERE GROUP_ID IN (" + groupIds + ")");
            if (mapList != null && mapList.size() > 0)
            {
                for (int i = 0; i < mapList.size(); i++)
                {
                    TopoMapEntity et2 = mapList.get(i);
                    if (i != mapList.size() - 1)
                    {
                        mapIds += et2.getMapId() + ",";
                    }
                    else
                    {
                        mapIds += et2.getMapId();
                    }

                    // 删除
                    new TopoMapDal().deleteById(et2.getMapId());
                }
            }

            sqlExecutor.executeNonQuery("DELETE FROM BMP_OBJGROUP WHERE GROUP_TYPE=5 AND GROUP_ID IN (" + groupIds + ")");
            sqlExecutor.executeNonQuery("DELETE FROM BMP_OBJ2GROUP WHERE GROUP_ID IN (" + groupIds + ") AND USE_TYPE=3");
            // 删除BMP_OBJ2GROUP表中该拓扑图与对象的所有绑定关系
            sqlExecutor.executeNonQuery("DELETE FROM BMP_GROUP2GROUP WHERE GROUP_ID IN (" + groupIds + ") OR PARENT_ID='" + groupId + "'");
            sqlExecutor.executeNonQuery("DELETE FROM BMP_TOPONODE WHERE MAP_ID IN (" + mapIds + ") OR GROUP_ID IN (" + groupIds + ")");
            // 删除节点与对象（组）关系
            sqlExecutor.executeNonQuery("DELETE FROM BMP_ROLETOPOAUTHORITY WHERE MAP_ID IN (" + mapIds + ")"); // 删除该拓扑图与角色的权限关联
            // exec.transCommit();

            // 删除XML文件
            if (mapList != null && mapList.size() > 0)
            {
                for (TopoMapEntity map : mapList)
                {
                    new TopoXmlFile().deleteTopoMapFile(map.getMapInfo());
                }
            }
        }
        catch (Exception ex)
        {
            // exec.transRollback();
            result = "-1";
        }

        return result;
    }

    /**
     * 获取拓扑图信息包括XML文件内容
     * @param topoId 拓扑图ID
     * @return 返回值
     * @throws Exception 异常
     */
    public String queryTopoMapById(int topoId) throws Exception
    {
        String result = "";
        try
        {
            Document doc =
                sqlExecutor.fill("SELECT A.*,B.GROUP_NAME FROM BMP_TOPOMAP A LEFT JOIN BMP_OBJGROUP B ON A.GROUP_ID=B.GROUP_ID WHERE MAP_ID="
                    + topoId, "DataSource", "BMP_TOPOMAP");

            List list = doc.selectNodes("/DataSource/BMP_TOPOMAP");
            Element ele = (Element) list.get(0);
            String filePath = ele.element("MAP_INFO").getText(); // 文件路径

            String mapInfo = new TopoXmlFile().readTopoMapFile(filePath); // 读取XML文件
            if (!"-1".equals(mapInfo) && !"-2".equals(mapInfo))
            {

                // 为返回XML添加新节点XML_INFO表示该图的XML信息
                Element e = ele.addElement("XML_INFO");
                e.setText(mapInfo);
                result = doc.asXML();
            }
            else
            {
                result = mapInfo;
            }
        }
        catch (Exception ex)
        {
            result = "-1";
        }

        return result;
    }

    /**
     * 查询拓扑图列表
     * @param ifView :是否展现拓扑图时获取，展现时不获取状态为“新建”的图
     * @param field_1 :0代表自建图，不可删除和修改的图；1代表可以修改和删除的图；
     * @param filterId :过滤哪些拓扑图
     * @param userId :登陆用户ID
     * @return 结果
     * @throws Exception 异常
     */
    public String queryTopoMaps(String field_1, boolean ifView, String filterId, String userId) throws Exception
    {
        String cmd =
            "SELECT DISTINCT A.*,B.PARENT_ID FROM BMP_TOPOMAP A " + "LEFT JOIN BMP_GROUP2GROUP B ON A.GROUP_ID=B.GROUP_ID "
                + "LEFT JOIN BMP_ROLETOPOAUTHORITY C ON A.MAP_ID=C.MAP_ID " + "LEFT JOIN UUM_USERTOROLE D ON C.ROLE_ID=D.ROLE_ID "
                + " WHERE ((B.USE_TYPE = 2) OR (B.USE_TYPE IS NULL)) ";

        if (!StringUtil.isNullOrEmpty(field_1))
        {
            cmd += " AND A.FIELD_1 = " + field_1;
        }

        if (ifView)
        {
            cmd += " AND A.MAP_STATE <> 0 ";
        }

        if (!StringUtil.isNullOrEmpty(filterId))
        {
            cmd += " AND A.MAP_ID NOT IN (" + filterId + ") ";
        }

        cmd += " AND ((B.PARENT_ID IS NULL) OR ((B.PARENT_ID IS NOT NULL) AND (D.USER_ID = '" + userId + "'))) ORDER BY A.MAP_ID ";

        return sqlExecutor.fill(cmd).asXML();
    }

    /**
     * 删除原有的绑定关系
     * @param mapId 拓扑图ID
     * @param groupId 拓扑图默认的对象组ID
     * @throws Exception
     */
    private void deleteTopoBind(String mapId, String groupId) throws Exception
    {
        try
        {
            sqlExecutor.executeNonQuery("DELETE FROM BMP_TOPONODE WHERE MAP_ID=" + mapId);
            sqlExecutor.executeNonQuery("DELETE FROM BMP_OBJ2GROUP WHERE GROUP_ID=" + groupId + " AND USE_TYPE=3");
            // 删除BMP_OBJ2GROUP表中该拓扑图与对象的所有绑定关系
            sqlExecutor.executeNonQuery("DELETE FROM BMP_GROUP2GROUP WHERE PARENT_ID=" + groupId + " AND USE_TYPE=1");
            // 删除BMP_GROUP2GROUP表中该拓扑图绑定的子对象组关系
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**
     * 处理拓扑图绑定关系 1、先删除BMP_TOPONODE、BMP_OBJ2GROUP、BMP_GROUP2GROUP表中已有的对应的绑定关系; 2、解析XML。 3、对绑定了对象的节点，BMP_TOPONODE表添加节点与对象的关联，NODE_TYPE为1；
     * BMP_OBJ2GROUP表添加对象与对象组的关联，USE_TYPE为3，代表拓扑图中的绑定关系； 4、对绑定了对象组的节点，BMP_TOPONODE表添加节点与对象组的关联，NODE_TYPE为2；
     * BMP_GROUP2GROUP表添加对象组与对象组的关联，USE_TYPE为1，代表拓扑图中的绑定关系； 5、对绑定了子拓扑图的节点，先查出该子拓扑图的默认对象组ID，然后BMP_TOPONODE表添加节点与对象组的关联，NODE_TYPE为2；
     * BMP_GROUP2GROUP表添加子图默认对象组与本图对象组的关联，USE_TYPE为1，代表拓扑图中的绑定关系；
     * @param mapInfo 拓扑图XML
     * @throws Exception
     */
    private void topoHandle(String mapId, String groupId, String mapInfo) throws Exception
    {
        try
        {
            if (!mapId.trim().isEmpty() && !groupId.trim().isEmpty() && !mapInfo.trim().isEmpty())
            {
                // 先删除原记录
                deleteTopoBind(mapId, groupId);

                // 重新插入
                Document info = DocumentHelper.parseText(mapInfo);
                List infoList = info.selectNodes("/Topology/Node");
                Iterator it = infoList.iterator();
                while (it.hasNext())
                {
                    Element e = (Element) it.next();
                    String nodeId = e.attribute("Id").getValue();
                    String nodeName = e.attribute("Name").getValue();
                    String nodeBindType = e.attribute("BindType").getValue();
                    String nodeBindId = e.attribute("BindID").getValue();
                    String subTopoId = e.attribute("SubTopoId").getValue();

                    if ("object".equals(nodeBindType) && !"".equals(nodeBindId.trim())) // 如果节点绑定了对象
                    {
                        sqlExecutor.executeNonQuery("INSERT INTO BMP_TOPONODE VALUES('" + nodeId + "','" + mapId + "','" + nodeName + "',1,'"
                            + nodeBindId + "',NULL)");

                        Object isExist =
                            sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand(
                                "SELECT 1 FROM BMP_OBJ2GROUP WHERE OBJ_ID=%s AND GROUP_ID=%s AND USE_TYPE=3",
                                new SqlValue(nodeBindId, SqlParamType.Numeric), new SqlValue(groupId, SqlParamType.Numeric)));
                        if (isExist == null)
                        {
                            sqlExecutor.executeNonQuery("INSERT INTO BMP_OBJ2GROUP VALUES('" + nodeBindId + "','" + groupId + "',3)"); // 3代表拓扑图中的绑定关系
                        }

                    }
                    else if ("objgroup".equals(nodeBindType) && !"".equals(nodeBindId.trim())) // 如果节点绑定了对象组
                    {
                        sqlExecutor.executeNonQuery("INSERT INTO BMP_TOPONODE VALUES('" + nodeId + "','" + mapId + "','" + nodeName + "',2,NULL,'"
                            + nodeBindId + "')");
                        Object isExist =
                            sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand(
                                "SELECT 1 FROM BMP_GROUP2GROUP WHERE GROUP_ID=%s AND PARENT_ID=%s AND USE_TYPE=1",
                                new SqlValue(nodeBindId, SqlParamType.Numeric), new SqlValue(groupId, SqlParamType.Numeric)));
                        if (isExist == null)
                        {
                            sqlExecutor.executeNonQuery("INSERT INTO BMP_GROUP2GROUP VALUES ('" + nodeBindId + "','" + groupId + "',1)"); // 类型1代表绑定关系
                        }
                    }

                    // 如果存在子拓扑图，则保存给节点与该子拓扑图所属的对象组
                    if (!"".equals(subTopoId.trim()))
                    {

                        // 先查询该子拓扑图所属的对象组ID
                        final String[] subGroupId = new String[] { "" };
                        String sql = "SELECT GROUP_ID FROM BMP_TOPOMAP WHERE MAP_ID=" + subTopoId;
                        DefaultDal.read(sql, new IReadHandle()
                        {
                            @Override
                            public void handle(ResultSet rs) throws Exception
                            {
                                if (rs.next())
                                {
                                    subGroupId[0] = rs.getString("GROUP_ID");
                                }
                            }
                        });

                        // 插入节点与对象组关系
                        if (!StringUtil.isNullOrEmpty(groupId))
                        {
                            sqlExecutor.executeNonQuery("INSERT INTO BMP_TOPONODE VALUES('" + nodeId + "'," + mapId + ",'" + nodeName + "',2,NULL,"
                                + subGroupId[0] + ")");
                            sqlExecutor.executeNonQuery("INSERT INTO BMP_GROUP2GROUP VALUES ('" + subGroupId[0] + "','" + groupId + "',1)"); // 类型1代表绑定关系
                        }

                    }
                }
            }
        }
        catch (Exception ex)
        {

            throw ex;
        }
    }

}
