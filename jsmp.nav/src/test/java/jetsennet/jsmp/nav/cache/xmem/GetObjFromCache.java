package jetsennet.jsmp.nav.cache.xmem;

import jetsennet.jsmp.nav.xmem.XmemcachedUtil;

public class GetObjFromCache
{

	public static void main(String[] args)
	{
		XmemcachedUtil cache = XmemcachedUtil.getInstance();
		Object obj = cache.get("SUB_PGM$JETSENf650e7b3-cd25-4364-9ec9-cfa6a2cc0b91");
		System.out.println(obj);
	}

}
