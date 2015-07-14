/************************************************************************
日 期：2011-12-29
作 者: 郭祥
版 本：v1.3
描 述: 文件相关的工具类
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import jetsennet.jbmp.mib.MibUtil;

/**
 * 文件相关的工具类
 * @author 郭祥
 */
public class FileUtil
{

    private static final Logger logger = Logger.getLogger(FileUtil.class);

    /**
     * 获取给定目录下的文件，只获取该目录下的文件
     * @param path 路径
     * @return 找不到文件时返回空集合
     */
    public static List<File> getFiles(String path)
    {
        List<File> retval = new ArrayList<File>();
        if (path == null)
        {
            return retval;
        }
        File file = new File(path);
        if (file != null)
        {
            if (file.exists() && file.isDirectory())
            {
                File[] files = file.listFiles();
                for (File f : files)
                {
                    if (f.isFile())
                    {
                        retval.add(f);
                    }
                }
            }
        }
        return retval;
    }

    /**
     * 获取给定目录下的文件，包括子目录下的文件
     * @param dir 目录
     * @return 结果
     */
    public static ArrayList<File> getDirFile(File dir)
    {
        ArrayList<File> result = new ArrayList<File>();
        ArrayList<File> stack = new ArrayList<File>();
        if (dir.exists())
        {
            if (dir.isDirectory())
            {
                stack.add(dir);
            }
            else
            {
                result.add(dir);
            }
        }
        while (!stack.isEmpty())
        {
            File tempDir = stack.get(0);
            File[] list = tempDir.listFiles();
            for (File f : list)
            {
                if (f.isFile())
                {
                    result.add(f);
                }
                else if (f.isDirectory())
                {
                    stack.add(f);
                }
            }
            stack.remove(0);
        }
        return result;
    }

    /**
     * 使用XXXX;XXXX;XXXX的样式打印文件
     * @param files 文件
     * @return 结果
     */
    public static String fileList(File[] files)
    {
        StringBuilder sb = new StringBuilder();
        for (File file : files)
        {
            sb.append(file.getPath());
            sb.append(";");
        }
        if (sb.length() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 文件或文件夹不存在时，创建文件或文件夹
     * @param file 文件
     * @param isDir 目录
     * @throws IOException 异常
     */
    public static void createIfNotExist(File file, boolean isDir) throws IOException
    {
        if (!file.exists())
        {
            if (file.getParentFile() != null && !file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
            if (isDir)
            {
                file.mkdirs();
            }
            else
            {
                file.createNewFile();
            }
        }
    }

    /**
     * 获取文件路径，TOMCAT下基准目录为 ：项目目录\WEB-INF\classes 目录
     * @param path 路径
     * @return 结果
     */
    public static String getFilePath(String path)
    {
        String retval = null;
        try
        {
            retval = java.net.URLDecoder.decode(MibUtil.class.getClassLoader().getResource("../../").getPath(), "UTF-8");
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 读取文件
     * @param path 路径
     * @return 结果
     */
    public static String readFile(String path)
    {
        if (path == null || path.isEmpty())
        {
            return null;
        }
        File file = new File(path);
        System.out.println(file.getAbsolutePath());
        if (file.exists() && file.isFile())
        {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;
            try
            {
                // BufferedInputStream bio = new BufferedInputStream(new FileInputStream(file));
                br = new BufferedReader(new FileReader(file));
                String temp = null;
                while ((temp = br.readLine()) != null)
                {
                    sb.append(temp);
                    sb.append("\n");
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (Exception ex)
                    {
                        logger.error("", ex);
                    }
                    finally
                    {
                        br = null;
                    }
                }
            }
            return sb.toString();
        }
        else
        {
            return null;
        }
    }

    /**
     * 读取文件
     * @param path 路径
     * @return 结果
     */
    public static String readFile(File file)
    {
        if (file == null)
        {
            return null;
        }
        if (file.exists() && file.isFile())
        {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;
            try
            {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                String temp = null;
                while ((temp = br.readLine()) != null)
                {
                    sb.append(temp);
                    sb.append("\n");
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (Exception ex)
                    {
                        logger.error("", ex);
                    }
                    finally
                    {
                        br = null;
                    }
                }
            }
            return sb.toString();
        }
        else
        {
            return null;
        }
    }

    /**
     * 根据包路径查找文件，找不到文件时返回NULL
     * @param path 路径
     * @return 结果
     */
    public static File ensureFile(String path)
    {
        if (path == null)
        {
            return null;
        }
        File retval = null;
        URL url = FileUtil.class.getResource(path);
        if (url != null)
        {
            try
            {
                retval = new File(url.toURI());
            }
            catch (Exception ex)
            {
                logger.error("", ex);
                retval = null;
            }
        }
        return retval;
    }
    /**
     * 根据图片文件路径获取文件宽度和高度
     * @param path 路径
     * @return 结果
     */
    public static int[] getNetImageWidthHeight(String imageUrl)
    {
        BufferedImage image=getBufferedImage(imageUrl);
        int[] result=new int[2];
        if (image!=null)
        {
            result[0]=image.getHeight();
            result[1]=image.getWidth();
        }
        else
        {
            System.out.println("图片不存在！");
        }
        return result;
    }

    /**
     * 
     * @param imgUrl 图片地址
     * @return 
     */
    public static BufferedImage getBufferedImage(String imgUrl) {
        InputStream is = null;
        BufferedImage img = null;
        try {
            is = new FileInputStream(new File(imgUrl));
            img = ImageIO.read(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }
    /**
     * @param args 参数
     * @throws URISyntaxException 异常
     */
    public static void main(String[] args) throws URISyntaxException
    {
        String temp = "/jetsennet/jmmp/protocols/http/replace/play.template";
        // temp = "/jetsennet";
        URL url = FileUtil.class.getResource(temp);
        File f = new File(url.toURI());
        System.out.println(FileUtil.readFile(f));
    }
}
