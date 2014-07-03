package jetsennet.jbmp.autodiscovery.helper;

import jetsennet.jbmp.autodiscovery.DiscoverException;

/**
 * 网络设备扫描接口
 * @author GUO
 */
public interface IDiscover
{

    /**
     * 扫描指定IP段并返回扫描结果
     * @param coll 用于扫描的IP段
     * @return 扫描得到的结果
     * @throws DiscoverException 异常
     */
    public AutoDisResult find(AutoDisResult coll) throws DiscoverException;
}
