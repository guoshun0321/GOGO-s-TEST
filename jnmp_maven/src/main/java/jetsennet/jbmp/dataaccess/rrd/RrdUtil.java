package jetsennet.jbmp.dataaccess.rrd;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.util.JdomParseUtil;

/**
 * @author ？
 */
public class RrdUtil
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(RrdUtil.class);

    /**
     * 通过xml设置值。成功时返回1，失败时返回0。
     * @param objId 对象id
     * @param xml 参数
     * @return 结果
     */
    public synchronized static int setValueFromXml(int objId, String xml)
    {
        int retval = 1;
        SAXBuilder sax = new SAXBuilder();
        InputStream in = null;
        try
        {
            in = new BufferedInputStream(new ByteArrayInputStream(xml.getBytes()));
            Document doc = sax.build(in);
            Element root = doc.getRootElement();
            List<Element> children = root.getChildren("objAttr");
            Map<String, Double> rrdMap = new HashMap<String, Double>();
            for (Element child : children)
            {
                String objAttrId = JdomParseUtil.getElementString(child, "objAttrId", "name", true);
                int value = JdomParseUtil.getElementInt(child, "objAttrValue", -1, true);
                rrdMap.put(objAttrId, (double) value);
            }
            checkRrdFile(objId);
            RrdHelper.getInstance().save(Integer.toString(objId), new Date(), rrdMap);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = 0;
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    in = null;
                }
            }
        }
        return retval;
    }

    /**
     * 检查RRD文件
     * @param objId 参数
     */
    public static void checkRrdFile(int objId)
    {

        ObjAttribDal oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        ArrayList<ObjAttribEntity> oas = oadal.getCollObjAttribByID(objId);

        // 计算需要保存性能数据的对象属性ID列表
        List<String> oaIdLst = new ArrayList<String>(oas.size());
        for (ObjAttribEntity oa : oas)
        {
            oaIdLst.add(Integer.toString(oa.getObjAttrId()));
        }

        // 检查rrd文件是否存在|是否需要更新
        int interval = computeInterval(oas);
        if (oaIdLst.size() > 0)
        {
            try
            {
                RrdHelper.getInstance().checkRrdFile(Integer.toString(objId), interval, oaIdLst.toArray(new String[oaIdLst.size()]));
            }
            catch (Exception e)
            {
                logger.error("监控对象:" + objId + "的rrd文件检查失败", e);
            }
        }
    }

    /**
     * 计算对象属性的采集间隔的最小公约数
     * @param attrs 属性
     * @return 结果
     */
    public static int computeInterval(ArrayList<ObjAttribEntity> attrs)
    {
        ObjAttribEntity first = attrs.get(0);
        if (first.getCollTimespan() <= 0)
        {
            first.setCollTimespan(300);
        }
        int temp = first.getCollTimespan();
        for (int i = 1; i < attrs.size(); i++)
        {
            if (attrs.get(i).getCollTimespan() <= 0)
            {
                attrs.get(i).setCollTimespan(300);
            }
            temp = gys(temp, attrs.get(i).getCollTimespan());
        }
        return temp;
    }

    /**
     * 计算最大公约数
     * @param paramA 参数
     * @param paramB 参数
     * @return 结果
     */
    public static int gys(int paramA, int paramB)
    {
        if (paramA > paramB)
        {
            if (paramA % paramB == 0)
            {
                return paramB;
            }
            else
            {
                return gys(paramB, paramA % paramB);
            }
        }
        else
        {
            if (paramB % paramA == 0)
            {
                return paramA;
            }
            else
            {
                return gys(paramA, paramB % paramA);
            }
        }
    }

}
