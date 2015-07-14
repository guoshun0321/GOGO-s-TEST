/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.util.Date;

import org.apache.log4j.Logger;

import jetsennet.jbmp.util.ConfigUtil;

/**
 * 代码优化改动
 * @author liwei
 */
public class EmailUtil
{
    private static final Logger logger = Logger.getLogger(EmailUtil.class);

    /**
     * 发送邮件
     * @param userInfos 邮件用户数组
     * @param msg 邮件内容
     * @return ret 0表示发送失败，1表示发送成功
     * @throws
     */
    public static int sendEmail(String[] userInfos, String msg) throws Exception
    {
        int ret = 0;

        String mailHost = ConfigUtil.getEmailHost(); // 发送邮件服务器地址
        String mailUserName = ConfigUtil.getEmailUserName(); // 发送邮件服务器的用户帐号
        String mailPassword = ConfigUtil.getEmailPassword(); // 发送邮件服务器的用户密码
        String mailFrom = ConfigUtil.getEmailFrom(); // 发送邮件的邮箱地址
        try
        {
            // 使用HTML格式发送邮件
            SendEmailAction sendmail = SendEmailAction.getHtmlMailSender(mailHost, mailUserName, mailPassword);

            sendmail.setSubject("监控系统报警邮件");
            sendmail.setSendDate(new Date());
            sendmail.setMailContent(msg);

            sendmail.setMailFrom(mailFrom);
            sendmail.setMailTo(userInfos, "to");

            logger.info("正在发送邮件，请稍候.......邮件内容：" + msg);
            sendmail.sendMail();
            ret = 1;
            logger.info("恭喜你，邮件已经成功发送!" + msg);
        }
        catch (Exception ex)
        {
            ret = 0;
            throw ex;
        }

        return ret;
    }

    public static void main(String[] args)
    {

    }
}
