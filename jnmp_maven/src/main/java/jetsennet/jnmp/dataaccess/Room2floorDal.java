/**********************************************************************
 * 日 期: 2013-06-30
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Room2floorDal.java
 * 历 史: 2013-06-30 Create
 *********************************************************************/
package jetsennet.jnmp.dataaccess;

import org.apache.log4j.Logger;
import jetsennet.jnmp.entity.Room2floorEntity;
import jetsennet.jbmp.dataaccess.DefaultDal;

/**
 *  Dal
 */
public class Room2floorDal extends DefaultDal<Room2floorEntity>
{
    private static final Logger logger = Logger.getLogger(Room2floorDal.class);
    
    public Room2floorDal()
    {
        super(Room2floorEntity.class);
    }
}
