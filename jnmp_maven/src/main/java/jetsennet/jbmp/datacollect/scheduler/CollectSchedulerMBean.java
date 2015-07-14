package jetsennet.jbmp.datacollect.scheduler;

import jetsennet.jbmp.manage.ILifecycleMBean;

public interface CollectSchedulerMBean extends ILifecycleMBean
{

    public int getTaskNum();

    public int getPoolSize();

    public int getCorePoolSize();

    public int getMaxPoolSize();

    public int getActivePoolSize();

    public int getQueueSize();

}
