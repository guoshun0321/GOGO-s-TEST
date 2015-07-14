/************************************************************************
 * 日 期：2011-11-24 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 告警查询及告警处理的接口 
 * 历 史：
 ************************************************************************/
package jetsennet.jnmp.services;

import javax.jws.WebService;

import jetsennet.net.WSResult;

import org.apache.log4j.Logger;

/**
 * @author 余灵
 */
@WebService(name = "NMPAlarmService", serviceName = "NMPAlarmService", targetNamespace = "http://JetsenNet/JNMP/")
public class NMPAlarmService
{
    private static Logger logger = Logger.getLogger(NMPAlarmService.class);

    /**
     * 记录错误日志
     * @param retObj 发生错误的WSResult对象
     * @param message 相关错误信息
     * @param ex 异常
     */
    private void errorProcess(WSResult retObj, String message, Exception ex)
    {
        logger.error(message, ex);
        retObj.errorCode = -1;
        retObj.errorString = message + ex.getMessage();
    }
}
