package jetsennet.jsmp.nav.media.db;

/**
 * 数据库操作结果
 * 
 * @author jetsen
 */
public class DataSynDbResult
{

	/**
	 * 操作类型
	 */
	public final int type;
	/**
	 * 操作数据
	 */
	public final Object obj;
	/**
	 * 操作结果
	 */
	public final int num;

	public static final int TYPE_INSERT = 0;
	public static final int TYPE_UPDATE = 1;
	public static final int TYPE_DELETE = 2;

	public DataSynDbResult(int type, Object obj, int num)
	{
		this.type = type;
		this.obj = obj;
		this.num = num;
	}

}
