/************************************************************************
日 期：2012-04-05
作 者: 梁宏杰
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.entity.CollectorEntity;
import jetsennet.jbmp.util.ConvertUtil;

/**
 * @author ？
 */
public class CollectorDal extends DefaultDal<CollectorEntity>
{

    private static final Logger logger = Logger.getLogger(CollectorDal.class);

    /**
     * 构造方法
     */
    public CollectorDal()
    {
        super(CollectorEntity.class);
    }

    /**
     * 以字符串形式返回包含的对象ID
     * 
     * @param collId
     * @return
     */
    public String getContainObjs(int collId)
    {
        String retval = null;
        try
        {
            String sql = "SELECT OBJ_ID FROM BMP_OBJ2GROUP WHERE GROUP_ID IN (SELECT GROUP_ID FROM BMP_OBJGROUP WHERE NUM_VAL1 = " + collId + ")";
            final List<Integer> objIds = new ArrayList<Integer>();
            read(sql, new IReadHandle()
            {

                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        objIds.add(rs.getInt("OBJ_ID"));
                    }
                }

            });
            retval = ConvertUtil.listToString(objIds, ",", false).trim();
            retval = "".equals(retval) ? null : retval;
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        CollectorDal cdal = ClassWrapper.wrap(CollectorDal.class);

        for (int i = 1; i <= 2000; i++)
        {
            CollectorEntity coll = new CollectorEntity();
            coll.setCollName("test_" + i);
            coll.setCollType("0");
            cdal.insert(coll);
        }
    }
}
