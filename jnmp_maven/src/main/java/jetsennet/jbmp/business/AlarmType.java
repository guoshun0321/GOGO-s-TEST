package jetsennet.jbmp.business;

import java.util.HashMap;
import java.util.List;

import jetsennet.jbmp.dataaccess.AlarmTypeDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AlarmTypeEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;

/**
 * @author ？
 */
public class AlarmType
{

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addAlarmType(String objXml) throws Exception
    {
        AlarmTypeDal dal = new AlarmTypeDal();
        return dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAlarmType(String objXml) throws Exception
    {
        AlarmTypeDal dal = new AlarmTypeDal();
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        String typeId = map.get("TYPE_ID");

        // 更新报警类型
        dal.update(map);

        // 检测循环，如发现则抛出异常
        String parentId = map.get("PARENT_ID");
        if (parentId != null && (!"".equals(parentId)))
        {
            checkCircle(Integer.parseInt(parentId), Integer.parseInt(typeId));
        }
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteAlarmType(int keyId) throws Exception
    {
        AlarmTypeDal dal = new AlarmTypeDal();
        dal.delete(keyId);
    }

    private void checkCircle(int compareId, int typeId) throws Exception
    {
        DefaultDal<AlarmTypeEntity> atdal = new DefaultDal<AlarmTypeEntity>(AlarmTypeEntity.class);
        SqlCondition cond = new SqlCondition("PARENT_ID", Integer.toString(typeId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        List<AlarmTypeEntity> atLst = atdal.getLst(cond);
        for (AlarmTypeEntity at : atLst)
        {
            if (at.getTypeId() == compareId)
            {
                throw new Exception("不能创建循环的组关系!");
            }
            checkCircle(compareId, at.getTypeId());
        }
    }
}
