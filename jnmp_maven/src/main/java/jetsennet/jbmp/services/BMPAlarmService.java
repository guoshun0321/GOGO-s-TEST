/************************************************************************
日 期：2012-4-6
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.services;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import jetsennet.jbmp.business.AlarmMonitor;
import jetsennet.net.WSResult;

/**
 * @author yl
 */
@WebService(name = "BMPAlarmService", serviceName = "BMPAlarmService", targetNamespace = "http://JetsenNet/JBMP/")
public class BMPAlarmService
{
    private static Logger logger = Logger.getLogger(BMPAlarmService.class);

    /**
     * 获取某对象/对象组的告警列表
     * @param filterId :对象/对象组ID
     * @param filterType :获取类型。"OBJECT"为对象，"OBJGROUP"为对象组
     * @param sTime :起始时间
     * @param eTime :结束时间
     * @param level :告警级别
     * @param type : active，活动告警；history，历史告警。
     * @return 返回该对象的告警列表的XML
     */
    public WSResult bmpGetFilterAlarmList(int filterId, String filterType, long sTime, long eTime, int level, String type)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new AlarmMonitor().getFilterAlarmList(filterId, filterType, sTime, eTime, level, type);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取报警列表失败!", ex);
        }

        return retObj;
    }

    /**
     * 获取某对象/对象组的告警列表
     * @param filterId :对象/对象组ID
     * @param filterType :获取类型。"OBJECT"为对象，"OBJGROUP"为对象组
     * @param sTime :起始时间
     * @param eTime :结束时间
     * @param level :告警级别
     * @param type : active，活动告警；history，历史告警。
     * @return 返回该对象及子对象根据属性分组的未处理告警条数
     */
    public WSResult bmpGetFilterAlarmCount(int filterId, String filterType, long sTime, long eTime, int level, String type)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new AlarmMonitor().getFilterAlarmCount(filterId, filterType, sTime, eTime, level, type);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取报警条数失败!", ex);
        }

        return retObj;
    }

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
