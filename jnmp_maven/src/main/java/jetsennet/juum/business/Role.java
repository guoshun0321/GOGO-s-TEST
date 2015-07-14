package jetsennet.juum.business;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.juum.dataaccess.RoleDal;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.FormatUtil;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author ？
 */
public class Role
{
    /**
     * 构造函数。初始化
     */
    public Role()
    {
        uumConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("uum_driver"), DbConfig.getProperty("uum_dburl"), DbConfig.getProperty("uum_dbuser"), DbConfig
                .getProperty("uum_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(uumConnectionInfo);
        dalRole = new RoleDal(sqlExecutor);
    }

    private ConnectionInfo uumConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private RoleDal dalRole;

    /**
     * 新增角色
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int addRole(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        int roleId = 0;
        sqlExecutor.transBegin();
        try
        {
            Object obj =
                sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand("select 1 from UUM_ROLE Where NAME=%s",
                    new SqlValue(model.get("NAME"))));
            if (obj != null)
            {
                throw new Exception("角色名称重复!");
            }

            roleId = sqlExecutor.getNewId(RoleDal.TABLE_NAME);
            model.put(RoleDal.PRIMARY_KEY, String.valueOf(roleId));
            // new java.rmi.server.UID().toString();
            dalRole.add(model);

            String[] userIds = model.get("ROLE_USER").split(",");
            if (userIds.length > 0)
            {
                for (int i = 0; i < userIds.length; i++)
                {
                    if (userIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOROLE (USER_ID,ROLE_ID) VALUES (%s,%s)", new SqlValue(userIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            String[] functionIds = model.get("ROLE_FUNCTION").split(",");
            if (functionIds.length > 0)
            {
                for (int i = 0; i < functionIds.length; i++)
                {
                    if (functionIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_ROLEAUTHORITY (FUNCTION_ID,ROLE_ID) VALUES (%s,%s)", new SqlValue(functionIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            String[] topoIds = model.get("ROLE_TOPO").split(",");
            if (topoIds.length > 0)
            {
                for (int i = 0; i < topoIds.length; i++)
                {
                    if (topoIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO BMP_ROLETOPOAUTHORITY (MAP_ID,ROLE_ID) VALUES (%s,%s)", new SqlValue(topoIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }

        return roleId;
    }

    /**
     * 编辑角色
     * @param objXml 参数
     * @throws Exception 异常
     */
    private List<String> mapList;

    public void updateRole(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        sqlExecutor.transBegin();
        try
        {
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("DELETE FROM UUM_USERTOROLE WHERE ROLE_ID=%s",
                new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("DELETE FROM UUM_ROLEAUTHORITY WHERE ROLE_ID=%S",
                new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand("DELETE FROM BMP_ROLETOPOAUTHORITY WHERE ROLE_ID=%S",
                new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));

            String[] userIds = model.get("ROLE_USER").split(",");
            if (userIds.length > 0)
            {
                for (int i = 0; i < userIds.length; i++)
                {
                    if (userIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_USERTOROLE (USER_ID,ROLE_ID) VALUES (%s,%s)", new SqlValue(userIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            String[] functionIds = model.get("ROLE_FUNCTION").split(",");
            if (functionIds.length > 0)
            {
                for (int i = 0; i < functionIds.length; i++)
                {
                    if (functionIds[i].length() > 0)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO UUM_ROLEAUTHORITY (FUNCTION_ID,ROLE_ID) VALUES (%s,%s)", new SqlValue(functionIds[i], SqlParamType.Numeric),
                            new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            String StrRoleTopo = model.get("ROLE_TOPO");
            if(StrRoleTopo != null && !"".equals(StrRoleTopo)){
                String[] topoIds = model.get("ROLE_TOPO").split(",");
                
//            if (topoIds.length > 0)
//            {
//                mapList = new ArrayList<String>();
//                for (int i = 0; i < topoIds.length; i++)
//                {
//                    if (topoIds[i].length() > 0)
//                    {
//                        if (!mapList.contains(topoIds[i]))
//                        {
//                            mapList.add(topoIds[i]);
//                        }
//
//                        final List<String> groupList = new ArrayList<String>();
//                        String sql =
//                            "SELECT A.GROUP_ID FROM BMP_TOPOMAP A LEFT JOIN BMP_GROUP2GROUP B ON A.GROUP_ID=B.GROUP_ID WHERE (B.USE_TYPE = 2 or B.USE_TYPE is null) and A.MAP_STATE <> 0 and A.MAP_ID = "
//                                + topoIds[i];
//                        DefaultDal.read(sql, new IReadHandle()
//                        {
//                            @Override
//                            public void handle(ResultSet rs) throws Exception
//                            {
//                                while (rs.next())
//                                {
//                                    groupList.add(rs.getString("GROUP_ID"));
//                                }
//                            }
//                        });
//
//                        getAllMapByGroupId(groupList);
//                    }
//                }
//            }

                if (topoIds.length > 0)
                {
                    for (String map : topoIds)
                    {
                        sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().formatCommand(
                            "INSERT INTO BMP_ROLETOPOAUTHORITY (MAP_ID,ROLE_ID) VALUES (%s,%s)", new SqlValue(map, SqlParamType.Numeric),
                            new SqlValue(model.get(RoleDal.PRIMARY_KEY), SqlParamType.Numeric)));
                    }
                }
            }

            dalRole.update(model);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    private void getAllMapByGroupId(List<String> groupList) throws Exception
    {
        if (mapList != null && groupList != null)
        {
            for (String groupId : groupList)
            {
                final List<String> temp = new ArrayList<String>();

                String sql =
                    "SELECT A.*,B.PARENT_ID FROM BMP_TOPOMAP A LEFT JOIN BMP_GROUP2GROUP B ON A.GROUP_ID=B.GROUP_ID WHERE (B.USE_TYPE = 2 or B.USE_TYPE is null) and A.MAP_STATE <> 0 and B.PARENT_ID = "
                        + groupId;
                DefaultDal.read(sql, new IReadHandle()
                {
                    @Override
                    public void handle(ResultSet rs) throws Exception
                    {
                        while (rs.next())
                        {
                            temp.add(rs.getString("GROUP_ID"));
                            if (!mapList.contains(rs.getString("MAP_ID")))
                            {
                                mapList.add(rs.getString("MAP_ID"));
                            }
                        }
                    }
                });

                if (temp.size() > 0)
                {
                    getAllMapByGroupId(temp);
                }
            }
        }
    }

    /**
     * 删除角色
     * @param keyId 参数
     * @throws Exception 异常
     */
    public void deleteRole(int keyId) throws Exception
    {
        sqlExecutor.transBegin();
        try
        {
            sqlExecutor.executeNonQuery("DELETE FROM UUM_USERTOROLE WHERE ROLE_ID = " + keyId);
            sqlExecutor.executeNonQuery("DELETE FROM UUM_ROLEAUTHORITY WHERE ROLE_ID = " + keyId);
            sqlExecutor.executeNonQuery("DELETE FROM BMP_ROLETOPOAUTHORITY WHERE ROLE_ID = " + keyId);
            dalRole.deleteById(keyId);
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }

    /**
     * 删除角色
     * @param roleXml 参数
     * @throws Exception 异常
     */
    public void deleteRoles(String roleXml) throws Exception
    {
        Document xmlDoc = DocumentHelper.parseText(roleXml);
        Element rootNode = xmlDoc.getRootElement();

        List<Node> itemNodes = rootNode.selectNodes("Item");

        List<String> roleList = new ArrayList<String>();
        for (Node itemNode : itemNodes)
        {
            roleList.add(FormatUtil.tryGetItemText(itemNode, "Id", "-1"));
        }

        String[] arrRoles = new String[roleList.size()];
        arrRoles = roleList.toArray(arrRoles);
        String roleIds = StringUtil.join(arrRoles, ",");

        sqlExecutor.transBegin();
        try
        {
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_USERTOROLE",
                new SqlCondition("ROLE_ID", roleIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_ROLEAUTHORITY",
                new SqlCondition("ROLE_ID", roleIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("BMP_ROLETOPOAUTHORITY",
                new SqlCondition("ROLE_ID", roleIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));
            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getDeleteCommandString("UUM_ROLE",
                new SqlCondition("ID", roleIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));

            sqlExecutor.transCommit();

        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            throw ex;
        }
    }
    
   public String isHasEditTopoRight(String roleId) throws Exception
   {
       sqlExecutor.transBegin();
           Object obj =
               sqlExecutor.executeScalar(sqlExecutor.getSqlParser().formatCommand("select 1 from UUM_ROLEAUTHORITY Where function_id=2001300 and role_id=%s",
                   new SqlValue(roleId)));
           if (obj != null)
           {
               return "true";
           }else{
               return "";
           }
}
}