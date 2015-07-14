package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.entity.Port2PortEntiry;
import jetsennet.jbmp.protocols.linklayer.util.SnmpNetInterface;

public class LinkLayerLinkRel
{

    private List<LinkLayerLinkRelEntry> rels;

    public LinkLayerLinkRel()
    {
        rels = new ArrayList<LinkLayerLinkRelEntry>();
    }

    /**
     * 添加关系
     * @param dev1
     * @param interf1
     * @param dev2
     */
    public void addRel(LinkLayerDev dev1, SnmpNetInterface interf1, LinkLayerDev dev2)
    {
        boolean isExist = false;
        for (LinkLayerLinkRelEntry rel : rels)
        {
            if ((rel.getDev1().equals(dev1) && rel.getDev2().equals(dev2)) || (rel.getDev2().equals(dev1) && rel.getDev1().equals(dev2)))
            {
                if (rel.getInterf1() == null)
                {
                    rel.setInterf1(interf1);
                }
                else if (rel.getInterf2() == null)
                {
                    rel.setInterf2(interf1);
                }
                isExist = true;
            }
        }
        if (!isExist)
        {
            LinkLayerLinkRelEntry rel = new LinkLayerLinkRelEntry();
            rel.setDev1(dev1);
            rel.setInterf1(interf1);
            rel.setDev2(dev2);
            rels.add(rel);
        }
    }

    /**
     * 添加关系
     * @param dev1
     * @param interf1
     * @param dev2
     * @param interf2
     */
    public void addRel(LinkLayerDev dev1, SnmpNetInterface interf1, LinkLayerDev dev2, SnmpNetInterface interf2)
    {
        boolean isExist = false;
        for (LinkLayerLinkRelEntry rel : rels)
        {
            if ((rel.getDev1().equals(dev1) && rel.getDev2().equals(dev2)) || (rel.getDev2().equals(dev1) && rel.getDev1().equals(dev2)))
            {
                isExist = true;
            }
        }
        if (!isExist)
        {
            LinkLayerLinkRelEntry rel = new LinkLayerLinkRelEntry();
            rel.setDev1(dev1);
            rel.setInterf1(interf1);
            rel.setDev2(dev2);
            rel.setInterf2(interf2);
            rels.add(rel);
        }
    }

    public List<Port2PortEntiry> toEntity(int groupId)
    {
        List<Port2PortEntiry> retval = new ArrayList<Port2PortEntiry>();
        for (LinkLayerLinkRelEntry rel : rels)
        {
            Port2PortEntiry temp = rel.toEntity(groupId);
            if (temp != null)
            {
                retval.add(temp);
            }
        }
        return retval;
    }

}
