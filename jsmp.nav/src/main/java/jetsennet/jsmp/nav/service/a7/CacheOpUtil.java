package jetsennet.jsmp.nav.service.a7;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class CacheOpUtil
{

	public static final DataCacheOp cache = DataCacheOp.getInstance();

	public static final ResponseEntity getSelectableItem(int pgmId)
	{
		ResponseEntity retval = new ResponseEntity("Selectableltem");
		ProgramEntity program = cache.get(CachedKeyUtil.programKey(pgmId));
		if (program != null)
		{
			ResponseEntityUtil.obj2Resp(program, null, retval);
		}
		return retval;
	}

}
