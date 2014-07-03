/************************************************************************
 日 期：2012-8-20
 作 者: 梁宏杰
 版 本: v1.3
 描 述: 
 历 史:
 ************************************************************************/
package jetsennet.jbmp.datacollect.collector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnsMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ScalarMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jbmp.exception.SnmpException;
import jetsennet.jbmp.formula.FormulaCache;
import jetsennet.jbmp.formula.FormulaConstants;
import jetsennet.jbmp.formula.FormulaElement;
import jetsennet.jbmp.formula.FormulaTransUtil;
import jetsennet.jbmp.formula.ex.CalcFormula;
import jetsennet.jbmp.formula.ex.CalcFormulaEntity;
import jetsennet.jbmp.formula.ex.TerminalSymbol;
import jetsennet.jbmp.formula.ex.TerminalSymbolList;
import jetsennet.jbmp.ins.InsConstants;
import jetsennet.jbmp.protocols.snmp.AbsSnmpPtl;
import jetsennet.jbmp.protocols.snmp.SnmpResult;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;
import jetsennet.jbmp.util.OIDUtil;
import jetsennet.jbmp.util.ThreeTuple;

import org.apache.log4j.Logger;
import org.mvel2.MVEL;
import org.snmp4j.smi.VariableBinding;

/**
 * SNMP采集器
 * @author 梁宏杰
 */
public class SNMPCollector extends AbsCollector
{

    /**
     * 连接信息
     */
    protected AbsSnmpPtl snmp;
    /**
     * 缓存表
     */
    private static final Map<Integer, Value> cacheMap = new HashMap<Integer, Value>();
    /**
     * 公式头部长度
     */
    private static final int FORMULA_HEAD_LENGTH = 5;
    /**
     * 0，不需要转换成double
     */
    private static final String ZERO = "0";
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SNMPCollector.class);

    /**
     * 构造函数
     */
    public SNMPCollector()
    {
    }

    @Override
    public void connect() throws CollectorException
    {
        try
        {
            snmp = AbsSnmpPtl.getInstance(mo.getVersion());
            snmp.init(mo.getIpAddr(), mo.getIpPort(), mo.getUserName());
            VariableBinding vb = snmp.getNext(CollConstants.SNMP_OID_PREFIX);
            if (vb == null)
            {
                close();
                throw new SnmpException("SNMP连接失败");
            }
        }
        catch (SnmpException e)
        {
            String msg = mo.getIpAddr() + ":" + mo.getIpPort() + "上的SNMP监控对象连接失败";
            logger.error(msg);
            throw new CollectorException(msg, e);
        }
    }

    /**
     * 采集
     */
    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        setTime(new Date());
        Map<String, VariableBinding> rs = getDatas(objAttrLst);

        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        for (ObjAttribEntity oa : objAttrLst)
        {
            String exp = parseFormula(oa, rs);
            if (exp == null)
            {
                continue;
            }
            Object temp = this.genCollData(oa, exp);

            // 判断性能数据的值是否>=0，舍弃<0的值
            if (temp != null && temp instanceof CollData)
            {
                CollData data = (CollData) temp;
                if (data.value != null && oa.getAttribType() == AttribClassEntity.CLASS_LEVEL_PERF)
                {
                    try
                    {
                        double tempValue = Double.parseDouble(data.value);
                        if (tempValue < 0)
                        {
                            temp = Double.NaN;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.warn("数据转换异常，值:" + data.value + "；属性名称：" + oa.getObjattrName(), e);
                        temp = Double.NaN;
                    }
                }
            }

            result.put(oa, temp);
        }
        generateFailedData(result, objAttrLst);
        return result;
    }

    /**
     * 采集数据
     * @param objAttrLst
     * @return
     */
    protected Map<String, VariableBinding> getDatas(List<ObjAttribEntity> objAttrLst)
    {
        Map<String, VariableBinding> rs = new HashMap<String, VariableBinding>();
        String[] oids = OIDUtil.getOIDs((ArrayList<ObjAttribEntity>) objAttrLst);
        if (oids != null && oids.length > 0)
        {
            try
            {
                Map<String, VariableBinding> varMap = snmp.snmpGet(oids);
                Set<String> recvOIDs = varMap.keySet();
                for (String recv : recvOIDs)
                {
                    String oid = InsConstants.OID_HEAD + ":(" + recv + ")";
                    rs.put(oid, varMap.get(recv));
                }
            }
            catch (SnmpException ex)
            {
                logger.error(ex.getMessage(), ex);
                return new HashMap<String, VariableBinding>();
            }
        }
        return rs;
    }

    /**
     * 解析表达式
     * @param oa
     * @param oid2Value
     * @return
     */
    protected String parseFormula(ObjAttribEntity oa, Map<String, VariableBinding> oid2Value)
    {
        String retval = null;

        try
        {
            String formulaStr = oa.getAttribParam();
            FormulaElement root = FormulaCache.getInstance().getFormula(formulaStr);
            CalcFormula calc = root.getCalcFormula();
            ArrayList<CalcFormulaEntity> calcEntityLst = calc.getFormula();
            StringBuilder sb = new StringBuilder();
            boolean needCalc = this.needCalc(formulaStr);
            for (CalcFormulaEntity calcEntity : calcEntityLst)
            {
                if (calcEntity.type == FormulaConstants.OID_STRING)
                {
                    String oidStr = calcEntity.value;
                    VariableBinding bind = oid2Value.get(oidStr);
                    if (bind == null)
                    {
                        logger.error("找不到" + oidStr + "对应的值，对象属性" + oa.getObjattrName() + "采集失败");
                        return null;
                    }
                    String str = SnmpValueTranser.getInstance().trans(bind, oa.getDataEncoding(), 200);
                    sb.append(str);
                    if (needCalc && !ZERO.equals(str))
                    {
                        sb.append("d");
                    }
                }
                else
                {
                    sb.append(calcEntity.value);
                }
            }
            retval = sb.toString();
        }
        catch (Exception ex)
        {
            retval = null;
        }
        return retval;
    }

    /**
     * 判断公式是否需要计算
     * @param str
     * @return
     */
    private boolean needCalc(String str)
    {
        if (str.startsWith("str:("))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * 生成采集数据
     * @param oa
     * @param exp
     * @return
     */
    protected Object genCollData(ObjAttribEntity oa, String exp)
    {
        Object retval = null;
        try
        {
            String oriExp = exp;
            exp = exp.substring(FORMULA_HEAD_LENGTH, exp.length() - 1);
            if (oriExp.startsWith(InsConstants.HEAD_STR))
            {
                retval = genCollData(oa, exp, CollData.DATATYPE_PERF);
            }
            else if (oriExp.startsWith(InsConstants.HEAD_DIF))
            {
                long newTime = time.getTime();
                double newValue = Double.valueOf(this.mvel(exp));

                // 从缓存中取出数据比较
                Value obj = cacheMap.get(oa.getObjAttrId());
                if (obj == null)
                {
                    // 缓存中没有数据时，插入新数据，并返回Double.NaN
                    cacheMap.put(oa.getObjAttrId(), new Value(newValue, newTime));
                    retval = Double.NaN;
                }
                else
                {
                    double oldValue = obj.value;
                    long oldTime = obj.time;

                    // 更新缓存中的数据
                    obj.value = newValue;
                    obj.time = newTime;

                    long seconds = TimeUnit.MILLISECONDS.toSeconds(newTime - oldTime);
                    seconds = seconds == 0 ? 1 : seconds;
                    double t = (newValue - oldValue) / seconds;
                    retval = genCollData(oa, String.valueOf(t), CollData.DATATYPE_PERF);
                }
            }
            else if (oriExp.startsWith(InsConstants.HEAD_CAL))
            {
                double t = Double.valueOf(this.mvel(exp));
                retval = genCollData(oa, String.valueOf(t), CollData.DATATYPE_PERF);
            }
        }
        catch (Exception ex)
        {
            logger.info(ex.getMessage());
            retval = Double.NaN;
        }
        return retval;
    }

    /**
     * 生成采集数据
     * @param oa
     * @param value
     * @param dataType
     * @return
     */
    private CollData genCollData(ObjAttribEntity oa, String value, int dataType)
    {
        CollData data = new CollData();
        data.objID = oa.getObjId();
        data.objAttrID = oa.getObjAttrId();
        data.attrID = oa.getAttribId();
        data.dataType = dataType;
        data.value = value;
        data.srcIP = mo.getIpAddr();
        data.time = time;
        return data;
    }

    @Override
    public void close()
    {
        AbsSnmpPtl.closeSnmp();
    }

    /**
     * 获取数据，用于SNMP实例化
     * 
     * @param msg 参数
     * @return 结果
     */
    public TransMsg collectForIns(TransMsg msg)
    {

        logger.debug("SNMP采集器：准备获取实例化数据。");
        try
        {
            // 确定对象
            MObjectEntity mo = msg.getMo();

            // 确定MIB库ID
            Object obj = msg.getRecInfo();
            if (obj instanceof SnmpResult)
            {
                SnmpResult snmpR = (SnmpResult) obj;
                // 扫描
                logger.debug("SNMP采集器：准备扫描数据。");
                snmpR.scan(mo.getIpAddr(), mo.getVersion(), mo.getIpPort(), mo.getUserName());

                // 组装数据
                logger.debug("SNMP采集器：准备组装数据。");
                this.assemble(msg, snmpR);
            }
            else
            {
                logger.error("SNMP采集器：实例化传入参数为NULL。");
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        logger.debug("SNMP采集器：获取实例化数据结束。");
        return msg;
    }

    /**
     * 组装数据
     * 
     * @param msg
     * @param snmpR
     */
    private void assemble(TransMsg msg, SnmpResult snmpR)
    {
        // 处理标量数据
        ScalarMsg scalarMsg = msg.getScalar();
        if (null != scalarMsg)
        {
            List<ObjAttribEntity> objAttrLst = scalarMsg.getInputs();
            for (ObjAttribEntity oa : objAttrLst)
            {
                ThreeTuple<String, String, String> temp = this.genResult(oa, snmpR, oa.getAttribParam(), -1);
                if (temp != null)
                {
                    String exp = temp.first;
                    if (exp == null || "".equals(exp))
                    {
                        exp = FormulaTransUtil.ensureExpStr(oa.getAttribParam());
                    }
                    if (exp != null && !"".equals(exp))
                    {
                        scalarMsg.addOutput(exp, temp.second, temp.third);
                    }
                    else
                    {
                        scalarMsg.addOutputE(null);
                    }
                }
                else
                {
                    String exp = null;
                    if (exp == null || "".equals(exp))
                    {
                        exp = FormulaTransUtil.ensureExpStr(oa.getAttribParam());
                    }
                    if (exp != null && !"".equals(exp))
                    {
                        scalarMsg.addOutput(exp, "", oa.getObjattrName());
                    }
                    else
                    {
                        scalarMsg.addOutputE(null);
                    }
                }
            }
        }

        // 处理表格数据
        ColumnsMsg columnsMsg = msg.getColumns();
        if (null != columnsMsg)
        {
            List<ColumnMsg> cols = columnsMsg.getColumns();
            if (cols != null)
            {
                for (ColumnMsg col : cols)
                {
                    try
                    {
                        ObjAttribEntity oa = col.getInput();
                        String[] oriFmls = FormulaTransUtil.trans2Oid(oa.getAttribParam(), snmpR);
                        for (int i = 0; i < oriFmls.length; i++)
                        {
                            String oriFml = oriFmls[i];
                            ThreeTuple<String, String, String> temp = this.genResult(oa, snmpR, oriFml, i);
                            if (temp != null)
                            {
                                col.addOutput(temp.first, temp.second, temp.third);
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        logger.error("", ex);
                        col.clearOutput();
                    }
                }
            }
        }
    }

    /**
     * 生成最后结果
     * @param oa 对象属性
     * @param snmpR 结果集
     * @param formula 需要被转换的公式
     * @param index 参数
     * @return 第一个参数是最终表达式，第二个参数是最终值，第三个参数是最终名称。计算失败时，返回null
     */
    public ThreeTuple<String, String, String> genResult(ObjAttribEntity oa, SnmpResult snmpR, String formula, int index)
    {
        ThreeTuple<String, String, String> retval = null;
        try
        {
            logger.debug("原始表达式：" + formula);
            String[] oriStr = formula.split(";");

            // 最终表达式
            String fmlExp = oriStr[0];
            fmlExp = fmlExp == null ? "" : fmlExp;

            // 值表达式（包括名称）
            String temp = FormulaTransUtil.trans2Result(formula, snmpR);
            logger.debug("转换后表达式：" + temp);

            // 最终值
            String[] transStr = temp.split(";");
            String transExp = transStr[0];
            String transExpRlt = this.cal(transExp);
            logger.debug("转换后表达式的值：" + transExpRlt);

            // 最终名称
            String transName = null;
            if (transStr != null && transStr.length == 2)
            {
                transName = transStr[1];
                transName = this.genName(transName, oa.getObjattrName());
            }
            else
            {
                if (index >= 0)
                {
                    transName = oa.getObjattrName() + (index + 1);
                }
                else
                {
                    transName = oa.getObjattrName();
                }
            }
            retval = new ThreeTuple<String, String, String>(fmlExp, transExpRlt, transName);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 计算采集结果。只计算exp和str开头的公式，dif开头的公式或计算出错时返回""。
     * exp开头的公式考虑到MVEL计算时整数溢出的情况，需要将除0外的其他数字(123)转换成123d。
     * 
     * @param exp 参数
     * @return 结果
     */
    public String cal(String exp)
    {
        String retval = null;

        if (exp != null)
        {
            if (exp.startsWith("exp:("))
            {
                try
                {
                    //                    exp = this.mvelInt2double(exp);
                    exp = exp.substring(FORMULA_HEAD_LENGTH, exp.length() - 1);
                    retval = this.mvel(exp);
                }
                catch (Exception ex)
                {
                    logger.debug("", ex);
                    retval = null;
                }
            }
            else if (exp.startsWith("str:("))
            {
                retval = exp.substring(5, exp.length() - 1);
            }
        }
        retval = retval == null ? "" : retval;
        return retval;
    }

    /**
     * 将公式中的数字(除0外)，替换成"数字+d"的形式
     * @return
     */
    private String mvelInt2double(String exp)
    {
        FormulaElement root = FormulaCache.getInstance().getFormula(exp);
        TerminalSymbolList tSymbolList = root.getList();
        ArrayList<TerminalSymbol> symbols = tSymbolList.getSymbols();
        StringBuilder sb = new StringBuilder();
        for (TerminalSymbol symbol : symbols)
        {
            if (symbol.getType() == FormulaConstants.NUM && !ZERO.equals(symbol.getStr()))
            {
                sb.append(symbol.getStr() + "d");
            }
            else
            {
                sb.append(symbol.getStr());
            }
        }
        return sb.toString();
    }

    /**
     * 四则运算
     * @param formula
     * @return
     * @throws CollectorException
     */
    private String mvel(String formula) throws Exception
    {
        try
        {
            logger.debug("计算：" + formula);
            // 这里用BigDecimal是因为Double转换成String时，如果数据比较大会使用科学计数法表示
            BigDecimal bValue = new BigDecimal(MVEL.eval(formula).toString());

            // 这里将不带小数部分的值当整形处理，主要是避免枚举值被转换成double值
            // 这样在后面比较的时候不方便
            long lValue = bValue.longValue();
            double dValue = bValue.doubleValue();
            String str = null;
            if (dValue == lValue)
            {
                str = Long.toString(lValue);
            }
            else
            {
                str = bValue.toPlainString();
            }
            return str;
        }
        catch (Exception ex)
        {
            throw new Exception(formula + "计算错误");
        }
    }

    /**
     * 生成名称
     * @param nameFml 参数
     * @param oaName 参数
     * @return 结果
     */
    public String genName(String nameFml, String oaName)
    {
        if (nameFml != null && nameFml.startsWith("name:("))
        {
            nameFml = nameFml.substring(6, nameFml.length() - 1);
            return nameFml;
        }
        return oaName;
    }

    private class Value
    {

        public double value;
        public long time;

        public Value(double value, long time)
        {
            super();
            this.value = value;
            this.time = time;
        }
    }

    public static void main(String[] args) throws Exception
    {
        SNMPCollector snmp = new SNMPCollector();
        String cal = "exp:(24669*65536/1024/1024)";
        cal = "exp:(0/0*100)";
        String value = snmp.cal(cal);
        System.out.println(value);
    }
}
