/************************************************************************
日 期：2011-12-29
作 者: 郭祥
版 本：v1.3
描 述: 解析实例化配置文件
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.exception.ConfigureException;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.util.ConfigFileUtil;
import jetsennet.jbmp.util.JdomParseUtil;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 解析实例化配置文件
 * @author 郭祥
 */
public final class InsConfigColl
{

    /**
     * 对象实例化默认配置
     */
    private InsConfig defConf;
    /**
     * 对象实例化默认配置
     */
    private InsConfig defConfSub;
    /**
     * 完全配置
     */
    private ArrayList<InsConfig> configs;
    /**
     * 模糊匹配
     */
    private ArrayList<InsConfig> configs1;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(InsConfigColl.class);

    // 单例
    private static InsConfigColl instance = new InsConfigColl();

    private InsConfigColl()
    {
        configs = new ArrayList<InsConfig>();
        configs1 = new ArrayList<InsConfig>();
        defConf = null;
        this.parse();
    }

    public static InsConfigColl getInstance()
    {
        return instance;
    }

    /**
     * 获取实例化配置。先进行完全匹配，再进行模糊匹配。
     * @param type 类型
     * @return 结果
     */
    public InsConfig get(String type)
    {
        if (type != null)
        {
            for (int i = 0; i < configs.size(); i++)
            {
                InsConfig config = configs.get(i);
                if (config.getClassType().equals(type))
                {
                    return config;
                }
            }
            for (int i = 0; i < configs1.size(); i++)
            {
                InsConfig config = configs1.get(i);
                if (type.startsWith(config.getClassType()))
                {
                    return config;
                }
            }
        }
        return defConf;
    }

    /**
     * 获取实例化配置。先进行完全匹配，再进行模糊匹配。
     * @param type 类型
     * @return 结果
     */
    public InsConfig getSub(String type)
    {
        if (type != null)
        {
            for (int i = 0; i < configs.size(); i++)
            {
                InsConfig config = configs.get(i);
                if (config.getClassType().equals(type))
                {
                    return config;
                }
            }
            for (int i = 0; i < configs1.size(); i++)
            {
                InsConfig config = configs1.get(i);
                if (type.startsWith(config.getClassType()))
                {
                    return config;
                }
            }
        }
        return defConfSub;
    }

    /**
     * 清除所有配置
     */
    public void clear()
    {
        defConf = null;
        configs.clear();
        configs1.clear();
    }

    /**
     * 获取全部配置文件
     * @return
     */
    public ArrayList<InsConfig> getAll()
    {
        return configs;
    }

    /**
     * 解析XML文件
     */
    private void parse()
    {
        SAXBuilder sax = new SAXBuilder();
        InputStream io = null;
        try
        {
            io = ConfigFileUtil.getInsConfigFile();
            if (io == null)
            {
                throw new ConfigureException("配置模块：找不到配置文件<ins.xml>");
            }
            Document doc = sax.build(io);
            Element root = doc.getRootElement();
            Element acs = root.getChild("insConfigs");
            if (acs == null)
            {
                throw new ConfigureException("配置模块：ins.xml中找不到节点<insConfigs>");
            }
            List<Element> children = acs.getChildren("insConfig");
            if (children == null || children.isEmpty())
            {
                throw new ConfigureException("配置模块：节点insConfigs下找不到节点<insConfig>");
            }
            for (Element child : children)
            {
                InsConfig ac = new InsConfig();
                ac.setName(JdomParseUtil.getElementString(child, "name", "name", false));
                ac.setClassType(JdomParseUtil.getElementString(child, "type", null, true));
                ac.setMatchType(JdomParseUtil.getElementInt(child, "matchType", 0, false));
                ac.setInsSub(JdomParseUtil.getElementBoolean(child, "insSub", true, false));
                ac.setSub(JdomParseUtil.getElementBoolean(child, "isSub", false, false));

                // 处理对象
                if (!ac.isSub())
                {
                    if (defConf == null)
                    {
                        ac.setProExtend(JdomParseUtil.getElementString(child, "proExtend", null, false));
                        ac.setInsClass(JdomParseUtil.getElementString(child, "insClass", null, true));
                        defConf = ac;
                    }
                    else
                    {
                        ac.setProExtend(JdomParseUtil.getElementString(child, "proExtend", defConf.getProExtend(), false));
                        ac.setInsClass(JdomParseUtil.getElementString(child, "insClass", defConf.getInsClass(), false));
                        this.put(ac);
                    }
                }
                else
                {
                    // 处理子对象
                    if (defConfSub == null)
                    {
                        ac.setProExtend(JdomParseUtil.getElementString(child, "proExtend", null, false));
                        ac.setInsClass(JdomParseUtil.getElementString(child, "insClass", null, true));
                        defConfSub = ac;
                    }
                    else
                    {
                        ac.setProExtend(JdomParseUtil.getElementString(child, "proExtend", defConfSub.getProExtend(), false));
                        ac.setInsClass(JdomParseUtil.getElementString(child, "insClass", defConfSub.getInsClass(), false));
                        this.put(ac);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new ConfigureException("配置模块：解析实例化配置文件<ins.xml>出错。", ex);
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
                    logger.error("", ex);
                }
                finally
                {
                    io = null;
                }
            }
        }
        logger.info("默认对象实例化配置\n" + defConf);
        logger.info("默认子对象实例化配置\n" + defConfSub);
        logger.info("实例化配置\n" + configs);
    }

    private void put(InsConfig ac)
    {
        if (ac.getMatchType() == 0)
        {
            configs.add(ac);
        }
        else if (ac.getMatchType() == 1)
        {
            configs1.add(ac);
        }
    }

    /**
     * 获取对象实例化类
     * @param insClass
     * @return
     * @throws Exception
     */
    public static AbsObjIns ensureObjInsClass(String insClass) throws Exception
    {
        AbsObjIns retval = null;
        Object obj = ensureClass(insClass);
        if (obj != null && obj instanceof AbsObjIns)
        {
            retval = (AbsObjIns) obj;
        }
        return retval;
    }

    /**
     * 获取子对象实例化类
     * @param insClass
     * @return
     * @throws Exception
     */
    public static AbsSubObjIns ensureSubObjInsClass(String insClass) throws Exception
    {
        AbsSubObjIns retval = null;
        Object obj = ensureClass(insClass);
        if (obj != null && obj instanceof AbsSubObjIns)
        {
            retval = (AbsSubObjIns) obj;
        }
        return retval;
    }

    /**
     * 初始化实例化类
     * @param insClass
     * @return
     * @throws Exception
     */
    public static Object ensureClass(String insClass) throws Exception
    {
        if (insClass == null)
        {
            throw new InstanceException("实例化模块：实例化类名称为空。");
        }
        Object retval = null;
        try
        {
            logger.debug("实例化模块：确定实例化类：" + insClass);
            retval = Class.forName(insClass).newInstance();
        }
        catch (Exception ex)
        {
            throw new InstanceException("实例化模块：无法实例化类：" + insClass, ex);
        }
        return retval;
    }

    public InsConfig getDefConf()
    {
        return defConf;
    }

    public InsConfig getDefConfSub()
    {
        return defConfSub;
    }

}
