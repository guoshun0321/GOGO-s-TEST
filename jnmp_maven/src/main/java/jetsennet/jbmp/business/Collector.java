package jetsennet.jbmp.business;

import java.util.HashMap;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.CollectTaskDal;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.ObjGroupDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.CollectTaskEntity;
import jetsennet.jbmp.entity.CollectorEntity;
import jetsennet.jbmp.entity.ObjGroupEntity;
import jetsennet.util.SerializerUtil;

/**
 * @author ？
 */
public class Collector
{
    private static final Logger logger = Logger.getLogger(Collector.class);

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addCollector(String objXml) throws Exception
    {
        DefaultDal<CollectorEntity> dal = new DefaultDal<CollectorEntity>(CollectorEntity.class);
        ObjGroupDal objGrpDal = new ObjGroupDal();
        CollectTaskDal taskDal = new CollectTaskDal();
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");

        // 创建采集器
        int collId = dal.insert(map);

        // 创建相应的采集组
        ObjGroupEntity objGrpEntity = new ObjGroupEntity();
        objGrpEntity.setGroupType(ObjGroupEntity.GROUP_TYPE_COLLECT);
        objGrpEntity.setGroupName(map.get("COLL_NAME") + "组");
        objGrpEntity.setGroupCode(map.get("IP_ADDR"));
        objGrpEntity.setGroupDesc("系统自动创建的采集组");
        objGrpEntity.setNumVal1(collId);
        int groupId = objGrpDal.insert(objGrpEntity);

        // 创建相应的采集任务
        CollectTaskEntity taskEntity = new CollectTaskEntity();
        taskEntity.setCollId(collId);
        taskEntity.setGroupId(groupId);
        taskEntity.setTaskType(CollectTaskEntity.TASK_TYPE_PERIOD);
        taskEntity.setWeekMask("1234567");
        taskEntity.setHourMask("01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24");
        taskEntity.setCreateUser("System");
        taskDal.insert(taskEntity);
        return collId;
    }

    /**
     * 新建或更新
     * @param entity 实体
     * @throws Exception 异常
     */
    @Business
    public void insertOrUpdate(CollectorEntity entity) throws Exception
    {
        DefaultDal<CollectorEntity> dal = new DefaultDal<CollectorEntity>(CollectorEntity.class);
        ObjGroupDal objGrpDal = new ObjGroupDal();
        CollectTaskDal taskDal = new CollectTaskDal();

        // 尝试更新
        int success = dal.update(entity);
        if (success > 0)
        {
            return;
        }

        // 创建采集器
        int collId = dal.insert(entity);
        entity.setCollId(collId);

        // 创建相应的采集组
        ObjGroupEntity objGrpEntity = new ObjGroupEntity();
        objGrpEntity.setGroupType(ObjGroupEntity.GROUP_TYPE_COLLECT);
        objGrpEntity.setGroupName(entity.getCollName() + "组");
        objGrpEntity.setGroupCode(entity.getIpAddr());
        objGrpEntity.setGroupDesc("系统自动创建的采集组");
        objGrpEntity.setNumVal1(collId);
        int groupId = objGrpDal.insert(objGrpEntity);

        // 创建相应的采集任务
        CollectTaskEntity taskEntity = new CollectTaskEntity();
        taskEntity.setCollId(collId);
        taskEntity.setGroupId(groupId);
        taskEntity.setTaskType(CollectTaskEntity.TASK_TYPE_PERIOD);
        taskEntity.setWeekMask("1234567");
        taskEntity.setHourMask("01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24");
        taskEntity.setCreateUser("System");
        taskDal.insert(taskEntity);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateCollector(String objXml) throws Exception
    {
        DefaultDal<CollectorEntity> dal = new DefaultDal<CollectorEntity>(CollectorEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteCollector(int keyId) throws Exception
    {
        DefaultDal<CollectorEntity> dal = new DefaultDal<CollectorEntity>(CollectorEntity.class);
        dal.delete(keyId);
    }
}
