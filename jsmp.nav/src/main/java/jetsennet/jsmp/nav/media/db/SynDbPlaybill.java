package jetsennet.jsmp.nav.media.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.media.cache.PlaybillCache;

public class SynDbPlaybill implements ISynDb
{

	@Override
	public void syn() throws Exception
	{
		PlayBillDal pbdal = new PlayBillDal();
		List<Integer> pbIds = pbdal.getRecentPlayBillId();
		Map<String, String> pbMap = new LinkedHashMap<>();
		Map<String, List<String>> pbItemMap = new LinkedHashMap<>();
		if (pbIds != null && !pbIds.isEmpty())
		{
			for (Integer pbId : pbIds)
			{
				PlaybillEntity pb = pbdal.get(pbId);
				if (pb != null)
				{
					PlaybillCache.insertPlaybill(pb);
					String key = PlaybillCache.channelPlaybill(pb.getChlAssetId(), pb.getPlayDate().getTime());
					pbMap.put(key, pb.getAssetId());

					List<String> pbItemIds = new ArrayList<>();
					List<PlaybillItemEntity> pbItems = pbdal.getItems(pbId);
					if (pbItems != null)
					{
						for (PlaybillItemEntity pbItem : pbItems)
						{
							PlaybillCache.insertPlaybillItem(pbItem);
							pbItemIds.add(pbItem.getAssetId());
						}
					}
					pbItemMap.put(PlaybillCache.playbillItemList(pb.getAssetId()), pbItemIds);
				}
			}
		}
		PlaybillCache.insertChlPb(pbMap);
	}

}
