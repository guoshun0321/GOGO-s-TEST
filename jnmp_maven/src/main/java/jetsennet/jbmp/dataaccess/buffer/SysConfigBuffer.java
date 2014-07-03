package jetsennet.jbmp.dataaccess.buffer;

import jetsennet.jbmp.dataaccess.SysconfigDal;
import jetsennet.jbmp.util.BMPConstants;

/**
 * 系统配置缓存。
 * 采用单例模式，在第一次使用时初始化。
 * 
 * @author 郭祥
 */
public class SysConfigBuffer
{

    /**
     * 是否自动清除报警
     */
    public static final boolean isAutoClean;

    static
    {
        SysconfigDal sdal = new SysconfigDal();
        int autoRecover = sdal.getConfigData(BMPConstants.AUTO_RECOVER_STR, BMPConstants.AUTO_RECOVER_NO);
        isAutoClean = autoRecover == BMPConstants.AUTO_RECOVER_YES ? true : false;
    }

}
