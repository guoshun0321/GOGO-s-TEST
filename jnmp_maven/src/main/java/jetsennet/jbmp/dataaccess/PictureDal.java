/**********************************************************************
 * 日 期: 2012-09-19
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: PictureDal.java
 * 历 史: 2012-09-19 Create
 *********************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.PictureEntity;

/**
 * Dal
 */
public class PictureDal extends DefaultDal<PictureEntity>
{
    private static final Logger logger = Logger.getLogger(PictureDal.class);

    /**
     * 构造方法
     */
    public PictureDal()
    {
        super(PictureEntity.class);
    }
    
    /**
     * @return 查询某个对象的子对象默认图片和关联对象ID
     */
    public ArrayList<PictureEntity> getObjChildDefalutPic(int parentId)
    {
        String sql = String.format("SELECT P.PICTURE_PATH,T.OBJ_ID AS PICTURE_ID,T.FIELD_1 AS PICTURE_NAME,P.FIELD_1 AS FIELD_2 FROM BMP_OBJECT T, BMP_PICTURE P,BMP_ATTRIBCLASS C WHERE T.PARENT_ID = %s AND T.CLASS_ID = C.CLASS_ID AND P.PICTURE_NAME=C.FIELD_1",parentId);
        try
        {
            return (ArrayList<PictureEntity>) getLst(sql);
        }
        catch (Exception e)
        {
            logger.error(e);
            return null;
        }
    }
    
    /**
     * @return 查询某个对象的默认图片
     */
    public PictureEntity getObjDefalutPic(int objId)
    {
        String sql = String.format("SELECT P.* FROM BMP_OBJECT T, BMP_PICTURE P,BMP_ATTRIBCLASS C WHERE T.OBJ_ID = %s AND T.CLASS_ID = C.CLASS_ID AND P.PICTURE_NAME=C.FIELD_1",objId);
        try
        {
            return (PictureEntity) get(sql);
        }
        catch (Exception e)
        {
            logger.error(e);
            return null;
        }
    }
}
