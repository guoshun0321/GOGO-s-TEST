package jetsennet.jsmp.nav.media.db;

import java.util.List;

import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
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
			List<Object> objs = dal.getProgram(pgmId);
			if (objs != null && objs.size() > 0 && objs.get(0) != null && objs.get(0) instanceof ProgramEntity)
			{
				ProgramCache.delete((ProgramEntity) objs.get(0));
				ProgramCache.insert(objs);
			}
		}

		List<Pgm2PgmEntity> pgm2pgms = AbsDal.dal.queryAllBusinessObjs(Pgm2PgmEntity.class);
		for (Pgm2PgmEntity pgm2pgm : pgm2pgms)
		{
			ProgramCache.insertPgm2Pgm(pgm2pgm);
		}
	}

}
