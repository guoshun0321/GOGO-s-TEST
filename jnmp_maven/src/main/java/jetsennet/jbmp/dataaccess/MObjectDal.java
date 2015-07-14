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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.entity.ObjGroupEntity;
import jetsennet.jbmp.ins.SubObjsInsRst;
import jetsennet.jbmp.ins.helper.AttrsInsResult;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

import org.apache.log4j.Logger;

/**
 * @author ？
 */
public class MObjectDal extends DefaultDal<MObjectEntity>
{

    private ObjAttribDal oadao;
    private Obj2GroupDal o2gdal;
    private static final Logger logger = Logger.getLogger(MObjectDal.class);

    /**
     * 构造函数
     */
    public MObjectDal()
    {
        super(MObjectEntity.class);
        oadao = new ObjAttribDal();
        o2gdal = new Obj2GroupDal();
    }

    /**
     * 插入对象，同时插入对象的属性和所属设备
     * @param mo 对象
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    @Transactional
    public int insert(MObjectEntity mo) throws Exception
    {
        if (mo.getParentId() <= 0)
        {
            mo.setParentId(0);
        }
        // insert(mo);
        int index = super.insert(mo);

        // 插入对象属性
        ArrayList<ObjAttribEntity> oas = mo.getAttrs();
        if (oas != null && !oas.isEmpty())
        {
            for (int i = 0; i < oas.size(); i++)
            {
                oadao.insert(oas.get(i));
            }
        }
        return index;
    }

    /**
     * 更新对象状态
     * @param mo 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int updateState(MObjectEntity mo) throws Exception
    {
        return this.updateState(mo.getObjId(), mo.getObjState());
    }

    /**
     * 更新对象状态
     * @param id 参数
     * @param state 状态
     * @return 结果
     * @throws Exception 异常
     */
    public int updateState(int id, int state) throws Exception
    {
        ISqlExecutor executor = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(executor.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.addField("OBJ_STATE", state);
        cmd.setFilter(new SqlCondition("OBJ_ID", Integer.toString(id), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        return this.update(cmd.toString());
    }

    /**
     * @return 结果
     */
    public ArrayList<MObjectEntity> getBackupObject()
    {
        String sql = "SELECT * FROM BMP_OBJECT WHERE PARENT_ID = 0 AND CLASS_TYPE LIKE 'SNMP_%'";
        try
        {
            return (ArrayList<MObjectEntity>) getLst(sql);
        }
        catch (Exception e)
        {
            logger.error(e);
            return null;
        }
    }

    /**
     * @param objId 对象
     * @throws Exception 异常
     */
    @Transactional
    public void deleteById(int objId) throws Exception
    {
        List<MObjectEntity> subs = this.getByParentId(objId);
        for (MObjectEntity sub : subs)
        {
            this.deleteRel(sub.getObjId());
            this.deleteSelf(sub.getObjId());
        }
        this.deleteRel(objId);
        this.deleteSelf(objId);
    }

    /**
     * @param parentId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<MObjectEntity> getByParentId(int parentId) throws Exception
    {
        String sql = "SELECT * FROM BMP_OBJECT a WHERE a.PARENT_ID =  %s";
        sql = String.format(sql, parentId);
        return getLst(sql);
    }

    /**
     * @param idStr 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<MObjectEntity> getByParentId(String idStr) throws Exception
    {
        SqlCondition cond = new SqlCondition("PARENT_ID", idStr, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric);
        return getLst(cond);
    }

    private void deleteRel(int objId) throws Exception
    {
        ArrayList<ObjAttribEntity> oas =
            (ArrayList<ObjAttribEntity>) oadao.getLst(new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal,
                SqlParamType.Numeric));
        for (ObjAttribEntity oa : oas)
        {
            oadao.delete(oa.getObjAttrId());
        }
        
        o2gdal.deleteByObjId(Integer.toString(objId));
    }

    private void deleteSelf(int objId) throws Exception
    {
        delete(objId);
    }

    /**
     * @param objId 参数
     * @return 结果
     */
    @Transactional
    public String getGroup(int objId)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            ObjGroupDal ogdao = new ObjGroupDal();
            ArrayList<ObjGroupEntity> entities =
                (ArrayList<ObjGroupEntity>) ogdao.getLst("SELECT * FROM BMP_OBJGROUP WHERE GROUP_TYPE in (0,1,3,4) ORDER BY GROUP_TYPE");

            String sql = "SELECT GROUP_ID FROM BMP_OBJ2GROUP WHERE USE_TYPE = 0 AND OBJ_ID = " + objId;
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
            logger.debug("", ex);
        }
        return sb.toString();
    }

    /**
     * @param objId 参数
     * @return 结果
     */
    public String getSelGroup(int objId)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            String sql = "SELECT GROUP_ID FROM BMP_OBJ2GROUP WHERE USE_TYPE = 0 AND OBJ_ID = " + objId;
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

            for (int i = 0; i < groupIds.size(); i++)
            {
                sb.append(groupIds.get(i));
                sb.append(",");
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        catch (Exception ex)
        {
            logger.debug("", ex);
        }
        return sb.toString();
    }

    /**
     * 获取IP为给定IP且处于维护中的对象
     * @param ip 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<MObjectEntity> getForTrap(String ip) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] {
                new SqlCondition("IP_ADDR", ip, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String),
                new SqlCondition("OBJ_STATE", Integer.toString(MObjectEntity.OBJ_STATE_MAINTAIN), SqlLogicType.And, SqlRelationType.NotEqual,
                    SqlParamType.Numeric) };
        return this.getLst(conds);
    }

    /**
     * 根据对象类型，对象IP，端口号获取对象
     * @param ip 参数
     * @param classId 参数
     * @return 结果
     */
    @Transactional
    public List<MObjectEntity> getByIdent(int classId, String ip)
    {
        List<MObjectEntity> retval = null;
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("CLASS_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("IP_ADDR", ip, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
            retval = this.getLst(conds);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 删除对象并同时删除所有子对象
     * @param objId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void deleteObjAndSub(int objId) throws Exception
    {
        ArrayList<Integer> idList = new ArrayList<Integer>();
        idList.add(objId);
        List<MObjectEntity> temps = this.getByParentId(objId);
        StringBuilder sb = null;
        while (temps != null && temps.size() > 0)
        {
            sb = new StringBuilder();
            for (MObjectEntity temp : temps)
            {
                sb.append(temp.getObjId());
                sb.append(",");
                idList.add(temp.getObjId());
            }
            if (sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            temps = this.getByParentId(sb.toString());
        }
        for (int i = idList.size(); i > 0; i--)
        {
            this.deleteById(idList.get(i - 1));
        }
    }

    /**
     * 插入子对象及其属性
     * @param subObj 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insertSubObj(SubObjsInsRst subObj) throws Exception
    {
        if (subObj == null)
        {
            return;
        }
        ObjAttribDal oadal = new ObjAttribDal();
        Map<MObjectEntity, AttrsInsResult> subs = subObj.getSubs();
        Set<MObjectEntity> mos = subs.keySet();
        for (MObjectEntity mo : mos)
        {
            if (mo != null)
            {
                int id = this.insert(mo);
                AttrsInsResult air = subs.get(mo);
                if (air != null)
                {
                    oadal.insert(id, air.getOutput());
                }
            }
        }
    }

    /**
     * 根据IP和类型更新对象状态
     * @param ip 参数
     * @param classId 参数
     * @param state 状态
     */
    public void updateStateByIpAndClassId(String ip, int classId, int state)
    {
        try
        {
            String sql = "UPDATE BMP_OBJECT SET OBJ_STATE = %s WHERE IP_ADDR = '%s' AND CLASS_ID = %s";
            sql = String.format(sql, state, ip, classId);
            this.update(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * CATV，获取系统中的码流信息
     * @return 结果
     */
    public Map<String, Integer> getTsPortRel()
    {
        Map<String, Integer> retval = new HashMap<String, Integer>();
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("CLASS_ID", Integer.toString(BMPConstants.CATV_CLASS_ID_TS), SqlLogicType.And,
                SqlRelationType.Equal, SqlParamType.Numeric), };
        try
        {
            List<MObjectEntity> mos = this.getLst(conds);
            if (mos != null)
            {
                for (MObjectEntity mo : mos)
                {
                    retval.put(mo.getIpAddr(), mo.getObjId());
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 获取全部接口
     * @param objId
     * @return
     */
    @Transactional
    public Map<Integer, MObjectEntity> getAllInterface(int objId) throws Exception
    {
        Map<Integer, MObjectEntity> retval = new HashMap<Integer, MObjectEntity>();
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("PARENT_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("CLASS_ID", Integer.toString(10063), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("OBJ_STATE", Integer.toString(0), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
            List<MObjectEntity> mos = this.getLst(conds);
            for (MObjectEntity mo : mos)
            {
                Integer ifIndex = ConvertUtil.stringToInt(mo.getField1());
                if (ifIndex != null && ifIndex > 0)
                {
                    retval.put(ifIndex, mo);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 获取某组里面的所有对象
     * @param groupId
     * @return
     */
    @Transactional
    public List<MObjectEntity> getByGroupId(int groupId)
    {
        List<MObjectEntity> retval = null;
        try
        {
            String sql =
                "SELECT a.* FROM BMP_OBJECT a LEFT JOIN BMP_OBJ2GROUP b ON a.OBJ_ID = b.OBJ_ID LEFT JOIN BMP_OBJGROUP c ON b.GROUP_ID = c.GROUP_ID WHERE c.GROUP_ID = %s";
            sql = String.format(sql, groupId);
            retval = this.getLst(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    public void updateLinkType(int objId, int linkType)
    {
        try
        {
            String sql = "UPDATE BMP_OBJECT SET NUM_VAL2=%s WHERE OBJ_ID = %s";
            sql = String.format(sql, linkType, objId);
            this.update(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 更新对象采集状态
     * @param objId
     * @param collState
     */
    @Transactional
    public void updateCollState(int objId, int collState)
    {
        try
        {
            String sql = "UPDATE BMP_OBJECT SET RECEIVE_ENABLE=%s WHERE OBJ_ID = %s";
            sql = String.format(sql, collState, objId);
            this.update(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 更新子对象采集状态
     * @param objId
     * @param collState
     */
    @Transactional
    public void updateSubCollState(int objId, int collState)
    {
        try
        {
            String sql = "UPDATE BMP_OBJECT SET RECEIVE_ENABLE=%s WHERE PARENT_ID = %s";
            sql = String.format(sql, collState, objId);
            this.update(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public static void main(String[] args) throws Exception
    {
        //        MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
        //        List<MObjectEntity> mos = modal.getAll();
        //        String template = "UPDATE BMP_OBJECT SET OBJ_NAME='%S',NUM_VAL1=%s WHERE IP_ADDR='%s'";
        //        StringBuilder sb = new StringBuilder();
        //        for (MObjectEntity mo : mos)
        //        {
        //            if (mo.getClassId() == 7 || mo.getClassId() == 25)
        //            {
        //                sb.append(String.format(template, mo.getObjName(), mo.getNumVal1(), mo.getIpAddr()));
        //                sb.append("\n");
        //            }
        //        }
        //        logger.debug("\n" + sb.toString());

        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();

        exec.transBegin();
        try
        {
            exec.transCommit();
        }
        catch (Exception ex)
        {
            exec.transRollback();
        }
        finally
        {
            SqlExecutorFacotry.unbindSqlExecutor();
        }

    }
}
