package jetsennet.jbmp.util;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * @version 1.0 date 2011-12-15上午11:07:52
 * @author xli
 */
public class PathUtils
{

    /**
     * 解析参数
     * @param str <code>PATH:cop;WQL:wql;KEYVALUE=keyvalue;KEY=key;</code> PATH和KEYVALUE是必须的
     * @return SMIBean
     */
    public static SMIBean getStr(String str)
    {
        if (!str.startsWith(SMIParmDefine.PATH))
        {
            return null;
        }
        else if (str.startsWith(SMIParmDefine.PATH))
        {
            String[] params;
            SMIBean bean = new SMIBean();
            if ((params = str.split(";")).length > 3)
            {
                String path = params[0].substring(params[0].indexOf(SMIParmDefine.PATH) + SMIParmDefine.PATH.length(), params[0].length());
                String wql = params[1].substring(params[1].indexOf(SMIParmDefine.WQL) + SMIParmDefine.WQL.length(), params[1].length());
                String keyValue =
                    params[2].substring(params[2].indexOf(SMIParmDefine.KEYVALUE) + SMIParmDefine.KEYVALUE.length(), params[2].length());
                String key = params[3].substring(params[3].indexOf(SMIParmDefine.KEY) + SMIParmDefine.KEY.length(), params[3].length());
                bean.setPath(path);
                bean.setWql(wql);
                bean.setKey(key);
                bean.setKeyValue(keyValue);
                return bean;
            }
            else if ((params = str.split(";")).length > 1)
            {
                String path = params[0].substring(params[0].indexOf(SMIParmDefine.PATH) + SMIParmDefine.PATH.length(), params[0].length());
                String keyValue =
                    params[1].substring(params[1].indexOf(SMIParmDefine.KEYVALUE) + SMIParmDefine.KEYVALUE.length(), params[1].length());
                bean.setPath(path);
                bean.setKeyValue(keyValue);
                return bean;
            }
        }
        return null;
    }

    /**
     * 解析参数
     * @param objAttrLst 参数
     * @return 结果
     */
    public static List<SMIBean> getStr(List<ObjAttribEntity> objAttrLst)
    {
        List<SMIBean> list = new ArrayList<SMIBean>();
        for (ObjAttribEntity obj : objAttrLst)
        {
            list.add(getStr(obj.getAttribParam()));
        }
        return list;
    }
}
