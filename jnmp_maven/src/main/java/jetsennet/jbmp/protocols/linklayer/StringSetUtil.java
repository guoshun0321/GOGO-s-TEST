package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jbmp.util.ConvertUtil;

public class StringSetUtil
{

    /**
     * 求交集
     * @param oMacs
     */
    public static List<String> intersect(List<String> strs1, List<String> strs2)
    {
        List<String> retval = new ArrayList<String>();
        if (strs1 != null && strs2 != null)
        {
            int length1 = strs1.size();
            for (int i = 0; i < length1; i++)
            {
                String temp1 = strs1.get(i);
                if (strs2.contains(temp1) && !retval.contains(temp1))
                {
                    retval.add(temp1);
                }
            }
        }
        return retval;
    }

    /**
     * 求并集
     * @param strs1
     * @param strs2
     * @return
     */
    public static List<String> union(List<String> strs1, List<String> strs2)
    {
        List<String> retval = new ArrayList<String>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        Object obj = new Object();
        if (strs1 != null)
        {
            for (String mac : strs1)
            {
                if (mac != null && retMap.get(mac) == null)
                {
                    retval.add(mac);
                    retMap.put(mac, obj);
                }
            }
        }
        if (strs2 != null)
        {
            for (String mac : strs2)
            {
                if (mac != null && retMap.get(mac) == null)
                {
                    retval.add(mac);
                    retMap.put(mac, obj);
                }
            }
        }
        return retval;
    }

    /**
     * 判断两个集合是否相等
     * @param strs1
     * @param strs2
     * @return
     */
    public static boolean equal(List<String> strs1, List<String> strs2)
    {
        if (strs1 == null && strs2 == null)
        {
            return true;
        }
        if (strs1 == null || strs2 == null)
        {
            return false;
        }
        boolean retval = true;
        if (strs1.size() == strs2.size())
        {
            int length1 = strs1.size();
            int length2 = strs2.size();
            for (int i = 0; i < length1; i++)
            {
                String tempMac = strs1.get(i);
                if (!strs2.contains(tempMac))
                {
                    retval = false;
                    break;
                }
            }
            if (retval)
            {
                for (int i = 0; i < length2; i++)
                {
                    String tempMac = strs2.get(i);
                    if (!strs1.contains(tempMac))
                    {
                        retval = false;
                        break;
                    }
                }
            }
        }
        return retval;
    }

    public static void main(String[] args)
    {
        String[] mac1 =
            "00:0b:2f:3f:49:92, 00:e0:4c:4d:00:aa, 00:e0:66:44:64:b0, 00:e0:66:3e:91:90, 00:e0:4c:4d:0:b8, 00:e0:66:44:65:45, 00:e0:4c:38:0b:64, 00:e0:4c:42:37:dc, 00:e0:66:46:41:6f, 80:fb:06:45:b4:5e"
                .split(",");
        String[] mac2 =
            "c8:60:00:ec:e8:8c, 00:e0:66:46:3e:a0, 00:1d:4f:4b:47:50, 00:e0:66:46:46:b2, 00:e0:4c:42:44:86, e8:39:35:8f:15:9d, 00:e0:4c:42:44:f7, 00:e0:66:46:46:cb, 00:0c:29:43:31:21, 00:e0:4c:06:d4:90, 00:e0:4c:06:d4:65, 00:e0:4c:07:c4:4e, 00:e0:66:23:06:68, 00:e0:4c:06:d4:56, 00:e0:4c:42:44:78, 00:e0:4c:42:44:7d, 00:e0:4c:e5:04:f6, 78:ac:c0:3c:d8:6c, 00:e0:4c:28:d5:c9, 00:e0:66:23:07:c6, 4c:e6:76:48:23:a9, 00:0b:2f:46:6e:59, 00:e0:b6:14:4f:d9, 00:e0:66:47:37:f7, 00:e0:66:46:3f:fa, 00:0b:2f:47:8e:cc, 00:0b:2f:46:6e:cf, 00:e0:66:46:3f:2a, 00:e0:66:47:37:32, 00:e0:66:46:3f:00, 00:e0:66:46:27:09, 14:da:e9:2d:04:35, 00:e0:4c:db:a5:c5, 78:2b:cb:1d:61:36, 00:e0:66:44:64:ac, 00:e0:66:44:64:b0, 00:e0:66:24:64:fc, 14:da:e9:2d:2f:a5, 14:da:e9:2d:2f:aa, 00:15:b2:a3:08:eb, 00:15:b2:a3:08:ea, 00:15:b2:a3:08:e9, 00:15:b2:a3:08:e8, 00:0b:2f:42:55:cc, 00:e0:b0:e8:a2:ad, 5c:63:bf:8b:f1:2c, 50:e5:49:eb:82:3f, 00:e0:66:46:45:94, 14:da:e9:2d:36:a6, 00:e0:4c:42:37:f4, 00:e0:b0:e8:43:67, 00:e0:4c:42:37:cc, 00:e0:4c:42:37:d2, 00:0b:2f:32:9c:bb, 00:e0:65:04:86:69, 00:e0:66:44:65:3d, 00:e0:66:44:65:3a, 00:e0:b6:0c:c5:4b, 00:e0:66:44:65:45, 00:e0:66:39:95:3d, 00:e0:66:4d:35:41, 00:e0:4c:42:37:50, 00:e0:4c:4d:00:aa, 00:e0:4c:4d:00:b8, 00:e0:66:4e:7a:8b, 90:2b:34:30:58:4b, 00:e0:66:47:2a:cf, 00:e0:4c:33:a0:ae, 00:e0:4c:06:e0:61, 00:e0:66:52:aa:20, 00:e0:4c:06:e0:71, 00:0c:29:a7:5d:46, 00:e0:b6:16:e2:06, 00:e0:4c:26:a9:c2, 00:e0:4c:26:a9:cd, 50:e5:49:eb:cc:16, 00:e0:4c:3b:a1:91, 00:1e:8c:c8:e9:86, 00:e0:4c:3b:a1:8f, e8:39:35:31:d8:6f, 00:e0:4c:26:a9:b9, 50:e5:49:e2:94:88, 00:e0:4c:3c:39:42, 00:e0:66:23:03:0d, 00:e0:4c:26:e9:24, 00:07:e9:11:44:f3, 90:2b:34:4a:a1:86, 00:e0:66:23:03:32, 00:e0:4c:3c:39:29, 00:0b:2f:54:aa:a6, 00:e0:66:3e:03:26, 00:e0:4c:e5:0a:12, 00:0c:29:8e:a7:9a, 00:e0:66:23:00:ee, 14:d6:4d:15:27:ed, 00:e0:66:23:08:ed, 00:1f:d0:c9:ce:97, 00:e0:65:04:23:b0, 14:d6:4d:15:27:ad, 00:0b:2f:3e:b9:45, 00:e0:b0:ef:d6:79, 00:e0:66:3e:98:a0, 00:e0:4c:e3:ba:81, e8:06:88:cc:76:47, c8:60:00:56:d6:b9, 00:e0:66:23:08:56, 00:e0:66:23:08:5a, 00:e0:66:23:08:58, 38:83:45:e8:db:f1, 00:e0:66:23:08:5e, 00:0b:2f:3f:49:92, 14:d6:4d:15:27:6d, 00:e0:66:23:08:6d, 00:e0:66:23:08:73, 00:e0:66:23:08:74, 50:e5:49:e2:a7:b8, 00:e0:66:23:08:19, 00:e0:66:23:08:2a, 00:e0:4c:37:52:3f, 00:e0:4c:36:12:23, c8:60:00:5e:df:3c, 00:e0:66:3e:91:d2, 00:e0:b0:f0:47:02, 90:2b:34:11:fb:15, c8:60:00:ec:e7:b9, 00:30:18:a6:af:d7, c8:60:00:ec:e7:c9, 00:e0:66:3e:91:90, b0:48:7a:7a:8d:62, 00:0c:29:f4:16:88, 00:0b:2f:54:80:de, 00:0b:2f:54:80:df, 00:0b:2f:54:80:f8, 00:0b:2f:54:80:e2, 00:e0:4c:38:0b:64, 00:0b:2f:54:80:e1, 00:e0:4c:3b:a3:1a, 90:2b:34:16:db:ca, 00:e0:66:23:09:35, 00:0c:29:e8:86:1d, 00:e0:66:23:09:39, 00:e0:66:46:41:5c"
                .split(",");
        List<String> strs1 = Arrays.asList(mac1);
        List<String> strs2 = Arrays.asList(mac2);
        List<String> retval = StringSetUtil.intersect(strs1, strs2);
        System.out.println(retval);
        List<String> retval1 = StringSetUtil.union(strs1, strs2);
        System.out.println(retval1);
    }

}
