/************************************************************************
日 期：2011-12-29
作 者: 郭祥
版 本：v1.3
描 述: 解析实例化配置文件
历 史：
 ************************************************************************/
package jetsennet.jbmp.autodiscovery;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import jetsennet.jbmp.exception.ConfigureException;
import jetsennet.jbmp.util.ConfigFileUtil;
import jetsennet.jbmp.util.JdomParseUtil;
import jetsennet.jbmp.util.XmlUtil;

/**
 * 解析实例化配置文件
 * @author 郭祥
 */
public final class AutoDisConfigColl
{

    private Map<Integer, AutoDisConfig> autos;
    private static final String AUTODIS_FILE_NAME = "autodis.xml";
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AutoDisConfigColl.class);

    private static AutoDisConfigColl instance = new AutoDisConfigColl();

    private AutoDisConfigColl()
    {
        autos = new HashMap<Integer, AutoDisConfig>();
        this.parse();
    }

    public static AutoDisConfigColl getInstance()
    {
        return instance;
    }

    /**
     * 获取自动发现配置。
     * @param type 类型
     * @return 结果
     */
    public AutoDisConfig get(int type)
    {
        return autos.get(type);
    }

    /**
     * @return 结果
     */
    public String toXml()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "RecordSet");
        for (AutoDisConfig adc : autos.values())
        {
            sb.append(adc.toXml());
        }
        XmlUtil.appendXmlEnd(sb, "RecordSet");
        return sb.toString();
    }

    private void parse()
    {
        autos.clear();
        SAXBuilder sax = new SAXBuilder();
        InputStream io = null;
        try
        {
            io = ConfigFileUtil.getConfigFile(AUTODIS_FILE_NAME);
            if (io == null)
            {
                throw new ConfigureException(String.format("配置模块：找不到配置文件(%s)", AUTODIS_FILE_NAME));
            }
            Document doc = sax.build(io);
            Element root = doc.getRootElement();
            Element acs = root.getChild("autoConfigs");
            if (acs == null)
            {
                throw new ConfigureException(String.format("配置模块：%s中找不到节点<autoConfigs>", AUTODIS_FILE_NAME));
            }
            List<Element> children = acs.getChildren("autoConfig");
            if (children == null || children.isEmpty())
            {
                throw new ConfigureException("配置模块：节点autoConfigs下找不到节点<autoConfig>");
            }
            for (Element child : children)
            {
                AutoDisConfig ac = new AutoDisConfig();
                ac.setTaskType(JdomParseUtil.getElementInt(child, AutoDisConfig.AUTO_DIS_CONF_TASKTYPE, -1, true));
                ac.setDisClass(JdomParseUtil.getElementString(child, AutoDisConfig.AUTO_DIS_CONF_CLASS, "", true));
                ac.setDisPlay(JdomParseUtil.getElementInt(child, AutoDisConfig.AUTO_DIS_CONF_DISPLAY, -1, false));
                ac.setDesc(JdomParseUtil.getElementString(child, AutoDisConfig.AUTO_DIS_CONF_DESC, "", true));
                this.put(ac);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new ConfigureException(String.format("配置模块：解析实例化配置文件(%s)出错。", AUTODIS_FILE_NAME), ex);
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
    }

    private void put(AutoDisConfig ac)
    {
        autos.put(ac.getTaskType(), ac);
    }
}
