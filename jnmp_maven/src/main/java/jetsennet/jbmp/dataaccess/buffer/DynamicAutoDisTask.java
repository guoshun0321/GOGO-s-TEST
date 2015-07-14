/************************************************************************
日 期：2012-3-13
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess.buffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AutoDisTaskDal;
import jetsennet.jbmp.dataaccess.base.AbsDynamicDBBuffer;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jbmp.util.StringUtil;

/**
 * @author 郭祥
 */
public final class DynamicAutoDisTask extends AbsDynamicDBBuffer<AutoDisTaskEntity>
{

    private AutoDisTaskDal adtdal;
    private static final Logger logger = Logger.getLogger(DynamicAutoDisTask.class);
    private static DynamicAutoDisTask instance = new DynamicAutoDisTask();

    private DynamicAutoDisTask()
    {
        super(AutoDisTaskEntity.class);
        adtdal = ClassWrapper.wrapTrans(AutoDisTaskDal.class);
        timerName = "dynamib_db_timer_autodistask";
    }

    public static DynamicAutoDisTask getInstance()
    {
        return instance;
    }

    @Override
    protected boolean compare(AutoDisTaskEntity oldObj, AutoDisTaskEntity newObj)
    {
        if (oldObj.getCollId() == newObj.getCollId() && oldObj.getTaskType() == newObj.getTaskType()
            && StringUtil.stringCompare(oldObj.getBeginIp(), newObj.getBeginIp()) && StringUtil.stringCompare(oldObj.getEndIp(), newObj.getEndIp())
            && StringUtil.stringCompare(oldObj.getAddInfo(), newObj.getAddInfo())
            && StringUtil.stringCompare(oldObj.getCommunity(), newObj.getCommunity()) && oldObj.getExeType() == newObj.getExeType()
            && StringUtil.stringCompare(oldObj.getTimePoint(), newObj.getTimePoint())
            && StringUtil.stringCompare(oldObj.getWeekMask(), newObj.getWeekMask()))
        {
            return true;
        }
        return false;
    }

    @Override
    protected Map<Integer, AutoDisTaskEntity> getNewDatas()
    {
        Map<Integer, AutoDisTaskEntity> retval = new HashMap<Integer, AutoDisTaskEntity>();
        try
        {
            List<AutoDisTaskEntity> tasks = adtdal.getAll();
            for (AutoDisTaskEntity task : tasks)
            {
                retval.put(task.getTaskId(), task);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        DynamicAutoDisTask dy = DynamicAutoDisTask.getInstance();
        dy.start();
    }
}
