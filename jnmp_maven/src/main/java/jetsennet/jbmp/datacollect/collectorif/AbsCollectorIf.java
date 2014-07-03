package jetsennet.jbmp.datacollect.collectorif;

import jetsennet.jbmp.entity.MObjectEntity;

/**
 * 采集器
 * @author x.li
 */
public abstract class AbsCollectorIf implements ICollectorIf
{
    /**
     * 监控对象
     */
    protected MObjectEntity mo;

    @Override
    public void setMonitorObject(MObjectEntity mo)
    {
        this.mo = mo;
    }
}
