/************************************************************************
日 期：2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: SNMP自动发现
历 史:
 ************************************************************************/
package jetsennet.jnmp.autodiscovery;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AbsAutoDiscover;
import jetsennet.jbmp.autodiscovery.AutoDis;
import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.DiscoverException;
import jetsennet.jbmp.autodiscovery.helper.AbsAutoDisResultHandle;
import jetsennet.jbmp.autodiscovery.helper.AbsDiscover;
import jetsennet.jbmp.autodiscovery.helper.AbsIpFilter;
import jetsennet.jbmp.autodiscovery.helper.ArpIpFilter;
import jetsennet.jbmp.autodiscovery.helper.AutoDisResult;
import jetsennet.jbmp.autodiscovery.helper.AutoDisResultHandleWithPtl;
import jetsennet.jbmp.autodiscovery.helper.ClassDiscover;
import jetsennet.jbmp.autodiscovery.helper.NetworkDiscover;
import jetsennet.jbmp.entity.AutoDisTaskEntity;
import jetsennet.jnmp.autodiscovery.helper.FtpDiscover;

/**
 * SNMP自动发现
 * @author 郭祥
 */
public class FtpAutoDiscover extends AbsAutoDiscover
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AutoDis.class);

    /**
     * 构造函数
     */
    public FtpAutoDiscover()
    {
    }

    @Override
    public AutoDisResult discover(AutoDisTaskEntity task, int userId, String userName, boolean isLog) throws DiscoverException
    {
        AutoDisResult coll = null;
        try
        {
            coll = this.initResult(task);
            long begin = System.currentTimeMillis();

            // ARP
            NetworkDiscover nd = new NetworkDiscover();
            nd.find(coll);

            // FTP
            AbsIpFilter filter = new ArpIpFilter();
            AbsDiscover hd = new FtpDiscover();
            hd.setFilter(filter);
            hd.find(coll);

            // 类型
            AbsDiscover cd = new ClassDiscover(10092, "FTP");
            cd.find(coll);

            // 数据处理
            AbsAutoDisResultHandle handle = new AutoDisResultHandleWithPtl(task.getTaskId(), task.getCollId(), AutoDisConstant.PRO_NAME_FTP);
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
}
