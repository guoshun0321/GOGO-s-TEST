/************************************************************************
 * 日 期：2011-11-24 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 性能数据查询接口 
 * 历 史：2011-11-29 添加方法nmpGetObjAttrValueByObjAndClass；
 ************************************************************************/
package jetsennet.jbmp.services;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import jetsennet.jbmp.business.PerfDataMonitor;
import jetsennet.net.WSResult;

/**
 * @author 余灵
 */
@WebService(name = "BMPPerfService", serviceName = "BMPPerfService", targetNamespace = "http://JetsenNet/JNMP/")
public class BMPPerfService
{
    private static Logger logger = Logger.getLogger(BMPPerfService.class);

    /**
     * 获取单个对象属性的性能数据
     * @param objId 对象ID
     * @param objattrId 对象属性ID
     * @param attrType 类型
     * @param fetchSize 获取性能数据的条数
     * @return 性能数据XML
     */
    public WSResult bmpGetPerfDataByObjAttr(int objId, int objattrId, int attrType, int fetchSize)
    {
        WSResult retObj = new WSResult();
        try
        {
            retObj.resultVal = new PerfDataMonitor().getPerfDataByObjAttr(objId, objattrId, attrType, fetchSize);
            logger.info("获取某对象属性的性能数据结果：" + retObj.resultVal);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取某对象属性的性能数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 获取某对象的某属性的性能数据
     * @param objId 对象ID
     * @param attrId 属性ID
     * @param fetchSize 获取性能数据的条数
     * @return 性能数据XML
     */
    public WSResult bmpGetPerfDataByObjAndAttrib(int objId, int attrId, int fetchSize)
    {
        WSResult retObj = new WSResult();
        try
        {
            retObj.resultVal = new PerfDataMonitor().getPerfDataByObjAndAttrib(objId, attrId, fetchSize);
            logger.info("获取某对象的某属性的性能数据结果：" + retObj.resultVal);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取某对象的某属性的性能数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 获取某对象的某类别的自定义属性或者配置信息、表格数据的值
     * @param objId 对象ID
     * @param classId 分类ID
     * @param isFresh 是否刷新
     * @return 自定义属性或者配置信息、表格数据的值XML
     */
    public WSResult bmpGetObjAttrValueByObjAndClass(int objId, int classId, boolean isFresh)
    {
        WSResult retObj = new WSResult();
        try
        {
            retObj.resultVal = new PerfDataMonitor().getObjAttrValueByObjAndClass(objId, classId, isFresh);
            logger.info("获取某对象的某类别的自定义属性或者配置信息、表格数据的值结果：" + retObj.resultVal);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取某对象的某类别的自定义属性或者配置信息、表格数据的值失败!", ex);
        }
        return retObj;
    }

    /**
     * 获取所有对象属性的性能数据 主要用于获取拓扑图中的性能数据和性能面板中的性能数据
     * @param objXml 要获取性能数据的各对象属性及参数的XML
     * @return 返回性能数据XML
     */
    public WSResult bmpGetObjAttrsPerfData(String objXml)
    {
        WSResult retObj = new WSResult();
        try
        {
            logger.info("获取所有对象属性的性能数据XML:" + objXml);
            retObj.resultVal = new PerfDataMonitor().getObjAttrsPerfData(objXml);
            logger.info("获取所有对象属性的性能数据结果：" + retObj.resultVal);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取对象属性的性能数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 获取其他类型的对象的性能数据
     * @param objXml 固定界面的对象的XML
     * @param type 对象类型
     * @param objID 对象ID
     * @return 性能数据XML
     */
    public WSResult bmpGetElseObjCollValue(String objXml, String type, int objID)
    {
        WSResult retObj = new WSResult();
        try
        {
            logger.info("获取其他类型的对象的性能数据XML:" + objXml);
            retObj.resultVal = new PerfDataMonitor().getElseObjCollValue(objXml, type, objID);
            logger.info("获取其他类型的对象的性能数据结果：" + retObj.resultVal);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取其他类型的对象的采集数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 查询SNMP历史数据
     * @param objId 对象ID
     * @param startTime 开始时间(单位是秒)
     * @param endTime 结束时间(单位是秒)
     * @param objAttrIds 对象属性id列表
     * @return 结果
     */
    public WSResult bmpQuerySNMPHistoryData(int objId, long startTime, long endTime, String objAttrIds)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new PerfDataMonitor().querySNMPHistoryData(objId, startTime, endTime, objAttrIds);
            logger.info("查询SNMP历史数据结果：" + retObj.resultVal);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "查询SNMP历史数据失败!", ex);
        }
        return retObj;
    }

    /**
     * 查询SNMP历史数据 调用bmpQuerySNMPHistoryData，返回值使用xml格式
     * @param objId 对象ID
     * @param startTime 开始时间(单位是秒)
     * @param endTime 结束时间(单位是秒)
     * @param objAttrIds 对象属性id列表
     * @return 结果
     */
    public String bmpQuerySNMPHistoryDataXml(int objId, long startTime, long endTime, String objAttrIds)
    {
        return bmpQuerySNMPHistoryData(objId, startTime, endTime, objAttrIds).resultVal;
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
