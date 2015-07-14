/************************************************************************
 日 期：2012-5-24
 作 者: 郭祥
 版 本: v1.3
 描 述: HTTP响应
 历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP响应实体
 * @author 郭祥
 */
public class HttpResponse
{

    /**
     * 返回的状态码
     */
    public int code;
    /**
     * HTTP state description
     */
    public String message;
    /**
     * 头部信息
     */
    public Map<String, String> headerParams = new HashMap<String, String>();
    /**
     * 原始内容，按byte存储
     */
    public byte[] content;
    /**
     * 重定向地址
     */
    public String redirect;
    /**
     * 请求类型
     */
    public String contentType;
    /**
     * 连接的超时事件
     */
    public int connectTimeout;
    /**
     * 读取的超时时间
     */
    public int readTimeout;
    /**
     * 状态码，成功
     */
    public static final int CODE_OK = 200;

    public HttpResponse()
    {
    }

    /**
     * 判断返回状态码是否表示重定向
     * @param code
     * @return
     */
    public static boolean isRedirect(int code)
    {
        if (code == 301 || code == 302 || code == 304)
        {
            return true;
        }
        return false;
    }

    /**
     * 判断返回状态码是否表示OK
     * @param code
     * @return
     */
    public static boolean isOK(int code)
    {
        if (code == 200 || code == 201 || code == 202)
        {
            return true;
        }
        return false;
    }
}
