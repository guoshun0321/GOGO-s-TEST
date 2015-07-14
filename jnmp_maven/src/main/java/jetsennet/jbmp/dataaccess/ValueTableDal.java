/************************************************************************
日 期: 2012-1-6
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.entity.ValueTableEntity;
import jetsennet.jbmp.mib.parse.SnmpEnumEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author 郭祥
 */
public class ValueTableDal extends DefaultDal<ValueTableEntity>
{

    /**
     * 构造方法
     */
    public ValueTableDal()
    {
        super(ValueTableEntity.class);
    }

    /**
     * @param begin 开始
     * @param end 结束
     * @return 结果
     * @throws Exception 异常
     */
    public List<ValueTableEntity> getBetween(int begin, int end) throws Exception
    {
        String sql = "SELECT * FROM BMP_VALUETABLE WHERE VALUE_ID >=" + begin + " AND VALUE_ID <= " + end;
        return this.getLst(sql);
    }

    /**
     * @param valueId 值
     * @throws Exception 异常
     */
    public void deleteByValueId(int valueId) throws Exception
    {
        String sql = "DELETE FROM BMP_VALUETABLE WHERE VALUE_ID = " + valueId + " OR VALUE_TYPE = " + valueId;
        this.delete(sql);
    }

    /**
     * @param mibId 参数
     * @throws Exception 异常
     */
    public void deleteByMibId(int mibId) throws Exception
    {
        SqlCondition cond = new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        this.delete(cond);
    }

    /**
     * @param node 节点
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public int insert(SnmpNodesEntity node, int mibId) throws Exception
    {
        int retval = -1;
        SnmpEnumEntity ee = node.getEnumE();
        if (ee != null)
        {
            ArrayList<ValueTableEntity> vts = ee.genValueTable();
            if (vts != null && !vts.isEmpty())
            {
                for (ValueTableEntity vt : vts)
                {
                    vt.setMibId(mibId);
                    if (vt.getValueType() == -1)
                    {
                        retval = this.insert(vt);
                    }
                    else
                    {
                        vt.setValueType(retval);
                        this.insert(vt);
                    }
                }
                // 防内存溢出
                vts.clear();
                vts = null;
            }
        }
        return retval;
    }

    /**
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<ValueTableEntity> getByType(int mibId) throws Exception
    {
        SqlCondition conds = new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return (ArrayList<ValueTableEntity>) getLst(conds);
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        new Thread(new TestThread()).start();
        while (true)
        {
            Thread.sleep(60 * 1000);
        }
    }

    private static class TestThread implements Runnable
    {

        @Override
        public void run()
        {
            ValueTableEntity vt = new ValueTableEntity();
            vt.setValueId(-1);
            vt.setValueType(-1);
            vt.setMibId(-1);
            vt.setAttribValue("value");
            vt.setValueName("test");
            vt.setValueDesc("desc");
            ValueTableDal vtdal = new ValueTableDal();
            for (int i = 0; i < 10000; i++)
            {
                try
                {
                    vtdal.insert(vt);
                }
                catch (Exception ex)
                {
                    Logger.getLogger(ValueTableDal.class.getName()).log(Level.SEVERE, null, ex);
                    while (true)
                    {
                        try
                        {
                            Thread.sleep(60 * 1000);
                        }
                        catch (InterruptedException ex1)
                        {
                            Logger.getLogger(ValueTableDal.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            }
        }

    }

}
