package jetsennet.jsmp.nav.media.db;

import jetsennet.jsmp.nav.util.UncheckedNavException;

/**
 * 从数据库同步所有数据
 * 
 * @author 郭祥
 */
public class SynFromDb
{

	public void syn()
	{
		try
		{
			new SynDbColumn().syn();
			new SynDbProgram().syn();
			new SynDbChannel().syn();
			new SynDbPlaybill().syn();
		}
		catch (Exception ex)
		{
			throw new UncheckedNavException(ex);
		}
	}

}
