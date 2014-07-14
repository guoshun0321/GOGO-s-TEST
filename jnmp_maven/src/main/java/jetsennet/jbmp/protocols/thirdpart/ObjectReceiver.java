package jetsennet.jbmp.protocols.thirdpart;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.helper.AbsAutoDisResultHandle;
import jetsennet.jbmp.autodiscovery.helper.AutoDisResult;
import jetsennet.jbmp.autodiscovery.helper.ProResult;
import jetsennet.jbmp.autodiscovery.helper.SingleResult;
import jetsennet.jbmp.autodiscovery.helper.ThirdPartDisResultHandle;
import jetsennet.jbmp.autodiscovery.helper.ThirdpartClassDiscover;
import jetsennet.jbmp.autodiscovery.helper.ThirdpartDisAutoIns;

/**
 * <devs> <dev id="id1" pid="id1" name="name1" ip="192.168.1.1" classId="2013" port="160"> <dev id="id2" pid="id1" name="name2" ip="192.168.1.2"
 * classId="2013" port="160"> </devs>
 * @author 郭祥
 */
public class ObjectReceiver
{

    /**
     * @param objXml 参数
     * @throws Exception 异常
     */
    public void receive(String objXml) throws Exception
    {
        Document doc = DocumentHelper.parseText(objXml);
        List<Element> objLst = doc.selectNodes("devs/dev");
        AutoDisResult retval = new AutoDisResult();
        if (objLst != null && !objLst.isEmpty())
        {
            for (Element obj : objLst)
            {
                String ident = this.attributeValue(obj, "id", true);
                String pident = this.attributeValue(obj, "pid", false);
                String name = this.attributeValue(obj, "name", true);
                String ip = this.attributeValue(obj, "ip", true);
                String port = this.attributeValue(obj, "port", true);
                String classId = this.attributeValue(obj, "classId", true);
                SingleResult sr = this.genSingle(ident, pident, name, ip, port, classId);
                if (sr != null)
                {
                    retval.addIpResult(sr);
                }
            }
        }

        // 类型
        ThirdpartClassDiscover cd = new ThirdpartClassDiscover(AutoDisConstant.PRO_NAME_PART);
        cd.find(retval);

        // 数据处理
        AbsAutoDisResultHandle handle = new ThirdPartDisResultHandle(-1, "key", AutoDisConstant.PRO_NAME_PART);
        // 激活自动实例化
        handle.setIns(new ThirdpartDisAutoIns());
        handle.handle(retval, -1);
    }

    private SingleResult genSingle(String ident, String pident, String name, String ip, String port, String classId)
    {
        SingleResult sr = new SingleResult(ident);
        ProResult pro = new ProResult(AutoDisConstant.PRO_NAME_PART);
        pro.addResult(AutoDisConstant.ID, ident);
        pident = pident == null ? ident : pident;
        pro.addResult(AutoDisConstant.PID, pident);
        pro.addResult(AutoDisConstant.NAME, name);
        pro.addResult(AutoDisConstant.IP, ip);
        pro.addResult(AutoDisConstant.PORT, port);
        pro.addResult(AutoDisConstant.CLASS_ID, classId);
        sr.addProResult(pro);
        return sr;
    }

    private String attributeValue(Element ele, String attr, boolean isThrow) throws Exception
    {
        if (ele == null || attr == null)
        {
            throw new NullPointerException();
        }
        String retval = ele.attributeValue(attr);
        if (retval == null && isThrow)
        {
            throw new Exception(String.format("%s字符串中的属性%s的值无法获取", ele.toString(), attr));
        }
        return retval;
    }

    /**
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        ObjectReceiver or = new ObjectReceiver();
        String str =
            "<devs><dev id='id1' pid='id1' name='name1' ip='192.168.1.1' classId='2013' port='160'></dev><dev id='id2' pid='id1' "
                + "name='name2' ip='192.168.1.2' classId='2013' port='160'></dev></devs>";
        or.receive(str);
    }

}
