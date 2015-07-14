package jetsennet.jbmp.util;

import java.util.Comparator;

import jetsennet.jbmp.entity.SnmpNodesEntity;

/**
 * @author ？
 */
public class PreorderMibOidComparator implements Comparator<SnmpNodesEntity>
{
    @Override
    public int compare(SnmpNodesEntity o1, SnmpNodesEntity o2)
    {
        MibOidNumArray num1 = new MibOidNumArray(o1.getNodeOid());
        MibOidNumArray num2 = new MibOidNumArray(o2.getNodeOid());
        return num1.compare(num2);
    }

    class MibOidNumArray
    {

        private int[] nums;

        public MibOidNumArray(String oid)
        {
            String[] oids = oid.split("\\.");
            nums = new int[oids.length];
            for (int i = 0; i < oids.length; i++)
            {
                nums[i] = Integer.valueOf(oids[i]);
            }
        }

        public int compare(MibOidNumArray other)
        {
            int[] oNums = other.getNums();
            int min = Math.min(this.nums.length, oNums.length);
            if (nums.length == oNums.length)
            {
                for (int i = 0; i < min; i++)
                {
                    if (nums[i] < oNums[i])
                    {
                        return -1;
                    }
                    else if (nums[i] > oNums[i])
                    {
                        return 1;
                    }
                }
            }
            else if (nums.length > oNums.length)
            {
                return 1;
            }
            else
            {
                return -1;
            }
            return 0;
        }

        public int[] getNums()
        {
            return nums;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            for (int i : nums)
            {
                sb.append(i);
                sb.append(".");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }
}
