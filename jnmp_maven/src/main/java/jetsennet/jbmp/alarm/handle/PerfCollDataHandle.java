/************************************************************************
日 期：2011-12-01
作 者: 郭祥
版 本：v1.3
描 述: 性能数据处理
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.ArrayList;
import java.util.Map;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.rule.AbsHistoryData;
import jetsennet.jbmp.alarm.rule.DataHandleDef;
import jetsennet.jbmp.alarm.rule.HistoryDataBuffer;
import jetsennet.jbmp.dataaccess.rrd.RrdHelper;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.MObjectEntity;

import org.apache.log4j.Logger;

/**
 * 性能数据处理
 * @author 郭祥
 */
public class PerfCollDataHandle extends AbsCollDataHandle
{

    private DataHandleDef handle;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(PerfCollDataHandle.class);

    /**
     * 构造方法
     */
    public PerfCollDataHandle()
    {
        super();
        handle = DataHandleDef.getInstance();
    }

    @Override
    public boolean filter(CollData idata, int objId)
    {
        // 这里暂时没用到参数
        return true;
    }

    @Override
    public void save(CollData data, String coding) throws Exception
    {
        if (data == null)
        {
            throw new NullPointerException();
        }
        if (data.params == null || data.params.get(CollData.PARAMS_DATA) == null)
        {
            return;
        }
        Object obj = data.params.get(CollData.PARAMS_DATA);
        RrdHelper.getInstance().save(Integer.toString(data.objID), data.time, (Map<String, Double>) obj);
    }

    @Override
    public ArrayList<AlarmEventEntity> alarm(AlarmConfig config, CollData idata, int objId, String coding)
    {
        if (idata != null)
        {
            AbsHistoryData ad = HistoryDataBuffer.getInstance().getAlarmData(idata.objAttrID);
            if (ad != null && ad.getRule() != null)
            {
                // 确定对象
                MObjectEntity mo = mobjBuffer.get(objId);

                // 产生报警
                if (mo != null && mo.getObjState() == MObjectEntity.OBJ_STATE_MANAGEABLE)
                {
                    handle.handleData(idata.value, idata.time, config.getEventHandleClass(), ad, null, objId, idata.objAttrID, idata.attrID);
                }
            }
        }
        return null;
    }
}
