package jetsennet.jbmp.protocols.tts;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.protocols.jgroup.AbsGroupServer;
import jetsennet.util.StringUtil;

public class AlarmBroadcast extends AbsGroupServer
{

    /**
     * 格式整理
     */
    private IAlarmBroadCastFormat format;
    /**
     * 缓存
     */
    private LinkedBlockingQueue<AlarmEventEntity> eventBuffer;
    private ExecutorService serv;
    private Future<Integer> future;
    private ReadCallable readCall;
    /**
     * 是否停止处理数据
     */
    private AtomicBoolean stop = new AtomicBoolean(false);
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AlarmBroadcast.class);
    // 单例
    private static AlarmBroadcast instance = new AlarmBroadcast();

    private AlarmBroadcast()
    {
        eventBuffer = new LinkedBlockingQueue<AlarmEventEntity>();
        format = new AlarmBroadcastFormatDef();
    }

    public static AlarmBroadcast getInstance()
    {
        return instance;
    }

    @Override
    public void start(String fileName) throws Exception
    {
        try
        {
            super.start(fileName);
            stop.set(false);
            eventBuffer.clear();
            serv = Executors.newSingleThreadExecutor();
            readCall = new ReadCallable();
            future = serv.submit(new ReadCallable());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    @Override
    public void stop()
    {
        if (readCall != null)
        {
            stop.set(true);
            eventBuffer.add(genEmpty());
        }
        try
        {
            if (future != null)
            {
                future.get();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            future = null;
        }
        try
        {
            if (serv != null)
            {
                serv.shutdownNow();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            serv = null;
        }
        // TODO Auto-generated method stub
        super.stop();
        eventBuffer.clear();
    }

    private AlarmEventEntity genEmpty()
    {
        AlarmEventEntity event = new AlarmEventEntity();
        event.setAlarmEvtId(-1);
        return event;
    }

    @Override
    protected void handle(AlarmEventEntity alarm)
    {
        if (!stop.get())
        {
            eventBuffer.add(alarm);
        }
    }

    private class ReadCallable implements Callable<Integer>
    {

        @Override
        public Integer call() throws Exception
        {
            while (!stop.get())
            {
                AlarmEventEntity alarm = eventBuffer.take();
                if (alarm != null && alarm.getAlarmEvtId() > 0)
                {
                    String formatTxt = format.format(alarm);
                    // 朗读文本
                    if (!StringUtil.isNullOrEmpty(formatTxt))
                    {
                        TTSMethod tts = new TTSMethod();
                        tts.speak(formatTxt);
                    }
                }
            }
            return 1;
        }
    }

}
