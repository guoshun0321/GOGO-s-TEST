package jetsennet.jsmp.nav.syn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.syn.cache.DataSyn4Cache;
import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.jsmp.nav.util.ObjectUtil;
import jetsennet.jsmp.nav.util.TestObjGenUtil;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import junit.framework.TestCase;

public class DataCachedTest extends TestCase
{

	private static SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;
	private static Session session = factory.openSession();
	private static DataCacheOp cache = DataCacheOp.getInstance();

	protected void setUp() throws Exception
	{
		cache.deleteAll();
	}

	protected void tearDown() throws Exception
	{
//		cache.deleteAll();
	}

	public void testChannel() throws Exception
	{
		Map<String, Object> special = new HashMap<String, Object>();
		special.put("ASSET_ID", "UUID");
		List<ChannelEntity> channels = TestObjGenUtil.genObj(ChannelEntity.class, 10, special);
		List<PhysicalChannelEntity> pchannels = new ArrayList<PhysicalChannelEntity>(100);
		for (ChannelEntity channel : channels)
		{
			DataSyn4Cache.getHandle(ChannelEntity.class).insert(channel);
			special = new HashMap<String, Object>();
			special.put("CHL_ID", channel.getChlId());
			List<PhysicalChannelEntity> temp = TestObjGenUtil.genObj(PhysicalChannelEntity.class, 10, special);
			pchannels.addAll(temp);
			for (PhysicalChannelEntity pch : temp)
			{
				DataSyn4Cache.getHandle(PhysicalChannelEntity.class).insert(pch);
			}
		}

		for (ChannelEntity channel : channels)
		{
			String key = CachedKeyUtil.channelKey(channel.getChlId());
			assertTrue(ObjectUtil.compare(ChannelEntity.class, channel, cache.get(key)));

			key = CachedKeyUtil.channel2pchannel(channel.getChlId());
			HashSet<Integer> relSet = (HashSet<Integer>) cache.get(CachedKeyUtil.channel2pchannel(channel.getChlId()));
			for (Integer rel : relSet)
			{
				for (PhysicalChannelEntity pchannel : pchannels)
				{
					if (pchannel.getPhychlId() == rel)
					{
						assertEquals(channel.getChlId(), pchannel.getChlId());
					}
				}
			}
		}

		for (PhysicalChannelEntity pch : pchannels)
		{
			String key = CachedKeyUtil.physicalChannelKey(pch.getPhychlId());
			assertTrue(ObjectUtil.compare(PhysicalChannelEntity.class, pch, cache.get(key)));
		}

		for (ChannelEntity channel : channels)
		{
			DataSyn4Cache.getHandle(ChannelEntity.class).delete(channel);
			String key = CachedKeyUtil.channelKey(channel.getChlId());
			ChannelEntity temp = cache.get(key, true);
			assertNull(temp);
		}

		for (PhysicalChannelEntity pch : pchannels)
		{
			DataSyn4Cache.getHandle(PhysicalChannelEntity.class).delete(pch);
			String key = CachedKeyUtil.physicalChannelKey(pch.getPhychlId());
			PhysicalChannelEntity temp = cache.get(key, true);
			assertNull(temp);
		}

	}

	public void testColumn() throws Exception
	{
		List<ColumnEntity> chs = new ArrayList<ColumnEntity>(1110);
		List<ColumnEntity> level1Chs = new ArrayList<ColumnEntity>(10);
		Map<String, Object> special = new HashMap<String, Object>();
		special.put("PARENT_ID", 0);
		special.put("ASSET_ID", "UUID");
		special.put("LANGUAGE_CODE", "zh_CN");
		special.put("REGION_CODE", "jetsen");
		level1Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 10, special));

		List<ColumnEntity> level2Chs = new ArrayList<ColumnEntity>(10 * 10);
		for (ColumnEntity level1Ch : level1Chs)
		{
			special = new HashMap<String, Object>();
			special.put("PARENT_ID", level1Ch.getColumnId());
			special.put("PARENT_ASSETID", level1Ch.getAssetId());
			special.put("ASSET_ID", "UUID");
			special.put("LANGUAGE_CODE", "zh_CN");
			special.put("REGION_CODE", "jetsen");
			level2Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 10, special));
		}

		List<ColumnEntity> level3Chs = new ArrayList<ColumnEntity>(10 * 10 * 10);
		for (ColumnEntity level2Ch : level2Chs)
		{
			special = new HashMap<String, Object>();
			special.put("PARENT_ID", level2Ch.getColumnId());
			special.put("PARENT_ASSETID", level2Ch.getAssetId());
			special.put("ASSET_ID", "UUID");
			special.put("LANGUAGE_CODE", "zh_CN");
			special.put("REGION_CODE", "jetsen");
			level3Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 10, special));
		}
		chs.addAll(level1Chs);
		chs.addAll(level2Chs);
		chs.addAll(level3Chs);

		for (ColumnEntity ch : chs)
		{
			DataSyn4Cache.getHandle(ColumnEntity.class).insert(ch);
			String key = CachedKeyUtil.columnKey(ch.getColumnId());
			ColumnEntity temp = cache.get(key);
			assertTrue(ObjectUtil.compare(ColumnEntity.class, ch, temp));

			int id = cache.get(CachedKeyUtil.columnAssetKey(ch.getAssetId()));
			assertEquals(ch.getColumnId(), id);
		}

		HashSet<Integer> tops = cache.get(CachedKeyUtil.topColumn());
		assertEquals(10, tops.size());
		for (Integer top : tops)
		{
			String key = CachedKeyUtil.columnKey(top);
			ColumnEntity temp = cache.get(key);
			assertEquals(0, temp.getParentId());
		}

		for (ColumnEntity ch : level1Chs)
		{
			String key = CachedKeyUtil.subColumn(ch.getColumnId(), ch.getRegionCode());
			LinkedHashSet<Integer> subs = cache.get(key, true);
			assertEquals(10, subs.size());
			for (Integer sub : subs)
			{
				key = CachedKeyUtil.columnKey(sub);
				ColumnEntity temp = cache.get(key);
				assertEquals(ch.getColumnId(), temp.getParentId());
			}
		}
	}

}
