package jetsennet.jbmp.dataaccess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.Attrib2ClassEntity;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.Class2ClassEntity;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;
import jetsennet.jbmp.formula.FormulaValidate;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author liwei 资源库导入导出
 */
public class ResourceDal
{
    private static final Logger logger = Logger.getLogger(ResourceDal.class);

    // 数据库操作
    private AttribClassDal attribclassDal = ClassWrapper.wrapTrans(AttribClassDal.class);
    private Class2ClassDal class2Dal = ClassWrapper.wrapTrans(Class2ClassDal.class);
    private AttributeDal attributeDal = ClassWrapper.wrapTrans(AttributeDal.class);
    private Attrib2ClassDal attrib2Dal = ClassWrapper.wrapTrans(Attrib2ClassDal.class);
    private SnmpObjTypeDal snmpDal = ClassWrapper.wrapTrans(SnmpObjTypeDal.class);

    private static int mId = 0;

    /**
     * 主方法测试入口
     * @param args 参数
     */
    public static void main(String[] args)
    {
        ResourceDal t = new ResourceDal();
        // t.exportXml(10444);

    }

    /**
     * 导出资源类型xml
     * @param classId 类id
     * @return dom节点
     */
    public Document exportXml(int classId)
    {
        Document document = (Document) DocumentHelper.createDocument();
        Element data = document.addElement("Data");
        // 导入BMP_ATTRIBCLASS
        Element attri = data.addElement("attribClass");
        addAttriElement(attri, classId);
        return document;

    }

    /**
     * 获取attribClass父节点
     * @param e 元素
     * @param id classId
     */
    public void addAttriElement(Element e, int id)
    {
        // AttribClassDal t = new AttribClassDal();
        try
        {
            // 得到第一个节点
            AttribClassEntity entity = attribclassDal.getManufacturersByClassId(Integer.toString(id));
            Element attriParent = e.addElement("attriClassParent");
            // 给父节点添加数据
            addAttriClassTable(attriParent, entity);
            // 给父节点的子节点添加数据
            addAttriChild(attriParent, id);
            addsnmpObj(attriParent, id);
        }
        catch (Exception e1)
        {
            logger.error("给attribClass父类添加xml异常" + e1);
        }

    }

    /**
     * 给子节点添加数据
     * @param e 元素
     * @param parentId classid
     */
    public void addAttriChild(Element e, int parentId)
    {
        // AttribClassDal t = new AttribClassDal();
        Element children = e.addElement("CHILDREN");
        try
        {
            List<AttribClassEntity> entitys = attribclassDal.getChildEntity(parentId, 1);
            Iterator iter = entitys.iterator();
            while (iter.hasNext())
            {
                Element attriTable = children.addElement("attriClassTable");
                AttribClassEntity entity = (AttribClassEntity) iter.next();
                addAttriClassTable(attriTable, entity);
            }
        }
        catch (Exception e1)
        {
            logger.error("给attriClassTable节点添加xml异常" + e1);
        }
    }

    /**
     * 添加元素
     * @param attriTable 元素
     * @param entity 实体
     */
    public void addAttriClassTable(Element attriTable, AttribClassEntity entity)
    {
        try
        {
            Element classId = attriTable.addElement("CLASS_ID");
            classId.setText(entity.getClassId() + "");
            Element className = attriTable.addElement("CLASS_NAME");
            className.setText(entity.getClassName() + "");
            Element mibId = attriTable.addElement("MIB_ID");
            mibId.setText(entity.getMibId() + "");
            Element classType = attriTable.addElement("CLASS_TYPE");
            classType.setText(entity.getClassType() + "");
            Element classLevel = attriTable.addElement("CLASS_LEVEL");
            classLevel.setText(entity.getClassLevel() + "");
            Element classGroup = attriTable.addElement("CLASS_GROUP");
            classGroup.setText(entity.getClassGroup() + "");
            Element classDesc = attriTable.addElement("CLASS_DESC");
            classDesc.setText(entity.getClassDesc() + "");
            Element iconsrc = attriTable.addElement("ICON_SRC");
            iconsrc.setText(entity.getIconSrc() + "");
            Element manId = attriTable.addElement("MAN_ID");
            manId.setText(entity.getManId() + "");
            Element viewPos = attriTable.addElement("VIEW_POS");
            viewPos.setText(entity.getViewPos() + "");
            Element user = attriTable.addElement("CREATE_USER");
            user.setText(entity.getCreateUser() + "");
            Element time = attriTable.addElement("CREATE_TIME");
            time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(entity.getCreateTime()));
            Element field = attriTable.addElement("FIELD_1");
            field.setText(entity.getField1() + "");
            // Element attributes = attriTable.addElement("ATTRIBUTES");
            addAttributeElement(attriTable, entity.getClassId());
        }
        catch (Exception e)
        {
            logger.error("从AttribClassEntity解析到xml中异常！");
        }
    }

    /**
     * 给子节点添加class2class数据
     * @param e 元素
     * @param parentId classId
     */
    public void addClass2Class(Element e, int parentId)
    {
        // Class2ClassDal t = new Class2ClassDal();
        Element children = e.addElement("CLASS2CLASS");
        try
        {
            ArrayList<Class2ClassEntity> entitys = class2Dal.getEntityByParentId(parentId, 1);
            Iterator iter = entitys.iterator();
            while (iter.hasNext())
            {
                Element classTable = children.addElement("class2classTable");
                Class2ClassEntity entity = (Class2ClassEntity) iter.next();
                addClassTable(classTable, entity);
            }
        }
        catch (Exception e1)
        {
            logger.error("给attribClassz子类添加xml异常！" + e1);
        }
    }

    /**
     * 添加元素
     * @param classTable 元素
     * @param entity 实体
     */
    public void addClassTable(Element classTable, Class2ClassEntity entity)
    {
        Element classId = classTable.addElement("CLASS_ID");
        classId.setText(entity.getClassId() + "");
        Element className = classTable.addElement("PARENT_ID");
        className.setText(entity.getParentId() + "");
        Element classType = classTable.addElement("USE_TYPE");
        classType.setText(entity.getUseType() + "");
    }

    /**
     * 获取attribClass父节点
     * @param e 元素
     * @param id classId
     */
    public void addAttributeElement(Element e, int id)
    {
        // AttributeDal t = new AttributeDal();
        Element attributes = e.addElement("attributes");
        try
        {
            // 得到第一个节点
            List<AttributeEntity> entitys = attributeDal.getByClassId(id);
            Iterator iter = entitys.iterator();
            while (iter.hasNext())
            {
                Element attributeTable = attributes.addElement("attributeTable");
                AttributeEntity entity = (AttributeEntity) iter.next();
                addAttributeTable(attributeTable, entity);
            }
        }
        catch (Exception e1)
        {
            logger.error("添加attribute元素到xml异常！" + e1);
        }
    }

    /**
     * 添加元素
     * @param attributrTable 元素
     * @param entity 实体
     */
    public void addAttributeTable(Element attributrTable, AttributeEntity entity)
    {
        try
        {
            Element attribId = attributrTable.addElement("ATTRIB_ID");
            attribId.setText(entity.getAttribId() + "");
            Element attribName = attributrTable.addElement("ATTRIB_NAME");
            attribName.setText(entity.getAttribName() + "");
            Element attribValue = attributrTable.addElement("ATTRIB_VALUE");
            attribValue.setText(entity.getAttribValue() + "");
            Element valueType = attributrTable.addElement("VALUE_TYPE");
            valueType.setText(entity.getValueType() + "");
            Element dataEcode = attributrTable.addElement("DATA_ENCODING");
            dataEcode.setText(entity.getDataEncoding() + "");
            Element classType = attributrTable.addElement("CLASS_TYPE");
            classType.setText(entity.getClassType() + "");
            Element attribType = attributrTable.addElement("ATTRIB_TYPE");
            attribType.setText(entity.getAttribType() + "");
            Element attribMode = attributrTable.addElement("ATTRIB_MODE");
            attribMode.setText(entity.getAttribMode() + "");
            Element attribParam = attributrTable.addElement("ATTRIB_PARAM");
            attribParam.setText(entity.getAttribParam() + "");
            Element datatype = attributrTable.addElement("DATA_TYPE");
            datatype.setText(entity.getDataType() + "");
            Element dataUnit = attributrTable.addElement("DATA_UNIT");
            dataUnit.setText(entity.getDataUnit() + "");
            Element attribCode = attributrTable.addElement("ATTRIB_CODE");
            attribCode.setText(entity.getAttribCode() + "");
            Element attribDesc = attributrTable.addElement("ATTRIB_DESC");
            attribDesc.setText(entity.getAttribDesc() + "");
            Element collTimeSpan = attributrTable.addElement("COLL_TIMESPAN");
            collTimeSpan.setText(entity.getCollTimeSpan() + "");
            Element isVisable = attributrTable.addElement("IS_VISIBLE");
            isVisable.setText(entity.getIsVisible() + "");
            Element viewType = attributrTable.addElement("VIEW_TYPE");
            viewType.setText(entity.getViewType() + "");
            Element alarmId = attributrTable.addElement("ALARM_ID");
            alarmId.setText(entity.getAlarmId() + "");
            Element user = attributrTable.addElement("CREATE_USER");
            user.setText(entity.getCreateUser() + "");
            Element careteTime = attributrTable.addElement("CREATE_TIME");
            careteTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(entity.getCreateTime()));
            Element field = attributrTable.addElement("FIELD_1");
            field.setText(entity.getField1() + "");
        }
        catch (Exception e)
        {
            logger.error("从AttributeEntity解析到xml中异常！");
        }
    }

    /**
     * 给子节点添加addsnmpObjType数据
     * @param e 元素
     * @param parentId parentId
     */
    public void addsnmpObj(Element e, int parentId)
    {
        // SnmpObjTypeDal t = new SnmpObjTypeDal();
        Element children = e.addElement("snmpObjType");
        try
        {
            ArrayList<SnmpObjTypeEntity> entitys = snmpDal.getEntityByClassId(parentId);
            Iterator iter = entitys.iterator();
            while (iter.hasNext())
            {
                Element classTable = children.addElement("snmpObjTypeTable");
                SnmpObjTypeEntity entity = (SnmpObjTypeEntity) iter.next();
                addSnmpOBjTable(classTable, entity);
            }
        }
        catch (Exception e1)
        {
            logger.error("给addsnmpObjType子类添加xml异常！" + e1);
        }
    }

    /**
     * 添加元素
     * @param snmpObjTable 元素
     * @param entity 实体
     */
    public void addSnmpOBjTable(Element snmpObjTable, SnmpObjTypeEntity entity)
    {
        try
        {
            Element typeId = snmpObjTable.addElement("TYPE_ID");
            typeId.setText(entity.getTypeId() + "");
            Element snmpSysoid = snmpObjTable.addElement("SNMP_SYSOID");
            snmpSysoid.setText(entity.getSnmpSysoid() + "");
            Element snmpValue = snmpObjTable.addElement("SNMP_VALUE");
            snmpValue.setText(entity.getSnmpValue() + "");
            Element condition = snmpObjTable.addElement("CONDITION");
            condition.setText(entity.getCondition() + "");
            Element classId = snmpObjTable.addElement("CLASS_ID");
            classId.setText(entity.getClassId() + "");
            Element field = snmpObjTable.addElement("FIELD_1");
            field.setText(entity.getField1() + "");
        }
        catch (Exception e)
        {
            logger.error("从SnmpObjTypeEntity解析到xml中异常！");
        }
    }

    /**
     * fag 值为 fail表示出现异常； success表示成功
     * @param xml resxml字符串
     * @param parentId 父id
     * @return 返回结果
     */
    public String importResXml(String xml, int parentId, int mibId)
    {
        mId = mibId;
        String fag = "fail";
        Document doc = null;
        try
        {
            doc = DocumentHelper.parseText(xml);
        }
        catch (DocumentException e2)
        {
            logger.info("资源类型字符串转换成xml格式出错，信息：" + e2);
            return fag;
        }
        try
        {
            logger.info("开始资源类型导入");
            Element data = doc.getRootElement(); // 获取根节点
            Element attribClass = data.element("attribClass");
            Element attriClassParent = attribClass.element("attriClassParent");
            // 判断是否可以导入
            fag = findAttribClass(attriClassParent);
            if (!"success".equals(fag))
            {
                return fag;
            }
            if (!insertResouce(attriClassParent, parentId))
            {
                fag = "fail";
                return fag;
            }
            logger.info("导入资源类型成功");
            fag = "success";
        }
        catch (Exception e)
        {
            logger.info("导入资源类型失败", e);
            fag = "fail";
            return fag;
        }
        return fag;
    }

    /**
     * 把解析出来的xml导入到资源类型中
     * @param e 元素
     * @param parentId id
     * @return 返回结果
     */
    public boolean insertResouce(Element e, int parentId)
    {
        boolean fag = true;
        int classId;
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            exec.transBegin();
            classId = insertAttribClass(e, parentId);
            if (classId == -1)
            {
                return false;
            }
            Element children = e.element("CHILDREN");
            Iterator iter = children.elementIterator("attriClassTable");
            while (iter.hasNext())
            {
                Element table = (Element) iter.next();
                int idfag;
                idfag = insertAttribClass(table, classId);
                if (idfag == -1)
                {
                    return false;
                }
            }
            fag = insertSnmp(e, classId);
            if (fag)
            {
                exec.transCommit();
            }
            else
            {
                exec.transRollback();
            }

        }
        catch (Exception ex)
        {
            exec.transRollback();
            return false;
        }
        finally
        {
            SqlExecutorFacotry.unbindSqlExecutor();
        }
        return fag;
    }

    /**
     * 把解析出来的xml,把xml中对应的entity插入到BMP_ATTRIBCLASS
     * @param e 元素
     * @param parentId id
     * @return 返回结果
     */
    public int insertAttribClass(Element e, int parentId)
    {
        int classId = -1;
        String className = e.elementTextTrim("CLASS_NAME");
        String classType = e.elementTextTrim("CLASS_TYPE");
        String mibId = e.elementTextTrim("MIB_ID");
        String classLevel = e.elementTextTrim("CLASS_LEVEL");
        String classGroup = e.elementTextTrim("CLASS_GROUP");
        String classDesc = e.elementTextTrim("CLASS_DESC");
        String iconSrc = e.elementTextTrim("ICON_SRC");
        String manId = e.elementTextTrim("MAN_ID");
        String viewPos = e.elementTextTrim("VIEW_POS");
        String createUser = e.elementTextTrim("CREATE_USER");
        String createTime = e.elementTextTrim("CREATE_TIME");
        String field1 = e.elementTextTrim("FIELD_1");
        AttribClassEntity attribclass = new AttribClassEntity();
        attribclass.setClassName(className);
        attribclass.setClassType(classType);
        if (!"".equals(mibId) && mibId != null)
        {
            attribclass.setMibId(mId);
        }
        if (!"".equals(classLevel) && classLevel != null)
        {
            attribclass.setClassLevel(Integer.valueOf(classLevel));
        }
        if (!"".equals(classGroup) && classGroup != null)
        {
            attribclass.setClassGroup(Integer.valueOf(classGroup));
        }
        if (!"".equals(classDesc) && classDesc != null)
        {
            if ("null".equals(classDesc))
            {
                attribclass.setClassDesc("");
            }
            else
            {
                attribclass.setClassDesc(classDesc);
            }
        }
        // attribclass.setClassDesc(classDesc);
        // attribclass.setIconSrc(iconSrc);
        if (!"".equals(iconSrc) && iconSrc != null)
        {
            if ("null".equals(iconSrc))
            {
                attribclass.setIconSrc("");
            }
            else
            {
                attribclass.setIconSrc(iconSrc);
            }
        }
        if (!"".equals(manId) && manId != null)
        {
            attribclass.setManId(Integer.valueOf(manId));
        }
        if (!"".equals(viewPos) && viewPos != null)
        {
            attribclass.setViewPos(Integer.valueOf(viewPos));
        }
        if (!"".equals(createUser) && createUser != null)
        {
            if ("null".equals(createUser))
            {
                attribclass.setCreateUser("");
            }
            else
            {
                attribclass.setCreateUser(createUser);
            }
        }
        // attribclass.setCreateUser(createUser);
        try
        {
            attribclass.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(createTime));
        }
        catch (ParseException exp)
        {
            logger.error("attribClass库中时间转换异常" + exp);
        }
        if (!"".equals(field1) && field1 != null)
        {
            if ("null".equals(field1))
            {
                attribclass.setField1("");
            }
            else
            {
                attribclass.setField1(field1);
            }
        }
        // attribclass.setField1(field1);
        // AttribClassDal t = new AttribClassDal();
        try
        {
            classId = attribclassDal.insert(attribclass);
            if (parentId > 0)
            {
                // Class2ClassDal cla2ClaDal = new Class2ClassDal();
                class2Dal.insert(new Class2ClassEntity(classId, parentId));
            }
            insertAttribute(e, classId);
        }
        catch (Exception exp2)
        {
            logger.error("解析资源类型xml时，插入attribClass 实体异常。" + exp2);
            classId = -1;
        }
        return classId;
    }

    /**
     * 解析dom中的元素，把Attribute属性插入数据库实体中
     * @param e 元素
     * @param classId id
     */
    public void insertAttribute(Element e, int classId)
    {

        Element attributes = e.element("attributes");
        Iterator iter = attributes.elementIterator("attributeTable");
        while (iter.hasNext())
        {
            Element attributeTable = (Element) iter.next();
            String attribName = attributeTable.elementTextTrim("ATTRIB_NAME");
            String attribValue = attributeTable.elementTextTrim("ATTRIB_VALUE");
            String valueType = attributeTable.elementTextTrim("VALUE_TYPE");
            String dataEncoding = attributeTable.elementTextTrim("DATA_ENCODING");
            String classType = attributeTable.elementTextTrim("CLASS_TYPE");
            String attribType = attributeTable.elementTextTrim("ATTRIB_TYPE");
            String attribMode = attributeTable.elementTextTrim("ATTRIB_MODE");
            String attribParam = attributeTable.elementTextTrim("ATTRIB_PARAM");
            String dataType = attributeTable.elementTextTrim("DATA_TYPE");
            String dataUnit = attributeTable.elementTextTrim("DATA_UNIT");
            String attribCode = attributeTable.elementTextTrim("ATTRIB_CODE");
            String attribDesc = attributeTable.elementTextTrim("ATTRIB_DESC");
            String collTimespan = attributeTable.elementTextTrim("COLL_TIMESPAN");
            String isVisible = attributeTable.elementTextTrim("IS_VISIBLE");
            String viewType = attributeTable.elementTextTrim("VIEW_TYPE");
            String alarmId = attributeTable.elementTextTrim("ALARM_ID");
            String createUser = attributeTable.elementTextTrim("CREATE_USER");
            String createTime = attributeTable.elementTextTrim("CREATE_TIME");
            String field1 = attributeTable.elementTextTrim("FIELD_1");
            AttributeEntity attrib = new AttributeEntity();
            attrib.setAttribName(attribName);
            if (!"".equals(attribValue) && attribValue != null)
            {
                if ("null".equals(attribValue))
                {
                    attrib.setAttribValue("");
                }
                else
                {
                    attrib.setAttribValue(attribValue);
                }
            }
            // attrib.setAttribValue(attribValue);
            if (valueType != null && !"".equals(valueType))
            {
                attrib.setValueType(Integer.valueOf(valueType));
            }
            if (!"".equals(dataEncoding) && dataEncoding != null)
            {
                if ("null".equals(dataEncoding))
                {
                    attrib.setDataEncoding("");
                }
                else
                {
                    attrib.setDataEncoding(dataEncoding);
                }
            }
            // attrib.setDataEncoding(dataEncoding);
            // attrib.setClassType(classType);
            if (!"".equals(classType) && classType != null)
            {
                if ("null".equals(classType))
                {
                    attrib.setClassType("");
                }
                else
                {
                    attrib.setClassType(classType);
                }
            }
            if (attribType != null && !"".equals(attribType))
            {
                attrib.setAttribType(Integer.valueOf(attribType));
            }
            if (attribMode != null && !"".equals(attribMode))
            {
                attrib.setAttribMode(Integer.valueOf(attribMode));
            }
            if (!"".equals(attribParam) && attribParam != null)
            {
                if ("null".equals(attribParam))
                {
                    attrib.setAttribParam("");
                }
                else
                {
                    if ("101".equals(attribType) || "102".equals(attribType) || "103".equals(attribType) || "106".equals(attribType))
                    {
                        int enumValue = valid(attribParam, mId);
                        attrib.setValueType(enumValue);
                    }
                    attrib.setAttribParam(attribParam);
                }
            }
            // attrib.setAttribParam(attribParam);
            if (dataType != null && !"".equals(dataType))
            {
                attrib.setDataType(Integer.valueOf(dataType));
            }
            if (!"".equals(dataUnit) && dataUnit != null)
            {
                if ("null".equals(dataUnit))
                {
                    attrib.setDataUnit("");
                }
                else
                {
                    attrib.setDataUnit(dataUnit);
                }
            }
            // attrib.setDataUnit(dataUnit);
            // attrib.setAttribCode(attribCode);
            if (!"".equals(attribCode) && attribCode != null)
            {
                if ("null".equals(attribCode))
                {
                    attrib.setAttribCode("");
                }
                else
                {
                    attrib.setAttribCode(attribCode);
                }
            }
            if (!"".equals(attribDesc) && attribDesc != null)
            {
                if ("null".equals(attribDesc))
                {
                    attrib.setAttribDesc("");
                }
                else
                {
                    attrib.setAttribDesc(attribDesc);
                }
            }
            // attrib.setAttribDesc(attribDesc);
            if (collTimespan != null && !"".equals(collTimespan))
            {
                attrib.setCollTimeSpan(Integer.valueOf(collTimespan));
            }
            if (isVisible != null && !"".equals(isVisible))
            {
                attrib.setIsVisible(Integer.valueOf(isVisible));
            }
            if (!"".equals(viewType) && viewType != null)
            {
                if ("null".equals(viewType))
                {
                    attrib.setViewType("");
                }
                else
                {
                    attrib.setViewType(viewType);
                }
            }
            // AlarmId不存在时，默认插入一个新的Alarm
            if (alarmId == null || "".equals(alarmId))
            {
                alarmId = "0";
            }
            int iAlarmId = Integer.valueOf(alarmId);
            if (iAlarmId <= 0)
            {
                iAlarmId = this.newAlarm();
                attrib.setAlarmId(iAlarmId);
            }
            else
            {
                attrib.setAlarmId(iAlarmId);
            }

            if (!"".equals(createUser) && createUser != null)
            {
                if ("null".equals(createUser))
                {
                    attrib.setCreateUser("");
                }
                else
                {
                    attrib.setCreateUser(createUser);
                }
            }
            // attrib.setCreateUser(createUser);
            if (createTime != null && !"".equals(createTime))
            {
                try
                {
                    attrib.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(createTime));
                }
                catch (ParseException exp)
                {
                    logger.error("导出资源类型时，将时间插入实体异常" + exp);
                }
            }
            if (!"".equals(field1) && field1 != null)
            {
                if ("null".equals(field1))
                {
                    attrib.setField1("");
                }
                else
                {
                    attrib.setField1(field1);
                }
            }
            // attrib.setField1(field1);
            // AttributeDal t = new AttributeDal();
            try
            {
                int attributeId = attributeDal.insert(attrib);
                // Attrib2ClassDal dal = new Attrib2ClassDal();
                attrib2Dal.insert(new Attrib2ClassEntity(classId, attributeId));

            }
            catch (Exception exp2)
            {
                logger.error("导出资源类型时，插入Attribute实体异常" + exp2);
            }
        }
    }

    private int newAlarm()
    {
        int retval = 0;
        try
        {
            AlarmDal adal = ClassWrapper.wrapTrans(AlarmDal.class);
            AlarmEntity alarm = AlarmEntity.newUnValidAlarm("未命名规则");
            retval = adal.insert(alarm);
        }
        catch (Exception ex)
        {
            logger.error("生成未命名规则失败", ex);
        }
        return retval;
    }

    /**
     * 解析dom中的元素，把属性插入数据库实体 SnmpObjType中
     * @param e 元素
     * @param classId id
     * @return 结果
     */
    public boolean insertSnmp(Element e, int classId)
    {
        boolean fag = true;
        Element attributes = e.element("snmpObjType");
        Iterator iter = attributes.elementIterator("snmpObjTypeTable");
        while (iter.hasNext())
        {
            Element snmpTable = (Element) iter.next();
            String snmpSysoid = snmpTable.elementTextTrim("SNMP_SYSOID");
            String snmpValue = snmpTable.elementTextTrim("SNMP_VALUE");
            String condition = snmpTable.elementTextTrim("CONDITION");
            String field1 = snmpTable.elementTextTrim("FIELD_1");
            SnmpObjTypeEntity entity = new SnmpObjTypeEntity();
            if (!"".equals(snmpSysoid) && snmpSysoid != null)
            {
                if ("null".equals(snmpSysoid))
                {
                    entity.setSnmpSysoid("");
                }
                else
                {
                    entity.setSnmpSysoid(snmpSysoid);
                }
            }
            // entity.setSnmpSysoid(snmpSysoid);
            // entity.setSnmpValue(snmpValue);
            if (!"".equals(snmpValue) && snmpValue != null)
            {
                if ("null".equals(snmpValue))
                {
                    entity.setSnmpValue("");
                }
                else
                {
                    entity.setSnmpValue(snmpValue);
                }
            }
            if (!"".equals(condition) && condition != null)
            {
                if ("null".equals(condition))
                {
                    entity.setCondition("");
                }
                else
                {
                    entity.setCondition(condition);
                }
            }
            // entity.setCondition(condition);
            // entity.setField1(field1);
            if (!"".equals(field1) && field1 != null)
            {
                if ("null".equals(field1))
                {
                    entity.setField1("");
                }
                else
                {
                    entity.setField1(field1);
                }
            }
            entity.setClassId(classId);
            // SnmpObjTypeDal t = new SnmpObjTypeDal();
            try
            {
                snmpDal.insert(entity);
            }
            catch (Exception e1)
            {
                logger.error("资源类型导入时，插入SnmpObjTypeEntity实体异常" + e1);
                return false;
            }
        }
        return fag;
    }

    /**
     * 判断别名是否重复了
     * @param e 元素
     * @return 是否有相同别名，有就返回别名
     */
    public String findAttribClass(Element e)
    {
        String fag = "success";
        try
        {
            String classType = e.elementTextTrim("CLASS_TYPE");
            // AttribClassDal t = new AttribClassDal();
            List<AttribClassEntity> list = attribclassDal.getListByClassType(classType);
            if (list.size() > 0)
            {
                // 存在相同别名的资源类型，不能导入，返回别名
                return classType;
            }
            else
            {
                fag = "success";
            }
        }
        catch (Exception exp2)
        {
            logger.error("查找别名是否重复时失败" + exp2);
            fag = "fail";
            return fag;
        }
        return fag;
    }

    /**
     * @param formula 公式
     * @param mibId mib库ID
     * @return 枚举值
     */
    @SuppressWarnings("finally")
    public int valid(String formula, int mibId)
    {
        int temp = 0;
        try
        {
            FormulaValidate fv = new FormulaValidate();
            temp = fv.validate(formula, mibId);
        }
        catch (Exception ex)
        {
            logger.error("导入资源类型时校验出错，校验时用的mibid是" + mibId + ";异常是：" + ex);
        }
        finally
        {
            return temp;
        }
    }

}
