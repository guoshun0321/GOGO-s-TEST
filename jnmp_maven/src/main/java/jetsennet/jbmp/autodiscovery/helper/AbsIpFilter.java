/************************************************************************
日 期：2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: 过滤器
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;

/**
 * IP段过滤
 * @author 郭祥
 */
public abstract class AbsIpFilter
{

    /**
     * 过滤
     * @param irs 参数
     * @return 结果
     */
    public List<SingleResult> filter(List<SingleResult> irs)
    {
        return irs;
    }
}
