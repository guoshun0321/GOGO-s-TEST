/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: OID相关工具类
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.formula.FormulaCache;
import jetsennet.jbmp.formula.FormulaException;
import jetsennet.jbmp.ins.InsConstants;

/**
 * OID相关工具
 * @author 郭祥
 */
public class OIDUtil
{

    private static final Logger logger = Logger.getLogger(OIDUtil.class);

    /**
     * 计算索引的添加字段
     * @param input 输入
     * @param index 位置
     * @return 结果
     */
    public static String getIndex(String input, String index)
    {
        return input.substring(index.length() + 1);
    }

    /**
     * @param oid 参数
     * @return 结果
     */
    public static String OID2OIDStr(String oid)
    {
        if (oid == null)
        {
            throw new NullPointerException();
        }
        String retval = oid;
        if (!oid.startsWith("OID:("))
        {
            retval = "OID:(" + oid + ")";
        }
        return retval;
    }

    /**
     * @param oidStr 参数
     * @return 结果
     */
    public static String OIDStr2OID(String oidStr)
    {
        if (oidStr == null)
        {
            throw new NullPointerException();
        }
        String retval = oidStr;
        if (oidStr.startsWith("OID:("))
        {
            retval = oidStr.substring(5, oidStr.length() - 1);
        }
        return retval;
    }

    /**
     * 判断OID是否标量
     * @param oid 参数
     * @return 结果
     */
    public static boolean isScalar(String oid)
    {
        if (oid != null && oid.endsWith(".0"))
        {
            return true;
        }
        return false;
    }

    /**
     * 获取上一级的OID
     * @param oid 参数
     * @return 结果
     */
    public static String getSuperiorOid(String oid)
    {
        if (oid == null || "".equals(oid.trim()) || oid.split("\\.").length < 2)
        {
            return null;
        }
        oid = oid.substring(0, oid.lastIndexOf("."));
        return oid;
    }

    /**
     * 获取OID的深度 xxx.xxxx.xxxx.xxxx 的深度为4
     * @param oid 参数
     * @return 结果
     */
    public static int getOidDepth(String oid)
    {
        if (oid == null || "".equals(oid.trim()))
        {
            return -1;
        }
        return oid.split("\\.").length;
    }

    /**
     * 获取OID中最后一个节点后面的数字
     * @param oid 参数
     * @return 结果
     */
    public static int getLast(String oid)
    {
        int last = oid.lastIndexOf(".");
        return Integer.valueOf(oid.substring(last + 1));
    }

    /**
     * 获取属性表达式中的oid
     * @param oas 参数
     * @return 结果
     */
    public static String[] getOIDs(ArrayList<ObjAttribEntity> oas)
    {
        String regex = InsConstants.OID;
        Pattern pattern = Pattern.compile(regex);
        Set<String> tempOids = new HashSet<String>();
        for (ObjAttribEntity oa : oas)
        {
            String expression = oa.getAttribParam();
            if (expression == null)
            {
                continue;
            }
            Matcher matcher = pattern.matcher(expression);
            while (matcher.find())
            {
                String temp = matcher.group(1);
                if (!tempOids.contains(temp))
                {
                    tempOids.add(matcher.group(1));
                }
            }

        }
        return tempOids.toArray(new String[tempOids.size()]);
    }

    /**
     * 处理单个节点获得OID数组 不处理差分节点
     * @param oae 参数
     * @return 结果
     */
    public static String[] getOIDD(ObjAttribEntity oae)
    {
        String reg = oae.getAttribParam();
        if (null == reg || "".equals(reg.trim()) || reg.contains("dif"))
        {
            return null;
        }
        try
        {
            FormulaCache fc = FormulaCache.getInstance();
            return fc.getFormula(reg).getOids();
        }
        catch (FormulaException e)
        {
            logger.error("", e);
            return null;
        }
    }

    /**
     * @param oae 参数
     * @return 结果
     */
    public static String[] getOIDs(ObjAttribEntity oae)
    {
        String regex = InsConstants.OID;
        Pattern pattern = Pattern.compile(regex);
        Set<String> tempOids = new HashSet<String>();

        String expression = oae.getAttribParam();
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find())
        {
            String temp = matcher.group(1);
            if (!tempOids.contains(temp))
            {
                tempOids.add(matcher.group(1));
            }
        }
        return tempOids.toArray(new String[tempOids.size()]);
    }

    /**
     * 获取单个监控对象的OID
     * @param oa 对象
     * @return 结果
     */
    public static String[] getOID(ObjAttribEntity oa)
    {
        String regex = InsConstants.OID;
        Pattern pattern = Pattern.compile(regex);
        Set<String> tempOids = new HashSet<String>();

        String expression = oa.getAttribParam();
        if (expression == null)
        {
            return null;
        }
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find())
        {
            String temp = matcher.group(1);
            if (!tempOids.contains(temp))
            {
                tempOids.add(matcher.group(1));
            }
        }
        return tempOids.toArray(new String[tempOids.size()]);
    }
}
