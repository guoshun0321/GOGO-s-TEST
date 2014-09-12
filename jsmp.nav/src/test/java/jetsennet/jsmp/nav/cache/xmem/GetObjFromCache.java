package jetsennet.jsmp.nav.cache.xmem;

public class GetObjFromCache
{

	public static void main(String[] args)
	{
		DataCacheOp cache = DataCacheOp.getInstance();
		Object obj = cache.getT("COLUMN_QUERY_TOP");
		System.out.println(obj);
		
		obj = cache.getT("COLUMN$JETSEN4ac95dc0-769d-4156-9f07-df27b1a16616COL");
		System.out.println(obj);
	}

}
