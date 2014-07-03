/************************************************************************
日 期: 2012-2-16
作 者: 郭祥
版 本: v1.3
描 述: MIB工具类
历 史:
 ************************************************************************/
package jetsennet.jbmp.mib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jetsennet.jbmp.dataaccess.AttribClassDal;
import jetsennet.jbmp.dataaccess.MibBankDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MibBanksEntity;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.mib.node.EditNode;
import jetsennet.jbmp.mib.parse.MibLoader;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.jbmp.util.FileUtil;
import jetsennet.jbmp.util.OIDComparator;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibLoaderLog;
import net.percederberg.mibble.MibTypeSymbol;
import net.percederberg.mibble.MibValueSymbol;

import org.apache.log4j.Logger;

/**
 * MIB工具类
 * @author 郭祥
 */
public class MibUtil
{

    private static final Logger logger = Logger.getLogger(MibUtil.class);

    /**
     * 解析MIB文件解析产生的异常
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
     * 根据OID排列对象
     * @return 结果
     */
    public static ArrayList<EditNode> sortMibs(ArrayList<EditNode> mibs)
    {
        ArrayList<EditNode> result = new ArrayList<EditNode>();
        EditNode[] temp = mibs.toArray(new EditNode[mibs.size()]);
        Arrays.sort(temp, new OIDComparator());
        result.addAll(Arrays.asList(temp));
        return result;
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

    /**
     * 获取MIBS文件的路径
     * @return 结果
     */
    public static String getMibDirPath()
    {
        String retval = null;
        try
        {
            retval = java.net.URLDecoder.decode(MibUtil.class.getClassLoader().getResource("../../").getPath(), "UTF-8");
            retval += "mib";
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 获取全部的MIB文件
     * @return 结果
     */
    public static List<File> getMibFiles()
    {
        String path = getMibDirPath();
        List<File> files = FileUtil.getFiles(path);
        return files;
    }

    /**
     * 确定使用的MIB库
     * @param objId 参数
     * @return 结果
     */
    public static int ensureMib(int objId)
    {
        int retval = BMPConstants.DEFAULT_MIB_NAME_ID;
        try
        {
            AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
            AttribClassEntity ac = acdal.getByObjId(objId);
            if (ac != null)
            {
                retval = ac.getMibId() <= 0 ? BMPConstants.DEFAULT_MIB_NAME_ID : ac.getMibId();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 确定使用的MIB库
     * 
     * @param classId 分类ID
     * @return 结果
     */
    public static int ensureMibByClassId(int classId)
    {
        int retval = BMPConstants.DEFAULT_MIB_NAME_ID;
        try
        {
            AttribClassDal acdal = ClassWrapper.wrapTrans(AttribClassDal.class);
            AttribClassEntity ac = acdal.get(classId);
            if (ac != null)
            {
                retval = ac.getMibId() <= 0 ? BMPConstants.DEFAULT_MIB_NAME_ID : ac.getMibId();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 获取MIB库已经加载和未加载的MIB文件
     * @param mibId 参数
     * @return 结果
     */
    public static String getMibFileInfo(int mibId)
    {
        StringBuilder unloadedBuilder = new StringBuilder();
        StringBuilder loadedBuilder = new StringBuilder();

        List<String> fileNameList = new ArrayList<String>();
        try
        {
            MibBankDal mbdal = ClassWrapper.wrapTrans(MibBankDal.class);
            MibBanksEntity bank = mbdal.get(mibId);
            if (bank != null)
            {
                String mibFile = bank.getMibFile();
                mibFile = mibFile == null ? "" : mibFile;
                String[] fileNames = mibFile.split(":");
                fileNameList = Arrays.asList(fileNames);
            }
            else
            {
                return "?";
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }

        List<File> files = getMibFiles();

        if (files == null || files.isEmpty())
        {
        }
        else
        {
            for (File file : files)
            {
                String fileName = file.getName();
                if (fileNameList.contains(fileName))
                {
                    loadedBuilder.append(fileName);
                    loadedBuilder.append(":");
                }
                else
                {
                    unloadedBuilder.append(fileName);
                    unloadedBuilder.append(":");
                }
            }
        }
        String loadedStr = loadedBuilder.length() > 0 ? loadedBuilder.deleteCharAt(loadedBuilder.length() - 1).toString() : loadedBuilder.toString();
        String unloadedStr =
            unloadedBuilder.length() > 0 ? unloadedBuilder.deleteCharAt(unloadedBuilder.length() - 1).toString() : unloadedBuilder.toString();
        return unloadedStr + "?" + loadedStr;
    }

    /**
     * 添加或更新MIB
     * @param mibId 参数
     * @param str 参数
     */
    public static void updateOrInsertMib(int mibId, String str)
    {
        if (str == null)
        {
            return;
        }
        MibBanksEntity bank = null;
        try
        {
            MibBankDal mbdal = ClassWrapper.wrapTrans(MibBankDal.class);
            bank = mbdal.get(mibId);
            if (bank == null)
            {
                return;
            }
            bank.setMibFile(str);
            mbdal.update(bank);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        String path = getMibDirPath();
        String[] fileNames = str.split(":");
        ArrayList<File> files = new ArrayList<File>();
        for (String fileName : fileNames)
        {
            if (!"".equals(fileName.trim()))
            {
                String filePath = path + "/" + fileName;
                File file = new File(filePath);
                if (file.exists() && file.isFile())
                {
                    files.add(file);
                }
            }
        }
        MibLoader.getInstance().loadAndUpdate(files.toArray(new File[0]), mibId);
    }

    /**
     * 生成用于传输的MIB文件列表
     * @return 结果
     */
    public static String getMibFileListString()
    {
        List<File> mibFiles = getMibFiles();
        List<String> fileNames = new ArrayList<String>();
        List<String> size = new ArrayList<String>();
        for (File mibFile : mibFiles)
        {
            fileNames.add(mibFile.getName());
            if (mibFile.exists())
            {
                size.add(Long.toString(mibFile.length()));
            }
        }
        List<List<String>> values = new ArrayList<List<String>>();
        values.add(fileNames);
        values.add(size);
        String[] names = { "FILE_NAME", "FILE_SIZE" };
        return ConvertUtil.wrapToTrans(names, values);
    }

    /**
     * 删除MIB文件
     * @param fileName 文件名
     * @return 结果
     */
    public static boolean deleteMibFile(String fileName)
    {
        List<File> mibFiles = getMibFiles();
        for (File mibFile : mibFiles)
        {
            if (mibFile.getName().equals(fileName))
            {
                return mibFile.delete();
            }
        }
        return false;
    }

    /**
     * 将trap转换成属性
     * @param trap 参数
     * @param ac 参数
     * @param user 参数
     * @param time 参数
     * @return 结果
     */
    public static AttributeEntity trapToAttr(TrapTableEntity trap, AttribClassEntity ac, String user, Date time)
    {
        if (trap == null)
        {
            return null;
        }
        AttributeEntity attr = new AttributeEntity();
        attr.setAttribName(trap.getDescName());
        attr.setAttribValue(trap.getTrapOid());
        attr.setDataEncoding(BMPConstants.DEFAULT_SNMP_CODING);
        attr.setClassType(ac.getClassType());
        attr.setAttribType(AttribClassEntity.CLASS_LEVEL_TRAP);
        attr.setAttribMode(10);
        attr.setAttribParam("");
        attr.setDataType(AttributeEntity.DATA_TYPE_UNKNOWN);
        attr.setAttribDesc(trap.getDescTxt());
        attr.setCollTimeSpan(0);
        attr.setIsVisible(1);
        attr.setViewType(AttributeEntity.VIEW_TYPE_LABEL);
        attr.setAlarmId(-1);
        attr.setCreateUser(user);
        attr.setCreateTime(time);
        return attr;
    }
}
