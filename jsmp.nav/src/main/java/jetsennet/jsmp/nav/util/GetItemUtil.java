package jetsennet.jsmp.nav.util;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;

public class GetItemUtil
{
	
	public static void main(String[] args)
	{
		Object temp = DataCacheOp.getInstance().get("PGM_CREATOR$777");
		System.out.println(temp);
	}

}
