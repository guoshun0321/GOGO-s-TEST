package jetsennet.jbmp.business;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.Role2GroupDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.sqlclient.ISqlExecutor;

/************************************************************************
 * 日 期：2012-06-27 作 者: liwei 描 述: 对象组权限的相关操作 历 史：
 ************************************************************************/
public class RoleGroup
{

    private static final Logger logger = Logger.getLogger(RoleGroup.class);

    /**
     * 构造函数
     */
    public RoleGroup()
    {
    }

    /**
     * 李巍 新建对象组后把对象组的权限添加到角色中
     * @param id 对象组id
     * @param userId 用户id
     */
    @Business
    public void saveRoleGroup(int id, int userId)
    {
        ArrayList<Integer> roleIds = getRoleId(userId);
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        if (roleIds != null)
        {
            Iterator<Integer> iter = roleIds.iterator();
            while (iter.hasNext())
            {
                int roleId = iter.next();
                try
                {
                    exec.executeNonQuery("INSERT INTO BMP_ROLE2GROUP VALUES ('" + roleId + "', '" + id + "') ");
                    logger.info("INSERT INTO BMP_ROLE2GROUP VALUES ('" + roleId + "', '" + id + "') ");
                }
                catch (SQLException e)
                {
                    logger.info(e);
                }
            }
        }

    }

    /**
     * 李巍 获取用户的角色ID
     * @param userId 用户id
     * @return 结果
     */
    @Business
    public ArrayList<Integer> getRoleId(int userId)
    {
        final ArrayList<Integer> roleIds = new ArrayList<Integer>();

        try
        {
            String sql = "select distinct a.ROLE_ID as roleId from  UUM_USERTOROLE a where a.USER_ID = " + userId;
            DefaultDal.read(sql, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        roleIds.add(rs.getInt("roleId"));
                    }
                }
            });
        }
        catch (Exception e)
        {
            logger.info(e);
        }
        return roleIds;
    }

    /**
     * 通过采集器ID获取用户组id
     * @param collId 采集器ID
     * @return 结果
     * @throws SQLException 异常
     */
    @Business
    public ArrayList<Integer> getCollRole(int collId) throws SQLException
    {
        final ArrayList<Integer> groupIds = new ArrayList<Integer>();
        try
        {
            String sql = "SELECT GROUP_ID FROM BMP_OBJGROUP WHERE NUM_VAL1 =  " + collId;
            DefaultDal.read(sql, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        groupIds.add(rs.getInt("GROUP_Id"));
                    }
                }
            });
        }
        catch (Exception ex)
        {
            throw new SQLException(ex);
        }
        return groupIds;
    }

    /**
     * 将用户新建的采集器与用户的角色关联
     * @param collId 采集器id
     * @param userId 用户id
     */
    public void saveCollRole(int collId, int userId)
    {
        ArrayList<Integer> groupCollIds = null;
        try
        {
            groupCollIds = getCollRole(collId);
        }
        catch (SQLException e)
        {
            logger.info(e);
        }
        if (groupCollIds != null)
        {
            Iterator<Integer> itercoll = groupCollIds.iterator();
            while (itercoll.hasNext())
            {
                int groupId = itercoll.next();
                saveRoleGroup(groupId, userId);
            }
        }
    }

    /**
     * 插入
     * @param groupId 采集组id
     * @param roleIds 对象组ids
     * @throws Exception 异常
     */
    @Business
    public void insert(int groupId, ArrayList<Integer> roleIds) throws Exception
    {
        Role2GroupDal rgdal = new Role2GroupDal();
        if (groupId > 0 && roleIds != null && roleIds.size() > 0)
        {
            for (Integer roleId : roleIds)
            {
                rgdal.delete(roleId, groupId);
                rgdal.insert(roleId, groupId);
            }
        }
    }

    /**
     * 插入
     * @param groupId 采集组id
     * @param roleId 对象组id
     * @throws Exception 异常
     */
    @Business
    public void insert(int groupId, int roleId) throws Exception
    {
        Role2GroupDal rgdal = new Role2GroupDal();
        if (groupId > 0 && roleId > 0)
        {
            rgdal.delete(roleId, groupId);
            rgdal.insert(roleId, groupId);
        }
    }

}
