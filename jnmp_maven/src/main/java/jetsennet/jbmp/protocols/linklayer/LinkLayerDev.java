/************************************************************************
日 期：2012-10-10
作 者: 郭祥
版 本：v1.3
描 述: 链路层自动发现实体
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.autodiscovery.helper.LinkLayerData;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.linklayer.util.SnmpNetInterface;

import org.apache.log4j.Logger;

/**
 * 链路层自动发现实体
 * @author 郭祥
 */
public class LinkLayerDev
{

    /**
     * 对象
     */
    private MObjectEntity mo;
    /**
     * 类型
     */
    private int type;
    /**
     * MAC地址
     */
    private String mac;
    /**
     * 接口
     */
    private List<SnmpNetInterface> interfs;
    /**
     * 数据访问
     */
    private MObjectDal modal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(LinkLayerDev.class);

    public LinkLayerDev()
    {
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
        interfs = new ArrayList<SnmpNetInterface>();
    }

    /**
     * 将对象转换为实体
     * @param mo
     */
    public void init(MObjectEntity mo, LinkLayerData link, List<String> filterMacs)
    {
        logger.debug("开始处理对象：" + mo.getIpAddr());
        this.mo = mo;
        this.mac = link.getMac();
        int linkType = link.getLinkType();
        logger.debug("确定对象类型：" + linkType);
        if (linkType < MObjectEntity.LINKLAYER_TYPE_ROUTER || linkType > MObjectEntity.LINKLAYER_TYPE_UNKNOWN)
        {
            this.type = MObjectEntity.LINKLAYER_TYPE_UNKNOWN;
        }
        else
        {
            this.type = linkType;
            logger.debug("开始实例化接口数据。");
            this.initInterf(filterMacs, link);
        }
    }

    /**
     * 初始化接口信息
     * @throws Exception
     */
    private void initInterf(List<String> filterMacs, LinkLayerData link)
    {
        try
        {
            if (link.getAftMap() != null && link.getIfMap() != null)
            {
                // 地址转发表
                logger.debug("地址转发表：\n" + link.getAftMap());
                AddressForwardTable aft = new AddressForwardTable();
                aft.initTable(link.getAftMap());

                // 接口表
                logger.debug("接口表：\n" + link.getIfMap());
                SnmpTable ifTable = link.getIfMap();

                // 接口对象
                Map<Integer, MObjectEntity> interfMap = modal.getAllInterface(mo.getObjId());
                this.initInterfs(ifTable, aft, interfMap, filterMacs);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("设备IP：").append(mo.getIpAddr()).append(", ");
        sb.append("设备MAC：").append(this.mac);
        return sb.toString();
    }

    /**
     * 关联接口对象和地址转发表
     * @param ip
     * @param version
     * @param port
     * @param community
     * @param aft
     * @param interfMap
     * @throws Exception
     */
    private void initInterfs(SnmpTable ifTable, AddressForwardTable aft, Map<Integer, MObjectEntity> interfMap, List<String> filterMacs)
    {
        int rowNum = ifTable.getRowNum();
        for (int i = 0; i < rowNum; i++)
        {
            // 接口信息
            int ifIndex = Integer.valueOf(ifTable.getCell(i, 0).getEditValue());
            String ifDescr = ifTable.getCell(i, 1).getEditValue();
            int ifType = Integer.valueOf(ifTable.getCell(i, 2).getEditValue());
            String ifPhysAddress = ifTable.getCell(i, 3).getEditValue();

            // 只初始化存在的接口
            if (interfMap.get(ifIndex) != null)
            {
                // 地址转发表，进行MAC地址过滤
                AddressForwardTableEntity aftEntity = aft.get(ifIndex);
                if (aftEntity != null)
                {
                    aftEntity.filterMac(filterMacs);

                    SnmpNetInterface interf = new SnmpNetInterface(ifIndex, ifDescr, ifPhysAddress, ifType);
                    interf.setAft(aftEntity);
                    interf.setParent(this);
                    interf.setMo(interfMap.get(ifIndex));
                    interfs.add(interf);
                    logger.debug("确定接口：" + ifIndex + "，MAC地址：" + aftEntity.getMacs());
                }
            }
        }
    }

    /**
     * 根据MAC地址获取接口
     * @param oMac
     * @return
     */
    public SnmpNetInterface getInterfByMac(String oMac)
    {
        for (SnmpNetInterface interf : interfs)
        {
            AddressForwardTableEntity addrTable = interf.getAft();
            List<String> macs = addrTable.getMacs();
            for (String mac : macs)
            {
                if (mac.equals(oMac))
                {
                    return interf;
                }
            }
        }
        return null;
    }

    public MObjectEntity getMo()
    {
        return mo;
    }

    public void setMo(MObjectEntity mo)
    {
        this.mo = mo;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getMac()
    {
        return mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public List<SnmpNetInterface> getInterfs()
    {
        return interfs;
    }

    public void setInterfs(List<SnmpNetInterface> interfs)
    {
        this.interfs = interfs;
    }

    public MObjectDal getModal()
    {
        return modal;
    }

    public void setModal(MObjectDal modal)
    {
        this.modal = modal;
    }
}
