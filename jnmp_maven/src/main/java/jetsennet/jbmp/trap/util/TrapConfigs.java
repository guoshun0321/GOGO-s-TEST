package jetsennet.jbmp.trap.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.util.ConfigFileUtil;
import jetsennet.jbmp.util.JdomParseUtil;
import jetsennet.jbmp.util.XmlCfgUtil;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class TrapConfigs
{

    /**
     * Trap处理线程池大小
     */
    public int poolSize;
    /**
     * 使用的协议
     */
    public String protocol;
    /**
     * IP地址
     */
    public String ip;
    /**
     * 端口
     */
    public int port;
    /**
     * Trap匹配规则
     */
    public List<TrapConfig> configs;
    /**
     * 匹配类型，等于
     */
    private static final String MATCH_TYPE_EQUAL = "equal";
    /**
     * 匹配类型，Like
     */
    private static final String MATCH_TYPE_LIKE = "like";
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TrapConfigs.class);

    private static final TrapConfigs instance = new TrapConfigs();

    private TrapConfigs()
    {
        this.configs = new ArrayList<TrapConfig>();
        this.parse();
        logger.debug("Trap配置信息：\n" + this.toString());
    }

    public static TrapConfigs getInstance()
    {
        return instance;
    }

    private void parse()
    {

        SAXBuilder sax = new SAXBuilder();
        InputStream in = null;
        try
        {
            this.poolSize =
                XmlCfgUtil.getIntValue(TrapConstants.TRAP_CFG_FILE, TrapConstants.TRAP_THREAD_POOL_SIZE_CFG, TrapConstants.DEFAULT_THREAD_POOL_SIZE);
            this.ip = XmlCfgUtil.getStringValue(TrapConstants.TRAP_CFG_FILE, TrapConstants.TRAP_IP_CFG, TrapConstants.DEFAULT_IP);
            this.protocol = XmlCfgUtil.getStringValue(TrapConstants.TRAP_CFG_FILE, TrapConstants.TRAP_PROTOCOL_CFG, TrapConstants.DEFAULT_PROTOCOL);
            this.port = XmlCfgUtil.getIntValue(TrapConstants.TRAP_CFG_FILE, TrapConstants.TRAP_PORT_CFG, TrapConstants.DEFAULT_PORT);

            in = ConfigFileUtil.getConfigFile(TrapConstants.TRAP_CFG_FILE + ".xml");
            Document doc = sax.build(in);
            Element root = doc.getRootElement();

            // Trap配置信息
            Element server = root.getChild("server");

            // 匹配信息
            Element configsEle = root.getChild("configs");
            if (configsEle != null)
            {
                List<Element> configEles = configsEle.getChildren("config");
                if (configEles != null)
                {
                    for (Element configEle : configEles)
                    {
                        String trapOid = JdomParseUtil.getElementString(configEle, "trap_oid", null, true);

                        List<TrapMatcher> matchers = new ArrayList<TrapMatcher>();
                        Element matchersEle = configEle.getChild("matchers");
                        if (matchersEle != null)
                        {
                            List<Element> matcherEles = matchersEle.getChildren("matcher");
                            for (Element matcherEle : matcherEles)
                            {
                                String classType = JdomParseUtil.getElementString(matcherEle, "class_type", null, true);
                                String matchOid = JdomParseUtil.getElementString(matcherEle, "match_oid", null, true);
                                String matchType = JdomParseUtil.getElementString(matcherEle, "match_type", "equal", false);
                                boolean matchLike = false;
                                if (MATCH_TYPE_LIKE.equalsIgnoreCase(matchType))
                                {
                                    matchLike = true;
                                }
                                TrapMatcher matcher = new TrapMatcher(classType, matchOid, matchLike);
                                matchers.add(matcher);
                            }
                        }
                        this.configs.add(new TrapConfig(trapOid, matchers));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    in = null;
                }
            }
        }
    }

    public List<TrapMatcher> getMatchers(String trapOid)
    {
        List<TrapMatcher> retval = new ArrayList<TrapMatcher>();
        for (TrapConfig config : configs)
        {
            if (config.trapOid.equals(trapOid))
            {
                retval.addAll(config.matchers);
            }
        }
        return retval;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("IP:").append(this.ip).append("\n");
        sb.append("端口:").append(this.port).append("\n");
        sb.append("协议:").append(this.protocol).append("\n");
        sb.append("线程池大小:").append(this.poolSize).append("\n");
        sb.append("匹配规则：\n");
        for (TrapConfig config : configs)
        {
            sb.append(config);
        }
        return sb.toString();
    }

    public static void main(String[] args)
    {
        TrapConfigs.getInstance();
    }
}
