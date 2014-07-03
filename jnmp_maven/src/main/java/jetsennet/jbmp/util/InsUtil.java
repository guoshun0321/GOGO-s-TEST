/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 实例化工具类
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.SnmpObjTypeEntity;
import jetsennet.jbmp.formula.FormulaCache;
import jetsennet.jbmp.formula.FormulaConstants;
import jetsennet.jbmp.formula.FormulaElement;
import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.CalcFormulaEntity;
import jetsennet.jbmp.ins.helper.AttrsInsResult;

/**
 * 实例化工具类
 * @author 郭祥
 */
public class InsUtil
{

    private static final Logger logger = Logger.getLogger(InsUtil.class);

    /**
     * 公式里面表格型OID加上索引
     * @param param 参数
     * @param index OID中索引部分
     * @return 结果
     */
    public static String addIndexToColumn(String param, String index)
    {
        FormulaElement fe = FormulaCache.getInstance().getFormula(param);
        CalcFormula cf = fe.getCalcFormula();
        StringBuilder sb = new StringBuilder();
        for (CalcFormulaEntity cfe : cf.getFormula())
        {
            if (cfe.type == FormulaConstants.OID_STRING)
            {
                String temp = cfe.value;
                if (temp.endsWith(".0)"))
                {
                    sb.append(temp);
                }
                else
                {
                    temp = temp.substring(0, temp.length() - 1);
                    sb.append(temp);
                    sb.append(".");
                    sb.append(index);
                    sb.append(")");
                }
            }
            else
            {
                sb.append(cfe.value);
            }
        }
        return sb.toString();
    }

    /**
     * 合并实例化结果
     * @param insResults 参数
     * @return 结果
     */
    public static AttrsInsResult mergeResult(ArrayList<AttrsInsResult> insResults)
    {
        AttrsInsResult retval = new AttrsInsResult();
        if (insResults == null || insResults.isEmpty())
        {
            return retval;
        }
        for (AttrsInsResult insResult : insResults)
        {
            if (insResult == null)
            {
                continue;
            }
            retval.getInputAttrs().addAll(insResult.getInputAttrs());
            retval.getId2attrs().putAll(insResult.getId2attrs());
            retval.getOutput().addAll(insResult.getOutput());
            retval.getInsResult().putAll(insResult.getInsResult());
            retval.getErrResult().putAll(insResult.getErrResult());
        }
        return retval;
    }

    /**
     * 验证
     * @param sots 判断条件集合
     * @param key 键
     * @param value 值
     * @return 结果
     */
    public static boolean validate(List<SnmpObjTypeEntity> sots, String key, String value)
    {
        if (sots == null || sots.isEmpty())
        {
            return false;
        }
        for (SnmpObjTypeEntity sot : sots)
        {
            if (sot != null)
            {
                if (sot.getSnmpSysoid().equals(key))
                {
                    boolean isMatch = validate(sot, value);
                    if (isMatch)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断值是否满足条件
     * @param type 类型
     * @param value 值
     * @return 结果
     */
    public static boolean validate(SnmpObjTypeEntity type, String value)
    {
        boolean retval = false;
        if (type != null || value != null)
        {
            String condition = type.getCondition();
            String snmpValue = type.getSnmpValue();
            snmpValue = snmpValue == null ? "" : snmpValue;
            logger.debug("比较条件：" + condition + " <" + snmpValue + ">;比较值：" + value);
            if (condition.equals(SnmpObjTypeEntity.CONDITION_EX))
            {
                retval = true;
            }
            else if (condition.equals(SnmpObjTypeEntity.CONDITION_EQ))
            {
                if (value != null && value.equals(snmpValue))
                {
                    retval = true;
                }
            }
            else if (condition.equals(SnmpObjTypeEntity.CONDITION_LIKE))
            {
                if (value != null && value.contains(snmpValue))
                {
                    retval = true;
                }
            }
            else if (condition.equals(SnmpObjTypeEntity.CONDITION_IN))
            {
                String[] values = snmpValue.split(",");
                for (String tvalue : values)
                {
                    if (tvalue.equals(value))
                    {
                        retval = true;
                        break;
                    }
                }
            }
        }
        return retval;
    }

    /**
     * 确定自动实例化时需要的属性
     * @param classId 参数
     * @return 结果
     */
    public static List<AttributeEntity> ensureInsAttr(int classId)
    {
        List<AttributeEntity> retval = null;
        try
        {
            AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
            retval = acdal.getAutoInsAttrib(classId, null);
            AttributeDal adal = ClassWrapper.wrapTrans(AttributeDal.class);
            retval.add(adal.get(BMPConstants.ON_OFF_ATTRIB_ID));
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }
}
