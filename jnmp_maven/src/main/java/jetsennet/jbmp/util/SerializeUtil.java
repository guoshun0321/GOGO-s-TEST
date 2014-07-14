package jetsennet.jbmp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * @author ？
 */
public class SerializeUtil
{

    private static final Logger logger = Logger.getLogger(SerializeUtil.class);

    /**
     * 序列化
     * @param obj 对象
     * @return 结果
     */
    public static byte[] serialize(Object obj)
    {
        byte[] retval = null;
        ByteArrayOutputStream bout = null;
        ObjectOutputStream out = null;
        try
        {
            bout = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bout);
            out.writeObject(obj);
            out.flush();
            retval = bout.toByteArray();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
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
                finally
                {
                    bout = null;
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    out = null;
                }
            }
        }
        return retval;
    }

    /**
     * 反序列化
     * @param datas 参数
     * @return 结果
     */
    public static Object deserialize(byte[] datas)
    {
        Object retval = null;
        ObjectInputStream in = null;
        try
        {
            in = new ObjectInputStream(new ByteArrayInputStream(datas));
            retval = in.readObject();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
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
                finally
                {
                    in = null;
                }
            }
        }
        return retval;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        byte[] temp = SerializeUtil.serialize("test");
        String tet = (String) SerializeUtil.deserialize(temp);
        System.out.println(tet);
    }

}
