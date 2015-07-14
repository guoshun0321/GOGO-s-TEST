package jetsennet.jbmp.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;

public class BMPExportDuty extends HttpServlet
{

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("排班表.xls", "UTF-8"));
        try
        {
            final ResultSet mySet;
            HSSFWorkbook workbook = new HSSFWorkbook();
            final HSSFSheet sheet = workbook.createSheet("值班排班");
            // 创建表头
            HSSFRow row = sheet.createRow(0);
            HSSFCell userNameCell = row.createCell(0);
            userNameCell.setCellValue("用户姓名");
            HSSFCell startTimeCell = row.createCell(1);
            startTimeCell.setCellValue("开始时间");
            HSSFCell endTimeCell = row.createCell(2);
            endTimeCell.setCellValue("结束时间");
            HSSFCell descCell = row.createCell(3);
            descCell.setCellValue("值班描述");

            String sql = "select a.*,b.USER_NAME from BMP_DUTY a inner join UUM_USER b on a.USER_ID = b.ID order by START_TIME";
            DefaultDal.read(sql, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    int index = 0;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    while (rs.next())
                    {
                        index++;
                        HSSFRow row = sheet.createRow(index);
                        HSSFCell cell0 = row.createCell(0);
                        cell0.setCellValue(rs.getString("USER_NAME"));
                        HSSFCell cell1 = row.createCell(1);
                        cell1.setCellValue(dateFormat.format(rs.getTimestamp("START_TIME")));
                        HSSFCell cell2 = row.createCell(2);
                        cell2.setCellValue(dateFormat.format(rs.getTimestamp("END_TIME")));
                        HSSFCell cell3 = row.createCell(3);
                        cell3.setCellValue(rs.getString("DUTY_DESC"));
                    }
                }

            });
            workbook.write(response.getOutputStream());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
