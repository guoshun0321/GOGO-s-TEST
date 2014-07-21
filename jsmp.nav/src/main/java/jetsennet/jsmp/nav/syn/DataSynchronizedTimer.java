package jetsennet.jsmp.nav.syn;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每天凌晨0点核对数据
 * 
 * @author 郭祥
 */
public class DataSynchronizedTimer
{

	private ScheduledExecutorService ses;

	private volatile boolean isStart = false;

	private static final long TIME_DAY = 24 * 60 * 60 * 1000;

	private static final long TIME_2HOUR = 2 * 60 * 60 * 1000;

	private static final Logger logger = LoggerFactory.getLogger(DataSynchronizedTimer.class);

	private static final DataSynchronizedTimer instance = new DataSynchronizedTimer();

	private DataSynchronizedTimer()
	{
	}

	public static DataSynchronizedTimer getInstance()
	{
		return instance;
	}

	public void start()
	{
		if (!isStart)
		{
			ses = Executors.newScheduledThreadPool(1);
			long startTime = getStartTime();
			logger.info("初次访问延迟：" + startTime / 1000 / 60 + "分钟");
			ses.scheduleAtFixedRate(new DataCheckRunnable(), startTime, TIME_DAY, TimeUnit.MILLISECONDS);
			logger.info("数据核对模块模块启动。");
			this.isStart = true;
		}
		else
		{
			logger.info("数据核对模块模块重复启动。");
		}
	}

	public void stop()
	{
		if (isStart)
		{
			ses.shutdownNow();
			logger.info("数据核对模块关闭。");
			this.isStart = false;
		}
		else
		{
			logger.info("数据核对模块重复关闭。");
		}
	}

	private long getStartTime()
	{
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long future = calendar.getTimeInMillis();
		long retval = future - now;
		return retval > TIME_2HOUR ? retval : retval + TIME_DAY;
	}

	private class DataCheckRunnable implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				DataSynchronizedJms.getInstance().stop();
				DataSynchronizedFromDb.synFromDb();
				DataSynchronizedJms.getInstance().start();
			}
			catch (Exception ex)
			{
				logger.error("", ex);
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		DataSynchronizedTimer timer = DataSynchronizedTimer.getInstance();
		timer.start();
		TimeUnit.SECONDS.sleep(3);
		timer.stop();
	}

}
