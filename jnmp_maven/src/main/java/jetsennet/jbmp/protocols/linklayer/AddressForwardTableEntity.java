package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.List;

public class AddressForwardTableEntity
{

    /**
     * 物理接口
     */
    private int port;
    /**
     * 接收到的MAC地址
     */
    private List<String> macs;

    public AddressForwardTableEntity()
    {
        macs = new ArrayList<String>();
    }

    public void addMac(String mac)
    {
        if (mac != null && !macs.contains(mac))
        {
            macs.add(mac);
        }
    }

    public void filterMac(List<String> filterMacs)
    {
        this.macs = StringSetUtil.intersect(macs, filterMacs);
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public List<String> getMacs()
    {
        return macs;
    }

    public void setMacs(List<String> macs)
    {
        this.macs = macs;
    }

}
