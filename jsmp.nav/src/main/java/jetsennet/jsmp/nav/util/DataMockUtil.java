package jetsennet.jsmp.nav.util;

import java.util.List;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.service.a7.A7Util;
import jetsennet.jsmp.nav.service.a7.NavBusiness;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataMockUtil
{

	public static final ResponseEntity gen()
	{
		DataCacheOp op = DataCacheOp.getInstance();
		List<Integer> tops = op.get(CachedKeyUtil.topColumn());
		ColumnEntity column = op.get(CachedKeyUtil.columnKey(tops.get(0)));

		List<Integer> subs = op.getListInt(CachedKeyUtil.subColumn(column.getColumnId(), column.getRegionCode()));
		ColumnEntity column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));

		subs = op.getListInt(CachedKeyUtil.subColumn(column1.getColumnId(), column1.getRegionCode()));
		column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));

		subs = op.getListInt(CachedKeyUtil.columnPgm(column1.getColumnId()));
		ProgramEntity pgm = op.get(CachedKeyUtil.programKey(subs.get(0)));

		return A7Util.getContentItem(pgm, column1);
	}
	
	public static void main(String[] args)
	{
		System.out.println(DataMockUtil.gen().toXml(null).toString());
	}

}
