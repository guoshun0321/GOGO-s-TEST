/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件发送组件,具体的使用方法参照该类的main方法
 * @author xdh
 */
public abstract class SendEmailAction extends Authenticator
{

    private String username = null; // 邮件发送帐号用户名
    private String userpasswd = null; // 邮件发送帐号用户口令
    protected BodyPart messageBodyPart = null;
    protected Multipart multipart = new MimeMultipart("related");
    protected MimeMessage mailMessage = null;
    protected Session mailSession = null;
    protected Properties mailProperties = System.getProperties();
    protected InternetAddress mailFromAddress = null;
    protected InternetAddress mailToAddress = null;
    protected Authenticator authenticator = null;
    protected String mailSubject = "";
    protected Date mailSendDate = null;

    /**
     * 构造函数
     * @param smtpHost
     * @param username
     * @param password
     */
    protected SendEmailAction(String smtpHost, String username, String password)
    {
        this.username = username;
        this.userpasswd = password;
        mailProperties.put("mail.smtp.host", smtpHost);
        mailProperties.put("mail.smtp.auth", "true"); // 设置smtp认证，很关键的一句
        mailSession = Session.getDefaultInstance(mailProperties, this);
        mailMessage = new MimeMessage(mailSession);
        messageBodyPart = new MimeBodyPart();
    }

    /**
     * 构造一个纯文本邮件发送实例
     * @param smtpHost 参数
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    public static SendEmailAction getTextMailSender(String smtpHost, String username, String password)
    {
        return new SendEmailAction(smtpHost, username, password)
        {
            public void setMailContent(String mailContent) throws MessagingException
            {
                messageBodyPart.setText(mailContent);
                multipart.addBodyPart(messageBodyPart);
            }
        };
    }

    /**
     * 构造一个HTML邮件发送实例
     * @param smtpHost 参数
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    public static SendEmailAction getHtmlMailSender(String smtpHost, String username, String password)
    {
        return new SendEmailAction(smtpHost, username, password)
        {
            public void setMailContent(String mailContent) throws MessagingException
            {
                messageBodyPart.setContent(mailContent, "text/html; charset=utf-8");
                multipart.addBodyPart(messageBodyPart);
            }
        };
    }

    /**
     * 用于实现邮件发送用户验证
     * @see javax.mail.Authenticator#getPasswordAuthentication
     */
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(username, userpasswd);
    }

    /**
     * 设置邮件标题
     * @param mailSubject 参数
     * @throws MessagingException 异常
     */
    public void setSubject(String mailSubject) throws MessagingException
    {
        try
        {
            this.mailSubject = mailSubject;
            mailMessage.setSubject(mailSubject);
        }
        catch (Exception ex)
        {
            throw new MessagingException("设置邮件标题异常，请检查是否正确。", ex);
        }
    }

    /**
     * 所有子类都需要实现的抽象方法，为了支持不同的邮件类型
     * @param mailContent
     * @throws MessagingException
     */
    protected abstract void setMailContent(String mailContent) throws MessagingException;

    /**
     * 设置邮件发送日期
     * @param sendDate 发送日期
     * @throws MessagingException 异常
     */
    public void setSendDate(Date sendDate) throws MessagingException
    {
        try
        {
            this.mailSendDate = sendDate;
            mailMessage.setSentDate(sendDate);
        }
        catch (Exception ex)
        {
            throw new MessagingException("设置邮件发送日期异常。", ex);
        }
    }

    /**
     * 设置邮件发送附件
     * @param attachmentName 参数
     * @throws MessagingException 异常
     */
    public void setAttachments(String attachmentName) throws MessagingException
    {
        try
        {
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachmentName);
            messageBodyPart.setDataHandler(new DataHandler(source));
            int index = attachmentName.lastIndexOf(File.separator);
            String attachmentRealName = attachmentName.substring(index + 1);
            messageBodyPart.setFileName(attachmentRealName);
            multipart.addBodyPart(messageBodyPart);
        }
        catch (Exception ex)
        {
            throw new MessagingException("设置邮件发送附件异常。", ex);
        }
    }

    /**
     * 设置发件人地址
     * @param mailFrom 参数
     * @throws MessagingException 异常
     */
    public void setMailFrom(String mailFrom) throws MessagingException
    {
        try
        {
            mailFromAddress = new InternetAddress(mailFrom);
            mailMessage.setFrom(mailFromAddress);
        }
        catch (Exception ex)
        {
            throw new MessagingException("设置发件人地址异常，请检查地址是否正确。", ex);
        }
    }

    /**
     * 设置收件人地址，收件人类型为to,cc,bcc(大小写不限)
     * @param mailTo 邮件接收者地址
     * @param mailType 值为to,cc,bcc
     * @author xdh
     * @throws Exception 异常
     */
    public void setMailTo(String[] mailTo, String mailType) throws Exception
    {
        try
        {
            for (int i = 0; i < mailTo.length; i++)
            {
                mailToAddress = new InternetAddress(mailTo[i]);
                if ("to".equalsIgnoreCase(mailType))
                {
                    mailMessage.addRecipient(Message.RecipientType.TO, mailToAddress);
                }
                else if ("cc".equalsIgnoreCase(mailType))
                {
                    mailMessage.addRecipient(Message.RecipientType.CC, mailToAddress);
                }
                else if ("bcc".equalsIgnoreCase(mailType))
                {
                    mailMessage.addRecipient(Message.RecipientType.BCC, mailToAddress);
                }
                else
                {
                    throw new Exception("未知邮件类型: " + mailType + "!");
                }
            }
        }
        catch (Exception ex)
        {
            throw new Exception("设置邮件接收地址异常。", ex);
        }
    }

    /**
     * 开始发送邮件
     * @throws MessagingException 异常
     * @throws SendFailedException 异常
     */
    public void sendMail() throws MessagingException, SendFailedException
    {
        try
        {
            if (mailToAddress == null)
            {
                throw new MessagingException("请您填写收件人地址！");
            }
            mailMessage.setContent(multipart);
            Transport.send(mailMessage);
        }
        catch (Exception ex)
        {
            throw new SendFailedException("邮件发送异常。", ex);
        }
    }

    /**
     * 邮件发送测试
     * @param args 参数
     */
    public static void main(String[] args)
    {
        String mailHost = "smtp.163.com"; // 发送邮件服务器地址
        String mailUser = "guoshun0321"; // 发送邮件服务器的用户帐号
        String mailPassword = "19870321"; // 发送邮件服务器的用户密码
        String[] toAddress = { "914707971@qq.com" };

        // 使用纯文本格式发送邮件
        SendEmailAction sendmail = SendEmailAction.getTextMailSender(mailHost, mailUser, mailPassword);
        try
        {
            sendmail.setSubject("邮件发送测试");
            sendmail.setSendDate(new Date());
            String content = "<H1>你好,中国人</H1><img src=\"http://www.javayou.com/images/logo.gif\">";
            sendmail.setMailContent(content); // 发内容
            //            sendmail.setAttachments("C:\\test.txt"); // 发附件

            sendmail.setMailFrom("guoshun0321@163.com");
            sendmail.setMailTo(toAddress, "to");

            System.out.println("正在发送邮件，请稍候.......");
            sendmail.sendMail();
            System.out.println("恭喜你，邮件已经成功发送!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
