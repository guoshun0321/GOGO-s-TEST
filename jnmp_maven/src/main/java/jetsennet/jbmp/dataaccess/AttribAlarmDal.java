/************************************************************************
日 期：2012-04-05
作 者: 梁宏杰
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;

import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AttribAlarmEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author lianghongjie
 */
public class AttribAlarmDal extends DefaultDal<AttribAlarmEntity>
{
    /**
     * 构造放
     */
    public AttribAlarmDal()
    {
        super(AttribAlarmEntity.class);
    }

    /**
     * 根据对象属性ID删除
     * @param objAttrId 对象属性
     * @throws Exception 异常
     * @return 结果
     */
    public int deleteByObjAttribID(int objAttrId) throws Exception
    {
        SqlCondition cond =
            new SqlCondition("OBJATTR_ID", Integer.toString(objAttrId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
    }

    /**
     * 更新对象属性关联的告警
     * @param alarmId 告警
     * @param objattrIds 对象属性
     * @throws Exception 异常
     */
    @Transactional
    public void insert(int alarmId, ArrayList<String> objattrIds) throws Exception
    {
        if (objattrIds != null || objattrIds.size() > 0)
        {
            for (String objattrId : objattrIds)
            {
                this.deleteByObjAttribID(Integer.valueOf(objattrId));
                this.insert(new AttribAlarmEntity(Integer.valueOf(objattrId), alarmId));
            }
        }
    }

    /**
     * 删除后插入
     * @param objAttrId 对象属性
     * @param alarmId 告警
     * @throws Exception 异常
     */
    @Transactional
    public void updateOrInsert(int objAttrId, int alarmId) throws Exception
    {
        this.deleteByObjAttribID(objAttrId);
        this.insert(new AttribAlarmEntity(objAttrId, alarmId));
    }

    public String getObjAttrAlarms(String objAttrIdS) throws Exception
    {
        String sql = "SELECT DISTINCT ALARM_ID FROM BMP_ATTRIBALARM WHERE OBJATTR_ID IN (" + objAttrIdS + ")";
        final StringBuilder sb = new StringBuilder();
        DefaultDal.read(sql, new IReadHandle()
        {

            @Override
            public void handle(ResultSet rs) throws Exception
            {
                while (rs.next())
                {
                    sb.append(rs.getString("ALARM_ID")).append(",");
                }
                if (sb.length() > 0)
                {
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
        });
        return sb.toString();
    }
}
