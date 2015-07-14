/*
 * 日期：2013年2月23日
 * 描述：节点关联数据库访问类
 * 作者：郭世平
 */
package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.AlarmObjRelEntity;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

public class AlarmObjRelDal extends DefaultDal<AlarmObjRelEntity>
{
    public AlarmObjRelDal()
    {
        super(AlarmObjRelEntity.class);
    }

    /**
     * 查询设备的关联关系，如果某些设备已经被删除，那么相应的设备关联和报警关联也要删除
     * @return
     * @throws Exception
     */
    public String queryRelation() throws Exception
    {
        String sql =
            "SELECT A.*,B.OBJ_NAME,B.CLASS_ID,B.CLASS_TYPE FROM BMP_ALARMOBJREL A LEFT JOIN BMP_OBJECT B ON A.OBJ_ID =B.OBJ_ID WHERE B.OBJ_NAME IS NULL";
        final List<Map<String, String>> deleteList = new ArrayList<Map<String, String>>();
        DefaultDal.read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                while (rs.next())
                {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(String.valueOf(rs.getInt("REL_ID")), String.valueOf(rs.getInt("OBJ_ID")));
                    deleteList.add(map);
                }
            }

        });
        for (Map<String, String> map : deleteList)
        {
            for (String key : map.keySet())
            {
                deleteAlarmObjRel(key, map.get(key));
            }
        }
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        String querySql =
            "SELECT A.*,B.OBJ_NAME,B.CLASS_ID,B.CLASS_TYPE,E.ICON_SRC FROM BMP_ALARMOBJREL A LEFT JOIN BMP_OBJECT B ON A.OBJ_ID =B.OBJ_ID LEFT JOIN BMP_ATTRIBCLASS E ON B.CLASS_ID = E.CLASS_ID";
        ds = exec.fill(querySql);
        return ds.asXML();
    }

    /**
     * 查询所有采集器，如果该采集器删除了，那么该采集器下的所有关联都要删除
     * @return
     * @throws Exception
     */
    public String queryAllColl() throws Exception
    {
        String sql =
            "SELECT DISTINCT A.COLL_ID,B.COLL_NAME FROM BMP_ALARMOBJREL A LEFT JOIN BMP_COLLECTOR B ON A.COLL_ID = B.COLL_ID WHERE B.COLL_NAME IS NULL";
        final List<Integer> deleteColls = new ArrayList<Integer>();
        DefaultDal.read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                while (rs.next())
                {
                    deleteColls.add(rs.getInt("COLL_ID"));
                }
            }

        });
        StringBuilder sb = new StringBuilder();
        for (Integer i : deleteColls)
        {
            sb.append(i);
            sb.append(",");
        }
        if (!"".equals(sb.toString()))
        {
            String querySql = "SELECT * FROM BMP_ALARMOBJREL WHERE COLL_ID IN (" + sb.toString().substring(0, sb.toString().lastIndexOf(",")) + ")";
            List<AlarmObjRelEntity> deleteAlarmObjRelEntity = getLst(querySql);
            for (AlarmObjRelEntity entity : deleteAlarmObjRelEntity)
            {
                deleteAlarmObjRel(String.valueOf(entity.getRelId()), String.valueOf(entity.getObjId()));
            }
        }
        String resultSql = "SELECT * FROM BMP_COLLECTOR";
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        ds = exec.fill(resultSql);
        return ds.asXML();
    }

    /**
     * 删除节点、如果有子节点也要删除，并且同时删除BMP_ALARMOBJATTRREL表中数据
     * @param relId
     * @throws Exception
     */
    public void deleteAlarmObjRel(String relId, String objId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        boolean isTrans = exec.getIsTransing();
        try
        {
            if (!isTrans)
            {
                exec.transBegin();
            }
            this.delete(Integer.parseInt(relId));
            StringBuilder sbRel = new StringBuilder();
            StringBuilder sbObj = new StringBuilder();
            queryAllChild(relId, sbRel, objId, sbObj);
            String relIds = sbRel.toString().substring(0, sbRel.toString().length() - 1);
            String objIds = sbObj.toString().substring(0, sbObj.toString().length() - 1);

            delete("DELETE FROM BMP_ALARMOBJREL WHERE REL_ID IN (" + relIds + ")");
            delete("DELETE FROM BMP_ALARMOBJATTRREL WHERE OBJ_ID IN (" + objIds + ") OR OBJ_PID IN (" + objIds + ")");
            if (!isTrans)
            {
                exec.transCommit();
            }
        }
        catch (Exception ex)
        {
            if (!isTrans)
            {
                exec.transRollback();
            }
            throw ex;
        }
        finally
        {
            if (!isTrans)
            {
                SqlExecutorFacotry.unbindSqlExecutor();
            }
        }
    }

    private void queryAllChild(String relId, StringBuilder sbRel, String objId, StringBuilder sbObj)
    {
        try
        {
            List<AlarmObjRelEntity> children = this.getLst("SELECT * FROM BMP_ALARMOBJREL WHERE PARENT_ID = " + relId);
            if (children != null && children.size() > 0)
            {
                for (AlarmObjRelEntity entity : children)
                {
                    sbRel.append(relId);
                    sbRel.append(",");
                    sbObj.append(objId);
                    sbObj.append(",");
                    queryAllChild(String.valueOf(entity.getRelId()), sbRel, String.valueOf(entity.getObjId()), sbObj);
                }
            }
            else
            {
                sbRel.append(relId);
                sbRel.append(",");
                sbObj.append(objId);
                sbObj.append(",");
            }
        }
        catch (Exception e)
        {

        }
    }

    /**
     * 查询当前节点所在的树
     * @param objId 对象ID
     * @return
     * @throws Exception
     */
    public String queryCurrentTree(String objId) throws Exception
    {
        List<AlarmObjRelEntity> entitys = new ArrayList<AlarmObjRelEntity>();
        AlarmObjRelEntity entity = get(new SqlCondition("OBJ_ID", objId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (null != entity)
        {
            entitys.add(entity);
            queryCurrentParentTree(entity, entitys);
            queryCurrentChildTree(entity, entitys);
        }
        StringBuilder sb = new StringBuilder();
        for (AlarmObjRelEntity temp : entitys)
        {
            sb.append(temp.getRelId());
            sb.append(",");
        }
        String relIds = sb.toString().substring(0, sb.toString().length() - 1);
        String sql =
            "SELECT A.*,B.COLL_NAME, C.OBJ_NAME, D.ICON_SRC FROM BMP_ALARMOBJREL A LEFT JOIN BMP_COLLECTOR B ON A.COLL_ID = B.COLL_ID LEFT JOIN BMP_OBJECT C ON A.OBJ_ID = C.OBJ_ID LEFT JOIN BMP_ATTRIBCLASS D ON C.CLASS_ID = D.CLASS_ID WHERE A.REL_ID IN ("
                + relIds + ")";
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        ds = exec.fill(sql);
        return ds.asXML();
    }

    /**
     * 查询所有的父节点
     * @param entity
     * @param entitys
     */
    private void queryCurrentParentTree(AlarmObjRelEntity entity, List<AlarmObjRelEntity> entitys)
    {
        try
        {
            if (null != entity)
            {
                AlarmObjRelEntity tempEntity =
                    get(new SqlCondition("REL_ID", String.valueOf(entity.getParentId()), SqlLogicType.And, SqlRelationType.Equal,
                        SqlParamType.Numeric));
                if (null != tempEntity)
                {
                    entitys.add(tempEntity);
                    queryCurrentParentTree(tempEntity, entitys);
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    /**
     * 查询所有的子节点
     * @param entity
     * @param entitys
     */
    private void queryCurrentChildTree(AlarmObjRelEntity entity, List<AlarmObjRelEntity> entitys)
    {
        try
        {
            if (null != entity)
            {
                List<AlarmObjRelEntity> list =
                    getLst(new SqlCondition("PARENT_ID", String.valueOf(entity.getRelId()), SqlLogicType.And, SqlRelationType.Equal,
                        SqlParamType.Numeric));
                for (AlarmObjRelEntity tempEntity : list)
                {
                    if (null != tempEntity)
                    {
                        entitys.add(tempEntity);
                        queryCurrentChildTree(tempEntity, entitys);
                    }
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    public static void main(String[] args)
    {
        try
        {
            new AlarmObjRelDal().queryCurrentTree("736");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
