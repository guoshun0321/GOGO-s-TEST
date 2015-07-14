/************************************************************************
日 期：2011-11-28
作 者: 
版 本：v1.3
描 述: 采集器端通讯类。负责：集群加入、推出以及集群间通讯。
历 史：
 ************************************************************************/
package jetsennet.jbmp.datacollect.collectorif;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.alarm.AlarmException;
import jetsennet.jbmp.alarm.AlarmManager;
import jetsennet.jbmp.alarm.RegResource;
import jetsennet.jbmp.alarm.eventhandle.AlarmEventDispatch;
import jetsennet.jbmp.autodiscovery.AutoDisMethod;
import jetsennet.jbmp.autodiscovery.helper.LinkLayerData;
import jetsennet.jbmp.autodiscovery.helper.LinkLayerDataGen;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.rrd.RrdHelper;
import jetsennet.jbmp.dataaccess.rrd.RrdUtil;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.datacollect.datasource.DataAgentManager;
import jetsennet.jbmp.datacollect.datasource.IDataAgent;
import jetsennet.jbmp.datacollect.scheduler.CollManager;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.CollectTaskEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.QueryResult;
import jetsennet.jbmp.util.ConfigUtil;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 采集器端，报警集群。
 * @author
 */
public class ClusterManager
{
    /**
     * 集群协议栈
     */
    public static final String CLUSTER_PROT = "flush-tcp-collector.xml";
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ClusterManager.class);

    /**
     * channel
     */
    private JChannel channel;
    private RpcDispatcher dispatcher;
    /**
     * 报警模块
     */
    private AlarmManager alarm;
    /**
     * 锁
     */
    private Lock lock;

    /**
     * 构造方法
     */
    public ClusterManager()
    {
        lock = new ReentrantLock();
    }

    /**
     * 开始
     */
    public void start()
    {
        Lock l = lock;
        try
        {
            l.lock();
            channel = new JChannel(this.getClass().getClassLoader().getResource(ClusterManager.CLUSTER_PROT));
            ReceiverAdapter mListener = new AlarmClusterExtendedReceiverAdapter();
            dispatcher = new RpcDispatcher(channel, mListener, mListener, this);
            channel.setOpt(JChannel.LOCAL, false);
            channel.setOpt(JChannel.BLOCK, true);
            channel.setOpt(JChannel.AUTO_RECONNECT, true);
            channel.setOpt(JChannel.AUTO_GETSTATE, true);
            String clusterName = ConfigUtil.getString("cluster.name");
            clusterName = clusterName != null ? clusterName : "JNMPCluster";
            channel.connect(clusterName);
            logger.info("集群连接成功。");
            // 将集群注册到RegResource.RESOURCE_CLUSTER_NAME
            RegResource.set(RegResource.RESOURCE_CLUSTER_NAME, this);
            init();
        }
        catch (Exception ex)
        {
            logger.error("获取状态失败，请确认接收端服务器已打开。", ex);
            throw new AlarmException(ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 结束
     */
    public void stop()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (alarm != null)
            {
                try
                {
                    alarm.stop();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    alarm = null;
                }
            }
            if (channel != null)
            {
                try
                {
                    dispatcher.stop();
                    channel.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    channel = null;
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 
     */
    public void block()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (alarm != null)
            {
                alarm.blockAlarmEventHandle();
            }
            logger.info("事件处理模块阻塞。");
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 
     */
    public void unblock()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (alarm != null)
            {
                alarm.unblockAlarmEventHandle();
            }
            logger.info("事件处理模块解除阻塞。");
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 发送报警
     * @param event 参数
     */
    public void sendMessage(AlarmEventEntity event)
    {
        try
        {
            if (channel != null && event != null)
            {
                Message msg = new Message(null, null, event);
                channel.send(msg);
                logger.info("发送报警：" + event);
            }
            else
            {
                logger.error("channel未创建，请先开启集群模块。channel:" + channel + ";event:" + event);
            }
        }
        catch (Throwable t)
        {
            logger.error("", t);
        }
    }

    /**
     * 返回集群中，该节点的信息
     * 
     * @return 结果
     */
    public String[] getNodeInfos()
    {
        int collId = XmlCfgUtil.getIntValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_ID_CFG, -1);
        logger.debug("集群远程调用：获取节点ID：" + collId);
        return new String[] { Integer.toString(collId) };
    }

    /**
     * @param objId 对象id
     * @param size 大小
     * @param objAttrIds 对象属性
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, List<QueryResult>> queryPerfData(int objId, int size, int[] objAttrIds) throws Exception
    {
        return RrdHelper.getInstance().query(objId, size, objAttrIds);
    }

    /**
     * @param objId 对象
     * @param fetchSizes 参数
     * @param fetchTimes 参数
     * @param objAttrIds 对象属性
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, List<QueryResult>> queryPerfData(int objId, int[] fetchSizes, int[] fetchTimes, int[] objAttrIds) throws Exception
    {
        return RrdHelper.getInstance().query(objId, fetchSizes, fetchTimes, objAttrIds);
    }

    /**
     * @param objId 对象ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param objAttrIds 对象属性
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, List<QueryResult>> queryPerfData(int objId, long startTime, long endTime, int[] objAttrIds) throws Exception
    {
        return RrdHelper.getInstance().query(objId, startTime, endTime, objAttrIds);
    }

    /**
     * @param task 任务id
     * @return 结果
     * @throws Exception 异常
     */
    public Map<Integer, Integer> getCollectTaskInfo(CollectTaskEntity task) throws Exception
    {
        return CollManager.getInstance().getCollectTaskInfo(task);
    }

    /**
     * @param task 任务id
     * @return 结果
     * @throws Exception 异常
     */
    public boolean startCollectTask(CollectTaskEntity task) throws Exception
    {
        return CollManager.getInstance().handleSchedule(task);
    }

    /**
     * @param objId 对象id
     * @param task 任务id
     * @return 结果
     * @throws Exception 异常
     */
    public boolean startCollectTask(CollectTaskEntity task, int objId) throws Exception
    {
        return CollManager.getInstance().handleSchedule(task, objId);
    }

    /**
     * @param task 任务id
     * @return 结果
     * @throws Exception 异常
     */
    public boolean stopCollectTask(CollectTaskEntity task) throws Exception
    {
        return CollManager.getInstance().handleInterrupt(task);
    }

    /**
     * @param task 任务id
     * @return 结果
     * @throws Exception 异常
     */
    public boolean restartCollectTask(CollectTaskEntity task) throws Exception
    {
        if (stopCollectTask(task))
        {
            return startCollectTask(task);
        }
        return false;
    }

    /**
     * @param objId 对象id
     * @param task 任务id
     * @return 结果
     * @throws Exception 异常
     */
    public boolean stopCollectTask(CollectTaskEntity task, int objId) throws Exception
    {
        return CollManager.getInstance().handleInterrupt(task, objId);
    }

    /**
     * 远程调用移除事件
     * @param event 参数
     */
    public void handleFinish(AlarmEventEntity event)
    {
        AlarmEventDispatch.getInstance().manuClean(event.getObjAttrId(), event.getAlarmEvtId());
    }

    /**
     * 远程调用，数据采集
     * @param msg 传递过来的数据
     * @return 传递出去的数据
     */
    public TransMsg remoteCollData(TransMsg msg)
    {
        try
        {
            IDataAgent agent = DataAgentManager.getAgent();
            msg = agent.getDataForIns(msg);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return msg;
    }

    /**
     * 远程调用，自动发现
     * @param taskId 任务ID
     * @param userId 用户id
     * @param userName 用户名称
     */
    public void remoteAutoDis(int taskId, int userId, String userName)
    {
        AutoDisMethod dis = AutoDisMethod.getInstance();
        dis.disWithLock(taskId, userId, userName);
    }

    /**
     * 远程调用SNMP协议,WALK
     * @param objId 对象id
     * @param version 参数
     * @param oids 参数
     * @return 结果
     */
    public Map<String, Map<String, VariableBinding>> remoteSnmpWalk(int objId, String version, String[] oids)
    {
        Map<String, Map<String, VariableBinding>> retval = null;
        if (oids != null && oids.length > 0)
        {
            try
            {
                MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
                MObjectEntity mo = modal.get(objId);
                if (mo != null)
                {
                    IDataAgent agent = DataAgentManager.getAgent();
                    mo.setVersion(version);
                    retval = agent.snmpGetSubInfo(mo, oids);
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        if (retval == null)
        {
            retval = new HashMap<String, Map<String, VariableBinding>>();
        }
        return retval;
    }

    /**
     * 远程调用，自动发现
     * @param objId 对象ID
     * @param xml 参数
     * @return 结果
     */
    public int remoteSetValueManu(int objId, String xml)
    {
        return RrdUtil.setValueFromXml(objId, xml);
    }

    /**
     * 远程调用，用于链路层自动发现
     * @param objId 对象ID
     * @param xml 参数
     * @return 结果
     */
    public LinkLayerData remoteGetLinkLayerData(String ip, int port, String community, String version)
    {
        return LinkLayerDataGen.genData(ip, port, community, version, null);
    }

    private void init()
    {
        alarm = AlarmManager.getInstance();
        alarm.start();
    }

    class AlarmClusterExtendedReceiverAdapter extends ReceiverAdapter
    {
        @Override
        public void suspect(Address addr)
        {
            logger.info("集群成员异常: " + addr);
        }

        @Override
        public void viewAccepted(View view)
        {
            logger.info("集群视图更新:" + view);
        }
    }
}
