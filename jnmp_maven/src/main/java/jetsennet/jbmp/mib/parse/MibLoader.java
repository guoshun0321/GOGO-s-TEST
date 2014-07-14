/************************************************************************
日 期：2012-1-6
作 者: 郭祥
版 本: v1.3
描 述: MIB解析工具类
历 史:
 ************************************************************************/
package jetsennet.jbmp.mib.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import jetsennet.jbmp.business.MibFile;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.mib.MibUtil;

/**
 * MIB解析工具类
 * @author 郭祥
 */
public final class MibLoader
{

    /**
     * 数据库操作
     */
    private MibFile mfdal;
    /**
     * 
     */
    private static final String DEF_MIB_DIR = MibUtil.getMibDirPath();
    /**
     * 日志
     */
    public final Logger logger = Logger.getLogger(MibLoader.class);

    // 单例
    private static MibLoader instance = new MibLoader();

    private MibLoader()
    {
        this.mfdal = ClassWrapper.wrap(MibFile.class);
    }

    public static MibLoader getInstance()
    {
        return instance;
    }

    /**
     * 解析MIB文件
     * @param mibfiles 参数
     * @return 结果
     */
    public MibLoaderHelper load(File[] mibfiles)
    {
        MibLoaderHelper helper = new MibLoaderHelper(new String[] { DEF_MIB_DIR });
        String msg = helper.loadFile(mibfiles);
        if (msg != null && !"".equals(msg.trim()))
        {
            throw new MibException(msg);
        }
        return helper;
    }

    /**
     * 插入
     * @param helper 参数
     * @param mibId 参数
     * @return 结果
     */
    public MibLoaderHelper insert(MibLoaderHelper helper, int mibId)
    {
        if (helper == null)
        {
            return null;
        }
        mfdal.insert(helper, mibId);
        return helper;
    }

    /**
     * 删除
     * @param helper 参数
     * @param mibId 参数
     * @return 结果
     */
    public MibLoaderHelper delete(MibLoaderHelper helper, int mibId)
    {
        if (helper == null)
        {
            return null;
        }
        mfdal.delete(helper, mibId);
        return helper;
    }

    /**
     * 删除
     * @param mibId 参数
     */
    public void delete(int mibId)
    {
        mfdal.delete(mibId);
    }

    /**
     * 更新
     * @param helper 参数
     * @param mibId 参数
     */
    public void update(MibLoaderHelper helper, int mibId)
    {
        this.delete(helper, mibId);
        this.insert(helper, mibId);
    }

    /**
     * 加载并插入
     * @param mibfiles 参数
     * @param mibId 参数
     */
    public void loadAndInsert(File[] mibfiles, int mibId)
    {
        MibLoaderHelper helper = this.load(mibfiles);
        this.insert(helper, mibId);
    }

    /**
     * 加载并更新
     * @param mibfiles 参数
     * @param mibId 参数
     */
    public void loadAndUpdate(File[] mibfiles, int mibId)
    {
        MibLoaderHelper helper = this.load(mibfiles);
        this.update(helper, mibId);
    }

    /**
     * @param args 参数
     * @throws InterruptedException 异常
     */
    public static void main(String[] args) throws InterruptedException
    {
        ArrayList<File> files = new ArrayList<File>();
        files.add(new File("E:/JNMP/JnmpMibUtil/MIBS/RX1290/CFG.mib"));
        files.add(new File("E:/JNMP/JnmpMibUtil/MIBS/RX1290/ip-RX1290.mib"));
        files.add(new File("E:/JNMP/JnmpMibUtil/MIBS/RX1290/TTVAlarmTrap.mib"));
        files.add(new File("E:/JNMP/JnmpMibUtil/MIBS/RX1290/TTVBASE.mib"));
        files.add(new File("E:/JNMP/JnmpMibUtil/MIBS/RX1290/TTV-RX1290-8VSB-MIB.mib"));
        files.add(new File("E:/JNMP/JnmpMibUtil/MIBS/RX1290/TTV-RX1290-MIB.mib"));
        files.add(new File("E:/JNMP/JnmpMibUtil/MIBS/RX1290/TTVTYPES.mib"));
        MibLoader loader = new MibLoader();
        loader.update(loader.load(files.toArray(new File[0])), 6);
    }
}
