package jetsennet.jbmp.alarm;

import java.util.concurrent.TimeUnit;

import jetsennet.jbmp.datacollect.scheduler.CollectScheduler;

public class CollectotTest
{

    public static void main(String[] args) throws Exception
    {
        CollectScheduler coll = CollectScheduler.getInstance();
        coll.start();
        TimeUnit.SECONDS.sleep(100);
    }

}
