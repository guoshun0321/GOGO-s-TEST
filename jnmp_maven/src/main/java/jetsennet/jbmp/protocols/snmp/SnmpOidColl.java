/************************************************************************
日 期: 2012-3-26
作 者: 郭祥
版 本: v1.3
描 述: SNMP扫描帮助类，存储需要OID
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.exception.SnmpException;

/**
 * @author 郭祥
 */
public class SnmpOidColl
{

    private ArrayList<String> allOids;
    private ArrayList<String> usableOids;
    private ArrayList<String> errorOids;
    private ArrayList<String> usedOids;
    private ArrayList<String> curOids;
    private Map<String, VariableBinding> results;
    private int max;
    private int dcs;

    /**
     * @param oids 参数
     * @param max 参数
     * @param dcs 参数
     */
    public SnmpOidColl(String[] oids, int max, int dcs)
    {
        allOids = new ArrayList<String>();
        allOids.addAll(Arrays.asList(oids));
        usableOids = new ArrayList<String>();
        usableOids.addAll(allOids);
        errorOids = new ArrayList<String>();
        usedOids = new ArrayList<String>();
        curOids = new ArrayList<String>();
        results = new HashMap<String, VariableBinding>();
        this.max = max;
        this.dcs = dcs;
    }

    /**
     * 获取下次采集需要用到的OID
     * @return 结果
     */
    public String[] getUsableOids()
    {
        curOids.clear();
        if (usableOids.size() > max)
        {
            curOids.addAll(usableOids.subList(0, max));
        }
        else
        {
            curOids.addAll(usableOids);
        }
        return curOids.toArray(new String[curOids.size()]);
    }

    /**
     * 最大值递减
     * @throws SnmpException 异常
     */
    public void decrease() throws SnmpException
    {
        if (max <= dcs)
        {
            max = max - 1;
        }
        else
        {
            max = max - dcs;
        }
        if (max <= 0)
        {
            throw new SnmpException("max < 0；请检查网络状况");
        }
    }

    /**
     * 错误处理
     * @param oid 参数
     */
    public void error(String oid)
    {
        if (usableOids.contains(oid))
        {
            usableOids.remove(oid);
            errorOids.add(oid);
            results.put(oid, null);
        }
    }

    /**
     * @param oids 参数
     */
    public void error(String[] oids)
    {
        if (oids == null)
        {
            return;
        }
        for (int i = 0; i < oids.length; i++)
        {
            this.error(oids[i]);
        }
    }

    /**
     * @param oid 参数
     * @param binding 参数
     */
    public void info(String oid, VariableBinding binding)
    {
        if (usableOids.contains(oid))
        {
            usableOids.remove(oid);
            usedOids.add(oid);
            results.put(oid, binding);
        }
    }

    /**
     * @param oid 参数
     * @param binding 参数
     */
    public void addResult(String oid, VariableBinding binding)
    {
        results.put(oid, binding);
    }

    /**
     * 
     */
    public void setCurUsed()
    {
        for (int i = 0; i < curOids.size(); i++)
        {
            String temp = curOids.get(i);
            usableOids.remove(temp);
            usedOids.add(temp);
        }
    }

    public Map<String, VariableBinding> getResult()
    {
        return results;
    }

    /**
     * @return 结果
     */
    public boolean isMore()
    {
        if (usableOids.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
