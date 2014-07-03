package jetsennet.jbmp.ins;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class SubObjInsPool
{

    /**
     * 线程池
     */
    private ExecutorService service;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SubObjInsPool.class);

    // 单例
    private static final SubObjInsPool instance = new SubObjInsPool();

    private SubObjInsPool()
    {
        this.service = Executors.newFixedThreadPool(5);
    }

    public static SubObjInsPool getInstance()
    {
        return instance;
    }

    public void submit(Runnable run)
    {
        if (run != null)
        {
            service.submit(run);
        }
    }
}
