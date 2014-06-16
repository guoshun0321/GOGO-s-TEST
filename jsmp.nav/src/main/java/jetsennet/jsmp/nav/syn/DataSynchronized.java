package jetsennet.jsmp.nav.syn;

import java.util.List;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.monitor.MonitorServlet;
import jetsennet.jsmp.nav.monitor.MsgHandleMMsg;
import jetsennet.jsmp.nav.syn.cache.DataSyn4Cache;
import jetsennet.jsmp.nav.syn.cache.IDataSynCache;
import jetsennet.jsmp.nav.syn.db.DataSynDb;
import jetsennet.jsmp.nav.syn.db.DataSynDbResult;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据同步，用于同步运维系统数据
 * 
 * @author 郭祥
 */
public class DataSynchronized
{

	/**
	 * 连接
	 */
	private Connection conn;
	/**
	 * 会话
	 */
	private Session session;
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(DataSynchronized.class);

	public void start()
	{
		try
		{
			ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PWD, Config.MQ_SERVERS);
			conn = fac.createConnection();
			conn.start();

			Queue queue = new ActiveMQQueue(Config.MQ_QUEUE);
			session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);

			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(new DataSynMessageListener());
			logger.info("启动同步线程成功！");
		}
		catch (Exception ex)
		{
			logger.error("启动同步线程失败！", ex);
		}
	}

	public void close()
	{
		try
		{
			if (session != null)
			{
				session.close();
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		finally
		{
			session = null;
		}
		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		finally
		{
			conn = null;
		}
		logger.info("关闭同步线程！");
	}

	private class DataSynMessageListener implements MessageListener
	{

		public DataSynMessageListener()
		{
		}

		@Override
		public void onMessage(Message msg)
		{
			if (msg instanceof TextMessage)
			{
				MsgHandleMMsg mmsg = new MsgHandleMMsg();
				mmsg.setStartTime(System.currentTimeMillis());
				String xml = null;
				try
				{
					xml = ((TextMessage) msg).getText();
					DataSynEntity synData = DataSynXmlParse.parseXml(xml);
					DataHandleUtil.handleData(synData, xml);
					mmsg.setEndTime(System.currentTimeMillis());
				}
				catch (Exception ex)
				{
					mmsg.setEndTime(System.currentTimeMillis());
					mmsg.setException(true);
					mmsg.setXml(xml);
					logger.error(ex.getMessage() + "，错误的XML结构：" + xml, ex);
				}
				finally
				{
					acknowledge(msg);
					MonitorServlet.put(mmsg);
				}
			}
			else
			{
				logger.error("丢弃非文本消息：" + msg);
				acknowledge(msg);
			}
		}

		private void acknowledge(Message msg)
		{
			try
			{
				logger.error("错误消息：" + msg);
				msg.acknowledge();
			}
			catch (Exception ex)
			{
				logger.error("", ex);
			}
		}
	}
}
