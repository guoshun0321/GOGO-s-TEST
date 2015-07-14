/************************************************************************
日 期：2012-1-6
作 者: 郭祥
版 本: v1.3
描 述: MIB文件相关的操作
历 史:
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.TrapTableDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.entity.TrapTableEntity;
import jetsennet.jbmp.mib.parse.MibLoaderHelper;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * MIB文件相关的操作
 * @author 郭祥
 */
public class MibFile
{

    public static Logger logger = Logger.getLogger(MibFile.class);
    private SnmpNodesDal sndal;
    private TrapTableDal ttdal;

    /**
     * 构造方法
     */
    public MibFile()
    {
        this.sndal = new SnmpNodesDal();
        this.ttdal = new TrapTableDal();
    }

    /**
     * 新增
     * @param helper 参数
     * @param mibId 参数
     */
    public void insert(MibLoaderHelper helper, int mibId)
    {
        if (helper == null || mibId <= 0)
        {
            return;
        }
        try
        {
            // 取表中该mib_id数据
            SnmpNodesDal snmpNodesDal = new SnmpNodesDal();
            ArrayList<SnmpNodesEntity> snmpNodesList =
                (ArrayList<SnmpNodesEntity>) snmpNodesDal.getLst(new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.AndAll,
                    SqlRelationType.Equal, SqlParamType.String));
            TrapTableDal trapTableDal = new TrapTableDal();
            ArrayList<TrapTableEntity> trapTableList =
                (ArrayList<TrapTableEntity>) trapTableDal.getLst(new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.AndAll,
                    SqlRelationType.Equal, SqlParamType.String));
            // 读取上传mib文件中数据
            ArrayList<SnmpNodesEntity> oids = helper.getOids();
            ArrayList<TrapTableEntity> traps = helper.getTransTraps();
            // 删除上传文件中已经存在于表中的数据（oid相同的）
            Map<String, SnmpNodesEntity> oidsMap = new HashMap<String, SnmpNodesEntity>();
            for (int i = 0; i < oids.size(); i++)
            {
                oidsMap.put(oids.get(i).getNodeOid(), oids.get(i));
            }
            Map<String, TrapTableEntity> trapsMap = new HashMap<String, TrapTableEntity>();
            for (int i = 0; i < traps.size(); i++)
            {
                trapsMap.put(traps.get(i).getTrapOid(), traps.get(i));
            }

            for (int i = 0; i < snmpNodesList.size(); i++)
            {
                if (oidsMap.containsKey(snmpNodesList.get(i).getNodeOid()))
                {
                    oids.remove(oidsMap.get(snmpNodesList.get(i).getNodeOid()));
                }
            }
            for (int i = 0; i < trapTableList.size(); i++)
            {
                if (trapsMap.containsKey(trapTableList.get(i).getTrapOid()))
                {
                    traps.remove(trapsMap.get(trapTableList.get(i).getTrapOid()));
                }
            }
            // 添加数据
            sndal.insert(oids, mibId);
            ttdal.insert(traps, mibId);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 删除
     * @param helper 参数
     * @param mibId 参数
     */
    public void delete(MibLoaderHelper helper, int mibId)
    {
        if (helper == null || mibId <= 0)
        {
            return;
        }
        try
        {
            // 取表中该mib_id数据
            SnmpNodesDal snmpNodesDal = new SnmpNodesDal();
            ArrayList<SnmpNodesEntity> snmpNodesList =
                (ArrayList<SnmpNodesEntity>) snmpNodesDal.getLst(new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.AndAll,
                    SqlRelationType.Equal, SqlParamType.String));
            TrapTableDal trapTableDal = new TrapTableDal();
            ArrayList<TrapTableEntity> trapTableList =
                (ArrayList<TrapTableEntity>) trapTableDal.getLst(new SqlCondition("MIB_ID", Integer.toString(mibId), SqlLogicType.AndAll,
                    SqlRelationType.Equal, SqlParamType.String));
            // 读取上传mib文件中数据
            ArrayList<SnmpNodesEntity> oids = helper.getOids();
            ArrayList<TrapTableEntity> traps = helper.getTransTraps();
            // 删除表中不在上传文件中的数据
            // map集合中存入表中数据oid, id
            Map<String, Integer> snmpNodesMap = new HashMap<String, Integer>();
            for (int i = 0; i < snmpNodesList.size(); i++)
            {
                snmpNodesMap.put(snmpNodesList.get(i).getNodeOid(), snmpNodesList.get(i).getNodeId());
            }
            Map<String, Integer> trapTableMap = new HashMap<String, Integer>();
            for (int i = 0; i < trapTableList.size(); i++)
            {
                trapTableMap.put(trapTableList.get(i).getTrapOid(), trapTableList.get(i).getTrapId());
            }
            // 删除map中存在于上传数据中的数据（通过oid来判断）
            for (int i = 0; i < oids.size(); i++)
            {
                if (snmpNodesMap.containsKey(oids.get(i).getNodeOid()))
                {
                    snmpNodesMap.remove(oids.get(i).getNodeOid());
                }
            }
            for (int i = 0; i < traps.size(); i++)
            {
                if (trapTableMap.containsKey(traps.get(i).getTrapOid()))
                {
                    trapTableMap.remove(traps.get(i).getTrapOid());
                }
            }
            // 删除数据
            sndal.delete(snmpNodesMap.values().toArray(new Integer[0]));
            ttdal.delete(trapTableMap.values().toArray(new Integer[0]));
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 删除
     * @param mibId 参数
     */
    @Business
    public void delete(int mibId)
    {
        try
        {
            sndal.deleteByType(mibId);
            ttdal.deleteByType(mibId);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }
}
