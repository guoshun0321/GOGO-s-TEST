/************************************************************************
日 期：2012-3-31
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import jetsennet.util.StringUtil;

/**
 * @author yl
 */
public class TSTopoFile
{
    /**
     * 构造函数
     */
    public TSTopoFile()
    {

    }

    /**
     * 保存码流监控图
     * @param topoXml 图XML、
     * @param xmlName 名称
     * @return 结果
     * @throws Exception 异常
     */
    public String saveTSLevelTopo(String xmlName, String topoXml) throws Exception
    {
        try
        {
            String sPath = new TopoXmlFile().writeTopoMapFile("TSMap", xmlName, topoXml);
            return sPath;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "-1";
        }
    }

    /**
     * 删除码流监控图
     * @param objIds 对象ids
     * @return 结果
     * @throws Exception 异常
     */
    public String deleteTSLevelTopo(String objIds) throws Exception
    {
        try
        {
            String sPath = "1";

            String[] objIdArr = objIds.split(",");
            if (objIdArr != null && objIdArr.length > 0)
            {
                String result = "";
                for (String objId : objIdArr)
                {
                    if (!StringUtil.isNullOrEmpty(objId))
                    {
                        result = new TopoXmlFile().deleteTopoMapFile("upload/TSMapFile/TS_Level2_" + objId + ".xml");
                        if ("-1".equals(result))
                        {
                            sPath = result;
                        }
                    }
                }
            }

            return sPath;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "-1";
        }
    }

    /**
     * 读取码流监控图
     * @param xmlName 名称
     * @return 结果
     * @throws Exception 异常
     */
    public String readTSLevelTopo(String xmlName) throws Exception
    {
        String sReturn = "";

        try
        {
            sReturn = new TopoXmlFile().readTopoMapFile("upload/TSMapFile/" + xmlName + ".xml");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            sReturn = "-1"; // 读文件异常
        }

        return sReturn;
    }
}
