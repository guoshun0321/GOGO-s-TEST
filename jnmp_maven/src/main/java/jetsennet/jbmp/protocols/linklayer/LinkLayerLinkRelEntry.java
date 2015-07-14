package jetsennet.jbmp.protocols.linklayer;

import jetsennet.jbmp.entity.Port2PortEntiry;
import jetsennet.jbmp.protocols.linklayer.util.SnmpNetInterface;

public class LinkLayerLinkRelEntry
{

    private LinkLayerDev dev1;
    private SnmpNetInterface interf1;
    private LinkLayerDev dev2;
    private SnmpNetInterface interf2;

    public Port2PortEntiry toEntity(int groupId)
    {
        Port2PortEntiry retval = new Port2PortEntiry();
        retval.setRelType(Port2PortEntiry.REL_TYPE_AUTO);
        retval.setGroupId(groupId);
        if (interf1 != null)
        {
            retval.setPortAID(interf1.getMo().getObjId());
        }
        else if (dev1 != null)
        {
            retval.setPortAID(dev1.getMo().getObjId());
        }
        else
        {
            return null;
        }
        if (interf2 != null)
        {
            retval.setPortBID(interf2.getMo().getObjId());
        }
        else if (dev2 != null)
        {
            retval.setPortBID(dev2.getMo().getObjId());
        }
        else
        {
            return null;
        }
        return retval;
    }

    public LinkLayerDev getDev1()
    {
        return dev1;
    }

    public void setDev1(LinkLayerDev dev1)
    {
        this.dev1 = dev1;
    }

    public SnmpNetInterface getInterf1()
    {
        return interf1;
    }

    public void setInterf1(SnmpNetInterface interf1)
    {
        this.interf1 = interf1;
    }

    public LinkLayerDev getDev2()
    {
        return dev2;
    }

    public void setDev2(LinkLayerDev dev2)
    {
        this.dev2 = dev2;
    }

    public SnmpNetInterface getInterf2()
    {
        return interf2;
    }

    public void setInterf2(SnmpNetInterface interf2)
    {
        this.interf2 = interf2;
    }

}
