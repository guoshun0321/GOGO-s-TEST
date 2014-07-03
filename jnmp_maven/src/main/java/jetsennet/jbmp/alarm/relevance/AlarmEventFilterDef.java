package jetsennet.jbmp.alarm.relevance;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.buffer.AlarmObjAttrRelBuffer;
import jetsennet.jbmp.entity.AlarmEventEntity;

public class AlarmEventFilterDef implements IAlarmEventFilter
{

    private AlarmEventDal aedal;

    private static final Logger logger = Logger.getLogger(AlarmEventFilterDef.class);

    public AlarmEventFilterDef()
    {
        aedal = ClassWrapper.wrapTrans(AlarmEventDal.class);
    }

    @Override
    public synchronized boolean filter(AlarmEventEntity event)
    {
        boolean retval = true;
        int objAttrId = event.getObjAttrId();
        String cond = AlarmObjAttrRelBuffer.getInstance().getByObjAttrId(objAttrId);
        if (cond != null)
        {
            try
            {
                int num = aedal.getRelAlarmNum(cond);
                retval = num > 0 ? false : true;
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        return retval;
    }

}
