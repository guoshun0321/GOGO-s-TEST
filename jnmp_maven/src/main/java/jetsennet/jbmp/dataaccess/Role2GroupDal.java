/************************************************************************
日 期：2011-12-01
作 者:
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.helper.AbsDBDiversityHelper;
import jetsennet.jbmp.entity.Role2GroupEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author 郭祥
 */
public class Role2GroupDal extends DefaultDal<Role2GroupEntity>
{

    private static final Logger logger = Logger.getLogger(Role2GroupDal.class);

    /**
     * 构造方法
     */
    public Role2GroupDal()
    {
        super(Role2GroupEntity.class);
    }

    /**
     * @return 结果
     */
    public boolean checkExist()
    {
        AbsDBDiversityHelper helper = AbsDBDiversityHelper.getInstance();
        return helper.checkExist(this.tableInfo.tableName);
    }

    /**
     * @param roleId 角色id
     * @param groupId 对象组
     * @throws Exception 异常
     */
    public void insert(int roleId, int groupId) throws Exception
    {
        this.insert(new Role2GroupEntity(roleId, groupId));
    }

    /**
     * @param roleId 角色id
     * @param groupId 对象组
     * @throws Exception 异常
     */
    public void delete(int roleId, int groupId) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] {
                new SqlCondition("ROLE_ID", Integer.toString(roleId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("GROUP_ID", Integer.toString(groupId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        this.delete(conds);
    }

    /**
     * 获取用户可以访问的对象
     * 
     * @param userId
     * @return
     */
    public String getObjIdsByUserId(int userId)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            String sql =
                "SELECT OBJ_ID FROM BMP_OBJ2GROUP WHERE GROUP_ID IN (SELECT GROUP_ID FROM BMP_ROLE2GROUP WHERE ROLE_ID IN(SELECT ROLE_ID FROM UUM_USERTOROLE WHERE USER_ID = %s))";
            sql = String.format(sql, userId);
            List<Object> objIdLst = DefaultDal.getFirstLst(sql);
            if (objIdLst != null)
            {
                for (Object obj : objIdLst)
                {
                    if (obj != null)
                    {
                        sb.append(obj.toString()).append(",");
                    }
                }
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
                sql = "SELECT OBJ_ID FROM BMP_OBJECT WHERE PARENT_ID IN (" + sb.toString() + ")";
                objIdLst = DefaultDal.getFirstLst(sql);
                if (objIdLst != null)
                {
                    for (Object obj : objIdLst)
                    {
                        if (obj != null)
                        {
                            sb.append(",").append(obj.toString());
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return sb.toString();
    }
    
    public static void main(String[] args)
    {
        Role2GroupDal rgdal = ClassWrapper.wrapTrans(Role2GroupDal.class);
        System.out.println(rgdal.getObjIdsByUserId(30));
    }

}
