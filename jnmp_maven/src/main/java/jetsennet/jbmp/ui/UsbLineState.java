package jetsennet.jbmp.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import jetsennet.jbmp.datacollect.datasource.BFSSCollector;
import jetsennet.jbmp.datacollect.datasource.DataAgentManager;
import jetsennet.jbmp.util.ConfigUtil;

public class UsbLineState
{

    private CollMgrSCFrm frm;
    /**
     * 状态轮询
     */
    private Timer timer;
    /**
     * 是否使用USB方式采集
     */
    private boolean isUsb;
    /**
     * 轮询间隔
     */
    private static final long SPAN = 1 * 1000;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(UsbLineState.class);

    public UsbLineState(CollMgrSCFrm frm)
    {
        if (frm == null)
        {
            throw new NullPointerException();
        }
        this.frm = frm;

        String collType = ConfigUtil.getString("collect.type", DataAgentManager.COLL_TYPE_STR);
        if (!collType.equals(DataAgentManager.COLL_TYPE_STR))
        {
            this.isUsb = true;
        }
        else
        {
            this.isUsb = false;
        }
    }

    public synchronized void start()
    {
        try
        {
            if (isUsb)
            {
                frm.setUsbState("USB采集");
                timer = new Timer("TIMER-USB-STATE");
                timer.scheduleAtFixedRate(new PollingUsbStateTask(), SPAN, SPAN);
            }
            else
            {
                frm.setUsbState("直接采集");
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public synchronized void stop()
    {
        try
        {
            if (isUsb)
            {
                timer.cancel();
                frm.setUsbState("停止USB采集");
            }
            else
            {
                frm.setUsbState("停止采集");
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            timer = null;
        }
    }

    private class PollingUsbStateTask extends TimerTask
    {
        @Override
        public void run()
        {
            if (isUsb)
            {
                String str = BFSSCollector.getInstance().getState();
                frm.setUsbState(str);
            }

        }
    }

}
