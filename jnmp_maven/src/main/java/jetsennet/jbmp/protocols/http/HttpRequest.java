/************************************************************************
 日 期：2012-5-24
 作 者: 郭祥
 版 本: v1.3
 描 述: HTTP请求
 历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP请求实体
 * @author 郭祥
 */
public class HttpRequest
{

    /**
     * 目标地址
     */
    public String urlString;
    /**
     * 方法
     */
    public String method;
    /**
     * 参数
     */
    public Map<String, String> params;
    /**
     * 头部参数
     */
    public Map<String, String> headParams;

    public HttpRequest()
    {
        params = new LinkedHashMap<String, String>();
        headParams = new LinkedHashMap<String, String>();
    }

    public HttpRequest(String url, String method)
    {
        this.urlString = url;
        this.method = method;
        params = new LinkedHashMap<String, String>();
        headParams = new LinkedHashMap<String, String>();
    }

    /**
     * 生成简单HTTP请求 获取方式为GET 能附带JSESSIONID
     * @param url
     * @param sessionId
     */
    public static HttpRequest genRequest(String url, String sessionId)
    {
        HttpRequest httpReq = new HttpRequest();
        httpReq.method = "GET";
        httpReq.urlString = url;
        httpReq.headParams.put("User-Agent", "Mozilla/4.08 (compatible;EIS iPanel 2.0;Linux2.4.26/mips;win32; HI3110)");
        httpReq.headParams.put("Accept", "*/*");
        httpReq.headParams.put("Accept-Encoding", "identity");
        httpReq.headParams.put("Connection", "Keep-Alive");
        httpReq.headParams.put("Proxy-Connection", "Keep-Alive");
        httpReq.headParams.put("Cookie", "JSESSIONID=" + sessionId);
        return httpReq;
    }
}
