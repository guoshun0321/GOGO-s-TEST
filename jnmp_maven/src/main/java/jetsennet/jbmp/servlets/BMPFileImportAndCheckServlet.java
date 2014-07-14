/************************************************************************
日 期：2012-8-9
作 者: 郭世平
描 述: 批量导入对象，文件类型未excel，先检查文件是否符合格式，然后再执行导入，将数据插入到数据库，并实例化
历 史：
 ************************************************************************/
package jetsennet.jbmp.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import jetsennet.jbmp.business.MObject;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.InsManager;
import jetsennet.util.StringUtil;

public class BMPFileImportAndCheckServlet extends HttpServlet
{
    // 限制文件的上传大小 ,10M
    private int maxPostSize = 5 * 1024 * 1024;
    // 文件名
    private String fileName;
    // 文件路径
    private String uploadPath;
    // 采集器Id
    private String collId;
    // 采集组Id
    private String collGroupId;
    // 类别ID
    private String classId;
    // 类别
    private String classType;
    // 创建人
    private String createUser;
    // 用户ID
    private String userId;
    private List<Integer> objectIds;

    public BMPFileImportAndCheckServlet()
    {
        super();
    }

    public void destroy()
    {
        super.destroy();
    }

    /**
     * @param request
     * @param response
     * @throws Exception
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        System.out.println("开始上传！");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("utf-8");
        upload.setSizeMax(maxPostSize);

        try
        {
            List fileItems = upload.parseRequest(request);
            Iterator iter = fileItems.iterator();
            while (iter.hasNext())
            {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField())
                {
                    String name = item.getName();
                    System.out.println(name);

                    // 若指定了其他名字，则覆盖原来的文件名
                    if (!StringUtil.isNullOrEmpty(fileName))
                    {
                        name = fileName;
                    }

                    String rootPath = request.getRealPath("/");
                    String filePath = rootPath + uploadPath;

                    // 若该路径目录不存在，则创建
                    File f = new File(filePath);
                    if (!f.exists())
                    {
                        f.mkdirs();
                    }

                    File f2 = new File(filePath + File.separator + name);
                    try
                    {
                        item.write(f2);
                        if (!checkImportFile(request, response, f2))// 检查文件不通过
                        {
                            out.print("1导入的文件格式不对！");
                        }
                        else
                        {
                            addObjectFromFile(f2);
                            out.print("导入成功！");
                        }

                    }
                    catch (Exception e)
                    {
                        rollBack(objectIds);
                        e.printStackTrace();
                        out.print("2文件格式不对或执行导入文件出错！");
                    }
                }
            }
        }
        catch (SizeLimitExceededException es)
        {
            es.printStackTrace();
            out.print("错误：文件大小不能超过5M");
        }
        catch (FileUploadException e)
        {
            e.printStackTrace();
            out.print(e.getMessage());
            System.out.println("错误：" + e.getMessage());
        }
        finally
        {
            out.close();
            objectIds = null;
        }

    }

    /**
     * 重写doGet方法
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        request.setCharacterEncoding("utf-8");
        try
        {
            fileName = request.getParameter("fileName");
            uploadPath = request.getParameter("uploadPath");
            collId = request.getParameter("coll_id");
            collGroupId = request.getParameter("collgroup_id");
            classId = request.getParameter("class_id");
            classType = request.getParameter("class_type");
            createUser = java.net.URLDecoder.decode(request.getParameter("create_user"), "UTF-8");
            userId = request.getParameter("user_id");
            processRequest(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 重写doPost方法
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        request.setCharacterEncoding("utf-8");
        try
        {
            fileName = request.getParameter("fileName");
            uploadPath = request.getParameter("uploadPath");
            collId = request.getParameter("coll_id");
            collGroupId = request.getParameter("collgroup_id");
            classId = request.getParameter("class_id");
            classType = request.getParameter("class_type");
            createUser = java.net.URLDecoder.decode(request.getParameter("create_user"), "UTF-8");
            userId = request.getParameter("user_id");
            processRequest(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取对象，并插入到数据库中
     * @param file
     * @return
     * @throws Exception
     */
    private boolean addObjectFromFile(File file) throws Exception
    {
        FileInputStream inputStream = null;
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        HSSFSheet sheet = null;
        HSSFRow row = null;
        boolean flag = true;
        objectIds = new ArrayList<Integer>();
        if (file == null)
        {
            flag = false;
        }
        else
        {
            try
            {
                inputStream = new FileInputStream(file);
                fs = new POIFSFileSystem(inputStream);
                wb = new HSSFWorkbook(fs);
                sheet = wb.getSheetAt(0);

                int rownum = sheet.getLastRowNum();// 总行数
                // int colnum = row.getPhysicalNumberOfCells();//得到标题总列数
                for (int i = 1; i <= rownum; i++)
                {
                    HashMap<String, String> model = new HashMap<String, String>();
                    row = sheet.getRow(i);
                    model.put("CLASS_TYPE", classType);
                    model.put("OBJ_NAME", getStringCellValue(row.getCell(0)));
                    model.put("OBJ_STATE", "0");
                    if (row.getCell(1) == null)
                    {
                        model.put("IP_ADDR", "");
                    }
                    else
                    {
                        model.put("IP_ADDR", getStringCellValue(row.getCell(1)));
                    }

                    model.put("COLL_ID", collId);
                    model.put("COLLGROUP_ID", collGroupId);
                    if (row.getCell(2) == null)
                    {
                        model.put("IP_PORT", "");
                    }
                    else
                    {
                        model.put("IP_PORT", getStringCellValue(row.getCell(2)));
                    }
                    if (row.getCell(3) == null)
                    {
                        model.put("OBJ_DESC", "");
                    }
                    else
                    {
                        model.put("OBJ_DESC", getStringCellValue(row.getCell(3)));
                    }
                    model.put("CHECKUSEFUL", "60");
                    if (row.getCell(4) == null)
                    {
                        model.put("USER_NAME", "");
                    }
                    else
                    {
                        model.put("USER_NAME", getStringCellValue(row.getCell(4)));
                    }
                    model.put("VERSION", "snmpv1");
                    if (row.getCell(5) == null)
                    {
                        model.put("USER_PWD", "");
                    }
                    else
                    {
                        model.put("USER_PWD", getStringCellValue(row.getCell(5)));
                    }
                    model.put("CREATE_USER", createUser);
                    model.put("CLASS_ID", classId);
                    model.put("PARENT_ID", "0");
                    model.put("USER_ID", userId);
                    MObjectEntity mo = ClassWrapper.wrap(MObject.class).addObjFromMap(model);
                    objectIds.add(mo.getObjId());
                    InsManager.getInstance().autoIns(mo, null, Integer.parseInt(model.get("COLLGROUP_ID")), Integer.parseInt(model.get("COLL_ID")),
                        Integer.parseInt(model.get("USER_ID")), null, false);
                }
            }
            finally
            {
                try
                {
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 检查上传的文件是否符合定制规范
     * @param request
     * @param response
     * @param file
     * @return
     * @throws Exception
     */
    private boolean checkImportFile(HttpServletRequest request, HttpServletResponse response, File file) throws Exception
    {
        boolean flag = true;
        if (!checkExcelTitle(file))
        {
            flag = false;
        }
        else if (!checkExcelContnet(file))
        {
            flag = false;
        }
        return flag;
    }

    /**
     * 检查上传的excel文件的头
     * @param file
     * @return
     * @throws Exception
     */
    private boolean checkExcelTitle(File file) throws Exception
    {
        FileInputStream inputStream = null;
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        HSSFSheet sheet = null;
        HSSFRow row = null;
        boolean flag = true;
        if (file == null)
        {
            flag = false;
        }
        else
        {
            try
            {
                inputStream = new FileInputStream(file);
                fs = new POIFSFileSystem(inputStream);
                wb = new HSSFWorkbook(fs);
                sheet = wb.getSheetAt(0);
                row = sheet.getRow(0);// 得到excel标题
                int colnum = row.getPhysicalNumberOfCells();// 得到标题总列数
                if (colnum != 6)// 定义excel为12列
                {
                    flag = false;
                }
            }
            finally
            {
                try
                {
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 检查上传excel文件的的内容
     * @param file
     * @return
     * @throws Exception
     */
    private boolean checkExcelContnet(File file) throws Exception
    {
        FileInputStream inputStream = null;
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        HSSFSheet sheet = null;
        HSSFRow row = null;
        boolean flag = true;
        if (file == null)
        {
            flag = false;
        }
        else
        {
            try
            {
                inputStream = new FileInputStream(file);
                fs = new POIFSFileSystem(inputStream);
                wb = new HSSFWorkbook(fs);
                sheet = wb.getSheetAt(0);

                int rownum = sheet.getLastRowNum();// 总行数
                // int colnum = row.getPhysicalNumberOfCells();//得到标题总列数
                for (int i = 1; i <= rownum; i++)
                {
                    row = sheet.getRow(i);
                    String objName = getStringCellValue(row.getCell(0));
                    if (objName == "" || objName == null)
                    {
                        flag = false;
                        break;
                    }
                }
            }
            finally
            {
                try
                {
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     * @param cell
     * @return
     */
    private String getStringCellValue(HSSFCell cell)
    {
        String strCell = "";
        switch (cell.getCellType())
        {
        case HSSFCell.CELL_TYPE_STRING:
            strCell = cell.getStringCellValue();
            break;
        case HSSFCell.CELL_TYPE_NUMERIC:
            strCell = String.valueOf(cell.getNumericCellValue());
            break;
        case HSSFCell.CELL_TYPE_BOOLEAN:
            strCell = String.valueOf(cell.getBooleanCellValue());
            break;
        case HSSFCell.CELL_TYPE_BLANK:
            strCell = "";
            break;
        default:
            strCell = "";
            break;
        }
        if (strCell.equals("") || strCell == null)
        {
            return "";
        }
        if (cell == null)
        {
            return "";
        }
        return strCell;
    }

    /**
     * 插入失败的时候所有数据回滚
     * @param list
     */
    private static void rollBack(List<Integer> list)
    {
        MObjectDal objectDal = ClassWrapper.wrap(MObjectDal.class);
        String str = "";
        for (Integer i : list)
        {
            str += i + ",";
        }
        str = str.substring(0, str.lastIndexOf(","));
        String sql = "DELETE FROM BMP_OBJECT WHERE OBJ_ID IN (" + str + ")";
        try
        {
            objectDal.delete(sql);
        }
        catch (Exception e)
        {
            System.out.println("删除失败");
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        rollBack(list);
    }

}
