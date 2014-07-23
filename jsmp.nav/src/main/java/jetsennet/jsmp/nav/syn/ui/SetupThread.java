package jetsennet.jsmp.nav.syn.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.monitor.Monitor;
import jetsennet.jsmp.nav.syn.DataSynchronizedJms;
import jetsennet.jsmp.nav.syn.DataSynchronizedFromDb;
import jetsennet.jsmp.nav.syn.DataSynchronizedTimer;
import jetsennet.jsmp.nav.util.ThreadWaitFutrue;

public class SetupThread
{

	private DataSynFrame frame;

	private volatile boolean isStart = false;

	private static final Logger logger = LoggerFactory.getLogger(SetupThread.class);

	public SetupThread(DataSynFrame frame)
	{
		this.frame = frame;
	}

	public synchronized void start()
	{
		if (!isStart)
		{
			try
			{
				// 同步数据库数据
				logger.info("从数据库同步数据开始！");
				DataSynchronizedFromDb.synFromDb();
				logger.info("从数据库同步数据结束！");

				// 启动JMS同步模块
				DataSynchronizedJms.getInstance().start();
				// 启动监控模块
				Monitor.getInstance().start();
				// 启动数据核对模块
				DataSynchronizedTimer.getInstance().start();

				frame.startSyn();
				this.isStart = true;
				logger.info("Setup模块启动成功！");
			}
			catch (Exception ex)
			{
				logger.error("Setup模块启动异常！", ex);
			}
		}
		else
		{
			logger.info("Setup模块重复启动！");
		}
	}

	public synchronized void stop()
	{
		if (isStart)
		{
			try
			{
				// 关闭数据核对模块
				DataSynchronizedTimer.getInstance().stop();
				// 关闭监控模块
				Monitor.getInstance().stop();
				// 关闭JMS同步模块
				DataSynchronizedJms.getInstance().stop();

				frame.stopSyn();
				this.isStart = false;
				logger.info("Setup模块关闭成功！");
			}
			catch (Exception ex)
			{
				logger.error("Setup模块关闭异常！", ex);
			}
		}
		else
		{
			logger.info("Setup模块重复关闭！");
		}
	}
}
