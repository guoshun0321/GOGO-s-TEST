/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.sqlclient.DataRecordInfo;

/**
 * 代码优化改动
 * @author liwei
 */
public class MessageUtil
{

    private static final Logger logger = Logger.getLogger(MessageUtil.class);

    /**
     * 构造函数
     */
    public MessageUtil()
    {

    }

    /**
     * 发送短信
     * @param userInfos 短信用户数组
     * @param msg 短信内容
     * @return ret 0表示发送失败，1表示发送成功
     * @throws
     */
    public static int sendMessage(String[] userInfos, String msg)
    {
        int ret = 0;
        try
        {
            for (String phone : userInfos)
            {
                SendMessageAction.getInstance().addMessage(phone, msg);
            }
            ret = 1;
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            ret = 0;
        }

        return ret;
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        try
        {
            String[] userInfos = { "33333", "44444" };
            String msg = "hello";
            int isSuccess = sendMessage(userInfos, msg);
            System.out.println(isSuccess);

            List<DataRecordInfo> messages = SendMessageAction.getInstance().getMessage(new Date());
            System.out.println();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }
}
