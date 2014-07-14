package jetsennet.jbmp.dataaccess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.SyslogEntity;

/**
 * @author ?
 */
public class SyslogDal extends DefaultDal<SyslogEntity>
{

    /**
     * 构造方法
     */
    public SyslogDal()
    {
        super(SyslogEntity.class);
    }

    /**
     * @param tes 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(List<SyslogEntity> tes) throws Exception
    {
        for (SyslogEntity te : tes)
        {
            this.insert(te);
        }
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        SyslogDal sdal = ClassWrapper.wrapTrans(SyslogDal.class);
        SyslogEntity sys = sdal.get("select * from nmp_syslog");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sTempValue = df.format(sys.getCollTime());
        System.out.println(sTempValue);
    }
}
