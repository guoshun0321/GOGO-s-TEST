package jetsennet.jsmp.nav.util;

import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;

public class DataMockUtil
{

	public static final ResponseEntity gen()
	{
		//		DataCacheOp op = DataCacheOp.getInstance();
		//		List<String> tops = op.get(ColumnCache.topColumn());
		//		ColumnEntity column = op.get(ColumnCache.columnKey(tops.get(0)));
		//
		//		List<Integer> subs = op.getListInt(CachedKeyUtil.subColumn(column.getColumnId(), column.getRegionCode()));
		//		ColumnEntity column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));
		//
		//		subs = op.getListInt(CachedKeyUtil.subColumn(column1.getColumnId(), column1.getRegionCode()));
		//		column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));
		//
		//		subs = op.getListInt(CachedKeyUtil.columnPgm(column1.getColumnId()));
		//		ProgramEntity pgm = op.get(CachedKeyUtil.programKey(subs.get(0)));
		//
		//		return A7Util.getContentItem(pgm, column1);
		return null;
	}

	public static void main(String[] args)
	{
		System.out.println(DataMockUtil.gen().toXml(null).toString());
	}

}
