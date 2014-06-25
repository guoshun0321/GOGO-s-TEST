package jetsennet.jsmp.nav.syn.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.entity.Pgm2ProductEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProductEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.entity.PgmBaseEntity;

public class DataSyn4Cache
{

	private static final ConcurrentMap<Class<?>, IDataSynCache<?>> map = new ConcurrentHashMap<Class<?>, IDataSynCache<?>>(20);

	public static <T> IDataSynCache<T> getHandle(T obj)
	{
		if (obj != null)
		{
			return (IDataSynCache<T>) getHandle(obj.getClass());
		}
		else
		{
			return null;
		}
	}

	public static <T> IDataSynCache<T> getHandle(Class<T> cls)
	{
		IDataSynCache<T> retval = null;
		if (map.containsKey(cls))
		{
			retval = (IDataSynCache<T>) map.get(cls);
		}
		else
		{
			retval = (IDataSynCache<T>) genHandle(cls);
			IDataSynCache<?> temp = map.putIfAbsent(cls, retval);
			if (temp != null)
			{
				retval = (IDataSynCache<T>) temp;
			}
		}
		return retval;
	}

	private static IDataSynCache<?> genHandle(Class<?> cls)
	{
		// channel相关
		if (cls == ChannelEntity.class)
		{
			return new DataSynCacheChannel();
		}
		else if (cls == PhysicalChannelEntity.class)
		{
			return new DataSynCachePChannel();
		}
		// 节目单相关
		else if (cls == PlaybillEntity.class)
		{
			return new DataSynCachePlaybill();
		}
		else if (cls == PlaybillItemEntity.class)
		{
			return new DataSynCachePlaybillItem();
		}
		// 产品相关
		else if (cls == ProductEntity.class)
		{
			return new DataSynCacheProduct();
		}
		else if (cls == Pgm2ProductEntity.class)
		{
			return new DataSynCachePgm2Product();
		}
		// 节目相关
		else if (cls == ProgramEntity.class)
		{
			return new DataSynCacheProgram();
		}
		else if (cls == PgmBaseEntity.class)
		{
			return new DataSynCachePgmBase();
		}
		else if (cls == CreatorEntity.class)
		{
			return new DataSynCacheCreator();
		}
		else if (cls == DescauthorizeEntity.class)
		{
			return new DataSynCacheDescAuthorize();
		}
		else if (cls == PictureEntity.class)
		{
			return new DataSynCachePicture();
		}
		else if (cls == FileItemEntity.class)
		{
			return new DataSynCacheFileItem();
		}
		else if (cls == Pgm2PgmEntity.class)
		{
			return new DataSynCachePgm2Pgm();
		}
		// 栏目相关
		else if (cls == ColumnEntity.class)
		{
			return new DataSynCacheColumn();
		}
		// 关联栏目
		//      else if (cls == Column2RelateruleEntity.class)
		//        {
		//            return new UpdateMediaInfoCacheColumn2Relaterule();
		//        }
		//        else if (cls == RelateBlackEntity.class)
		//        {
		//            return new UpdateMediaInfoCacheRelateBlack();
		//        }
		//        else if (cls == RelateColumnEntity.class)
		//        {
		//            return new UpdateMediaInfoCacheRelateColumn();
		//        }
		return null;
	}

}
