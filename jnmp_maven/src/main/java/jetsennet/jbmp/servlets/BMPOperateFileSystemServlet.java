package jetsennet.jbmp.servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ？
 */
public class BMPOperateFileSystemServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        String operation = request.getParameter("type");
        String[] filepaths = request.getParameterValues("filePath");
        if (operation != null)
        {
            if ("delete".equalsIgnoreCase(operation))
            {
                deleteFiles(filepaths);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    private void deleteFiles(String[] filepaths)
    {
        String rootPath = getServletContext().getRealPath("/");
        for (int i = 0; i < filepaths.length; i++)
        {
            File file = new File(rootPath + filepaths[i]);
            if (file.exists())
            {
                file.delete();
            }
        }
    }
}
