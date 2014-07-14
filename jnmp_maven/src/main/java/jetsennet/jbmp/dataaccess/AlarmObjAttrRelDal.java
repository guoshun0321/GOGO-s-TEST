/*
 * 日期：2013年2月25日
 * 描述：关联报警数据库访问类
 * 作者：郭世平
 */
package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AlarmObjAttrRelEntity;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

public class AlarmObjAttrRelDal extends DefaultDal<AlarmObjAttrRelEntity>
{
    private static final Logger logger = Logger.getLogger(AlarmObjAttrRelDal.class);

    public AlarmObjAttrRelDal()
    {
        super(AlarmObjAttrRelEntity.class);
    }

    /**
     * 查找所有有效的报警关联关系
     * @return
     */
    @Transactional
    public List<AlarmObjAttrRelEntity> getValidRels(int collId)
    {
        List<AlarmObjAttrRelEntity> retval = null;
        try
        {
            CollectorDal cdal = new CollectorDal();
            String objStr = cdal.getContainObjs(collId);
            if (objStr != null)
            {
                String sql =
                    "SELECT a.* FROM BMP_ALARMOBJATTRREL a INNER JOIN BMP_OBJATTRIB b ON a.OBJATTR_ID = b.OBJATTR_ID INNER JOIN BMP_OBJATTRIB c ON a.OBJATTR_PID = c.OBJATTR_ID AND a.OBJ_ID IN (%s) AND a.OBJ_PID IN (%S)";
                sql = sql.format(sql, objStr, objStr);
                retval = this.getLst(sql);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 查询某个对象属性的报警关联来源
     * @param objAttrId
     * @return
     * @throws Exception
     */
    public String queryFromByObjArrId(String objAttrId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        String sql =
            "SELECT DISTINCT B.*,C.OBJ_ID,C.OBJ_NAME,E.ALARM_NAME FROM BMP_OBJATTRIB B LEFT JOIN BMP_OBJECT C ON C.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_ATTRIBALARM D ON D.OBJATTR_ID = B.OBJATTR_ID LEFT JOIN BMP_ALARM E ON E.ALARM_ID = D.ALARM_ID WHERE B.OBJATTR_ID IN (SELECT OBJATTR_PID FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_ID ="
                + objAttrId + ") AND E.ALARM_ID IS NOT NULL";
        ds = exec.fill(sql);
        return ds.asXML();
    }

    /**
     * 查询某个对象属性的报警关联衍生
     * @param objAttrId
     * @return
     * @throws Exception
     */
    public String queryToByObjArrId(String objAttrId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        Document ds = null;
        String sql =
            "SELECT DISTINCT B.*,C.OBJ_ID,C.OBJ_NAME,E.ALARM_NAME FROM BMP_OBJATTRIB B LEFT JOIN BMP_OBJECT C ON C.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_ATTRIBALARM D ON D.OBJATTR_ID = B.OBJATTR_ID LEFT JOIN BMP_ALARM E ON E.ALARM_ID = D.ALARM_ID WHERE B.OBJATTR_ID IN (SELECT OBJATTR_ID FROM BMP_ALARMOBJATTRREL WHERE OBJATTR_PID ="
                + objAttrId + ") AND E.ALARM_ID IS NOT NULL";
        ds = exec.fill(sql);
        return ds.asXML();
    }

    /**
     * 查询图形状态下的来源数据
     * @param objAttrId对象属性ID
     * @return
     * @throws Exception
     */
    public String queryAllFromByObjAttId(String objAttrId) throws Exception
    {
        // ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        // Document ds = null;
        Document resultDoc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource></DataSource>");
        List<AlarmObjAttrRelEntity> firstLevelLists =
            getLst(new SqlCondition("OBJATTR_ID", objAttrId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (firstLevelLists != null && firstLevelLists.size() > 0)
        {
            for (AlarmObjAttrRelEntity entity : firstLevelLists)
            {
                final Element ele = resultDoc.getRootElement().addElement("data");
                List<String> objAttrIdList = new ArrayList<String>();
                objAttrIdList.add(objAttrId);
                getFroms(entity, objAttrIdList);
                StringBuilder sqlSb = new StringBuilder();
                Collections.reverse(objAttrIdList);
                for (String tempObjAttrId : objAttrIdList)
                {
                    sqlSb
                        .append("SELECT B.OBJ_ID,B.OBJ_NAME,A.OBJATTR_ID,A.OBJATTR_NAME FROM BMP_OBJATTRIB A LEFT JOIN BMP_OBJECT B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_ATTRIBALARM C ON C.OBJATTR_ID = A.OBJATTR_ID WHERE A.OBJATTR_ID ="
                            + tempObjAttrId + " AND C.ALARM_ID IS NOT NULL");
                    sqlSb.append(" UNION ALL ");
                }
                String sql = sqlSb.toString().substring(0, sqlSb.toString().lastIndexOf("UNION ALL"));
                DefaultDal.read(sql, new IReadHandle()
                {

                    @Override
                    public void handle(ResultSet rs) throws Exception
                    {
                        while (rs.next())
                        {
                            Element nextLvel = ele.addElement("DataTable");
                            nextLvel.addElement("OBJ_ID").setText(String.valueOf(rs.getInt("OBJ_ID")));
                            nextLvel.addElement("OBJ_NAME").setText(rs.getString("OBJ_NAME"));
                            nextLvel.addElement("OBJATTR_ID").setText(String.valueOf(rs.getInt("OBJATTR_ID")));
                            nextLvel.addElement("OBJATTR_NAME").setText(rs.getString("OBJATTR_NAME"));
                        }
                    }

                });
            }
        }
        // List<String> objAttrIdList = new ArrayList<String>();
        // objAttrIdList.add(objAttrId);
        // AlarmObjAttrRelEntity objAttrRel =
        // get(new SqlCondition("OBJATTR_ID", objAttrId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        // if (objAttrRel == null)
        // {
        // return null;
        // }
        // getFroms(objAttrRel, objAttrIdList);
        // StringBuilder sqlSb = new StringBuilder();
        // Collections.reverse(objAttrIdList);
        // for (String tempObjAttrId : objAttrIdList)
        // {
        // sqlSb
        // .append("SELECT B.OBJ_ID,B.OBJ_NAME,A.OBJATTR_ID,A.OBJATTR_NAME FROM BMP_OBJATTRIB A LEFT JOIN BMP_OBJECT B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_ATTRIBALARM C ON C.OBJATTR_ID = A.OBJATTR_ID WHERE A.OBJATTR_ID ="
        // + tempObjAttrId + " AND C.ALARM_ID IS NOT NULL");
        // sqlSb.append(" UNION ALL ");
        // }
        // String sql = sqlSb.toString().substring(0, sqlSb.toString().lastIndexOf("UNION ALL"));
        // System.out.println(sql);
        return resultDoc.asXML();
    }

    private void getFroms(AlarmObjAttrRelEntity entity, List<String> list) throws Exception
    {
        list.add(String.valueOf(entity.getObjAttrPid()));
        AlarmObjAttrRelEntity objAttrRel =
            get(new SqlCondition("OBJATTR_ID", String.valueOf(entity.getObjAttrPid()), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (objAttrRel != null)
        {
            getFroms(objAttrRel, list);
        }
    }

    /**
     * 查询图形状态下的衍生数据
     * @param objAttrId
     * @return
     * @throws Exception
     */
    public String queryAllToByObjAttId(String objAttrId) throws Exception
    {
        // ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        // Document ds = null;
        // List<String> objAttrIdList = new ArrayList<String>();
        // objAttrIdList.add(objAttrId);
        // AlarmObjAttrRelEntity objAttrRel =
        // get(new SqlCondition("OBJATTR_PID", objAttrId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        // if (objAttrRel == null)
        // {
        // return null;
        // }
        // getTos(objAttrRel, objAttrIdList);
        // StringBuilder sqlSb = new StringBuilder();
        // for (String tempObjAttrId : objAttrIdList)
        // {
        // sqlSb
        // .append("SELECT B.OBJ_ID,B.OBJ_NAME,A.OBJATTR_ID,A.OBJATTR_NAME FROM BMP_OBJATTRIB A LEFT JOIN BMP_OBJECT B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_ATTRIBALARM C ON C.OBJATTR_ID = A.OBJATTR_ID WHERE A.OBJATTR_ID ="
        // + tempObjAttrId + " AND C.ALARM_ID IS NOT NULL");
        // sqlSb.append(" UNION ALL ");
        // }
        // String sql = sqlSb.toString().substring(0, sqlSb.toString().lastIndexOf("UNION ALL"));
        // System.out.println(sql);
        // ds = exec.fill(sql);
        Document resultDoc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource></DataSource>");
        List<AlarmObjAttrRelEntity> firstLevelLists =
            getLst(new SqlCondition("OBJATTR_PID", objAttrId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (firstLevelLists != null && firstLevelLists.size() > 0)
        {
            for (AlarmObjAttrRelEntity entity : firstLevelLists)
            {
                final Element ele = resultDoc.getRootElement().addElement("data");
                List<String> objAttrIdList = new ArrayList<String>();
                objAttrIdList.add(objAttrId);
                getTos(entity, objAttrIdList);
                StringBuilder sqlSb = new StringBuilder();
                for (String tempObjAttrId : objAttrIdList)
                {
                    sqlSb
                        .append("SELECT B.OBJ_ID,B.OBJ_NAME,A.OBJATTR_ID,A.OBJATTR_NAME FROM BMP_OBJATTRIB A LEFT JOIN BMP_OBJECT B ON A.OBJ_ID = B.OBJ_ID LEFT JOIN BMP_ATTRIBALARM C ON C.OBJATTR_ID = A.OBJATTR_ID WHERE A.OBJATTR_ID ="
                            + tempObjAttrId + " AND C.ALARM_ID IS NOT NULL");
                    sqlSb.append(" UNION ALL ");
                }
                String sql = sqlSb.toString().substring(0, sqlSb.toString().lastIndexOf("UNION ALL"));
                DefaultDal.read(sql, new IReadHandle()
                {

                    @Override
                    public void handle(ResultSet rs) throws Exception
                    {
                        while (rs.next())
                        {
                            Element nextLvel = ele.addElement("DataTable");
                            nextLvel.addElement("OBJ_ID").setText(String.valueOf(rs.getInt("OBJ_ID")));
                            nextLvel.addElement("OBJ_NAME").setText(rs.getString("OBJ_NAME"));
                            nextLvel.addElement("OBJATTR_ID").setText(String.valueOf(rs.getInt("OBJATTR_ID")));
                            nextLvel.addElement("OBJATTR_NAME").setText(rs.getString("OBJATTR_NAME"));
                        }
                    }

                });
            }
        }

        return resultDoc.asXML();
    }

    private void getTos(AlarmObjAttrRelEntity entity, List<String> list) throws Exception
    {
        list.add(String.valueOf(entity.getObjAttrId()));
        AlarmObjAttrRelEntity objAttrRel =
            get(new SqlCondition("OBJATTR_PID", String.valueOf(entity.getObjAttrId()), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (objAttrRel != null)
        {
            getTos(objAttrRel, list);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            new AlarmObjAttrRelDal().getValidRels(3);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
