/************************************************************************
日 期：2012-2-22
作 者: 郭祥
版 本: v1.3
描 述: 自动发现结果处理，只处理包含某种协议的
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.dataaccess.AutoDisObjDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AutoDisObjEntity;

/**
 * @author 郭祥
 */
public class AutoDisResultHandleWithPtl extends AbsAutoDisResultHandle
{

    /**
     * 协议名称
     */
    protected String ptlName;
    protected AutoDisObjDal adodal;
    private static final Logger logger = Logger.getLogger(AutoDisResultHandleWithPtl.class);

    /**
     * 构造方法
     * @param taskId 参数
     * @param collId 参数
     * @param ptlName 参数
     */
    public AutoDisResultHandleWithPtl(int taskId, int collId, String ptlName)
    {
        super(taskId, collId);
        this.ptlName = ptlName;
        adodal = ClassWrapper.wrapTrans(AutoDisObjDal.class);
    }

    @Override
    public void handle(AutoDisResult result, int userId)
    {
        try
        {
            List<AutoDisObjEntity> oldObjs = adodal.getByTaskId(taskId);
            List<AutoDisObjEntity> newObjs = this.toAutoDisObj(result);
            List<AutoDisObjEntity> retval = this.compare(oldObjs, newObjs);
            adodal.insertOrUpdate(retval);
            this.ins(retval, userId);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 将采集到的数据转换成AutoDisObjEntity
     * @param result 参数
     * @return 结果
     */
    public List<AutoDisObjEntity> toAutoDisObj(AutoDisResult result)
    {
        List<AutoDisObjEntity> retval = new ArrayList<AutoDisObjEntity>();
        List<SingleResult> irs = result.getIrs();
        for (SingleResult ir : irs)
        {
            try
            {
                ProResult pr = ir.getByPro(this.ptlName);
                if (pr != null) // 判断是否包含某种协议
                {
                    AutoDisObjEntity ad = new AutoDisObjEntity();
                    String ip = ir.getSingleKey();
                    ad.setIp(ip);
                    ad.setObjName(ip);
                    ProResult classpr = ir.getByPro(AutoDisConstant.PRO_NAME_CLASS);
                    ad.setClassId(Integer.valueOf(classpr.getByKey(AutoDisConstant.CLASS_ID)));
                    ad.setCollId(collId);
                    ad.setTaskId(taskId);
                    ad.setObjStatus(AutoDisObjEntity.STATUS_NEW);
                    ad.setObjDesc(ir.toXml());
                    String port = pr.getByKey(AutoDisConstant.PORT);
                    if (port != null)
                    {
                        ad.setPort(port);
                    }
                    retval.add(ad);
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        return retval;
    }
}
