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

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.Attrib2ClassEntity;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.exception.UncheckedSQLException;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ?
 */
public class AttributeDal extends DefaultDal<AttributeEntity>
{

    /**
     * 构造方法
     */
    public AttributeDal()
    {
        super(AttributeEntity.class);
    }

    /**
     * 根据属性ID获取属性
     * @param ids 参数
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<AttributeEntity> getAttrs(ArrayList<String> ids) throws Exception
    {
        if (ids == null || ids.isEmpty())
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(tableInfo.tableName);
        sb.append(" WHERE ATTRIB_ID IN (");
        for (int i = 0; i < ids.size(); i++)
        {
            String id = ids.get(i);
            if (id == null || id.isEmpty())
            {
                throw new UncheckedSQLException("属性ID中含有为空或NULL的值。");
            }
            sb.append(ids.get(i));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return (ArrayList<AttributeEntity>) getLst(sb.toString());
    }

    /**
     * 获取未实例化的属性
     * @param objId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<AttributeEntity> getNotinsAttrib(int objId) throws Exception
    {
        ArrayList<AttributeEntity> attrs = new ArrayList<AttributeEntity>();
        MObjectDal modao = new MObjectDal();
        MObjectEntity mo = modao.get(objId);
        if (mo != null)
        {
            ArrayList<AttributeEntity> alls = getByType(mo.getClassId());
            ArrayList<AttributeEntity> inss =
                (ArrayList<AttributeEntity>) getLst("SELECT * FROM BMP_OBJATTRIB a "
                    + "LEFT JOIN BMP_ATTRIBUTE b ON a.ATTRIB_ID = b.ATTRIB_ID WHERE a.OBJ_ID = " + objId);
            for (int i = 0; i < alls.size(); i++)
            {
                AttributeEntity temp = alls.get(i);
                boolean isContain = false;
                for (int j = 0; j < inss.size(); j++)
                {
                    AttributeEntity temp1 = inss.get(j);
                    if (temp1.getAttribId() == temp.getAttribId())
                    {
                        isContain = true;
                        break;
                    }
                }
                if (!isContain)
                {
                    attrs.add(temp);
                }
            }
        }
        return attrs;
    }

    /**
     * 获取已经实例化的属性
     * @param objId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<AttributeEntity> getInsedAttrib(int objId) throws Exception
    {
        ArrayList<AttributeEntity> attrs = null;
        MObjectDal modao = new MObjectDal();
        MObjectEntity mo = modao.get(objId);
        if (mo != null)
        {
            attrs =
                (ArrayList<AttributeEntity>) getLst("SELECT distinct b.* FROM BMP_OBJATTRIB a LEFT JOIN "
                    + "BMP_ATTRIBUTE b ON a.ATTRIB_ID = b.ATTRIB_ID WHERE a.OBJ_ID = " + objId);
        }

        return attrs;
    }

    /**
     * 根据类型获取属性集合
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<AttributeEntity> getByType(int classId) throws Exception
    {
        AttribClassDal acdao = new AttribClassDal();
        List<AttribClassEntity> acs = acdao.getBasic(classId);
        StringBuilder sb = new StringBuilder();
        sb.append(classId);
        for (AttribClassEntity ac : acs)
        {
            sb.append(",");
            sb.append(ac.getClassId());
        }

        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName("BMP_ATTRIB2CLASS a LEFT JOIN BMP_ATTRIBUTE b ON a.ATTRIB_ID = b.ATTRIB_ID");
        cmd.addField("b.*", "");
        cmd.setFilter(new SqlCondition[] { new SqlCondition("a.CLASS_ID", sb.toString(), SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric),
            new SqlCondition("b.ATTRIB_ID", "0", SqlLogicType.And, SqlRelationType.Than, SqlParamType.Numeric) });
        return (ArrayList<AttributeEntity>) getLst(cmd.toString());
    }

    /**
     * @param attr 属性
     * @param classId 参数
     * @return 结果
     */
    @Transactional
    public int insert(AttributeEntity attr, int classId)
    {
        Attrib2ClassDal acdao = new Attrib2ClassDal();
        int index = -1;
        try
        {
            this.insert(attr);
            acdao.insert(new Attrib2ClassEntity(classId, attr.getAttribId()));
        }
        catch (Exception e)
        {
            throw new UncheckedSQLException(e.getMessage(), e);
        }
        return index;
    }

    /**
     * @param attrs 参数
     * @param classId 参数
     */
    @Transactional
    public void insert(List<AttributeEntity> attrs, int classId)
    {
        Attrib2ClassDal acdao = new Attrib2ClassDal();
        try
        {
            for (AttributeEntity attr : attrs)
            {
                this.insert(attr);
                acdao.insert(new Attrib2ClassEntity(classId, attr.getAttribId()));
            }
        }
        catch (Exception e)
        {
            throw new UncheckedSQLException(e.getMessage(), e);
        }
    }

    /**
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<AttributeEntity> getAllMirandaAttr() throws Exception
    {
        return this.getLst("SELECT * FROM BMP_ATTRIBUTE WHERE ATTRIB_TYPE = 'SNMP_NETDEV_MIRANDA'");
    }

    /**
     * @param classId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<AttributeEntity> getByClassId(int classId) throws Exception
    {
        String sql = "SELECT a.* FROM BMP_ATTRIBUTE a LEFT JOIN BMP_ATTRIB2CLASS b ON a.ATTRIB_ID = b.ATTRIB_ID WHERE b.CLASS_ID = " + classId;
        return this.getLst(sql);
    }

    /**
     * @param alarmId 告警
     * @param attribIds 对象属性
     * @throws Exception 异常
     */
    @Transactional
    public void updateAttrAlarm(int alarmId, String attribIds) throws Exception
    {
        String sql = "UPDATE BMP_ATTRIBUTE SET ALARM_ID=" + alarmId + " WHERE ATTRIB_ID IN (" + attribIds + ")";
        this.update(sql);
    }

    /**
     * @param attribId 参数
     * @param classType 参数
     * @throws Exception 异常
     */
    @Transactional
    public void updateClassType(int attribId, String classType) throws Exception
    {
        String sql = "UPDATE BMP_ATTRIBUTE SET CLASS_TYPE='%s' WHERE ATTRIB_ID=%s";
        sql = String.format(sql, classType, attribId);
        this.update(sql);
    }

    /**
     * @param oBegin 开始
     * @param oEnd 结束
     * @param nBegin 参数
     * @throws Exception 异常
     */
    @Transactional
    public void rearrange(int oBegin, int oEnd, int nBegin) throws Exception
    {
        for (int i = oBegin, j = 0; i <= oEnd; i++)
        {
            AttributeEntity attr = this.get(i);
            this.delete(i);
            if (attr != null)
            {
                attr.setAttribId(nBegin + j);
                this.insert(attr, false);
                j++;
            }
        }
    }

    /**
     * @param classId 参数
     * @param begin 开始
     * @param end 结束
     * @throws Exception 异常
     */
    public void batchFix(int classId, int begin, int end) throws Exception
    {
        AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
        AttribClassEntity ac = acdal.get(classId);
        if (ac != null)
        {
            AttributeDal adal = ClassWrapper.wrapTrans(AttributeDal.class);
            Attrib2ClassDal a2cdal = ClassWrapper.wrapTrans(Attrib2ClassDal.class);
            a2cdal.deleteByClassId(classId);
            for (int i = begin; i <= end; i++)
            {
                adal.updateClassType(i, ac.getClassType());
                a2cdal.insert(new Attrib2ClassEntity(classId, i));
            }
        }
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        AttributeDal adal = new AttributeDal();
        adal.batchFix(2217, 10174, 10181);
    }
}
