/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.ActionStateEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * @author Guo
 */
public class ActionStateDal extends DefaultDal<ActionStateEntity>
{
    private static final Logger logger = Logger.getLogger(ActionStateDal.class);

    /**
     * 构造方法
     */
    public ActionStateDal()
    {
        super(ActionStateEntity.class);
    }

    /**
     * @param ases 参数
     * @throws Exception 异常
     */
    public void insert(ArrayList<ActionStateEntity> ases) throws Exception
    {
        if (ases != null && !ases.isEmpty())
        {
            for (ActionStateEntity ase : ases)
            {
                this.insert(ase);
            }
        }
    }

    /**
     * @param aee 参数
     * @throws Exception 异常
     */
    public void insert(AlarmEventEntity aee) throws Exception
    {
        if (aee == null)
        {
            return;
        }
        ArrayList<Integer> actionIds = aee.getActionIds();
        if (actionIds == null || actionIds.isEmpty())
        {
            return;
        }
        int alarmEventId = aee.getAlarmEvtId();
        if (alarmEventId <= 0)
        {
            logger.warn("alarmEventId小于0。");
            return;
        }
        Date now = new Date();
        for (Integer actionId : actionIds)
        {
            ActionStateEntity ase = new ActionStateEntity(alarmEventId, actionId, now);
            this.insert(ase);
        }
    }
}
