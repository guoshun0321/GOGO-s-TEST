package jetsennet.jsmp.nav.media.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayBillInfoMap
{

	private List<Integer> pbIds;

	private Map<Integer, PlayBillInfoEntry> infos;

	public PlayBillInfoMap()
	{
		this.pbIds = new ArrayList<>();
		this.infos = new HashMap<Integer, PlayBillInfoMap.PlayBillInfoEntry>();
	}

	public void add(int pbId, String chlAssetId, Date date)
	{
		this.pbIds.add(pbId);
		this.infos.put(pbId, new PlayBillInfoEntry(pbId, chlAssetId, date));
	}

	public List<Integer> getPbIds()
	{
		return this.pbIds;
	}

	public PlayBillInfoEntry getInfo(int pbId)
	{
		return this.infos.get(pbId);
	}

	public static class PlayBillInfoEntry
	{
		public final int pbId;

		public final String chlAssetId;

		public final Date date;

		public final long startTime;

		public PlayBillInfoEntry(int pbId, String chlAssetId, Date date)
		{
			this.pbId = pbId;
			this.chlAssetId = chlAssetId;
			this.date = date;
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			this.startTime = cal.getTimeInMillis();
		}
	}

}
