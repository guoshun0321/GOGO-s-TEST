package jetsennet.jnmp.protocols.smi;

import java.util.Enumeration;

import javax.wbem.cim.CIMException;
import javax.wbem.cim.CIMInstance;
import javax.wbem.cim.CIMNameSpace;
import javax.wbem.cim.CIMObjectPath;
import javax.wbem.client.CIMClient;
import javax.wbem.client.CIMListener;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;

import jetsennet.jbmp.util.SMIBean;
import jetsennet.jnmp.exception.SMIException;

/**
 * SMI辅助类
 * @author xli
 * @version 1.0 date 2011-12-8下午5:03:18
 */

public class SMIPtl extends AbstractSMIPtl
{

    /**
     * 
     */
    CIMClient cc = null;
    /**
     * 默认名称空间
     */
    public static final String DEF_NS = "root/cimv2";

    @Override
    public void close() throws CIMException
    {
        if (null != cc)
        {
            cc.close();
        }
    }

    @Override
    public void setListener(CIMListener listener)
    {

    }

    @Override
    public boolean init(String host, String uname, String pwd) throws SMIException
    {
        CIMNameSpace cns = new CIMNameSpace(host);
        UserPrincipal up = new UserPrincipal(uname);
        PasswordCredential pc = new PasswordCredential(pwd);
        try
        {
            cc = new CIMClient(cns, up, pc);
        }
        catch (CIMException e)
        {
            e.printStackTrace();
            return false;
        }
        if (null != cc)
        {
            return true;
        }
        return false;
    }

    /**
     * 根据WQL获得枚举实例 只获取一个值
     * @param bean 参数
     * @return 结果
     * @throws CIMException 异常
     */
    @SuppressWarnings("unchecked")
    public Enumeration<CIMInstance> enumerationInstance(SMIBean bean) throws CIMException
    {
        // 需要修改
        if (null != bean.getWql())
        {
            return cc.execQuery(new CIMObjectPath(bean.getPath()), bean.getWql(), CIMClient.WQL);
        }
        return enumerationInstance(new CIMObjectPath(bean.getPath()));
    }

    /**
     * 根据路径返回枚举值
     * @param cop 参数
     * @return 结果
     * @throws CIMException 异常
     */
    public Enumeration<CIMInstance> enumerationInstance(CIMObjectPath cop) throws CIMException
    {
        return cc.enumerateInstances(cop);
    }

    @Override
    public CIMInstance getInstance(CIMObjectPath cop) throws CIMException
    {
        return cc.getInstance(cop);
    }

    @Override
    public Enumeration<?> references(CIMObjectPath cop) throws CIMException
    {
        return cc.references(cop);
    }

    @Override
    public Enumeration<?> referenceNames(CIMObjectPath cop) throws CIMException
    {
        return cc.referenceNames(cop);
    }
}
