package jetsennet.jbmp.autodiscovery.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.entity.AutoDisObjEntity;

/**
 * @author liwei代码格式优化
 */
public class ThirdPartDisResultHandle extends AutoDisResultHandle
{
    private String key;
    private String proName;
    private static final Logger logger = Logger.getLogger(ThirdPartDisResultHandle.class);

    /**
     * @param collId 采集ID
     * @param key 参数
     * @param proName 参数
     */
    public ThirdPartDisResultHandle(int collId, String key, String proName)
    {
        super(-1, collId);
        this.key = key;
        this.proName = proName;
    }

    @Override
    public void handle(AutoDisResult result, int userId)
    {
        try
        {
            List<AutoDisObjEntity> oldObjs = adodal.getThirdPartObj(key, collId);
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

    @Override
    protected List<AutoDisObjEntity> toAutoDisObj(AutoDisResult result)
    {
        List<AutoDisObjEntity> retval = new ArrayList<AutoDisObjEntity>();
        List<SingleResult> irs = result.getIrs();
        for (SingleResult ir : irs)
        {
            try
            {
                AutoDisObjEntity ad = new AutoDisObjEntity();

                ProResult classpr = ir.getByPro(AutoDisConstant.PRO_NAME_CLASS);
                ad.setClassId(Integer.valueOf(classpr.getByKey(AutoDisConstant.CLASS_ID)));

                ProResult objpr = ir.getByPro(proName);
                ad.setIp(objpr.getByKey(AutoDisConstant.ID));
                ad.setObjName(objpr.getByKey(AutoDisConstant.NAME));
                // 暂时使用USER_NAME字段表示IP
                ad.setUserName(objpr.getByKey(AutoDisConstant.IP));

                ad.setCollId(collId);
                ad.setTaskId(taskId);
                ad.setField1(key);
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
