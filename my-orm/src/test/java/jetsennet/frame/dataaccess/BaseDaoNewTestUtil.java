package jetsennet.frame.dataaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.ddl.Ddl;
import jetsennet.orm.ddl.IDdl;
import jetsennet.orm.executor.keygen.EfficientPkEntity;
import jetsennet.orm.executor.keygen.IdentifierEntity;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.tableinfo.TableInfoParseClz;
import jetsennet.orm.tableinfo.TableInfoParseClzUorm;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import junit.framework.TestCase;

import org.dom4j.Document;
import org.uorm.dao.common.PaginationSupport;
import org.uorm.dao.common.SqlParameter;

public class BaseDaoNewTestUtil extends TestCase
{

    private Configuration config;

    private BaseDaoNew dao;

    private IDdl ddl;

    private SqlSessionFactory factory;

    public BaseDaoNewTestUtil(Configuration config, SqlSessionFactory factory)
    {
        this.factory = factory;
        this.config = config;
        this.dao = new BaseDaoNew(this.config);
        this.ddl = Ddl.getDdl(this.config);
    }

    public void rebuild() throws Exception
    {
        ddl.rebuild(TableInfoParseClzUorm.parse(FrameTestEntity.class));
        ddl.rebuild(TableInfoParseClz.parse(IdentifierEntity.class));
        ddl.rebuild(TableInfoParseClz.parse(EfficientPkEntity.class));
        dao.saveBusinessObjsCol(FrameTestEntity.newFrames(100));
    }

    public void fillTest() throws Exception
    {
        String sql = "SELECT * FROM ORM_FRAME WHERE ID2 >= ? AND FIELD1 <= ? ORDER BY ID1 ASC";
        SqlParameter param1 = new SqlParameter("ID2", 100);
        SqlParameter param2 = new SqlParameter("FIELD1", 200);
        Document doc = null;

        doc = dao.fill(sql, param1, param2);
        assertEquals(68, doc.getRootElement().elements().size());
        System.out.println(doc.asXML());

        doc = dao.fill(sql, "testRoot", "testItem", param1, param2);
        assertEquals(68, doc.getRootElement().elements().size());
        System.out.println(doc.asXML());

        doc = dao.fill(sql, 10, 7, param1, param2);
        assertEquals(7, doc.getRootElement().elements().size());
        System.out.println(doc.asXML());

        doc = dao.fill(sql, "root", "rootItem", 10, 15, param1, param2);
        assertEquals(15, doc.getRootElement().elements().size());
        System.out.println(doc.asXML());

        sql = "SELECT * FROM ORM_FRAME WHERE ID1 > ?";
        param1 = new SqlParameter("ID1", 90);
        doc = dao.fill(sql, "root", "rootItem", -1, -1, param1);
        assertEquals(10, doc.getRootElement().elements().size());
        System.out.println(doc.asXML());

        param1 = new SqlParameter("ID1", -1);
        doc = dao.fillByPagedQuery(sql, "PageRoot", "PageItem", 2, 8, param1);
        assertEquals(8, doc.getRootElement().elements().size());
        System.out.println(doc.asXML());
    }

    public void fillJsonTest() throws Exception
    {
        String sql = "SELECT * FROM ORM_FRAME WHERE ID1 >= ? AND ID1 <= ? ORDER BY ID1 ASC";
        SqlParameter param1 = new SqlParameter("ID2", 15);
        SqlParameter param2 = new SqlParameter("FIELD1", 20);
        String str = dao.fillJson(sql, param1, param2);
        System.out.println(str);

        str = dao.fillJson(sql, 2, 10, param1, param2);
        System.out.println(str);

        str = dao.fillJsonByPagedQuery(sql, 0, 10, param1, param2);
        System.out.println(str);
    }

    public void queryBusinessObjByPkTest() throws Exception
    {
        FrameTestEntity frame = dao.queryBusinessObjByPk(FrameTestEntity.class, 100, 10, 1000);
        assertNotNull(frame);
        assertEquals(10, frame.getId1());
    }

    public void queryTest() throws Exception
    {
        String sql = "SELECT * FROM ORM_FRAME WHERE ID1 >= ? AND ID1 <= ? ORDER BY ID1 ASC";
        SqlParameter param1 = new SqlParameter("ID1", 15);
        SqlParameter param2 = new SqlParameter("ID1", 200);
        List<Map<String, Object>> lst = dao.queryForListMap(sql, param1, param2);
        assertEquals(86, lst.size());

        List<Object[]> lst1 = dao.queryForListArray(sql, param1, param2);
        assertEquals(86, lst1.size());

        FrameTestEntity frame = dao.querySingleObject(FrameTestEntity.class, sql, param1, param2);
        assertNotNull(frame);
        assertEquals(15, frame.getId1());

        Map<String, Object> map = dao.queryForMap(sql, param1, param2);
        assertNotNull(map);
        assertEquals(15, Integer.valueOf(map.get("ID1").toString()).intValue());

        Object[] objs = dao.queryForArray(sql, param1, param2);
        assertEquals(15, Integer.valueOf(objs[0].toString()).intValue());
        assertNotNull(objs);

        List<FrameTestEntity> frames = dao.queryBusinessObjs(FrameTestEntity.class, sql, 2, 10, param1, param2);
        assertNotNull(frames);
        assertEquals(10, frames.size());

        frames = dao.queryAllBusinessObjs(FrameTestEntity.class);
        assertNotNull(frames);
        assertEquals(100, frames.size());
    }

    public void queryByPagedQueryTest() throws Exception
    {
        String sql = "SELECT * FROM ORM_FRAME WHERE ID1 >= ? AND ID1 <= ? ORDER BY ID1 ASC";
        SqlParameter param1 = new SqlParameter("ID1", 15);
        SqlParameter param2 = new SqlParameter("ID1", 200);

        PaginationSupport<FrameTestEntity> frames = dao.queryByPagedQuery(FrameTestEntity.class, sql, 2, 5, param1, param2);
        assertNotNull(frames);
        assertEquals(5, frames.getItems().size());
    }

    public void saveTest() throws Exception
    {
        Map<String, Object>[] mapLst = FrameTestEntity.newFrameMaps(factory);
        dao.saveModelData(FrameTestEntity.class, mapLst);
        assertEquals(200, tableNum());

        Map<String, Object> map = FrameTestEntity.newFrameMap(factory);
        dao.saveModelData(FrameTestEntity.class, map);
        assertEquals(201, tableNum());
    }

    public void deleteTest() throws Exception
    {
        dao.deleteBusiness(FrameTestEntity.class, 2010, 201, 20100);
        assertEquals(200, tableNum());

        dao.deleteBusiness(FrameTestEntity.newFrame(200), FrameTestEntity.newFrame(199), FrameTestEntity.newFrame(198));
        assertEquals(197, tableNum());

        List<FrameTestEntity> frames = new ArrayList<FrameTestEntity>();
        frames.add(FrameTestEntity.newFrame(197));
        frames.add(FrameTestEntity.newFrame(196));
        frames.add(FrameTestEntity.newFrame(195));
        dao.deleteBusinessCol(frames);
        assertEquals(194, tableNum());
    }

    public void executeTest() throws Exception
    {
        String sql = "SELECT * FROM ORM_FRAME WHERE ID1 > ?";
        SqlParameter param1 = new SqlParameter("ID1", 90);
        boolean retval = dao.execute(sql, param1);
        assertEquals(true, retval);

        sql = "SELECT * FROM ORM_FRAME WHERE ID1 > 90";
        retval = dao.execute(sql);
        assertEquals(true, retval);

        sql = "UPDATE ORM_FRAME SET FIELD1=123 WHERE ID1 = ?";
        retval = dao.execute(sql, param1);
        assertEquals(false, retval);

        sql = "UPDATE ORM_FRAME SET FIELD1=123 WHERE ID1 = 90";
        retval = dao.execute(sql);
        assertEquals(false, retval);
    }

    public void getTest() throws Exception
    {
        String order = "ORDER BY ID1 DESC";
        SqlCondition cond = new SqlCondition("ID1", "100", SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        FrameTestEntity frame = dao.get(FrameTestEntity.class, order, cond);
        assertNotNull(frame);
        assertEquals(100, frame.getId1());

        Map<String, Object> map = dao.getMap(FrameTestEntity.class, "ID1, FIELD1", order, cond);
        assertNotNull(map);
        assertEquals(100, Integer.valueOf(map.get("ID1").toString()).intValue());

        Map<String, String> mapS = dao.getStrMap(FrameTestEntity.class, "ID1, FIELD1", order, cond);
        assertNotNull(mapS);
        assertEquals("100", mapS.get("ID1"));

        String sql = "SELECT * FROM ORM_FRAME WHERE ID1 = ?";
        SqlParameter param1 = new SqlParameter("ID1", 100);
        mapS = dao.getStrMap(sql, param1);
        assertNotNull(mapS);
        assertEquals("100", mapS.get("ID1"));

        Object id = dao.getFirst(Integer.class, FrameTestEntity.class, "ID1", order, cond);
        assertEquals(100, id);

        cond = new SqlCondition("ID1", "100", SqlLogicType.And, SqlRelationType.Than, SqlParamType.Numeric);
        List<FrameTestEntity> frames = dao.getLst(FrameTestEntity.class, order, cond);
        assertNotNull(frames);
        assertEquals(94, frames.size());

        List<Map<String, Object>> map1 = dao.getMapLst(FrameTestEntity.class, "*", order, cond);
        assertNotNull(map1);
        assertEquals(94, map1.size());

        List<Map<String, String>> map2 = dao.getStrMapLst(FrameTestEntity.class, "*", order, cond);
        assertNotNull(map2);
        assertEquals(94, map2.size());

        sql = "SELECT * FROM ORM_FRAME WHERE ID1 > ?";
        param1 = new SqlParameter("ID1", 100);
        map2 = dao.getStrMapLst(sql, param1);
        assertNotNull(map2);
        assertEquals(94, map2.size());

        List<Integer> ids = dao.getFirstLst(Integer.class, FrameTestEntity.class, "ID1", order, cond);
        assertNotNull(ids);
        assertEquals(94, ids.size());

        boolean isExist = dao.isExist(FrameTestEntity.class, cond);
        assertEquals(true, isExist);
    }

    public void updateTest() throws Exception
    {
        List<FrameTestEntity> frames = new ArrayList<FrameTestEntity>();
        frames.add(FrameTestEntity.newFrame(99));
        frames.add(FrameTestEntity.newFrame(102));
        frames.add(FrameTestEntity.newFrame(135));

        int temp = dao.updateBusinessObjsCol(true, frames);
        if (factory.getConfig().isOracle())
        {
            assertEquals(-6, temp);
        }
        else
        {
            assertEquals(3, temp);
        }

        temp = dao.updateBusinessObjs(true, FrameTestEntity.newFrame(100), FrameTestEntity.newFrame(101), FrameTestEntity.newFrame(102));
        if (factory.getConfig().isOracle())
        {
            assertEquals(-6, temp);
        }
        else
        {
            assertEquals(3, temp);
        }

        String sql = "UPDATE ORM_FRAME SET FIELD4 = ? WHERE ID1 >= ? AND ID1 <= ?";
        SqlParameter param1 = new SqlParameter("FIELD4", "FIELD_BATCH");
        SqlParameter param2 = new SqlParameter("ID1", 50);
        SqlParameter param3 = new SqlParameter("ID1", 55);
        temp = dao.update(sql, param1, param2, param3);
        assertEquals(6, temp);
    }

    public int tableNum() throws Exception
    {
        String sql = "select count(0) from orm_frame";
        return dao.queryForObject(Integer.class, sql);
    }

}
