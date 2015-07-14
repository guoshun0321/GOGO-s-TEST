/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.snmp.AbsSnmpPtl;

/**
 * @author Guo
 */
public class SnmpUtil
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpUtil.class);

    /**
     * 转化成实际使用的OID格式。去掉第一个“.”（如果有）。
     * @param oid 参数
     * @return 结果
     */
    public static String getOid(String oid)
    {
        String result = oid;
        if (oid.startsWith("."))
        {
            result = oid.substring(1);
        }
        return result;
    }

    /**
     * 从ifTable表中获取MAC地址
     * @param table
     * @return
     */
    public static String ensureMacFromIfTable(SnmpTable table)
    {
        String retval = null;
        if (table != null && table.getTableName().equals("ifTable"))
        {
            int row = table.getRowNum();
            for (int i = 0; i < row; i++)
            {
                try
                {
                    int ifIndex = table.getCellInteger(i, 0);
                    String ifDesc = table.getCellString(i, 1);
                    int ifType = table.getCellInteger(i, 2);
                    String ifPhysAddress = table.getCellString(i, 3);
                    if (ifType == 6)
                    {
                        if (IPUtil.isLegalMac(ifPhysAddress))
                        {
                            if (ifPhysAddress.startsWith("00:00:00:00:00:00"))
                            {
                                logger.debug("放弃接口：" + ifIndex + "，空MAC地址：" + ifPhysAddress);
                            }
                            else if (ifPhysAddress.startsWith("44:45:53:54:00"))
                            {
                                logger.debug("放弃接口：" + ifIndex + "，DUN adapter：" + ifPhysAddress);
                            }
                            else
                            {
                                if (ifDesc.startsWith("WAN"))
                                {
                                    logger.debug("放弃接口：" + ifIndex + "，MAC：" + ifPhysAddress + "，描述：" + ifDesc);
                                }
                                else
                                {
                                    logger.debug("选择接口：" + ifIndex + "，MAC：" + ifPhysAddress);
                                    retval = ifPhysAddress;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            logger.debug("接口：" + ifIndex + "，不合法的MAC地址：" + ifPhysAddress);
                        }
                    }
                }
                catch (Exception ex)
                {
                    logger.debug("", ex);
                }
            }
        }
        return retval;
    }

    /**
     * 远程调用SNMP协议,WALK
     * @param objId 参数
     * @param version 参数
     * @param oids 参数
     * @return 结果
     */
    public static Map<String, Map<String, VariableBinding>> walk(int objId, String version, String[] oids)
    {
        Map<String, Map<String, VariableBinding>> retval = new LinkedHashMap<String, Map<String, VariableBinding>>();
        if (oids == null || oids.length == 0)
        {
            return retval;
        }
        AbsSnmpPtl snmp = AbsSnmpPtl.getInstance(version);
        try
        {
            MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
            MObjectEntity mo = modal.get(objId);
            if (mo == null)
            {
                return retval;
            }
            snmp.init(mo.getIpAddr(), mo.getIpPort(), mo.getUserName());
            for (String column : oids)
            {
                try
                {
                    Map<String, VariableBinding> temp = snmp.snmpGetWithBegin(column);
                    retval.put(column, temp);
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            try
            {
                snmp.close();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                snmp = null;
            }
        }
        return retval;
    }
}
