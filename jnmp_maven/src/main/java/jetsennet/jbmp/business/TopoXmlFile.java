/************************************************************************
日 期：2011-11-28
作 者: 余灵
版 本：v1.3
描 述: 拓扑图XML文件相关
历 史： 
 ************************************************************************/
package jetsennet.jbmp.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;

/**
 * @author 余灵
 */
public class TopoXmlFile
{
    /**
     * 构造函数
     */
    public TopoXmlFile()
    {

    }

    /**
     * 从服务器上读xml文件
     * @param filePath 文件路径
     * @return 返回XML文件；-2：文件不存在；-1：异常；
     * @throws Exception 异常
     */
    public String readTopoMapFile(String filePath) throws Exception
    {
        String sReturn = "";
        Document document = null;

        try
        {
            String sRequestPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("../../").getPath(), "UTF-8");
            String path = sRequestPath + filePath;
            File f = new File(path);

            if (f.exists())
            {
                FileReader fr = new FileReader(f);
                SAXReader saxReader = new SAXReader();
                document = saxReader.read(fr);
                sReturn = document.asXML();
            }
            else
            {
                sReturn = "-2"; // 文件不存在
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            sReturn = "-1"; // 读文件异常
        }

        return sReturn;
    }

    /**
     * 在服务器上写xml文件
     * @param fileName 文件名
     * @param fileType 文件类型
     * @param fileInfo 文件信息
     * @return 返回文件保存路径；-1：异常；
     * @throws Exception 异常
     */
    public String writeTopoMapFile(String fileType, String fileName, String fileInfo) throws Exception
    {
        try
        {
            String sPath = "upload/"; // 指定保存路径
            String fName = fileName + ".xml"; // 文件名

            if ("BMP_TOPOMAP".equals(fileType))
            {
                sPath = sPath + "TopoMapFile/";
                fName = "Topo_" + fName;
            }
            else if ("10".equals(fileType)) // 10：拓扑图模板；
            {
                sPath = sPath + "TopoTempFile/";
                fName = "TopoTemp_" + fName;
            }
            else if ("20".equals(fileType)) // 20：分类模板；
            {
                sPath = sPath + "ClassTempFile/";
                fName = "ClassTemp_" + fName;
            }
            else if ("30".equals(fileType)) // 30：设备模板；
            {
                sPath = sPath + "DeviceTempFile/";
                fName = "DeviceTemp_" + fName;
            }
            else if ("BMP_HOMEPAGE".equals(fileType)) // 主页
            {
                sPath = sPath + "HomePageFile/";
                fName = "HomePage_" + fName;
            }
            else if ("TSMap".equals(fileType)) // 码流一级页面
            {
                sPath = sPath + "TSMapFile/";
            }
            else if ("NMP_FLOWCHART".equals(fileType))
            {
                sPath = sPath + "FlowChartFile/";
                fName = "Flow_" + fName;
            }

            Document doc = DocumentHelper.parseText(fileInfo);
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");

            String sRequestPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("../../").getPath(), "UTF-8");
            String realPath = sRequestPath + sPath + fName;
            File f = new File(sRequestPath + sPath);
            if (!f.exists()) // 如果文件夹不存在，新建
            {
                f.mkdirs();
            }

            Writer writer = new OutputStreamWriter(new FileOutputStream(new File(realPath)));
            doc.write(writer);
            writer.close();

            return sPath + fName;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "-1";
        }
    }

    /**
     * 从服务器上删除XML文件
     * @param filePath 文件路径
     * @return 1：删除成功； -1：删除不成功或异常； -2：文件不存在；
     * @throws Exception 异常
     */
    public String deleteTopoMapFile(String filePath) throws Exception
    {
        String sReturn = "";

        try
        {
            String sRequestPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("../../").getPath(), "UTF-8");
            String path = sRequestPath + filePath;
            File f = new File(path);

            if (f.exists())
            {
                if (f.isFile())
                {
                    if (f.canWrite())
                    {
                        boolean flag = f.delete();
                        if (flag)
                        {
                            sReturn = "1";
                        }
                        else
                        {
                            sReturn = "-1";
                        }
                    }
                }
            }
            else
            {
                sReturn = "-2"; // 文件不存在
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            sReturn = "-1"; // 删除文件异常
        }

        return sReturn;
    }

}
