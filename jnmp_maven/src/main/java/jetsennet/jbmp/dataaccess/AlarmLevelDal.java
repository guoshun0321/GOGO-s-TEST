/************************************************************************
日 期: 2011-12-30
作 者: 
版 本: v1.3
描 述: 操作报警级别表
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;

/**
 * 操作报警级别表
 * @author
 */
public class AlarmLevelDal extends DefaultDal<AlarmLevelEntity>
{

    /**
     * 构造方法
     */
    public AlarmLevelDal()
    {
        super(AlarmLevelEntity.class);
    }

    /**
     * @param id 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<AlarmLevelEntity> getByAlarmId(Integer id) throws Exception
    {
        String sql = String.format("SELECT * FROM %1s WHERE ALARM_ID = %2s ORDER BY ALARM_LEVEL DESC", tableInfo.tableName, id);
        return getLst(sql);
    }

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public String addAlarmLevel(String objXml) throws Exception
    {
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        int alarmLevelID = insert(map);

        // ACTION_IDS不为空表示要添加关联报警动作
        if (map.get("ACTION_IDS") != null && (!"".equals(map.get("ACTION_IDS"))))
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            String[] actionIds = map.get("ACTION_IDS").split(",");
            for (int i = 0; i < actionIds.length; i++)
            {
                SqlField[] param =
                    new SqlField[] { new SqlField("LEVEL_ID", alarmLevelID, SqlParamType.Numeric),
                        new SqlField("ACTION_ID", actionIds[i], SqlParamType.Numeric), };
                exec.executeNonQuery(exec.getSqlParser().getInsertCommandString("BMP_ALARMACTION", Arrays.asList(param)));
            }
        }

        return "" + alarmLevelID;
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Transactional
    public void updateAlarmLevel(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        String levelId = model.get("LEVEL_ID");

        update(model);
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.executeNonQuery(exec.getSqlParser().getDeleteCommandString("BMP_ALARMACTION",
            new SqlCondition("LEVEL_ID", levelId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric)));

        // ACTION_IDS不为空表示要添加关联报警动作
        if (model.get("ACTION_IDS") != null && (!"".equals(model.get("ACTION_IDS"))))
        {
            String[] actionIds = model.get("ACTION_IDS").split(",");
            for (int i = 0; i < actionIds.length; i++)
            {
                SqlField[] param =
                    new SqlField[] { new SqlField("LEVEL_ID", levelId, SqlParamType.Numeric),
                        new SqlField("ACTION_ID", actionIds[i], SqlParamType.Numeric), };
                exec.executeNonQuery(exec.getSqlParser().getInsertCommandString("BMP_ALARMACTION", Arrays.asList(param)));
            }
        }
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Transactional
    public void deleteAlarmLevel(int keyId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.executeNonQuery(exec.getSqlParser().getDeleteCommandString("BMP_ALARMACTION",
            new SqlCondition("LEVEL_ID", String.valueOf(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric)));
        delete(keyId);
    }
}
