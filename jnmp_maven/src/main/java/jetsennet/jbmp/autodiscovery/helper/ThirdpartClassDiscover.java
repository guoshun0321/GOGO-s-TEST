/************************************************************************
日 期：2012-2-23
作 者: 郭祥
版 本: v1.3
描 述: 默认类型发现类，全部类型设置为未知
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.DiscoverException;
import jetsennet.jbmp.dataaccess.buffer.AttribClassBuffer;
import jetsennet.jbmp.entity.AttribClassEntity;

/**
 * 第三方类型发现类
 * @author 郭祥
 */
public class ThirdpartClassDiscover extends AbsDiscover
{

    /**
     * 包含ClassID的协议的名称
     */
    private String proName;

    private static final Logger logger = Logger.getLogger(ThirdpartClassDiscover.class);

    /**
     * @param proName 参数
     */
    public ThirdpartClassDiscover(String proName)
    {
        this.proName = proName;
    }

    @Override
    public AutoDisResult find(AutoDisResult coll, List<SingleResult> irs) throws DiscoverException
    {
        AttribClassBuffer buffer = new AttribClassBuffer();
        for (SingleResult ir : irs)
        {
            try
            {
                ProResult clspr = ir.getByPro(proName);
                String clsStr = clspr.getByKey(AutoDisConstant.CLASS_ID);
                int classId = Integer.valueOf(clsStr);
                AttribClassEntity ac = buffer.get(classId);

                if (ac != null)
                {
                    String className = buffer.get(classId).getClassType();
                    ProResult pr = new ProResult(AutoDisConstant.PRO_NAME_CLASS);
                    pr.addResult(AutoDisConstant.CLASS_ID, classId);
                    pr.addResult(AutoDisConstant.CLASS_TYPE, className);
                    ir.addProResult(pr);
                }
                else
                {
                    logger.error("找不到CLASS_ID为" + classId + "的类型。");
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        return coll;
    }
}
