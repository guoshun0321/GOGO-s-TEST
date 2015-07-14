package jetsennet.jbmp.protocols.linklayer;

import java.util.LinkedHashMap;
import java.util.Map;

import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.SnmpTable;

import org.apache.log4j.Logger;

/**
 * 地址转发表
 * @author 郭祥
 */
public class AddressForwardTable
{

    private Map<Integer, AddressForwardTableEntity> table;

    private static final Logger logger = Logger.getLogger(AddressForwardTable.class);

    public AddressForwardTable()
    {
        table = new LinkedHashMap<Integer, AddressForwardTableEntity>();
    }

    /**
     * 初始化
     * @param swtichIp
     * @param version
     * @param port
     * @param community
     */
    public void initTable(String swtichIp, String version, int port, String community)
    {
        try
        {
            SnmpTable aft = CommonSnmpTable.dot1dTpFdbTable();
            SnmpTableUtil.initSnmpTable(aft, null, version, swtichIp, port, community);
            logger.debug(swtichIp + "的地址转发表：\n" + aft.toString());
            this.initTable(aft);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 初始化
     * @param swtichIp
     * @param version
     * @param port
     * @param community
     */
    public void initTable(SnmpTable aft)
    {
        try
        {
            int rowNum = aft.getRowNum();
            for (int i = 0; i < rowNum; i++)
            {
                int portPhy = Integer.valueOf(aft.getCell(i, 1).getEditValue());
                String macAddr = aft.getCell(i, 0).getEditValue();
                int status = Integer.valueOf(aft.getCell(i, 2).getEditValue());
                AddressForwardTableEntity temp = table.get(portPhy);
                if (temp == null && (status == 3 || status == 5))
                {
                    temp = new AddressForwardTableEntity();
                    temp.setPort(portPhy);
                    table.put(portPhy, temp);
                }
                temp.addMac(macAddr);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    public AddressForwardTableEntity get(int port)
    {
        return table.get(port);
    }

    public static void main(String[] args)
    {
        AddressForwardTable table = new AddressForwardTable();
        table.initTable("130.0.0.199", "", 161, "public");
        System.out.println(table);
    }

}
