package jetsennet.util;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtil
{

    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

    /**
     * 关闭流
     * @param in
     */
    public static final void close(Closeable in)
    {

        try
        {
            if (in != null)
            {
                in.close();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            in = null;
        }
    }

}
