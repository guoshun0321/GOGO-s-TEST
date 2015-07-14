package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.MibBanksEntity;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author？
 */
public class MibBankDal extends DefaultDal<MibBanksEntity>
{
    private static final Logger logger = Logger.getLogger(MibBankDal.class);

    /**
     * 构造函数
     */
    public MibBankDal()
    {
        super(MibBanksEntity.class);
    }

    /**
     * 更新数据库中所有NODE_OID字段为oid的记录
     * @param oid 参数
     * @param desc 参数
     * @throws Exception 异常
     */
    @Transactional
    public void UpdateNodeByOID(String oid, String desc) throws Exception
    {
        if (oid == null || desc == null)
        {
            return;
        }
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName("BMP_SNMPNODES");
        cmd.addField("NODE_EXPLAIN", desc);
        cmd.setFilter(new SqlCondition("NODE_OID", oid, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String));
        this.update(cmd.toString());
    }

    /**
     * @param mibId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public ArrayList<MibBanksEntity> getByType(int mibId) throws Exception
    {
        SqlCondition conds = new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return (ArrayList<MibBanksEntity>) getLst(conds);
    }

    /**
     * 获取别名相同的结果集
     * @param name 别名
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<MibBanksEntity> getListByName(String name) throws Exception
    {

        String sql = "SELECT a.* FROM BMP_MIBBANKS a  WHERE a.MIB_ALIAS = '%s'";
        sql = String.format(sql, name);
        return this.getLst(sql);
    }

    /**
     * 获取名称相同的结果集
     * @param name mib名称
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public List<MibBanksEntity> getListByMibName(String name) throws Exception
    {

        String sql = "SELECT a.* FROM BMP_MIBBANKS a  WHERE a.MIB_NAME = '%s'";
        sql = String.format(sql, name);
        return this.getLst(sql);
    }

    /**
     * 主方法测试
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        MibBankDal t = new MibBankDal();
        ArrayList<MibBanksEntity> list = (ArrayList<MibBanksEntity>) t.getListByMibName("ASD");
        // ArrayList<MibBanksEntity> list = (ArrayList<MibBanksEntity>) t.getByType(1023);
        System.out.println(list);
    }

}
