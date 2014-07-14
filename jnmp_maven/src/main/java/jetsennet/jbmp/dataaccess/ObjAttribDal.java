package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AttribAlarmEntity;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.UncheckedSQLException;
import jetsennet.jbmp.util.TwoTuple;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.StringUtil;

/**
 * 对象属性的数据库操作
 * @author 郭祥
 */
public class ObjAttribDal extends DefaultDal<ObjAttribEntity>
{

    private static final Logger logger = Logger.getLogger(ObjAttribDal.class);

    /**
     * 构造方法
     */
    public ObjAttribDal()
    {
        super(ObjAttribEntity.class);
    }

    /**
     * 获取对象的对象属性集合
     * @param objId 对象id
     * @return 结果
     */
    public ArrayList<ObjAttribEntity> getByID(int objId)
    {
        SqlCondition cond = new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        try
        {
            return (ArrayList<ObjAttribEntity>) getLst(cond);
        }
        catch (Exception ex)
        {
            logger.error(ex);
            return null;
        }
    }

    /**
     * 获取监控对象需要采集的对象属性列表
     * @param objId 对象id
     * @return 结果
     */
    public ArrayList<ObjAttribEntity> getCollObjAttribByID(int objId)
    {
        String sql =
            "SELECT a.* FROM BMP_OBJATTRIB a WHERE a.OBJ_ID=" + objId + " AND (a.ATTRIB_TYPE=" + AttribClassEntity.CLASS_LEVEL_PERF
                + " OR a.ATTRIB_TYPE=" + AttribClassEntity.CLASS_LEVEL_MONITOR + " OR a.ATTRIB_ID=" + AttributeEntity.VALID_ATTRIB_ID + ")";
        try
        {
            return (ArrayList<ObjAttribEntity>) getLst(sql);
        }
        catch (Exception ex)
        {
            logger.error("获取对象的采集指标异常", ex);
            return null;
        }
    }

    /**
     * 获取监控对象需要刷新的配置信息列表
     * @param objId 对象id
     * @param objAttrIds 对象属性id
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<ObjAttribEntity> getCfgObjAttrib(int objId, String objAttrIds) throws Exception
    {
        String sql =
            "SELECT a.* FROM BMP_OBJATTRIB a WHERE a.OBJ_ID=" + objId + " AND a.OBJATTR_ID IN (" + objAttrIds + ")" + " AND (a.ATTRIB_TYPE="
                + AttribClassEntity.CLASS_LEVEL_CONFIG + " OR a.ATTRIB_TYPE=" + AttribClassEntity.CLASS_LEVEL_TABLE + ")";
        return (ArrayList<ObjAttribEntity>) getLst(sql);
    }

    /**
     * 获取对象及其子对象的对象属性集合
     * @param obj_id 对象id
     * @return 结果
     * @throws DAOException
     */
    public ArrayList<ObjAttribEntity> getAllByObjID(int obj_id)
    {
        String sql =
            "SELECT a.* FROM BMP_OBJATTRIB a LEFT JOIN BMP_OBJECT b ON a.OBJ_ID=b.OBJ_ID WHERE a.OBJ_ID=" + obj_id + " OR b.PARENT_ID=" + obj_id;
        try
        {
            return (ArrayList<ObjAttribEntity>) getLst(sql);
        }
        catch (Exception ex)
        {
            logger.error(ex);
            return null;
        }
    }

    /**
     * 插入重新实例化的对象属性
     * @param objId 对象id
     * @param attribIds 对象属性
     * @param attrs 属性
     * @throws Exception 异常
     */
    @Transactional
    public void insertReins(int objId, String[] attribIds, ArrayList<ObjAttribEntity> attrs) throws Exception
    {
        for (String attribId : attribIds)
        {
            deleteByObjIdAndAttribId(objId, Integer.valueOf(attribId));
        }
        insert(attrs);
    }

    /**
     * 插入对象属性，如果对象属性里面有报警ID的值，插入报警ID
     * @param oa 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    @Transactional
    public int insert(ObjAttribEntity oa) throws Exception
    {
        int retval = super.insert(oa);
        if (oa.getAlarmId() > 0)
        {
            AttribAlarmDal aadal = new AttribAlarmDal();
            aadal.updateOrInsert(retval, oa.getAlarmId());
        }
        return retval;
    }

    /**
     * 批量插入对象属性
     * @param oas 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(List<ObjAttribEntity> oas) throws Exception
    {
        if (oas == null || oas.isEmpty())
        {
            return;
        }
        for (ObjAttribEntity oa : oas)
        {
            if (oa != null)
            {
                this.insert(oa);
            }
        }
    }

    /**
     * 插入对象属性，同时插入对应的报警规则
     * 
     * @param oas 对象属性
     */
    @Transactional
    public void insertWithAlarm(List<ObjAttribEntity> oas) throws Exception
    {
        AlarmDal adal = new AlarmDal();
        AttribAlarmDal aadal = new AttribAlarmDal();
        for (ObjAttribEntity oa : oas)
        {
            if (oa == null)
            {
                continue;
            }
            oa.setAlarmId(-1);
            int objAttribId = this.insert(oa);

            int alarmId = adal.copyAlarm(oa.getAttribId());
            aadal.deleteByObjAttribID(objAttribId);
            aadal.insert(new AttribAlarmEntity(objAttribId, alarmId));
        }
    }

    /**
     * 批量插入对象属性，objId给出对象属性所属对象
     * @param objId 对象
     * @param oas 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(int objId, ArrayList<ObjAttribEntity> oas) throws Exception
    {
        if (oas == null || oas.isEmpty())
        {
            return;
        }
        AlarmDal adal = new AlarmDal();
        for (ObjAttribEntity oa : oas)
        {
            oa.setObjId(objId);
            oa.setAlarmId(adal.copyAlarm(oa.getAttribId()));
            this.insert(oa);
        }
    }

    /**
     * @param objId 对象
     * @param attribId 属性
     * @throws Exception 异常
     */
    @Transactional
    public void deleteByObjIdAndAttribId(int objId, int attribId) throws Exception
    {
        ArrayList<ObjAttribEntity> oas = this.getObjAttribByObjIdAndAttribId(objId, attribId);
        if (oas == null || oas.size() <= 0)
        {
            return;
        }
        for (ObjAttribEntity oa : oas)
        {
            this.deleteById(oa.getObjAttrId());
        }
    }

    /**
     * @param objId 对象
     * @param attribId 属性
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<ObjAttribEntity> getObjAttribByObjIdAndAttribId(int objId, int attribId) throws Exception
    {
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                new SqlCondition("ATTRIB_ID", Integer.toString(attribId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) };
        return (ArrayList<ObjAttribEntity>) getLst(conds);
    }

    /**
     * @param objAttribId 对象属性
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public int deleteById(int objAttribId) throws Exception
    {
        AttribAlarmDal aadao = new AttribAlarmDal();
        aadao.deleteByObjAttribID(objAttribId);
        SqlCondition cond =
            new SqlCondition("OBJATTR_ID", Integer.toString(objAttribId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
    }

    /**
     * @param attrs 属性
     */
    @Transactional
    public void insertOrUpdate(ArrayList<ObjAttribEntity> attrs)
    {
        for (ObjAttribEntity attr : attrs)
        {
            try
            {
                insertOrUpdate(attr);
            }
            catch (Exception ex)
            {
                logger.error(ex);
            }
        }
    }

    /**
     * @param oa 参数
     */
    public void insertOrUpdate(ObjAttribEntity oa)
    {
        try
        {
            int index = this.update(oa);
            if (index == 0)
            {
                this.insert(oa);
            }
        }
        catch (Exception e)
        {
            throw new UncheckedSQLException(e.getMessage(), e);
        }
    }

    /**
     * @param ids 参数
     * @param oids 参数
     * @throws Exception 异常
     */
    @Transactional
    public void updateProcess(ArrayList<Integer> ids, String[] oids) throws Exception
    {
        for (int i = 0; i < ids.size(); i++)
        {
            update(ids.get(i), oids[i]);
        }
    }

    /**
     * @param objAttribId 对象属性
     * @param param 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int update(int objAttribId, String param) throws Exception
    {
        String sql = "UPDATE BMP_OBJATTRIB SET ATTRIB_PARAM='" + param + "' WHERE OBJATTR_ID=" + objAttribId;
        return update(sql);
    }

    /**
     * 更新采集进程对象的对象属性
     * @param objId 参数
     * @param tail 参数
     * @throws Exception 异常
     */
    @Transactional
    public void updateProcess(int objId, String tail) throws Exception
    {
        MObjectDal modao = new MObjectDal();
        MObjectEntity entity = modao.get(objId);
        if (entity == null || !entity.getClassType().equals(AttribClassEntity.ATTRIBCLASS_SNMP_HOST_PROCESS))
        {
            return;
        }
        entity.setObjParam(tail);
        modao.update(entity);
        ArrayList<ObjAttribEntity> oas = getObjAttribByObjId(objId);
        for (ObjAttribEntity oa : oas)
        {
            oa.setAttribParam(this.replaceOidTail(oa.getAttribParam(), tail));
            update(oa);
        }
    }

    /**
     * @param objId 对象id
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<ObjAttribEntity> getObjAttribByObjId(int objId) throws Exception
    {
        SqlCondition cond = new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return (ArrayList<ObjAttribEntity>) getLst(cond);
    }

    /**
     * @param objId 对象id
     * @return 结果
     * @throws Exception 异常
     */
    public List<ObjAttribEntity> getObjAttrAndValByObjId(int objId) throws Exception
    {
        String sql =
            "SELECT a.OBJATTR_ID,a.COLL_TIMESPAN,b.STR_VALUE as ATTRIB_VALUE FROM BMP_OBJATTRIB a "
                + "LEFT JOIN BMP_OBJATTRIBVALUE b ON a.OBJATTR_ID=b.OBJATTR_ID WHERE a.OBJ_ID=" + objId;
        return getLst(sql);
    }

    /**
     * @param objIds 对象id
     * @return 结果
     * @throws Exception 异常
     */
    public List<ObjAttribEntity> getObjAttrAndValByObjIds(String objIds) throws Exception
    {
        String sql =
            "SELECT a.OBJATTR_ID,a.COLL_TIMESPAN,b.STR_VALUE as ATTRIB_VALUE FROM BMP_OBJATTRIB a "
                + "LEFT JOIN BMP_OBJATTRIBVALUE b ON a.OBJATTR_ID=b.OBJATTR_ID WHERE a.OBJ_ID IN (" + objIds + ")";
        return getLst(sql);
    }

    private String replaceOidTail(String oid, String tail)
    {
        if (oid == null && oid.indexOf(".") > 0)
        {
            return null;
        }
        return oid.substring(0, oid.lastIndexOf(".")) + "." + tail + "))";
    }

    /**
     * @param obj_id 对象id 对象id
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByObjId(int obj_id) throws Exception
    {
        SqlCondition cond = new SqlCondition("OBJ_ID", Integer.toString(obj_id), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
    }

    /**
     * @param attrib_id id
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByAttrib_id(int attrib_id) throws Exception
    {
        SqlCondition cond = new SqlCondition("ATTRIB_ID", Integer.toString(attrib_id), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
    }

    /**
     * @param objAttribId 对象属性
     * @param type 类型
     * @return 结果
     * @throws Exception 异常
     */
    public int updateCollType(int objAttribId, int type) throws Exception
    {
        String sql = "UPDATE BMP_OBJATTRIB SET COLL_TYPE=" + type + " WHERE OBJATTR_ID=" + objAttribId;
        return update(sql);
    }

    /**
     * @param objId 对象
     * @param attrId 属性
     * @param isVisible 参数
     * @return 结果
     */
    public List<ObjAttribEntity> getByObjIdAndAttribId(int objId, int attrId, Integer[] isVisible)
    {
        List<ObjAttribEntity> retval = null;
        try
        {
            String isVisibles = StringUtils.join(isVisible, ",");
            if (StringUtil.isNullOrEmpty(isVisibles))
            {
                isVisibles = "0,1";
            }

            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("ATTRIB_ID", Integer.toString(attrId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("IS_VISIBLE", isVisibles, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric) };

            retval = this.getLst(conds);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 根据OBJ_ID和ATTRIB_VALUE找到相应的对象。
     * @param objId 对象ID
     * @param attribValue 参数
     * @return 结果
     */
    @Transactional
    public ObjAttribEntity getByObjIdAndAttribValue(int objId, String attribValue)
    {
        ObjAttribEntity retval = null;
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("ATTRIB_VALUE", attribValue, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
            retval = this.get(conds);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 根据OBJ_ID和ATTRIB_VALUE找到相应的对象属性。
     * @param objId 对象ID
     * @param attribValue 参数
     * @return 结果
     */
    @Transactional
    public TwoTuple<ObjAttribEntity, AlarmEntity> getObjAttribAndAlarm(int objId, String attribValue)
    {
        ObjAttribEntity oa = null;
        AlarmEntity alarm = null;
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("ATTRIB_VALUE", attribValue, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
            oa = this.get(conds);
            if (oa != null)
            {
                AlarmDal adal = new AlarmDal();
                alarm = adal.getByObjAttribId(oa.getObjAttrId());
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return new TwoTuple<ObjAttribEntity, AlarmEntity>(oa, alarm);
    }

    /**
     * 获取以 value+"," 开头的对象属性集合
     * @param objId 对象id
     * @param value 参数
     * @return 结果
     */
    public List<ObjAttribEntity> getLikeValue(int objId, String value)
    {
        List<ObjAttribEntity> retval = null;
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("ATTRIB_VALUE", value + ",", SqlLogicType.And, SqlRelationType.Like, SqlParamType.String) };
            retval = this.getLst(conds);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * @param objId 对象id
     * @param types 参数
     * @return 结果
     */
    @Transactional
    public List<ObjAttribEntity> getByObjIdAndAttribValues(int objId, ArrayList<String> types)
    {
        if (types == null || !types.isEmpty())
        {
            return null;
        }
        List<ObjAttribEntity> retval = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.size(); i++)
        {
            sb.append(i);
            sb.append(",");
        }
        if (sb.length() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("ATTRIB_VALUE", sb.toString(), SqlLogicType.And, SqlRelationType.In, SqlParamType.String) };
            retval = this.getLst(conds);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * @param objAttriIds (对象属性ID数组)
     * @param time (采集时间间隔)
     * @throws Exception 异常
     */
    public void updateCollTime(String objAttriIds, int time) throws Exception
    {
        String sql = "UPDATE BMP_OBJATTRIB SET COLL_TIMESPAN = " + time + " WHERE OBJATTR_ID IN(" + objAttriIds + ")";
        update(sql);
    }

    /**
     * 设置通断性监测时间
     * @param objId 对象id
     * @param time 时间
     * @throws Exception 异常
     */
    public void updateCollTime(int objId, String time) throws Exception
    {
        String sql = "UPDATE BMP_OBJATTRIB SET COLL_TIMESPAN = " + time + " WHERE OBJ_ID = " + objId + " AND ATTRIB_ID = 40001";
        update(sql);
    }

    /**
     * 根据OBJ_ID和ATTRIB_TYPE找到相应的对象属性。
     * @param objId 对象ID
     * @return 结果
     */
    @Transactional
    public List<ObjAttribEntity> getSyslogObjAttrib(int objId)
    {
        List<ObjAttribEntity> retval = null;
        try
        {
            SqlCondition[] conds =
                new SqlCondition[] {
                    new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                    new SqlCondition("ATTRIB_TYPE", Integer.toString(AttribClassEntity.CLASS_LEVEL_SYSLOG), SqlLogicType.And, SqlRelationType.Equal,
                        SqlParamType.Numeric) };
            retval = this.getLst(conds);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }
}
