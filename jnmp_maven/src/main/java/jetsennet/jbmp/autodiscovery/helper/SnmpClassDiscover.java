/************************************************************************
日 期: 2012-1-31
作 者: 郭祥
版 本: v1.3
描 述: 发现设备类型
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.DiscoverException;
import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.InsUtil;

/**
 * 发现设备类型
 * @author 郭祥
 */
public class SnmpClassDiscover extends AbsDiscover
{

    private AttribClassDal acdal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpClassDiscover.class);

    /**
     * 构造方法
     */
    public SnmpClassDiscover()
    {
        acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
    }

    @Override
    public AutoDisResult find(AutoDisResult coll, List<SingleResult> irs) throws DiscoverException
    {
        Map<AttribClassEntity, List<SnmpObjTypeEntity>> idents = acdal.getIdentifiableClass();
        for (SingleResult ir : irs)
        {
            logger.debug("开始处理：" + ir.getSingleKey());
            ProResult snmpRes = ir.getByPro(AutoDisConstant.PRO_NAME_SNMP);
            if (snmpRes == null)
            {
                this.setTypeCommen(ir);
            }
            else
            {
                this.setTypeSnmp(ir, snmpRes, idents);
            }
        }
        return coll;
    }

    /**
     * @param ir 参数
     */
    private void setTypeCommen(SingleResult ir)
    {
        ProResult pr = new ProResult(AutoDisConstant.PRO_NAME_CLASS);
        pr.addResult(AutoDisConstant.CLASS_ID, BMPConstants.CLASS_ID_UNKNOWN);
        pr.addResult(AutoDisConstant.CLASS_TYPE, "UNKOWN");
        ir.addProResult(pr);
    }

    /**
     * @param ir 参数
     * @param snmpRes 参数
     * @param idents 参数
     */
    private void setTypeSnmp(SingleResult ir, ProResult snmpRes, Map<AttribClassEntity, List<SnmpObjTypeEntity>> idents)
    {
        AttribClassEntity ac = this.ensureClassType(snmpRes, idents);
        if (ac == null)
        {
            this.setTypeCommen(ir);
        }
        else
        {
            ProResult pr = new ProResult(AutoDisConstant.PRO_NAME_CLASS);
            pr.addResult(AutoDisConstant.CLASS_ID, ac.getClassId());
            pr.addResult(AutoDisConstant.CLASS_TYPE, ac.getClassType());
            ir.addProResult(pr);
        }
    }

    private AttribClassEntity ensureClassType(ProResult snmpRes, Map<AttribClassEntity, List<SnmpObjTypeEntity>> idents)
    {
        if (idents == null || idents.isEmpty())
        {
            return null;
        }

        Set<AttribClassEntity> acs = idents.keySet();
        for (AttribClassEntity ac : acs)
        {
            logger.debug("匹配类型：" + ac.getClassName());
            List<SnmpObjTypeEntity> sots = idents.get(ac);
            if (sots == null || sots.isEmpty())
            {
                continue;
            }
            for (SnmpObjTypeEntity sot : sots)
            {
                String value = snmpRes.getByKey(sot.getSnmpSysoid()); // 取值
                if (InsUtil.validate(sot, value))
                {
                    logger.debug("匹配成功。");
                    return ac;
                }
            }
        }
        return null;
    }
}
