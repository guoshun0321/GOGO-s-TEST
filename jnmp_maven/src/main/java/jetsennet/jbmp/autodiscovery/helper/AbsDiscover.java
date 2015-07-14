/************************************************************************
日 期: 2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: 协议扫描接口
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AutoDisLogUtil;
import jetsennet.jbmp.autodiscovery.DiscoverException;

/**
 * 协议扫描接口
 * @author 郭祥
 */
public abstract class AbsDiscover
{

    /**
     * 数据库日志
     */
    protected AutoDisLogUtil dblog;
    /**
     * 过滤
     */
    protected AbsIpFilter filter;
    /**
     * 文件日志
     */
    private static final Logger logger = Logger.getLogger(AbsDiscover.class);

    /**
     * 协议扫描
     * @param coll 用于扫描的IP段
     * @return 扫描得到的结果
     * @throws DiscoverException 异常
     */
    public AutoDisResult find(AutoDisResult coll) throws DiscoverException
    {
        List<SingleResult> irs = coll.getIrs();
        if (filter != null)
        {
            irs = filter.filter(irs);
        }
        return this.find(coll, irs);
    }

    /**
     * 协议扫描
     * @param coll 结果集
     * @param irs 经过过滤处理后的IP
     * @return
     * @throws DiscoverException
     */
    protected AutoDisResult find(AutoDisResult coll, List<SingleResult> irs) throws DiscoverException
    {
        return null;
    }

    public void setFilter(AbsIpFilter filter)
    {
        this.filter = filter;
    }

    /**
     * @param dblog 参数
     * @return 结果
     */
    public AutoDisLogUtil setDbLog(AutoDisLogUtil dblog)
    {
        this.dblog = dblog;
        return dblog;
    }
}
