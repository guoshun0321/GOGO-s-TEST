/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: BMP_ALARM表的操作
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AlarmActionEntity;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.entity.AttribAlarmEntity;
import jetsennet.jbmp.util.TwoTuple;

import org.apache.log4j.Logger;

/**
 * 报警规则
 * @author GuoXiang
 */
public class AlarmDal extends DefaultDal<AlarmEntity>
{

    private static final Logger logger = Logger.getLogger(AlarmDal.class);

    /**
     * 构造方法
     */
    public AlarmDal()
    {
        super(AlarmEntity.class);
    }

    /**
     * 属性对应的报警，包含该报警的所有报警级别
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public HashMap<Integer, AlarmEntity> getAlarmCollection() throws Exception
    {
        HashMap<Integer, AlarmEntity> result = new HashMap<Integer, AlarmEntity>();

        final HashMap<Integer, AlarmEntity> alarmMap = new HashMap<Integer, AlarmEntity>();
        String sql =
            "SELECT a.ALARM_NAME,a.ALARM_TYPE,a.ALARM_DESC,a.CHECK_SPAN,a.CHECK_NUM,a.OVER_NUM,a.CREATE_USER,a.CREATE_TIME,a.FIELD_1,"
                + "b.* FROM BMP_ALARM a INNER JOIN BMP_ALARMLEVEL b ON a.ALARM_ID = b.ALARM_ID";
        DefaultDal.read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                while (rs.next())
                {
                    AlarmEntity ae = DefaultDal.genEntity(AlarmEntity.class, rs);
                    if (alarmMap.get(ae.getAlarmId()) == null)
                    {
                        alarmMap.put(ae.getAlarmId(), ae);
                    }
                    ae = alarmMap.get(ae.getAlarmId());
                    AlarmLevelEntity ale = DefaultDal.genEntity(AlarmLevelEntity.class, rs);
                    ae.addLevel(ale);
                }
            }
        });

        AttribAlarmDal aadao = new AttribAlarmDal();
        ArrayList<AttribAlarmEntity> aas = (ArrayList<AttribAlarmEntity>) aadao.getAll();
        if (aas != null)
        {
            for (AttribAlarmEntity aa : aas)
            {
                int objAttribId = aa.getObjattrId();
                int alarmId = aa.getAlarmId();
                AlarmEntity entity = alarmMap.get(alarmId);
                if (entity != null)
                {
                    result.put(objAttribId, entity);
                }
            }
        }
        return result;
    }

    /**
     * 查找所有的报警，每个报警对应的报警级别以及报警级别和对象属性对应关系
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public TwoTuple<ArrayList<AlarmEntity>, ArrayList<AttribAlarmEntity>> getAllAlarms() throws Exception
    {
        final ArrayList<AlarmEntity> alarms = new ArrayList<AlarmEntity>();
        final HashMap<Integer, AlarmEntity> map = new HashMap<Integer, AlarmEntity>();

        // 这里不取BMP_ALARM表中的ALARM_ID字段，是因为在BMP_ALARMLEVEL中存在同名字段，为了在下面组装实体时方便，不需要区分两个ALARM_ID。
        String sql =
            "SELECT a.ALARM_NAME,a.ALARM_TYPE,a.ALARM_DESC,a.CHECK_SPAN,a.CHECK_NUM,a.OVER_NUM,a.CREATE_USER,a.CREATE_TIME,a.FIELD_1,"
                + "b.* FROM BMP_ALARM a INNER JOIN BMP_ALARMLEVEL b ON a.ALARM_ID = b.ALARM_ID WHERE a.IS_VALID = 0";
        DefaultDal.read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                while (rs.next())
                {
                    int alarmId = rs.getInt("ALARM_ID");
                    AlarmEntity ae = map.get(alarmId);
                    if (ae == null)
                    {
                        ae = DefaultDal.genEntity(AlarmEntity.class, rs);
                        map.put(ae.getAlarmId(), ae);
                        alarms.add(ae);
                    }
                    AlarmLevelEntity ale = DefaultDal.genEntity(AlarmLevelEntity.class, rs);
                    ae.addLevel(ale);
                }
            }
        });

        AttribAlarmDal aadao = new AttribAlarmDal();
        ArrayList<AttribAlarmEntity> aas = (ArrayList<AttribAlarmEntity>) aadao.getAll();
        return new TwoTuple<ArrayList<AlarmEntity>, ArrayList<AttribAlarmEntity>>(alarms, aas);
    }

    /**
     * 根据对象属性id获取报警，以及该报警下的报警级别
     * 
     * @param objAttribId 对象属性id
     * @return 报警规则
     * @throws Exception
     */
    @Transactional
    public AlarmEntity getByObjAttribId(int objAttribId) throws Exception
    {
        String sql = "SELECT B.* FROM BMP_ATTRIBALARM a LEFT JOIN BMP_ALARM b ON a.ALARM_ID = b.ALARM_ID WHERE a.OBJATTR_ID = " + objAttribId;
        AlarmEntity retval = this.get(sql);
        if (retval != null)
        {
            AlarmLevelDal aldal = new AlarmLevelDal();
            List<AlarmLevelEntity> levels = aldal.getByAlarmId(retval.getAlarmId());
            retval.setLevels(levels);
        }
        return retval;
    }

    /**
     * 根据属性id获取报警，以及该报警下的报警级别
     * 
     * @param attribId 属性ID
     * @return 报警规则
     * @throws Exception
     */
    @Transactional
    public AlarmEntity getByAttribId(int attribId) throws Exception
    {
        String sql = "SELECT * FROM BMP_ALARM WHERE ALARM_ID IN (SELECT ALARM_ID FROM BMP_ATTRIBUTE WHERE ATTRIB_ID = " + attribId + ")";
        AlarmEntity retval = this.get(sql);
        if (retval != null)
        {
            AlarmLevelDal aldal = new AlarmLevelDal();
            List<AlarmLevelEntity> levels = aldal.getByAlarmId(retval.getAlarmId());
            retval.setLevels(levels);
        }
        return retval;
    }

    /**
     * 批量复制属性对应的报警规则，并插入数据库。
     * @param attrIds 属性id
     * @param oldAlarmId 要进行复制操作的报警规则id
     * @return 新增报警规则的id
     * @throws Exception
     */
    @Transactional
    public String batchCopyAlarmFromAttribute(String attrIds, int oldAlarmId) throws Exception
    {
        String[] ids = attrIds.split(",");
        StringBuilder newAlarmIds = new StringBuilder();
        String sql = "SELECT * FROM BMP_ALARM WHERE ALARM_ID = '" + oldAlarmId + "'";
        AlarmEntity alarm = this.get(sql);
        for (int i = 0; i < ids.length; i++)
        {
            int alarmId = this.insert(alarm);
            AlarmLevelDal aldal = new AlarmLevelDal();
            AlarmActionDal aadal = new AlarmActionDal();
            List<AlarmLevelEntity> levels = aldal.getByAlarmId(oldAlarmId);
            if (levels != null)
            {
                for (AlarmLevelEntity level : levels)
                {
                    level.setAlarmId(alarmId);
                    int oldLevelId = level.getLevelId();
                    int levelId = aldal.insert(level);
                    ArrayList<Integer> actionIds = aadal.getActionIdsByLevelId(oldLevelId);
                    if (actionIds != null)
                    {
                        for (Integer actionId : actionIds)
                        {
                            AlarmActionEntity aa = new AlarmActionEntity();
                            aa.setLevelId(levelId);
                            aa.setActionId(actionId);
                            aadal.insert(aa);
                        }
                    }
                }
            }

            //更新属性所关联的报警id
            String updateSql = "UPDATE BMP_ATTRIBUTE SET ALARM_ID = '" + alarmId + "' WHERE ATTRIB_ID = '" + ids[i] + "'";
            SqlExecutorFacotry.getSqlExecutor().executeNonQuery(updateSql);
            //保存新生成的报警id
            newAlarmIds.append(alarmId).append(",");
        }
        return newAlarmIds.toString().substring(0, newAlarmIds.length() - 1);
    }

    /**
     * 批量复制指标对应的报警规则,并插入数据库。
     * @param objattrIds 指标id
     * @param oldAlarmId 要进行复制操作的报警规则id
     * @return 新增报警规则的id
     * @throws Exception
     */
    @Transactional
    public String batchCopyAlarmFromObjattr(String objattrIds, int oldAlarmId) throws Exception
    {
        String[] ids = objattrIds.split(",");
        StringBuilder newAlarmIds = new StringBuilder();
        String sql = "SELECT * FROM BMP_ALARM WHERE ALARM_ID = '" + oldAlarmId + "'";
        AlarmEntity alarm = this.get(sql);
        for (int i = 0; i < ids.length; i++)
        {
            int alarmId = this.insert(alarm);
            AlarmLevelDal aldal = new AlarmLevelDal();
            AlarmActionDal aadal = new AlarmActionDal();
            List<AlarmLevelEntity> levels = aldal.getByAlarmId(oldAlarmId);
            if (levels != null)
            {
                for (AlarmLevelEntity level : levels)
                {
                    level.setAlarmId(alarmId);
                    int oldLevelId = level.getLevelId();
                    int levelId = aldal.insert(level);
                    ArrayList<Integer> actionIds = aadal.getActionIdsByLevelId(oldLevelId);
                    if (actionIds != null)
                    {
                        for (Integer actionId : actionIds)
                        {
                            AlarmActionEntity aa = new AlarmActionEntity();
                            aa.setLevelId(levelId);
                            aa.setActionId(actionId);
                            aadal.insert(aa);
                        }
                    }
                }
            }
            //更新指标所关联的报警id
            String updateSql = "UPDATE BMP_ATTRIBALARM SET ALARM_ID = '" + alarmId + "' WHERE OBJATTR_ID = '" + ids[i] + "'";
            SqlExecutorFacotry.getSqlExecutor().executeNonQuery(updateSql);
            //保存新生成的报警id
            newAlarmIds.append(alarmId).append(",");
        }
        return newAlarmIds.toString().substring(0, newAlarmIds.length() - 1);
    }

    /**
     * 复制attribId对应的报警规则（如果不存在则新建一个不生效的报警规则），并插入数据库。
     * 
     * @param attribId
     */
    @Transactional
    public int copyAlarm(int attribId) throws Exception
    {
        int alarmId = -1;
        String sql = "SELECT * FROM BMP_ALARM WHERE ALARM_ID IN (SELECT ALARM_ID FROM BMP_ATTRIBUTE WHERE ATTRIB_ID = " + attribId + ")";
        AlarmEntity alarm = this.get(sql);
        if (alarm == null)
        {
            alarm = AlarmEntity.newUnValidAlarm("未命名规则");
            alarmId = this.insert(alarm);
        }
        else if (alarm.getAlarmId() <= 0)
        {
            // 处理alarmId为0的情况，这可能是由于历史数据导致的
            alarm = AlarmEntity.newUnValidAlarm("未命名规则");
            alarmId = this.insert(alarm);
        }
        else
        {
            int oldAlarmId = alarm.getAlarmId();
            alarmId = this.insert(alarm);
            AlarmLevelDal aldal = new AlarmLevelDal();
            AlarmActionDal aadal = new AlarmActionDal();
            List<AlarmLevelEntity> levels = aldal.getByAlarmId(oldAlarmId);
            if (levels != null)
            {
                for (AlarmLevelEntity level : levels)
                {
                    level.setAlarmId(alarmId);
                    int oldLevelId = level.getLevelId();
                    int levelId = aldal.insert(level);
                    ArrayList<Integer> actionIds = aadal.getActionIdsByLevelId(oldLevelId);
                    if (actionIds != null)
                    {
                        for (Integer actionId : actionIds)
                        {
                            AlarmActionEntity aa = new AlarmActionEntity();
                            aa.setLevelId(levelId);
                            aa.setActionId(actionId);
                            aadal.insert(aa);
                        }
                    }
                }
            }
        }
        return alarmId;
    }

    /**
     * 插入报警规则，同时插入规则级别
     * 
     * @return 报警规则ID
     */
    @Transactional
    public int insertAlarm(AlarmEntity alarm) throws Exception
    {
        int retval = this.insert(alarm);
        List<AlarmLevelEntity> levels = alarm.getLevels();
        if (levels != null && !levels.isEmpty())
        {
            AlarmLevelDal aldal = new AlarmLevelDal();
            for (AlarmLevelEntity level : levels)
            {
                level.setAlarmId(retval);
                aldal.insert(level);
            }
        }
        return retval;
    }

    /**
     * 获取报警
     */
    public TwoTuple<Map<Integer, AlarmEntity>, Map<Integer, List<Integer>>> getAllAlarmInfo() throws Exception
    {
        final ArrayList<AlarmEntity> alarms = new ArrayList<AlarmEntity>();
        final Map<Integer, AlarmEntity> map = new HashMap<Integer, AlarmEntity>();

        // 这里不取BMP_ALARM表中的ALARM_ID字段，是因为在BMP_ALARMLEVEL中存在同名字段，为了在下面组装实体时方便，不需要区分两个ALARM_ID。
        String sql =
            "SELECT a.ALARM_NAME,a.ALARM_TYPE,a.ALARM_DESC,a.CHECK_SPAN,a.CHECK_NUM,a.OVER_NUM,a.CREATE_USER,a.CREATE_TIME,a.FIELD_1,"
                + "b.* FROM BMP_ALARM a INNER JOIN BMP_ALARMLEVEL b ON a.ALARM_ID = b.ALARM_ID";
        DefaultDal.read(sql, new IReadHandle()
        {
            @Override
            public void handle(ResultSet rs) throws Exception
            {
                while (rs.next())
                {
                    int alarmId = rs.getInt("ALARM_ID");
                    AlarmEntity ae = map.get(alarmId);
                    if (ae == null)
                    {
                        ae = DefaultDal.genEntity(AlarmEntity.class, rs);
                        map.put(ae.getAlarmId(), ae);
                        alarms.add(ae);
                    }
                    AlarmLevelEntity ale = DefaultDal.genEntity(AlarmLevelEntity.class, rs);
                    ae.addLevel(ale);
                }
            }
        });

        Map<Integer, List<Integer>> actionMap = new HashMap<Integer, List<Integer>>();
        AlarmActionDal aadao = new AlarmActionDal();
        List<AlarmActionEntity> aas = aadao.getAll();

        for (AlarmActionEntity aa : aas)
        {
            List<Integer> actions = actionMap.get(aa.getLevelId());
            if (actions == null)
            {
                actions = new ArrayList<Integer>();
                actionMap.put(aa.getLevelId(), actions);
            }
            actions.add(aa.getActionId());
        }

        return new TwoTuple<Map<Integer, AlarmEntity>, Map<Integer, List<Integer>>>(map, actionMap);
    }
}
