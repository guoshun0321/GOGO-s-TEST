package jetsennet.jbmp.ins.helper;

import java.util.ArrayList;
import java.util.Map;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;

import org.apache.log4j.Logger;

public class AttrsInsHarrisCard extends AbsAttrsIns
{

    private static final String HEALTH_EXP = "harris_card.health";

    private static final String HIGH_END = "harris_card.high";

    private static final String LOW_END = "harris_card.low";

    private static final String CARD_INFO_PREFIX = "1.3.6.1.4.1.3142.2.7.294.1.";

    private static final String CARD_INFO_APPEND_HEALTH = ".1.81.";

    private static final String CARD_INFO_APPEND_HIGH = ".1.82.";
    
    private static final String CARD_INFO_APPEND_LOW = ".1.83.";
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AttrsInsHarrisCard.class);

    public AttrsInsHarrisCard()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException
    {
        String slotStr = mo.getField1();
        String suffix = mo.getField2();
        int slot = Integer.valueOf(slotStr) + 30;

        ArrayList<AttributeEntity> attrs = result.getInputAttrs();
        for (AttributeEntity attr : attrs)
        {
            logger.debug("解析属性：" + attr.getAttribName());
            ObjAttribEntity oa = attr.genObjAttrib();
            String attribParam = oa.getAttribParam();

            String exp = null;
            if (HEALTH_EXP.equals(attribParam))
            {
                exp = "exp:(OID:(" + CARD_INFO_PREFIX + slot + CARD_INFO_APPEND_HEALTH + suffix + "))";
            }
            else if (HIGH_END.equals(attribParam))
            {
                exp = "exp:(OID:(" + CARD_INFO_PREFIX + slot + CARD_INFO_APPEND_HIGH + suffix + "))";
            }
            else if (LOW_END.equals(attribParam))
            {
                exp = "exp:(OID:(" + CARD_INFO_PREFIX + slot + CARD_INFO_APPEND_LOW + suffix + "))";
            }
            else
            {
                result.addErr(attr, "无法解析参数：" + attribParam);
                continue;
            }
            oa.setObjId(mo.getObjId());
            oa.setAttribParam(exp);
            result.addResult(attr, oa);
        }
        return result;
    }

}
