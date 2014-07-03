package jetsennet.jbmp.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class KindEditorUploadServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private Logger logger = Logger.getLogger(KindEditorUploadServlet.class);

  private String attachedDir = "upload/knowledge/kindEditorAttached/";

  private long maxSize = 1000000L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
  {
    try
    {
      request.setCharacterEncoding("UTF-8");
    } catch (UnsupportedEncodingException e2) {
      this.logger.error("转码失败！" + e2.getMessage());
      e2.printStackTrace();
    }
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = null;
    try {
      out = response.getWriter();
    } catch (IOException e2) {
      this.logger.error("获取输出对象out失败！" + e2.getMessage());
      e2.printStackTrace();
    }

    String savePath = request.getRealPath("/") + this.attachedDir;

    String saveUrl = request.getContextPath() + "/" + this.attachedDir;

    HashMap extMap = new HashMap();
    extMap.put("image", "gif,jpg,jpeg,png,bmp");
    extMap.put("flash", "swf,flv");
    extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
    extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

    if (!ServletFileUpload.isMultipartContent(request)) {
      out.println(getError("请选择文件。"));
      return;
    }

    String dirName = request.getParameter("dir");
    if (dirName == null) {
      dirName = "image";
    }

    if (!extMap.containsKey(dirName)) {
      out.println(getError("目录名不正确。"));
      return;
    }

    savePath = savePath + dirName + "/";
    saveUrl = saveUrl + dirName + "/";
    File saveDirFile = new File(savePath);
    if (!saveDirFile.exists()) {
      saveDirFile.mkdirs();
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String ymd = sdf.format(new Date());
    savePath = savePath + ymd + "/";
    saveUrl = saveUrl + ymd + "/";
    File dirFile = new File(savePath);
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }

    FileItemFactory factory = new DiskFileItemFactory();
    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setHeaderEncoding("UTF-8");
    List items = new ArrayList();
    try {
      items = upload.parseRequest(request);
    } catch (FileUploadException e1) {
      this.logger.error("无法解析请求！" + e1.getMessage());
      e1.printStackTrace();
    }
    Iterator itr = items.iterator();
    while (itr.hasNext()) {
      FileItem item = (FileItem)itr.next();
      String fileName = item.getName();
      long fileSize = item.getSize();

      if (item.isFormField())
        continue;
      if (item.getSize() > this.maxSize) {
        out.println(getError("上传文件大小超过限制。"));
        return;
      }

      String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
      if (!Arrays.asList(((String)extMap.get(dirName)).split(",")).contains(fileExt)) {
        out.println(getError("上传文件扩展名是不允许的扩展名。\n只允许" + (String)extMap.get(dirName) + "格式。"));
        return;
      }

      SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
      String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
      try {
        File uploadedFile = new File(savePath, newFileName);
        item.write(uploadedFile);
      } catch (Exception e) {
        out.println(getError("上传文件失败。"));
        return;
      }

      JSONObject obj = new JSONObject();
      obj.put("error", Integer.valueOf(0));
      obj.put("url", saveUrl + newFileName);
      out.println(obj.toJSONString());
    }
  }

  private String getError(String message)
  {
    JSONObject obj = new JSONObject();
    obj.put("error", Integer.valueOf(1));
    obj.put("message", message);
    return obj.toJSONString();
  }
}