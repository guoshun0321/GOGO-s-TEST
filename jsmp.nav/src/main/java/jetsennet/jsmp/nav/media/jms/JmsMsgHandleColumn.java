package jetsennet.jsmp.nav.media.jms;

import java.util.List;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.media.cache.ColumnCache;
import jetsennet.jsmp.nav.media.db.ColumnDal;
import jetsennet.jsmp.nav.media.db.DataSynDbResult;
import jetsennet.jsmp.nav.media.db.DbHelper;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

@IdentAnnocation("columnTable,pictureTable")
public class JmsMsgHandleColumn extends AbsJmsMsgHandle
{

	@Override
	public void handleModify(DataSynContentEntity content)
	{
		List<Object> objs = content.getObjs();
		System.out.println(objs);
		for (Object obj : objs)
		{
			if (obj instanceof ColumnEntity)
			{
				DataSynDbResult dbResult = DbHelper.insertOrUpdate(obj);
				if (isValid(dbResult))
				{
					ColumnCache.update((ColumnEntity) dbResult.obj);
				}

				// 删除图片信息
				ColumnDal dal = new ColumnDal();
				String assetId = ((ColumnEntity) obj).getAssetId();
				dal.deletePicByColAssetId(assetId);
				ColumnCache.delPicByColAssetId(assetId);
			}
		}

		for (Object obj : objs)
		{
			if (obj instanceof PictureEntity)
			{
				DataSynDbResult dbResult = DbHelper.insertOrUpdate(obj);
				if (isValid(dbResult))
				{
					ColumnCache.insertPic((PictureEntity) dbResult.obj);
				}
			}
			else if (obj instanceof ColumnEntity)
			{
				// ignore
			}
			else
			{
				throw new UncheckedNavException("columnTable,pictureTable不处理数据：" + obj.getClass());
			}
		}

	}

	@Override
	public void handleDelete(DataSynContentEntity content)
	{
		List<Object> objs = content.getObjs();
		for (Object obj : objs)
		{
			if (obj instanceof ColumnEntity)
			{
				DataSynDbResult dbResult = DbHelper.delete(obj);
				if (isValid(dbResult))
				{
					ColumnCache.delete((ColumnEntity) dbResult.obj);
				}
			}
			else if (obj instanceof PictureEntity)
			{
				DataSynDbResult dbResult = DbHelper.delete(obj);
				if (isValid(dbResult))
				{
					ColumnCache.deletePic((PictureEntity) dbResult.obj);
				}
			}
		}
	}

}
