/************************************************************************
日 期：2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: SNMP自动发现
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.helper.AbsAutoDisResultHandle;
import jetsennet.jbmp.autodiscovery.helper.AbsIpFilter;
import jetsennet.jbmp.autodiscovery.helper.ArpIpFilter;
import jetsennet.jbmp.autodiscovery.helper.AutoDisResult;
import jetsennet.jbmp.autodiscovery.helper.NetworkDiscover;
import jetsennet.jbmp.autodiscovery.helper.SnmpClassDiscover;
import jetsennet.jbmp.autodiscovery.helper.SnmpDisResultHandle;
import jetsennet.jbmp.autodiscovery.helper.SnmpDiscover;
import jetsennet.jbmp.dataaccess.AutoDisTaskDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jbmp.util.BMPConstants;

/**
 * SNMP自动发现
 * @author 郭祥
 */
public class SnmpAutoDiscover extends AbsAutoDiscover
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AutoDis.class);

    /**
     * 构造方法
     */
    public SnmpAutoDiscover()
    {
    }

    @Override
    public AutoDisResult discover(AutoDisTaskEntity task, int userId, String userName, boolean isLog) throws DiscoverException
    {
        AutoDisResult coll = null;
        try
        {
            long begin = System.currentTimeMillis();
            coll = this.initResult(task);

            // ARP
//            NetworkDiscover nd = new NetworkDiscover();
//            nd.find(coll);

            // SNMP
            AbsIpFilter filter = new ArpIpFilter();
            String community = task.getCommunity();
            if (community == null || "".equals(community.trim()))
            {
                community = BMPConstants.SNMP_COMMUNITY;
            }
            SnmpDiscover sd = new SnmpDiscover(BMPConstants.SNMP_PORT, community);
            sd.setFilter(filter);
            sd.find(coll);

            // 类型
            SnmpClassDiscover cd = new SnmpClassDiscover();
            cd.find(coll);

            // 数据处理
            AbsAutoDisResultHandle handle = new SnmpDisResultHandle(task.getTaskId(), task.getCollId());
            this.setResultHandleParam(handle, task, isLog, userId, userName);
            handle.handle(coll, userId);
            long end = System.currentTimeMillis();
            coll.setTime(end - begin);
        }
        catch (Throwable ex)
        {
            logger.error("", ex);
            throw new DiscoverException(ex);
        }
        return coll;
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        AutoDisTaskDal taskDal = ClassWrapper.wrapTrans(AutoDisTaskDal.class);
        AutoDisTaskEntity task = taskDal.get(1);
        SnmpAutoDiscover disc = new SnmpAutoDiscover();
        disc.discover(task, -1, "", false);
    }
}
