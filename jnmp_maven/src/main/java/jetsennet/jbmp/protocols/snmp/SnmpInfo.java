package jetsennet.jbmp.protocols.snmp;

import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;

/**
 * @author？
 */
public class SnmpInfo
{

    private Address address;
    private int version;
    private OctetString pwd;
    private OID authProtocol;
    private OctetString authPassphrase;
    private OID privProtocol;
    private OctetString privPassphrase;
    private OctetString contextName;

    /**
     * 构造函数
     * @param ip IP地址
     * @param port 端口号
     * @param version 版本
     * @param pwd v1、v2对应的是community，v3对于的是security name
     * @param authPro 认证协议，对v3有效，可填-1:无，0:MD5，1:SHA
     * @param authPwd 认证密码，对v3有效
     * @param privPro 加密协议，对v3有效，可填-1:无，0:DES,1:AES128,2:AES192,3:AES256,4:DESEDE
     * @param privPwd 加密密码，对v3有效
     * @param contextName 上下文名称，对v3有效，可以理解为用户所处的区域
     */
    public SnmpInfo(String ip, int port, int version, String pwd, int authPro, String authPwd, int privPro, String privPwd, String contextName)
    {
        this.address = new UdpAddress(ip + "/" + port);
        this.version = version;
        this.pwd = new OctetString(pwd);
        switch (authPro)
        {
        case 0:
            authProtocol = AuthMD5.ID;
            authPassphrase = new OctetString(authPwd);
            break;
        case 1:
            authProtocol = AuthSHA.ID;
            authPassphrase = new OctetString(authPwd);
            break;
        default:
            break;
        }
        switch (privPro)
        {
        case 0:
            privProtocol = PrivDES.ID;
            privPassphrase = new OctetString(privPwd);
            break;
        case 1:
            privProtocol = PrivAES128.ID;
            privPassphrase = new OctetString(privPwd);
            break;
        case 2:
            privProtocol = PrivAES192.ID;
            privPassphrase = new OctetString(privPwd);
            break;
        case 3:
            privProtocol = PrivAES256.ID;
            privPassphrase = new OctetString(privPwd);
            break;
        case 4:
            privProtocol = Priv3DES.ID;
            privPassphrase = new OctetString(privPwd);
            break;
        default:
            break;
        }
        if (contextName != null)
        {
            this.contextName = new OctetString(contextName);
        }
    }

    public SnmpInfo(String ip, int port, int version, String community)
    {
        this(ip, port, version, community, -1, null, -1, null, null);
    }

    public String getCommunity()
    {
        return pwd.toString();
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public OctetString getPwd()
    {
        return pwd;
    }

    public void setPwd(OctetString pwd)
    {
        this.pwd = pwd;
    }

    public OID getAuthProtocol()
    {
        return authProtocol;
    }

    public void setAuthProtocol(OID authProtocol)
    {
        this.authProtocol = authProtocol;
    }

    public OctetString getAuthPassphrase()
    {
        return authPassphrase;
    }

    public void setAuthPassphrase(OctetString authPassphrase)
    {
        this.authPassphrase = authPassphrase;
    }

    public OID getPrivProtocol()
    {
        return privProtocol;
    }

    public void setPrivProtocol(OID privProtocol)
    {
        this.privProtocol = privProtocol;
    }

    public OctetString getPrivPassphrase()
    {
        return privPassphrase;
    }

    public void setPrivPassphrase(OctetString privPassphrase)
    {
        this.privPassphrase = privPassphrase;
    }

    public OctetString getContextName()
    {
        return contextName;
    }

    public void setContextName(OctetString contextName)
    {
        this.contextName = contextName;
    }
}
