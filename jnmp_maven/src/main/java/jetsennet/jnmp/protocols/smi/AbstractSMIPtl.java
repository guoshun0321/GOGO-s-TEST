package jetsennet.jnmp.protocols.smi;

import java.util.Enumeration;

import javax.wbem.cim.CIMException;
import javax.wbem.cim.CIMInstance;
import javax.wbem.cim.CIMObjectPath;
import javax.wbem.client.CIMListener;

import org.apache.log4j.Logger;

import jetsennet.jbmp.util.SMIBean;
import jetsennet.jnmp.exception.SMIException;

/**
 * SMI辅助类
 * @author xli
 * @version 1.0
 */
public abstract class AbstractSMIPtl
{

    /**
     * @param host 主机
     * @param uname 名
     * @param pwd 密码
     * @return 结果
     * @throws SMIException 异常
     */
    public abstract boolean init(String host, String uname, String pwd) throws SMIException;

    /**
     * 重置数据，并关闭底层连接。
     * @throws CIMException 异常
     */
    public abstract void close() throws CIMException;

    private static final Logger logger = Logger.getLogger(AbstractSMIPtl.class);

    private static ThreadLocal<SMIPtl> executor = new ThreadLocal<SMIPtl>();

    /**
     * 根据WQL获得枚举实例
     * @param bean 对象
     * @return 结果
     * @throws CIMException 异常
     */
    public abstract Enumeration<CIMInstance> enumerationInstance(SMIBean bean) throws CIMException;

    /**
     * 关闭客户端
     */
    public static void closeClient()
    {
        AbstractSMIPtl smi = executor.get();
        try
        {
            if (smi != null)
            {
                smi.close();
            }
        }
        catch (CIMException ex)
        {
            logger.error(ex);
        }
        finally
        {
            executor.set(null);
        }
    }

    /**
     * @return 结果
     */
    public static AbstractSMIPtl getInstance()
    {
        SMIPtl result = executor.get();
        if (result == null)
        {
            result = new SMIPtl();
            executor.set(result);
        }
        return result;
    }

    /**
     * 添加监听器监听事件
     * @param listener 参数
     */
    public abstract void setListener(CIMListener listener);

    /**
     * @param cop 参数
     * @return 结果
     * @throws CIMException 异常
     */
    public abstract Enumeration<?> enumerationInstance(CIMObjectPath cop) throws CIMException;

    /**
     * 获取单个实例
     * @param cop 参数
     * @return 实例
     * @throws CIMException 异常
     */
    public abstract CIMInstance getInstance(CIMObjectPath cop) throws CIMException;

    /**
     * 获取所有的引用实例
     * @param cop 参数
     * @return 实例
     * @throws CIMException 异常
     */
    public abstract Enumeration<?> references(CIMObjectPath cop) throws CIMException;

    /**
     * 获取所有的引用名称
     * @param cop 参数
     * @return 结果
     * @throws CIMException 异常
     */
    public abstract Enumeration<?> referenceNames(CIMObjectPath cop) throws CIMException;

}
