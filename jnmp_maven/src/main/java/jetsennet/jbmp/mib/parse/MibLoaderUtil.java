/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibLoaderLog;
import net.percederberg.mibble.MibTypeSymbol;
import net.percederberg.mibble.MibValueSymbol;

import org.apache.log4j.Logger;

/**
 * @author Guo
 */
public class MibLoaderUtil
{

    private static final Logger logger = Logger.getLogger(MibLoaderUtil.class);

    /**
     * 获取mib文件解析产生的异常
     * @param me 参数
     * @return 结果
     */
    public static String handleMibLoaderException(MibLoaderException me)
    {
        MibLoaderLog log = me.getLog();
        StringBuilder sb = new StringBuilder();
        sb.append("总共");
        sb.append(log.errorCount());
        sb.append("条异常信息：");
        sb.append("\n");
        Iterator it = log.entries();
        MibLoaderLog.LogEntry entry = null;
        int i = 1;
        while (it.hasNext())
        {
            entry = (MibLoaderLog.LogEntry) it.next();
            sb.append(i);
            sb.append(".");
            sb.append(entry.getMessage());
            sb.append("；行：");
            sb.append(entry.getLineNumber());
            sb.append("；列：");
            sb.append(entry.getColumnNumber());
            sb.append("\n");
            i++;
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 获取指定目录下面的所有文件
     * @param dir 参数
     * @return 结果
     */
    public static ArrayList<File> getDirFile(File dir)
    {
        ArrayList<File> result = new ArrayList<File>();
        ArrayList<File> stack = new ArrayList<File>();
        if (dir.exists())
        {
            if (dir.isDirectory())
            {
                stack.add(dir);
            }
            else
            {
                result.add(dir);
            }
        }
        while (!stack.isEmpty())
        {
            File tempDir = stack.get(0);
            File[] list = tempDir.listFiles();
            for (File f : list)
            {
                if (f.isFile())
                {
                    result.add(f);
                }
                else if (f.isDirectory())
                {
                    stack.add(f);
                }
            }
            stack.remove(0);
        }
        return result;
    }

    /**
     * @param oldArray 参数
     * @param newArray 参数
     * @return 结果
     */
    public static boolean equalArray(ArrayList<File> oldArray, ArrayList<File> newArray)
    {
        for (File f : newArray)
        {
            if (!oldArray.contains(f))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前oid的父oid
     * @param oid 参数
     * @return 结果
     */
    public static String parentOid(String oid)
    {
        if (oid == null)
        {
            return null;
        }
        int last = oid.lastIndexOf(".");
        if (last > 0)
        {
            return oid.substring(0, last);
        }
        else
        {
            return null;
        }
    }

    /**
     * 获取MIBS文件的储存路径
     * @return 结果
     */
    public static String getMibDirPath()
    {
        String path = System.getProperty("user.dir") + "\\MIBS";
        return path;
    }

    /**
     * 默认路径
     * @return 结果
     */
    public static String getCommonDirParh()
    {
        String path = getMibDirPath() + "\\COMMON";
        return path;
    }

    // <editor-fold defaultstate="collapsed" desc="节点类型判断">
    /**
     * 判断symbol是否为可管理对象
     * @param symbol 参数
     * @return 结果
     */
    public static boolean isOid(MibValueSymbol symbol)
    {
        String type = symbol.getType().getName();
        if ("OBJECT-TYPE".equals(type) || "OBJECT-IDENTITY".equals(type) || "OBJECT IDENTIFIER".equals(type) || "MODULE-IDENTITY".equals(type))
        {
            return true;
        }
        return false;
    }

    /**
     * 判断symbol是否为Trap
     * @param symbol 参数
     * @return 结果
     */
    public static boolean isTrap(MibValueSymbol symbol)
    {
        String type = symbol.getType().getName();
        if ("NOTIFICATION-TYPE".equals(type) || "TRAP-TYPE".equals(type))
        {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为合成类型
     * @param symbol 参数
     * @return 结果
     */
    public static boolean isComposeType(MibTypeSymbol symbol)
    {
        String type = symbol.getType().getName();
        if ("TEXTUAL-CONVENTION".equals(type))
        {
            return true;
        }
        return false;
    }
    // </editor-fold>
}
