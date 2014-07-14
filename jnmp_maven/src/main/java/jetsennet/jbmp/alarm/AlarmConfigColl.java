/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 报警配置alarm.xml
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.exception.ConfigureException;
import jetsennet.jbmp.util.ConfigFileUtil;
import jetsennet.jbmp.util.JdomParseUtil;
import jetsennet.jbmp.util.TwoTuple;

/**
 * 报警配置
 * @author 郭祥
 */
public final class AlarmConfigColl
{

    private ArrayList<AlarmConfig> configs;
    private static final Logger logger = Logger.getLogger(AlarmConfigColl.class);
    // 单例
    private static AlarmConfigColl instance = new AlarmConfigColl();

    private AlarmConfigColl()
    {
        configs = new ArrayList<AlarmConfig>();
        this.parse();
    }

    public static AlarmConfigColl getInstance()
    {
        return instance;
    }

    /**
     * 根据采集到的数据，找到相应的报警配置
     * @param data 参数
     * @return 报警配置。找不到时返回NULL
     */
    public AlarmConfig get(CollData data)
    {
        for (int i = 0; i < configs.size(); i++)
        {
            AlarmConfig config = configs.get(i);
            if (config.match(data.dataType, data.params))
            {
                return config;
            }
        }
        return null;
    }

    /**
     * @param ac 参数
     */
    public void put(AlarmConfig ac)
    {
        configs.add(ac);
    }

    /**
     * 清除
     */
    public void clear()
    {
        configs.clear();
    }

    public ArrayList<AlarmConfig> getAll()
    {
        return configs;
    }

    /**
     * 解析配置文件
     */
    private void parse()
    {
        SAXBuilder sax = new SAXBuilder();
        this.clear();
        ArrayList<String> poolNames = new ArrayList<String>();
        InputStream io = null;
        try
        {
            io = ConfigFileUtil.getAlarmConfigFile();
            Document doc = sax.build(io);
            Element root = doc.getRootElement();
            Element acs = root.getChild("alarmConfigs");
            if (acs == null)
            {
                return;
            }
            List<Element> children = acs.getChildren("alarmConfig");
            if (children != null)
            {
                for (Element child : children)
                {
                    // alarmConfig节点
                    AlarmConfig ac = null;
                    ac = new AlarmConfig();
                    ac.setName(JdomParseUtil.getElementString(child, "name", "name", false));
                    ac.setTypes(JdomParseUtil.getElementIntArray(child, "type", false));
                    String poolName = JdomParseUtil.getElementString(child, "poolName", null, false);
                    if (poolNames.contains(poolName))
                    {
                        throw new ConfigureException("配置模块：线程池：" + poolName + " 具有和之前配置相同名称的线程池。");
                    }
                    else
                    {
                        ac.setPoolName(poolName);
                    }
                    ac.setPoolSize(JdomParseUtil.getElementInt(child, "poolSize", 1, false));
                    ac.setAnalysisClass(JdomParseUtil.getElementString(child, "analysisClass", null, false));
                    ac.setCoding(JdomParseUtil.getElementString(child, "coding", null, false));
                    ac.setEventHandleClass(JdomParseUtil.getElementString(child, "eventHandleClass", null, false));
                    ac.setMatch(this.getElementAttrKeyAndValue(child, "match", false));
                    this.put(ac);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new ConfigureException("配置模块：解析报警配置文件出错。", ex);
        }
        finally
        {
            if (io != null)
            {
                try
                {
                    io.close();
                }
                catch (IOException ex)
                {
                    logger.error(ex);
                }
                finally
                {
                    io = null;
                }
            }
        }
        // logger.debug(configs);
    }

    private TwoTuple<String, String> getElementAttrKeyAndValue(Element el, String name, boolean isThrow)
    {
        TwoTuple<String, String> retval = null;
        Element child = el.getChild(name);
        if (child != null)
        {
            Attribute keyA = child.getAttribute("key");
            Attribute valueA = child.getAttribute("value");
            if (keyA == null || valueA == null)
            {
                throw new ConfigureException("配置模块：match元素解析出错。");
            }
            else
            {
                retval = new TwoTuple<String, String>(keyA.getValue(), valueA.getValue());
            }
        }
        else
        {
            if (isThrow)
            {
                throw new ConfigureException("配置模块：无法获取名称为：" + name + " 的元素对应的值。");
            }
        }
        return retval;
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        AlarmConfigColl coll = AlarmConfigColl.getInstance();
        System.out.println(coll.getAll());
    }
}
