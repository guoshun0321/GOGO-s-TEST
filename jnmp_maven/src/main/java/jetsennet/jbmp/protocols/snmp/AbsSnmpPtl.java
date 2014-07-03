/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.protocols.snmp;

import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.exception.SnmpException;

/**
 * 使用SNMP协议获取数据的接口
 * @author Guo
 */
public abstract class AbsSnmpPtl
{
    // 常量
    public static final String NOSUCHOBJECT = "noSuchObject";
    public static final String NOSUCHINSTANCE = "noSuchInstance";
    public static final String ENDOFMIBVIEW = "endOfMibView";

    /**
     * 初始化
     * @param retries 参数
     * @param ip 参数
     * @param port 参数
     * @param community 参数
     * @param retries参数
     * @param timeout 参数
     * @throws SnmpException 异常
     */
    abstract public void init(String ip, int port, String community, int retries, int timeout) throws SnmpException;

    /**
     * 初始化
     * @param ip 参数
     * @param port 参数
     * @param community 参数
     * @throws SnmpException 异常
     */
    abstract public void init(String ip, int port, String community) throws SnmpException;

    /**
     * 异步扫描用
     * @param ip 参数
     * @param port 参数
     * @param community 参数
     * @param oids 参数
     * @throws SnmpException 异常
     */
    abstract public void init(String ip, int port, String community, String[] oids) throws SnmpException;

    /**
     * 使用SNMP协议的GET方法获取数据。
     * @param iOIDs 参数
     * @return 返回值不为null
     * @throws SnmpException iOIDs为null时，抛出异常。iOIDs大小为0时，不抛出异常。
     */
    abstract public Map<String, VariableBinding> snmpGet(String[] iOIDs) throws SnmpException;

    /**
     * 使用SNMP协议的GETNEXT方法获取数据
     * @param iOID 参数
     * @return 结果
     * @throws SnmpException 异常
     */
    abstract public VariableBinding getNext(String iOID) throws SnmpException;

    /**
     * 使用SNMP协议的GETBULK方法获取数据。
     * @param iOIDs 参数
     * @param MaxRepetitions 参数
     * @return 结果
     * @throws SnmpException 异常
     */
    abstract public Map<String, VariableBinding> snmpGetBulk(String[] iOIDs, int MaxRepetitions) throws SnmpException;

    /**
     * 获取以begin开头的所有oid，返回不为NULL
     * @param begin 参数
     * @return 结果
     * @throws SnmpException 失败时，异常
     */
    abstract public Map<String, VariableBinding> snmpGetWithBegin(String begin) throws SnmpException;

    /**
     * 异步扫描
     * @throws SnmpException 异常
     */
    abstract public void asyncScan() throws SnmpException;

    /**
     * 设置异步扫描事件处理
     * @param listener 参数
     */
    abstract public void setListener(ResponseListener listener);

    /**
     * 重置数据，并关闭底层连接。
     * @throws SnmpException 异常
     */
    abstract public void close() throws SnmpException;

    // <editor-fold defaultstate="collapsed" desc="线程安全">
    private static final Logger logger = Logger.getLogger(AbsSnmpPtl.class);
    private static ThreadLocal<SnmpPtlV1> executor1 = new ThreadLocal<SnmpPtlV1>();
    private static ThreadLocal<SnmpPtlV2c> executor2 = new ThreadLocal<SnmpPtlV2c>();

    /**
     * 关闭
     */
    public static void closeSnmp()
    {
        AbsSnmpPtl snmp1 = executor1.get();
        try
        {
            if (snmp1 != null)
            {
                snmp1.close();
            }
        }
        catch (SnmpException ex)
        {
            logger.error(ex);
        }
        finally
        {
            executor1.set(null);
        }
        AbsSnmpPtl snmp2 = executor2.get();
        try
        {
            if (snmp2 != null)
            {
                snmp2.close();
            }
        }
        catch (SnmpException ex)
        {
            logger.error(ex);
        }
        finally
        {
            executor2.set(null);
        }
    }

    /**
     * @param version 版本
     * @return 结果
     */
    public static AbsSnmpPtl getInstance(String version)
    {
        if (version == null || version.equals(MObjectEntity.VERSION_SNMP_V1))
        {
            SnmpPtlV1 result = executor1.get();
            if (result == null)
            {
                result = new SnmpPtlV1();
                executor1.set(result);
            }
            return result;
        }
        else if (version.equals(MObjectEntity.VERSION_SNMP_V2C))
        {
            SnmpPtlV2c result = executor2.get();
            if (result == null)
            {
                result = new SnmpPtlV2c();
                executor2.set(result);
            }
            return result;
        }
        else
        {
            SnmpPtlV1 result = executor1.get();
            if (result == null)
            {
                result = new SnmpPtlV1();
                executor1.set(result);
            }
            return result;
        }
    }
    // </editor-fold>
}
