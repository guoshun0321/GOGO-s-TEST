/**********************************************************************
 * 日 期: 2013-06-30
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Room2floorEntity.java
 * 历 史: 2013-06-30 Create
 *********************************************************************/
package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 
 */
@Table(name="NMP_ROOM2FLOOR")
public class Room2floorEntity
{
    /**
     * ROOM_ID
     */
    @Column(name="ROOM_ID")   
    private int roomId;
    
    /**
     * FLOOR_ID
     */
    @Column(name="FLOOR_ID")   
    private int floorId;
    

    public int getRoomId()
    {
        return roomId;
    }
				 
    public void setRoomId(int roomId)
    {
        this.roomId = roomId;
    }
    public int getFloorId()
    {
        return floorId;
    }
				 
    public void setFloorId(int floorId)
    {
        this.floorId = floorId;
    }
}


                                        