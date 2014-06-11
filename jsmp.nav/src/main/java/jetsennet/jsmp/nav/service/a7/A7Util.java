package jetsennet.jsmp.nav.service.a7;

import jetsennet.jsmp.nav.util.UncheckedNavException;

public class A7Util
{

    /**
     * 解析GetFolderContents的分页参数
     * 
     * @param str
     * @return
     */
    public static int[] columnAndMoviePageInfo(String str)
    {
        int[] retval = new int[4];
        str = str.substring(1, str.length());
        String[] couples = str.split(",");
        if (couples.length != 4)
        {
            throw new UncheckedNavException("错误参数：" + str);
        }
        for (int i = 0; i < 4; i++)
        {
            String couple = couples[i];
            String[] tempLst = couple.split(":");
            if (couples.length != 2)
            {
                throw new UncheckedNavException("错误参数：" + str);
            }
            retval[i] = Integer.valueOf(tempLst[1]);
        }
        return retval;
    }

    /**
     * 生成GetFolderContents分页参数
     * 
     * @param str
     * @return
     */
    public static String genColumnAndMoviePageInfo(int[] nums)
    {
        if (nums.length != 4)
        {
            throw new UncheckedNavException("GetFolderContents分页参数生成失败！");
        }
        StringBuilder sb = new StringBuilder(100);
        sb.append("{moviePageSize:").append(nums[0]).append(",");
        sb.append("movieCurrentPage:").append(nums[1]).append(",");
        sb.append("folderPageSize:").append(nums[2]).append(",");
        sb.append("folderCurrentPage:").append(nums[3]).append("}");
        return sb.toString();
    }

}
