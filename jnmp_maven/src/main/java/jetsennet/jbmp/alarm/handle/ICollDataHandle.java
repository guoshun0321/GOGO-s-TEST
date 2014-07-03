/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 数据处理接口
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.alarm.bus.CollData;

/**
 * 数据处理接口。处理采集到的数据。
 * @author 郭祥
 */
public interface ICollDataHandle
{

    /**
     * 采集数据处理
     * @param icd 参数
     * @param config 参数
     */
    public void handle(CollData icd, AlarmConfig config);
}
