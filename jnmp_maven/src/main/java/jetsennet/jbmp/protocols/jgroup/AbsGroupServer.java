package jetsennet.jbmp.protocols.jgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.Request;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.Obj2CollectorEntity;
import jetsennet.jbmp.util.ConfigUtil;

/**
 * @author lianghongjie JGroup服务器抽象类，实现如下功能： 1、 能够及时处理采集器发送过来的告警事件（提炼接口handle，留给子类实现） 2、 对采集器状态进行跟踪 3、
 *         当JNMP服务器初始化、恢复时，以及检测到采集器从故障恢复为正常之后，需要同步数据库(提炼接口syncDB，留给子类实现)
 */
public abstract class AbsGroupServer
{
    private static final Logger logger = Logger.getLogger(AbsGroupServer.class);
    private static final int TIMEOUT = 10000;

    /**
     * JGroup通道对象
     */
    protected JChannel channel;
    protected RpcDispatcher dispatcher;
    protected Set<Address> serverSet;
    protected Map<Integer, Address> node2AddrMap;
    protected Map<Integer, Integer> obj2ColMap;

    /**
     * 是否已启动
     */
    protected boolean isStart;

    /**
     * 数据库操作
     */
    private DefaultDal dal;

    /**
     * 构造函数
     */
    public AbsGroupServer()
    {
        serverSet = new HashSet<Address>();
        node2AddrMap = new HashMap<Integer, Address>();
        obj2ColMap = new ConcurrentHashMap<Integer, Integer>();
        isStart = false;
        dal = ClassWrapper.wrapTrans(DefaultDal.class);
    }

    /**
     * 启动集群
     * @param fileName 文件名
     * @throws Exception 异常
     */
    public void start(String fileName) throws Exception
    {
        logger.info("准备加入集群...");
        channel = new JChannel(this.getClass().getClassLoader().getResource(fileName));
        // 消息处理
        ReceiverAdapter mListener = new ReceiverAdapter()
        {
            @Override
            public void receive(Message msg)
            {
                logger.debug("收到集群消息: " + msg);
                Object obj = msg.getObject();
                // 采集器推送过来的报警
                if (obj instanceof AlarmEventEntity)
                {
                    logger.info("收到报警事件: " + obj);
                    handle((AlarmEventEntity) obj);
                }
                // 采集器推送过来的报警状态
                else if (obj instanceof MObjectEntity)
                {

                }
                else
                {
                    super.receive(msg);
                }
            }

            @Override
            public void suspect(Address addr)
            {
                logger.info("集群成员异常: " + addr);
                serverSet.remove(addr);
            }

            @Override
            public void viewAccepted(View view)
            {
                logger.info("集群视图更新:" + view);

                // 集群成员变更通知处理，当监测到有新加入的服务器时，设置同步标志位
                boolean syncFlag = false;
                for (Address addr : view.getMembers())
                {
                    if (!serverSet.contains(addr))
                    {
                        logger.info("新增集群成员: " + addr);
                        serverSet.add(addr);
                        syncFlag = true;
                    }
                }
                if (syncFlag)
                {
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            reset();
                        }
                    }).start();
                }
            }
        };
        dispatcher = new RpcDispatcher(channel, mListener, mListener, this);

        // 连接上集群
        channel.setOpt(JChannel.LOCAL, false);
        channel.setOpt(JChannel.BLOCK, true);
        channel.setOpt(JChannel.AUTO_GETSTATE, true);
        channel.setOpt(JChannel.AUTO_RECONNECT, true);
        String clusterName = ConfigUtil.getString("cluster.name");
        clusterName = clusterName != null ? clusterName : "JNMPCluster";
        channel.connect(clusterName);
        isStart = true;
        logger.info("加入集群成功");
    }

    /**
     * 停止集群
     */
    public void stop()
    {
        if (!isStart)
        {
            return;
        }
        logger.info("准备退出集群...");
        serverSet.clear();
        node2AddrMap.clear();
        dispatcher.stop();
        channel.close();
        isStart = false;
        logger.info("退出集群成功");
    }

    protected void reset()
    {
        LockService lockService = new LockService(channel);
        Lock lock = lockService.getLock("JNMPLock");
        logger.debug("获取集群锁");
        lock.lock();
        try
        {
            callRemote("block", true);
            try
            {
                logger.info("同步集群信息开始");
                syncCluster();
                logger.info("同步集群信息结束");
                logger.info("同步数据库开始");
                syncDB();
                logger.info("同步数据库结束");
            }
            catch (Exception e)
            {
                logger.error("同步集群/数据库异常", e);
            }
            callRemote("unblock");
        }
        finally
        {
            logger.debug("释放集群锁");
            lock.unlock();
        }
    }

    private void syncCluster()
    {
        node2AddrMap.clear();
        RspList lst = callRemote("getNodeInfos", true);
        for (Object entry : lst.entrySet())
        {
            String[] rspValue = (String[]) ((Entry<Address, Rsp>) entry).getValue().getValue();
            if (rspValue != null && rspValue.length > 0)
            {
                try
                {
                    int id = Integer.parseInt(rspValue[0]);
                    node2AddrMap.put(id, ((Entry<Address, Rsp>) entry).getKey());
                }
                catch (Exception e)
                {
                    logger.error("集群节点返回信息异常", e);
                }
            }
        }
    }

    /**
     * @param methodName 方法
     * @return 结果
     */
    public RspList callRemote(String methodName)
    {
        return callRemote(methodName, false);
    }

    /**
     * @param methodName 方法
     * @param isBlock 参数
     * @return 结果
     */
    public RspList callRemote(String methodName, boolean isBlock)
    {
        return callRemote(methodName, new Object[] {}, new Class[] {}, isBlock);
    }

    /**
     * @param methodName 方法
     * @param params 参数
     * @param classes 参数
     * @return 结果
     */
    public RspList callRemote(String methodName, Object[] params, Class[] classes)
    {
        return callRemote(methodName, params, classes, false);
    }

    /**
     * @param methodName 方法
     * @param params 参数
     * @param classes 参数
     * @param isBlock 参数
     * @return 结果
     */
    public RspList callRemote(String methodName, Object[] params, Class[] classes, boolean isBlock)
    {
        return (RspList) callRemote((Address) null, methodName, params, classes, isBlock);
    }

    /**
     * @param colId 采集id
     * @param methodName 方法
     * @return 结果
     */
    public Object callRemote(int colId, String methodName)
    {
        return callRemote(colId, methodName, false);
    }

    /**
     * @param colId 采集id
     * @param methodName 方法
     * @param isBlock 参数
     * @return 结果
     */
    public Object callRemote(int colId, String methodName, boolean isBlock)
    {
        return callRemote(colId, methodName, new Object[] {}, new Class[] {}, isBlock);
    }

    /**
     * @param colId 采集id
     * @param methodName 方法
     * @param params 参数
     * @param classes 参数
     * @return 结果
     */
    public Object callRemote(int colId, String methodName, Object[] params, Class[] classes)
    {
        return callRemote(colId, methodName, params, classes, false);
    }

    /**
     * @param methodName 方法
     * @param params 参数
     * @param classes 参数
     * @param isBlock 参数
     * @return 结果
     */
    public Object callRemote(int colId, String methodName, Object[] params, Class[] classes, boolean isBlock)
    {
        Address addr = node2AddrMap.get(colId);
        if (addr == null)
        {
            return null;
        }
        return callRemote(addr, methodName, params, classes, isBlock);
    }

    /**
     * @param objId 对象id
     * @param methodName 方法
     * @return 结果
     */
    public Object callRemote(String objId, String methodName)
    {
        return callRemote(objId, methodName, false);
    }

    /**
     * @param objId 对象id
     * @param methodName 方法
     * @param isBlock 参数
     * @return 结果
     */
    public Object callRemote(String objId, String methodName, boolean isBlock)
    {
        return callRemote(objId, methodName, new Object[] {}, new Class[] {}, isBlock);
    }

    /**
     * @param objId 对象id
     * @param methodName 方法
     * @param params 参数
     * @param classes 参数
     * @return 结果
     */
    public Object callRemote(String objId, String methodName, Object[] params, Class[] classes)
    {
        return callRemote(objId, methodName, params, classes, false);
    }

    /**
     * @param objId 对象id
     * @param methodName 方法
     * @param params 参数
     * @param classes 参数
     * @param isBlock 参数
     * @return 结果
     */
    public Object callRemote(String objId, String methodName, Object[] params, Class[] classes, boolean isBlock)
    {
        Integer colId = obj2ColMap.get(Integer.parseInt(objId));
        if (colId == null)
        {
            colId = Integer.valueOf(-1);
        }
        Address addr = node2AddrMap.get(colId);
        if (addr == null)
        {
            return null;
        }
        return callRemote(addr, methodName, params, classes, isBlock);
    }

    /**
     * @param colId 参数
     * @return 结果
     */
    public boolean isOnline(int colId)
    {
        logger.debug("判读节点：" + colId + "是否在线");
        Address addr = node2AddrMap.get(colId);
        if (addr == null)
        {
            logger.debug("节点：" + colId + "，不在线。");
            return false;
        }
        Object obj = callRemote(addr, "getNodeInfos", new Object[] {}, new Class[] {}, true);
        if (obj != null)
        {
            logger.debug("节点：" + colId + "，返回结果:" + obj);
        }
        else
        {
            logger.debug("节点：" + colId + "，无返回结果。");
        }
        return null != obj;
    }

    /**
     * @param objId 参数
     * @return 结果
     */
    public boolean isOnline(String objId)
    {
        Integer colId = obj2ColMap.get(Integer.parseInt(objId));
        if (colId == null)
        {
            return false;
        }
        return isOnline(colId);
    }

    /**
     * 远程调用集群中其他节点的方法
     * @param adr 节点地址，null表示所有
     * @param methodName 方法名
     * @param params 方法参数
     * @param classes 方法class
     * @param isBlock 调用此方法时是否阻塞，true：阻塞；false：立刻返回
     * @return 调用结果
     */
    public Object callRemote(Address adr, String methodName, Object[] params, Class[] classes, boolean isBlock)
    {
        return this.callRemote(adr, methodName, params, classes, isBlock, TIMEOUT);
    }

    /**
     * @param objId 对象id
     * @param methodName 方法
     * @param params 参数
     * @param classes 参数
     * @param isBlock 参数
     * @return 结果
     */
    public Object callRemote(String objId, String methodName, Object[] params, Class[] classes, boolean isBlock, int timeout)
    {
        Integer colId = obj2ColMap.get(Integer.parseInt(objId));
        if (colId == null)
        {
            colId = Integer.valueOf(-1);
        }
        Address addr = node2AddrMap.get(colId);
        if (addr == null)
        {
            return null;
        }
        return callRemote(addr, methodName, params, classes, isBlock, timeout);
    }

    /**
     * 远程调用集群中其他节点的方法
     * @param adr 节点地址，null表示所有
     * @param methodName 方法名
     * @param params 方法参数
     * @param classes 方法class
     * @param isBlock 调用此方法时是否阻塞，true：阻塞；false：立刻返回
     * @param timeout 超时
     * @return 调用结果
     */
    private Object callRemote(Address adr, String methodName, Object[] params, Class[] classes, boolean isBlock, int timeout)
    {
        try
        {
            RequestOptions opt = new RequestOptions();
            List<Address> adrLst = null;
            if (adr != null)
            {
                opt.setAnycasting(true);
                adrLst = new ArrayList<Address>(1);
                adrLst.add(adr);
            }
            if (isBlock)
            {
                opt.setMode(Request.GET_ALL);
                opt.setTimeout(timeout);
                RspList lst = dispatcher.callRemoteMethods(adrLst, methodName, params, classes, opt);
                logger.debug("方法<" + methodName + ">同步远程调用结果:" + lst);
                if (adr == null)
                {
                    return lst;
                }
                if (lst == null || lst.size() == 0)
                {
                    return null;
                }
                for (Object rsp : lst.values())
                {
                    Object value = ((Rsp) rsp).getValue();
                    if (value != null && !(value instanceof Exception))
                    {
                        return value;
                    }
                }
            }
            else
            {
                opt.setMode(Request.GET_NONE);
                dispatcher.callRemoteMethods(adrLst, methodName, params, classes, opt);
                logger.debug("方法<" + methodName + ">异步远程调用完成。");
            }
        }
        catch (Exception e)
        {
            logger.error("远程调用异常", e);
        }
        return null;
    }

    /**
     * 收到集群的block时调用
     */
    public void block()
    {
        logger.debug("收到阻塞调用");
    }

    /**
     * 收到集群的unblock时调用
     */
    public void unblock()
    {
        logger.debug("收到阻塞恢复调用");
    }

    /**
     * 返回该集群节点的配置信息
     * @return 字符串数组，其中第一个是id，web服务器和告警通知服务器默认-1，采集器是其具体的采集器id
     */
    public String[] getNodeInfos()
    {
        return new String[] { "-1" };
    }

    /**
     * 刷新对象id跟采集器id之间的关系
     * @throws Exception 异常
     */
    public void refreshObj2ColMap() throws Exception
    {
        List<Obj2CollectorEntity> o2cLst =
            dal.getLst(Obj2CollectorEntity.class, "SELECT o.OBJ_ID, g.NUM_VAL1 AS COLL_ID FROM BMP_OBJGROUP g "
                + "LEFT JOIN BMP_OBJ2GROUP o2g ON g.GROUP_ID=o2g.GROUP_ID "
                + "LEFT JOIN BMP_OBJECT o ON o.OBJ_ID=o2g.OBJ_ID OR o.PARENT_ID=o2g.OBJ_ID "
                + "WHERE g.GROUP_TYPE=3");
        Map<Integer, Integer> o2cMap = new HashMap<Integer, Integer>();
        for (Obj2CollectorEntity entity : o2cLst)
        {
            o2cMap.put(entity.getObjId(), entity.getCollId());
        }
        obj2ColMap.clear();
        obj2ColMap.putAll(o2cMap);
    }

    /**
     * 同步数据库接口，留给子类实现
     */
    protected void syncDB()
    {
    }

    /**
     * 处理告警事件接口，留给子类实现
     * @param alarm
     */
    protected abstract void handle(AlarmEventEntity alarm);
}
