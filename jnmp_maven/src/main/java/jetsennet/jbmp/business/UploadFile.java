/************************************************************************
日 期：2012-2-16
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author yl
 */
public class UploadFile
{
    /**
     * 构造函数
     */
    public UploadFile()
    {
    }

    /**
     * 读取指定文件夹下的文件
     * @param uploadPath 上传相对路径
     * @return XML字符串形式返回文件列表
     * @throws FileNotFoundException 异常
     * @throws IOException 异常
     * @throws DocumentException 异常
     */
    public String readUploadFile(String uploadPath) throws FileNotFoundException, IOException, DocumentException
    {
        // 定义一个Document作为返回结果
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FileLists></FileLists>";
        Document document = DocumentHelper.parseText(result);

        // 定义根节点
        Element root = document.getRootElement();

        // 获取存储的根路径
        String sRequestPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("../../").getPath(), "UTF-8");
        String filePath = sRequestPath + uploadPath;

        // 获取该路径下所有的文件，并添加元素到根节点
        if (filePath != null)
        {
            File f = new File(filePath);

            if (f.exists() && f.isDirectory())
            {
                File[] li = f.listFiles();

                // 循环获取该路径下所有的文件
                for (File ff : li)
                {
                    if (ff.isFile())
                    {
                        // 添加文件名和路径到根节点
                        Element ele = root.addElement("File");
                        ele.addAttribute("name", ff.getName());
                        ele.addAttribute("path", ff.getPath());

                        // 判断如果是图片，则返回宽、高属性
                        ImageInputStream iis = ImageIO.createImageInputStream(ff);
                        try
                        {
                            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                            if (readers.hasNext())
                            {
                                ImageReader reader = readers.next();
                                reader.setInput(iis, true);
                                ele.addAttribute("width", String.valueOf(reader.getWidth(0)));
                                ele.addAttribute("height", String.valueOf(reader.getHeight(0)));
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        finally
                        {
                            if (iis != null)
                            {
                                iis.close();
                            }
                        }

                    }
                }
            }
        }

        return document.asXML();
    }

    /**
     * 删除指定的上传的文件
     * @param uploadPath 上传相对路径。
     * @param fileName 要删除的文件名
     * @return 返回删除结果
     * @throws URISyntaxException 异常
     * @throws UnsupportedEncodingException 异常
     */
    public boolean deleteUploadFile(String uploadPath, String fileName) throws URISyntaxException, UnsupportedEncodingException
    {
        boolean flag = false;

        // 获取存储的根路径
        String sRequestPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("../../").getPath(), "UTF-8");
        String projPath = sRequestPath + uploadPath;

        // 删除该路径下指定名称的文件
        if (projPath != null)
        {
            File f = new File(projPath);

            if (f.exists() && f.isDirectory())
            {
                File[] li = f.listFiles();

                // 过滤所有文件，删除指定文件名的文件
                for (File ff : li)
                {
                    if (ff.isFile() && ff.getName().equals(fileName))
                    {
                        flag = ff.delete();
                    }
                }
            }
        }

        return flag;
    }
}
