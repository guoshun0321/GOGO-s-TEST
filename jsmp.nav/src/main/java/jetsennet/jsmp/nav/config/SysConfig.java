package jetsennet.jsmp.nav.config;

public class SysConfig
{

	/**
	 * 保留PRE_DATE天的节目单
	 */
	public static final int PRE_DATE = 14;

	public static final String selectionStartKey(String uuid)
	{
		return "SELECTION_START$" + uuid;
	}

}
