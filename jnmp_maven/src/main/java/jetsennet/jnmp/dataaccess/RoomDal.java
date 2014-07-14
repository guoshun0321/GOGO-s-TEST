/**********************************************************************
 * 日 期: 2013-06-30
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: RoomDal.java
 * 历 史: 2013-06-30 Create
 *********************************************************************/
package jetsennet.jnmp.dataaccess;

import org.apache.log4j.Logger;
import jetsennet.jnmp.entity.RoomEntity;
import jetsennet.jbmp.dataaccess.DefaultDal;

/**
 *  Dal
 */
public class RoomDal extends DefaultDal<RoomEntity>
{
    private static final Logger logger = Logger.getLogger(RoomDal.class);
    
    public RoomDal()
    {
        super(RoomEntity.class);
    }
}
