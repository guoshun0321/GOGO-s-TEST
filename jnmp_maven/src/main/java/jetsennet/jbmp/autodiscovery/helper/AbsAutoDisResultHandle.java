/************************************************************************
日 期：2011-12-02
作 者: 郭祥
版 本：v1.3
描 述: 自动发现结果处理
历 史：
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jbmp.autodiscovery.AutoDisLogUtil;
import jetsennet.jbmp.autodiscovery.AutoDisUtil;
import jetsennet.jbmp.entity.AutoDisObjEntity;

/**
 * 自动发现结果处理
 * @author 郭祥
 */
public abstract class AbsAutoDisResultHandle
{

    /**
     * 采集任务ID
     */
    protected int taskId;
    /**
     * 采集器ID
     */
    protected int collId;
    /**
     * 自动实例化插件
     */
    private DisAutoIns ins;
    /**
     * 日志插件
     */
    protected AutoDisLogUtil log;

    /**
     * @param taskId 任务ID
     * @param collId 采集器ID
     */
    public AbsAutoDisResultHandle(int taskId, int collId)
    {
        this.taskId = taskId;
        this.collId = collId;
        log = new AutoDisLogUtil();
    }

    /**
     * @param result 参数
     * @param userId 用户ID
     */
    public abstract void handle(AutoDisResult result, int userId);

    /**
     * @param userName 用户名称
     * @param userId 用户ID
     */
    public void activeLog(int userId, String userName)
    {
        log.active(taskId, userId, userName);
    }

    protected void ins(List<AutoDisObjEntity> retval, int userId)
    {
        // 实例化类为空时，补实例化
        if (ins == null)
        {
            return;
        }
        ins.ins(retval, collId, userId);
    }

    /**
     * @param ins the ins to set
     */
    public void setIns(DisAutoIns ins)
    {
        this.ins = ins;
    }

    /**
     * 比较数据库表里面的数据和新采集到的数据
     * @param oldObjs 参数
     * @param newObjs 参数
     * @return 结果
     */
    public List<AutoDisObjEntity> compare(List<AutoDisObjEntity> oldObjs, List<AutoDisObjEntity> newObjs)
    {
        // 返回数据
        List<AutoDisObjEntity> retval = new ArrayList<AutoDisObjEntity>();

        // 保证新数据集合不为空
        if (newObjs == null)
        {
            newObjs = new ArrayList<AutoDisObjEntity>();
        }
        // 旧数据
        Map<String, AutoDisObjEntity> oldMap = AutoDisUtil.listToMap(oldObjs);

        for (int i = 0; i < newObjs.size(); i++)
        {
            // 新数据
            AutoDisObjEntity newObj = newObjs.get(i);
            String key = newObj.getIp();
            // 旧数据
            AutoDisObjEntity oldObj = oldMap.get(key);
            if (oldObj == null)
            {
                // 旧数据不存在，插入新数据
                log.logNew(newObj.getIp(), newObj.getClassId());
                newObj.setOpStatus(AutoDisObjEntity.OP_STATUS_NEW);
                retval.add(newObj);
            }
            else
            {
                // 旧数据存在
                int oldStatus = oldObj.getObjStatus();
                // 新旧数据类型相同，保持旧数据
                if (oldObj.getClassId() == newObj.getClassId())
                {
                    // 修改旧数据状态
                    if (oldStatus == AutoDisObjEntity.STATUS_NEW || oldStatus == AutoDisObjEntity.STATUS_USABLE
                        || oldStatus == AutoDisObjEntity.STATUS_UPDATE)
                    {
                        log
                            .logUpdate(newObj.getIp(), oldObj.getClassId(), newObj.getClassId(), oldObj.getObjStatus(),
                                AutoDisObjEntity.STATUS_USABLE);
                        oldObj.setOpStatus(AutoDisObjEntity.OP_STATUS_UPD1);
                        oldObj.setObjStatus(AutoDisObjEntity.STATUS_USABLE);
                    }
                    else
                    {
                        log.logUpdate(newObj.getIp(), oldObj.getClassId(), newObj.getClassId(), oldObj.getObjStatus(), AutoDisObjEntity.STATUS_NEW);
                        oldObj.setOpStatus(AutoDisObjEntity.OP_STATUS_UPD2);
                        oldObj.setObjStatus(AutoDisObjEntity.STATUS_NEW);
                    }
                    retval.add(oldObj);
                }
                else
                {
                    // 新旧数据类型不同，新数据ID置为就数据ID，并修改状态，插入新数据
                    if (oldStatus == AutoDisObjEntity.STATUS_NEW || oldStatus == AutoDisObjEntity.STATUS_USABLE
                        || oldStatus == AutoDisObjEntity.STATUS_UPDATE)
                    {
                        log
                            .logUpdate(newObj.getIp(), oldObj.getClassId(), newObj.getClassId(), oldObj.getObjStatus(),
                                AutoDisObjEntity.STATUS_UPDATE);
                        newObj.setObjId(oldObj.getObjId());
                        newObj.setOpStatus(AutoDisObjEntity.OP_STATUS_UPD3);
                        newObj.setObjStatus(AutoDisObjEntity.STATUS_UPDATE);
                    }
                    else
                    {
                        log.logUpdate(newObj.getIp(), oldObj.getClassId(), newObj.getClassId(), oldObj.getObjStatus(), AutoDisObjEntity.STATUS_NEW);
                        newObj.setObjId(oldObj.getObjId());
                        newObj.setOpStatus(AutoDisObjEntity.OP_STATUS_UPD4);
                        newObj.setObjStatus(AutoDisObjEntity.STATUS_NEW);
                    }
                    retval.add(newObj);
                }
                // 从旧数据中移除已经比较的数据
                oldMap.remove(key);
            }
        }
        // 旧数据存在，新数据不存在
        if (!oldMap.isEmpty())
        {
            Set<String> oldKeys = oldMap.keySet();
            for (String oldKey : oldKeys)
            {
                AutoDisObjEntity oldObj = oldMap.get(oldKey);
                if (oldObj.getObjStatus() == AutoDisObjEntity.STATUS_DELETE)
                {
                    oldObj.setObjStatus(AutoDisObjEntity.STATUS_UNUSABLE);
                }
                else
                {
                    oldObj.setObjStatus(AutoDisObjEntity.STATUS_DELETE);
                }
                log.logDelete(oldObj.getIp(), oldObj.getClassId());
                oldObj.setOpStatus(AutoDisObjEntity.OP_STATUS_DEL);
                retval.add(oldObj);
            }
        }
        return retval;
    }
}
