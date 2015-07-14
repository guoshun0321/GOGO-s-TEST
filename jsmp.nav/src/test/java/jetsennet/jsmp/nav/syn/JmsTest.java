package jetsennet.jsmp.nav.syn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import jetsennet.jsmp.nav.config.Config;
import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsTest extends TestCase
{

	private static final String TEST_QUEUE = "NAV.TEST";

	private static final List<String> retLst = Collections.synchronizedList(new ArrayList<String>(10));

	private static CountDownLatch latch = new CountDownLatch(2);
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(JmsTest.class);

	public void testJms() throws Exception
	{
		for (int i = 0; i < 5; i++)
		{
			this.send();
			this.rec();
			latch.await();
			System.out.println(retLst);
			assertEquals(4, retLst.size());
			assertEquals("message one", retLst.get(0));
			assertEquals("message two", retLst.get(1));
			assertEquals("message three", retLst.get(2));
			assertEquals("end", retLst.get(3));
		}
	}

	private void send()
	{
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Connection conn = null;
				Session session = null;
				try
				{
					ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PWD, Config.MQ_SERVERS);
					conn = fac.createConnection();
					conn.start();

					Queue queue = new ActiveMQQueue(TEST_QUEUE);
					session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

					MessageProducer prod = session.createProducer(queue);
					prod.setDeliveryMode(DeliveryMode.PERSISTENT);

					prod.send(session.createTextMessage("message one"));
					prod.send(session.createTextMessage("message two"));
					prod.send(session.createTextMessage("message three"));
					prod.send(session.createTextMessage("end"));
				}
				catch (Exception ex)
				{
				}
				finally
				{
					try
					{
						session.close();
						conn.close();
					}
					catch (Exception ex)
					{
						logger.error("", ex);
					}
					latch.countDown();
				}
			}
		});
		t.start();
	}

	public void rec()
	{
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Connection conn = null;
				Session session = null;
				try
				{
					ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PWD, Config.MQ_SERVERS);
					conn = fac.createConnection();
					conn.start();

					Queue queue = new ActiveMQQueue(TEST_QUEUE);
					session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);

					MessageConsumer consumer = session.createConsumer(queue);
					consumer.setMessageListener(new DataMessageListener());

					TimeUnit.SECONDS.sleep(2);
				}
				catch (Exception ex)
				{
				}
				finally
				{
					try
					{
						session.close();
						conn.close();
					}
					catch (Exception ex)
					{
						logger.error("", ex);
					}
					latch.countDown();
				}
			}
		});
		t.start();
	}

	private class DataMessageListener implements MessageListener
	{
		@Override
		public void onMessage(Message msg)
		{
			try
			{
				if (msg instanceof TextMessage)
				{
					String text = ((TextMessage) msg).getText();
					retLst.add(text);
				}
				else
				{
					logger.error("error msg : " + msg.toString());
				}
			}
			catch (Exception ex)
			{
				logger.error("", ex);
			}
			finally
			{
				try
				{
					msg.acknowledge();
				}
				catch (Exception ex)
				{
					logger.error("", ex);
				}
			}
		}
	}

}
