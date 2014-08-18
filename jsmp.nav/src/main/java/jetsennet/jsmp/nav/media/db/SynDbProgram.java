package jetsennet.jsmp.nav.media.db;

import java.util.List;

import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.media.cache.ProgramCache;

public class SynDbProgram implements ISynDb
{

	@Override
	public void syn() throws Exception
	{
		ProgramDal dal = new ProgramDal();
		List<Integer> pgmIds = dal.getPgmIds();
		if (pgmIds == null)
		{
			return;
		}
		for (Integer pgmId : pgmIds)
		{
			synProgram(pgmId);
		}
	}

	/**
	 * 从数据库同步pgmId相关的数据到缓存
	 * 
	 * @param pgmId
	 */
	public static void synProgram(int pgmId)
	{
		ProgramDal dal = new ProgramDal();
		List<Object> objs = dal.getProgram(pgmId);
		if (objs != null && objs.size() > 0 && objs.get(0) != null && objs.get(0) instanceof ProgramEntity)
		{
			ProgramCache.delete((ProgramEntity) objs.get(0));
			ProgramCache.insert(objs);
		}
	}

}
