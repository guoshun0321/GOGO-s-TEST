/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.util;

import java.util.Comparator;

import jetsennet.jbmp.mib.node.EditNode;

/**
 * OID排序
 * @author Guo
 */
public class OIDComparator implements Comparator
{
    @Override
    public int compare(Object o1, Object o2)
    {
        String oid1 = ((EditNode) o1).getOid();
        String oid2 = ((EditNode) o2).getOid();
        if (OIDUtil.getOidDepth(oid1) > OIDUtil.getOidDepth(oid2))
        {
            return 1;
        }
        else if (OIDUtil.getOidDepth(oid1) < OIDUtil.getOidDepth(oid2))
        {
            return -1;
        }
        else
        {
            int last1 = OIDUtil.getLast(oid1);
            int last2 = OIDUtil.getLast(oid2);
            if (last1 < last2)
            {
                return -1;
            }
            else if (last1 > last2)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
}
