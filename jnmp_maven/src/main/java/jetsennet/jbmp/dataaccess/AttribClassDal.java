/************************************************************************
日 期：2012-04-05
作 者: 
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.Class2ClassEntity;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class AttribClassDal extends DefaultDal<AttribClassEntity>
{

    private static final Logger logger = Logger.getLogger(AttribClassDal.class);

    /**
     * 构造方法
     */
    public AttribClassDal()
    {
        super(AttribClassEntity.class);
    }

    /**
     * 获取所有属性分类
     * 
     * @return 参数
     * @throws Exception 异常
     */
    public ArrayList<AttribClassEntity> getAllType() throws Exception
    {
        String sql = "SELECT * FROM BMP_ATTRIBCLASS";
        return (ArrayList<AttribClassEntity>) getLst(sql);
    }

    /**
     * 获取不同级别的属性分类
     * 
     * @param levels 分类
     * @return 结果
     * @throws Exception 异常
     */
    public List<AttribClassEntity> getByLevel(int[] levels) throws Exception
    {
        if (levels == null || levels.length == 0)
        {
            return this.getAll();
        }
        ArrayList<SqlCondition> conds = new ArrayList<SqlCondition>();
        for (int level : levels)
        {
            SqlCondition cond =
                new SqlCondition("CLASS_LEVEL", Integer.toString(level), SqlLogicType.Or, SqlRelationType.Equal, SqlParamType.Numeric);
            conds.add(cond);
        }
        return getLst(conds.toArray(new SqlCondition[conds.size()]));
    }

    /**
     * 获取该分类下面的所有子分类
     * 
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<AttribClassEntity> getBasic(int classId) throws Exception
    {
        String sql =
            "SELECT b.* FROM BMP_CLASS2CLASS a LEFT JOIN BMP_ATTRIBCLASS b ON a.CLASS_ID = b.CLASS_ID WHERE PARENT_ID=%s AND b.CLASS_LEVEL>=100";
        sql = String.format(sql, classId);
        return this.getLst(sql);
    }

    /**
     * 添加对象，在parentId大于0时，添加对象之间的关系
     * 
     * @param ac 参数
     * @param parentId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(AttribClassEntity ac, int parentId) throws Exception
    {
        int classId = this.insert(ac);
        if (parentId > 0)
        {
            Class2ClassDal ccdao = new Class2ClassDal();
            ccdao.insert(new Class2ClassEntity(classId, parentId));
        }
    }

    /**
     * 添加对象，在parentId大于0时，添加对象之间的关系
     * 
     * @param ac 参数
     * @param parentId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void update(AttribClassEntity ac, int parentId) throws Exception
    {
        this.update(ac);
        Class2ClassDal ccdao = new Class2ClassDal();
        if (parentId > 0)
        {
            ccdao.deleteByClassId(ac.getClassId());
            ccdao.insert(new Class2ClassEntity(ac.getClassId(), parentId));
        }
    }

    /**
     * 根据属性集id获取其子属性集列表
     * 
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<AttribClassEntity> getAttribClassByParent(String parentId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.addField("a.*", "");
        cmd.setTableName("BMP_ATTRIBCLASS a LEFT JOIN BMP_CLASS2CLASS b ON a.CLASS_ID = b.CLASS_ID");
        cmd.setFilter(new SqlCondition[] { new SqlCondition("b.PARENT_ID", parentId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("a.CLASS_LEVEL", Integer.toString(2), SqlLogicType.And, SqlRelationType.Than, SqlParamType.Numeric) });
        return this.getLst(cmd.toString());
    }

    /**
     * 获取子对象和基础对象
     * 
     * @param classId 参数
     * @return 结果
     */
    @Transactional
    public List<AttribClassEntity> getSub(int classId)
    {
        List<AttribClassEntity> retval = null;
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("a.*", "");
            cmd.setTableName("BMP_ATTRIBCLASS a LEFT JOIN BMP_CLASS2CLASS b ON a.CLASS_ID = b.CLASS_ID");
            cmd.setFilter(new SqlCondition[] {
                new SqlCondition("b.PARENT_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("a.CLASS_LEVEL", Integer.toString(2), SqlLogicType.And, SqlRelationType.ThanEqual, SqlParamType.Numeric) });
            // String sql =
            // "SELECT a.* FROM BMP_ATTRIBCLASS a LEFT JOIN BMP_CLASS2CLASS b ON a.CLASS_ID = b.CLASS_ID WHERE b.PARENT_ID = "
            // + classId;
            retval = this.getLst(cmd.toString());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 根据对象分类和级别获取属性
     * 
     * @param classId 参数
     * @param levels 参数
     * @return 结果
     */
    @Transactional
    public ArrayList<AttributeEntity> getAttrByClassId(int classId, int[] levels)
    {
        ArrayList<AttributeEntity> retval = null;
        String sql = "SELECT CLASS_ID FROM BMP_CLASS2CLASS WHERE PARENT_ID = " + classId;
        SqlCondition cond1 = new SqlCondition("b.CLASS_ID", sql, SqlLogicType.And, SqlRelationType.In, SqlParamType.UnKnow, true);
        StringBuilder sb = null;
        SqlCondition cond2 = null;
        if (levels != null && levels.length > 0)
        {
            sb = new StringBuilder();
            for (int level : levels)
            {
                sb.append(level);
                sb.append(",");
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            cond2 = new SqlCondition("a.ATTRIB_TYPE", sb.toString(), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
        }
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("a.*", "");
            cmd.setTableName("BMP_ATTRIBUTE a LEFT JOIN BMP_ATTRIB2CLASS b ON a.ATTRIB_ID = b.ATTRIB_ID");
            if (cond2 != null)
            {
                cmd.setFilter(cond1, cond2);
            }
            else
            {
                cmd.setFilter(cond1);
            }
            AttributeDal adao = new AttributeDal();
            retval = (ArrayList<AttributeEntity>) adao.getLst(cmd.toString());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 根据对象分类和级别获取属性
     * 
     * @param classId 参数
     * @param levels 参数
     * @return 结果
     */
    @Transactional
    public ArrayList<AttributeEntity> getAutoInsAttrib(int classId, int[] levels)
    {
        ArrayList<AttributeEntity> retval = null;
        String sql =
            "SELECT a.CLASS_ID FROM BMP_CLASS2CLASS a INNER JOIN BMP_ATTRIBCLASS b ON a.CLASS_ID = b.CLASS_ID WHERE PARENT_ID = " + classId
                + " AND USE_TYPE = 0 AND (FIELD_1 <> '1' OR FIELD_1 IS NULL)";
        SqlCondition cond1 = new SqlCondition("b.CLASS_ID", sql, SqlLogicType.And, SqlRelationType.In, SqlParamType.UnKnow, true);
        StringBuilder sb = null;
        SqlCondition cond2 = null;
        if (levels != null && levels.length > 0)
        {
            sb = new StringBuilder();
            for (int level : levels)
            {
                sb.append(level);
                sb.append(",");
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            cond2 = new SqlCondition("a.ATTRIB_TYPE", sb.toString(), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
        }
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("a.*", "");
            cmd.setTableName("BMP_ATTRIBUTE a LEFT JOIN BMP_ATTRIB2CLASS b ON a.ATTRIB_ID = b.ATTRIB_ID");
            if (cond2 != null)
            {
                cmd.setFilter(cond1, cond2);
            }
            else
            {
                cmd.setFilter(cond1);
            }
            AttributeDal adao = new AttributeDal();
            retval = (ArrayList<AttributeEntity>) adao.getLst(cmd.toString());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 删除对象，且在BMP_CLASS2CLASS表中删除和它相关的记录
     * 
     * @param classId 对视id
     * @throws Exception 异常
     */
    @Transactional
    public void deleteByClassId(int classId) throws Exception
    {
        this.delete(classId);
        Class2ClassDal ccdal = new Class2ClassDal();
        ccdal.deleteByParentId(classId);
        ccdal.deleteByClassId(classId);
        Attrib2ClassDal acdal = new Attrib2ClassDal();
        acdal.deleteByClassId(classId);
    }

    /**
     * 获取父分类ID
     * 
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public int getParentId(int classId) throws Exception
    {
        Class2ClassDal ccdal = new Class2ClassDal();
        ArrayList<Class2ClassEntity> ids = ccdal.getByClassId(classId);
        if (ids != null && ids.size() > 0)
        {
            return ids.get(0).getParentId();
        }
        return -1;
    }

    /**
     * 获取全部子分类
     * 
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<AttribClassEntity> getAllSubObjs() throws Exception
    {
        SqlCondition cond = new SqlCondition("CLASS_LEVEL", "2", SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return this.getLst(cond);
    }

    /**
     * 获取全部子分类
     * 
     * @param classType 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public AttribClassEntity getByClassType(String classType) throws Exception
    {
        SqlCondition cond = new SqlCondition("CLASS_TYPE", classType, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return this.get(cond);
    }

    /**
     * 获取全部基础分类
     * 
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    public List<AttribClassEntity> getAllBasicObjs() throws Exception
    {
        SqlCondition cond = new SqlCondition("CLASS_LEVEL", "100", SqlLogicType.And, SqlRelationType.ThanEqual, SqlParamType.Numeric);
        return this.getLst(cond);
    }

    /**
     * 查询某类型下面是否有继承的子类型
     * 
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public boolean hasChildClass(int parentId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.addField("a.*", "");
        cmd.setTableName("BMP_CLASS2CLASS a LEFT JOIN BMP_ATTRIBCLASS b ON a.CLASS_ID = b.CLASS_ID");
        cmd.setFilter(new SqlCondition[] {
            new SqlCondition("a.PARENT_ID", String.valueOf(parentId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("a.USE_TYPE", String.valueOf(Class2ClassEntity.USE_TYPE_PARENT), SqlLogicType.And, SqlRelationType.Equal,
                SqlParamType.Numeric),
            new SqlCondition("b.CLASS_LEVEL", Integer.toString(2), SqlLogicType.And, SqlRelationType.LessEqual, SqlParamType.Numeric) });
        int size = this.getLst(cmd.toString()).size();
        return size > 0;
    }

    /**
     * 根据类型ClassId获得设备厂商
     * 
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     * @throws NumberFormatException 异常
     */
    public AttribClassEntity getManufacturersByClassId(String classId) throws NumberFormatException, Exception
    {
        AttribClassEntity a = get(Integer.parseInt(classId));
        return a;
    }

    /**
     * 获取子对象分类，以及相关的标识信息
     * 
     * @param classId 参数
     * @return 结果
     */
    @Transactional
    public Map<AttribClassEntity, List<SnmpObjTypeEntity>> getSubClass(int classId)
    {
        Map<AttribClassEntity, List<SnmpObjTypeEntity>> retval = null;
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("a.*", "");
            cmd.setTableName("BMP_ATTRIBCLASS a LEFT JOIN BMP_CLASS2CLASS b ON a.CLASS_ID = b.CLASS_ID");
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("b.PARENT_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("a.CLASS_LEVEL", Integer.toString(AttribClassEntity.CLASS_LEVEL_SUB), SqlLogicType.And, SqlRelationType.Equal,
                        SqlParamType.Numeric) };
            cmd.setFilter(conds);
            List<AttribClassEntity> acs = this.getLst(cmd.toString());
            SnmpObjTypeDal sotdal = new SnmpObjTypeDal();
            retval = sotdal.getArray(acs);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 获取所有对象，以及其识别信息
     * 
     * @return 结果
     */
    @Transactional
    public Map<AttribClassEntity, List<SnmpObjTypeEntity>> getIdentifiableClass()
    {
        Map<AttribClassEntity, List<SnmpObjTypeEntity>> retval = null;
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("a.*", "");
            cmd.setTableName("BMP_ATTRIBCLASS a");
            SqlCondition[] conds =
                new SqlCondition[] { new SqlCondition("a.CLASS_LEVEL", Integer.toString(AttribClassEntity.CLASS_LEVEL_OBJ), SqlLogicType.And,
                    SqlRelationType.Equal, SqlParamType.Numeric) };
            cmd.setFilter(conds);
            List<AttribClassEntity> acs = this.getLst(cmd.toString());
            SnmpObjTypeDal sotdal = new SnmpObjTypeDal();
            retval = sotdal.getArray(acs);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 根据对象ID获取所属分类
     * 
     * @param objId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public AttribClassEntity getByObjId(int objId) throws Exception
    {
        String sql = "SELECT b.* FROM BMP_OBJECT a LEFT JOIN BMP_ATTRIBCLASS b ON a.CLASS_ID = b.CLASS_ID WHERE a.OBJ_ID = %s";
        sql = String.format(sql, objId);
        return this.get(sql);
    }

    /**
     * 根据属性ID获取所属分类
     * 
     * @param attribId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public AttribClassEntity getByAttribId(int attribId) throws Exception
    {
        String sql =
            "SELECT c.* FROM BMP_ATTRIBUTE a LEFT JOIN BMP_ATTRIB2CLASS b ON a.ATTRIB_ID = b.ATTRIB_ID"
                + "LEFT JOIN BMP_ATTRIBCLASS c ON b.CLASS_ID = c.CLASS_ID WHERE a.ATTRIB_ID = %s";
        sql = String.format(sql, attribId);
        return this.get(sql);
    }

    /**
     * 更新MIB库ID，同时更新子分类的MIB库ID
     * 
     * @param classId 参数
     * @param mibId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void updateMibId(String classId, String mibId) throws Exception
    {
        String sql =
            "UPDATE BMP_ATTRIBCLASS SET MIB_ID = %1$s WHERE CLASS_LEVEL >= 100 AND"
                + " (CLASS_ID IN (SELECT CLASS_ID FROM BMP_CLASS2CLASS WHERE PARENT_ID = %2$s) OR CLASS_ID = %2$s)";
        sql = String.format(sql, mibId, classId);
        this.update(sql);
    }

    /**
     * 根据parentId获取分类
     * 
     * @param parentId 参数
     * @param useType 参数
     * @return 结果
     */
    @Transactional
    public List<AttribClassEntity> getChildEntity(int parentId, int useType)
    {
        List<AttribClassEntity> retval = null;
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("a.*", "");
            cmd.setTableName("BMP_ATTRIBCLASS a LEFT JOIN BMP_CLASS2CLASS b ON a.CLASS_ID = b.CLASS_ID");
            cmd.setFilter(new SqlCondition[] {
                new SqlCondition("b.PARENT_ID", Integer.toString(parentId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("b.USE_TYPE", Integer.toString(useType), SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric),
                new SqlCondition("a.CLASS_LEVEL", "0,1,2", SqlLogicType.And, SqlRelationType.NotIn, SqlParamType.Numeric), });
            // String sql =
            // "SELECT a.* FROM BMP_ATTRIBCLASS a LEFT JOIN BMP_CLASS2CLASS b ON a.CLASS_ID = b.CLASS_ID WHERE b.PARENT_ID = "
            // + classId;
            retval = this.getLst(cmd.toString());
        }
        catch (Exception ex)
        {
            logger.error("获取属性集出现", ex);
        }
        return retval;
    }

    /**
     * 获取别名相同的结果集
     * 
     * @param classType 别名
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<AttribClassEntity> getListByClassType(String classType) throws Exception
    {
        String sql = "SELECT a.* FROM BMP_ATTRIBCLASS a  WHERE a.CLASS_TYPE = '%s'";
        sql = String.format(sql, classType);
        return this.getLst(sql);
    }

    /**
     * 获取名称相同的结果集
     * 
     * @param classType 别名
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<AttribClassEntity> getListByClassName(String className) throws Exception
    {
        String sql = "SELECT a.* FROM BMP_ATTRIBCLASS a  WHERE a.CLASS_NAME = '%s'";
        sql = String.format(sql, className);
        return this.getLst(sql);
    }

    /**
     * 获取设备类型下的子对象
     * 
     * @param classId 参数
     * @return 结果
     */
    @Transactional
    public List<AttribClassEntity> getSubByParentId(int classId)
    {
        List<AttribClassEntity> retval = null;
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
            cmd.addField("a.*", "");
            cmd.setTableName("BMP_ATTRIBCLASS a LEFT JOIN BMP_CLASS2CLASS b ON a.CLASS_ID = b.CLASS_ID");
            cmd.setFilter(new SqlCondition[] {
                new SqlCondition("b.PARENT_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("a.CLASS_LEVEL", Integer.toString(2), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) });
            retval = this.getLst(cmd.toString());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * @param args 主方法 参数
     */
    public static void main(String[] args)
    {
        try
        {
            ArrayList<AttributeEntity> list = new AttribClassDal().getAutoInsAttrib(2013, null);
            System.out.println(list);
        }
        catch (Exception e)
        {
        }
    }

}
