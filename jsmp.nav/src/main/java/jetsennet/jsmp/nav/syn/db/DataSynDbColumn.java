package jetsennet.jsmp.nav.syn.db;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jsmp.nav.entity.ColumnEntity;

/**
 * 从数据库同步栏目
 * 
 * @author 郭祥
 */
public class DataSynDbColumn extends AbsDataSynDb
{

	private List<ColumnEntity> columns;

	public DataSynDbColumn()
	{
		this.columns = new ArrayList<>();
	}

	public void getColumnInfo()
	{
		
	}

}
