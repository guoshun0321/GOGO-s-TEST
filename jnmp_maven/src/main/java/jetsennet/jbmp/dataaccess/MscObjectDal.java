/************************************************************************
日 期: 2012-3-16
作 者: 郭祥
版 本: v1.3
描 述: 
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjGroupEntity;
import jetsennet.sqlclient.ISqlExecutor;

public class MscObjectDal extends DefaultDal<MObjectEntity>
{
    private static final Logger logger = Logger.getLogger(MscObjectDal.class);

    public MscObjectDal()
    {
        super(MObjectEntity.class);
    }

    @Transactional
    public String getGroup(int objId, int userId) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            boolean isAdmin = this.isAdministrator(userId);

            ObjGroupDal ogdao = new ObjGroupDal();
            ArrayList<ObjGroupEntity> entities;
            String sql = "";
            if (isAdmin)
            {
                entities = (ArrayList<ObjGroupEntity>) ogdao.getLst("SELECT * FROM BMP_OBJGROUP WHERE GROUP_TYPE in (0,1,3,4) ORDER BY GROUP_TYPE");
                sql = "SELECT GROUP_ID FROM BMP_OBJ2GROUP WHERE USE_TYPE = 0 AND OBJ_ID = " + objId;
            }
            else
            {
                entities =
                    (ArrayList<ObjGroupEntity>) ogdao
                        .getLst("SELECT * FROM BMP_OBJGROUP WHERE group_id in (select c.group_id from uum_user a inner join uum_usertorole b on a.id = b.user_id "
                            + "inner join bmp_role2group c on b.role_id = c.role_id where a.id = " + userId + ")" + " ORDER BY GROUP_TYPE");
                sql =
                    "SELECT GROUP_ID FROM BMP_OBJ2GROUP WHERE USE_TYPE = 0 AND OBJ_ID = " + objId
                        + " and group_id in (select c.group_id from uum_user a inner join uum_usertorole b on a.id = b.user_id "
                        + "inner join bmp_role2group c on b.role_id = c.role_id where a.id = " + userId + ")";
            }

            final ArrayList<Integer> groupIds = new ArrayList<Integer>();
            DefaultDal.read(sql, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        groupIds.add(rs.getInt("GROUP_ID"));
                    }
                }
            });

            sb.append("<RecordSet>");
            for (ObjGroupEntity entity : entities)
            {
                sb.append("<Record>");
                sb.append("<GROUP_ID>");
                sb.append(entity.getGroupId());
                sb.append("</GROUP_ID>");
                sb.append("<GROUP_NAME>");
                sb.append(entity.getGroupName());
                sb.append("</GROUP_NAME>");
                sb.append("<GROUP_TYPE>");
                sb.append(entity.getGroupType());
                sb.append("</GROUP_TYPE>");
                sb.append("<IS_SELECTED>");
                if (groupIds.contains(entity.getGroupId()))
                {
                    sb.append("1");
                }
                else
                {
                    sb.append("0");
                }
                sb.append("</IS_SELECTED>");
                sb.append("</Record>");
            }
            sb.append("</RecordSet>");
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw ex;
        }
        return sb.toString();
    }

    /**
     * 判断是否是管理员
     * @param userId
     * @return
     */
    public boolean isAdministrator(int userId)
    {
        final boolean[] retval = new boolean[] { false };
        try
        {
            String sql = "select count(0) as A from UUM_USERTOROLE where ROLE_ID = 1 and USER_ID = " + userId;
            DefaultDal.read(sql, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    if (rs.next())
                    {
                        int temp = rs.getInt("A");
                        if (temp > 0)
                        {
                            retval[0] = true;
                        }
                    }
                }
            });
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval[0];
    }

    /**
     * 查询关联设备
     * @param collId采集器ID
     * @param relId关联关系ID
     * @param classId类型ID
     * @param flag标识 1：显示为未添加的设备以及已经和该节点直接关联的子节点对应的对象 2：只查询出已经直接关联的子节点对应的对象
     * @return
     * @throws Exception
     */
    public String queryObject(String collId, String relId, String classId, String flag) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        if ("1".equals(flag))
        {
            String sql = "";
            if ("".equals(relId))
            {
                sql =
                    "SELECT DISTINCT A.*,D.REL_ID FROM BMP_OBJECT A LEFT JOIN BMP_OBJ2GROUP B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_OBJGROUP C ON B.GROUP_ID = C.GROUP_ID LEFT JOIN BMP_ALARMOBJREL D ON A.OBJ_ID = D.OBJ_ID WHERE C.NUM_VAL1 = "
                        + collId + " AND A.CLASS_ID = " + classId + " AND (A.OBJ_ID NOT IN (SELECT OBJ_ID FROM BMP_ALARMOBJREL) OR D.PARENT_ID = 0)";
            }
            else
            {
                sql =
                    "SELECT DISTINCT A.*,D.REL_ID FROM BMP_OBJECT A LEFT JOIN BMP_OBJ2GROUP B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_OBJGROUP C ON B.GROUP_ID = C.GROUP_ID LEFT JOIN BMP_ALARMOBJREL D ON A.OBJ_ID = D.OBJ_ID WHERE C.NUM_VAL1 = "
                        + collId
                        + " AND A.CLASS_ID = "
                        + classId
                        + " AND (A.OBJ_ID NOT IN (SELECT OBJ_ID FROM BMP_ALARMOBJREL) OR D.PARENT_ID ="
                        + relId + ")";
            }
            ds = exec.fill(sql);
        }
        else if ("2".equals(flag))
        {
            String sql = "";
            if ("".equals(relId))
            {
                sql =
                    "SELECT DISTINCT A.*,D.REL_ID FROM BMP_OBJECT A LEFT JOIN BMP_OBJ2GROUP B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_OBJGROUP C ON B.GROUP_ID = C.GROUP_ID LEFT JOIN BMP_ALARMOBJREL D ON A.OBJ_ID = D.OBJ_ID WHERE C.NUM_VAL1 = "
                        + collId + " AND A.CLASS_ID = " + classId + " AND D.PARENT_ID = 0";
            }
            else
            {
                sql =
                    "SELECT DISTINCT A.*,D.REL_ID FROM BMP_OBJECT A LEFT JOIN BMP_OBJ2GROUP B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_OBJGROUP C ON B.GROUP_ID = C.GROUP_ID LEFT JOIN BMP_ALARMOBJREL D ON A.OBJ_ID = D.OBJ_ID WHERE C.NUM_VAL1 = "
                        + collId + " AND A.CLASS_ID = " + classId + " AND D.PARENT_ID =" + relId;
            }
            ds = exec.fill(sql);
        }

        return ds.asXML();
    }

    /**
     * 查询某对象属性的来源
     * @param objAttrId对象属性ID
     * @return
     * @throws Exception
     */
    public String querySuper(String objAttrId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        String sql =
            "SELECT DISTINCT A.*,C.ALARM_NAME FROM BMP_OBJATTRIB A LEFT JOIN BMP_ATTRIBALARM B ON A.OBJATTR_ID = B.OBJATTR_ID LEFT JOIN BMP_ALARM C ON C.ALARM_ID = B.ALARM_ID WHERE A.OBJATTR_ID IN (SELECT OBJATTR_PID FROM BMP_ALARMOBJATTRREL C LEFT JOIN BMP_ATTRIBALARM D ON C.OBJATTR_PID = D.OBJATTR_ID where C.OBJATTR_ID ="
                + objAttrId + " AND D.ALARM_ID IS NOT NULL " + ")";
        ds = exec.fill(sql);
        return ds.asXML();
    }

    /**
     *查询某对象属性的衍生
     * @param objAttrId对象属性ID
     * @return
     * @throws Exception
     */
    public String queryChild(String objAttrId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        String sql =
            "SELECT DISTINCT A.*,C.ALARM_NAME FROM BMP_OBJATTRIB A LEFT JOIN BMP_ATTRIBALARM B ON A.OBJATTR_ID = B.OBJATTR_ID LEFT JOIN BMP_ALARM C ON C.ALARM_ID = B.ALARM_ID where A.OBJATTR_ID IN (SELECT C.OBJATTR_ID FROM BMP_ALARMOBJATTRREL C LEFT JOIN BMP_ATTRIBALARM D ON C.OBJATTR_ID = D.OBJATTR_ID WHERE C.OBJATTR_PID = "
                + objAttrId + " AND D.ALARM_ID IS NOT NULL)";
        ds = exec.fill(sql);
        return ds.asXML();
    }

    /**
     * 查询某对象的对象属性，并判断对象属性是否已近关联
     * @param objId 对象ID
     * @return
     * @throws Exception
     */
    public String queryAttrWithRel(String objId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        String sql =
            "select DISTINCT A.*,C.ALARM_NAME,C.ALARM_ID,(SELECT COUNT(REL_ID ) FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_ID = A.OBJATTR_ID OR OBJATTR_PID = A.OBJATTR_ID) AS NUM from BMP_OBJATTRIB A LEFT JOIN BMP_ATTRIBALARM B ON A.OBJATTR_ID = B.OBJATTR_ID LEFT JOIN BMP_ALARM C ON B.ALARM_ID = C.ALARM_ID WHERE A.OBJ_ID = "
                + objId + " AND C.ALARM_ID IS NOT NULL ORDER BY A.OBJATTR_ID";
        ds = exec.fill(sql);
        return ds.asXML();
    }

    /**
     * 根据资源组ID来查询该组下的所有对象包括子对象
     * @param groupId 组ID
     * @return
     * @throws Exception
     */
    public String queryAllObjectByGroupId(String groupId) throws Exception
    {
        String sql =
            "SELECT A.* FROM BMP_OBJECT A LEFT JOIN BMP_OBJ2GROUP B ON A.OBJ_ID = B.OBJ_ID WHERE B.GROUP_ID = "
                + groupId
                + " UNION "
                + "SELECT * FROM BMP_OBJECT C WHERE C.PARENT_ID IN (SELECT A.OBJ_ID FROM BMP_OBJECT A LEFT JOIN BMP_OBJ2GROUP B ON A.OBJ_ID = B.OBJ_ID WHERE B.GROUP_ID = "
                + groupId + " )";
        List<MObjectEntity> objects = getLst(sql);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> ");
        sb.append("<RecordSet>");
        for (MObjectEntity entity : objects)
        {
            sb.append("<Record>");
            sb.append("<OBJ_ID>");
            sb.append(entity.getObjId());
            sb.append("</OBJ_ID>");
            sb.append("<PARENT_ID>");
            sb.append(entity.getParentId());
            sb.append("</PARENT_ID>");
            sb.append("<OBJ_NAME>");
            sb.append(entity.getObjName());
            sb.append("</OBJ_NAME>");
            sb.append("<CLASS_ID>");
            sb.append(entity.getClassId());
            sb.append("</CLASS_ID>");
            sb.append("</Record>");
        }
        sb.append("</RecordSet>");
        return sb.toString();
    }

    public static void main(String[] args)
    {
        MscObjectDal modal = ClassWrapper.wrapTrans(MscObjectDal.class);
        modal.isAdministrator(1);
    }

}
