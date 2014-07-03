/**********************************************************************
 * 日 期： 2012-4-13
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  BMPExtendService.java
 * 历 史： 2012-4-13 创建
 *********************************************************************/
package jetsennet.jbmp.services;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import jetsennet.jbmp.business.CollDataReceiver;
import jetsennet.jbmp.business.ReceiveTaskBase;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.protocols.thirdpart.ObjectReceiver;
import jetsennet.net.WSResult;

/**
 * 监控系统第三方接口服务
 */
@WebService(name = "BMPExtendService", serviceName = "BMPExtendService", targetNamespace = "http://JetsenNet/JNMP/")
public class BMPExtendService
{
    private static Logger logger = Logger.getLogger(BMPExtendService.class);

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

    /**
     * 接收第三方系统发生过来的性能数据
     * @param objXml 数据格式如下 <ObjSet> <Obj ID="000942" IP="192.168.8.58"> <IndexSet DateTime="2012-04-13 12:00:00"> <Index ID="If1Out" Value="101"/>
     *            <Index ID="If1In" Value="88"/> </IndexSet> </Obj> </ObjSet> 可以同时接收多个对象的性能数据， 可以同时接收多个子对象的性能数据， 可以同时接收多个时间段的性能数据，
     *            可以同时接收同一时间段多个指标的性能数据
     * @return 结果
     */
    public WSResult bmpReceivePerfData(String objXml)
    {
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrap(CollDataReceiver.class).receive(objXml);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "接收性能数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 接收第三方系统发生过来的报警数据 节目报警：
     * @param msg <Msg Version="4" MsgID="4310" Type="MonUp" DateTime="2010-12-20 04:02:36" SrcCode="440600M01" DstCode="440000G01" Priority="1"
     *            ReplyID="4310"> <Return Type="AlarmSearchPSet" Value="0" Desc="成功" /> <ReturnInfo> <AlarmSearchPSet Index="7" Freq="339000"
     *            ServiceID="104" VideoPID="163" AudioPID="92"> <AlarmSearchP Type="31" Desc="静帧" AlarmID="123121212" Value="1"
     *            Time="2011-02-28 14:26:00" /> </AlarmSearchPSet> </ReturnInfo> </Msg> 码流报警： <Msg Version="4" MsgID="4310" Type="MonUp"
     *            DateTime="2010-12-20 04:02:36" SrcCode="440600M01" DstCode="440000G01" Priority="1" ReplyID="4310"> <Return Type="AlarmSearchFSet"
     *            Value="0" Desc="成功" /> <ReturnInfo> <AlarmSearchFSet Index="7" Freq="339000"> <AlarmSearchF Type="1" Desc="失锁" AlarmID="123121212"
     *            Value="1" Time="2011-02-28 14:26:00" /> </AlarmSearchPSet> </ReturnInfo> </Msg> 可以接收同一对象的多个报警数据
     * @return 结果
     */
    public WSResult bmpReceiveAlarmData(String msg)
    {
        WSResult retObj = new WSResult();
        try
        {
            ClassWrapper.wrap(ReceiveTaskBase.class).receive(msg);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "接收报警数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 接收第三方性能数据 发送方数据格式： <devs> <dev id="id1" pid="id1" name="name1" ip="192.168.1.1" classId="2013" port="160"> <dev id="id2" pid="id1" name="name2"
     * ip="192.168.1.2" classId="2013" port="160"> </devs>
     * @param msg 参数
     * @return 结果
     */
    public WSResult bmpReceiveObject(String msg)
    {
        WSResult retval = new WSResult();
        try
        {
            new ObjectReceiver().receive(msg);
        }
        catch (Exception ex)
        {
            errorProcess(retval, "接收报警数据失败!", ex);
        }
        return retval;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        String objXml =
            "<ObjSet><Obj ID='000942' IP='192.168.8.58'><IndexSet DateTime='2012-04-13 12:00:00'><Index ID='If1Out' Value='101'/>"
                + "<Index ID='If1In' Value='88'/></IndexSet></Obj></ObjSet>";
        new BMPExtendService().bmpReceivePerfData(objXml);
    }
}
