package jetsennet.jsmp.nav.syn.ui;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.media.db.SynFromDb;
import jetsennet.jsmp.nav.media.syn.DataSynJms;
import jetsennet.jsmp.nav.media.syn.DataSynPeriod;
import jetsennet.jsmp.nav.monitor.Monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupThread
{

	private DataSynFrame frame;

	private boolean isStart = false;

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
				logger.info("Setup模块准备启动！");

				// 启动监控模块
				Monitor.getInstance().start();

				// 同步数据库数据
				logger.info("开始从数据库同步数据！");
				DataCacheOp.getInstance().deleteAll();
				new SynFromDb().syn();
				logger.info("从数据库同步数据结束！");

				// 启动JMS同步模块
				DataSynJms.getInstance().start();

				// 启动周期性数据核对模块
				DataSynPeriod.getInstance().start();

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
				// 关闭周期性数据核对模块
				DataSynPeriod.getInstance().stop();

				// 关闭JMS同步模块
				DataSynJms.getInstance().stop();

				// 关闭监控模块
				Monitor.getInstance().stop();

				this.isStart = false;
				frame.stopSyn();
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
