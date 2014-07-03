/************************************************************************
日 期：2012-10-10
作 者: 郭祥
版 本：v1.3
描 述: 链路层自动发现
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.autodiscovery.helper.LinkLayerData;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.Port2PortDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.protocols.linklayer.util.SnmpNetInterface;
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.util.StringUtil;

import org.apache.log4j.Logger;

/**
 * 链路层自动发现
 * @author 郭祥
 */
public class LinkLayerDisc
{

    /**
     * 需要扫描的对象
     */
    private List<MObjectEntity> devs;
    /**
     * 对象的链路层数据
     */
    private Map<MObjectEntity, LinkLayerData> mo2data;
    /**
     * 对象的链路层数据
     */
    private Map<String, LinkLayerData> ip2data;
    /**
     * 所有的MAC地址
     */
    private List<String> allMacs;
    /**
     * 远程调用
     */
    private BMPServletContextListener listener;
    // 数据库访问
    MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(LinkLayerDisc.class);

    public LinkLayerDisc()
    {
        listener = BMPServletContextListener.getInstance();
        mo2data = new HashMap<MObjectEntity, LinkLayerData>();
        ip2data = new HashMap<String, LinkLayerData>();
        devs = new ArrayList<MObjectEntity>();
        allMacs = new ArrayList<String>();
    }

    /**
     * 链路层自动发现
     * @param groupId
     */
    public void disc(int groupId)
    {
        List<MObjectEntity> mos = modal.getByGroupId(groupId);
        if (mos != null)
        {
            this.disc(mos, groupId);
        }
    }

    /**
     * 链路层自动发现
     * @param mos
     * @param groupId
     */
    public void disc(List<MObjectEntity> mos, int groupId)
    {
        LinkLayerLinkRel rel = this.devRelDisc(mos);

        // 更新连接关系
        Port2PortDal ppdal = ClassWrapper.wrapTrans(Port2PortDal.class);
        ppdal.updateLink(groupId, rel.toEntity(groupId));
    }

    /**
     * 链路层关系自动发现
     * @param mos
     * @return
     */
    public LinkLayerLinkRel devRelDisc(List<MObjectEntity> mos)
    {
        // 存储关系
        LinkLayerLinkRel rel = new LinkLayerLinkRel();

        // 获取链路层数据
        this.initLinkLayerData(mos);

        // 对象转化为实体
        Map<String, LinkLayerDev> devMap = new HashMap<String, LinkLayerDev>();
        List<LinkLayerDev> devList = new ArrayList<LinkLayerDev>();
        List<LinkLayerDev> hosts = new ArrayList<LinkLayerDev>();
        List<LinkLayerDev> switchers = new ArrayList<LinkLayerDev>();
        List<LinkLayerDev> routers = new ArrayList<LinkLayerDev>();
        for (MObjectEntity mo : devs)
        {
            // 对象转换为实体
            LinkLayerDev tempDev = new LinkLayerDev();
            LinkLayerData tempData = mo2data.get(mo);
            tempDev.init(mo, tempData, allMacs);
            // 添加到集合
            devMap.put(mo.getField2(), tempDev);
            devList.add(tempDev);
            // 初始化接口信息
            int tempType = tempDev.getType();
            switch (tempType)
            {
            case MObjectEntity.LINKLAYER_TYPE_ROUTER:
                routers.add(tempDev);
                break;
            case MObjectEntity.LINKLAYER_TYPE_SWITCH:
                switchers.add(tempDev);
                break;
            case MObjectEntity.LINKLAYER_TYPE_HOST:
                hosts.add(tempDev);
                break;
            case MObjectEntity.LINKLAYER_TYPE_UNKNOWN:
                hosts.add(tempDev);
                break;
            }
            modal.updateLinkType(mo.getObjId(), tempType);
        }

        // 初始化接口和接口、接口和设备之间的连接关系
        List<SnmpNetInterface> allInterf = new ArrayList<SnmpNetInterface>();
        logger.debug("接口、设备关系初始化开始。");
        for (LinkLayerDev srcDev : devList)
        {
            List<SnmpNetInterface> srcInterfs = srcDev.getInterfs();
            for (SnmpNetInterface srcInterf : srcInterfs)
            {
                // 添加源接口
                allInterf.add(srcInterf);
                AddressForwardTableEntity addrTable = srcInterf.getAft();
                List<String> macs = addrTable.getMacs();
                for (String mac : macs)
                {
                    // 找到目标设备
                    LinkLayerDev dstDev = devMap.get(mac);
                    if (dstDev != null)
                    {
                        // 查找目标设备的相关接口
                        SnmpNetInterface dstInterf = dstDev.getInterfByMac(srcDev.getMac());
                        if (dstInterf != null)
                        {
                            // 连接到接口
                            logger.debug("接口:" + srcInterf.getMo().getObjId() + "连接到接口：" + dstInterf.getMo().getObjId());
                            srcInterf.addLink(dstInterf);
                        }
                        else
                        {
                            // 连接到设备
                            logger.debug("接口:" + srcInterf.getMo().getObjId() + "连接到主机：" + dstDev.getMo().getObjId());
                            srcInterf.addLink(dstDev);
                        }
                    }
                }
            }
        }
        logger.debug("接口、设备关系初始化结束。");

        logger.debug("接口到主机关系判断开始。");
        // 查找全部连接到主机的关系
        for (SnmpNetInterface srcInterf : allInterf)
        {
            // 找出连接全部是主机的接口
            List<Object> relDevs = srcInterf.getRelDevs();
            // 判断是否为叶节点
            boolean isLeaf = true;
            for (Object relObj : relDevs)
            {
                if (relObj instanceof LinkLayerDev)
                {
                    LinkLayerDev dstDev = (LinkLayerDev) relObj;
                    // 这里需要考虑，不一定正确
                    if (dstDev.getType() == MObjectEntity.LINKLAYER_TYPE_SWITCH || dstDev.getType() == MObjectEntity.LINKLAYER_TYPE_ROUTER)
                    {
                        isLeaf = false;
                        break;
                    }
                }
                else
                {
                    // 连接到接口
                    isLeaf = false;
                    break;
                }
            }
            if (isLeaf)
            {
                // 添加主机和交换机之间的关系
                for (Object obj : relDevs)
                {
                    logger.debug("接口:" + srcInterf.getMo().getObjId() + "连接到主机：" + ((LinkLayerDev) obj).getMo().getObjId() + "关系确认。");
                    rel.addRel(srcInterf.getParent(), srcInterf, (LinkLayerDev) obj);
                }
            }
        }

        // 从接口的关系集合里删掉主机
        for (int i = 0; i < allInterf.size();)
        {
            SnmpNetInterface srcInterf = allInterf.get(i);
            for (LinkLayerDev host : hosts)
            {
                srcInterf.removeLink(host);
            }
            if (srcInterf.getRelDevs().isEmpty())
            {
                allInterf.remove(i);
            }
            else
            {
                i++;
            }
        }
        logger.debug("接口到主机关系判断结束。");

        logger.debug("交换机到交换机关系判断开始。");
        boolean isStop = false;
        // 交换机之间的关系
        while (!allInterf.isEmpty() && !isStop)
        {
            // 本次轮询中需要被删除的设备（边缘节点）
            List<LinkLayerDev> delDevs = new ArrayList<LinkLayerDev>();
            for (int i = 0; i < allInterf.size(); i++)
            {
                // 取出只包含一个接口的接口
                SnmpNetInterface srcInterf = allInterf.get(i);
                if (srcInterf.getRelDevs().size() == 1)
                {
                    Object obj = srcInterf.getRelDevs().get(0);
                    if (obj instanceof SnmpNetInterface)
                    {
                        // 添加两个接口之间的连接关系
                        SnmpNetInterface tempInterf = (SnmpNetInterface) obj;
                        rel.addRel(srcInterf.getParent(), srcInterf, tempInterf.getParent(), tempInterf);

                        // 边缘节点添加到删除队列
                        delDevs.add(tempInterf.getParent());
                    }
                    else
                    {
                        // 如果连的是主机的话，忽略这个连接
                    }
                }
            }

            if (!delDevs.isEmpty())
            {
                // 边缘节点集合不为空时，从接口集合中删掉边缘节点
                for (int i = 0; i < allInterf.size();)
                {
                    SnmpNetInterface srcInterf = allInterf.get(i);
                    for (LinkLayerDev dev : delDevs)
                    {
                        srcInterf.removeLink(dev);
                    }
                    if (srcInterf.getRelDevs().isEmpty())
                    {
                        allInterf.remove(i);
                    }
                    else
                    {
                        i++;
                    }
                }
            }
            else
            {
                // 边缘节点集合为空时，可能是算法出现问题。停止自动发现的执行。
                isStop = true;
            }
        }
        logger.debug("交换机到交换机关系判断结束。");
        return rel;
    }

    /**
     * 初始化链路层数据
     * @param mos
     */
    private void initLinkLayerData(List<MObjectEntity> mos)
    {
        for (MObjectEntity mo : mos)
        {
            String ip = mo.getIpAddr();
            if (ip != null)
            {
                if (ip2data.get(ip) == null)
                {
                    LinkLayerData tempData = null;

                    try
                    {
                        // 本地执行，测试用
                        // tempData = LinkLayerDataGen.genData(ip, mo.getIpPort(), mo.getUserName(), mo.getVersion(), null);
                        tempData =
                            (LinkLayerData) listener.callRemote(Integer.toString(mo.getObjId()), "remoteGetLinkLayerData", new Object[] { ip,
                                mo.getIpPort(), mo.getUserName(), mo.getVersion() }, new Class[] { String.class, int.class, String.class,
                                String.class }, true, 20000);
                    }
                    catch (Exception ex)
                    {
                        logger.error("", ex);
                    }
                    if (tempData != null)
                    {
                        String mac = tempData.getMac();
                        if (!StringUtil.isNullOrEmpty(mac))
                        {
                            logger.debug("确定对象：" + mo.getObjId() + "，IP：" + mo.getIpAddr() + "，MAC：" + mac);
                            devs.add(mo);
                            mo2data.put(mo, tempData);
                            ip2data.put(ip, tempData);
                            allMacs.add(mac);
                        }
                        else
                        {
                            logger.debug("无法获取MAC地址，放弃对象：" + mo.getObjId() + "，IP：" + mo.getIpAddr());
                        }
                    }
                    else
                    {
                        logger.debug("获取数据失败，放弃对象：" + +mo.getObjId() + "，IP：" + mo.getIpAddr());
                    }
                }
                else
                {
                    logger.debug("存在IP相同的对象，放弃对象：" + mo.getObjId() + "，IP：" + mo.getIpAddr());
                }
            }
            else
            {
                logger.debug("IP地址为空，放弃对象：" + mo.getObjId() + "，IP：" + mo.getIpAddr());
            }
        }
    }

    public static void main(String[] args)
    {
        LinkLayerDisc link = new LinkLayerDisc();
        link.disc(24);
    }

}
