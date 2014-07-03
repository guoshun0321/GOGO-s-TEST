package jetsennet.jbmp.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.jbmp.entity.AlarmStatisticEntity;
import jetsennet.jbmp.util.ConfigUtil;
import jetsennet.jbmp.util.ThreeTuple;

/**
 * @author lianghongjie 告警订阅、发布类
 */
public class AlarmPubSub implements AtmosphereHandler<HttpServletRequest, HttpServletResponse>
{
    private static final Logger logger = Logger.getLogger(AlarmPubSub.class);
    public static final String EMPTYRESP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response></Response>";
    public static final String RETURN_NOW = "ReturnNow";

    public static final String SUBCRIBE = "sub";
    public static final String UNSUBCRIBE = "unsub";
    public static final String ALARM_SUBINFO = "AlarmSubInfo";

    public static final String CLIENTID = "Timestamp";
    public static final String MSGID = "MsgId";
    public static final String ISGLOBAL = "IsGlobal";
    public static final String FILTER_LEVEL = "FilterLevel";
    public static final String OBJECTS = "Objects";
    public static final String GROUPS = "Groups";
    public static final String MAPS = "Maps";
    public static final String NODE = "Node";

    public static final String NEW_ALARMS = "NewAlarms";
    public static final String UPDATE_ALARMS = "UpdateAlarms";
    public static final String HIS_ALARMS = "HisAlarms";
    public static final String OLD_ALARMS = "OldAlarms";
    public static final String ALARM = "Alarm";

    public static int ALARM_QUERY_SUSPEND_TIMEOUT = 30 * 1000;
    public static int ALARM_QUERY_SESSION_TIMEOUT = 60 * 10;
    public static int REAL_ALARM_KEEP_HOURS = 24;
    public static int REAL_ALARM_KEEP_SIZE = 100000;
    public static int REAL_ALARM_FETCH_SIZE = 50;

    static
    {
        ALARM_QUERY_SUSPEND_TIMEOUT = ConfigUtil.getInteger("realalarm.suspendtimeout", 30) * 1000;
        ALARM_QUERY_SESSION_TIMEOUT = ConfigUtil.getInteger("realalarm.sessiontimeout", 600);
        REAL_ALARM_KEEP_HOURS = ConfigUtil.getInteger("realalarm.keephours", 24);
        REAL_ALARM_KEEP_SIZE = ConfigUtil.getInteger("realalarm.keepsize", 100000);
        REAL_ALARM_FETCH_SIZE = ConfigUtil.getInteger("realalarm.fetchsize", 50);
    }

    private Map<Long, AlarmSubEntity> subMap = new ConcurrentHashMap<Long, AlarmSubEntity>();
    private AtomicLong clientId = new AtomicLong(1);

    /*
     * 销毁
     */
    @Override
    public void destroy()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.atmosphere.cpr.AtmosphereHandler#onRequest(org.atmosphere.cpr.AtmosphereResource)
     */
    @Override
    public void onRequest(AtmosphereResource<HttpServletRequest, HttpServletResponse> source) throws IOException
    {
        // 验证请求参数
        String request = source.getRequest().getParameter(SUBCRIBE);
        logger.info("收到请求:" + request);
        if (request == null)
        {
            logger.error("非法请求:" + request);
            return;
        }
        Document requestDoc = null;
        try
        {
            requestDoc = DocumentHelper.parseText("<Request>" + request + "</Request>");
        }
        catch (DocumentException e)
        {
            logger.error("请求参数错误:" + request);
            return;
        }
        Element root = requestDoc.getRootElement();

        // 从缓存中查找上次请求状态
        AlarmSubEntity sub = null;
        Node clientIdNode = root.selectSingleNode(CLIENTID);
        if (clientIdNode != null)
        {
            long time = Long.parseLong(clientIdNode.getText());
            sub = subMap.get(time);
        }
        if (sub == null)
        {
            // 将请求信息缓存在后台
            sub = new AlarmSubEntity();
            long client = clientId.getAndIncrement();
            sub.setClientId(client);
            long cur = System.currentTimeMillis();
            sub.setLastUpdateTime(cur);
            subMap.put(client, sub);

            // 清除过期的请求信息
            for (AlarmSubEntity info : subMap.values())
            {
                if (cur - info.getLastUpdateTime() > TimeUnit.SECONDS.toMillis(AlarmPubSub.ALARM_QUERY_SESSION_TIMEOUT))
                {
                    logger.info("清除过期的客户端信息:" + info.getClientId());
                    subMap.remove(info.getClientId());
                }
            }
        }
        else
        {
            long cur = System.currentTimeMillis();
            sub.setLastUpdateTime(cur);
        }

        // 提取请求消息id
        String msgId = null;
        Node msgIdNode = root.selectSingleNode(MSGID);
        msgId = msgIdNode == null ? "" : msgIdNode.getText();
        String oldMsgId = (String) source.getRequest().getAttribute(MSGID);
        if (oldMsgId != null && Integer.parseInt(oldMsgId) > Integer.parseInt(msgId))
        {
            logger.info("过期请求，旧MsgId:" + oldMsgId + " 新MsgId:" + msgId);
            return;
        }

        // 将请求信息保存到请求属性中，以便请求恢复时使用
        source.getRequest().setAttribute(ALARM_SUBINFO, sub);
        source.getRequest().setAttribute(MSGID, msgId);

        // 处理请求
        synchronized (sub)
        {
            sub.fromXml(root);
            if (sub.setRequest(source))
            {
                try
                {
                    sub.wait();
                }
                catch (InterruptedException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
            AlarmStatistic.getInstance().checkAndResponse(sub, source, true);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.atmosphere.cpr.AtmosphereHandler#onStateChange(org.atmosphere.cpr.AtmosphereResourceEvent)
     */
    @Override
    public void onStateChange(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) throws IOException
    {
        AlarmSubEntity sub = (AlarmSubEntity) event.getResource().getRequest().getAttribute(ALARM_SUBINFO);

        // 处理事件
        synchronized (sub)
        {
            // 该请求已经处理完并返回给前台了
            if (sub.getRequest() != event.getResource())
            {
                logger.info("广播返回:" + event.getResource().getRequest().getAttribute(MSGID));
                event.getResource().resume();
                sub.notifyAll();
                return;
            }

            // 该事件是由超时|用户关闭窗口触发的
            if (event.isCancelled() || event.isResumedOnTimeout() || event.isResuming())
            {
                try
                {
                    // 响应消息头部
                    Document doc = DocumentHelper.parseText(AlarmPubSub.EMPTYRESP);
                    Element root = doc.getRootElement();
                    root.addElement(AlarmPubSub.CLIENTID).setText(Long.toString(sub.getClientId()));
                    String msgId = (String) event.getResource().getRequest().getAttribute(MSGID);
                    root.addElement(AlarmPubSub.MSGID).setText(msgId);
                    AlarmStatistic.getInstance().response(event.getResource(), doc.asXML());
                    event.getResource().resume();
                    sub.setRequest(null);
                    logger.info("超时返回:" + msgId);
                    return;
                }
                catch (DocumentException e)
                {
                    logger.error("超时返回异常:" + e, e);
                    return;
                }
            }

            // 该事件是由广播消息触发的
            AlarmStatistic.getInstance().checkAndResponse(sub, event.getResource(), false);
        }
    }

    /**
     * ？
     */
    public static class AlarmSubEntity
    {
        private long clientId;
        private long lastUpdateTime;
        private AtmosphereResource<HttpServletRequest, HttpServletResponse> request;

        private long lastTime;
        private List<ThreeTuple<Long, Long, Integer>> curAlarmLst;

        private boolean isGlobal;
        private int filterLevel;
        private List<AlarmStatisticEntity> objs;
        private Set<Integer> objSet;
        private List<AlarmStatisticEntity> grps;
        private List<AlarmStatisticEntity> maps;

        /**
         * 内部类构造函数
         */
        public AlarmSubEntity()
        {
            curAlarmLst = new LinkedList<ThreeTuple<Long, Long, Integer>>();
            objs = new ArrayList<AlarmStatisticEntity>();
            objSet = new HashSet<Integer>();
            grps = new ArrayList<AlarmStatisticEntity>();
            maps = new ArrayList<AlarmStatisticEntity>();
        }

        /**
         * @param root 参数
         */
        public void fromXml(Element root)
        {
            Node global = root.selectSingleNode(ISGLOBAL);
            if (global != null)
            {
                setGlobal(Boolean.parseBoolean(global.getText()));
            }
            Node level = root.selectSingleNode(FILTER_LEVEL);
            if (level != null)
            {
                setFilterLevel(Integer.parseInt(level.getText()));
            }
            List<AlarmStatisticEntity> objLst = getStatisticEntry(root, OBJECTS);
            List<AlarmStatisticEntity> grpLst = getStatisticEntry(root, GROUPS);
            List<AlarmStatisticEntity> mapLst = getStatisticEntry(root, MAPS);
            if (objLst != null || grpLst != null || mapLst != null)
            {
                // 切换拓扑的时候都需要重新发送当前告警列表给前台
                this.lastTime = 0;
                this.curAlarmLst.clear();
                this.objSet.clear();

                if (objLst != null)
                {
                    objs.clear();
                    objs.addAll(objLst);
                    for (AlarmStatisticEntity entity : objLst)
                    {
                        objSet.add(entity.getId());
                        AlarmStatistic.getInstance().fetchObjAllChilds(entity.getId(), objSet);
                    }
                }
                if (grpLst != null)
                {
                    grps.clear();
                    grps.addAll(grpLst);
                    for (AlarmStatisticEntity entity : grpLst)
                    {
                        AlarmStatistic.getInstance().fetchGrpAllChilds(entity.getId(), objSet);
                    }
                }
                if (mapLst != null)
                {
                    maps.clear();
                    maps.addAll(mapLst);
                }
            }
        }

        private List<AlarmStatisticEntity> getStatisticEntry(Element root, String paramName)
        {
            Node node = root.selectSingleNode(paramName);
            if (node == null)
            {
                return null;
            }
            List<Element> elmLst = node.selectNodes(NODE);
            List<AlarmStatisticEntity> requestLst = new ArrayList<AlarmStatisticEntity>(elmLst.size());
            for (Element elm : elmLst)
            {
                AlarmStatisticEntity entity = new AlarmStatisticEntity();
                entity.fromXml(elm);
                if (!OBJECTS.equals(paramName))
                {
                    entity.setIsGroup(true);
                }
                requestLst.add(entity);
            }
            return requestLst;
        }

        public long getClientId()
        {
            return clientId;
        }

        public void setClientId(long clientId)
        {
            this.clientId = clientId;
        }

        public long getLastUpdateTime()
        {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(long lastUpdateTime)
        {
            this.lastUpdateTime = lastUpdateTime;
        }

        public AtmosphereResource<HttpServletRequest, HttpServletResponse> getRequest()
        {
            return request;
        }

        /**
         * @param request 参数
         * @return 结果
         */
        public boolean setRequest(AtmosphereResource<HttpServletRequest, HttpServletResponse> request)
        {
            if (this.request != null && request != null)
            {
                try
                {
                    // 响应消息头部
                    Document doc = DocumentHelper.parseText(AlarmPubSub.EMPTYRESP);
                    Element root = doc.getRootElement();
                    root.addElement(AlarmPubSub.CLIENTID).setText(Long.toString(getClientId()));
                    String msgId = (String) this.request.getRequest().getAttribute(MSGID);
                    root.addElement(AlarmPubSub.MSGID).setText(msgId);
                    AlarmStatistic.getInstance().response(this.request, doc.asXML());
                    logger.info("中断上一请求:" + msgId);

                    AtmosphereResource<HttpServletRequest, HttpServletResponse> tmp = this.request;
                    this.request = request;
                    if (tmp.getBroadcaster() != null)
                    {
                        tmp.getBroadcaster().broadcast(RETURN_NOW, tmp);
                    }
                    return true;
                }
                catch (Exception e)
                {
                    logger.error("恢复请求异常:" + e, e);
                    return false;
                }
            }
            else
            {
                this.request = request;
                return false;
            }
        }

        public Set<Integer> getObjSet()
        {
            return objSet;
        }

        public void setObjSet(Set<Integer> objSet)
        {
            this.objSet = objSet;
        }

        public long getLastTime()
        {
            return lastTime;
        }

        public void setLastTime(long lastTime)
        {
            this.lastTime = lastTime;
        }

        public boolean isGlobal()
        {
            return isGlobal;
        }

        /**
         * @param isGlobal 参数
         */
        public void setGlobal(boolean isGlobal)
        {
            this.isGlobal = isGlobal;
            this.lastTime = 0;
            this.curAlarmLst.clear();
        }

        public int getFilterLevel()
        {
            return filterLevel;
        }

        /**
         * @param filterLevel 参数
         */
        public void setFilterLevel(int filterLevel)
        {
            this.filterLevel = filterLevel;
            this.lastTime = 0;
            this.curAlarmLst.clear();
        }

        public List<AlarmStatisticEntity> getObjs()
        {
            return objs;
        }

        public void setObjs(List<AlarmStatisticEntity> objs)
        {
            this.objs = objs;
        }

        public List<AlarmStatisticEntity> getGrps()
        {
            return grps;
        }

        public void setGrps(List<AlarmStatisticEntity> grps)
        {
            this.grps = grps;
        }

        public List<AlarmStatisticEntity> getMaps()
        {
            return maps;
        }

        public void setMaps(List<AlarmStatisticEntity> maps)
        {
            this.maps = maps;
        }

        /**
         * @return the curAlarmLst
         */
        public List<ThreeTuple<Long, Long, Integer>> getCurAlarmLst()
        {
            return curAlarmLst;
        }

        /**
         * @param curAlarmLst the curAlarmLst to set
         */
        public void setAlarmLst(List<ThreeTuple<Long, Long, Integer>> curAlarmLst)
        {
            this.curAlarmLst = curAlarmLst;
        }
    }
}
