/************************************************************************
日 期：2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: 自动发现工具类
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.helper.SingleResult;
import jetsennet.jbmp.entity.AutoDisObjEntity;
import jetsennet.jbmp.entity.MObjectEntity;

/**
 * 自动发现工具类
 * @author 郭祥
 */
public class AutoDisUtil
{

    private static final Logger logger = Logger.getLogger(AutoDisUtil.class);

    /**
     * 从集合<irs>中挑选有<pro>协议的结果集。如果<pro>为空，结果为irs的内容
     * @param irs 参数
     * @param pro 参数
     * @return 结果
     */
    public static List<SingleResult> getIpResultByPro(List<SingleResult> irs, String pro)
    {
        List<SingleResult> retval = new ArrayList<SingleResult>();
        if (irs != null && !irs.isEmpty())
        {
            if (pro == null)
            {
                for (SingleResult ir : irs)
                {
                    retval.add(ir);
                }
            }
            else
            {
                for (SingleResult ir : irs)
                {
                    if (ir.getByPro(pro) != null)
                    {
                        retval.add(ir);
                    }
                }
            }
        }
        return retval;
    }

    /**
     * IP集合
     * @param irs 参数
     * @return 结果
     */
    public static List<String> getIpColl(List<SingleResult> irs)
    {
        List<String> retval = new ArrayList<String>();
        if (irs != null && !irs.isEmpty())
        {
            for (SingleResult ir : irs)
            {
                retval.add(ir.getSingleKey());
            }
        }
        return retval;
    }

    /**
     * @param list 参数
     * @return 结果
     */
    public static Map<String, AutoDisObjEntity> listToMap(List<AutoDisObjEntity> list)
    {
        Map<String, AutoDisObjEntity> map = new HashMap<String, AutoDisObjEntity>();
        if (list == null || list.isEmpty())
        {
            return map;
        }
        for (AutoDisObjEntity obj : list)
        {
            map.put(obj.getIp(), obj);
        }
        return map;
    }

    /**
     * 判断设备类型
     * @param sysServices
     * @param ipForwarding
     * @param dot1dBaseNumPorts
     * @return
     */
    public static int ensureLinkType(String sysServices, String ipForwarding, String dot1dBaseNumPorts)
    {
        int retval = MObjectEntity.LINKLAYER_TYPE_UNKNOWN;
        try
        {
            if (ipForwarding != null && sysServices != null)
            {
                if (dot1dBaseNumPorts == null || dot1dBaseNumPorts.equalsIgnoreCase("NULL"))
                {
                    // 这个参数备用
                    int service = Integer.valueOf(sysServices);
                    int forward = Integer.valueOf(ipForwarding);
                    if (forward == 1)
                    {
                        retval = MObjectEntity.LINKLAYER_TYPE_ROUTER;
                    }
                    else
                    {
                        retval = MObjectEntity.LINKLAYER_TYPE_HOST;
                    }
                }
                else
                {
                    retval = MObjectEntity.LINKLAYER_TYPE_SWITCH;
                }
            }
        }
        catch (Exception ex)
        {
            // 忽略异常
        }
        return retval;
    }
}
