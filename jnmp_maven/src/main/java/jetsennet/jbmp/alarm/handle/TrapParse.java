/************************************************************************
日 期：2012-1-9
作 者: 郭祥
版 本: v1.3
描 述: Trap中的OID转换成可识别的文字
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.snmp4j.smi.Variable;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;
import jetsennet.jbmp.trap.util.TrapConstants;
import jetsennet.jbmp.util.XmlUtil;

/**
 * Trap中的OID转换成可识别的文字
 * @author 郭祥
 */
public class TrapParse
{

    private TrapTableDal ttdal;
    public static Logger logger = Logger.getLogger(TrapParse.class);

    /**
     * 构造方法
     */
    public TrapParse()
    {
        ttdal = ClassWrapper.wrapTrans(TrapTableDal.class);
    }

    /**
     * 解析Trap
     * @param data 参数
     * @param mo 参数
     * @param coding 参数
     * @return 结果
     */
    public String parse(CollData data, MObjectEntity mo, String coding)
    {
        if (data == null || data.params == null || mo == null)
        {
            return null;
        }
        Object obj = data.params.get(TrapConstants.SNMP_TRAP_OID);
        if (obj == null)
        {
            return null;
        }
        String trapOid = obj.toString();
        TrapTableEntity tt = ttdal.getTrap(trapOid, MibUtil.ensureMib(mo.getObjId()));
        return this.parseToXml(tt, data, coding);
    }

    /**
     * 解析Trap
     * @param tt
     * @param data
     * @param coding
     * @return
     */
    private String parseToXml(TrapTableEntity tt, CollData data, String coding)
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "TRAPS");
        this.parseTrap(tt, data, coding, sb);
        XmlUtil.appendXmlEnd(sb, "TRAPS");
        return sb.toString();
    }

    private String parseTrap(TrapTableEntity tt, CollData data, String coding, StringBuilder sb)
    {
        XmlUtil.appendXmlBegin(sb, "TRAP");
        if (tt != null)
        {
            XmlUtil.appendTextNode(sb, "NAME", tt.getDescName());
            XmlUtil.appendTextNode(sb, "OID", tt.getTrapOid());
        }
        XmlUtil.appendXmlBegin(sb, "SUBS");
        Set<String> keys = data.params.keySet();
        for (String key : keys)
        {
            Object obj = data.params.get(key);
            if (obj != null && obj instanceof Variable)
            {
                this.parseNode(key, (Variable) obj, coding, tt, sb);
            }
        }
        XmlUtil.appendXmlEnd(sb, "SUBS");
        XmlUtil.appendXmlEnd(sb, "TRAP");
        return sb.toString();
    }

    private void parseNode(String oid, Variable var, String coding, TrapTableEntity tt, StringBuilder sb)
    {
        XmlUtil.appendXmlBegin(sb, "SUB");
        TrapTableEntity sub = this.ensureNode(oid, tt);
        if (sub != null)
        {
            XmlUtil.appendTextNode(sb, "NAME", tt.getDescName());
        }
        XmlUtil.appendTextNode(sb, "OID", oid);
        XmlUtil.appendXmlBegin(sb, "VALUE");
        sb.append(SnmpValueTranser.getInstance().trans(var, coding, 0));
        XmlUtil.appendXmlEnd(sb, "VALUE");
        XmlUtil.appendXmlEnd(sb, "SUB");
    }

    private TrapTableEntity ensureNode(String oid, TrapTableEntity tt)
    {
        if (tt == null || tt.getSubs() == null || tt.getSubs().isEmpty())
        {
            return null;
        }
        List<TrapTableEntity> subs = tt.getSubs();
        for (TrapTableEntity sub : subs)
        {
            if (sub.getTrapOid().equals(oid))
            {
                return sub;
            }
        }
        return null;
    }
}
