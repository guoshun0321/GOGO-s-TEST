package jetsennet.jsmp.nav.cache.xmem;

public class GetObjFromCache
{

	public static void main(String[] args)
	{
		DataCacheOp cache = DataCacheOp.getInstance();
		Object obj = cache.getT("SUB_PGM$JETSENf650e7b3-cd25-4364-9ec9-cfa6a2cc0b91");
		System.out.println(obj);
	}

}
