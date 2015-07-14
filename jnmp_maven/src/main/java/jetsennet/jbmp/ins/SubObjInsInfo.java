package jetsennet.jbmp.ins;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.util.JdomParseUtil;
import jetsennet.jbmp.util.XmlUtil;

/**
 * @author ？
 */
public class SubObjInsInfo
{

    /**
     * 对象ID
     */
    private int objId;
    /**
     * 分类ID
     */
    private int classId;
    /**
     * 附加信息
     */
    private List<SubObjInsInfoEntry> subs;

    /**
     * @param sub 参数
     */
    public void addSubs(SubObjInsInfoEntry sub)
    {
        if (subs == null)
        {
            subs = new ArrayList<SubObjInsInfoEntry>();
        }
        subs.add(sub);
    }

    /**
     * 转换成XML传递给前端
     * @return 结果
     */
    public String toXml()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "RecordSet");
        if (subs != null)
        {
            for (SubObjInsInfoEntry sub : subs)
            {
                sub.toXml(sb);
            }
        }
        XmlUtil.appendXmlEnd(sb, "RecordSet");
        return sb.toString();
    }

    /**
     * 接收前台XML生成对象
     * @param msg 参数
     * @param objId 对象id
     * @param classId 参数
     * @throws Exception 异常
     * @return 结果
     */
    public static SubObjInsInfo fromXml(String msg, MObjectEntity mo, int classId) throws Exception
    {
        SubObjInsInfo retval = new SubObjInsInfo();
        retval.setClassId(classId);
        retval.setObjId(mo.getObjId());
        SAXBuilder sax = new SAXBuilder();
        Document doc = sax.build(new ByteArrayInputStream(msg.getBytes("UTF-8")));
        Element root = doc.getRootElement();
        List<Element> children = root.getChildren("Record");
        if (children != null)
        {
            for (Element child : children)
            {
                SubObjInsInfoEntry sub = new SubObjInsInfoEntry();
                sub.objName = JdomParseUtil.getElementString(child, "name", null, true);
                sub.addInfo = JdomParseUtil.getElementString(child, "info", null, true);
                sub.desc = JdomParseUtil.getElementString(child, "desc", "", false);
                sub.objState = JdomParseUtil.getElementInt(child, "state", mo.getObjState(), false);
                retval.addSubs(sub);
            }
        }
        return retval;
    }

    public int getObjId()
    {
        return objId;
    }

    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public int getClassId()
    {
        return classId;
    }

    public void setClassId(int classId)
    {
        this.classId = classId;
    }

    public List<SubObjInsInfoEntry> getSubs()
    {
        return subs;
    }

    public void setSubs(List<SubObjInsInfoEntry> subs)
    {
        this.subs = subs;
    }

    /**
     * 内部类
     */
    public static class SubObjInsInfoEntry
    {
        /**
         * 名称
         */
        public String objName;
        /**
         * 附加信息
         */
        public String addInfo;
        /**
         * 描述
         */
        public String desc;
        /**
         * 状态。1，维护；0，管理。
         */
        public int objState;
        
        /**
         * 构造函数
         */
        public SubObjInsInfoEntry()
        {
            this.desc = "";
        }

        /**
         * @param sb 参数
         * @return 结果
         */
        public String toXml(StringBuilder sb)
        {
            if (sb == null)
            {
                sb = new StringBuilder();
            }
            XmlUtil.appendXmlBegin(sb, "Record");
            XmlUtil.appendTextNode(sb, "name", objName);
            XmlUtil.appendTextNode(sb, "info", addInfo);
            XmlUtil.appendXmlEnd(sb, "Record");
            return sb.toString();
        }
    }

}
