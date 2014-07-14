/************************************************************************
日 期：2012-2-23
作 者: 郭祥
版 本: v1.3
描 述: 默认类型发现类，全部类型设置为未知
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.DiscoverException;
import jetsennet.jbmp.util.BMPConstants;

/**
 * 默认类型发现类，传入分类ID和分类名称。传入值非法时，类型设置为未知。
 * @author 郭祥
 */
public class ClassDiscover extends AbsDiscover
{

    /**
     * 分类ID
     */
    private int classId;
    /**
     * 分类名称
     */
    private String className;

    /**
     * @param classId 参数
     * @param className 参数
     */
    public ClassDiscover(int classId, String className)
    {

        this.classId = classId;
        this.className = className;
        if (this.classId <= 0 || this.className == null || this.className.trim().isEmpty())
        {
            this.classId = BMPConstants.CLASS_ID_UNKNOWN;
            this.className = "UNKOWN";
        }
    }

    @Override
    public AutoDisResult find(AutoDisResult coll, List<SingleResult> irs) throws DiscoverException
    {
        for (SingleResult ir : irs)
        {
            ProResult pr = new ProResult(AutoDisConstant.PRO_NAME_CLASS);
            // 未知
            pr.addResult(AutoDisConstant.CLASS_ID, classId);
            pr.addResult(AutoDisConstant.CLASS_TYPE, className);

            ir.addProResult(pr);
        }
        return coll;
    }
}
