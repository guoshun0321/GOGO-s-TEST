/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AlarmActionEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author Guo
 */
public class AlarmActionDal extends DefaultDal<AlarmActionEntity>
{
    /**
     * 构造方法
     */
    public AlarmActionDal()
    {
        super(AlarmActionEntity.class);
    }

    /**
     * @param levelId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<AlarmActionEntity> getByLevelId(int levelId) throws Exception
    {
        SqlCondition cond = new SqlCondition("LEVEL_ID", Integer.toString(levelId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return (ArrayList<AlarmActionEntity>) getLst(cond);
    }

    /**
     * @param levelId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<Integer> getActionIdsByLevelId(int levelId) throws Exception
    {
        ArrayList<AlarmActionEntity> aaes = this.getByLevelId(levelId);
        ArrayList<Integer> retval = new ArrayList<Integer>();
        if (aaes != null && !aaes.isEmpty())
        {
            for (AlarmActionEntity aae : aaes)
            {
                retval.add(aae.getActionId());
            }
        }
        return retval;
    }

    /**
     * 更新报警级别对应的报警动作
     * 
     * @param levelId
     * @param actionStr
     * @throws Exception
     */
    @Transactional
    public void updateLevelAction(int levelId, String actionStr) throws Exception
    {
        if (actionStr == null)
        {
            actionStr = "";
        }
        String delStr = "DELETE FROM BMP_ALARMACTION WHERE LEVEL_ID = " + levelId;
        delete(delStr);
        String[] actionIds = actionStr.split(",");
        for (String actionId : actionIds)
        {
            actionId = actionId.trim();
            if (!actionId.equals(""))
            {
                AlarmActionEntity aa = new AlarmActionEntity();
                aa.setLevelId(levelId);
                aa.setActionId(Integer.valueOf(actionId));
                this.insert(aa);
            }
        }
    }

}
