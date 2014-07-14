/************************************************************************
日 期：2012-2-20
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.AutoDisUtil;

/**
 * 使用ARP协议进行过滤
 * @author 郭祥
 */
public class ArpIpFilter extends AbsIpFilter
{

    /**
     * 构造方法
     */
    public ArpIpFilter()
    {
    }

    @Override
    public List<SingleResult> filter(List<SingleResult> irs)
    {
        List<SingleResult> retval = null;
        retval = AutoDisUtil.getIpResultByPro(irs, AutoDisConstant.PRO_NAME_ARP);
        return retval;
    }
}
