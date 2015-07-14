/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.backup;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * @author Guo
 */
public class BackupUtil
{
    private static final Logger logger = Logger.getLogger(BackupUtil.class);

    /**
     * 获取备份时间
     * @param day 时间
     * @return 结果
     */
    public static Calendar getBackupTime(int day)
    {
        if (day > 0)
        {
            day = day * -1;
        }
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        ca.add(Calendar.DATE, day);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        return ca;
    }

    /**
     * @return 初始备份时间
     */
    public static Calendar getBeginTime()
    {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        ca.add(Calendar.DATE, 1);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        return ca;
    }

    /**
     * @param cla 类
     * @param methodName 方法名称
     * @param params 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static Object invoke(Class cla, String methodName, Object params) throws Exception
    {
        Method method = cla.getMethod(methodName, params.getClass());
        return method.invoke(null, params);
    }

    /**
     * 获取配置节点的值
     * @param elm 参数
     * @param key 键
     * @param defVal 默认值
     * @return 结果
     */
    public static String getValue(Element elm, String key, String defVal)
    {
        Element valElm = (Element) elm.selectSingleNode(key);
        if (valElm == null)
        {
            return defVal;
        }
        return valElm.getTextTrim();
    }

    /**
     * 读取备份模块信息
     * @return 结果
     */
    public static List getBackupCfgs()
    {
        Document doc = readDocument("backup");
        Element root = doc.getRootElement();
        return root.selectNodes("module");
    }

    /**
     * 读取配置文件
     */
    private static Document readDocument(String fileName)
    {
        URL file = XmlCfgUtil.class.getClassLoader().getResource(fileName + ".xml");
        SAXReader reader = new SAXReader();
        Document doc = null;
        try
        {
            doc = reader.read(file);
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
            logger.error("初始化失败," + fileName + ".xml" + "文件解析异常", e);
            return null;
        }
        return doc;
    }
}
