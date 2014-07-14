package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;
import jetsennet.jbmp.util.TwoTuple;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class SnmpObjTypeDal extends DefaultDal<SnmpObjTypeEntity>
{

    private static final Logger logger = Logger.getLogger(SnmpObjTypeDal.class);

    /**
     * 构造方法给
     */
    public SnmpObjTypeDal()
    {
        super(SnmpObjTypeEntity.class);
    }

    /**
     * 查找匹配的类型
     * @param sysOid 参数
     * @param sysName 参数
     * @param sysDesc 参数
     * @return 结果
     */
    @Transactional
    public AttribClassEntity getAttrClassByOid(String sysOid, String sysName, String sysDesc)
    {
        SqlCondition cond1 = new SqlCondition("SNMP_SYSOID", sysOid, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
        SqlCondition cond2 = new SqlCondition("SNMP_SYSNAME", sysName, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
        try
        {
            SnmpObjTypeEntity sot = this.get(new SqlCondition[] { cond1, cond2 });
            if (sot == null)
            {
                sot = this.get(cond1);
            }
            if (sot == null)
            {
                List<SnmpObjTypeEntity> sots = this.getAll();
                for (SnmpObjTypeEntity s : sots)
                {
                    if (s.getField1() != null && sysDesc.contains(s.getField1()))
                    {
                        sot = s;
                        break;
                    }
                }
            }
            if (sot != null)
            {
                AttribClassDal acdao = new AttribClassDal();
                return acdao.get(sot.getClassId());
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return null;
    }

    /**
     * 获取属性分类对应的匹配规则
     * @param acs 参数
     * @return 结果
     * @throws Exception 名称
     */
    @Transactional
    public Map<AttribClassEntity, List<SnmpObjTypeEntity>> getArray(List<AttribClassEntity> acs) throws Exception
    {
        Map<AttribClassEntity, List<SnmpObjTypeEntity>> retval = new LinkedHashMap<AttribClassEntity, List<SnmpObjTypeEntity>>();
        if (acs == null || acs.isEmpty())
        {
            return retval;
        }
        for (AttribClassEntity ac : acs)
        {
            SqlCondition cond =
                new SqlCondition("CLASS_ID", Integer.toString(ac.getClassId()), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
            List<SnmpObjTypeEntity> types = this.getLst(cond);
            retval.put(ac, types);
        }
        return retval;
    }

    /**
     * 根据CLASS_ID获取匹配规则
     * @param classId 参数
     * @return 结果
     */
    @Transactional
    public TwoTuple<AttribClassEntity, List<SnmpObjTypeEntity>> getByClassId(int classId)
    {
        try
        {
            AttribClassDal acdal = new AttribClassDal();
            AttribClassEntity ac = acdal.get(classId);
            if (ac == null)
            {
                return null;
            }
            SqlCondition cond =
                new SqlCondition("CLASS_ID", Integer.toString(ac.getClassId()), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
            List<SnmpObjTypeEntity> types = this.getLst(cond);
            return new TwoTuple<AttribClassEntity, List<SnmpObjTypeEntity>>(ac, types);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return null;
    }

    /**
     * 根据classId获取所属分类
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<SnmpObjTypeEntity> getEntityByClassId(int classId) throws Exception
    {
        SqlCondition cond = new SqlCondition("CLASS_ID", Integer.toString(classId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return (ArrayList<SnmpObjTypeEntity>) getLst(cond);
    }
}
