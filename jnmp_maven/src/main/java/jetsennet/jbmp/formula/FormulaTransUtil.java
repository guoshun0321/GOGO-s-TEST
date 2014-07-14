package jetsennet.jbmp.formula;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.ConvertException;
import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.CalcFormulaEntity;
import jetsennet.jbmp.protocols.snmp.SnmpResult;
import jetsennet.jbmp.protocols.snmp.SnmpResult.SnmpResultColl;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.jbmp.util.OIDUtil;

/**
 * 公式转换工具类 公式分为4种形态：<br/> 
 * 1、原始公式形态(ori)，例如：exp:(param1+param2);name:(name1)<br/>
 * 2、原始OID公式形态(orioid)，例如：exp:(OID:(1.3.6)+OID:(1.3.6));name:(OID:(1.3.6))<br/> 
 * 3、最终OID公式形态(finaloid)，例如：exp:(OID:(1.3.6.0)+OID:(1.3.6.1));name:(OID:(1.3.6.0))<br/> 
 * 4、最终结果公式形态(final)，例如：exp:(1+2)<br/>
 * 4种形态从1到4转换。除了2到3的转化为1对N外，其他均为1对1
 * @author 郭祥
 */
public class FormulaTransUtil
{

    public static Logger logger = Logger.getLogger(FormulaTransUtil.class);

    /**
     * 原始公式转换成原始OID公式 产生异常是返回NULL
     * @param ori 参数
     * @param mibId 参数
     * @return 结果
     */
    public static String ori2orioid(String ori, int mibId)
    {
        FormulaTrans trans = new FormulaTrans();
        try
        {
            trans.transform(ori, mibId, false);
        }
        catch (Exception ex)
        {
            throw new ConvertException(ex);
        }
        return trans.getOutput();
    }

    /**
     * 最终公式转换成最终结果公式
     * @param finalOid 参数
     * @param map 参数
     * @return 结果
     * @throws ConvertException 异常
     */
    public static String finaloid2final(String finalOid, Map<String, Object> map) throws ConvertException
    {
        if (map == null)
        {
            throw new ConvertException("传入转换参数map为null");
        }
        StringBuilder sb = new StringBuilder();
        FormulaElement fe = getFormulaElement(finalOid);
        CalcFormula cf = fe.getCalcFormula();
        for (CalcFormulaEntity cfe : cf.getFormula())
        {
            if (cfe.type == FormulaConstants.OID_STRING)
            {
                String oid = cfe.value;
                Object obj = map.get(oid);
                if (obj != null)
                {
                    sb.append(obj.toString());
                }
                else
                {
                    throw new ConvertException(String.format("找不到OID<%s>对应的值", oid));
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
     * 解析公式
     * @param param 参数
     * @return 结果
     * @throws ConvertException 异常
     */
    public static FormulaElement getFormulaElement(String param) throws ConvertException
    {
        FormulaElement retval = null;
        try
        {
            retval = FormulaCache.getInstance().getFormula(param);
        }
        catch (Exception ex)
        {
            throw new ConvertException(ex);
        }
        if (retval == null)
        {
            throw new ConvertException(String.format("无法解析公式<%s>。", param));
        }
        return retval;
    }

    /**
     * 将tomcat传递过来的公式转换成存储在数据库里面的公式
     * 
     * @param formula 公式
     * @param scanResult 扫描结果
     * @return 结果
     * @throws Exception 异常
     */
    public static String[] trans2Oid(String formula, SnmpResult scanResult) throws Exception
    {
        FormulaElement root = FormulaCache.getInstance().getFormula(formula);
        String[] oids = root.getOids();
        int size = ensureSize(scanResult, oids);
        Map<String, SnmpResultColl> map = scanResult.map;
        String[] retval = new String[size];
        for (int i = 0; i < size; i++)
        {
            StringBuilder sb = new StringBuilder();
            CalcFormula cf = root.getCalcFormula();
            for (CalcFormulaEntity cfe : cf.getFormula())
            {
                if (cfe.type == FormulaConstants.OID_STRING)
                {
                    String oid = OIDUtil.OIDStr2OID(cfe.value);
                    SnmpResultColl sr = map.get(oid);
                    if (sr != null)
                    {
                        if (sr.wrongStr != null)
                        {
                            throw new Exception(sr.wrongStr);
                        }
                        else if (sr.single != null)
                        {
                            sb.append(OIDUtil.OID2OIDStr(sr.single.transOid));
                        }
                        else if (sr.rs != null)
                        {
                            sb.append(OIDUtil.OID2OIDStr(sr.rs.get(i).transOid));
                        }
                    }
                }
                else
                {
                    sb.append(cfe.value);
                }
            }
            retval[i] = sb.toString();
        }
        return retval;
    }

    /**
     * 最终OID表达式转换成结果表达式
     * 
     * @param formula 参数
     * @param scanResult 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static String trans2Result(String formula, SnmpResult snmpR) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        FormulaElement fe = getFormulaElement(formula);
        CalcFormula cf = fe.getCalcFormula();

        // name表达式里面的OID需要用枚举值替换原始值
        boolean isName = false;
        boolean isExp = false;
        for (CalcFormulaEntity cfe : cf.getFormula())
        {
            if (cfe.type == FormulaConstants.OID_STRING)
            {
                String oid = OIDUtil.OIDStr2OID(cfe.value);
                String value = snmpR.getValue(oid, isName);
                if (value != null)
                {
                    sb.append(value);
                    if (isExp && !"0".equals(value))
                    {
                        sb.append("d");
                    }
                }
                else
                {
                    throw new ConvertException(String.format("找不到OID<%s>对应的值", oid));
                }
            }
            else if (cfe.type == FormulaConstants.STR_STRING)
            {
                sb.append(cfe.value.substring(1, cfe.value.length() - 1));
            }
            else if (cfe.type == FormulaConstants.NAME_HEAD)
            {
                isName = true;
                isExp = false;
                sb.append(cfe.value);
            }
            else if (cfe.type == FormulaConstants.EXP_HEAD)
            {
                isExp = true;
                sb.append(cfe.value);
            }
            else
            {
                sb.append(cfe.value);
            }
        }
        return sb.toString();
    }

    /**
     * 确定实例化后的对象属性的大小
     * @param scanResult 参数
     * @param oids 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static int ensureSize(SnmpResult scanResult, String[] oids) throws Exception
    {
        int size = -1;
        String sizeOid = "";
        for (String oid : oids)
        {
            SnmpResultColl sr = scanResult.map.get(oid);
            if (sr == null)
            {
                throw new Exception(String.format("%s对应的采集结果不存在", oid));
            }
            if (sr.rs != null)
            {
                if (size == -1)
                {
                    size = sr.rs.size();
                    sizeOid = oid;
                }
                else
                {
                    if (sr.rs.size() != size)
                    {
                        throw new Exception(String.format("OID<%s,%s>与OID<%s,%s>实例化的数量不相同", sizeOid, size, oid, sr.rs.size()));
                    }
                }
            }
        }
        size = size < 0 ? 1 : size;
        return size;
    }

    /**
     * 取出形式为AAAA;BBBBB或AAAA中的AAAA
     * @param fml 参数
     * @return 异常
     */
    public static String ensureExpStr(String fml)
    {
        if (fml == null)
        {
            return null;
        }
        String[] oriStr = fml.split(";");
        String fmlExp = oriStr[0];
        return fmlExp == null ? "" : fmlExp;
    }

    /**
     * 替换OID尾部
     * @param exp 参数
     * @param endOid 参数
     * @return 结果
     */
    public static String replaceEndOid(String exp, String endOid)
    {
        FormulaCache fcache = FormulaCache.getInstance();
        StringBuilder sb = new StringBuilder();
        try
        {
            FormulaElement root = fcache.getFormula(exp);
            CalcFormula calc = root.getCalcFormula();
            ArrayList<CalcFormulaEntity> formulas = calc.getFormula();
            for (CalcFormulaEntity entity : formulas)
            {
                String temp = entity.value;
                if (entity.type == FormulaConstants.OID_STRING)
                {
                    temp = temp.substring(5, temp.length() - 1);
                    if (!temp.endsWith(".0"))
                    {
                        String[] endArray = endOid.split("\\.");
                        String[] oidArray = temp.split("\\.");
                        int endLength = endArray.length;
                        int oidLength = oidArray.length;
                        if (oidLength >= endLength)
                        {
                            for (int i = endLength - 1, j = oidLength - 1; i >= 0; i--, j--)
                            {
                                oidArray[j] = endArray[i];
                            }
                            temp = ConvertUtil.arrayToString(oidArray, ".", false);
                        }
                    }
                    temp = "OID:(" + temp + ")";
                }
                sb.append(temp);
            }
        }
        catch (Exception ex)
        {
            logger.debug("", ex);
            return exp;
        }
        return sb.toString();
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        String s = "exp:(OID:(1.3.6.1.25.36.88.145))";
        System.out.println(s);
        System.out.println(FormulaTransUtil.replaceEndOid(s, "82.143"));

        s = "exp:(OID:(1.3.6.1.25.36.88.145.0))";
        System.out.println(s);
        System.out.println(FormulaTransUtil.replaceEndOid(s, "82.143"));
    }
}
