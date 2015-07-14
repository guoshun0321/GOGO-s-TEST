package jetsennet.jbmp.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.Attrib2ClassDal;
import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.Class2ClassDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.Attrib2ClassEntity;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.Class2ClassEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;
import jetsennet.jbmp.util.SqlQueryUtil;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;

/**
 * @author ？
 */
public class AttribClass
{
    /**
     * 数据库访问
     */
    private AttribClassDal attrClaDal = new AttribClassDal();
    private Class2ClassDal cla2ClaDal = new Class2ClassDal();
    private AttributeDal attrDal = new AttributeDal();
    private Attrib2ClassDal attr2ClaDal = new Attrib2ClassDal();

    /**
     * 新增
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void addAttribClass(String objXml) throws Exception
    {
        // 创建属性集
        int classId = attrClaDal.insertXml(objXml);
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String subTypes = map.get("SUB_TYPES");
        String parentId = map.get("PARENT_ID");
        String className = map.get("CLASS_NAME");
        String classType = map.get("CLASS_TYPE");
        String classLevel = map.get("CLASS_LEVEL");
        int mibId = Integer.parseInt(map.get("MIB_ID"));

        // 若是根分类则创建默认属性集并返回
        if (parentId == null || parentId.length() == 0)
        {
            createDefaultClasses(classId, className, classType);
            return;
        }

        // 添加父子关系
        Class2ClassEntity c2cEntity = new Class2ClassEntity(classId, Integer.parseInt(parentId));
        cla2ClaDal.insert(c2cEntity);

        // 建立对象类型与子对象类型的关系
        if (subTypes != null && subTypes.length() > 0)
        {
            createRelation(classId, subTypes);
        }

        // 拷贝父类型的属性集
        if (classLevel != null && classLevel.length() > 0 && Integer.parseInt(classLevel) <= AttribClassEntity.CLASS_LEVEL_SUB)
        {
            copyClasses(classId, parentId, className, classType, mibId);
        }
    }

    /**
     * @param classId
     * @param subTypes
     * @throws Exception
     */
    private void createRelation(int classId, String subTypes) throws Exception
    {
        String[] types = subTypes.split(",");
        for (String typeId : types)
        {
            Class2ClassEntity c2cEntity = new Class2ClassEntity(Integer.parseInt(typeId), classId);
            c2cEntity.setUseType(Class2ClassEntity.USE_TYPE_CONTAIN);
            cla2ClaDal.insert(c2cEntity);
        }
    }

    private void createDefaultClasses(int parentId, String className, String classType) throws Exception
    {
        int classId = -1;

        AttribClassEntity customer = new AttribClassEntity();
        customer.setClassName(className + "自定义属性");
        customer.setClassType(classType + "_CUSTOM");
        customer.setClassLevel(AttribClassEntity.CLASS_LEVEL_CUSTOM);
        customer.setClassDesc(className + "的自定义属性");
        classId = attrClaDal.insert(customer);
        cla2ClaDal.insert(new Class2ClassEntity(classId, parentId));

        AttribClassEntity monitor = new AttribClassEntity();
        monitor.setClassName(className + "监测指标");
        monitor.setClassType(classType + "_MONITOR");
        monitor.setClassLevel(AttribClassEntity.CLASS_LEVEL_MONITOR);
        monitor.setClassDesc(className + "的监测指标");
        classId = attrClaDal.insert(monitor);
        cla2ClaDal.insert(new Class2ClassEntity(classId, parentId));

        AttribClassEntity perf = new AttribClassEntity();
        perf.setClassName(className + "性能指标");
        perf.setClassType(classType + "_PERF");
        perf.setClassLevel(AttribClassEntity.CLASS_LEVEL_PERF);
        perf.setClassDesc(className + "的性能指标");
        classId = attrClaDal.insert(perf);
        cla2ClaDal.insert(new Class2ClassEntity(classId, parentId));

        AttribClassEntity trap = new AttribClassEntity();
        trap.setClassName(className + "Trap信息");
        trap.setClassType(classType + "_TRAP");
        trap.setClassLevel(AttribClassEntity.CLASS_LEVEL_TRAP);
        trap.setClassDesc(className + "的Trap信息");
        classId = attrClaDal.insert(trap);
        cla2ClaDal.insert(new Class2ClassEntity(classId, parentId));

        AttribClassEntity signal = new AttribClassEntity();
        signal.setClassName(className + "信号属性");
        signal.setClassType(classType + "_SIGNAL");
        signal.setClassLevel(AttribClassEntity.CLASS_LEVEL_SIGNAL);
        signal.setClassDesc(className + "的信号属性");
        classId = attrClaDal.insert(signal);
        cla2ClaDal.insert(new Class2ClassEntity(classId, parentId));

        AttribClassEntity syslog = new AttribClassEntity();
        syslog.setClassName(className + "Syslog信息");
        syslog.setClassType(classType + "_SYSLOG");
        syslog.setClassLevel(AttribClassEntity.CLASS_LEVEL_SYSLOG);
        syslog.setClassDesc(className + "的Syslog信息");
        classId = attrClaDal.insert(syslog);
        cla2ClaDal.insert(new Class2ClassEntity(classId, parentId));
    }

    private void copyClasses(int classId, String parentId, String className, String classType, int mibId) throws Exception
    {
        List<AttribClassEntity> attrClaLst = attrClaDal.getAttribClassByParent(parentId);
        for (AttribClassEntity attrCla : attrClaLst)
        {
            // 复制属性集到新的属性集
            int oldClassId = attrCla.getClassId();
            String newClassType = classType;
            switch (attrCla.getClassLevel())
            {
            case AttribClassEntity.CLASS_LEVEL_CUSTOM:
                attrCla.setClassName(className + "自定义属性");
                attrCla.setClassDesc(className + "的自定义属性");
                newClassType = classType + "_CUSTOM";
                break;
            case AttribClassEntity.CLASS_LEVEL_MONITOR:
                attrCla.setClassName(className + "监测指标");
                attrCla.setClassDesc(className + "的监测指标");
                newClassType = classType + "_MONITOR";
                break;
            case AttribClassEntity.CLASS_LEVEL_PERF:
                attrCla.setClassName(className + "性能指标");
                attrCla.setClassDesc(className + "的性能指标");
                newClassType = classType + "_PERF";
                break;
            case AttribClassEntity.CLASS_LEVEL_TRAP:
                attrCla.setClassName(className + "Trap信息");
                attrCla.setClassDesc(className + "的Trap信息");
                newClassType = classType + "_TRAP";
                break;
            case AttribClassEntity.CLASS_LEVEL_SIGNAL:
                attrCla.setClassName(className + "信号属性");
                attrCla.setClassDesc(className + "的信号属性");
                newClassType = classType + "_SIGNAL";
                break;
            case AttribClassEntity.CLASS_LEVEL_SYSLOG:
                attrCla.setClassName(className + "Syslog信息");
                attrCla.setClassDesc(className + "的Syslog信息");
                newClassType = classType + "_SYSLOG";
                break;
            default:
                attrCla.setClassName(className + "配置信息");
                attrCla.setClassDesc(className + "的配置信息");
                newClassType = classType + "_" + attrCla.getClassType();
                break;
            }
            attrCla.setClassType(newClassType);
            attrCla.setCreateTime(new Date());
            attrCla.setMibId(mibId);
            int newClassId = attrClaDal.insert(attrCla);

            // 建立属性集与新属性集的关系
            cla2ClaDal.insert(new Class2ClassEntity(newClassId, classId));

            List<AttributeEntity> attrLst = attrDal.getByClassId(oldClassId);
            for (AttributeEntity attr : attrLst)
            {
                // 复制属性集中的属性到新的属性集
                attr.setClassType(newClassType);
                attr.setCreateTime(new Date());
                int attrId = attrDal.insert(attr);

                // 建立属性与属性集的关系
                attr2ClaDal.insert(new Attrib2ClassEntity(newClassId, attrId));
            }
        }
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAttribClass(String objXml) throws Exception
    {
        // 更新属性集
        attrClaDal.updateXml(objXml);
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String classId = map.get("CLASS_ID");
        String subTypes = map.get("SUB_TYPES");

        // 删除对象类型与子对象类型的关系
        SqlCondition cond1 = new SqlCondition("PARENT_ID", classId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        SqlCondition cond2 =
            new SqlCondition("USE_TYPE",
                String.valueOf(Class2ClassEntity.USE_TYPE_CONTAIN),
                SqlLogicType.And,
                SqlRelationType.Equal,
                SqlParamType.Numeric);
        cla2ClaDal.delete(cond1, cond2);

        if (subTypes != null && subTypes.length() > 0)
        {
            // 重新建立对象类型与子对象类型的关系
            createRelation(Integer.parseInt(classId), subTypes);
        }

        // 更新父MIB_ID时，同时更新子MIB_ID
        String mibId = map.get("MIB_ID");
        if (classId != null && mibId != null)
        {
            attrClaDal.updateMibId(classId, mibId);
        }
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAttribClassSet(int keyId) throws Exception
    {
        List<AttributeEntity> attrLst = attrDal.getByClassId(keyId);
        for (AttributeEntity attr : attrLst)
        {
            // 删除相应的属性
            attrDal.delete(attr.getAttribId());
        }

        // 删除属性与属性集之间的关系
        attr2ClaDal.deleteByClassId(keyId);

        // 删除属性集
        attrClaDal.delete(keyId);

        // 删除该属性集与父节点之间的关系
        cla2ClaDal.deleteByClassId(keyId);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAttribClass(int keyId) throws Exception
    {
        DefaultDal<SnmpObjTypeEntity> objTypeDal = new DefaultDal<SnmpObjTypeEntity>(SnmpObjTypeEntity.class);

        // 检测下面有没有子对象类型或者对象类型，有则不允许删除，必须先把所有子类删除之后才能删除该类型
        if (attrClaDal.hasChildClass(keyId))
        {
            throw new Exception("该设备类型下有子类型存在，请先删除其子类型。");
        }

        // 删除关联的属性集
        List<AttribClassEntity> attrClaLst = attrClaDal.getAttribClassByParent(String.valueOf(keyId));
        for (AttribClassEntity attrCla : attrClaLst)
        {
            List<AttributeEntity> attrLst = attrDal.getByClassId(attrCla.getClassId());
            for (AttributeEntity attr : attrLst)
            {
                // 删除相应的属性
                attrDal.delete(attr.getAttribId());
            }

            // 删除属性与属性集之间的关系
            attr2ClaDal.deleteByClassId(attrCla.getClassId());

            // 删除属性集
            attrClaDal.delete(attrCla.getClassId());
        }

        // 删除属性集与设备类型之间的关系
        cla2ClaDal.deleteByParentId(keyId);

        // 删除该属性集
        attrClaDal.delete(keyId);

        // 删除该属性集与父节点之间的关系
        cla2ClaDal.deleteByClassId(keyId);

        // 删除属性集对应的标识
        objTypeDal.delete(new SqlCondition("CLASS_ID", Integer.toString(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));

        // 删除资源类型关联的对象和子对象
        MObjectDal objDal = new MObjectDal();
        List<MObjectEntity> list =
            objDal.getLst(new SqlCondition("CLASS_ID", Integer.toString(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (list.size() > 0)
        {
            for (MObjectEntity entity : list)
            {
                int objId = entity.getObjId();
                // 删除对象
                objDal.delete(objId);
                // 删除对象的子对象
                objDal.delete(new SqlCondition("PARENT_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
            }
        }
    }

    /**
     * 根据类型ClassId获得设备厂商
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     * @throws NumberFormatException 异常
     */
    @Business
    public int getManufacturersByClassId(String classId) throws NumberFormatException, Exception
    {
        AttribClassDal attribClassDal = new AttribClassDal();
        return attribClassDal.getManufacturersByClassId(classId).getManId();
    }

    /**
     * 获取某分类的所有子分类
     * @param classId 父分类ID
     * @return 返回所有分类的XML
     * @throws Exception 异常
     */
    public String querySubClassByParentId(int classId) throws Exception
    {
        String result = "";

        try
        {
            List<Map<String, String>> subLst =
                new SqlQueryUtil().getLst("SELECT A.CLASS_ID,A.CLASS_NAME,A.CLASS_TYPE,B.PARENT_ID"
                    + " FROM BMP_ATTRIBCLASS A LEFT JOIN BMP_CLASS2CLASS B ON A.CLASS_ID = B.CLASS_ID" + " WHERE A.CLASS_LEVEL=2 AND B.PARENT_ID = "
                    + classId);

            getSubClasses(subLst, subLst);

            result = new SqlQueryUtil().listToXmlString(subLst);
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return result;
    }

    /**
     * 递归获取子分类
     * @param lst 上一级查询的结果
     * @param result 存储每次递归获取到的结果
     * @throws Exception
     */
    private void getSubClasses(List<Map<String, String>> lst, List<Map<String, String>> result) throws Exception
    {
        try
        {
            if (lst != null && lst.size() > 0)
            {
                String classIds = "";
                int size = lst.size();
                for (int i = 0; i < size; i++)
                {
                    classIds += lst.get(i).get("CLASS_ID");
                    if (i != (size - 1))
                    {
                        classIds += ",";
                    }
                }

                List<Map<String, String>> subLst =
                    new SqlQueryUtil().getLst("SELECT A.CLASS_ID,A.CLASS_NAME,A.CLASS_TYPE,B.PARENT_ID"
                        + " FROM BMP_ATTRIBCLASS A LEFT JOIN BMP_CLASS2CLASS B ON A.CLASS_ID = B.CLASS_ID"
                        + " WHERE A.CLASS_LEVEL=2 AND B.PARENT_ID IN (" + classIds + ")");
                if (subLst != null && subLst.size() > 0)
                {
                    for (Map<String, String> map : subLst)
                    {
                        result.add(map);
                    }

                    getSubClasses(subLst, result);
                }

            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**
     * 检测下面有没有子对象类型或者对象类型
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public boolean AttribClassCHild(int keyId) throws Exception
    {
        boolean result = false;
        DefaultDal<SnmpObjTypeEntity> objTypeDal = new DefaultDal<SnmpObjTypeEntity>(SnmpObjTypeEntity.class);
        result = attrClaDal.hasChildClass(keyId);
        return result;
    }
}
