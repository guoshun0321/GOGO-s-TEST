package jetsennet.jsmp.nav.service.a7;

import javax.servlet.http.HttpServletResponse;

public class ErrorHandle
{

    public static final HttpServletResponse illegalRequest(HttpServletResponse resp)
    {
        resp.setStatus(400);
        return resp;
    }

}
