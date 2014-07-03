package jetsennet.jbmp.manage;

import static jetsennet.jbmp.manage.LifecycleStateEnum.*;
import jetsennet.jbmp.util.UncheckedJbmpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LifecycleBase implements ILifecycle, ILifecycleMBean
{

    /**
     * 组件当前状态
     */
    private LifecycleStateEnum state;
    /**
     * 组件状态描述
     */
    private String stateDesc;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(LifecycleBase.class);

    public LifecycleBase()
    {
        this.state = NEW;
        this.stateDesc = null;
    }

    @Override
    public synchronized void start()
    {
        switch (state)
        {
        case NEW:
        case STOPPED:
            try
            {
                this.startInternal();
                this.state = STARTED;
            }
            catch (Exception ex)
            {
                throw new UncheckedJbmpException("组件启动失败", ex);
            }
            break;
        case STARTED:
            logger.error("组件已启动。");
            break;
        default:
            break;
        }
    }

    protected abstract void startInternal() throws Exception;

    @Override
    public synchronized void stop()
    {
        switch (state)
        {
        case NEW:
        case STOPPED:
            logger.error("组件处理不能停止的状态：" + state.name());
            break;
        case STARTED:
            try
            {
                this.stopInternal();
                this.state = STOPPED;
            }
            catch (Exception ex)
            {
                throw new UncheckedJbmpException("组件停止失败", ex);
            }
            break;
        default:
            break;
        }
    }

    protected abstract void stopInternal() throws Exception;

    protected synchronized boolean isRunning()
    {
        return this.state == STARTED ? true : false;
    }

    @Override
    public synchronized String getState()
    {
        return this.state.name();
    }

    @Override
    public synchronized String getStateDesc()
    {
        return this.stateDesc;
    }

}
