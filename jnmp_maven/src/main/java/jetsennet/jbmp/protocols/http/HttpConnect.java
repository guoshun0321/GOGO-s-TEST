/************************************************************************
 日 期：2012-5-24
 作 者: 郭祥
 版 本: v1.3
 描 述:
 历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jbmp.util.ConvertUtil;

import org.apache.log4j.Logger;

/**
 * 发送HTTP请求
 * @author 郭祥
 */
public class HttpConnect
{

    /**
     * 连接超时
     */
    private int connTimeout;
    /**
     * 读取超时
     */
    private int readTimeout;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(HttpConnect.class);

    public HttpConnect()
    {
        this.connTimeout = 5000;
        this.readTimeout = 5000;
    }

    public void setConnTimeOut(int connTimeout)
    {
        if (connTimeout > 0)
        {
            this.connTimeout = connTimeout;
        }
    }

    public void setReadTimeOut(int readTimeout)
    {
        if (readTimeout > 0)
        {
            this.readTimeout = readTimeout;
        }
    }

    /**
     * 发送GET请求
     * @param urlString URL地址
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendGet(String urlString) throws Exception
    {
        return this.send(urlString, "GET", null, null);
    }

    /**
     * 发送GET请求
     * @param urlString URL地址
     * @param params 参数集合
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params) throws Exception
    {

        return this.send(urlString, "GET", params, null);

    }

    /**
     * 发送GET请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param propertys 请求属性
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params, Map<String, String> headParams) throws Exception
    {
        return this.send(urlString, "GET", params, headParams);
    }

    /**
     * 发送POST请求
     * @param urlString URL地址
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendPost(String urlString) throws Exception
    {
        return this.send(urlString, "POST", null, null);
    }

    /**
     * 发送POST请求
     * @param urlString URL地址
     * @param params 参数集合
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendPost(String urlString, Map<String, String> headParams) throws Exception
    {
        return this.send(urlString, "POST", headParams, null);
    }

    /**
     * 发送POST请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param headParams 请求属性
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendPost(String urlString, Map<String, String> params, Map<String, String> headParams) throws Exception
    {
        return this.send(urlString, "POST", params, headParams);
    }

    /**
     * 发送请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param headParams 请求属性
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse send(String urlString, String method, Map<String, String> params, Map<String, String> headParams) throws Exception
    {
        HttpRequest req = new HttpRequest();
        req.urlString = urlString;
        req.method = method;
        req.params = params;
        req.headParams = headParams;
        return this.send(req);
    }

    /**
     * 发送HTTP请求
     */
    public HttpResponse send(HttpRequest req) throws Exception
    {
        HttpResponse retval = null;
        InputStream in = null;
        HttpURLConnection urlConn = null;

        try
        {
            String urlString = HttpUtil.ensureSendUrl(req);

            URL url = new URL(urlString);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setInstanceFollowRedirects(false);
            urlConn.setConnectTimeout(this.connTimeout);
            urlConn.setReadTimeout(this.readTimeout);
            HttpUtil.setConnInfo(req, urlConn);
            in = urlConn.getInputStream();

            retval = this.genResponse(urlConn);
            retval.content = this.getContent(in);
        }
        catch (Exception ex)
        {
            String msg = String.format("页面(%s)请求异常！", req.urlString);
            logger.error(msg, ex);
            retval = this.genResponse(urlConn);
            retval.content = this.getContent(in);
        }
        finally
        {
            if (urlConn != null)
            {
                try
                {
                    urlConn.disconnect();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        return retval;
    }

    /**
     * 获取请求内容，二进制表示
     * @param in
     * @return
     * @throws Exception
     */
    protected byte[] getContent(InputStream in) throws Exception
    {
        byte[] retval = null;
        BufferedInputStream bin = null;
        ByteArrayOutputStream bout = null;
        try
        {
            bin = new BufferedInputStream(in);
            bout = new ByteArrayOutputStream();
            int i = -1;
            while ((i = bin.read()) != -1)
            {
                bout.write(i);
            }
            retval = bout.toByteArray();
        }
        finally
        {
            if (bin != null)
            {
                try
                {
                    bin.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
            if (bout != null)
            {
                try
                {
                    bout.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        return retval;
    }

    protected HttpResponse genResponse(HttpURLConnection urlConn) throws Exception
    {
        HttpResponse httpResponser = new HttpResponse();
        // 赋值
        httpResponser.code = urlConn.getResponseCode();
        httpResponser.message = urlConn.getResponseMessage();
        httpResponser.contentType = urlConn.getContentType();
        httpResponser.connectTimeout = urlConn.getConnectTimeout();
        httpResponser.readTimeout = urlConn.getReadTimeout();
        if (HttpResponse.isRedirect(httpResponser.code))
        {
            httpResponser.redirect = urlConn.getHeaderField("Location");
        }
        Map<String, List<String>> headers = urlConn.getHeaderFields();
        Set<String> keys = headers.keySet();
        for (String key : keys)
        {
            httpResponser.headerParams.put(key, ConvertUtil.listToString(headers.get(key), "", false));
        }
        return httpResponser;
    }

    public static void main(String[] args) throws Exception
    {
        HttpConnect conn = new HttpConnect();
        conn.sendGet("http://www.baidu.com");
    }
}
