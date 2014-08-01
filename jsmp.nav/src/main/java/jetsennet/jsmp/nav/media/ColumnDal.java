package jetsennet.jsmp.nav.media;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jsmp.nav.entity.ColumnEntity;

/**
 * 从数据库同步栏目
 * 
 * @author 郭祥
 */
public class ColumnDal extends AbsMediaDal
{

	private List<ColumnEntity> columns;

	public ColumnDal()
	{
		this.columns = new ArrayList<>();
	}

	public void getColumnInfo()
	{
		
	}

}
