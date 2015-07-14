package jetsennet.jbmp.business;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.util.StringUtil;

/**
 * @author ？
 */
public class ReceiveTaskBase
{
    private static final Logger logger = Logger.getLogger(ReceiveTaskBase.class);
    public static final String RECEIVE_CLASS_PATH = "jetsennet.jbmp.business.";

    /**
     * @param msg 参数
     * @throws Exception 异常
     */
    public void receive(String msg) throws Exception
    {
        try
        {
            if (!StringUtil.isNullOrEmpty(msg))
            {
                Document doc = DocumentHelper.parseText(msg);
                Node node = (Node) doc.selectSingleNode("/Msg/Return");
                String typeClass = ((Element) node).attributeValue("Type");
                String value = ((Element) node).attributeValue("Value");
                if (!StringUtil.isNullOrEmpty(value) && "0".equals(value))
                {
                    if (StringUtil.isNullOrEmpty(typeClass))
                    {
                        logger.error(String.format("对应的类(%s)不合法。", typeClass));
                        return;
                    }
                    Object obj = null;
                    obj = Class.forName(RECEIVE_CLASS_PATH + typeClass).newInstance();
                    if (obj != null && obj instanceof AlarmSearchPSet)
                    {
                        ((AlarmSearchPSet) obj).onReceiveMessage(msg);
                    }
                    else if (obj != null && obj instanceof AlarmSearchFSet)
                    {
                        ((AlarmSearchFSet) obj).onReceiveMessage(msg);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
            throw ex;
        }
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        try
        {
            StringBuilder sbRequest = new StringBuilder();
            sbRequest.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>");
            sbRequest
                .append("<Msg  Version=\"4\" MsgID=\"4310\" Type=\"MonUp\" DateTime=\"2012-04-13 12:12:12\" SrcCode=\"11111\"  DstCode=\"440000G01\" "
                    + "Priority=\"1\" ReplyID=\"4310\">");
            sbRequest.append("<Return Type=\"AlarmSearchPSet\" Value=\"0\" Desc=\"成功\" />");
            sbRequest.append("<ReturnInfo>");
            sbRequest.append("<AlarmSearchPSet Index=\"7\" Freq=\"ts1\" ServiceID=\"100\" VideoPID=\"163\" AudioPID=\"92\">");
            sbRequest.append("<AlarmSearchP Type=\"31\" Desc=\"静帧\" AlarmID=\"1\" Value=\"1\" Time=\"2012-04-13 12:12:12\"/>");
            sbRequest.append("</AlarmSearchPSet>");
            sbRequest.append("</ReturnInfo>");
            sbRequest.append("</Msg>");

            String msg = sbRequest.toString();

            new ReceiveTaskBase().receive(msg);
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
        }
    }
}
