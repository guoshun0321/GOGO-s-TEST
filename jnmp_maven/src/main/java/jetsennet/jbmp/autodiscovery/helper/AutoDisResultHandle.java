/************************************************************************
日 期：2012-2-22
作 者: 郭祥
版 本: v1.3
描 述: 通用自动发现结果处理
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
 * 通用自动发现结果处理
 * @author 郭祥
 */
public class AutoDisResultHandle extends AbsAutoDisResultHandle
{

    protected AutoDisObjDal adodal;
    private static final Logger logger = Logger.getLogger(AutoDisResultHandle.class);

    /**
     * @param taskId 任务ID
     * @param collId 采集器ID
     */
    public AutoDisResultHandle(int taskId, int collId)
    {
        super(taskId, collId);
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
     * @param result
     * @return
     */
    protected List<AutoDisObjEntity> toAutoDisObj(AutoDisResult result)
    {
        List<AutoDisObjEntity> retval = new ArrayList<AutoDisObjEntity>();
        List<SingleResult> irs = result.getIrs();
        for (SingleResult ir : irs)
        {
            try
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
                retval.add(ad);
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        return retval;
    }
}
