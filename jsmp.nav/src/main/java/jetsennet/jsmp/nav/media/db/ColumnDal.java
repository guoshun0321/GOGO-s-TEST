package jetsennet.jsmp.nav.media.db;

import jetsennet.orm.session.Session;

public class ColumnDal extends AbsDal
{

	public int deletePicByColAssetId(String assetId)
	{
		String sql = "DELETE FROM NS_PICTURE WHERE OBJ_ASSETID = '" + assetId + "'";
		Session session = dal.getSession();
		return session.delete(sql);
	}

}
