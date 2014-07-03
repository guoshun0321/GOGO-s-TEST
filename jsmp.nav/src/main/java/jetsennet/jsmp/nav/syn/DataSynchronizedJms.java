package jetsennet.jsmp.nav.syn;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.monitor.Monitor;
import jetsennet.jsmp.nav.monitor.MsgHandleMMsg;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据同步，用于同步运维系统数据
 * 
 * @author 郭祥
 */
public class DataSynchronizedJms
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
     * 模块状态
     */
    private volatile boolean isStart = false;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DataSynchronizedJms.class);

    public static final DataSynchronizedJms instance = new DataSynchronizedJms();

    private DataSynchronizedJms()
    {
    }

    public static DataSynchronizedJms getInstance()
    {
        return instance;
    }

    public synchronized void start()
    {
        try
        {
            if (!isStart)
            {
                ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(Config.MQ_USER, Config.MQ_PWD, Config.MQ_SERVERS);
                conn = fac.createConnection();
                conn.start();

                Queue queue = new ActiveMQQueue(Config.MQ_QUEUE);
                session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);

                MessageConsumer consumer = session.createConsumer(queue);
                consumer.setMessageListener(new DataSynMessageListener());
                logger.info("启动JMS同步模块成功！");

                this.isStart = true;
            }
            else
            {
                logger.info("JMS同步模块重复启动！");
            }
        }
        catch (Exception ex)
        {
            logger.error("启动JMS同步模块失败！", ex);
        }
    }

    public synchronized void stop()
    {
        try
        {
            if (this.isStart)
            {
                if (session != null)
                {
                    session.close();
                }
                logger.info("关闭JMS同步模块成功！");
            }
            else
            {
                logger.info("JMS同步模块重复关闭！");
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
                    if (Config.ISDEBUG)
                    {
                        logger.debug("收到消息：" + xml);
                    }
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
                    Monitor.getInstance().put(mmsg);
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

    public static void main(String[] args) throws Exception
    {
        DataSynchronizedJms syn = new DataSynchronizedJms();
        syn.start();
        TimeUnit.SECONDS.sleep(500);
        syn.stop();
    }
}
